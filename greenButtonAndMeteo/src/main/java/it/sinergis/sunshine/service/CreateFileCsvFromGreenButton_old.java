package it.sinergis.sunshine.service;

import it.sinergis.sunshine.database.DatabaseSos40;
import it.sinergis.sunshine.servlet.CodaProcessiServlet;
import it.sinergis.sunshine.util.ConfigUtils;
import it.sinergis.sunshine.util.ConfigValues;
import it.sinergis.sunshine.util.Constants;
import it.sinergis.sunshine.util.ConvertDate;
import it.sinergis.sunshine.util.CreateFileCsvFromGBException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.xml.bind.StringInputStream;

/**
 * Classe che si occupa di gestire la scrittura dei file csv a partire dei dati contenuti nel db di green button
 * 
 * @author Rossana Bambili
 */
public class CreateFileCsvFromGreenButton_old {
	
	/** Logger. */
	private static Logger logger;
	
	/**
	 * Variabile di istanza.
	 */
	private static CreateFileCsvFromGreenButton createFileCsvFromGreenButton;
	
	/**
	 * Costruttore privato, Singleton.
	 */
	private CreateFileCsvFromGreenButton_old() {
		logger = Logger.getLogger(this.getClass());
	}
	
	/**
	 * Metodo di accesso alla classe.
	 * 
	 * @return
	 */
	public static CreateFileCsvFromGreenButton getInstance() {
		if (createFileCsvFromGreenButton == null) {
			//	createFileCsvFromGreenButton = new CreateFileCsvFromGreenButton();
		}
		
		return createFileCsvFromGreenButton;
	}
	
	public synchronized String createFileCsv() {
		
		String response = "<CreateFileCsvFromGreenButtonResponse>";
		try {
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			long countUsagePoint = Long.valueOf(ConfigValues.COUNT_USAGE_POINTS);
			//scorriamo i contatori dal file di configurazione usagePointConfiguration
			for (int i = 1; i <= countUsagePoint; i++) {
				try {
					int numCsvCreati = 0;
					logger.debug("INIZIO CREAZIONE CSV PER LO USAGE POINT CON ID:"
							+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_USAGE_POINT_NAME,
									Constants.USAGE_POINT_NAME, i));
					
					response += "<usagePoint><idUsagePoint>"
							+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_USAGE_POINT_NAME,
									Constants.USAGE_POINT_NAME, i) + "</idUsagePoint>";
					//leggiamo i dati dello usagepoint che ha id uguale al valore della variabile idUsagePoint_i letto dal
					//file di configurazione usagePointConfiguration.properties
					
					ArrayList<Document> docs = CodaProcessiServlet.usagePointCache.get(Integer.valueOf(i));
					Document docUsagePoint = null;
					Document docReadingType = null;
					Document docLocalTime = null;
					Document docIntervalBlock = null;
					if (docs == null || docs.size() != 3) {
						String ib = getUsagePoint(i);
						InputStream ibIs = new StringInputStream(ib);
						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						dbf.setNamespaceAware(true);
						DocumentBuilder db = dbf.newDocumentBuilder();
						docUsagePoint = db.parse(ibIs);
						
						ib = getReadingType(i);
						ibIs = new StringInputStream(ib);
						dbf = DocumentBuilderFactory.newInstance();
						dbf.setNamespaceAware(true);
						db = dbf.newDocumentBuilder();
						docReadingType = db.parse(ibIs);
						
						ib = getLocalTime(i);
						ibIs = new StringInputStream(ib);
						dbf = DocumentBuilderFactory.newInstance();
						dbf.setNamespaceAware(true);
						db = dbf.newDocumentBuilder();
						docLocalTime = db.parse(ibIs);
						
						docs = new ArrayList<Document>();
						docs.add(docUsagePoint);
						docs.add(docReadingType);
						docs.add(docLocalTime);
						
						CodaProcessiServlet.usagePointCache.put(Integer.valueOf(i), docs);
					}
					else {
						docUsagePoint = docs.get(0);
						docReadingType = docs.get(1);
						docLocalTime = docs.get(2);
					}
					
					if (docUsagePoint == null || docReadingType == null || docLocalTime == null) {
						throw new CreateFileCsvFromGBException("Errore nella lettura dei dati dello usage point.");
					}
					
					//compongo il nome del file csv in questa maniera: Example:	FER-001_GASR_MCU_1h_00000001.csv  <meterID>_<type><abs/rel>_<unit>_<freq>_<counter>.csv
					//Mapping:
					//<meterID>	usage_point.description	
					//<type>	Commodity 
					//<abs/rel>	AccumulationBehaviour
					//<unit>	Uom
					//<freq>    TimeAttribute  
					//<counter>	00000001 		        
					String usage_description = null;
					//cerco la descrizione dello usage point che sarebbe il title della entry dello usage point
					if (docUsagePoint.getElementsByTagNameNS(Constants.URI_ESPI, "UsagePoint") != null
							&& docUsagePoint.getElementsByTagNameNS(Constants.URI_ESPI, "UsagePoint").item(0) != null) {
						Element eleUsagePoint = (Element) docUsagePoint.getElementsByTagNameNS(Constants.URI_ESPI,
								"UsagePoint").item(0);
						if (eleUsagePoint.getParentNode() != null
								&& eleUsagePoint.getParentNode().getParentNode() != null) {
							Element entryUsagePoint = (Element) eleUsagePoint.getParentNode().getParentNode();
							if (entryUsagePoint.getElementsByTagNameNS(Constants.URI_ATOM, "title") != null
									&& entryUsagePoint.getElementsByTagNameNS(Constants.URI_ATOM, "title").item(0) != null) {
								Element titleUsagePointEle = (Element) entryUsagePoint.getElementsByTagNameNS(
										Constants.URI_ATOM, "title").item(0);
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
					logger.debug("usage_description:" + usage_description);
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
						Element eleReadingType = (Element) docReadingType.getElementsByTagNameNS(Constants.URI_ESPI,
								"ReadingType").item(0);
						if (eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "commodity") != null
								&& eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "commodity").item(0) != null) {
							Element commodityEle = (Element) eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI,
									"commodity").item(0);
							if (commodityEle.getFirstChild() != null
									&& commodityEle.getFirstChild().getNodeValue() != null
									&& !commodityEle.getFirstChild().getNodeValue().isEmpty()) {
								commodityespi = commodityEle.getFirstChild().getNodeValue();
							}
						}
						if (eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "accumulationBehaviour") != null
								&& eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "accumulationBehaviour")
										.item(0) != null) {
							Element accumulationbehaviourEle = (Element) eleReadingType.getElementsByTagNameNS(
									Constants.URI_ESPI, "accumulationBehaviour").item(0);
							if (accumulationbehaviourEle.getFirstChild() != null
									&& accumulationbehaviourEle.getFirstChild().getNodeValue() != null
									&& !accumulationbehaviourEle.getFirstChild().getNodeValue().isEmpty()) {
								accumulationbehaviourespi = accumulationbehaviourEle.getFirstChild().getNodeValue();
							}
						}
						if (eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "uom") != null
								&& eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "uom").item(0) != null) {
							Element uomEle = (Element) eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "uom")
									.item(0);
							if (uomEle.getFirstChild() != null && uomEle.getFirstChild().getNodeValue() != null
									&& !uomEle.getFirstChild().getNodeValue().isEmpty()) {
								uomespi = uomEle.getFirstChild().getNodeValue();
							}
						}
						if (eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "timeAttribute") != null
								&& eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "timeAttribute").item(0) != null) {
							Element timeattributeEle = (Element) eleReadingType.getElementsByTagNameNS(
									Constants.URI_ESPI, "timeAttribute").item(0);
							if (timeattributeEle.getFirstChild() != null
									&& timeattributeEle.getFirstChild().getNodeValue() != null
									&& !timeattributeEle.getFirstChild().getNodeValue().isEmpty()) {
								timeattributeespi = timeattributeEle.getFirstChild().getNodeValue();
							}
						}
						if (eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "currency") != null
								&& eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI, "currency").item(0) != null) {
							Element currencyEle = (Element) eleReadingType.getElementsByTagNameNS(Constants.URI_ESPI,
									"currency").item(0);
							if (currencyEle.getFirstChild() != null
									&& currencyEle.getFirstChild().getNodeValue() != null
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
					accumulationbehaviour = ConfigUtils.getGBToCsvConfigValueWithAttribute(
							Constants.ACCUMULATION_BEHAVIOUR_NAME, Constants.ACCUMULATION_BEHAVIOUR_ATTRIBUTE_ID_NAME,
							accumulationbehaviourespi);
					logger.debug("accumulationbehaviour:" + accumulationbehaviour);
					
					if (uomespi == null) {
						throw new CreateFileCsvFromGBException("Manca il codice uom del ReadingType.");
					}
					//cerco nel file properties mappingGreenButtonToCsv il valore della variabile uom_<uomespi>  
					uom = ConfigUtils.getGBToCsvConfigValueWithAttribute(Constants.UOM_NAME,
							Constants.UOM_ATTRIBUTE_ID_NAME, uomespi);
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
								&& eleLocalTimeParameters.getElementsByTagNameNS(Constants.URI_ESPI, "dstEndRule")
										.item(0) != null) {
							Element dstEndRuleEle = (Element) eleLocalTimeParameters.getElementsByTagNameNS(
									Constants.URI_ESPI, "dstEndRule").item(0);
							if (dstEndRuleEle.getFirstChild() != null
									&& dstEndRuleEle.getFirstChild().getNodeValue() != null
									&& !dstEndRuleEle.getFirstChild().getNodeValue().isEmpty()) {
								dstEndRule = dstEndRuleEle.getFirstChild().getNodeValue();
							}
						}
						if (eleLocalTimeParameters.getElementsByTagNameNS(Constants.URI_ESPI, "dstStartRule") != null
								&& eleLocalTimeParameters.getElementsByTagNameNS(Constants.URI_ESPI, "dstStartRule")
										.item(0) != null) {
							Element dstStartRuleEle = (Element) eleLocalTimeParameters.getElementsByTagNameNS(
									Constants.URI_ESPI, "dstStartRule").item(0);
							if (dstStartRuleEle.getFirstChild() != null
									&& dstStartRuleEle.getFirstChild().getNodeValue() != null
									&& !dstStartRuleEle.getFirstChild().getNodeValue().isEmpty()) {
								dstStartRule = dstStartRuleEle.getFirstChild().getNodeValue();
							}
						}
						if (eleLocalTimeParameters.getElementsByTagNameNS(Constants.URI_ESPI, "dstOffset") != null
								&& eleLocalTimeParameters.getElementsByTagNameNS(Constants.URI_ESPI, "dstOffset").item(
										0) != null) {
							Element dstOffsetEle = (Element) eleLocalTimeParameters.getElementsByTagNameNS(
									Constants.URI_ESPI, "dstOffset").item(0);
							if (dstOffsetEle.getFirstChild() != null
									&& dstOffsetEle.getFirstChild().getNodeValue() != null
									&& !dstOffsetEle.getFirstChild().getNodeValue().isEmpty()) {
								dstOffsetespi = dstOffsetEle.getFirstChild().getNodeValue();
							}
						}
						if (eleLocalTimeParameters.getElementsByTagNameNS(Constants.URI_ESPI, "tzOffset") != null
								&& eleLocalTimeParameters.getElementsByTagNameNS(Constants.URI_ESPI, "tzOffset")
										.item(0) != null) {
							Element tzOffsetEle = (Element) eleLocalTimeParameters.getElementsByTagNameNS(
									Constants.URI_ESPI, "tzOffset").item(0);
							if (tzOffsetEle.getFirstChild() != null
									&& tzOffsetEle.getFirstChild().getNodeValue() != null
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
					
					//invochiamo la getobservationbyid per recuperare l'ultima observation inserita
					//in modo che da greenbutton leggiamo gli interval block che hanno la data start maggiore o uguale
					//alla data di questa ultima observation
					String identifierObservation;
					if (commodity.length() <= 3) {
						identifierObservation = ConfigUtils.getGBToCsvConfigValue(
								Constants.IDENTIFIER_OBSERVATION_NAME, Constants.USAGE_POINT_NAME, i)
								+ usage_description + "_" + commodity + accumulationbehaviour + "_" + uom;
					}
					else {
						identifierObservation = ConfigUtils.getGBToCsvConfigValue(
								Constants.IDENTIFIER_OBSERVATION_NAME, Constants.USAGE_POINT_NAME, i)
								+ usage_description + "_" + commodity + "_" + uom;
					}
					
					String identifier_last = identifierObservation = ConfigUtils.getGBToCsvConfigValue(
							Constants.IDENTIFIER_OBSERVATION_NAME, Constants.USAGE_POINT_NAME, i)
							+ usage_description
							+ "_" + commodity;
					
					//versione alternativa get last observation		             
					String lastDateObservationString = null;
					DatabaseSos40 dbso40 = new DatabaseSos40();
					Date lastDateObservation;
					
					try {
						lastDateObservation = dbso40.getLastObservationDate(identifierObservation);
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
					
					/*
					 * if (currency!=null && !currency.equals("0")) { identifierObservation += "_" + "LRC"; } else {
					 * identifierObservation += "_" + "LRM"; } Date lastDateObservation = null; String
					 * lastDateObservationString = null; try { lastDateObservation =
					 * this.getLastObservation(identifierObservation,i); GregorianCalendar gc = (GregorianCalendar)
					 * GregorianCalendar.getInstance(); gc.setTime(lastDateObservation); if (dstOffset!=0 && tzOffset!=0
					 * && dstStartRule!=null && dstEndRule!=null && dstStartRule.length()==8 && dstEndRule.length()==8)
					 * { gc = ConvertDate.convertTimeFromUtc(gc,dstStartRule,dstEndRule,dstOffset,tzOffset); }
					 * gc.add(java.util.Calendar.SECOND,60); lastDateObservationString = df.format(gc.getTime()); }
					 * catch (Exception e) { lastDateObservation = null; lastDateObservationString = null; }
					 */
					
					String ib = getIntervalBlock(i, lastDateObservationString);
					InputStream ibIs = new StringInputStream(ib);
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					dbf.setNamespaceAware(true);
					DocumentBuilder db = dbf.newDocumentBuilder();
					docIntervalBlock = db.parse(ibIs);
					
					//scrorro gli intervalbock dello usage point e per ognuno di questi creo un csv
					if (docIntervalBlock != null
							&& docIntervalBlock.getElementsByTagNameNS(Constants.URI_ESPI, "IntervalBlock") != null
							&& docIntervalBlock.getElementsByTagNameNS(Constants.URI_ESPI, "IntervalBlock").getLength() > 0) {
						NodeList intervalBlockList = docIntervalBlock.getElementsByTagNameNS(Constants.URI_ESPI,
								"IntervalBlock");
						
						String nomeFile = usage_description + "_" + commodity + accumulationbehaviour + "_" + uom + "_"
								+ timeattribute;
						
						//scrive in un file csv con nome nomeFile gli intervalreading contenuti in ciascuno interval block 
						numCsvCreati = this.writeIntervalBlockInCsv(i, intervalBlockList, nomeFile, currency,
								lastDateObservation, dstStartRule, dstEndRule, dstOffset, tzOffset);
					}
					if (numCsvCreati == 0) {
						throw new CreateFileCsvFromGBException("Nessun file csv creato per il corrente usagepoint.");
					}
					
					response += "<pathDirectoryCsv>"
							+ ConfigUtils.getGBToCsvConfigValue(Constants.URL_FILE_CSV_NAME,
									Constants.USAGE_POINT_NAME, i) + "</pathDirectoryCsv></usagePoint>";
					logger.debug("FINE CREAZIONE CSV PER LO USAGE POINT CON ID:"
							+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_USAGE_POINT_NAME,
									Constants.USAGE_POINT_NAME, i));
				}
				catch (SAXException e) {
					e.printStackTrace();
					logger.error("Eccezione:", e);
					response += "<error>" + e.getLocalizedMessage() + "</error></usagePoint>";
				}
				catch (ParserConfigurationException e) {
					e.printStackTrace();
					logger.error("Eccezione:", e);
					response += "<error>" + e.getLocalizedMessage() + "</error></usagePoint>";
				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
					logger.error("Eccezione:", e);
					response += "<error>" + e.getLocalizedMessage() + "</error></usagePoint>";
				}
				catch (MalformedURLException e) {
					e.printStackTrace();
					logger.error("Eccezione:", e);
					response += "<error>" + e.getLocalizedMessage() + "</error></usagePoint>";
				}
				catch (ProtocolException e) {
					e.printStackTrace();
					logger.error("Eccezione:", e);
					response += "<error>" + e.getLocalizedMessage() + "</error></usagePoint>";
				}
				catch (IOException e) {
					e.printStackTrace();
					logger.error("Eccezione:", e);
					response += "<error>" + e.getLocalizedMessage() + "</error></usagePoint>";
				}
				catch (CreateFileCsvFromGBException e) {
					e.printStackTrace();
					logger.error("Eccezione:", e);
					response += "<error>" + e.getLocalizedMessage() + "</error></usagePoint>";
				}
				catch (Exception e) {
					e.printStackTrace();
					logger.error("Eccezione:", e);
					response += "<error>" + e.getLocalizedMessage() + "</error></usagePoint>";
				}
				
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Eccezione:", e);
			response += "<error>" + e.getLocalizedMessage() + "</error>";
		}
		response += "</CreateFileCsvFromGreenButtonResponse>";
		
		logger.debug(response);
		
		return response;
		
	}
	
	public int writeIntervalBlockInCsv(int indexContatore, NodeList intervalBlockList, String nomeFile,
			String currency, Date lastDateObservation, String dstStartRule, String dstEndRule, int dstOffset,
			int tzOffset) throws FileNotFoundException, IOException {
		int numCsvCreati = 0;
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
						String counter = String.valueOf(j + 1);
						while (counter.length() < 8) {
							counter = "0" + counter;
						}
						
						String nomeFileCompleto = nomeFile + "_" + counter + ".csv";
						File csv = new File(ConfigUtils.getGBToCsvConfigValue(Constants.URL_FILE_CSV_NAME,
								Constants.USAGE_POINT_NAME, indexContatore) + nomeFileCompleto);
						is = new FileOutputStream(csv);
						osw = new OutputStreamWriter(is);
						logger.debug("INIZIO SCRITTURA FILE CSV CON NOME:"
								+ ConfigUtils.getGBToCsvConfigValue(Constants.URL_FILE_CSV_NAME,
										Constants.USAGE_POINT_NAME, indexContatore) + nomeFileCompleto);
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
										Constants.USAGE_POINT_NAME, indexContatore) + nomeFileCompleto);
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
	
	public String getUsagePoint(int indexUsagePoint) throws MalformedURLException, ProtocolException, IOException {
		HttpURLConnection connServlet = null;
		String response = "";
		try {
			
			String urlReadUsagePoint = ConfigUtils.getGBToCsvConfigValue(Constants.URL_DATA_CUSTODIAN_NAME,
					Constants.USAGE_POINT_NAME, indexUsagePoint)
					+ "Subscription/"
					+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_SUBSCRIPTION_NAME, Constants.USAGE_POINT_NAME,
							indexUsagePoint)
					+ "/UsagePoint/"
					+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_USAGE_POINT_NAME, Constants.USAGE_POINT_NAME,
							indexUsagePoint);
			
			logger.debug("urlReadUsagePoint:" + urlReadUsagePoint);
			
			logger.debug("Inizio lettura UsagePoint con idUsagePoint="
					+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_USAGE_POINT_NAME, Constants.USAGE_POINT_NAME,
							indexUsagePoint));
			
			URL url = new URL(urlReadUsagePoint);
			
			connServlet = (HttpURLConnection) url.openConnection();
			connServlet.setRequestMethod("GET");
			connServlet.setDoOutput(true);
			connServlet.setDoInput(true);
			connServlet.setUseCaches(false);
			connServlet.setRequestProperty("Authorization", ConfigUtils.getGBToCsvConfigValue(
					Constants.AUTHORIZATION_NAME, Constants.USAGE_POINT_NAME, indexUsagePoint));
			
			String output = IOUtils.toString(connServlet.getInputStream(), "UTF-8");
			
			output = output.replaceAll("\t", "");
			output = output.replaceAll("\n", "");
			output = output.replaceAll("\r", "");
			output = output.trim();
			
			response = output;
			logger.debug("Fine lettura UsagePoint con idUsagePoint="
					+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_USAGE_POINT_NAME, Constants.USAGE_POINT_NAME,
							indexUsagePoint));
			
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
	
	public String getReadingType(int indexUsagePoint) throws MalformedURLException, ProtocolException, IOException {
		HttpURLConnection connServlet = null;
		String response = "";
		try {
			
			String urlReadReadingType = ConfigUtils.getGBToCsvConfigValue(Constants.URL_DATA_CUSTODIAN_NAME,
					Constants.USAGE_POINT_NAME, indexUsagePoint)
					+ "ReadingType/"
					+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_READING_TYPE_NAME, Constants.USAGE_POINT_NAME,
							indexUsagePoint);
			
			logger.debug("urlReadReadingType:" + urlReadReadingType);
			
			logger.debug("Inizio lettura ReadingType con idReadingType="
					+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_READING_TYPE_NAME, Constants.USAGE_POINT_NAME,
							indexUsagePoint));
			
			URL url = new URL(urlReadReadingType);
			
			connServlet = (HttpURLConnection) url.openConnection();
			connServlet.setRequestMethod("GET");
			connServlet.setDoOutput(true);
			connServlet.setDoInput(true);
			connServlet.setUseCaches(false);
			connServlet.setRequestProperty("Authorization", ConfigUtils.getGBToCsvConfigValue(
					Constants.AUTHORIZATION_NAME, Constants.USAGE_POINT_NAME, indexUsagePoint));
			
			String output = IOUtils.toString(connServlet.getInputStream(), "UTF-8");
			
			output = output.replaceAll("\t", "");
			output = output.replaceAll("\n", "");
			output = output.replaceAll("\r", "");
			output = output.trim();
			response = output;
			logger.debug("Fine lettura ReadingType con idReadingType="
					+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_READING_TYPE_NAME, Constants.USAGE_POINT_NAME,
							indexUsagePoint));
			
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
			
			String urlReadLocalTime = ConfigUtils.getGBToCsvConfigValue(Constants.URL_DATA_CUSTODIAN_NAME,
					Constants.USAGE_POINT_NAME, indexUsagePoint)
					+ "LocalTimeParameters/"
					+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_LOCAL_TIME_NAME, Constants.USAGE_POINT_NAME,
							indexUsagePoint);
			
			logger.debug("urlReadReadingType:" + urlReadLocalTime);
			
			logger.debug("Inizio lettura LocalTime con idLocalTime="
					+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_LOCAL_TIME_NAME, Constants.USAGE_POINT_NAME,
							indexUsagePoint));
			
			URL url = new URL(urlReadLocalTime);
			
			connServlet = (HttpURLConnection) url.openConnection();
			connServlet.setRequestMethod("GET");
			connServlet.setDoOutput(true);
			connServlet.setDoInput(true);
			connServlet.setUseCaches(false);
			connServlet.setRequestProperty("Authorization", ConfigUtils.getGBToCsvConfigValue(
					Constants.AUTHORIZATION_NAME, Constants.USAGE_POINT_NAME, indexUsagePoint));
			
			String output = IOUtils.toString(connServlet.getInputStream(), "UTF-8");
			
			output = output.replaceAll("\t", "");
			output = output.replaceAll("\n", "");
			output = output.replaceAll("\r", "");
			output = output.trim();
			response = output;
			logger.debug("Fine lettura LocalTime con idLocalTime="
					+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_LOCAL_TIME_NAME, Constants.USAGE_POINT_NAME,
							indexUsagePoint));
			
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
	
	public String getIntervalBlock(int indexUsagePoint, String published_min) throws MalformedURLException,
			ProtocolException, IOException {
		HttpURLConnection connServlet = null;
		String response = "";
		try {
			
			String urlReadIntervalBlock = ConfigUtils.getGBToCsvConfigValue(Constants.URL_DATA_CUSTODIAN_NAME,
					Constants.USAGE_POINT_NAME, indexUsagePoint)
					//	+ "Batch/RetailCustomer/"
					//		+ ConfigUtils.getGBToCsvConfigValue(Constants.RETAIL_CUSTOMER, Constants.USAGE_POINT_NAME,
					//				indexUsagePoint);
					
					+ "Batch/Subscription/"
					+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_SUBSCRIPTION_NAME, Constants.USAGE_POINT_NAME,
							indexUsagePoint);
			//+ "/UsagePoint/"
			//+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_USAGE_POINT_NAME, Constants.USAGE_POINT_NAME,
			//		indexUsagePoint);
			
			//+ "/MeterReading/"
			//+ ConfigUtils.getGBToCsvConfigValue(Constants.ID_METER_READING_NAME, Constants.USAGE_POINT_NAME,
			//		indexUsagePoint) + "/IntervalBlock";
			
			if (published_min != null && !published_min.isEmpty()) {
				urlReadIntervalBlock += "?published-min=" + published_min;
			}
			logger.debug("urlReadIntervalBlock:" + urlReadIntervalBlock);
			
			logger.debug("Inizio lettura IntervalBlock con data published_min" + published_min);
			
			URL url = new URL(urlReadIntervalBlock);
			
			connServlet = (HttpURLConnection) url.openConnection();
			
			connServlet.setRequestMethod("GET");
			connServlet.setDoOutput(true);
			connServlet.setDoInput(true);
			connServlet.setUseCaches(false);
			
			connServlet.setRequestProperty("Authorization", ConfigUtils.getGBToCsvConfigValue(
					Constants.AUTHORIZATION_NAME, Constants.USAGE_POINT_NAME, indexUsagePoint));
			logger.debug("Posso o non posso leggere ? " + connServlet.getResponseCode());
			String output = IOUtils.toString(connServlet.getInputStream(), "UTF-8");
			
			output = output.replaceAll("\t", "");
			output = output.replaceAll("\n", "");
			output = output.replaceAll("\r", "");
			output = output.trim();
			
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
	
	public Date getLastObservation(String identifierObservation, int indexUsagePoint) throws MalformedURLException,
			ProtocolException, IOException, ParserConfigurationException, SAXException, ParseException {
		BufferedReader in = null;
		HttpURLConnection connServlet = null;
		OutputStreamWriter writer = null;
		SimpleDateFormat dfDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date lastDate = null;
		try {
			
			//leggiamo l'ultima observation inserita
			String request = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\">" + "<env:Body>"
					+ "<sos:GetObservationById service=\"SOS\" version=\"2.0.0\" "
					+ "xmlns:sos=\"http://www.opengis.net/sos/2.0\">" + "<sos:observation>" + identifierObservation
					+ "</sos:observation>" + "</sos:GetObservationById>" + "</env:Body>" + "</env:Envelope>";
			
			logger.debug("urlSOS:"
					+ ConfigUtils.getGBToCsvConfigValue(Constants.URL_SOS_NAME, Constants.USAGE_POINT_NAME,
							indexUsagePoint));
			logger.debug("GetObservationById:" + request);
			
			logger.debug("Inizio lettura ultima observation=" + identifierObservation);
			
			URL url = new URL(ConfigUtils.getGBToCsvConfigValue(Constants.URL_SOS_NAME, Constants.USAGE_POINT_NAME,
					indexUsagePoint));
			
			connServlet = (HttpURLConnection) url.openConnection();
			connServlet.setRequestMethod("POST");
			connServlet.setDoOutput(true);
			connServlet.setDoInput(true);
			connServlet.setFollowRedirects(true);
			connServlet.setUseCaches(false);
			// content type
			connServlet.setRequestProperty("Content-Type", "application/soap+xml");
			
			writer = new OutputStreamWriter(connServlet.getOutputStream());
			
			writer.write(request);
			writer.flush();
			writer.close();
			
			in = new BufferedReader(new InputStreamReader(connServlet.getInputStream()));
			String output;
			String line = "";
			
			for (output = ""; (line = in.readLine()) != null; output = output + line.toString().trim()) {
				;
			}
			
			output = output.replaceAll("\t", "");
			output = output.replaceAll("\n", "");
			output = output.replaceAll("\r", "");
			output = output.trim();
			
			logger.debug("RISPOSTA GetObservationById:" + output);
			logger.debug("Fine lettura ultima observation=" + identifierObservation);
			
			InputStream ibIs = new StringInputStream(output);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			Document docObservation = db.parse(ibIs);
			
			if (docObservation != null) {
				
				if (docObservation.getElementsByTagNameNS(Constants.URI_GML, "timePosition") != null
						&& docObservation.getElementsByTagNameNS(Constants.URI_GML, "timePosition").item(0) != null) {
					Node tp = docObservation.getElementsByTagNameNS(Constants.URI_GML, "timePosition").item(0);
					if (tp.getFirstChild() != null && tp.getFirstChild().getNodeValue() != null
							&& !tp.getFirstChild().getNodeValue().isEmpty()) {
						lastDate = dfDateTime.parse((tp.getFirstChild().getNodeValue().substring(0, 19)) + "Z");
						logger.debug(tp.getFirstChild().getNodeValue());
					}
				}
			}
			
			in.close();
			connServlet.disconnect();
			
		}
		finally {
			if (connServlet != null) {
				connServlet.disconnect();
			}
			if (in != null) {
				in.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
		
		return lastDate;
	}
}
