package it.sinergis.sunshine.sos40.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "observationhasoffering", schema = "public")
public class Observationhasoffering implements Serializable {
	@Id
	@Column(name = "observationid")
	private int observationid;
	@Id
	@Column(name = "offeringid")
	private int offeringid;
	
	public int getObservationid() {
		return observationid;
	}
	
	public void setObservationid(int observationid) {
		this.observationid = observationid;
	}
	
	public int getOfferingid() {
		return offeringid;
	}
	
	public void setOfferingid(int offeringid) {
		this.offeringid = offeringid;
	}
}