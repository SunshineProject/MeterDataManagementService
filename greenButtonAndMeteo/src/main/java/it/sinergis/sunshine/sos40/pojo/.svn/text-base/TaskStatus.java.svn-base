package it.sinergis.sunshine.sos40.pojo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "task_status", schema = "schedulingreport")
public class TaskStatus {
	public static final String COMPLETE = "complete";
	public static final String START = "start";
	public static final String NODATA = "end";
	public static final String RUNNING = "running";
	public static final String ERROR = "error";
	
	public static final int GRENBUTTONID = 3;
	public static final int METEOID = 2;
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "start_process")
	private Date startProcess;
	
	public Date getLastUpdate() {
		return lastUpdate;
	}
	
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	@Column(name = "last_update")
	private Date lastUpdate;
	
	@Column(name = "end_process")
	private Date endProcess;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "id_process")
	private long idProcess;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public Date getStartProcess() {
		return startProcess;
	}
	
	public void setStartProcess(Date startProcess) {
		this.startProcess = startProcess;
	}
	
	public Date getEndProcess() {
		return endProcess;
	}
	
	public void setEndProcess(Date endProcess) {
		this.endProcess = endProcess;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public long getIdProcess() {
		return idProcess;
	}
	
	public void setIdProcess(long idprocces) {
		this.idProcess = idprocces;
	}
	
}
