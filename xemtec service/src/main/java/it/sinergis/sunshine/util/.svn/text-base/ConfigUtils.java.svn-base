package it.sinergis.sunshine.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * Classe che legge le impostazioni da file.
 * 
 * @author Rossana Bambili
 */
public final class ConfigUtils {

	/** Logger. */
	private static Logger logger = Logger.getLogger(ConfigUtils.class);

	/** ResourceBundle con le impostazioni dello usage point in cui inserire gli interval block. */
	private static ResourceBundle xeRb;
	

	/**
	 * Costruttore di default.
	 */
	private ConfigUtils() {
	}

	
	/**
	 * Metodo che restituisce le configurazioni dello usage point
	 * 
	 * @param key nome
	 * 
	 * @return valore
	 * @throws MissingResourceException eccezione sollevata in caso che la
	 *         proprieta' richiesta non sia presente nel file di configurazione	 *        
	 */
	public static String getXemtecConfigValue(String key)
			throws MissingResourceException {
		if (xeRb == null) {
			try {
				xeRb = ResourceBundle
						.getBundle(Constants.XEMTEC_CONFIG_FILE_NAME);
			} catch (NullPointerException npe) {
				logger.error("File " + Constants.XEMTEC_CONFIG_FILE_NAME
						+ ".properties non trovato");
				npe.printStackTrace();
			} catch (MissingResourceException mre) {
				logger.error("File " + Constants.XEMTEC_CONFIG_FILE_NAME
						+ ".properties non trovato");
				mre.printStackTrace();
			}
		}

		return xeRb.getString(key);
	}
	
	
}
