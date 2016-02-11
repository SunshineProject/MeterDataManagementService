package it.sinergis.sunshine.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.log4j.Logger;


/**
 * Classe che genera dei file csv che simulano 
 * la lettura di un contatore per un intera giornata  
 * @author Rossana Bambili
 */
public class CreateFileCsv {
    
	
	/** Logger. */
	private static Logger logger; 

	
    /**
     * Costruttore di default
    */
    public CreateFileCsv(){
    	logger = Logger.getLogger(this.getClass());    	
    }

    public String execute(Date datastart,String path,Double usage)
    {
    	FileOutputStream is = null;
    	OutputStreamWriter osw = null;   
    	String response = "<CreateFileCsvResponse>";
    	
    	try {
    		
    		if (datastart==null || path==null || path.equals("") || usage==null) {
    			throw new Exception("Occorre specificare la data su cui creare la lettura, il path della directory in cui inserire i csv e il consumo iniziale.");
    		}
    		
    		DateFormat dfdate = new SimpleDateFormat("ddMMyyyyHHmmss");       	
        	
        	GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
            gc.setTime(datastart);
            gc.set(java.util.Calendar.HOUR_OF_DAY, 01);
            gc.set(java.util.Calendar.MINUTE, 00);
            gc.set(java.util.Calendar.SECOND, 00);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");          
            
        	for (int j=1;j<=6;j++) {
            	String dateStr = dfdate.format(gc.getTime());	    
            	File csv = new File(path + "\\Contatore" + dateStr + ".csv");
            	is = new FileOutputStream(csv);
            	osw = new OutputStreamWriter(is);    
            	DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            	dfs.setDecimalSeparator('.');
            	DecimalFormat df2 = new DecimalFormat( "####0.000" );
            	df2.setDecimalFormatSymbols(dfs);
              
            	for (int i=1;i<=4;i++) {
            		String row =  "";
            		if (i==4) {
            			 row =  df.format(gc.getTime()) + ";" + df2.format(usage);
            		} else {
            			 row =  df.format(gc.getTime()) + ";" + df2.format(usage) + "\r\n";
            		}
            		logger.debug("row:"+row);
            		osw.write(row);
            		gc.add(java.util.Calendar.SECOND, 3600);
            		usage = usage + 0.915; 
            	}
            	osw.close();
            	is.close();            	
            }
        	response += "Elaborazione effettuata con successo";
    		
    	} catch (IOException e) {
    		response += "<error>" +e.getLocalizedMessage() + "</error>";
			e.printStackTrace();
			logger.error("Eccezione:", e);
		} catch (Exception e) {
			response += "<error>" +e.getLocalizedMessage() + "</error>";
			e.printStackTrace();
			logger.error("Eccezione:", e);
		}   finally {
			if (is!=null) {
				try {	
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("Eccezione:", e);
				}
			}	
			if (osw!=null) {
				try {	
					osw.close();
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("Eccezione:", e);
				}
			}		
		}
    	
    	response += "</CreateFileCsvResponse>";
    	return response;
	}
}
