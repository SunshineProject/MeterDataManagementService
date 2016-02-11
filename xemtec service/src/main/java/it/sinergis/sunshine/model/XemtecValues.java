package it.sinergis.sunshine.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Modello per la gestione dei dati dei dispositivi per xemtec.
 * 
 * @author Rossana Bambili
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "XemtecValues.findByDate", query = "SELECT xv FROM XemtecValues xv WHERE xv.deviceId = :idDevice AND xv.readDate> :startDate ORDER BY xv.readDate ASC"),
	@NamedQuery(name = "XemtecValues.findByIdDevice", query = "SELECT xv FROM XemtecValues xv WHERE xv.deviceId = :idDevice ORDER BY xv.readDate ASC")
})
@Table(name = "XemtecValues")
public class XemtecValues implements Serializable {
	/** Seriale della classe. */
	private static final long serialVersionUID = 1L;

	/** Value ID. */
	@Column(name = "ValueID", nullable = false)
	@Id
	private Long id;
	
	/** Id device. */
	@Column(name = "DeviceID", nullable = false)
	private Long deviceId;
	
	/** Read Value. */
	@Column(name = "ReadValue", nullable = false)
	private Long readValue;
	
	/** Formatted Value. */
	@Column(name = "FormattedValue", nullable = false)
	private String formattedValue;
	
	
	/** Read Date. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ReadDate", nullable = false)
	private Date readDate;

	/**
	 * Crea un nuovo oggetto values per il dispositivo associato.
	 */
	public XemtecValues() {
	}

	

	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public Long getDeviceId() {
		return deviceId;
	}



	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}



	public Long getReadValue() {
		return readValue;
	}



	public void setReadValue(Long readValue) {
		this.readValue = readValue;
	}



	public Date getReadDate() {
		return readDate;
	}



	public void setReadDate(Date readDate) {
		this.readDate = readDate;
	}



	public String getFormattedValue() {
		return formattedValue;
	}



	public void setFormattedValue(String formattedValue) {
		this.formattedValue = formattedValue;
	}
	
	
}