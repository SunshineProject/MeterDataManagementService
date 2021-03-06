package it.sinergis.sunshine.util;

/**
 * Classe per la definizione delle costanti.
 * 
 * @author Rossana Bambili
 */
public class Constants {
	
	/**
	 * Nome del file di configurazione con le informazioni dello usage point in cui inserire gli interval block
	 */
	public static final String XEMTEC_CONFIG_FILE_NAME = "xemtecConfiguration";
	
	/** Nome della persistence unit usata nelle classi JPA per il db di Xemtec. */
	public static final String PERSISTENCE_XEMTEC = "xemtec";
	
	/**
	 * prefisso della costante dove sta l'id dello usage point in cui inserire gli interval block.
	 */
	public static final String ID_USAGE_POINT_PREFIX = "idUsagePoint_";
	
	/**
	 * prefisso della costante dove sta l'id del meter reading in cui inserire gli interval block.
	 */
	public static final String ID_METER_READING_PREFIX = "idMeterReading_";
	
	/**
	 * prefisso della costante dove sta l'id del retail customer in cui inserire lo usage point.
	 */
	public static final String ID_RETAIL_CUSTOMER_PREFIX = "idRetailCustomer_";
	
	/**
	 * prefisso della costante dove sta l'id del device xemtec per sapere quale dispotivo leggere .
	 */
	public static final String ID_DEVICE_PREFIX = "idDevice_";
	
	/**
	 * prefisso della costante dove sta l'url della directory csv dove inserire i file csv.
	 */
	public static final String URL_FILE_CSV_PREFIX = "urlFileCsv_";
	
	/**
	 * uri Atom.
	 */
	public static final String URI_ATOM = "http://www.w3.org/2005/Atom";
	
	/**
	 * uri Espi.
	 */
	public static final String URI_ESPI = "http://naesb.org/espi";
	
	/** Costruttore privato. */
	public Constants() {
	}
}
