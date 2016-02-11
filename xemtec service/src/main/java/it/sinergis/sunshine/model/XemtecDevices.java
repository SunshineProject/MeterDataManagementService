package it.sinergis.sunshine.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Modello per la gestione dei dati dei dispositivi per xemtec.
 * 
 * @author Rossana Bambili
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "XemtecDevices.findById", query = "SELECT xd FROM XemtecDevices xd WHERE xd.id = :idDevice")
})
@Table(name = "XemtecDevices")
public class XemtecDevices implements Serializable {
	/** Seriale della classe. */
	private static final long serialVersionUID = 1L;

	/** Id device. */
	@Column(name = "DeviceID", nullable = false)
	@Id
	private Long id;


	/**
	 * Crea un nuovo oggetto dispositivo.
	 */
	public XemtecDevices() {
	}

	

	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



}