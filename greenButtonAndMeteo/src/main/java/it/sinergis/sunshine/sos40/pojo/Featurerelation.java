package it.sinergis.sunshine.sos40.pojo;

// Generated 3-feb-2015 15.58.44 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Featurerelation generated by hbm2java
 */
@Entity
@Table(name = "featurerelation")
public class Featurerelation implements java.io.Serializable {
	
	private long id;
	
	public Featurerelation() {
	}
	
	public Featurerelation(long id) {
		this.id = id;
	}
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "featureofinterest", column = @Column(name = "childfeatureid", nullable = false)),
			@AttributeOverride(name = "featureofinterest_1", column = @Column(name = "parentfeatureid", nullable = false)) })
	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
}
