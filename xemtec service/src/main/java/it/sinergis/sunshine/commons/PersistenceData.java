package it.sinergis.sunshine.commons;

/**
 * Bean per la gestione dei contenuti del persistence
 *
 * @author Rossana Bambili
 */
public class PersistenceData
{
	private String host = null;
	
	private String port = null;

	private String user = null;

	private String password = null;

	private String instance = null;

	private String dbtype = "oracle";
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public String getDbtype() {
		return dbtype;
	}

	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}
	
}
