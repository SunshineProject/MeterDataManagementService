/**
 * 
 */
package it.sinergis.sunshine.util;

/**
 * Classe per la definizione dei parametri configurabili usando file di
 * properties.
 * 
 * @author Rossana Bambili
 */
public class ConfigValues {


	/**
	 * ora in cui lo scheduler deve far partire il thread.
	 */
	public static final String ORA_SCHEDULING = ConfigUtils
			.getXemtecConfigValue("oraScheduling");

	/**
	 * minuto in cui lo scheduler far� partire il thread.
	 */
	public static final String MINUTO_SCHEDULING = ConfigUtils
			.getXemtecConfigValue("minutoScheduling");

	/**
	 * secondo in cui lo scheduler far� partire il thread.
	 */
	public static final String SECONDO_SCHEDULING = ConfigUtils
			.getXemtecConfigValue("secondoScheduling");
	

	/**
	 * indica se lo schedulatore che scrive su greenbutton da xemtec e' attivo.
	 */
	public static final String XEMTEC_TO_GREEN_BUTTON_ATTIVO = ConfigUtils
			.getXemtecConfigValue("attivo");
	
	
	/**
	 * url data custodian.
	 */
	public static final String URL_DATA_CUSTODIAN = ConfigUtils
			.getXemtecConfigValue("urlDataCustodian");
	
	
	/**
	 * authorization data custodian admin.
	 */
	public static final String AUTHORIZATION_DATA_CUSTODIAN = ConfigUtils
			.getXemtecConfigValue("authorization");
	
	
	/**
	 * numero di usage point.
	 */
	public static final String COUNT_USAGE_POINT = ConfigUtils
			.getXemtecConfigValue("countUsagePoint");	
		
	/** Costruttore privato. */
	private ConfigValues() {
	}
}
