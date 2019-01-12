/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Michael García A
 */
@Entity
@Table(name = "tipo_persona")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TipoPersona.findAll", query = "SELECT t FROM TipoPersona t")
    , @NamedQuery(name = "TipoPersona.findByIdTipoPersona", query = "SELECT t FROM TipoPersona t WHERE t.idTipoPersona = :idTipoPersona")
    , @NamedQuery(name = "TipoPersona.findByNombreTipoPersona", query = "SELECT t FROM TipoPersona t WHERE t.nombreTipoPersona = :nombreTipoPersona")})
public class TipoPersona implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_TIPO_PERSONA")
    private Integer idTipoPersona;
    @Column(name = "NOMBRE_TIPO_PERSONA")
    private String nombreTipoPersona;
    @JoinTable(name = "tipopersona_persona", joinColumns = {
        @JoinColumn(name = "ID_TIPO_PERSONA", referencedColumnName = "ID_TIPO_PERSONA")}, inverseJoinColumns = {
        @JoinColumn(name = "ID_PERSONA", referencedColumnName = "ID_PERSONA")})
    @ManyToMany
    private List<Persona> personaList;

    public TipoPersona() {
    }

    public TipoPersona(Integer idTipoPersona) {
        this.idTipoPersona = idTipoPersona;
    }

    public Integer getIdTipoPersona() {
        return idTipoPersona;
    }

    public void setIdTipoPersona(Integer idTipoPersona) {
        this.idTipoPersona = idTipoPersona;
    }

    public String getNombreTipoPersona() {
        return nombreTipoPersona;
    }

    public void setNombreTipoPersona(String nombreTipoPersona) {
        this.nombreTipoPersona = nombreTipoPersona;
    }

    @XmlTransient
    public List<Persona> getPersonaList() {
        return personaList;
    }

    public void setPersonaList(List<Persona> personaList) {
        this.personaList = personaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTipoPersona != null ? idTipoPersona.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TipoPersona)) {
            return false;
        }
        TipoPersona other = (TipoPersona) object;
        if ((this.idTipoPersona == null && other.idTipoPersona != null) || (this.idTipoPersona != null && !this.idTipoPersona.equals(other.idTipoPersona))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.TipoPersona[ idTipoPersona=" + idTipoPersona + " ]";
    }
    
}
