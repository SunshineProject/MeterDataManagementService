package it.sinergis.sunshine.servlet;

import it.sinergis.sunshine.service.CreateFileCsvFromGreenButton;
import it.sinergis.sunshine.service.ImportMeteoDataSOS;

import java.io.IOException;
import java.io.PrintWriter;

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
		
		String risposta = "";
		PrintWriter out = null;
		response.setContentType("text/xml; charset=UTF-8");
		try {		
		
			if (action.equalsIgnoreCase("CreateFileCsvFromGreenButton")) {
				CreateFileCsvFromGreenButton createFileCsvFromGreenButton = CreateFileCsvFromGreenButton.getInstance();
				risposta = createFileCsvFromGreenButton.createFileCsv();
				out = response.getWriter();
		        out.write(risposta);
		        out.flush();
		        out.close();
			}
		
			if (action.equalsIgnoreCase("ImportMeteoDataSOS")) {
				ImportMeteoDataSOS importMeteoDataSOS = ImportMeteoDataSOS.getInstance();
				risposta = importMeteoDataSOS.insertObservations();
				out = response.getWriter();
		        out.write(risposta);
		        out.flush();
		        out.close();
			}
			
		} catch (Exception e) {			
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
	}	
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		logger = Logger.getLogger(this.getClass());
	}


}
