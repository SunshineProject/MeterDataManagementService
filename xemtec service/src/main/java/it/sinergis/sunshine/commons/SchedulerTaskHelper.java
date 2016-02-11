package it.sinergis.sunshine.commons;

import it.sinergis.sunshine.service.CreateIntervalBlocks;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Inner class che implementa Job.
 * 
 * @author Rossana
 * 
 */
public class SchedulerTaskHelper implements Job {

	/** Logger. */
	private Logger logger = Logger.getLogger(this.getClass());


	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		String jobName = context.getJobDetail().getFullName();

		if (logger.isInfoEnabled()) {
			logger.info("Sto eseguendo " + jobName + " alle " + new Date());
		}

		CreateIntervalBlocks richiesta = CreateIntervalBlocks.getInstance();

		richiesta.insertIntervalBlock();

	}
}
