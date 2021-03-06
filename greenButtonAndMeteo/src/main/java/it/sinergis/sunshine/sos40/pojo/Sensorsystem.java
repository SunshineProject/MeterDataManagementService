package it.sinergis.sunshine.sos40.pojo;

// Generated 3-feb-2015 15.58.44 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Sensorsystem generated by hbm2java
 */
@Entity
@Table(name = "sensorsystem")
public class Sensorsystem implements java.io.Serializable {
	
	private long id;
	
	public Sensorsystem() {
	}
	
	public Sensorsystem(long id) {
		this.id = id;
	}
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "procedure", column = @Column(name = "childsensorid", nullable = false)),
			@AttributeOverride(name = "procedure_1", column = @Column(name = "parentsensorid", nullable = false)) })
	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
}
