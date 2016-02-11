package it.sinergis.sunshine.sos40.pojo;

import java.io.Serializable;
// Generated 3-feb-2015 15.58.44 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "numericvalue", schema = "public")
public class Numericvalue implements Serializable {
	@Id
	@Column(name = "observationid")
	private int observationid;
	@Column(name = "value")
	private double value;
	
	public int getObservationid() {
		return observationid;
	}
	
	public void setObservationid(int observationid) {
		this.observationid = observationid;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
}
