package it.sinergis.sunshine.sos40.pojo;
// Generated 3-feb-2015 15.58.44 by Hibernate Tools 3.4.0.CR1


import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Featureofinteresttype generated by hbm2java
 */
@Entity
@Table(name="featureofinteresttype"
    , uniqueConstraints = @UniqueConstraint(columnNames="featureofinteresttype") 
)
public class Featureofinteresttype  implements java.io.Serializable {


     private long featureofinteresttypeid;
     private String featureofinteresttype;
     private Set offeringallowedfeaturetypes = new HashSet(0);
     private Set featureofinterests = new HashSet(0);

    public Featureofinteresttype() {
    }

	
    public Featureofinteresttype(long featureofinteresttypeid, String featureofinteresttype) {
        this.featureofinteresttypeid = featureofinteresttypeid;
        this.featureofinteresttype = featureofinteresttype;
    }
    public Featureofinteresttype(long featureofinteresttypeid, String featureofinteresttype, Set offeringallowedfeaturetypes, Set featureofinterests) {
       this.featureofinteresttypeid = featureofinteresttypeid;
       this.featureofinteresttype = featureofinteresttype;
       this.offeringallowedfeaturetypes = offeringallowedfeaturetypes;
       this.featureofinterests = featureofinterests;
    }
   
     @Id 

    
    @Column(name="featureofinteresttypeid", unique=true, nullable=false)
    public long getFeatureofinteresttypeid() {
        return this.featureofinteresttypeid;
    }
    
    public void setFeatureofinteresttypeid(long featureofinteresttypeid) {
        this.featureofinteresttypeid = featureofinteresttypeid;
    }

    
    @Column(name="featureofinteresttype", unique=true, nullable=false)
    public String getFeatureofinteresttype() {
        return this.featureofinteresttype;
    }
    
    public void setFeatureofinteresttype(String featureofinteresttype) {
        this.featureofinteresttype = featureofinteresttype;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="unresolved")
    public Set getOfferingallowedfeaturetypes() {
        return this.offeringallowedfeaturetypes;
    }
    
    public void setOfferingallowedfeaturetypes(Set offeringallowedfeaturetypes) {
        this.offeringallowedfeaturetypes = offeringallowedfeaturetypes;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="featureofinteresttype")
    public Set getFeatureofinterests() {
        return this.featureofinterests;
    }
    
    public void setFeatureofinterests(Set featureofinterests) {
        this.featureofinterests = featureofinterests;
    }




}


