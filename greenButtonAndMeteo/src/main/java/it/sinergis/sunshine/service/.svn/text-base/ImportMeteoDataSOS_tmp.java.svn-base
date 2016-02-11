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

public class ImportMeteoDataSOS_tmp {
	/* static information */
	private static Logger logger = Logger.getLogger(ImportMeteoDataSOS.class.getName());
	private static ImportMeteoDataSOS_tmp importMeteoDataSOS1;
	private final static SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private final static SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static SimpleDateFormat df3 = new SimpleDateFormat("yyyy-MM-dd");
	private final static DateTimeFormatter parser2 = ISODateTimeFormat.dateTimeNoMillis();
	
	/* */
	private String offering;
	private String obTypename;
	private String obIdentifier;
	private String prefixIdentifier;
	private String prefixObservedIdentifier;
	private String urlSos;
	private int countTypeNames;
	private ArrayList<String> wfsTagNames;
	private ArrayList<String> wfsTagUrlNames;
	
	private String username = ConfigUtils.getImportMeteoDataConfigValue(Constants.USER_WFS_METEO_NAME);
	private String password = ConfigUtils.getImportMeteoDataConfigValue(Constants.PASSWORD_WFS_METEO_NAME);
	private String userpass = username + ":" + password;
	
	private String hourScheduling;
	
	private ImportMeteoDataSOS_tmp() {
		offering = ConfigUtils.getImportMeteoDataConfigValue(Constants.OFFERING_NAME); //get base url offering
		obTypename = ConfigUtils.getImportMeteoDataConfigValue(Constants.OBSERVATION_TYPE_NAME); //get observation type
		obIdentifier = ConfigUtils.getImportMeteoDataConfigValue(Constants.OBSERVATION_IDENTIFIER_NAME); //get observation
		prefixIdentifier = ConfigUtils.getImportMeteoDataConfigValue(Constants.PROCEDURE_IDENTIFIER_NAME); //get procedure
		prefixObservedIdentifier = ConfigUtils
				.getImportMeteoDataConfigValue(Constants.OBSERVED_PROPERTY_IDENTIFIER_NAME); //
		urlSos = ConfigUtils.getImportMeteoDataConfigValue(Constants.URL_SOS_METEO_NAME);
		
		wfsTagNames = new ArrayList<String>();
		wfsTagUrlNames = new ArrayList<String>();
		
		wfsTagNames.add(Constants.WFS_FORECAST_PROC); // nome nodo meteo
		wfsTagNames.add(Constants.WFS_OBSERVATION_PROC);//
		wfsTagUrlNames.add(Constants.WFS_FORECAST); //url meteo
		wfsTagUrlNames.add(Constants.WFS_OBSERVATION); //url osservazioni
		
		countTypeNames = ConfigValues.COUNT_TYPENAMES_METEO;
		hourScheduling = ConfigUtils.getImportMeteoDataConfigValue(Constants.HOUR_SCHEDULING);
		
		username = ConfigUtils.getImportMeteoDataConfigValue(Constants.USER_WFS_METEO_NAME);
		password = ConfigUtils.getImportMeteoDataConfigValue(Constants.PASSWORD_WFS_METEO_NAME);
		userpass = username + ":" + password;
	}
	
	public static ImportMeteoDataSOS_tmp getInstance() {
		if (importMeteoDataSOS1 == null) {
			importMeteoDataSOS1 = new ImportMeteoDataSOS_tmp();
		}
		return importMeteoDataSOS1;
	}
	
	public synchronized String insertObservations() {
		String response = "<ImportMeteoDataResponse xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:sos=\"http://www.opengis.net/sos/2.0\">";
		try {
			if (countTypeNames == 0) {
				throw new ImportMeteoDataException("Nessun TypeName specificato nel file di configurazione.");
			}
			
			for (int wfsIndex = 0; wfsIndex < wfsTagUrlNames.size(); wfsIndex++) {
				String wfsNodeName = wfsTagNames.get(wfsIndex);
				String wfsNodeUrlName = wfsTagUrlNames.get(wfsIndex);
				
				int countLocations = ConfigUtils.getImportMeteoDataConfigCount("Procedure", wfsNodeName);
				if (countLocations == 0) {
					throw new ImportMeteoDataException("Nessuna procedure presente nel file di configurazione.");
				}
				
				for (int typeIndex = 0; typeIndex < countTypeNames; typeIndex++) {
					String typename = ConfigUtils.getImportMeteoDataConfigAttribute(Constants.TYPE_NAME, null,
							typeIndex + 1, "name");
					
					response += insertSingleObservation(typeIndex, wfsNodeName, wfsNodeUrlName, countLocations);
					
				}
				
			}
			
		}
		catch (ImportMeteoDataException e) {
			e.printStackTrace();
			logger.error("Eccezione:", e);
			response += "<error>" + e.getLocalizedMessage() + "</error>";
		}
		
		return response;
	}
	
	private String insertSingleObservation(int typeIndex, String wfsNodeName, String wfsNodeUrlName, int countLocations) {
		String response = "";
		
		String typename = ConfigUtils.getImportMeteoDataConfigAttribute(Constants.TYPE_NAME, null, typeIndex + 1,
				"name");
		response += "<typeName name=\"" + typename + "\">";
		try {
			String unit = ConfigUtils.getImportMeteoDataConfigValue(Constants.UNIT_NAME, Constants.TYPE_NAME,
					typeIndex + 1);
			String unitCode = ConfigUtils.getImportMeteoDataConfigValue(Constants.UNIT_CODE_NAME, Constants.TYPE_NAME,
					typeIndex + 1);
			String observedProperty = ConfigUtils.getImportMeteoDataConfigValue(Constants.OBSERVED_PROPERTY_NAME,
					Constants.TYPE_NAME, typeIndex + 1);
			String frequency = ConfigUtils.getImportMeteoDataConfigValue(Constants.FREQUENCY_NAME, Constants.TYPE_NAME,
					typeIndex + 1);
			HashMap<String, List<Node>> forecasts = getDatiMeteo(typename, wfsNodeName, wfsNodeUrlName);
			response += uploadSOSObservation(countLocations, wfsNodeName, unit, unitCode, observedProperty, frequency,
					forecasts);
			
		}
		catch (IOException | ParserConfigurationException | SAXException | ImportMeteoDataException
				| MissingResourceException | ParseException e) {
			e.printStackTrace();
			logger.error("Eccezione:", e);
			response += "<error>" + e.getLocalizedMessage() + "</error></typeName>";
		}
		return response;
	}
	
	public HashMap<String, List<Node>> getDatiMeteo(String typename, String wfsNodeName, String wfsNodeUrlName)
			throws MalformedURLException, ProtocolException, IOException, ParserConfigurationException, SAXException,
			ImportMeteoDataException {
		HashMap<String, List<Node>> forecasts = new HashMap<String, List<Node>>();
		//TO riasettare le informazioni 
		String meteoOutput = executeDatiMeteoRequest(typename, wfsNodeName, wfsNodeUrlName);
		InputStream ibIs = new StringInputStream(meteoOutput);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document docDatiMeteo = db.parse(ibIs);
		
		if (docDatiMeteo == null) {
			throw new ImportMeteoDataException("Wfs is invalid");
		}
		if (docDatiMeteo.getElementsByTagNameNS(Constants.URI_SIGYM, typename) != null
				&& docDatiMeteo.getElementsByTagNameNS(Constants.URI_SIGYM, typename).getLength() > 0) {
			NodeList listForecasts = docDatiMeteo.getElementsByTagNameNS(Constants.URI_SIGYM, typename);
			//scrorro i forecast e creo una hashmap dove la chiave  l'id della location quindi del pilot mentre
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
		
		return forecasts;
		
	}
	
	private String executeDatiMeteoRequest(String typename, String wfsNodeName, String wfsUrlMeteo)
			throws MalformedURLException, ProtocolException, IOException {
		
		HttpURLConnection connServlet = null;
		String response = "";
		String output = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		try {
			logger.debug("Inizio lettura WFS dati meteo tipo " + typename + ", :" + wfsNodeName);
			
			//qui si carica l'url wfs 
			//String urlMeteo = ConfigUtils.getImportMeteoDataConfigValue(listWfs.get(indexWfs - 1));	
			String urlMeteo = ConfigUtils.getImportMeteoDataConfigValue(wfsUrlMeteo);
			
			logger.debug("url metero " + urlMeteo);
			System.out.println("url metero " + urlMeteo);
			GregorianCalendar gc = new GregorianCalendar();
			//viene settato a mezza notte
			gc.set(GregorianCalendar.HOUR_OF_DAY, 0); //anything 0 - 23
			gc.set(GregorianCalendar.MINUTE, 0);
			gc.set(GregorianCalendar.SECOND, 0);
			
			if (wfsNodeName.equalsIgnoreCase(Constants.WFS_OBSERVATION_PROC)) {
				gc.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(hourScheduling));
				
				gc.add(GregorianCalendar.DAY_OF_MONTH, -1);
				
				GregorianCalendar gc1 = new GregorianCalendar();
				//viene settato a mezza notte				
				gc1.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(hourScheduling)); //anything 0 - 23
				gc1.set(GregorianCalendar.MINUTE, 0);
				gc1.set(GregorianCalendar.SECOND, 0);
				gc1.add(GregorianCalendar.HOUR_OF_DAY, -1);
				
				String base_time_old = df.format(gc.getTime());
				String base_time_new = df.format(gc1.getTime());
				String base_time = base_time_old + ',' + base_time_new;
				
				urlMeteo += "&TYPENAME=" + typename + "&TIME=" + base_time;
			}
			else {
				String base_time = df.format(gc.getTime());
				urlMeteo += "&TYPENAME=" + typename + "&BASE_TIME=" + base_time;
			}
			
			//urlMeteo += "&TYPENAME=" + typename + "&BASE_TIME=2014-10-04T00:00:00Z";
			//WFS METEO
			System.out.println("url metero " + urlMeteo);
			URL url = new URL(urlMeteo);
			
			connServlet = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
			connServlet.setDoOutput(true);
			connServlet.setDoInput(true);
			connServlet.setRequestMethod("POST");
			connServlet.setUseCaches(false);
			
			String basicAuth = "Basic " + new String(new sun.misc.BASE64Encoder().encode(userpass.getBytes()));
			connServlet.setRequestProperty("Authorization", basicAuth);
			
			InputStream imputStream = connServlet.getInputStream();
			logger.debug("Fine lettura WFS dati meteo");
			output = IOUtils.toString(connServlet.getInputStream(), "UTF-8");
			
			connServlet.disconnect();
			
		}
		finally {
			if (connServlet != null) {
				connServlet.disconnect();
			}
		}
		
		//logger.debug(response);
		
		return output;
	}
	
	private String uploadSOSObservation(int countLocations, String wfsNodeName, String unit, String unitCode,
			String observedProperty, String frequency, HashMap<String, List<Node>> forecasts)
			throws ImportMeteoDataException, ParseException {
		String response = "";
		for (int i = 0; i < countLocations; i++) {
			boolean procedureSaved = false;
			
			//mi leggo l'id <Procedure id="86" code="VDN-F00">
			String locationid = ConfigUtils.getImportMeteoDataConfigAttribute(Constants.PROCEDURE_NAME, wfsNodeName,
					i + 1, "id");
			//mi leggo il code <Procedure id="86" code="VDN-F00">
			String procedureCode = ConfigUtils.getImportMeteoDataConfigAttribute(Constants.PROCEDURE_NAME, wfsNodeName,
					i + 1, "code");
			response += "<insertObservation procedure=\"" + locationid + "\">";
			List<Node> forecastsLocation = forecasts.get(locationid);
			if (forecastsLocation == null || forecastsLocation.size() == 0) {
				throw new ImportMeteoDataException("Nessuna observation inserita per questa procedure:" + wfsNodeName);
			}
			
			String insertObservation = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
					+ "<env:Body>"
					+ "<sos:InsertObservation xmlns:swes=\"http://www.opengis.net/swes/2.0\" xmlns:sos=\"http://www.opengis.net/sos/2.0\" "
					+ "xmlns:swe=\"http://www.opengis.net/swe/2.0\" xmlns:sml=\"http://www.opengis.net/sensorML/1.0.1\" "
					+ "xmlns:gml=\"http://www.opengis.net/gml/3.2\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
					+ "xmlns:om=\"http://www.opengis.net/om/2.0\" xmlns:sams=\"http://www.opengis.net/samplingSpatial/2.0\" "
					+ "xmlns:sf=\"http://www.opengis.net/sampling/2.0\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" service=\"SOS\" version=\"2.0.0\">"
					+ "<sos:offering>" + offering + procedureCode + "_" + observedProperty + "_" + unitCode + "_"
					+ frequency + "</sos:offering>";
			
			for (int j = 0; j < forecastsLocation.size(); j++) {
				Element forecastLocation = (Element) forecastsLocation.get(j);
				String basetime = null, starttime = null, endtime, value = null, endtimeidentifier = null;
				Node basetimenode, timenode = null;
				Date basetimeToDelete = null;
				
				Node valuenode = forecastLocation.getElementsByTagNameNS(Constants.URI_SIGYM, "value").item(0);
				if (wfsNodeName.equalsIgnoreCase(Constants.WFS_OBSERVATION_PROC)) {
					basetimenode = forecastLocation.getElementsByTagNameNS(Constants.URI_SIGYM, "timestamp").item(0);
					timenode = forecastLocation.getElementsByTagNameNS(Constants.URI_SIGYM, "timestamp").item(0);
				}
				else {
					basetimenode = forecastLocation.getElementsByTagNameNS(Constants.URI_SIGYM, "base_time").item(0);
					timenode = forecastLocation.getElementsByTagNameNS(Constants.URI_SIGYM, "time").item(0);
				}
				
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
				
				endtimeidentifier = df2.format(gc.getTime());
				
				SimpleDateFormat sdf = new SimpleDateFormat();
				String dateAsString = sdf.format(parser2.parseDateTime(basetime).toDate());
				Date basetimeDate = sdf.parse(dateAsString);
				//										Date basetimeDate = df1.parse(basetime);
				
				String basetimeidentifier = df3.format(basetimeDate);
				//mi salvo il basetime per usare nel delete delle osservazioni
				/*
				 * if (basetimeToDelete == null) { gc.setTime(basetimeDate); gc.add(java.util.Calendar.HOUR, -24);
				 * //String baseTimePrec = df3.format(gc.getTime()); basetimeToDelete = gc.getTime(); }
				 */
				/*
				 * if (!procedureSaved && !wfsCalled && !typeParsed) { listIdentifierToDelete.add(obIdentifier +
				 * procedureCode); procedureSaved = true; }
				 */
				insertObservation += genereteInsertObservation(procedureCode, observedProperty, unitCode, frequency,
						endtimeidentifier, basetimeidentifier, wfsNodeName, i, j, unit, value, starttime, endtime,
						basetime);
				
			}
			
			insertObservation += "</sos:InsertObservation></env:Body></env:Envelope>";
			logger.info("insertObservation: " + insertObservation);
			
			response += sendSOSObservation(insertObservation) + "</insertObservation>";
			
			/*
			 * Delete old forecast make sure this method
			 */
			
			if (wfsNodeName.equalsIgnoreCase(Constants.WFS_FORECAST_PROC)) {
				GregorianCalendar gc = new GregorianCalendar();
				String basetimeidentifier = df3.format(gc.getTime());
				
				String notlike = obIdentifier + procedureCode + "_" + observedProperty + "_" + unitCode + "_"
						+ frequency + "/" + basetimeidentifier;
				String like = obIdentifier + procedureCode + "_" + observedProperty + "_" + unitCode + "_" + frequency
						+ "/";
				
				DatabaseSos40 dbsos = new DatabaseSos40();
				dbsos.delete2(like, notlike);
				
			}
			
			//
			
		}
		return response;
	}
	
	private String sendSOSObservation(String insertObservation) {
		BufferedReader in = null;
		HttpURLConnection connServlet = null;
		OutputStreamWriter writer = null;
		URL url;
		String response;
		try {
			url = new URL(urlSos);
			
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
			writer.write(insertObservation);
			writer.flush();
			writer.close();
			
			in = new BufferedReader(new InputStreamReader(connServlet.getInputStream()));
			String output;
			String line = "";
			
			for (output = ""; (line = in.readLine()) != null; output = output + line.toString().trim()) {
				;
			}
			
			/*
			 * output = output.replaceAll("\t", ""); output = output.replaceAll("\n", ""); output =
			 * output.replaceAll("\r", ""); output = output.trim();
			 */
			
			int pos = output.indexOf("<soap:Body>");
			if (pos != -1) {
				output = output.substring(pos + 11);
				pos = output.indexOf("</soap:Body>");
				if (pos != -1) {
					output = output.substring(0, pos);
				}
			}
			in.close();
			connServlet.disconnect();
			response = output;
			logger.info("RISPOSTA insertObservation:" + output);
		}
		catch (IOException e) {
			e.printStackTrace();
			logger.error("Eccezione:", e);
			response = "<error>" + e.getLocalizedMessage() + "</error>";//</insertObservation>";
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
		return response;
		
	}
	
	private String genereteInsertObservation(String procedureCode, String observedProperty, String unitCode,
			String frequency, String endtimeidentifier, String basetimeidentifier, String wfsNodeName, int index,
			int counter, String unit, String value, String starttime, String endtime, String basetime) {
		
		String identifierObservation = obIdentifier + procedureCode + "_" + observedProperty + "_" + unitCode + "_"
				+ frequency + "/" + basetimeidentifier + "/" + endtimeidentifier;
		
		String insertObservation = "<sos:observation>" + "<om:OM_Observation gml:id=\"o"
				+ counter
				+ "\">"
				+ "<gml:identifier codeSpace=\""
				+ ConfigUtils.getImportMeteoDataConfigValue(Constants.OBSERVATION_CODE_SPACE_NAME,
						Constants.PROCEDURE_NAME, wfsNodeName, index + 1)/*
																		 * qui ho aggiunto un livello di gerarchia
																		 */
				+ "\">"
				+ identifierObservation
				+ "</gml:identifier>"
				+ "<om:type xlink:href=\""
				+ obTypename
				+ "\"/>"
				+ "<om:phenomenonTime>"
				+ "<gml:TimePeriod gml:id=\"Ki.ObsTime."
				+ counter
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
				+ counter
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
				+ ConfigUtils.getImportMeteoDataConfigValue(Constants.FEATURE_OF_INTEREST_NAME,
						Constants.PROCEDURE_NAME, wfsNodeName, index + 1)/*
																		 * aggiunto un livello di gerarchia
																		 */
				+ "\"/>" + "<om:result xsi:type=\"gml:MeasureType\" uom=\"" + unit + "\">" + value + "</om:result>"
				+ "</om:OM_Observation>" + "</sos:observation>";
		return insertObservation;
	}
	
	public static void main(String args[]) {
		ImportMeteoDataSOS_tmp iSOS = ImportMeteoDataSOS_tmp.getInstance();
		iSOS.insertObservations();
	}
	
}
