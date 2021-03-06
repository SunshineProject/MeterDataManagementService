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

/**
 * Proceduredescriptionformat generated by hbm2java
 */
@Entity
@Table(name="proceduredescriptionformat"
)
public class Proceduredescriptionformat  implements java.io.Serializable {


     private long proceduredescriptionformatid;
     private String proceduredescriptionformat;
     private Set validproceduretimes = new HashSet(0);
     private Set procedures = new HashSet(0);

    public Proceduredescriptionformat() {
    }

	
    public Proceduredescriptionformat(long proceduredescriptionformatid, String proceduredescriptionformat) {
        this.proceduredescriptionformatid = proceduredescriptionformatid;
        this.proceduredescriptionformat = proceduredescriptionformat;
    }
    public Proceduredescriptionformat(long proceduredescriptionformatid, String proceduredescriptionformat, Set validproceduretimes, Set procedures) {
       this.proceduredescriptionformatid = proceduredescriptionformatid;
       this.proceduredescriptionformat = proceduredescriptionformat;
       this.validproceduretimes = validproceduretimes;
       this.procedures = procedures;
    }
   
     @Id 

    
    @Column(name="proceduredescriptionformatid", unique=true, nullable=false)
    public long getProceduredescriptionformatid() {
        return this.proceduredescriptionformatid;
    }
    
    public void setProceduredescriptionformatid(long proceduredescriptionformatid) {
        this.proceduredescriptionformatid = proceduredescriptionformatid;
    }

    
    @Column(name="proceduredescriptionformat", nullable=false)
    public String getProceduredescriptionformat() {
        return this.proceduredescriptionformat;
    }
    
    public void setProceduredescriptionformat(String proceduredescriptionformat) {
        this.proceduredescriptionformat = proceduredescriptionformat;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="proceduredescriptionformat")
    public Set getValidproceduretimes() {
        return this.validproceduretimes;
    }
    
    public void setValidproceduretimes(Set validproceduretimes) {
        this.validproceduretimes = validproceduretimes;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="proceduredescriptionformat")
    public Set getProcedures() {
        return this.procedures;
    }
    
    public void setProcedures(Set procedures) {
        this.procedures = procedures;
    }




}


