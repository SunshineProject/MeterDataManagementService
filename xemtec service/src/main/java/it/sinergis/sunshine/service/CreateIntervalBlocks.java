package it.sinergis.sunshine.service;

import it.sinergis.sunshine.jpadao.JpaXemtecValuesDAO;
import it.sinergis.sunshine.model.XemtecValues;
import it.sinergis.sunshine.util.ConfigUtils;
import it.sinergis.sunshine.util.ConfigValues;
import it.sinergis.sunshine.util.Constants;
import it.sinergis.sunshine.util.CreateIntervalBlocksException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.sun.xml.bind.StringInputStream;

/**
 * Classe che si occupa di gestire la scrittura degli interval block a partire da file csv. Implementata come un
 * Singleton, vista la necessità di consultare i file csv di una directory.
 * 
 * @author Rossana
 */
public class CreateIntervalBlocks {
	
	/** Logger. */
	private static Logger logger;
	
	/**
	 * Variabile di istanza.
	 */
	private static CreateIntervalBlocks createIntervalBlocks;
	
	public static final Object lock = new Object();
	
	private static long lastTime;
	private static long clockSequence = 0;
	
	/**
	 * Costruttore privato, Singleton.
	 */
	private CreateIntervalBlocks() {
		logger = Logger.getLogger(this.getClass());
	}
	
	/**
	 * Metodo di accesso alla classe.
	 * 
	 * @return
	 */
	public static CreateIntervalBlocks getInstance() {
		if (createIntervalBlocks == null) {
			createIntervalBlocks = new CreateIntervalBlocks();
		}
		
		return createIntervalBlocks;
	}
	
	public synchronized String insertIntervalBlock() {
		//BufferedReader in   = null;
		HttpURLConnection connServlet = null;
		OutputStreamWriter writer = null;
		String response = "<CreateIntervalBlockResponse>";
		try {
			
			SimpleDateFormat dfDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			
			long countUsagePoint = Long.valueOf(ConfigValues.COUNT_USAGE_POINT);
			
			for (int i = 1; i <= countUsagePoint; i++) {
				try {
					response += "<intervalBlockEntry><idUsagePoint>"
							+ ConfigUtils.getXemtecConfigValue(Constants.ID_USAGE_POINT_PREFIX + i) + "</idUsagePoint>";
					//leggiamo l'ultimo interval block cioè l'ultimo interval block con data discendente 
					String ib = getLastIntervalBlock(i);
					InputStream ibIs = new StringInputStream(ib);
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					dbf.setNamespaceAware(true);
					
					Date lastDate = null;
					
					DocumentBuilder db = dbf.newDocumentBuilder();
					
					Document docIntervalBlock = db.parse(ibIs);
					
					if (docIntervalBlock != null) {
						
						if (docIntervalBlock.getElementsByTagNameNS(Constants.URI_ATOM, "published") != null
								&& docIntervalBlock.getElementsByTagNameNS(Constants.URI_ATOM, "published").item(0) != null) {
							Node pub = docIntervalBlock.getElementsByTagNameNS(Constants.URI_ATOM, "published").item(0);
							if (pub.getFirstChild() != null && pub.getFirstChild().getNodeValue() != null) {
								lastDate = dfDateTime.parse(pub.getFirstChild().getNodeValue());
							}
						}
					}
					
					Collection<XemtecValues> xemtecValues = null;
					//se abbiamo trovato l'ultimo interval block allora leggiamo dal db di Xemtec a partire dalla data
					//dell'ultimo blocco temporale
					JpaXemtecValuesDAO jpaXemtecValuesDAO = new JpaXemtecValuesDAO();
					if (lastDate != null) {
						xemtecValues = jpaXemtecValuesDAO.findByDate(
								Long.valueOf(ConfigUtils.getXemtecConfigValue(Constants.ID_DEVICE_PREFIX + i)),
								lastDate);
					}
					else {
						xemtecValues = jpaXemtecValuesDAO.findById(Long.valueOf(ConfigUtils
								.getXemtecConfigValue(Constants.ID_DEVICE_PREFIX + i)));
					}
					
					if (xemtecValues != null && xemtecValues.size() > 0) {
						ArrayList<XemtecValues> intervalblockValues = new ArrayList<it.sinergis.sunshine.model.XemtecValues>(
								xemtecValues);
						
						Date readDate = intervalblockValues.get(0).getReadDate();
						
						String readDateString = dfDateTime.format(readDate);
						
						UUID uuid = this.generateId();
						
						logger.debug("INSERIMENTO BLOCCO TEMPORALE CON START DATE: " + readDateString);
						logger.debug("UUID:" + uuid.toString());
						
						//se esiste l'ultima data inserita nel db greenbutton allora mi calcolo la durata
						//del primo intervalreading facendo la differenza tra questa ultima data e la prima data trovata da xemtec
						//altrimenti se non esiste lastadate ed esistono almeno due date da xemtec allora  mi calcolo la durata
						//del primo intervalreading facendo la differenza tra le prime due date di xemtec altrimenti 
						//se non esiste un lastdate e da xemtec abbiamo trovato solo una data allora la durata del primo
						//intervalreading sarà di 3600.
						long durationIntervalBlock = 3600;
						long durationIntervalReading = 3600;
						if (lastDate != null) {
							Date dateSecond = intervalblockValues.get(0).getReadDate();
							long diff = dateSecond.getTime() - lastDate.getTime();
							durationIntervalReading = diff / 1000;
							durationIntervalBlock = diff / 1000;
						}
						else if (intervalblockValues.size() > 1) {
							//nel caso della prima data supponiamo che la durata sia uguale
							//alla diffrenza tra la seconda e la prima
							Date dateFirst = intervalblockValues.get(0).getReadDate();
							Date dateSecond = intervalblockValues.get(1).getReadDate();
							long diff = dateSecond.getTime() - dateFirst.getTime();
							durationIntervalReading = diff / 1000;
						}
						
						GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
						gc.setTime(readDate);
						gc.add(java.util.Calendar.SECOND, -((int) durationIntervalReading));
						
						if (intervalblockValues.size() > 1) {
							Date dateLast = intervalblockValues.get(intervalblockValues.size() - 1).getReadDate();
							long diff = dateLast.getTime() - gc.getTime().getTime();
							durationIntervalBlock = diff / 1000;
						}
						
						String entry = "<entry xmlns=\"" + Constants.URI_ATOM + "\" xmlns:espi=\"" + Constants.URI_ESPI
								+ "\">" + "<id>urn:uuid:" + uuid.toString() + "</id>" + "<link rel=\"self\" href=\""
								+ ConfigValues.URL_DATA_CUSTODIAN + "RetailCustomer/"
								+ ConfigUtils.getXemtecConfigValue(Constants.ID_RETAIL_CUSTOMER_PREFIX + i)
								+ "/UsagePoint/"
								+ ConfigUtils.getXemtecConfigValue(Constants.ID_USAGE_POINT_PREFIX + i)
								+ "/MeterReading/"
								+ ConfigUtils.getXemtecConfigValue(Constants.ID_METER_READING_PREFIX + i)
								+ "/IntervalBlock/1\"/>" + "<link rel=\"up\" href=\"" + ConfigValues.URL_DATA_CUSTODIAN
								+ "RetailCustomer/"
								+ ConfigUtils.getXemtecConfigValue(Constants.ID_RETAIL_CUSTOMER_PREFIX + i)
								+ "/UsagePoint/"
								+ ConfigUtils.getXemtecConfigValue(Constants.ID_USAGE_POINT_PREFIX + i)
								+ "/MeterReading/"
								+ ConfigUtils.getXemtecConfigValue(Constants.ID_METER_READING_PREFIX + i)
								+ "/IntervalBlock\"/>" + "<title/>" + "<content>" + "<IntervalBlock xmlns=\""
								+ Constants.URI_ESPI + "\">" + "<interval>" + "<duration>" + durationIntervalBlock
								+ "</duration>" + "<start>" + gc.getTime().getTime() / 1000 + "</start>"
								+ "</interval>";
						for (int j = 0; j < intervalblockValues.size(); j++) {
							XemtecValues row = intervalblockValues.get(j);
							Date dateRow = row.getReadDate();
							
							if (j != 0) {
								Date dateSecond = intervalblockValues.get(j).getReadDate();
								Date dateFirst = intervalblockValues.get(j - 1).getReadDate();
								long diff = dateSecond.getTime() - dateFirst.getTime();
								durationIntervalReading = diff / 1000;
							}
							
							gc = (GregorianCalendar) GregorianCalendar.getInstance();
							gc.setTime(dateRow);
							gc.add(java.util.Calendar.SECOND, -((int) durationIntervalReading));
							String formattedValue = row.getFormattedValue();
							formattedValue = formattedValue.replaceAll(",", ".");
							logger.debug("formattedValue:" + formattedValue);
							
							Double value = Double.valueOf(formattedValue);
							logger.debug("formattedValue:" + value);
							
							entry += "<IntervalReading>" + "<timePeriod>" + "<duration>" + durationIntervalReading
									+ "</duration>" + "<start>" + gc.getTime().getTime() / 1000 + "</start>"
									+ "</timePeriod>";
							
							value = value / ((double) 0.001);
							logger.debug("formattedValue:" + value);
							
							DecimalFormatSymbols dfs = new DecimalFormatSymbols();
							dfs.setDecimalSeparator(',');
							dfs.setGroupingSeparator('.');
							
							DecimalFormat df2 = new DecimalFormat("#,###,##0");
							df2.setDecimalFormatSymbols(dfs);
							String dd2dec = df2.format(value);
							dd2dec = dd2dec.replaceAll("\\.", "");
							logger.debug("formattedValue:" + dd2dec);
							entry += "<value>" + dd2dec + "</value>" + "</IntervalReading>";
						}
						XemtecValues row = intervalblockValues.get(intervalblockValues.size() - 1);
						Date dateRow = row.getReadDate();
						String published = dfDateTime.format(dateRow);
						entry += "</IntervalBlock>" + "</content>" + "<published>" + published + "</published>"
								+ "<updated>" + published + "</updated>" + "</entry>";
						
						logger.debug("Entry xml:" + entry);
						
						String urlCreateIntervalBlock = ConfigValues.URL_DATA_CUSTODIAN + "RetailCustomer/"
								+ ConfigUtils.getXemtecConfigValue(Constants.ID_RETAIL_CUSTOMER_PREFIX + i)
								+ "/UsagePoint/"
								+ ConfigUtils.getXemtecConfigValue(Constants.ID_USAGE_POINT_PREFIX + i)
								+ "/MeterReading/"
								+ ConfigUtils.getXemtecConfigValue(Constants.ID_METER_READING_PREFIX + i)
								+ "/IntervalBlock";
						
						logger.debug("urlCreateIntervalBlock:" + urlCreateIntervalBlock);
						logger.debug("Inizio creazione interval block su datacustodian");
						
						//URL url = new URL(urlCreateIntervalBlock);
						URL url = new URL(urlCreateIntervalBlock + "?access_token="
								+ ConfigValues.AUTHORIZATION_DATA_CUSTODIAN.replace("Bearer ", ""));
						connServlet = (HttpURLConnection) url.openConnection();
						connServlet.setRequestMethod("POST");
						connServlet.setDoOutput(true);
						connServlet.setDoInput(true);
						connServlet.setFollowRedirects(true);
						connServlet.setUseCaches(false);
						//connServlet.setRequestProperty("Authorization", ConfigValues.AUTHORIZATION_DATA_CUSTODIAN);
						// content type
						connServlet.setRequestProperty("Content-Type", "application/atom+xml");
						
						writer = new OutputStreamWriter(connServlet.getOutputStream());
						
						writer.write(entry);
						writer.flush();
						writer.close();
						
						/*
						 * in = new BufferedReader(new InputStreamReader(connServlet.getInputStream())); String output;
						 * String line=""; for (output = ""; (line = in.readLine()) != null; output = output +
						 * line.toString().trim()) { ; }
						 */
						
						String output = IOUtils.toString(connServlet.getInputStream(), "UTF-8");
						output = output.replaceAll("\t", "");
						output = output.replaceAll("\n", "");
						output = output.replaceAll("\r", "");
						output = output.trim();
						
						//logger.debug("RISPOSTA DATACUSTODIAN:" + output);		            
						logger.debug("Fine creazione interval block su datacustodian");
						response += output + "</intervalBlockEntry>";
						// in.close();
						connServlet.disconnect();
					}
					else {
						throw new CreateIntervalBlocksException(
								"Non e' stato inserito nessun interval block per questo usage point.");
					}
					
				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
					logger.error("Eccezione:", e);
					response += "<error>" + e.getLocalizedMessage() + "</error></intervalBlockEntry>";
				}
				catch (SQLException e) {
					e.printStackTrace();
					logger.error("Eccezione:", e);
					response += "<error>" + e.getLocalizedMessage() + "</error></intervalBlockEntry>";
				}
				catch (SAXException e) {
					e.printStackTrace();
					logger.error("Eccezione:", e);
					response += "<error>" + e.getLocalizedMessage() + "</error></intervalBlockEntry>";
				}
				catch (ParseException e) {
					e.printStackTrace();
					logger.error("Eccezione:", e);
					response += "<error>" + e.getLocalizedMessage() + "</error></intervalBlockEntry>";
				}
				catch (ParserConfigurationException e) {
					e.printStackTrace();
					logger.error("Eccezione:", e);
					response += "<error>" + e.getLocalizedMessage() + "</error></intervalBlockEntry>";
				}
				catch (IOException e) {
					e.printStackTrace();
					logger.error("Eccezione:", e);
					response += "<error>" + e.getLocalizedMessage() + "</error></intervalBlockEntry>";
				}
				catch (CreateIntervalBlocksException e) {
					e.printStackTrace();
					logger.error("Eccezione:", e);
					response += "<error>" + e.getLocalizedMessage() + "</error></intervalBlockEntry>";
				}
				catch (Exception e) {
					e.printStackTrace();
					logger.error("Eccezione:", e);
					response += "<error>" + e.getLocalizedMessage() + "</error></intervalBlockEntry>";
				}
				finally {
					/*
					 * if (in!=null) { try { in.close(); } catch (IOException e) { e.printStackTrace();
					 * logger.error("Eccezione:", e); } }
					 */
					if (connServlet != null) {
						connServlet.disconnect();
					}
					if (writer != null) {
						try {
							writer.close();
						}
						catch (IOException e) {
							e.printStackTrace();
							logger.error("Eccezione:", e);
						}
					}
				}
				
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Eccezione:", e);
			response += "<error>" + e.getLocalizedMessage() + "</error>";
		}
		response += "</CreateIntervalBlockResponse>";
		
		logger.debug(response);
		
		return response;
		
	}
	
	public String getLastIntervalBlock(long indexUsagePoint) throws IOException {
		//BufferedReader in   = null;
		HttpURLConnection connServlet = null;
		String response = "";
		try {
			
			String urlReadIntervalBlock = ConfigValues.URL_DATA_CUSTODIAN + "Batch/RetailCustomer/"
					+ ConfigUtils.getXemtecConfigValue(Constants.ID_RETAIL_CUSTOMER_PREFIX + indexUsagePoint)
					+ "/UsagePoint/"
					+ ConfigUtils.getXemtecConfigValue(Constants.ID_USAGE_POINT_PREFIX + indexUsagePoint) + "/"
					+ "MeterReading/"
					+ ConfigUtils.getXemtecConfigValue(Constants.ID_METER_READING_PREFIX + indexUsagePoint);
			
			logger.debug("urlReadIntervalBlock:" + urlReadIntervalBlock);
			
			logger.debug("Inizio lettura IntervalBlock");
			
			URL url = new URL(urlReadIntervalBlock + "?access_token="
					+ ConfigValues.AUTHORIZATION_DATA_CUSTODIAN.replace("Bearer ", ""));
			
			connServlet = (HttpURLConnection) url.openConnection();
			connServlet.setRequestMethod("GET");
			connServlet.setDoOutput(true);
			connServlet.setDoInput(true);
			connServlet.setUseCaches(false);
			//connServlet.setRequestProperty ("Authorization", ConfigValues.AUTHORIZATION_DATA_CUSTODIAN);
			
			/*
			 * in = new BufferedReader(new InputStreamReader(connServlet.getInputStream())); String output; String
			 * line=""; for (output = ""; (line = in.readLine()) != null; output = output + line.toString().trim()) { ;
			 * }
			 */
			
			String output = IOUtils.toString(connServlet.getInputStream(), "UTF-8");
			
			output = output.replaceAll("\t", "");
			output = output.replaceAll("\n", "");
			output = output.replaceAll("\r", "");
			output = output.trim();
			//logger.debug("RISPOSTA DATACUSTODIAN:" + output);	
			response = output;
			logger.debug("Fine lettura IntervalBlock");
			
			//in.close();
			connServlet.disconnect();
			
		}
		finally {
			if (connServlet != null) {
				connServlet.disconnect();
			}
			/*
			 * if (in!=null) { in.close(); }
			 */
		}
		
		// logger.debug(response);
		return response;
	}
	
	/**
	 * Will generate unique time based UUID where the next UUID is always greater then the previous.
	 */
	public final static UUID generateId() throws Exception {
		return generateIdFromTimestamp(System.currentTimeMillis());
	}
	
	public final static UUID generateIdFromTimestamp(long currentTimeMillis) {
		long time;
		
		synchronized (lock) {
			if (currentTimeMillis > lastTime) {
				lastTime = currentTimeMillis;
				clockSequence = 0;
			}
			else {
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
