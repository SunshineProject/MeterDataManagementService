package it.sinergis.sunshine.sos40.pojo;

// Generated 3-feb-2015 15.58.44 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Offeringallowedobservationtype generated by hbm2java
 */
@Entity
@Table(name = "offeringallowedobservationtype")
public class Offeringallowedobservationtype implements java.io.Serializable {
	
	private long id;
	
	public Offeringallowedobservationtype() {
	}
	
	public Offeringallowedobservationtype(long id) {
		this.id = id;
	}
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "offering", column = @Column(name = "offeringid", nullable = false)),
			@AttributeOverride(name = "observationtype", column = @Column(name = "observationtypeid", nullable = false)) })
	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
}