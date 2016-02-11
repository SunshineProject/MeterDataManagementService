/**
 * 
 */
package it.sinergis.sunshine.util;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
/**
 * Classe di utilita' per convertire le date.
 * 
 * @author Rossana Bambili
 */
public class ConvertDate {
	/** L'istanza del logger. */
	private static Logger logger;

	/**
	 * Costruttore.
	 */
	private ConvertDate() {
		 logger = Logger.getLogger(this.getClass());
	}

	
	/**
	 * Applica una conversione al formato utc + 1 .
	 * 
	 * @param gc data da aconveertire
	 * @param dstStartRule regola per inizio ora solare
	 * @param dstEndRule regola per fine ora solare
	 * @param dstOffset secondi di fuso ora solare
	 * @param tzOffset secondi da sottarre
	 * @return data convertita
	 */
	public static GregorianCalendar convertTimeToUtc(GregorianCalendar gc,String dstStartRule,String dstEndRule,int dstOffset,int tzOffset) {
		
		//calcolo la data di inizio dell'ora solare  
		
		String bin = new BigInteger(dstStartRule, 16).toString(2);	
		//String bin = String.valueOf(binInt);
		
		while (bin.length()<32) {
			bin = "0" + bin;
		}
		
		int meseStart = Integer.parseInt(bin.substring(0,4), 2);
		
		int operatorStart = Integer.parseInt(bin.substring(4,7), 2);
		
		int dayOfMonthStart = Integer.parseInt(bin.substring(7,12), 2);
		 
		int dayOfWeekStart = Integer.parseInt(bin.substring(12,15), 2);
		 
		int hoursStart = Integer.parseInt(bin.substring(15,20), 2);		 
		 
		int secondsStart = Integer.parseInt(bin.substring(20), 2);
		
		Calendar calStart = Calendar.getInstance();
        calStart.set(Calendar.MONTH, meseStart -1);
        calStart.set(Calendar.YEAR, gc.get(Calendar.YEAR));
       
        if (secondsStart!=0) {
        	calStart.set(Calendar.HOUR_OF_DAY, hoursStart);
        	if ((secondsStart-1)!=0) {
	        	calStart.set(Calendar.MINUTE, (secondsStart-1)/60);
	        	calStart.set(Calendar.SECOND,0);
        	} else {
        		calStart.set(Calendar.MINUTE, 0);
	        	calStart.set(Calendar.SECOND,0);
        	}
        } else {
        	calStart.set(Calendar.HOUR_OF_DAY, hoursStart-1);
        	calStart.set(Calendar.MINUTE,59);
        	calStart.set(Calendar.SECOND,59);
        }
       
                     
        switch(dayOfWeekStart) {
	         case 1:
	        	 calStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	             break;
	         case 2:
	        	 calStart.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
	             break;
	         case 3:
	        	 calStart.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
	             break;
	         case 4:
	        	 calStart.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
	             break;
	         case 5:
	        	 calStart.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
	             break;
	         case 6:
	        	 calStart.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
	             break;
	         case 7:
	        	 calStart.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
	             break;            
        }
         
        switch(operatorStart) {
	         case 0:
	        	 calStart.set(Calendar.DAY_OF_MONTH, dayOfMonthStart);
	             break;
	         case 1:
	        	 calStart.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);//da rivedere
	             break;
	         case 2:
	        	 calStart.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);    
	             break;
	         case 3:
	        	 calStart.set(Calendar.DAY_OF_WEEK_IN_MONTH, 2);    
	             break;
	         case 4:
	        	 calStart.set(Calendar.DAY_OF_WEEK_IN_MONTH, 3);    
	             break;
	         case 5:
	        	 calStart.set(Calendar.DAY_OF_WEEK_IN_MONTH, 4);    
	             break;
	         case 6:
	        	 calStart.set(Calendar.DAY_OF_WEEK_IN_MONTH, -1);    
	             break;          
	         case 7:
	        	 calStart.set(Calendar.DAY_OF_WEEK_IN_MONTH, -1);        	
	             break;   
        }
     
        //calcolo la data di fine dell'ora solare  
        bin = new BigInteger(dstEndRule, 16).toString(2);
        
        while (bin.length()<32) {
			bin = "0" + bin;
		}
				
        int meseEnd = Integer.parseInt(bin.substring(0,4), 2);
		
		int operatorEnd = Integer.parseInt(bin.substring(4,7), 2);
		
		int dayOfMonthEnd = Integer.parseInt(bin.substring(7,12), 2);
		 
		int dayOfWeekEnd = Integer.parseInt(bin.substring(12,15), 2);
		 
		int hoursEnd = Integer.parseInt(bin.substring(15,20), 2);		 
		 
		int secondsEnd = Integer.parseInt(bin.substring(20), 2);
  		
  		Calendar calEnd = Calendar.getInstance();
        calEnd.set(Calendar.MONTH, meseEnd -1);       
        calEnd.set(Calendar.YEAR, gc.get(Calendar.YEAR));        
        calEnd.set(Calendar.HOUR_OF_DAY, hoursEnd);
        if (secondsEnd!=0) {
        	calEnd.set(Calendar.MINUTE, secondsEnd/60);
        	calEnd.set(Calendar.SECOND,0);
        } else {
        	calEnd.set(Calendar.MINUTE,0);
        	calEnd.set(Calendar.SECOND,0);
        }
      
        switch(dayOfWeekEnd) {
	         case 1:
	        	 calEnd.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	             break;
	         case 2:
	        	 calEnd.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
	             break;
	         case 3:
	        	 calEnd.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
	             break;
	         case 4:
	        	 calEnd.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
	             break;
	         case 5:
	        	 calEnd.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
	             break;
	         case 6:
	        	 calEnd.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
	             break;
	         case 7:
	        	 calEnd.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
	             break;            
        }
       
        switch(operatorEnd) {
	        case 0:
	       	 calEnd.set(Calendar.DAY_OF_MONTH, dayOfMonthEnd);
	            break;
	        case 1:
	       	 calEnd.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);//da rivedere
	            break;
	        case 2:
	       	 calEnd.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);    
	            break;
	        case 3:
	       	 calEnd.set(Calendar.DAY_OF_WEEK_IN_MONTH, 2);    
	            break;
	        case 4:
	       	 calEnd.set(Calendar.DAY_OF_WEEK_IN_MONTH, 3);    
	            break;
	        case 5:
	       	 calEnd.set(Calendar.DAY_OF_WEEK_IN_MONTH, 4);    
	            break;
	        case 6:
	       	 calEnd.set(Calendar.DAY_OF_WEEK_IN_MONTH, -1);    
	            break;          
	        case 7:
	       	 calEnd.set(Calendar.DAY_OF_WEEK_IN_MONTH, -1);        	
	            break;   
	   }

    
       Date datareading = gc.getTime();
       if (datareading.compareTo(calStart.getTime())>0 && datareading.compareTo(calEnd.getTime())<=0) {
    	   gc.add(java.util.Calendar.SECOND,-dstOffset);
       }
       gc.add(java.util.Calendar.SECOND,-tzOffset);
       return gc;
	}
	
	/**
	 * Applica una conversione dal formato utc + 1 .
	 * 
	 * @param gc data da aconveertire
	 * @param dstStartRule regola per inizio ora solare
	 * @param dstEndRule regola per fine ora solare
	 * @param dstOffset secondi di fuso ora solare
	 * @param tzOffset secondi da sottarre
	 * @return data convertita
	 */
	public static GregorianCalendar convertTimeFromUtc(GregorianCalendar gc,String dstStartRule,String dstEndRule,int dstOffset,int tzOffset) {
		
		//calcolo la data di inizio dell'ora solare  
		
		String bin = new BigInteger(dstStartRule, 16).toString(2);	
		//String bin = String.valueOf(binInt);
		
		while (bin.length()<32) {
			bin = "0" + bin;
		}
		
		int meseStart = Integer.parseInt(bin.substring(0,4), 2);
		
		int operatorStart = Integer.parseInt(bin.substring(4,7), 2);
		
		int dayOfMonthStart = Integer.parseInt(bin.substring(7,12), 2);
		 
		int dayOfWeekStart = Integer.parseInt(bin.substring(12,15), 2);
		 
		int hoursStart = Integer.parseInt(bin.substring(15,20), 2);		 
		 
		int secondsStart = Integer.parseInt(bin.substring(20), 2);
		
		Calendar calStart = Calendar.getInstance();
        calStart.set(Calendar.MONTH, meseStart -1);
        calStart.set(Calendar.YEAR, gc.get(Calendar.YEAR));
       
        if (secondsStart!=0) {
        	calStart.set(Calendar.HOUR_OF_DAY, hoursStart);
        	if ((secondsStart-1)!=0) {
	        	calStart.set(Calendar.MINUTE, (secondsStart-1)/60);
	        	calStart.set(Calendar.SECOND,0);
        	} else {
        		calStart.set(Calendar.MINUTE, 0);
	        	calStart.set(Calendar.SECOND,0);
        	}
        } else {
        	calStart.set(Calendar.HOUR_OF_DAY, hoursStart-1);
        	calStart.set(Calendar.MINUTE,59);
        	calStart.set(Calendar.SECOND,59);
        }
       
                     
        switch(dayOfWeekStart) {
	         case 1:
	        	 calStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	             break;
	         case 2:
	        	 calStart.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
	             break;
	         case 3:
	        	 calStart.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
	             break;
	         case 4:
	        	 calStart.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
	             break;
	         case 5:
	        	 calStart.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
	             break;
	         case 6:
	        	 calStart.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
	             break;
	         case 7:
	        	 calStart.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
	             break;            
        }
         
        switch(operatorStart) {
	         case 0:
	        	 calStart.set(Calendar.DAY_OF_MONTH, dayOfMonthStart);
	             break;
	         case 1:
	        	 calStart.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);//da rivedere
	             break;
	         case 2:
	        	 calStart.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);    
	             break;
	         case 3:
	        	 calStart.set(Calendar.DAY_OF_WEEK_IN_MONTH, 2);    
	             break;
	         case 4:
	        	 calStart.set(Calendar.DAY_OF_WEEK_IN_MONTH, 3);    
	             break;
	         case 5:
	        	 calStart.set(Calendar.DAY_OF_WEEK_IN_MONTH, 4);    
	             break;
	         case 6:
	        	 calStart.set(Calendar.DAY_OF_WEEK_IN_MONTH, -1);    
	             break;          
	         case 7:
	        	 calStart.set(Calendar.DAY_OF_WEEK_IN_MONTH, -1);        	
	             break;   
        }
     
        //calcolo la data di fine dell'ora solare  
        bin = new BigInteger(dstEndRule, 16).toString(2);
        
        while (bin.length()<32) {
			bin = "0" + bin;
		}
				
        int meseEnd = Integer.parseInt(bin.substring(0,4), 2);
		
		int operatorEnd = Integer.parseInt(bin.substring(4,7), 2);
		
		int dayOfMonthEnd = Integer.parseInt(bin.substring(7,12), 2);
		 
		int dayOfWeekEnd = Integer.parseInt(bin.substring(12,15), 2);
		 
		int hoursEnd = Integer.parseInt(bin.substring(15,20), 2);		 
		 
		int secondsEnd = Integer.parseInt(bin.substring(20), 2);
  		
  		Calendar calEnd = Calendar.getInstance();
        calEnd.set(Calendar.MONTH, meseEnd -1);       
        calEnd.set(Calendar.YEAR, gc.get(Calendar.YEAR));        
        calEnd.set(Calendar.HOUR_OF_DAY, hoursEnd);
        if (secondsEnd!=0) {
        	calEnd.set(Calendar.MINUTE, secondsEnd/60);
        	calEnd.set(Calendar.SECOND,0);
        } else {
        	calEnd.set(Calendar.MINUTE,0);
        	calEnd.set(Calendar.SECOND,0);
        }
      
        switch(dayOfWeekEnd) {
	         case 1:
	        	 calEnd.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	             break;
	         case 2:
	        	 calEnd.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
	             break;
	         case 3:
	        	 calEnd.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
	             break;
	         case 4:
	        	 calEnd.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
	             break;
	         case 5:
	        	 calEnd.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
	             break;
	         case 6:
	        	 calEnd.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
	             break;
	         case 7:
	        	 calEnd.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
	             break;            
        }
       
        switch(operatorEnd) {
	        case 0:
	       	 calEnd.set(Calendar.DAY_OF_MONTH, dayOfMonthEnd);
	            break;
	        case 1:
	       	 calEnd.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);//da rivedere
	            break;
	        case 2:
	       	 calEnd.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);    
	            break;
	        case 3:
	       	 calEnd.set(Calendar.DAY_OF_WEEK_IN_MONTH, 2);    
	            break;
	        case 4:
	       	 calEnd.set(Calendar.DAY_OF_WEEK_IN_MONTH, 3);    
	            break;
	        case 5:
	       	 calEnd.set(Calendar.DAY_OF_WEEK_IN_MONTH, 4);    
	            break;
	        case 6:
	       	 calEnd.set(Calendar.DAY_OF_WEEK_IN_MONTH, -1);    
	            break;          
	        case 7:
	       	 calEnd.set(Calendar.DAY_OF_WEEK_IN_MONTH, -1);        	
	            break;   
	   }

    
       Date datareading = gc.getTime();
       if (datareading.compareTo(calStart.getTime())>0 && datareading.compareTo(calEnd.getTime())<=0) {
    	   gc.add(java.util.Calendar.SECOND,dstOffset);
       }
       gc.add(java.util.Calendar.SECOND,tzOffset);
       return gc;
	}

}
