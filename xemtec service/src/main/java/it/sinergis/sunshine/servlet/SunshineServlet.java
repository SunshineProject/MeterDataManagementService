package it.sinergis.sunshine.servlet;

import it.sinergis.sunshine.service.CreateFileCsv;
import it.sinergis.sunshine.service.CreateIntervalBlocks;
import it.sinergis.sunshine.service.CreazioneIntervalBlocks;
import it.sinergis.sunshine.service.ReadUsagePoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class SunshineServlet extends HttpServlet {

	private Logger logger;

	private static final long serialVersionUID = -2813156894006182978L;

	public SunshineServlet() {
		super();
	}

	@Override
	public void destroy() {
		super.destroy();

	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("actionName");
		String datastart = request.getParameter("datastart");
		String path = request.getParameter("path");
		String usageString = request.getParameter("usage");
		String publishedmin = request.getParameter("published-min");
		String publishedmax = request.getParameter("published-max");
		String idusagepoint = request.getParameter("idusagepoint");
		Long idUsagePoint = null;
		
		if (idusagepoint!=null && !idusagepoint.equals("")) {
			idUsagePoint = Long.valueOf(idusagepoint);
		}
	
		String idretailcustomer = request.getParameter("idretailcustomer");
		Long idRetailCustomer = null;
		
		if (idretailcustomer!=null && !idretailcustomer.equals("")) {
			idRetailCustomer = Long.valueOf(idretailcustomer);
		}
	
		InputStream is = request.getInputStream();
		String risposta = "";
		PrintWriter out = null;
		response.setContentType("text/xml; charset=UTF-8");
		try {		
			if (action.equalsIgnoreCase("CreateIntervalBlocksFromCsv")) {
				CreazioneIntervalBlocks creazioneIntervalBlocks = CreazioneIntervalBlocks.getInstance();
				risposta = creazioneIntervalBlocks.insertIntervalBlock();
				out = response.getWriter();
		        out.write(risposta);
		        out.flush();
		        out.close();
			}
			if (action.equalsIgnoreCase("CreateIntervalBlocks")) {
				CreateIntervalBlocks creazioneIntervalBlocks = CreateIntervalBlocks.getInstance();
				risposta = creazioneIntervalBlocks.insertIntervalBlock();
				out = response.getWriter();
		        out.write(risposta);
		        out.flush();
		        out.close();
			}
			if (action.equalsIgnoreCase("CreateIntervalBlockFromXml")) {
				if (is!=null) {
					String richiesta = this.fromStream(is);
					CreazioneIntervalBlocks creazioneIntervalBlocks = CreazioneIntervalBlocks.getInstance();
					risposta = creazioneIntervalBlocks.insertIntervalBlockFromXml(richiesta);
				}	else {
					CreazioneIntervalBlocks creazioneIntervalBlocks = CreazioneIntervalBlocks.getInstance();
					risposta = creazioneIntervalBlocks.insertIntervalBlockFromXml(null);
				}
				out = response.getWriter();
		        out.write(risposta);
		        out.flush();
		        out.close();
			}
			if (action.equalsIgnoreCase("CreateFileCsv")) {
				CreateFileCsv createFileCsv = new CreateFileCsv();
				if (datastart==null || datastart.equals("") || path==null || path.equals("") || usageString==null || usageString.equals("")) {
					risposta = createFileCsv.execute(null, path, null);
				} else {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					Date dateFirst = df.parse(datastart);
					Double usage = Double.valueOf(usageString);
					risposta = createFileCsv.execute(dateFirst, path, usage);
				}	
				out = response.getWriter();
		        out.write(risposta);
		        out.flush();
		        out.close();	
			}
			if (action.equalsIgnoreCase("ReadUsagePoint")) {
				ReadUsagePoint readUsagePoint = new ReadUsagePoint();
				risposta = readUsagePoint.execute(publishedmin, publishedmax,idUsagePoint,idRetailCustomer);
				if (risposta.indexOf("ReadUsagePointResponse")==-1) {
					  response.setContentType("application/x-download");
	                  response.setHeader("Content-Disposition", "attachment; filename=GreenButtonDownload.xml");
	                  out = new PrintWriter(response.getOutputStream());
                      out.write(risposta);
                      out.flush();
				} else {
					  out = response.getWriter();
			          out.write(risposta);
			          out.flush();
			          out.close();
				}
			}				
			
		} catch (Exception e) {			
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
	}
	
	public String fromStream(InputStream in) throws IOException
	{
	    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	    StringBuilder out = new StringBuilder();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        out.append(line);       
	    }
	    return out.toString();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		logger = Logger.getLogger(this.getClass());
	}


}
