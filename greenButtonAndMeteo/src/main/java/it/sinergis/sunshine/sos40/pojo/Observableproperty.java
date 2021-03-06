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
 * Observableproperty generated by hbm2java
 */
@Entity
@Table(name="observableproperty"
    , uniqueConstraints = @UniqueConstraint(columnNames="identifier") 
)
public class Observableproperty  implements java.io.Serializable {


     private long observablepropertyid;
     private char hibernatediscriminator;
     private String identifier;
     private String description;
     private Set observationconstellations = new HashSet(0);
     private Set compositephenomenonsForChildobservablepropertyid = new HashSet(0);
     private Set resulttemplates = new HashSet(0);
     private Set compositephenomenonsForParentobservablepropertyid = new HashSet(0);
     private Set serieses = new HashSet(0);

    public Observableproperty() {
    }

	
    public Observableproperty(long observablepropertyid, char hibernatediscriminator, String identifier) {
        this.observablepropertyid = observablepropertyid;
        this.hibernatediscriminator = hibernatediscriminator;
        this.identifier = identifier;
    }
    public Observableproperty(long observablepropertyid, char hibernatediscriminator, String identifier, String description, Set observationconstellations, Set compositephenomenonsForChildobservablepropertyid, Set resulttemplates, Set compositephenomenonsForParentobservablepropertyid, Set serieses) {
       this.observablepropertyid = observablepropertyid;
       this.hibernatediscriminator = hibernatediscriminator;
       this.identifier = identifier;
       this.description = description;
       this.observationconstellations = observationconstellations;
       this.compositephenomenonsForChildobservablepropertyid = compositephenomenonsForChildobservablepropertyid;
       this.resulttemplates = resulttemplates;
       this.compositephenomenonsForParentobservablepropertyid = compositephenomenonsForParentobservablepropertyid;
       this.serieses = serieses;
    }
   
     @Id 

    
    @Column(name="observablepropertyid", unique=true, nullable=false)
    public long getObservablepropertyid() {
        return this.observablepropertyid;
    }
    
    public void setObservablepropertyid(long observablepropertyid) {
        this.observablepropertyid = observablepropertyid;
    }

    
    @Column(name="hibernatediscriminator", nullable=false, length=1)
    public char getHibernatediscriminator() {
        return this.hibernatediscriminator;
    }
    
    public void setHibernatediscriminator(char hibernatediscriminator) {
        this.hibernatediscriminator = hibernatediscriminator;
    }

    
    @Column(name="identifier", unique=true, nullable=false)
    public String getIdentifier() {
        return this.identifier;
    }
    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    
    @Column(name="description")
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="observableproperty")
    public Set getObservationconstellations() {
        return this.observationconstellations;
    }
    
    public void setObservationconstellations(Set observationconstellations) {
        this.observationconstellations = observationconstellations;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="unresolved")
    public Set getCompositephenomenonsForChildobservablepropertyid() {
        return this.compositephenomenonsForChildobservablepropertyid;
    }
    
    public void setCompositephenomenonsForChildobservablepropertyid(Set compositephenomenonsForChildobservablepropertyid) {
        this.compositephenomenonsForChildobservablepropertyid = compositephenomenonsForChildobservablepropertyid;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="observableproperty")
    public Set getResulttemplates() {
        return this.resulttemplates;
    }
    
    public void setResulttemplates(Set resulttemplates) {
        this.resulttemplates = resulttemplates;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="unresolved")
    public Set getCompositephenomenonsForParentobservablepropertyid() {
        return this.compositephenomenonsForParentobservablepropertyid;
    }
    
    public void setCompositephenomenonsForParentobservablepropertyid(Set compositephenomenonsForParentobservablepropertyid) {
        this.compositephenomenonsForParentobservablepropertyid = compositephenomenonsForParentobservablepropertyid;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="observableproperty")
    public Set getSerieses() {
        return this.serieses;
    }
    
    public void setSerieses(Set serieses) {
        this.serieses = serieses;
    }




}


