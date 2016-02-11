package it.sinergis.sunshine.servlet;

import it.sinergis.sunshine.commons.SchedulerTaskHelper;
import it.sinergis.sunshine.util.ConfigValues;

import java.text.ParseException;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 * CodaProcessiServlet, che gestisce la coda processi.
 * 
 * @author Rossana
 */
public class CodaProcessiServlet extends HttpServlet {
	
	/**
	 * Id per serializzazione.
	 */
	private static final long serialVersionUID = 1L;
	
	/** Logger. */
	private Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * Lo scheduler del processo.
	 */
	private Scheduler scheduler;
	
	@Override
	public void init(ServletConfig config) {
		if (logger.isInfoEnabled()) {
			logger.info("Starting CodaProcessiServlet");
		}
		schedulerStart();
	}
	
	@Override
	public void destroy() {
		if (logger.isInfoEnabled()) {
			logger.info("Destroying CodaProcessiServlet");
		}
		schedulerStop();
	}
	
	private void schedulerStop() {
		try {
			if (scheduler != null) {
				scheduler.shutdown(false);
				//Necessario per lo shutdown completo dei worker prima dello spegnimento del server
				Thread.sleep(1000);
			}
		}
		catch (SchedulerException se) {
			logger.error("Exception in stopping the scheduler", se);
		}
		catch (InterruptedException ie) {
			logger.error("Exception in stopping the scheduler", ie);
		}
	}
	
	/**
	 * Funzione che fa partire lo scheduler.
	 */
	private void schedulerStart() {
		if (logger.isInfoEnabled()) {
			logger.info("starting scheduler..");
		}
		
		try {
			SchedulerFactory factory = new StdSchedulerFactory();
			scheduler = factory.getScheduler();
			
			String defaultGroup = Scheduler.DEFAULT_GROUP;
			scheduler.start();
			
			if ("true".equalsIgnoreCase(ConfigValues.XEMTEC_TO_GREEN_BUTTON_ATTIVO)
					|| "1".equalsIgnoreCase(ConfigValues.XEMTEC_TO_GREEN_BUTTON_ATTIVO)) {
				JobDetail jobDetail = new JobDetail("CreaIntervalBlock", defaultGroup, SchedulerTaskHelper.class);
				
				CronTrigger trigger = new CronTrigger("myCronTrigger", defaultGroup, "CreaIntervalBlock", defaultGroup,
						buildPattern());
				
				Date inizioScheduling = scheduler.scheduleJob(jobDetail, trigger);
				
				if (logger.isInfoEnabled()) {
					logger.info("Data inizio: " + inizioScheduling);
				}
			}
			
		}
		catch (SchedulerException se) {
			logger.error("Scheduler exception", se);
			schedulerStop();
		}
		catch (ParseException pe) {
			logger.error("Errore nel parsing del pattern per lo scheduling", pe);
		}
	}
	
	/**
	 * Legge la configurazione dal file di properties e costruisce il pattern.
	 * 
	 * @return il pattern del timestamp utilizzato nello scheduler.
	 */
	private String buildPattern() {
		String seconds = ConfigValues.SECONDO_SCHEDULING;
		String minutes = ConfigValues.MINUTO_SCHEDULING;
		String hours = ConfigValues.ORA_SCHEDULING;
		
		StringBuilder builder = new StringBuilder();
		builder.append(seconds);
		builder.append(" ");
		builder.append(minutes);
		builder.append(" ");
		builder.append(hours);
		builder.append(" * * ?");
		
		return builder.toString();
	}
	
}
