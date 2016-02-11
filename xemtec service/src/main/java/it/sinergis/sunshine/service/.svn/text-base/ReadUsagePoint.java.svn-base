package it.sinergis.sunshine.service;

import it.sinergis.sunshine.util.ConfigValues;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.log4j.Logger;


/**
 * Classe che legge e restituisce un xml con gli usagepoint
 * richiesti in base al periodo temporale
 * @author Rossana Bambili
 */
public class ReadUsagePoint {
    
	
	/** Logger. */
	private static Logger logger; 

	
    /**
     * Costruttore di default
    */
    public ReadUsagePoint(){
    	logger = Logger.getLogger(this.getClass());    	
    }

    public String execute(String datastart,String dataend,Long idUsagePoint,Long idRetailCustomer)
    {
    	BufferedReader in   = null;
		HttpURLConnection  connServlet = null;		
		String response = "";
		try {	
				    	
			   	String urlReadUsagePoint = ConfigValues.URL_DATA_CUSTODIAN + "Batch/RetailCustomer/";
			   	
			   	if (idRetailCustomer==null) {
			   		throw new Exception("Specificare l\'id del Retail Customer");
			   	} else {
			   		urlReadUsagePoint +=  idRetailCustomer +"/UsagePoint/";
			   	}
			   	
			   	if (idUsagePoint!=null) {
			   		urlReadUsagePoint += idUsagePoint + "/";
			   	}
			   			
			   	if ((datastart!=null && !datastart.equals("")) || (dataend!=null && !dataend.equals("")) ) {
			   	 	urlReadUsagePoint += "?";
			   	 	if (datastart!=null && !datastart.equals("")) {
			   	 		urlReadUsagePoint += "published-min=" + java.net.URLEncoder.encode(datastart,"UTF-8");
				   	 	if (dataend!=null && !dataend.equals("")) {
				   	 		urlReadUsagePoint += "&published-max=" + java.net.URLEncoder.encode(dataend,"UTF-8"); 
				   	 	}
			   	 	} else {
				   	 	if (dataend!=null && !dataend.equals("")) {
				   	 		urlReadUsagePoint += "published-max=" + java.net.URLEncoder.encode(dataend,"UTF-8");
				   	 	}
			   	 	}
			   	}		  
			   	
			   	
		    	logger.debug("urlReadUsagePoint:" + urlReadUsagePoint);
		    
		    	logger.debug("Inizio lettura usage point");
		    	
		    	URL url = new URL(urlReadUsagePoint);			    		

	    		connServlet = (HttpURLConnection)url.openConnection();
	            connServlet.setRequestMethod("GET");    
	            connServlet.setDoOutput(true);
	            connServlet.setDoInput(true); 		                       
	            connServlet.setUseCaches(false);
	           
	            in   = new BufferedReader(new InputStreamReader(connServlet.getInputStream()));
	            String         output;
	            String line="";

	            for (output = ""; (line = in.readLine()) != null; output = output + line.toString().trim())
	            {
	                ;
	            }
	           	    
	            output = output.replaceAll("\t", "");
	            output = output.replaceAll("\n", "");
	            output = output.replaceAll("\r", "");
	            output = output.trim();
	        	logger.debug("RISPOSTA DATACUSTODIAN:" + output);	
	        	response = output;
	        	logger.debug("Fine lettura usage point");
	            
	            in.close();
	    	    connServlet.disconnect(); 
			    	   	            
			
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Eccezione:", e);
				response = "<ReadUsagePointResponse>";
				response += "<error>" +e.getLocalizedMessage() + "</error>";
				response += "</ReadUsagePointResponse>";
			}  catch (Exception e) {
				e.printStackTrace();
				logger.error("Eccezione:", e);
				response = "<ReadUsagePointResponse>";
				response += "<error>" +e.getLocalizedMessage() + "</error>";
				response += "</ReadUsagePointResponse>";
			}  finally {
				if (in!=null) {
					try {	
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
						logger.error("Eccezione:", e);
					}
				}
				if (connServlet!=null) {					
						connServlet.disconnect();						
				}					
			}
			  
		    logger.debug(response);
		    return  response;		    
	}
}
