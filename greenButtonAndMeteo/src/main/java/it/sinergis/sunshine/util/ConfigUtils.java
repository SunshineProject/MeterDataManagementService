package it.sinergis.sunshine.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.MissingResourceException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Classe che legge le impostazioni da file.
 * 
 * @author Rossana Bambili
 */
public final class ConfigUtils {
	
	/** Logger. */
	private static Logger logger = Logger.getLogger(ConfigUtils.class);
	
	/** Document con le impostazioni di configurazione dello usage point. */
	private static Document greenButtonToCsvDoc;
	
	/** Document con le impostazioni dei dati meteo da inserire nel db sos. */
	private static Document mdDoc;
	
	/**
	 * Costruttore di default.
	 */
	private ConfigUtils() {
	}
	
	/**
	 * Metodo che restituisce le configurazioni dello usage point
	 * 
	 * @param key
	 *            nome
	 * @return valore
	 * @throws MissingResourceException
	 *             eccezione sollevata in caso che la proprieta' richiesta non sia presente nel file di configurazione *
	 */
	/*
	 * public static String getGreenButtonToCsvConfigValue(String key) throws MissingResourceException { if
	 * (greenButtonToCsvRb == null) { try { greenButtonToCsvRb = ResourceBundle
	 * .getBundle(Constants.GREEN_BUTTON_TO_CSV_CONFIG_FILE_NAME); } catch (NullPointerException npe) {
	 * logger.error("File " + Constants.GREEN_BUTTON_TO_CSV_CONFIG_FILE_NAME + ".properties non trovato");
	 * npe.printStackTrace(); } catch (MissingResourceException mre) { logger.error("File " +
	 * Constants.GREEN_BUTTON_TO_CSV_CONFIG_FILE_NAME + ".properties non trovato"); mre.printStackTrace(); } } return
	 * greenButtonToCsvRb.getString(key); }
	 */
	/**
	 * Metodo che restituisce le configurazioni per l'importazione dei dati meteo
	 * 
	 * @param key
	 *            nome
	 * @return valore
	 * @throws MissingResourceException
	 *             eccezione sollevata in caso che la proprieta' richiesta non sia presente nel file di configurazione *
	 */
	/*
	 * public static String getImportMeteoDataConfigValue(String key) throws MissingResourceException { if (mdRb ==
	 * null) { try { mdRb = ResourceBundle .getBundle(Constants.METEO_DATA_CONFIG_FILE_NAME); } catch
	 * (NullPointerException npe) { logger.error("File " + Constants.METEO_DATA_CONFIG_FILE_NAME +
	 * ".properties non trovato"); npe.printStackTrace(); } catch (MissingResourceException mre) { logger.error("File "
	 * + Constants.METEO_DATA_CONFIG_FILE_NAME + ".properties non trovato"); mre.printStackTrace(); } } return
	 * mdRb.getString(key); }
	 */
	
	/**
	 * Metodo che restituisce le configurazioni per l'importazione dei dati meteo
	 * 
	 * @param key
	 *            nome
	 * @return valore
	 * @throws MissingResourceException
	 *             eccezione sollevata in caso che la proprieta' richiesta non sia presente nel file di configurazione *
	 */
	//mi torna il valore del tag indentificato univocametne da key
	public static String getImportMeteoDataConfigValue(String key) throws MissingResourceException {
		
		String configvalue = null;
		try {
			mdDoc = setDocument(mdDoc, Constants.METEO_DATA_CONFIG_FILE_NAME);
			
			if (mdDoc.getElementsByTagName(key) != null && mdDoc.getElementsByTagName(key).item(0) != null
					&& mdDoc.getElementsByTagName(key).item(0).getFirstChild() != null
					&& mdDoc.getElementsByTagName(key).item(0).getFirstChild().getNodeValue() != null) {
				Element ele = (Element) mdDoc.getElementsByTagName(key).item(0);
				configvalue = ele.getFirstChild().getNodeValue();
			}
			
			if (configvalue == null || configvalue.isEmpty()) {
				throw new MissingResourceException("Can't find resource key " + key, ConfigUtils.class.getName(), key);
			}
			
		}
		catch (ParserConfigurationException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (SAXException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (IOException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		
		return configvalue;
	}
	
	/**
	 * Metodo che restituisce le configurazioni per l'importazione dei dati meteo
	 * 
	 * @param key
	 *            nome
	 * @return numero di elements che hanno quel nome
	 * @throws MissingResourceException
	 *             eccezione sollevata in caso che la proprieta' richiesta non sia presente nel file di configurazione *
	 */
	public static int getImportMeteoDataConfigCount(String key) throws MissingResourceException {
		
		int configcount = 0;
		try {
			mdDoc = setDocument(mdDoc, Constants.METEO_DATA_CONFIG_FILE_NAME);
			
			if (mdDoc.getElementsByTagName(key) != null) {
				configcount = mdDoc.getElementsByTagName(key).getLength();
			}
			
		}
		catch (ParserConfigurationException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (SAXException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (IOException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		
		return configcount;
	}
	
	public static int getImportMeteoDataConfigCount(String key, String parentKey) throws MissingResourceException {
		
		int configcount = 0;
		try {
			mdDoc = setDocument(mdDoc, Constants.METEO_DATA_CONFIG_FILE_NAME);
			
			if (mdDoc.getElementsByTagName(parentKey) != null) {
				Element ele = (Element) mdDoc.getElementsByTagName(parentKey).item(0);
				configcount = ele.getElementsByTagName("Procedure").getLength();
			}
			
		}
		catch (ParserConfigurationException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (SAXException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (IOException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		
		return configcount;
	}
	
	/**
	 * Metodo che restituisce le configurazioni per l'importazione dei dati meteo
	 * 
	 * @param key
	 *            nome del tag
	 * @param keyParent
	 *            nome del tag padre
	 * @param index
	 *            indice del tag con nome uguale a key
	 * @return valore
	 * @throws MissingResourceException
	 *             eccezione sollevata in caso che la proprieta' richiesta non sia presente nel file di configurazione *
	 */
	public static String getImportMeteoDataConfigValue(String key, String keyParent, int index)
			throws MissingResourceException {
		
		String configvalue = null;
		try {
			mdDoc = setDocument(mdDoc, Constants.METEO_DATA_CONFIG_FILE_NAME);
			
			if (keyParent == null || keyParent.isEmpty()) {
				if (mdDoc.getElementsByTagName(key) != null && mdDoc.getElementsByTagName(key).item(index - 1) != null
						&& mdDoc.getElementsByTagName(key).item(index - 1).getFirstChild() != null
						&& mdDoc.getElementsByTagName(key).item(index - 1).getFirstChild().getNodeValue() != null) {
					Element ele = (Element) mdDoc.getElementsByTagName(key).item(index - 1);
					configvalue = ele.getFirstChild().getNodeValue();
				}
			}
			else {
				if (mdDoc.getElementsByTagName(keyParent) != null
						&& mdDoc.getElementsByTagName(keyParent).item(index - 1) != null) {
					Element eleParent = (Element) mdDoc.getElementsByTagName(keyParent).item(index - 1);
					//qui vado leggere i typename
					if (eleParent.getElementsByTagName(key) != null
							&& eleParent.getElementsByTagName(key).item(0) != null
							&& eleParent.getElementsByTagName(key).item(0).getFirstChild() != null
							&& eleParent.getElementsByTagName(key).item(0).getFirstChild().getNodeValue() != null) {
						Element ele = (Element) eleParent.getElementsByTagName(key).item(0);
						configvalue = ele.getFirstChild().getNodeValue();
					}
				}
			}
			
			if (configvalue == null || configvalue.isEmpty()) {
				throw new MissingResourceException("Can't find resource key " + key, ConfigUtils.class.getName(), key);
			}
			
		}
		catch (ParserConfigurationException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (SAXException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (IOException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		
		return configvalue;
	}
	
	/**
	 * ho aggiunto un livello di gerarchia, posso partire dal nonno
	 * 
	 * @param key
	 * @param keyParent
	 * @param keyGranParent
	 * @param index
	 * @return
	 * @throws MissingResourceException
	 */
	public static String getImportMeteoDataConfigValue(String key, String keyParent, String keyGrandParent, int index)
			throws MissingResourceException {
		
		String configvalue = null;
		try {
			mdDoc = setDocument(mdDoc, Constants.METEO_DATA_CONFIG_FILE_NAME);
			
			if (mdDoc.getElementsByTagName(keyGrandParent) != null
					&& mdDoc.getElementsByTagName(keyGrandParent).item(0) != null) {
				Element eleGrandParent = (Element) mdDoc.getElementsByTagName(keyGrandParent).item(0);
				if (eleGrandParent.getElementsByTagName(keyParent) != null
						&& eleGrandParent.getElementsByTagName(keyParent).item(index - 1) != null
						&& eleGrandParent.getElementsByTagName(keyParent).item(index - 1).getFirstChild() != null
						&& eleGrandParent.getElementsByTagName(keyParent).item(index - 1).getFirstChild()
								.getNodeValue() != null) {
					Element eleParent = (Element) eleGrandParent.getElementsByTagName(keyParent).item(index - 1);
					if (eleParent.getElementsByTagName(key) != null
							&& eleParent.getElementsByTagName(key).item(0) != null
							&& eleParent.getElementsByTagName(key).item(0).getFirstChild() != null
							&& eleParent.getElementsByTagName(key).item(0).getFirstChild().getNodeValue() != null) {
						Element ele = (Element) eleParent.getElementsByTagName(key).item(0);
						configvalue = ele.getFirstChild().getNodeValue();
					}
				}
			}
			
			if (configvalue == null || configvalue.isEmpty()) {
				throw new MissingResourceException("Can't find resource key " + key, ConfigUtils.class.getName(), key);
			}
			
		}
		catch (ParserConfigurationException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (SAXException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (IOException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		
		return configvalue;
	}
	
	/**
	 * Metodo che restituisce le configurazioni per l'importazione dei dati meteo
	 * 
	 * @param key
	 *            nome del tag
	 * @param keyParent
	 *            nome del tag padre
	 * @param index
	 *            indice del tag con nome uguale a key
	 * @param attribute
	 *            attributo del tag con nome uguale a key
	 * @return valore
	 * @throws MissingResourceException
	 *             eccezione sollevata in caso che la proprieta' richiesta non sia presente nel file di configurazione *
	 */
	//mi da il valore dell'attributo specificato per un determinato tag
	public static String getImportMeteoDataConfigAttribute(String key, String keyParent, int index, String attribute)
			throws MissingResourceException {
		
		String configvalue = null;
		try {
			mdDoc = setDocument(mdDoc, Constants.METEO_DATA_CONFIG_FILE_NAME);
			
			if (keyParent == null || keyParent.isEmpty()) {
				if (mdDoc.getElementsByTagName(key) != null && mdDoc.getElementsByTagName(key).item(index - 1) != null
						&& mdDoc.getElementsByTagName(key).item(index - 1).getFirstChild() != null
						&& mdDoc.getElementsByTagName(key).item(index - 1).getFirstChild().getNodeValue() != null) {
					Element ele = (Element) mdDoc.getElementsByTagName(key).item(index - 1);
					configvalue = ele.getAttribute(attribute);
				}
			}
			else {
				if (mdDoc.getElementsByTagName(keyParent) != null
						&& mdDoc.getElementsByTagName(keyParent).item(0) != null) {
					Element eleParent = (Element) mdDoc.getElementsByTagName(keyParent).item(0);
					if (eleParent.getElementsByTagName(key) != null
							&& eleParent.getElementsByTagName(key).item(index - 1) != null
							&& eleParent.getElementsByTagName(key).item(index - 1).getFirstChild() != null
							&& eleParent.getElementsByTagName(key).item(index - 1).getFirstChild().getNodeValue() != null) {
						Element ele = (Element) eleParent.getElementsByTagName(key).item(index - 1);
						configvalue = ele.getAttribute(attribute);
					}
				}
			}
			
			if (configvalue == null || configvalue.isEmpty()) {
				throw new MissingResourceException("Can't find resource key " + key, ConfigUtils.class.getName(), key);
			}
			
		}
		catch (ParserConfigurationException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (SAXException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (IOException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		
		return configvalue;
	}
	
	/**
	 * Metodo che restituisce le configurazioni per la creazione dei csv da green button
	 * 
	 * @param key
	 *            nome
	 * @return valore
	 * @throws MissingResourceException
	 *             eccezione sollevata in caso che la proprieta' richiesta non sia presente nel file di configurazione *
	 */
	public static String getGBToCsvConfigValue(String key) throws MissingResourceException {
		
		String configvalue = null;
		try {
			greenButtonToCsvDoc = setDocument(greenButtonToCsvDoc, Constants.GREEN_BUTTON_TO_CSV_CONFIG_FILE_NAME);
			
			if (greenButtonToCsvDoc.getElementsByTagName(key) != null
					&& greenButtonToCsvDoc.getElementsByTagName(key).item(0) != null
					&& greenButtonToCsvDoc.getElementsByTagName(key).item(0).getFirstChild() != null
					&& greenButtonToCsvDoc.getElementsByTagName(key).item(0).getFirstChild().getNodeValue() != null) {
				Element ele = (Element) greenButtonToCsvDoc.getElementsByTagName(key).item(0);
				configvalue = ele.getFirstChild().getNodeValue();
			}
			
			if (configvalue == null || configvalue.isEmpty()) {
				throw new MissingResourceException("Can't find resource key " + key, ConfigUtils.class.getName(), key);
			}
			
		}
		catch (ParserConfigurationException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (SAXException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (IOException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		
		return configvalue;
	}
	
	/**
	 * Metodo che restituisce le configurazioni per la creazione dei csv da green button
	 * 
	 * @param key
	 *            nome
	 * @return valore
	 * @throws MissingResourceException
	 *             eccezione sollevata in caso che la proprieta' richiesta non sia presente nel file di configurazione *
	 */
	public static String getGBToCsvConfigValueWithAttribute(String key, String attributeName, String attributeValue)
			throws MissingResourceException {
		
		String configvalue = null;
		try {
			greenButtonToCsvDoc = setDocument(greenButtonToCsvDoc, Constants.GREEN_BUTTON_TO_CSV_CONFIG_FILE_NAME);
			
			if (key != null && attributeName != null && attributeValue != null
					&& greenButtonToCsvDoc.getElementsByTagName(key) != null
					&& greenButtonToCsvDoc.getElementsByTagName(key).getLength() > 0) {
				NodeList listEle = greenButtonToCsvDoc.getElementsByTagName(key);
				for (int k = 0; k < listEle.getLength(); k++) {
					Element ele = (Element) listEle.item(k);
					if (ele != null && ele.getFirstChild() != null && ele.getFirstChild().getNodeValue() != null
							&& ele.getAttribute(attributeName) != null
							&& ele.getAttribute(attributeName).equals(attributeValue)) {
						configvalue = ele.getFirstChild().getNodeValue();
						break;
					}
				}
				
			}
			
			if (configvalue == null || configvalue.isEmpty()) {
				throw new MissingResourceException("Can't find resource key " + key, ConfigUtils.class.getName(), key);
			}
			
		}
		catch (ParserConfigurationException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (SAXException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (IOException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		
		return configvalue;
	}
	
	public static LinkedList<UsagePoint> getGBToCsvUsagePoint(String dataCustodian, int index, String usagePointName) {
		LinkedList<UsagePoint> usagePoints = new LinkedList<UsagePoint>();
		try {
			greenButtonToCsvDoc = setDocument(greenButtonToCsvDoc, Constants.GREEN_BUTTON_TO_CSV_CONFIG_FILE_NAME);
			
			Element eleParent = (Element) greenButtonToCsvDoc.getElementsByTagName(dataCustodian).item(index);
			
			int elements = eleParent.getElementsByTagName(usagePointName).getLength();
			
			for (int i = 0; i < elements; i++) {
				UsagePoint usagePoint = new UsagePoint();
				Element ele = (Element) eleParent.getElementsByTagName(usagePointName).item(i);
				String upn = ele.getElementsByTagName(Constants.ID_USAGE_POINT_NAME).item(0).getFirstChild()
						.getNodeValue();
				String upmr = ele.getElementsByTagName(Constants.ID_METER_READING_NAME).item(0).getFirstChild()
						.getNodeValue();
				String uprt = ele.getElementsByTagName(Constants.ID_READING_TYPE_NAME).item(0).getFirstChild()
						.getNodeValue();
				
				String upcomodity = null;
				try {
					upcomodity = ele.getElementsByTagName("Type").item(0).getFirstChild().getNodeValue().trim();
					logger.debug("leggo il nuovo nome" + upcomodity);
				}
				catch (Exception e) {
					upcomodity = null;
					//TOTO il blocco potrebbe non esistere: migliorare il codice 
				}
				
				String upname = null;
				try {
					upname = ele.getElementsByTagName(Constants.PROCEDURE).item(0).getFirstChild().getNodeValue()
							.trim();
					logger.debug("leggo il nuovo nome" + upname);
				}
				catch (Exception e) {
					upname = null;
					//TOTO il blocco potrebbe non esistere: migliorare il codice 
				}
				
				usagePoint.setIdUsagePoint(Integer.parseInt(upn));
				usagePoint.setIdMeterReading(Integer.parseInt(upmr));
				usagePoint.setIdReadingType(Integer.parseInt(uprt));
				usagePoint.setOffering_name(upname);
				usagePoint.setCommodity(upcomodity);
				usagePoints.add(usagePoint);
				
			}
			
			//Element ele = (Element) eleParent.getElementsByTagName(usagePoint).item(0);
			//configvalue = ele.getFirstChild().getNodeValue();
			
		}
		catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return usagePoints;
		
	}
	
	/**
	 * Metodo che restituisce le configurazioni per l'importazione dei dati meteo
	 * 
	 * @param key
	 *            nome del tag
	 * @param keyParent
	 *            nome del tag padre
	 * @param index
	 *            indice del tag con nome uguale a key
	 * @return valore
	 * @throws MissingResourceException
	 *             eccezione sollevata in caso che la proprieta' richiesta non sia presente nel file di configurazione *
	 */
	public static String getGBToCsvConfigValue(String key, String keyParent, int index) throws MissingResourceException {
		
		String configvalue = null;
		try {
			greenButtonToCsvDoc = setDocument(greenButtonToCsvDoc, Constants.GREEN_BUTTON_TO_CSV_CONFIG_FILE_NAME);
			
			if (keyParent == null || keyParent.isEmpty()) {
				if (greenButtonToCsvDoc.getElementsByTagName(key) != null
						&& greenButtonToCsvDoc.getElementsByTagName(key).item(index) != null
						&& greenButtonToCsvDoc.getElementsByTagName(key).item(index).getFirstChild() != null
						&& greenButtonToCsvDoc.getElementsByTagName(key).item(index).getFirstChild().getNodeValue() != null) {
					Element ele = (Element) greenButtonToCsvDoc.getElementsByTagName(key).item(index);
					configvalue = ele.getFirstChild().getNodeValue();
				}
			}
			else {
				if (greenButtonToCsvDoc.getElementsByTagName(keyParent) != null
						&& greenButtonToCsvDoc.getElementsByTagName(keyParent).item(index) != null) {
					Element eleParent = (Element) greenButtonToCsvDoc.getElementsByTagName(keyParent).item(index);
					if (eleParent.getElementsByTagName(key) != null
							&& eleParent.getElementsByTagName(key).item(0) != null
							&& eleParent.getElementsByTagName(key).item(0).getFirstChild() != null
							&& eleParent.getElementsByTagName(key).item(0).getFirstChild().getNodeValue() != null) {
						Element ele = (Element) eleParent.getElementsByTagName(key).item(0);
						configvalue = ele.getFirstChild().getNodeValue();
					}
				}
			}
			
			if (configvalue == null || configvalue.isEmpty()) {
				throw new MissingResourceException("Can't find resource key " + key, ConfigUtils.class.getName(), key);
			}
			
		}
		catch (ParserConfigurationException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (SAXException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (IOException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		
		return configvalue;
	}
	
	/**
	 * Metodo che restituisce le configurazioni per l'importazione dei dati meteo
	 * 
	 * @param key
	 *            nome del tag
	 * @param keyParent
	 *            nome del tag padre
	 * @param index
	 *            indice del tag con nome uguale a key
	 * @param attribute
	 *            attributo del tag con nome uguale a key
	 * @return valore
	 * @throws MissingResourceException
	 *             eccezione sollevata in caso che la proprieta' richiesta non sia presente nel file di configurazione *
	 */
	public static String getGBToCsvConfigAttribute(String key, String keyParent, int index, String attribute)
			throws MissingResourceException {
		
		String configvalue = null;
		try {
			greenButtonToCsvDoc = setDocument(greenButtonToCsvDoc, Constants.GREEN_BUTTON_TO_CSV_CONFIG_FILE_NAME);
			
			if (keyParent == null || keyParent.isEmpty()) {
				if (greenButtonToCsvDoc.getElementsByTagName(key) != null
						&& greenButtonToCsvDoc.getElementsByTagName(key).item(index - 1) != null
						&& greenButtonToCsvDoc.getElementsByTagName(key).item(index - 1).getFirstChild() != null
						&& greenButtonToCsvDoc.getElementsByTagName(key).item(index - 1).getFirstChild().getNodeValue() != null) {
					Element ele = (Element) greenButtonToCsvDoc.getElementsByTagName(key).item(index - 1);
					configvalue = ele.getAttribute(attribute);
				}
			}
			else {
				if (greenButtonToCsvDoc.getElementsByTagName(keyParent) != null
						&& greenButtonToCsvDoc.getElementsByTagName(keyParent).item(index - 1) != null) {
					Element eleParent = (Element) greenButtonToCsvDoc.getElementsByTagName(keyParent).item(index - 1);
					if (eleParent.getElementsByTagName(key) != null
							&& eleParent.getElementsByTagName(key).item(0) != null
							&& eleParent.getElementsByTagName(key).item(0).getFirstChild() != null
							&& eleParent.getElementsByTagName(key).item(0).getFirstChild().getNodeValue() != null) {
						Element ele = (Element) eleParent.getElementsByTagName(key).item(0);
						configvalue = ele.getAttribute(attribute);
					}
				}
			}
			
			if (configvalue == null || configvalue.isEmpty()) {
				throw new MissingResourceException("Can't find resource key " + key, ConfigUtils.class.getName(), key);
			}
			
		}
		catch (ParserConfigurationException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (SAXException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (IOException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		
		return configvalue;
	}
	
	public static int getGBToCsvConfigCount(String key) throws MissingResourceException {
		
		int configcount = 0;
		try {
			greenButtonToCsvDoc = setDocument(greenButtonToCsvDoc, Constants.GREEN_BUTTON_TO_CSV_CONFIG_FILE_NAME);
			
			if (greenButtonToCsvDoc.getElementsByTagName(key) != null) {
				configcount = greenButtonToCsvDoc.getElementsByTagName(key).getLength();
			}
			
		}
		catch (ParserConfigurationException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (SAXException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		catch (IOException ex) {
			logger.error("Errore nella lettura del file di configurazione " + Constants.METEO_DATA_CONFIG_FILE_NAME);
			ex.printStackTrace();
		}
		
		return configcount;
	}
	
	public static Document setDocument(Document doc, String nameFile) throws ParserConfigurationException,
			FileNotFoundException, SAXException, IOException {
		
		if (doc == null) {
			InputStream inputStream = ConfigUtils.class.getClassLoader().getResourceAsStream(nameFile);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			//mdDoc = db.parse(inputStream);
			doc = db.parse(inputStream);
			//doc = db.parse(new FileInputStream("C:\\Sviluppo\\ws_keplero\\sunshine-services 2.0\\config\\localhost\\"
			//		+ nameFile));
		}
		return doc;
		
	}
	
}
