package it.sinergis.sunshine.service;

import it.sinergis.sunshine.database.DatabaseCreateLog;
import it.sinergis.sunshine.database.DatabaseSos40;
import it.sinergis.sunshine.sos40.pojo.TaskStatus;
import it.sinergis.sunshine.util.ConfigUtils;
import it.sinergis.sunshine.util.Constants;
import it.sinergis.sunshine.util.ConvertDate;
import it.sinergis.sunshine.util.CreateFileCsvFromGBException;
import it.sinergis.sunshine.util.SplitString;
import it.sinergis.sunshine.util.UsagePoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.xml.bind.StringInputStream;

public class CreateFileCsvFromGreenButton {
	
	public static void main(String args[]) {
		CreateFileCsvFromGreenButton createFileCsvFromGreenButton = new CreateFileCsvFromGreenButton();
		createFileCsvFromGreenButton.createFileCsv();
		
	}
	
	/** Logger. */
	private static Logger logger;
	
	/**
	 * Variabile di istanza.
	 */
	private static CreateFileCsvFromGreenButton createFileCsvFromGreenButton;
	
	/**
	 * Costruttore privato, Singleton.
	 */
	private CreateFileCsvFromGreenButton() {
		logger = Logger.getLogger(this.getClass());
	}
	
	/**
	 * Metodo di accesso alla classe.
	 * 
	 * @return
	 */
	public static CreateFileCsvFromGreenButton getInstance() {
		if (createFileCsvFromGreenButton == null) {
			createFileCsvFromGreenButton = new CreateFileCsvFromGreenButton();
		}
		
		return createFileCsvFromGreenButton;
	}
	
	private TaskStatus taskStatus;
	
	public synchronized String createFileCsv() {
		String response = "<CreateFileCsvFromGreenButtonResponse>";
		taskStatus = DatabaseCreateLog.createLogField(TaskStatus.GRENBUTTONID);
		
		try {
			logger.debug("start the generation of CSV ");
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			long dataCustodianPoint = ConfigUtils.getGBToCsvConfigCount(Constants.DATA_CUSTODIAN);
			// per ogni data custodian
			String dataCustodinParent = Constants.DATA_CUSTODIAN;
			for (int i = 0; i < dataCustodianPoint; i++) {
				
				response += generateCsvForDataCustodian(i, dataCustodinParent);
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Eccezione:", e);
			
			taskStatus.setStatus(TaskStatus.ERROR);
			taskStatus.setDescription(e.getLocalizedMessage());
			DatabaseCreateLog.closeLog(taskStatus);
			
			return response + "<error>" + e + "</error></CreateFileCsvFromGreenButtonResponse>";
		}
		
		taskStatus.setStatus(TaskStatus.COMPLETE);
		taskStatus.setDescription("finished without error");
		DatabaseCreateLog.closeLog(taskStatus);
		
		return response + "</CreateFileCsvFromGreenButtonResponse>";
	}
	
	private String generateCsvForDataCustodian(int dataCustodianIndex, String dataCustodinParent) {
		//get genera information from the configuration  file 
		String response = "<usagePoint>";
		
		try {
			response += "<datacustodian>"
					+ ConfigUtils.getGBToCsvConfigValue(Constants.URL_DATA_CUSTODIAN_NAME, Constants.DATA_CUSTODIAN,
							dataCustodianIndex) + "</datacustodian>";
			
			String idSubscription = ConfigUtils.getGBToCsvConfigValue(Constants.ID_SUBSCRIPTION_NAME,
					dataCustodinParent, dataCustodianIndex);
			String authorization = ConfigUtils.getGBToCsvConfigValue(Constants.AUTHORIZATION_NAME, dataCustodinParent,
					dataCustodianIndex);
			String UrlDataCustodian = ConfigUtils.getGBToCsvConfigValue(Constants.URL_DATA_CUSTODIAN_NAME,
					dataCustodinParent, dataCustodianIndex);
			String UrlSOS = ConfigUtils.getGBToCsvConfigValue(Constants.URL_SOS_NAME, dataCustodinParent,
					dataCustodianIndex);
			String IdentifierObservation = ConfigUtils.getGBToCsvConfigValue(Constants.IDENTIFIER_OBSERVATION_NAME,
					dataCustodinParent, dataCustodianIndex);
			String IdLocalTimeParameters = ConfigUtils.getGBToCsvConfigValue(Constants.ID_LOCAL_TIME_NAME,
					dataCustodinParent, dataCustodianIndex);
			taskStatus.setStatus(TaskStatus.RUNNING);
			taskStatus.setDescription("loading configuration for " + UrlDataCustodian);
			DatabaseCreateLog.updateLog(taskStatus);
			
			LinkedList<UsagePoint> usagePoints = ConfigUtils.getGBToCsvUsagePoint(Constants.DATA_CUSTODIAN,
					dataCustodianIndex, Constants.USAGE_POINT_NAME);
			
			Object[] timeProperty = getTimeProperty(dataCustodianIndex);
			setOfferingname(usagePoints, dataCustodianIndex);
			setMeteringinformation(usagePoints, dataCustodianIndex);
			setIdentifierObservation(usagePoints, dataCustodianIndex);
			
			String dateLastReading = lastObservation(usagePoints, (String) timeProperty[0], (String) timeProperty[1],
					(Integer) timeProperty[2], (Integer) timeProperty[3]);
			//System.out.println(dateLastReading);
			
			//dateLastReading = null;//"2014-03-00T06:01:00Z";
			logger.debug(dateLastReading);
			for (UsagePoint up : usagePoints) {
				
				String xmlIntbloc = getIntervalBlock(dataCustodianIndex, up.getLastObservation(), up.getIdUsagePoint()
						+ "");
				
				writeIntervaBlock(xmlIntbloc, dataCustodianIndex, usagePoints, (String) timeProperty[0],
						(String) timeProperty[1], (Integer) timeProperty[2], (Integer) timeProperty[3], null);
				taskStatus.setStatus(TaskStatus.RUNNING);
				taskStatus.setDescription("uploaded " + up.getIdentifier());
				DatabaseCreateLog.updateLog(taskStatus);
			}
			
		}
		catch (IOException | ParserConfigurationException | SAXException | CreateFileCsvFromGBException
				| IndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			logger.error(e.getStackTrace());
			
			e.printStackTrace();
			response += "<error>" + e.getLocalizedMessage() + "</error>";
		}
		response += "</usagePoint>";
		
		return response;
	}
	
	private static String readFile() {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			String sCurrentLine;
			String BufferedReader;
			
			br = new BufferedReader(new FileReader("C:\\tmp\\xmlrequest.xml"));
			
			while ((sCurrentLine = br.readLine()) != null) {
				//System.out.println(sCurrentLine);
				sb.append(sCurrentLine);
			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (br != null)
					br.close();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return sb.toString();
	}
	
	//TODO improve this method
	private static UsagePoint getRigthUsagePoint(LinkedList<UsagePoint> usagePoints, int usageusagePointid,
			int meterReadingid, UsagePoint last) {
		if (last != null && last.getIdUsagePoint() == usageusagePointid && last.getIdMeterReading() == meterReadingid) {
			return last;
		}
		for (UsagePoint up : usagePoints) {
			if (up.getIdUsagePoint() == usageusagePointid && up.getIdMeterReading() == meterReadingid) {
				return up;
				
			}
		}
		return null;
		
	}
	
	private void writeIntervaBlock(String xmlIntbloc, int dataCustodianIndex, LinkedList<UsagePoint> UsagePoints,
			String dstStartRule, String dstEndRule, int dstOffset, int tzOffset, Date lastDateObservation)
			throws ParserConfigurationException, SAXException, IOException, CreateFileCsvFromGBException {
		InputStream ibIs = new StringInputStream(xmlIntbloc);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		Document docIntervalBlock = db.parse(ibIs);
		UsagePoint rup = null;
		int eleParentNumber = docIntervalBlock.getElementsByTagNameNS(Constants.URI_ATOM, "entry").getLength();
		for (int i = 0; i < eleParentNumber; i++) {
			Element eleParent = (Element) docIntervalBlock.getElementsByTagNameNS(Constants.URI_ATOM, "entry").item(i);
			Element ele = (Element) eleParent.getElementsByTagNameNS(Constants.URI_ATOM, "link").item(0);
			String href = ele.getAttribute("href");
			
			int usageusagePointid = SplitString.getUsagePointId(href);
			int meterReadingid = SplitString.getMeterReadingId(href);
			NodeList intervalBlockList = eleParent.getElementsByTagNameNS(Constants.URI_ESPI, "IntervalBlock");
			
			rup = getRigthUsagePoint(UsagePoints, usageusagePointid, meterReadingid, rup);
			
			if (rup != null) {
				int numCsvCreati = rup.getCounter();
				String nomeFile = rup.getOffering_name() + "_" + rup.getCommodity() + rup.getAccumulationbehaviour()
						+ "_" + rup.getUom() + "_" + rup.getTimeattribute();
				
				//System.out.println(nomeFile);
				logger.debug("Fine lettura UsagePoint con idUsagePoint=" + href);
				
				numCsvCreati = writeIntervalBlockInCsv(dataCustodianIndex, intervalBlockList, nomeFile,
						rup.getCurrency(), lastDateObservation, dstStartRule, dstEndRule, dstOffset, tzOffset,
						numCsvCreati);
				//numCsvCreati++;
				rup.setCounter(numCsvCreati);
			}
			
		}
		/*
		 * if (docIntervalBlock != null && docIntervalBlock.getElementsByTagNameNS(Constants.URI_ESPI, "entry") != null
		 * && docIntervalBlock.getElementsByTagNameNS(Constants.URI_ESPI, "entry").getLength() > 0) { NodeList
		 * intervalBlockList = docIntervalBlock.getElementsByTagNameNS(Constants.URI_ESPI, "entry"); //String nomeFile =
		 * usage_description + "_" + commodity + accumulationbehaviour + "_" + uom + "_" // + timeattribute; //scrive in
		 * un file csv con nome nomeFile gli intervalreading contenuti in ciascuno interval block //numCsvCreati =
		 * this.writeIntervalBlockInCsv(i, intervalBlockList, nomeFile, currency, // lastDateObservation, dstStartRule,
		 * dstEndRule, dstOffset, tzOffset); }
		 */
		//if (numCsvCreati == 0) {
		//throw new CreateFileCsvFromGBException("Nessun file csv creato per il corrente usagepoint.");
		//}
		
	}
	
	private void setIdentifierObservation(LinkedList<UsagePoint> usagePoints, int indexDataCustodian) {
		for (UsagePoint up : usagePoints) {
			
			String identifier = getIdentifierObservation(indexDataCustodian, up.getCommodity(), up.getOffering_name(),
					up.getAccumulationbehaviour(), up.getUom());
			up.setIdentifier(identifier);
			//System.out.println(up.getIdentifier());
		}
	}
	
	private void setMeteringinformation(LinkedList<UsagePoint> usagepoints, int indexDataCustodian)
			throws MalformedURLException, ProtocolException, IOException, ParserConfigurationException, SAXException,
			CreateFileCsvFromGBException {
		for (UsagePoint up : usagepoints) {
			String ib = getReadingType(indexDataCustodian, up.getIdReadingType());
			InputStream ibIs = new StringInputStream(ib);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document docUsagePoint = db.parse(ibIs);
			String[] usageDescription = getTypeProperty(docUsagePoint);
			//commodity, accumulationbehaviour, uom, timeattribute, currency
			if (up.getCommodity() == null || up.getCommodity().length() < 2) {
				up.setCommodity(usageDescription[0]);
			}
			
			up.setAccumulationbehaviour(usageDescription[1]);
			up.setUom(usageDescription[2]);
			up.setTimeattribute(usageDescription[3]);
			up.setCurrency(usageDescription[4]);
			
		}
		
	}
	
	private void setOfferingname(LinkedList<UsagePoint> usagepoints, int indexDataCustodian)
			throws MalformedURLException, ProtocolException, IOException, ParserConfigurationException, SAXException,
			CreateFileCsvFromGBException {
		for (UsagePoint up : usagepoints) {
			if (up.getOffering_name() != null && up.getOffering_name().length() > 1) {
				continue;
				//se � gi� presa dal file di configurazione viene ignorato il nome della description dell'usage point;
			}
			String ib = getUsagePoint(indexDataCustodian, up.getIdUsagePoint());
			InputStream ibIs = new StringInputStream(ib);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document docUsagePoint = db.parse(ibIs);
			String usageDescription = getUsageDescription(docUsagePoint);
			up.setOffering_name(usageDescription);
			
		}
		
	}
	
	private Object[] getTimeProperty(int index) throws MalformedURLException, ProtocolException, IOException,
			ParserConfigurationException, SAXException, CreateFileCsvFromGBException {
		String ib = getLocalTime(index);
		InputStream ibIs = new StringInputStream(ib);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document docReadingType = db.parse(ibIs);
		
		return getTimeProperty_private(docReadingType);
		
	}
	
	public String getUsagePoint(int indexDataCustodian, int idUsagePoint) throws MalformedURLException,
			ProtocolException, IOException {
		HttpURLConnection connServlet = null;
		String response = "";
		try {
			
			/*
			 * String urlReadUsagePoint = ConfigUtils.getGBToCsvConfigValue(Constants.URL_DATA_CUSTODIAN_NAME,
			 * Constants.DATA_CUSTODIAN, indexDataCustodian) + "Subscription/" +
			 * ConfigUtils.getGBToCsvConfigValue(Constants.ID_SUBSCRIPTION_NAME, Constants.DATA_CUSTODIAN,
			 * indexDataCustodian) + "/UsagePoint/" + idUsagePoint;
			 */
			
			String urlReadUsagePoint = ConfigUtils.getGBToCsvConfigValue(Constants.URL_DATA_CUSTODIAN_NAME,
					Constants.DATA_CUSTODIAN, indexDataCustodian)
					+ "Subscription/"
					+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_SUBSCRIPTION_NAME, Constants.DATA_CUSTODIAN,
							indexDataCustodian) + "/UsagePoint/" + idUsagePoint;
			//+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_USAGE_POINT_NAME, Constants.USAGE_POINT_NAME,
			//		indexDataCustodian);
			
			logger.debug("urlReadUsagePoint:" + urlReadUsagePoint);
			
			//logger.debug("Inizio lettura UsagePoint con idUsagePoint="
			//		+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_USAGE_POINT_NAME, Constants.USAGE_POINT_NAME,
			//				indexUsagePoint));
			
			URL url = new URL(urlReadUsagePoint);
			
			connServlet = (HttpURLConnection) url.openConnection();
			connServlet.setRequestMethod("GET");
			connServlet.setDoOutput(true);
			connServlet.setDoInput(true);
			connServlet.setUseCaches(false);
			connServlet.setRequestProperty(
					"Authorization",
					"Bearer "
							+ ConfigUtils.getGBToCsvConfigValue(Constants.AUTHORIZATION_NAME, Constants.DATA_CUSTODIAN,
									indexDataCustodian));
			
			String output = IOUtils.toString(connServlet.getInputStream(), "UTF-8");
			
			output = output.replaceAll("\t", "");
			output = output.replaceAll("\n", "");
			output = output.replaceAll("\r", "");
			output = output.trim();
			
			response = output;
			logger.debug("Fine lettura UsagePoint con idUsagePoint="
					+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_USAGE_POINT_NAME, Constants.USAGE_POINT_NAME,
							indexDataCustodian));
			
			connServlet.disconnect();
			
		}
		finally {
			if (connServlet != null) {
				connServlet.disconnect();
			}
		}
		
		logger.debug(response);
		
		return response;
	}
	
	public String getUsageDescription(Document docUsagePoint) throws CreateFileCsvFromGBException {
		String usage_description = null;
		//cerco la descrizione dello usage point che sarebbe il title della entry dello usage point
		if (docUsagePoint.getElementsByTagNameNS(Constants.URI_ESPI, "UsagePoint") != null
				&& docUsagePoint.getElementsByTagNameNS(Constants.URI_ESPI, "UsagePoint").item(0) != null) {
			Element eleUsagePoint = (Element) docUsagePoint.getElementsByTagNameNS(Constants.URI_ESPI, "UsagePoint")
					.item(0);
			if (eleUsagePoint.getParentNode() != null && eleUsagePoint.getParentNode().getParentNode() != null) {
				Element entryUsagePoint = (Element) eleUsagePoint.getParentNode().getParentNode();
				if (entryUsagePoint.getElementsByTagNameNS(Constants.URI_ATOM, "title") != null
						&& entryUsagePoint.getElementsByTagNameNS(Constants.URI_ATOM, "title").item(0) != null) {
					Element titleUsagePointEle = (Element) entryUsagePoint.getElementsByTagNameNS(Constants.URI_ATOM,
							"title").item(0);
					if (titleUsagePointEle.getFirstChild() != null
							&& titleUsagePointEle.getFirstChild().getNodeValue() != null
							&& !titleUsagePointEle.getFirstChild().getNodeValue().isEmpty()) {
						usage_description = titleUsagePointEle.getFirstChild().getNodeValue();
					}
				}
			}
		}
		if (usage_description == null) {
			throw new CreateFileCsvFromGBException("Manca la descrizione dello usage point.");
		}
		return usage_description;
	}
	
	public String[] getTypeProperty(Document docReadingType) throws CreateFileCsvFromGBException {
		//cerco il codice commodity espi all'interno del tag ReadingType dello usage point
		String commodityespi = null;
		String commodity = null;
		//cerco il codice accumulationbehaviour espi all'interno del tag ReadingType dello usage point
		String accumulationbehaviourespi = null;
		String accumulationbehaviour = null;
		//cerco il codice uom espi all'interno del tag ReadingType dello usage point
		String uomespi = null;
		String uom = null;
		//cerco il codice timeattribute espi all'interno del tag ReadingType dello usage point
		String timeattributeespi = null;
		String timeattribute = null;
		
		//cerco il codice currency per verificare che ci sia il costo nei dati di greenbutton
		String currency = null;
		
		if (docReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "ReadingType") != null
				&& docReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "ReadingType").item(0) != null) {
			Element eleReadingType = (Element) docReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "ReadingType")
					.item(0);
			if (eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "commodity") != null
					&& eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "commodity").item(0) != null) {
				Element commodityEle = (Element) eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "commodity")
						.item(0);
				if (commodityEle.getFirstChild() != null && commodityEle.getFirstChild().getNodeValue() != null
						&& !commodityEle.getFirstChild().getNodeValue().isEmpty()) {
					commodityespi = commodityEle.getFirstChild().getNodeValue();
				}
			}
			if (eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "accumulationBehaviour") != null
					&& eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "accumulationBehaviour").item(0) != null) {
				Element accumulationbehaviourEle = (Element) eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI,
						"accumulationBehaviour").item(0);
				if (accumulationbehaviourEle.getFirstChild() != null
						&& accumulationbehaviourEle.getFirstChild().getNodeValue() != null
						&& !accumulationbehaviourEle.getFirstChild().getNodeValue().isEmpty()) {
					accumulationbehaviourespi = accumulationbehaviourEle.getFirstChild().getNodeValue();
				}
			}
			if (eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "uom") != null
					&& eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "uom").item(0) != null) {
				Element uomEle = (Element) eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "uom").item(0);
				if (uomEle.getFirstChild() != null && uomEle.getFirstChild().getNodeValue() != null
						&& !uomEle.getFirstChild().getNodeValue().isEmpty()) {
					uomespi = uomEle.getFirstChild().getNodeValue();
				}
			}
			if (eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "timeAttribute") != null
					&& eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "timeAttribute").item(0) != null) {
				Element timeattributeEle = (Element) eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI,
						"timeAttribute").item(0);
				if (timeattributeEle.getFirstChild() != null && timeattributeEle.getFirstChild().getNodeValue() != null
						&& !timeattributeEle.getFirstChild().getNodeValue().isEmpty()) {
					timeattributeespi = timeattributeEle.getFirstChild().getNodeValue();
				}
			}
			if (eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "currency") != null
					&& eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "currency").item(0) != null) {
				Element currencyEle = (Element) eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "currency")
						.item(0);
				if (currencyEle.getFirstChild() != null && currencyEle.getFirstChild().getNodeValue() != null
						&& !currencyEle.getFirstChild().getNodeValue().isEmpty()) {
					currency = currencyEle.getFirstChild().getNodeValue();
				}
			}
		}
		if (commodityespi == null) {
			throw new CreateFileCsvFromGBException("Manca il codice commodity del ReadingType.");
		}
		//cerco nel file properties mappingGreenButtonToCsv il valore della variabile commodity_<commodityespi>  
		commodity = ConfigUtils.getGBToCsvConfigValueWithAttribute(Constants.COMMODITY_NAME,
				Constants.COMMODITY_ATTRIBUTE_ID_NAME, commodityespi);
		logger.debug("commodity:" + commodity);
		
		if (accumulationbehaviourespi == null) {
			throw new CreateFileCsvFromGBException("Manca il codice accumulationbehaviour del ReadingType.");
		}
		//cerco nel file properties mappingGreenButtonToCsv il valore della variabile accumulationbehaviour_<accumulationbehaviourespi>  
		accumulationbehaviour = ConfigUtils.getGBToCsvConfigValueWithAttribute(Constants.ACCUMULATION_BEHAVIOUR_NAME,
				Constants.ACCUMULATION_BEHAVIOUR_ATTRIBUTE_ID_NAME, accumulationbehaviourespi);
		logger.debug("accumulationbehaviour:" + accumulationbehaviour);
		
		if (uomespi == null) {
			throw new CreateFileCsvFromGBException("Manca il codice uom del ReadingType.");
		}
		//cerco nel file properties mappingGreenButtonToCsv il valore della variabile uom_<uomespi>  
		uom = ConfigUtils.getGBToCsvConfigValueWithAttribute(Constants.UOM_NAME, Constants.UOM_ATTRIBUTE_ID_NAME,
				uomespi);
		logger.debug("uom:" + uom);
		
		if (timeattributeespi == null) {
			throw new CreateFileCsvFromGBException("Manca il codice timeattribute del ReadingType.");
		}
		//cerco nel file properties mappingGreenButtonToCsv il valore della variabile timeattribute_<timeattributeespi>  
		timeattribute = ConfigUtils.getGBToCsvConfigValueWithAttribute(Constants.TIME_ATTRIBUTE_NAME,
				Constants.TIME_ATTRIBUTE_ID_NAME, timeattributeespi);
		logger.debug("timeattribute:" + timeattribute);
		logger.debug("currency:" + currency);
		
		//cerchiamo i dati del localtimeparameters
		Element eleLocalTimeParameters = null;
		
		return new String[] { commodity, accumulationbehaviour, uom, timeattribute, currency };
		
	}
	
	public Object[] getTimeProperty_private(Document docLocalTime) {
		Element eleLocalTimeParameters = null;
		
		if (docLocalTime != null
				&& docLocalTime.getElementsByTagNameNS(Constants.URI_ESPI, "LocalTimeParameters") != null
				&& docLocalTime.getElementsByTagNameNS(Constants.URI_ESPI, "LocalTimeParameters").item(0) != null) {
			eleLocalTimeParameters = (Element) docLocalTime.getElementsByTagNameNS(Constants.URI_ESPI,
					"LocalTimeParameters").item(0);
		}
		
		//cerco il dstEndRule espi all'interno del tag LocalTimeParameters dello usage point
		String dstEndRule = null;
		//cerco il dstStartRule espi all'interno del tag LocalTimeParameters dello usage point
		String dstStartRule = null;
		//cerco il dstOffset espi all'interno del tag LocalTimeParameters dello usage point
		String dstOffsetespi = null;
		//cerco il tzOffset espi all'interno del tag LocalTimeParameters dello usage point
		String tzOffsetespi = null;
		int dstOffset = 0;
		int tzOffset = 0;
		if (eleLocalTimeParameters != null) {
			if (eleLocalTimeParameters.getElementsByTagNameNS(Constants.URI_ESPI, "dstEndRule") != null
					&& eleLocalTimeParameters.getElementsByTagNameNS(Constants.URI_ESPI, "dstEndRule").item(0) != null) {
				Element dstEndRuleEle = (Element) eleLocalTimeParameters.getElementsByTagNameNS(Constants.URI_ESPI,
						"dstEndRule").item(0);
				if (dstEndRuleEle.getFirstChild() != null && dstEndRuleEle.getFirstChild().getNodeValue() != null
						&& !dstEndRuleEle.getFirstChild().getNodeValue().isEmpty()) {
					dstEndRule = dstEndRuleEle.getFirstChild().getNodeValue();
				}
			}
			if (eleLocalTimeParameters.getElementsByTagNameNS(Constants.URI_ESPI, "dstStartRule") != null
					&& eleLocalTimeParameters.getElementsByTagNameNS(Constants.URI_ESPI, "dstStartRule").item(0) != null) {
				Element dstStartRuleEle = (Element) eleLocalTimeParameters.getElementsByTagNameNS(Constants.URI_ESPI,
						"dstStartRule").item(0);
				if (dstStartRuleEle.getFirstChild() != null && dstStartRuleEle.getFirstChild().getNodeValue() != null
						&& !dstStartRuleEle.getFirstChild().getNodeValue().isEmpty()) {
					dstStartRule = dstStartRuleEle.getFirstChild().getNodeValue();
				}
			}
			if (eleLocalTimeParameters.getElementsByTagNameNS(Constants.URI_ESPI, "dstOffset") != null
					&& eleLocalTimeParameters.getElementsByTagNameNS(Constants.URI_ESPI, "dstOffset").item(0) != null) {
				Element dstOffsetEle = (Element) eleLocalTimeParameters.getElementsByTagNameNS(Constants.URI_ESPI,
						"dstOffset").item(0);
				if (dstOffsetEle.getFirstChild() != null && dstOffsetEle.getFirstChild().getNodeValue() != null
						&& !dstOffsetEle.getFirstChild().getNodeValue().isEmpty()) {
					dstOffsetespi = dstOffsetEle.getFirstChild().getNodeValue();
				}
			}
			if (eleLocalTimeParameters.getElementsByTagNameNS(Constants.URI_ESPI, "tzOffset") != null
					&& eleLocalTimeParameters.getElementsByTagNameNS(Constants.URI_ESPI, "tzOffset").item(0) != null) {
				Element tzOffsetEle = (Element) eleLocalTimeParameters.getElementsByTagNameNS(Constants.URI_ESPI,
						"tzOffset").item(0);
				if (tzOffsetEle.getFirstChild() != null && tzOffsetEle.getFirstChild().getNodeValue() != null
						&& !tzOffsetEle.getFirstChild().getNodeValue().isEmpty()) {
					tzOffsetespi = tzOffsetEle.getFirstChild().getNodeValue();
				}
			}
		}
		if (tzOffsetespi != null && dstOffsetespi != null && dstStartRule != null && dstEndRule != null
				&& !tzOffsetespi.isEmpty() && !dstOffsetespi.isEmpty() && !dstStartRule.isEmpty()
				&& !dstEndRule.isEmpty()) {
			dstOffset = Integer.valueOf(dstOffsetespi);
			tzOffset = Integer.valueOf(tzOffsetespi);
		}
		return new Object[] { dstStartRule, dstEndRule, dstOffset, tzOffset };
	}
	
	private String getIdentifierObservation(int index, String commodity, String usage_description,
			String accumulationbehaviour, String uom) {
		String identifierObservation;
		if (commodity.length() <= 3) {
			identifierObservation = ConfigUtils.getGBToCsvConfigValue(Constants.IDENTIFIER_OBSERVATION_NAME,
					Constants.DATA_CUSTODIAN, index)
					+ usage_description
					+ "_"
					+ commodity
					+ accumulationbehaviour
					+ "_" + uom;
		}
		else {
			identifierObservation = ConfigUtils.getGBToCsvConfigValue(Constants.IDENTIFIER_OBSERVATION_NAME,
					Constants.DATA_CUSTODIAN, index) + usage_description + "_" + commodity + "_" + uom;
		}
		return identifierObservation;
	}
	
	private String lastObservation(LinkedList<UsagePoint> identifierObservations, String dstStartRule,
			String dstEndRule, int dstOffset, int tzOffset) {
		
		Date lastDateObservation = null;
		String lastDateObservationString;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		try {
			for (UsagePoint identifierObservationUP : identifierObservations) {
				DatabaseSos40 dbso40 = new DatabaseSos40();
				String identifierObservation = identifierObservationUP.getIdentifier();
				//System.out.println(":" + identifierObservation.substring(0, 57));
				Date observation = dbso40.getLastObservationDate(identifierObservation.substring(0, 57));
				if (observation == null) {
					//controllo se esiste un ultima osservazione (se vuota verr� caricato per tutti le date )					
					lastDateObservation = null;
					identifierObservationUP.setLastObservation(null);
					break;
				}
				
				GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
				gc.setTime(observation);
				
				if (dstOffset != 0 && tzOffset != 0 && dstStartRule != null && dstEndRule != null
						&& dstStartRule.length() == 8 && dstEndRule.length() == 8) {
					gc = ConvertDate.convertTimeFromUtc(gc, dstStartRule, dstEndRule, dstOffset, tzOffset);
				}
				
				gc.add(java.util.Calendar.SECOND, 60);
				lastDateObservationString = df.format(gc.getTime());
				identifierObservationUP.setLastObservation(lastDateObservationString);
				
				if (lastDateObservation == null) {
					lastDateObservation = observation;
				}
				else {
					
					if (lastDateObservation.after(observation)) {
						lastDateObservation = observation;
						
					}
				}
				
			}
			//lastDateObservation = dbso40.getLastObservationDate(identifierObservation);
			GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
			gc.setTime(lastDateObservation);
			
			if (dstOffset != 0 && tzOffset != 0 && dstStartRule != null && dstEndRule != null
					&& dstStartRule.length() == 8 && dstEndRule.length() == 8) {
				gc = ConvertDate.convertTimeFromUtc(gc, dstStartRule, dstEndRule, dstOffset, tzOffset);
			}
			
			gc.add(java.util.Calendar.SECOND, 60);
			lastDateObservationString = df.format(gc.getTime());
			
		}
		catch (Exception e) {
			lastDateObservation = null;
			lastDateObservationString = null;
		}
		return lastDateObservationString;
		
	}
	
	public String getReadingType(int indexDataCustodian, int idMetertype) throws MalformedURLException,
			ProtocolException, IOException {
		HttpURLConnection connServlet = null;
		String response = "";
		try {
			String readingType = idMetertype + "";
			String urlReadReadingType = ConfigUtils.getGBToCsvConfigValue(Constants.URL_DATA_CUSTODIAN_NAME,
					Constants.DATA_CUSTODIAN, indexDataCustodian) + "ReadingType/" + readingType;
			
			logger.debug("urlReadReadingType:" + urlReadReadingType);
			
			logger.debug("Inizio lettura ReadingType con idReadingType=" + readingType);
			
			URL url = new URL(urlReadReadingType);
			
			connServlet = (HttpURLConnection) url.openConnection();
			connServlet.setRequestMethod("GET");
			connServlet.setDoOutput(true);
			connServlet.setDoInput(true);
			connServlet.setUseCaches(false);
			connServlet.setRequestProperty(
					"Authorization",
					"Bearer "
							+ ConfigUtils.getGBToCsvConfigValue(Constants.AUTHORIZATION_NAME, Constants.DATA_CUSTODIAN,
									indexDataCustodian));
			
			String output = IOUtils.toString(connServlet.getInputStream(), "UTF-8");
			
			output = output.replaceAll("\t", "");
			output = output.replaceAll("\n", "");
			output = output.replaceAll("\r", "");
			output = output.trim();
			response = output;
			logger.debug("Fine lettura ReadingType con idReadingType=" + readingType);
			
			connServlet.disconnect();
			
		}
		finally {
			if (connServlet != null) {
				connServlet.disconnect();
			}
		}
		
		logger.debug(response);
		return response;
	}
	
	public String getLocalTime(int indexUsagePoint) throws MalformedURLException, ProtocolException, IOException {
		HttpURLConnection connServlet = null;
		String response = "";
		try {
			String localTimeParameter = ConfigUtils.getGBToCsvConfigValue(Constants.ID_LOCAL_TIME_NAME,
					Constants.DATA_CUSTODIAN, indexUsagePoint);
			String urlReadLocalTime = ConfigUtils.getGBToCsvConfigValue(Constants.URL_DATA_CUSTODIAN_NAME,
					Constants.DATA_CUSTODIAN, indexUsagePoint) + "LocalTimeParameters/" + localTimeParameter;
			
			logger.debug("urlReadReadingType:" + urlReadLocalTime);
			
			logger.debug("Inizio lettura LocalTime con idLocalTime=" + localTimeParameter);
			
			URL url = new URL(urlReadLocalTime);
			
			connServlet = (HttpURLConnection) url.openConnection();
			connServlet.setRequestMethod("GET");
			connServlet.setDoOutput(true);
			connServlet.setDoInput(true);
			connServlet.setUseCaches(false);
			connServlet.setRequestProperty(
					"Authorization",
					"Bearer "
							+ ConfigUtils.getGBToCsvConfigValue(Constants.AUTHORIZATION_NAME, Constants.DATA_CUSTODIAN,
									indexUsagePoint));
			
			String output = IOUtils.toString(connServlet.getInputStream(), "UTF-8");
			
			output = output.replaceAll("\t", "");
			output = output.replaceAll("\n", "");
			output = output.replaceAll("\r", "");
			output = output.trim();
			response = output;
			logger.debug("Fine lettura LocalTime con idLocalTime=" + localTimeParameter);
			
			connServlet.disconnect();
			
		}
		finally {
			if (connServlet != null) {
				connServlet.disconnect();
			}
		}
		
		logger.debug(response);
		return response;
	}
	
	public String getIntervalBlock(int dataCustodian, String published_min, String usagePoint)
			throws MalformedURLException, ProtocolException, IOException {
		HttpURLConnection connServlet = null;
		String response = "";
		try {
			
			String urlReadIntervalBlock = ConfigUtils.getGBToCsvConfigValue(Constants.URL_DATA_CUSTODIAN_NAME,
					Constants.DATA_CUSTODIAN, dataCustodian)
					//	+ "Batch/RetailCustomer/"
					//		+ ConfigUtils.getGBToCsvConfigValue(Constants.RETAIL_CUSTOMER, Constants.USAGE_POINT_NAME,
					//				indexUsagePoint);
					
					+ "Batch/RetailCustomer/"
					+ ConfigUtils.getGBToCsvConfigValue(Constants.RETAIL_CUSTOMER, Constants.DATA_CUSTODIAN,
							dataCustodian) + "/UsagePoint/" + usagePoint;
			//+ "/MeterReading/"
			//+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_METER_READING_NAME, Constants.USAGE_POINT_NAME,
			//		dataCustodian)
			
			;
			//+ "/MeterReading/"
			//+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_METER_READING_NAME, Constants.DATA_CUSTODIAN,
			//		dataCustodian) + "/IntervalBlock";
			//TODO 
			//urlReadIntervalBlock =  ConfigUtils.getGBToCsvConfigValue(Constants.URL_DATA_CUSTODIAN_NAME,
			//		Constants.DATA_CUSTODIAN, dataCustodian)
			
			//+ "/UsagePoint/"
			//+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_USAGE_POINT_NAME, Constants.USAGE_POINT_NAME,
			//		indexUsagePoint);
			
			//+ "/MeterReading/"
			//+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_METER_READING_NAME, Constants.USAGE_POINT_NAME,
			//		indexUsagePoint) + "/IntervalBlock";
			urlReadIntervalBlock += "?access_token="
					+ ConfigUtils.getGBToCsvConfigValue(Constants.AUTHORIZATION_NAME, Constants.DATA_CUSTODIAN,
							dataCustodian);
			
			if (published_min != null && !published_min.isEmpty()) {
				urlReadIntervalBlock += "&published-min=" + published_min;//+ "&published-max=" + "2015-03-00T0:0:00Z";
				
			}
			else {
				//urlReadIntervalBlock += "?published-max=" + "2015-01-00T0:0:00Z";
			}
			logger.debug("urlReadIntervalBlock:" + urlReadIntervalBlock);
			/*
			 * urlReadIntervalBlock = ConfigUtils.getGBToCsvConfigValue(Constants.URL_DATA_CUSTODIAN_NAME,
			 * Constants.DATA_CUSTODIAN, dataCustodian) +
			 * "Batch/RetailCustomer/1/UsagePoint/3089662?published-min=2015-03-20T06:01:00Z";
			 */
			//System.out.println(urlReadIntervalBlock);
			
			logger.debug("Inizio lettura IntervalBlock con data published_min" + published_min);
			
			URL url = new URL(urlReadIntervalBlock);
			
			connServlet = (HttpURLConnection) url.openConnection();
			
			connServlet.setRequestMethod("GET");
			connServlet.setDoOutput(true);
			connServlet.setDoInput(true);
			connServlet.setUseCaches(false);
			
			//connServlet.setRequestProperty("Authorization", ConfigUtils.getGBToCsvConfigValue(
			//		Constants.AUTHORIZATION_NAME, Constants.DATA_CUSTODIAN, dataCustodian));
			logger.debug("Posso o non posso leggere ? " + connServlet.getResponseCode());
			//System.out.println("Posso o non posso leggere ? " + connServlet.getResponseCode());
			String output = IOUtils.toString(connServlet.getInputStream(), "UTF-8");
			
			output = output.replaceAll("\t", "");
			output = output.replaceAll("\n", "");
			output = output.replaceAll("\r", "");
			output = output.trim();
			
			//System.out.println("---");
			//System.out.println(output);
			//System.out.println("---");
			
			response = output;
			logger.debug("Fine lettura IntervalBlock con data published_min" + published_min);
			
			connServlet.disconnect();
			
		}
		finally {
			if (connServlet != null) {
				connServlet.disconnect();
			}
		}
		
		return response;
	}
	
	public int writeIntervalBlockInCsv(int indexContatore, NodeList intervalBlockList, String nomeFile,
			String currency, Date lastDateObservation, String dstStartRule, String dstEndRule, int dstOffset,
			int tzOffset, int numCsvCreati) throws FileNotFoundException, IOException {
		//= 0;
		FileOutputStream is = null;
		OutputStreamWriter osw = null;
		SimpleDateFormat dfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		DecimalFormat dfvalue = new DecimalFormat("####0.000");
		dfvalue.setDecimalFormatSymbols(dfs);
		DecimalFormat dfcost = new DecimalFormat("####0.00000");
		dfcost.setDecimalFormatSymbols(dfs);
		try {
			
			//scrorro gli interval block
			for (int j = 0; j < intervalBlockList.getLength(); j++) {
				Element intervalBlockEle = (Element) intervalBlockList.item(j);
				
				//recuperiamo la data di inizio dell'interval block
				//se questa data e' >= alla data della ultima observation inserita in sos
				//allora viene scritto il csv per questo interval block
				Date startIntervalDate = null;
				Element eleInterval = (Element) intervalBlockEle.getElementsByTagNameNS(Constants.URI_ESPI, "interval")
						.item(0);
				if (eleInterval != null && eleInterval.getElementsByTagNameNS(Constants.URI_ESPI, "start") != null
						&& eleInterval.getElementsByTagNameNS(Constants.URI_ESPI, "start").item(0) != null) {
					Element startIntervalEle = (Element) eleInterval
							.getElementsByTagNameNS(Constants.URI_ESPI, "start").item(0);
					if (startIntervalEle.getFirstChild() != null
							&& startIntervalEle.getFirstChild().getNodeValue() != null
							&& !startIntervalEle.getFirstChild().getNodeValue().isEmpty()) {
						String startInterval = startIntervalEle.getFirstChild().getNodeValue();
						long startIntervalLong = Long.valueOf(startInterval).longValue();
						
						//converto lo start nel formato unix timestamp
						startIntervalDate = new Date();
						startIntervalDate.setTime(startIntervalLong * 1000);
						GregorianCalendar gcs = (GregorianCalendar) GregorianCalendar.getInstance();
						gcs.setTime(startIntervalDate);
						if (dstOffset != 0 && tzOffset != 0 && dstStartRule.length() == 8 && dstEndRule.length() == 8) {
							gcs = ConvertDate.convertTimeToUtc(gcs, dstStartRule, dstEndRule, dstOffset, tzOffset);
						}
						startIntervalDate.setTime(gcs.getTime().getTime());
					}
				}
				
				//se questa data startIntervalDate e' >= alla data della ultima observation inserita in sos
				//allora viene scritto il csv per questo interval block
				if (lastDateObservation == null
						|| (lastDateObservation != null && startIntervalDate != null && startIntervalDate
								.compareTo(lastDateObservation) >= 0)) {
					NodeList intervalReadingList = intervalBlockEle.getElementsByTagNameNS(Constants.URI_ESPI,
							"IntervalReading");
					if (intervalReadingList != null && intervalReadingList.getLength() > 0) {
						
						//creo il contatore per inserirlo nel nome del file 
						String counter = String.valueOf(numCsvCreati + j + 1);
						while (counter.length() < 8) {
							counter = "0" + counter;
						}
						
						String nomeFileCompleto = nomeFile + "_" + counter + ".csv";
						File csv = new File(ConfigUtils.getGBToCsvConfigValue(Constants.URL_FILE_CSV_NAME,
								Constants.DATA_CUSTODIAN, indexContatore) + nomeFileCompleto);
						is = new FileOutputStream(csv);
						osw = new OutputStreamWriter(is);
						logger.debug("INIZIO SCRITTURA FILE CSV CON NOME:"
								+ ConfigUtils.getGBToCsvConfigValue(Constants.URL_FILE_CSV_NAME,
										Constants.DATA_CUSTODIAN, indexContatore) + nomeFileCompleto);
						/*
						 * System.out.println("INIZIO SCRITTURA FILE CSV CON NOME:" +
						 * ConfigUtils.getGBToCsvConfigValue(Constants.URL_FILE_CSV_NAME, Constants.DATA_CUSTODIAN,
						 * indexContatore) + nomeFileCompleto);
						 */
						for (int k = 0; k < intervalReadingList.getLength(); k++) {
							Element intervalReadingEle = (Element) intervalReadingList.item(k);
							String durationS = "";
							String startS = "";
							String valueS = "";
							String costS = "";
							//leggo la duration in secondi nel tag interval reading
							if (intervalReadingEle.getElementsByTagNameNS(Constants.URI_ESPI, "duration") != null
									&& intervalReadingEle.getElementsByTagNameNS(Constants.URI_ESPI, "duration")
											.item(0) != null) {
								Element durationEle = (Element) intervalReadingEle.getElementsByTagNameNS(
										Constants.URI_ESPI, "duration").item(0);
								if (durationEle.getFirstChild() != null
										&& durationEle.getFirstChild().getNodeValue() != null
										&& !durationEle.getFirstChild().getNodeValue().isEmpty()) {
									durationS = durationEle.getFirstChild().getNodeValue();
								}
							}
							//leggo lo start in timestamp nel tag interval reading
							if (intervalReadingEle.getElementsByTagNameNS(Constants.URI_ESPI, "start") != null
									&& intervalReadingEle.getElementsByTagNameNS(Constants.URI_ESPI, "start").item(0) != null) {
								Element startEle = (Element) intervalReadingEle.getElementsByTagNameNS(
										Constants.URI_ESPI, "start").item(0);
								if (startEle.getFirstChild() != null && startEle.getFirstChild().getNodeValue() != null
										&& !startEle.getFirstChild().getNodeValue().isEmpty()) {
									startS = startEle.getFirstChild().getNodeValue();
								}
							}
							//leggo il value scritto senza punti decimali nel tag interval reading
							if (intervalReadingEle.getElementsByTagNameNS(Constants.URI_ESPI, "value") != null
									&& intervalReadingEle.getElementsByTagNameNS(Constants.URI_ESPI, "value").item(0) != null) {
								Element valueEle = (Element) intervalReadingEle.getElementsByTagNameNS(
										Constants.URI_ESPI, "value").item(0);
								if (valueEle.getFirstChild() != null && valueEle.getFirstChild().getNodeValue() != null
										&& !valueEle.getFirstChild().getNodeValue().isEmpty()) {
									valueS = valueEle.getFirstChild().getNodeValue();
								}
							}
							////leggo il costo nel tag interval reading
							if (intervalReadingEle.getElementsByTagNameNS(Constants.URI_ESPI, "cost") != null
									&& intervalReadingEle.getElementsByTagNameNS(Constants.URI_ESPI, "cost").item(0) != null) {
								Element costEle = (Element) intervalReadingEle.getElementsByTagNameNS(
										Constants.URI_ESPI, "cost").item(0);
								if (costEle.getFirstChild() != null && costEle.getFirstChild().getNodeValue() != null
										&& !costEle.getFirstChild().getNodeValue().isEmpty()) {
									costS = costEle.getFirstChild().getNodeValue();
								}
							}
							int duration = Long.valueOf(durationS).intValue();
							
							long start = Long.valueOf(startS).longValue();
							double valueDouble = Double.valueOf(valueS).doubleValue();
							valueDouble = valueDouble / 1000;
							
							//converto lo start nel formato unix timestamp
							Date startDate = new Date();
							startDate.setTime(start * 1000);
							
							//aggiungo allo start di greenbutton la durata perchè nel csv visualizziamo la data di fine e non la data di inizio
							GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
							gc.setTime(startDate);
							gc.add(java.util.Calendar.SECOND, duration);
							
							if (dstOffset != 0 && tzOffset != 0 && dstStartRule.length() == 8
									&& dstEndRule.length() == 8) {
								gc = ConvertDate.convertTimeToUtc(gc, dstStartRule, dstEndRule, dstOffset, tzOffset);
							}
							
							//scriviamo la riga del csv
							String row = dfDateTime.format(gc.getTime()) + ";" + dfvalue.format(valueDouble);
							
							//se esiste l'unita di misura del costo (quindi e' diversa da zero) ed esiste anche il costo 
							//aggiungiamo il costo nel csv
							if (currency != null && !currency.equals("0") && costS != null && !costS.isEmpty()) {
								double costDouble = Double.valueOf(costS).doubleValue();
								costDouble = costDouble / 100000;
								row = row + ";" + dfcost.format(costDouble);
							}
							if (k != intervalReadingList.getLength() - 1) {
								row += "\r\n";
							}
							logger.debug("row:" + row);
							osw.write(row);
						}
						osw.close();
						is.close();
						logger.debug("FINE SCRITTURA FILE CSV CON NOME:"
								+ ConfigUtils.getGBToCsvConfigValue(Constants.URL_FILE_CSV_NAME,
										Constants.DATA_CUSTODIAN, indexContatore) + nomeFileCompleto);
						numCsvCreati++;
					}
				}
			}
		}
		finally {
			if (is != null) {
				try {
					is.close();
				}
				catch (IOException e) {
					e.printStackTrace();
					logger.error("Eccezione:", e);
				}
			}
			if (osw != null) {
				try {
					osw.close();
				}
				catch (IOException e) {
					e.printStackTrace();
					logger.error("Eccezione:", e);
				}
			}
		}
		return numCsvCreati;
	}
	
}
