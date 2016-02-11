package it.sinergis.sunshine.service;

import it.sinergis.sunshine.database.DatabaseSos40;
import it.sinergis.sunshine.util.ConfigUtils;
import it.sinergis.sunshine.util.ConfigValues;
import it.sinergis.sunshine.util.Constants;
import it.sinergis.sunshine.util.ImportMeteoDataException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.MissingResourceException;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.xml.bind.StringInputStream;

/**
 * Classe che si occupa di gestire la scrittura degli interval block a partire da file csv. Implementata come un
 * Singleton, vista la necessità di consultare i file csv di una directory.
 * 
 * @author Rossana
 */
public class CopyOfImportMeteoDataSOS {
	
	/** Logger. */
	private static Logger logger = Logger.getLogger(CopyOfImportMeteoDataSOS.class.getName());
	
	/**
	 * Variabile di istanza.
	 */
	private static CopyOfImportMeteoDataSOS importMeteoDataSOS;
	
	/**
	 * Costruttore privato, Singleton.
	 */
	private CopyOfImportMeteoDataSOS() {
		
	}
	
	/**
	 * Metodo di accesso alla classe.
	 * 
	 * @return
	 */
	public static CopyOfImportMeteoDataSOS getInstance() {
		if (importMeteoDataSOS == null) {
			importMeteoDataSOS = new CopyOfImportMeteoDataSOS();
		}
		
		return importMeteoDataSOS;
	}
	
	public synchronized String insertObservations() {
		BufferedReader in = null;
		HttpURLConnection connServlet = null;
		OutputStreamWriter writer = null;
		List<String> listIdentifierToDelete = new ArrayList<String>();
		Date basetimeToDelete = null;
		boolean wfsCalled = false;
		boolean typeParsed = false;
		String response = "<ImportMeteoDataResponse xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:sos=\"http://www.opengis.net/sos/2.0\">";
		HashMap<String, List<Node>> forecasts = null;
		ArrayList<String> wfsTagName = new ArrayList<String>();
		ArrayList<String> wfsTagUrlName = new ArrayList<String>();
		try {
			
			SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat df3 = new SimpleDateFormat("yyyy-MM-dd");
			
			SimpleDateFormat sdfUTC = new SimpleDateFormat();
			sdfUTC.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
			DateTimeFormatter parser2 = ISODateTimeFormat.dateTimeNoMillis();
			
			//#########################PARAMETRI STATICI############################
			
			//contiene il numero delle misure
			int countTypeNames = ConfigValues.COUNT_TYPENAMES_METEO;
			
			int countWfsUrls = ConfigValues.COUNT_WFSURLS_METEO;
			
			String offering = ConfigUtils.getImportMeteoDataConfigValue(Constants.OFFERING_NAME);
			String obTypename = ConfigUtils.getImportMeteoDataConfigValue(Constants.OBSERVATION_TYPE_NAME);
			String obIdentifier = ConfigUtils.getImportMeteoDataConfigValue(Constants.OBSERVATION_IDENTIFIER_NAME);
			String prefixIdentifier = ConfigUtils.getImportMeteoDataConfigValue(Constants.PROCEDURE_IDENTIFIER_NAME);
			String prefixObservedIdentifier = ConfigUtils
					.getImportMeteoDataConfigValue(Constants.OBSERVED_PROPERTY_IDENTIFIER_NAME);
			String urlSos = ConfigUtils.getImportMeteoDataConfigValue(Constants.URL_SOS_METEO_NAME);
			//			wfsTagName.add(ConfigUtils.getImportMeteoDataConfigValue(Constants.WFS_FORECAST));
			//			wfsTagName.add(ConfigUtils.getImportMeteoDataConfigValue(Constants.WFS_OBSERVATION));
			wfsTagName.add(Constants.WFS_FORECAST_PROC);
			wfsTagName.add(Constants.WFS_OBSERVATION_PROC);
			wfsTagUrlName.add(Constants.WFS_FORECAST);
			wfsTagUrlName.add(Constants.WFS_OBSERVATION);
			//#########################FINE PARAMETRI STATICI############################
			
			if (countTypeNames == 0) {
				throw new ImportMeteoDataException("Nessun TypeName specificato nel file di configurazione.");
			}
			
			//ciclo sui wfs_url
			//<FOR_WFS>
			
			for (int wfsIndex = 1; wfsIndex <= countWfsUrls; wfsIndex++) {
				
				//contiene il numero delle procedure
				int countLocations = ConfigUtils.getImportMeteoDataConfigCount("Procedure",
						wfsTagName.get(wfsIndex - 1));
				if (countLocations == 0) {
					throw new ImportMeteoDataException("Nessuna procedure presente nel file di configurazione.");
				}
				
				//per ogni typename--
				for (int tn = 1; tn <= countTypeNames; tn++) {
					//questa mi da il nome del typename
					String typename = ConfigUtils.getImportMeteoDataConfigAttribute(Constants.TYPE_NAME, null, tn,
							"name");
					
					try {
						response += "<typeName name=\"" + typename + "\">";
						String unit = ConfigUtils.getImportMeteoDataConfigValue(Constants.UNIT_NAME,
								Constants.TYPE_NAME, tn);
						String unitCode = ConfigUtils.getImportMeteoDataConfigValue(Constants.UNIT_CODE_NAME,
								Constants.TYPE_NAME, tn);
						String observedProperty = ConfigUtils.getImportMeteoDataConfigValue(
								Constants.OBSERVED_PROPERTY_NAME, Constants.TYPE_NAME, tn);
						String frequency = ConfigUtils.getImportMeteoDataConfigValue(Constants.FREQUENCY_NAME,
								Constants.TYPE_NAME, tn);
						
						//se active = 0 e ho uno start e un end 
						
						//leggo il wfs dei dati meteo
						String ib = getDatiMeteo(typename, wfsIndex, wfsTagUrlName);
						InputStream ibIs = new StringInputStream(ib);
						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						dbf.setNamespaceAware(true);
						
						DocumentBuilder db = dbf.newDocumentBuilder();
						
						Document docDatiMeteo = db.parse(ibIs);
						forecasts = new HashMap<String, List<Node>>();
						
						if (docDatiMeteo != null) {
							if (docDatiMeteo.getElementsByTagNameNS(Constants.URI_SIGYM, typename) != null
									&& docDatiMeteo.getElementsByTagNameNS(Constants.URI_SIGYM, typename).getLength() > 0) {
								NodeList listForecasts = docDatiMeteo.getElementsByTagNameNS(Constants.URI_SIGYM,
										typename);
								//scrorro i forecast e creo una hashmap dove la chiave è l'id della location quindi del pilot mentre
								//il valore e una lista di node che rappresentano i forecasts 
								for (int k = 0; k < listForecasts.getLength(); k++) {
									Element forecastEle = (Element) listForecasts.item(k);
									String fid = forecastEle.getAttribute("fid");
									String idLocation = null;
									if (fid != null) {
										StringTokenizer st = new StringTokenizer(fid, ",");
										while (st.hasMoreElements()) {
											idLocation = (String) st.nextElement();
										}
									}
									if (idLocation != null) {
										if (forecasts.get(idLocation) == null) {
											List<Node> lista = new ArrayList<Node>();
											lista.add(forecastEle);
											forecasts.put(idLocation, lista);
										}
										else {
											forecasts.get(idLocation).add(forecastEle);
										}
									}
								}
							}
							
							//per ogni procedure leggo l'hashmap che mi sono fatto prima e creo le insertObservation
							for (int i = 1; i <= countLocations; i++) {
								boolean procedureSaved = false;
								
								//mi leggo l'id <Procedure id="86" code="VDN-F00">
								String locationid = ConfigUtils.getImportMeteoDataConfigAttribute(
										Constants.PROCEDURE_NAME, wfsTagName.get(wfsIndex - 1), i, "id");
								//mi leggo il code <Procedure id="86" code="VDN-F00">
								String procedureCode = ConfigUtils.getImportMeteoDataConfigAttribute(
										Constants.PROCEDURE_NAME, wfsTagName.get(wfsIndex - 1), i, "code");
								
								try {
									response += "<insertObservation procedure=\"" + locationid + "\">";
									List<Node> forecastsLocation = forecasts.get(locationid);
									if (forecastsLocation == null || forecastsLocation.size() == 0) {
										throw new ImportMeteoDataException(
												"Nessuna observation inserita per questa procedure.");
									}
									
									String insertObservation = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
											+ "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
											+ "<env:Body>"
											+ "<sos:InsertObservation xmlns:swes=\"http://www.opengis.net/swes/2.0\" xmlns:sos=\"http://www.opengis.net/sos/2.0\" "
											+ "xmlns:swe=\"http://www.opengis.net/swe/2.0\" xmlns:sml=\"http://www.opengis.net/sensorML/1.0.1\" "
											+ "xmlns:gml=\"http://www.opengis.net/gml/3.2\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
											+ "xmlns:om=\"http://www.opengis.net/om/2.0\" xmlns:sams=\"http://www.opengis.net/samplingSpatial/2.0\" "
											+ "xmlns:sf=\"http://www.opengis.net/sampling/2.0\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" service=\"SOS\" version=\"2.0.0\">"
											+ "<sos:offering>" + offering + procedureCode + "_" + observedProperty
											+ "_" + unitCode + "_" + frequency + "</sos:offering>";
									
									for (int j = 0; j < forecastsLocation.size(); j++) {
										Element forecastLocation = (Element) forecastsLocation.get(j);
										String basetime = null;
										String starttime = null;
										String endtime = null;
										String value = null;
										Node basetimenode = null;
										if (wfsIndex > 1) {
											basetimenode = forecastLocation.getElementsByTagNameNS(Constants.URI_SIGYM,
													"timestamp").item(0);
										}
										else {
											basetimenode = forecastLocation.getElementsByTagNameNS(Constants.URI_SIGYM,
													"base_time").item(0);
										}
										
										Node timenode = null;
										if (wfsIndex > 1) {
											timenode = forecastLocation.getElementsByTagNameNS(Constants.URI_SIGYM,
													"timestamp").item(0);
										}
										else {
											timenode = forecastLocation.getElementsByTagNameNS(Constants.URI_SIGYM,
													"time").item(0);
										}
										
										Node valuenode = forecastLocation.getElementsByTagNameNS(Constants.URI_SIGYM,
												"value").item(0);
										
										if (basetimenode != null && basetimenode.getFirstChild() != null
												&& basetimenode.getFirstChild().getNodeValue() != null
												&& !basetimenode.getFirstChild().getNodeValue().isEmpty()) {
											basetime = basetimenode.getFirstChild().getNodeValue();
											
										}
										
										if (timenode != null && timenode.getFirstChild() != null
												&& timenode.getFirstChild().getNodeValue() != null
												&& !timenode.getFirstChild().getNodeValue().isEmpty()) {
											starttime = timenode.getFirstChild().getNodeValue();
											
										}
										if (valuenode != null && valuenode.getFirstChild() != null
												&& valuenode.getFirstChild().getNodeValue() != null
												&& !valuenode.getFirstChild().getNodeValue().isEmpty()) {
											value = valuenode.getFirstChild().getNodeValue();
										}
										//Date starttimeDate = df1.parse(starttime);
										Date starttimeDate = parser2.parseDateTime(starttime).toDate();
										
										GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
										gc.setTime(starttimeDate);
										gc.add(java.util.Calendar.SECOND, 3600);
										
										endtime = df1.format(gc.getTime());
										
										String endtimeidentifier = df2.format(gc.getTime());
										
										SimpleDateFormat sdf = new SimpleDateFormat();
										String dateAsString = sdf.format(parser2.parseDateTime(basetime).toDate());
										Date basetimeDate = sdf.parse(dateAsString);
										//										Date basetimeDate = df1.parse(basetime);
										
										String basetimeidentifier = df3.format(basetimeDate);
										//mi salvo il basetime per usare nel delete delle osservazioni
										if (basetimeToDelete == null) {
											gc.setTime(basetimeDate);
											gc.add(java.util.Calendar.HOUR, -24);
											//String baseTimePrec = df3.format(gc.getTime());
											basetimeToDelete = gc.getTime();
										}
										if (!procedureSaved && !wfsCalled && !typeParsed) {
											listIdentifierToDelete.add(obIdentifier + procedureCode);
											procedureSaved = true;
										}
										
										String identifierObservation = obIdentifier + procedureCode + "_"
												+ observedProperty + "_" + unitCode + "_" + frequency + "/"
												+ basetimeidentifier + "/" + endtimeidentifier;
										
										insertObservation += "<sos:observation>" + "<om:OM_Observation gml:id=\"o"
												+ j
												+ "\">"
												+ "<gml:identifier codeSpace=\""
												+ ConfigUtils.getImportMeteoDataConfigValue(
														Constants.OBSERVATION_CODE_SPACE_NAME,
														Constants.PROCEDURE_NAME, wfsTagName.get(wfsIndex - 1), i)/*
																												 * qui
																												 * ho
																												 * aggiunto
																												 * un
																												 * livello
																												 * di
																												 * gerarchia
																												 */
												+ "\">"
												+ identifierObservation
												+ "</gml:identifier>"
												+ "<om:type xlink:href=\""
												+ obTypename
												+ "\"/>"
												+ "<om:phenomenonTime>"
												+ "<gml:TimePeriod gml:id=\"Ki.ObsTime."
												+ j
												+ "\">"
												+ "<gml:beginPosition>"
												+ starttime
												+ "</gml:beginPosition>"
												+ "<gml:endPosition>"
												+ endtime
												+ "</gml:endPosition>"
												+ "</gml:TimePeriod>"
												+ "</om:phenomenonTime>"
												+ "<om:resultTime>"
												+ "<gml:TimeInstant gml:id=\"Ki.resTime."
												+ j
												+ "\">"
												+ "<gml:timePosition>"
												+ basetime
												+ "</gml:timePosition>"
												+ "</gml:TimeInstant>"
												+ "</om:resultTime>"
												+ "<om:procedure xlink:href=\""
												+ prefixIdentifier
												+ procedureCode
												+ "\"/>"
												+ "<om:observedProperty xlink:href=\""
												+ prefixObservedIdentifier
												+ observedProperty
												+ "\"/>"
												+ "<om:featureOfInterest xlink:href=\""
												+ ConfigUtils.getImportMeteoDataConfigValue(
														Constants.FEATURE_OF_INTEREST_NAME, Constants.PROCEDURE_NAME,
														wfsTagName.get(wfsIndex - 1), i)/*
																						 * aggiunto un livello di
																						 * gerarchia
																						 */
												+ "\"/>" + "<om:result xsi:type=\"gml:MeasureType\" uom=\"" + unit
												+ "\">" + value + "</om:result>" + "</om:OM_Observation>"
												+ "</sos:observation>";
										
									}
									
									insertObservation += "</sos:InsertObservation></env:Body></env:Envelope>";
									logger.debug("insertObservation: " + insertObservation);
									
									URL url = new URL(urlSos);
									
									connServlet = (HttpURLConnection) url.openConnection();
									connServlet.setRequestMethod("POST");
									connServlet.setDoOutput(true);
									connServlet.setDoInput(true);
									connServlet.setFollowRedirects(true);
									connServlet.setUseCaches(false);
									// content type
									connServlet.setRequestProperty("Content-Type", "application/soap+xml");
									
									writer = new OutputStreamWriter(connServlet.getOutputStream());
									
									//TODO da togliere il commento
									//writer.write(insertObservation);
									writer.flush();
									writer.close();
									
									in = new BufferedReader(new InputStreamReader(connServlet.getInputStream()));
									String output;
									String line = "";
									
									for (output = ""; (line = in.readLine()) != null; output = output
											+ line.toString().trim()) {
										;
									}
									
									/*
									 * output = output.replaceAll("\t", ""); output = output.replaceAll("\n", "");
									 * output = output.replaceAll("\r", ""); output = output.trim();
									 */
									
									int pos = output.indexOf("<soap:Body>");
									if (pos != -1) {
										output = output.substring(pos + 11);
										pos = output.indexOf("</soap:Body>");
										if (pos != -1) {
											output = output.substring(0, pos);
										}
									}
									
									response += output + "</insertObservation>";
									
									logger.debug("RISPOSTA insertObservation:" + output);
									
									in.close();
									connServlet.disconnect();
									
								}
								catch (MalformedURLException e) {
									e.printStackTrace();
									logger.error("Eccezione:", e);
									response += "<error>" + e.getLocalizedMessage() + "</error></insertObservation>";
								}
								catch (ProtocolException e) {
									e.printStackTrace();
									logger.error("Eccezione:", e);
									response += "<error>" + e.getLocalizedMessage() + "</error></insertObservation>";
								}
								catch (IOException e) {
									e.printStackTrace();
									logger.error("Eccezione:", e);
									response += "<error>" + e.getLocalizedMessage() + "</error></insertObservation>";
								}
								catch (MissingResourceException e) {
									e.printStackTrace();
									logger.error("Eccezione:", e);
									response += "<error>" + e.getLocalizedMessage() + "</error></insertObservation>";
								}
								catch (ParseException e) {
									e.printStackTrace();
									logger.error("Eccezione:", e);
									response += "<error>" + e.getLocalizedMessage() + "</error></insertObservation>";
								}
								catch (ImportMeteoDataException e) {
									e.printStackTrace();
									logger.error("Eccezione:", e);
									response += "<error>" + e.getLocalizedMessage() + "</error></insertObservation>";
								}
								catch (Exception e) {
									e.printStackTrace();
									logger.error("Eccezione:", e);
									response += "<error>" + e.getLocalizedMessage() + "</error></insertObservation>";
								}
								finally {
									if (in != null) {
										try {
											in.close();
										}
										catch (IOException e) {
											e.printStackTrace();
											logger.error("Eccezione:", e);
										}
									}
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
						else {
							throw new ImportMeteoDataException("Wfs dati meteo non valida.");
						}
						response += "</typeName>";
					}
					catch (SAXException e) {
						e.printStackTrace();
						logger.error("Eccezione:", e);
						response += "<error>" + e.getLocalizedMessage() + "</error></typeName>";
					}
					catch (ParserConfigurationException e) {
						e.printStackTrace();
						logger.error("Eccezione:", e);
						response += "<error>" + e.getLocalizedMessage() + "</error></typeName>";
					}
					catch (MalformedURLException e) {
						e.printStackTrace();
						logger.error("Eccezione:", e);
						response += "<error>" + e.getLocalizedMessage() + "</error></typeName>";
					}
					catch (ProtocolException e) {
						e.printStackTrace();
						logger.error("Eccezione:", e);
						response += "<error>" + e.getLocalizedMessage() + "</error></typeName>";
					}
					catch (IOException e) {
						e.printStackTrace();
						logger.error("Eccezione:", e);
						response += "<error>" + e.getLocalizedMessage() + "</error></typeName>";
					}
					catch (MissingResourceException e) {
						e.printStackTrace();
						logger.error("Eccezione:", e);
						response += "<error>" + e.getLocalizedMessage() + "</error></typeName>";
					}
					catch (ImportMeteoDataException e) {
						e.printStackTrace();
						logger.error("Eccezione:", e);
						response += "<error>" + e.getLocalizedMessage() + "</error></typeName>";
					}
					catch (Exception e) {
						e.printStackTrace();
						logger.error("Eccezione:", e);
						response += "<error>" + e.getLocalizedMessage() + "</error></typeName>";
					}
					typeParsed = true;
				}
				//metto a true, per segnalare il primo passaggio
				wfsCalled = true;
				
			}//</FOR_WFS>
			
			//se esiste la cancello
			DatabaseSos40 dbsos = new DatabaseSos40();
			dbsos.delete(listIdentifierToDelete, basetimeToDelete);
			
		}
		catch (MissingResourceException e) {
			e.printStackTrace();
			logger.error("Eccezione:", e);
			response += "<error>" + e.getLocalizedMessage() + "</error>";
		}
		catch (ImportMeteoDataException e) {
			e.printStackTrace();
			logger.error("Eccezione:", e);
			response += "<error>" + e.getLocalizedMessage() + "</error>";
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Eccezione:", e);
			response += "<error>" + e.getLocalizedMessage() + "</error>";
		}
		response += "</ImportMeteoDataResponse>";
		
		logger.debug(response);
		
		return response;
		
	}
	
	public String getDatiMeteo(String typename, int indexWfs, List<String> listWfs) throws MalformedURLException,
			ProtocolException, IOException {
		HttpURLConnection connServlet = null;
		String response = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		try {
			
			logger.debug("Inizio lettura WFS dati meteo: " + typename + " , " + indexWfs + " , "
					+ listWfs.get(indexWfs - 1) + "");
			
			//qui si carica l'url wfs 
			String urlMeteo = ConfigUtils.getImportMeteoDataConfigValue(listWfs.get(indexWfs - 1));
			logger.debug("url metero " + urlMeteo);
			GregorianCalendar gc = new GregorianCalendar();
			//viene settato a mezza notte
			gc.set(GregorianCalendar.HOUR_OF_DAY, 0); //anything 0 - 23
			gc.set(GregorianCalendar.MINUTE, 0);
			gc.set(GregorianCalendar.SECOND, 0);
			
			String base_time = df.format(gc.getTime());
			
			if (indexWfs > 1) {
				urlMeteo += "&TYPENAME=" + typename + "&TIME=" + base_time;
			}
			else {
				urlMeteo += "&TYPENAME=" + typename + "&BASE_TIME=" + base_time;
			}
			
			//urlMeteo += "&TYPENAME=" + typename + "&BASE_TIME=2014-10-04T00:00:00Z";
			//WFS METEO
			URL url = new URL(urlMeteo);
			
			connServlet = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
			connServlet.setDoOutput(true);
			connServlet.setDoInput(true);
			connServlet.setRequestMethod("POST");
			connServlet.setUseCaches(false);
			
			String username = ConfigUtils.getImportMeteoDataConfigValue(Constants.USER_WFS_METEO_NAME);
			String password = ConfigUtils.getImportMeteoDataConfigValue(Constants.PASSWORD_WFS_METEO_NAME);
			String userpass = username + ":" + password;
			
			String basicAuth = "Basic " + new String(new sun.misc.BASE64Encoder().encode(userpass.getBytes()));
			connServlet.setRequestProperty("Authorization", basicAuth);
			
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
			//logger.debug("RISPOSTA WFS:" + output);	
			response = output;
			logger.debug("Fine lettura WFS dati meteo");
			
			connServlet.disconnect();
			
		}
		finally {
			if (connServlet != null) {
				connServlet.disconnect();
			}
		}
		
		//logger.debug(response);
		return response;
	}
	
}
