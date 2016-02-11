package it.sinergis.sunshine.service;

import it.sinergis.sunshine.util.ConfigUtils;
import it.sinergis.sunshine.util.ConfigValues;
import it.sinergis.sunshine.util.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.log4j.Logger;

/**
 * Classe che si occupa di gestire la scrittura degli interval block a partire da file csv. Implementata
 * come un Singleton, vista la necessità di consultare i file csv di una directory.
 * 
 * @author Rossana
 * 
 */
public class CreazioneIntervalBlocks {

	/** Logger. */
	private static Logger logger = Logger
			.getLogger(CreazioneIntervalBlocks.class.getName());

	private static List<String> fileList;

	/**
	 * Variabile di istanza.
	 */
	private static CreazioneIntervalBlocks creazioneIntervalBlocks;

	public static final Object lock = new Object();

	private static long lastTime;
	private static long clockSequence = 0;

	/**
	 * Costruttore privato, Singleton.
	 */
	private CreazioneIntervalBlocks() {

	}

	/**
	 * Metodo di accesso alla classe.
	 * 
	 * @return
	 */
	public static CreazioneIntervalBlocks getInstance() {
		if (creazioneIntervalBlocks == null) {
			creazioneIntervalBlocks = new CreazioneIntervalBlocks();
		}

		return creazioneIntervalBlocks;
	}

	public synchronized String insertIntervalBlock() {
		BufferedReader in   = null;
		HttpURLConnection  connServlet = null;
		OutputStreamWriter writer = null;
		String response = "<CreateIntervalBlockResponse>";
		try {
			

			long countUsagePoint = Long.valueOf(ConfigValues.COUNT_USAGE_POINT);
			
		 	for (int index = 1; index <= countUsagePoint; index++) {
		 		
		 		File directoryCsv = new File(ConfigUtils
						.getXemtecConfigValue(Constants.URL_FILE_CSV_PREFIX + index));
				searchFile(directoryCsv, "csv");
				BufferedReader br = null;
				for (int i = 0; i < fileList.size(); i++) {
					try {
						
						String csvFile = fileList.get(i);
						response += "<csv><path>" +  csvFile + "</path>";
						List<String[]> rows = new ArrayList<String[]>();
						UUID uuid = this.generateId();
						int numfile = i+1;
						logger.debug("FILE CSV ANALIZZATO NUMERO "+ numfile);
						logger.debug("UUID:"+ uuid.toString());
					
						String line = "";
						String cvsSplitBy = ";";
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						df.setTimeZone(TimeZone.getTimeZone("GMT"));
						SimpleDateFormat sdfSource = new SimpleDateFormat(
			                      "yyyy-MM-dd'T'HH:mm:ss");	
						br = new BufferedReader(new FileReader(csvFile));
						logger.debug("csvFile:"+csvFile);
						while ((line = br.readLine()) != null) {
							// use comma as separator
							String[] row = line.split(cvsSplitBy);
							logger.debug("row csv:"+row[0] + ";" + row[1]);
							rows.add(row);
						}
						if (rows.size()>1) {
							String[] rowFirst = rows.get(0);
							String[] rowSecond = rows.get(1);
							String dateFirstString = rowFirst[0];
							String dateSecondString = rowSecond[0];
							
							Date dateFirst = df.parse(dateFirstString);
							Date dateSecond = df.parse(dateSecondString);
							long diff = dateSecond.getTime() - dateFirst.getTime();
					        long durationIntervalBlock = (diff / 1000) * rows.size();
					        long durationIntervalReading = diff / 1000;
					        
					    	GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
				            gc.setTime(dateFirst);
				           
				            gc.add(java.util.Calendar.SECOND,-((int)durationIntervalReading));
					      				      
					        String entry = "<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:espi=\"http://naesb.org/espi\">"
							+"<id>urn:uuid:" + uuid.toString() +"</id>"
							+"<link rel=\"self\" href=\""+ ConfigValues.URL_DATA_CUSTODIAN + "RetailCustomer/"+ ConfigUtils
							.getXemtecConfigValue(Constants.ID_RETAIL_CUSTOMER_PREFIX + index) +"/UsagePoint/"+ ConfigUtils
							.getXemtecConfigValue(Constants.ID_USAGE_POINT_PREFIX + index)
									+"/MeterReading/"+ ConfigUtils
									.getXemtecConfigValue(Constants.ID_METER_READING_PREFIX + index) +"/IntervalBlock/1\"/>"
							+"<link rel=\"up\" href=\""+ ConfigValues.URL_DATA_CUSTODIAN + "RetailCustomer/"+ ConfigUtils
							.getXemtecConfigValue(Constants.ID_RETAIL_CUSTOMER_PREFIX + index) +"/UsagePoint/"+ ConfigUtils
							.getXemtecConfigValue(Constants.ID_USAGE_POINT_PREFIX + index)
									+"/MeterReading/"+ ConfigUtils
									.getXemtecConfigValue(Constants.ID_METER_READING_PREFIX + index) +"/IntervalBlock\"/>"
							+"<title/>"
							+"<content>"
								+"<IntervalBlock xmlns=\"http://naesb.org/espi\">"
									+"<interval>"
										+"<duration>"+durationIntervalBlock+"</duration>"
										+"<start>"+gc.getTime().getTime()/1000+"</start>"								
									+"</interval>";
					       	for (int j = 0; j < rows.size(); j++) {
					    		String[] row = rows.get(j);
					    		Date dateRow = df.parse(row[0]);
					    		gc = (GregorianCalendar) GregorianCalendar.getInstance();
					            gc.setTime(dateRow);
					           
					            gc.add(java.util.Calendar.SECOND,-((int)durationIntervalReading));
					    		Double value = Double.valueOf(row[1]);
					    		entry += "<IntervalReading>"
										     +"<timePeriod>"
										        +"<duration>"+durationIntervalReading+"</duration>"
											    +"<start>"+gc.getTime().getTime()/1000+"</start>"
											 +"</timePeriod>";
					    		value = value/((double) 0.001);
					    						    		 
					    		DecimalFormat df2 = new DecimalFormat( "#,###,##0" );
					    		//value = BigDecimal.valueOf(value).doubleValue();
					    		String dd2dec = df2.format(value);
					    		dd2dec = dd2dec.replaceAll("\\.", "");
					    				    		
					    		entry += "<value>"+dd2dec+"</value>"
									      +"</IntervalReading>";
					    	}
					    	
					       	String[] row = rows.get(rows.size()-1);
				    		Date dateRow = df.parse(row[0]);
					    	String published = sdfSource.format(dateRow);	     	
					    	entry += "</IntervalBlock>"
							        +"</content>"
							        +"<published>"+published+"</published>"	
							        +"<updated>"+published+"</updated>"	
						           +"</entry>";
					    	
					    	logger.debug("Entry xml:" + entry);
					    	
					    	String urlCreateIntervalBlock = ConfigValues.URL_DATA_CUSTODIAN + "RetailCustomer/"+ ConfigUtils
									.getXemtecConfigValue(Constants.ID_RETAIL_CUSTOMER_PREFIX + index) +"/UsagePoint/"+ ConfigUtils
									.getXemtecConfigValue(Constants.ID_USAGE_POINT_PREFIX + index)
									+"/MeterReading/"+ ConfigUtils
									.getXemtecConfigValue(Constants.ID_METER_READING_PREFIX + index) +"/IntervalBlock";
					    	
					    	logger.debug("urlCreateIntervalBlock:" + urlCreateIntervalBlock);
					    	logger.debug("Inizio creazione interval block su datacustodian");
					    	
					    	URL url = new URL(urlCreateIntervalBlock);			    		

				    		connServlet = (HttpURLConnection)url.openConnection();
				            connServlet.setRequestMethod("POST");    
				            connServlet.setDoOutput(true);
				            connServlet.setDoInput(true); 		        
				            connServlet.setFollowRedirects(true);			          
				            connServlet.setUseCaches(false);
				            // content type
				            connServlet.setRequestProperty("Content-Type", "application/atom+xml");
				           		            
				            
				            writer = new OutputStreamWriter(connServlet.getOutputStream());

				            writer.write(entry);
				            writer.flush();
				            writer.close();	
				            
				            in   = new BufferedReader(new InputStreamReader(connServlet.getInputStream()));
				            String         output;
				            line="";

				            for (output = ""; (line = in.readLine()) != null; output = output + line.toString().trim())
				            {
				                ;
				            }
				            
				            output = output.replaceAll("\t", "");
	    		            output = output.replaceAll("\n", "");
	    		            output = output.replaceAll("\r", "");
	    		            output = output.trim();
				           
				        	logger.debug("RISPOSTA DATACUSTODIAN:" + output);		            
				        	logger.debug("Fine creazione interval block su datacustodian");
				        	response += output + "</csv>";
				            in.close();
				    	    connServlet.disconnect();			    	   	            
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						logger.error("Eccezione:", e);
						response += "<error>" +e.getLocalizedMessage() + "</error></csv>";
					} catch (IOException e) {
						e.printStackTrace();
						logger.error("Eccezione:", e);
						response += "<error>" +e.getLocalizedMessage() + "</error></csv>";					
					} catch (ParseException e) {
						e.printStackTrace();
						logger.error("Eccezione:", e);
						response += "<error>" +e.getLocalizedMessage() + "</error></csv>";
					}  catch (Exception e) {
						e.printStackTrace();
						logger.error("Eccezione:", e);
						response += "<error>" +e.getLocalizedMessage() + "</error></csv>";
					} finally {
						if (in!=null) {
							try {	
								in.close();
							} catch (IOException e) {
								e.printStackTrace();
								logger.error("Eccezione:", e);
							}
						}
						if (connServlet!=null) {					
								connServlet.disconnect();						
						}
						if (writer!=null) {
							try {	
								writer.close();
							} catch (IOException e) {
								e.printStackTrace();
								logger.error("Eccezione:", e);
							}
						}					
						if (br != null) {
							try {
								br.close();
							} catch (IOException e) {
								e.printStackTrace();
								logger.error("Eccezione:", e);
							}
						}
					}
				}			
		 	}

		}  catch (Exception e) {
			e.printStackTrace();
			logger.error("Eccezione:", e);
			response += "<error>" +e.getLocalizedMessage() + "</error>";
		}
		response += "</CreateIntervalBlockResponse>";
		
		logger.debug(response);
		
		return  response;

	}
	
	
	public synchronized String insertIntervalBlockFromXml(String entry) {
		BufferedReader in   = null;
		HttpURLConnection  connServlet = null;
		OutputStreamWriter writer = null;
		String response = "<CreateIntervalBlockResponse>";
		try {	
				    	
			    if (entry==null || entry.equals("")) {
			    	throw new Exception("Parametro xml di richiesta mancante");
			    }
		    	logger.debug("Entry xml:" + entry);
		    	String urlCreateIntervalBlock = ConfigValues.URL_DATA_CUSTODIAN + "RetailCustomer/"+ ConfigUtils
						.getXemtecConfigValue(Constants.ID_RETAIL_CUSTOMER_PREFIX + "1") +"/UsagePoint/"+ ConfigUtils
						.getXemtecConfigValue(Constants.ID_USAGE_POINT_PREFIX + "1")
						+"/MeterReading/"+ ConfigUtils
						.getXemtecConfigValue(Constants.ID_METER_READING_PREFIX + "1") +"/IntervalBlock";
		    	
		    	logger.debug("urlCreateIntervalBlock:" + urlCreateIntervalBlock);
		    	logger.debug("Inizio creazione interval block su datacustodian");
		    	
		    	URL url = new URL(urlCreateIntervalBlock);			    		

	    		connServlet = (HttpURLConnection)url.openConnection();
	            connServlet.setRequestMethod("POST");    
	            connServlet.setDoOutput(true);
	            connServlet.setDoInput(true); 		        
	            connServlet.setFollowRedirects(true);			          
	            connServlet.setUseCaches(false);
	            // content type
	            connServlet.setRequestProperty("Content-Type", "application/atom+xml");
	           		            
	            
	            writer = new OutputStreamWriter(connServlet.getOutputStream());

	            writer.write(entry);
	            writer.flush();
	            writer.close();	
	            
	            in   = new BufferedReader(new InputStreamReader(connServlet.getInputStream()));
	            String         output;
	            String line="";

	            for (output = ""; (line = in.readLine()) != null; output = output + line.toString().trim())
	            {
	                ;
	            }
	           	    
	            output = output.replaceAll("\t", "");
	            output = output.replaceAll("\n", "");
	            output = output.replaceAll("\r", "");
	            output = output.trim();
	        	logger.debug("RISPOSTA DATACUSTODIAN:" + output);	
	        	response += output;
	        	logger.debug("Fine creazione interval block su datacustodian");
	            
	            in.close();
	    	    connServlet.disconnect(); 
			    	   	            
			
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Eccezione:", e);
				response += "<error>" +e.getLocalizedMessage() + "</error>";
			}  catch (Exception e) {
				e.printStackTrace();
				logger.error("Eccezione:", e);
				response += "<error>" +e.getLocalizedMessage() + "</error>";
			}  finally {
				if (in!=null) {
					try {	
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
						logger.error("Eccezione:", e);
					}
				}
				if (connServlet!=null) {					
						connServlet.disconnect();						
				}
				if (writer!=null) {
					try {	
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
						logger.error("Eccezione:", e);
					}
				}			
			}
		
		     response += "</CreateIntervalBlockResponse>";
		 	 logger.debug(response);
		     return  response;		    
	}

	public static void searchFile(File pathFile, String estensione) {

		File listFile[] = pathFile.listFiles();
		fileList = new ArrayList<String>();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				if (listFile[i].isDirectory()) {
					searchFile(listFile[i], estensione);
				} else {

					if (estensione != null) {

						if (listFile[i].getName().endsWith(estensione)) {

							fileList.add(listFile[i].getPath());
							System.out.println("File di Import selezionato: [ "
									+ listFile[i].getPath() + " ]");
						}

					} else {

						fileList.add(listFile[i].getPath());
						System.out.println("File di Import selezionato: [ "
								+ listFile[i].getPath() + " ]");
					}

				}
			}
		}
	}

	/**
	 * Will generate unique time based UUID where the next UUID is always
	 * greater then the previous.
	 */
	public final static UUID generateId() {
		return generateIdFromTimestamp(System.currentTimeMillis());
	}

	public final static UUID generateIdFromTimestamp(long currentTimeMillis) {
		long time;

		synchronized (lock) {
			if (currentTimeMillis > lastTime) {
				lastTime = currentTimeMillis;
				clockSequence = 0;
			} else {
				++clockSequence;
			}
		}

		time = currentTimeMillis;

		// low Time
		time = currentTimeMillis << 32;

		// mid Time
		time |= ((currentTimeMillis & 0xFFFF00000000L) >> 16);

		// hi Time
		time |= 0x1000 | ((currentTimeMillis >> 48) & 0x0FFF);

		long clockSequenceHi = clockSequence;

		clockSequenceHi <<= 48;

		long lsb = clockSequenceHi;

		return new UUID(time, lsb);
	}
}
