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
 * Codespace generated by hbm2java
 */
@Entity
@Table(name="codespace"
    , uniqueConstraints = @UniqueConstraint(columnNames="codespace") 
)
public class Codespace  implements java.io.Serializable {


     private long codespaceid;
     private String codespace;
     private Set featureofinterests = new HashSet(0);
     private Set observations = new HashSet(0);

    public Codespace() {
    }

	
    public Codespace(long codespaceid, String codespace) {
        this.codespaceid = codespaceid;
        this.codespace = codespace;
    }
    public Codespace(long codespaceid, String codespace, Set featureofinterests, Set observations) {
       this.codespaceid = codespaceid;
       this.codespace = codespace;
       this.featureofinterests = featureofinterests;
       this.observations = observations;
    }
   
     @Id 

    
    @Column(name="codespaceid", unique=true, nullable=false)
    public long getCodespaceid() {
        return this.codespaceid;
    }
    
    public void setCodespaceid(long codespaceid) {
        this.codespaceid = codespaceid;
    }

    
    @Column(name="codespace", unique=true, nullable=false)
    public String getCodespace() {
        return this.codespace;
    }
    
    public void setCodespace(String codespace) {
        this.codespace = codespace;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="codespace")
    public Set getFeatureofinterests() {
        return this.featureofinterests;
    }
    
    public void setFeatureofinterests(Set featureofinterests) {
        this.featureofinterests = featureofinterests;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="codespace")
    public Set getObservations() {
        return this.observations;
    }
    
    public void setObservations(Set observations) {
        this.observations = observations;
    }




}


