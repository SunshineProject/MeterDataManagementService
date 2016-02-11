/**
 * 
 */
package it.sinergis.sunshine.util;

/**
 * Classe per la definizione dei parametri configurabili usando file di properties.
 * 
 * @author Rossana Bambili
 */
public class ConfigValues {
	
	/* SCRITTURA CSV */
	
	/**
	 * ora in cui lo scheduler deve far partire il thread.
	 */
	public static final String ORA_SCHEDULING = ConfigUtils.getGBToCsvConfigValue("HourScheduling");
	
	/**
	 * minuto in cui lo scheduler far� partire il thread.
	 */
	public static final String MINUTO_SCHEDULING = ConfigUtils.getGBToCsvConfigValue("MinuteScheduling");
	
	/**
	 * secondo in cui lo scheduler far� partire il thread.
	 */
	public static final String SECONDO_SCHEDULING = ConfigUtils.getGBToCsvConfigValue("SecondScheduling");
	
	/**
	 * indica se lo schedulatore che scrive i csv da greenbutton e' attivo.
	 */
	public static final String GREEN_BUTTON_TO_CSV_ATTIVO = ConfigUtils.getGBToCsvConfigValue("Active");
	
	/**
	 * numero di usage point.
	 */
	public static final String COUNT_USAGE_POINTS = ConfigUtils.getGBToCsvConfigValue("CountUsagePoints");
	
	/* DATI METEO */
	
	/**
	 * ora in cui lo scheduler deve far partire il thread.
	 */
	public static final String ORA_SCHEDULING_METEO = ConfigUtils.getImportMeteoDataConfigValue("HourScheduling");
	
	/**
	 * minuto in cui lo scheduler far� partire il thread.
	 */
	public static final String MINUTO_SCHEDULING_METEO = ConfigUtils.getImportMeteoDataConfigValue("MinuteScheduling");
	
	/**
	 * secondo in cui lo scheduler far� partire il thread.
	 */
	public static final String SECONDO_SCHEDULING_METEO = ConfigUtils.getImportMeteoDataConfigValue("SecondScheduling");
	
	/**
	 * indica se lo schedulatore dei dati meteo e' attivo.
	 */
	public static final String IMPORT_METEO_DATA_ATTIVO = ConfigUtils.getImportMeteoDataConfigValue("Active");
	
	/**
	 * indica il numero di location da leggere e importare su sos.
	 */
	public static final int COUNT_PROCEDURES_METEO = ConfigUtils.getImportMeteoDataConfigCount("Procedure");
	
	/**
	 * indica il numero di typename da leggere e importare su sos.
	 */
	public static final int COUNT_TYPENAMES_METEO = ConfigUtils.getImportMeteoDataConfigCount("TypeName");
	
	public static final int COUNT_WFSURLS_METEO = ConfigUtils.getImportMeteoDataConfigCount("url");
	
	/** Costruttore privato. */
	private ConfigValues() {
	}
}
