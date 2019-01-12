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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Michael García A
 */
@Entity
@Table(name = "estrato")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Estrato.findAll", query = "SELECT e FROM Estrato e")
    , @NamedQuery(name = "Estrato.findByIdEstrato", query = "SELECT e FROM Estrato e WHERE e.idEstrato = :idEstrato")
    , @NamedQuery(name = "Estrato.findByNumEstrato", query = "SELECT e FROM Estrato e WHERE e.numEstrato = :numEstrato")})
public class Estrato implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_ESTRATO")
    private Integer idEstrato;
    @Column(name = "NUM_ESTRATO")
    private Integer numEstrato;
    @OneToMany(mappedBy = "idEstrato")
    private List<Cliente> clienteList;

    public Estrato() {
    }

    public Estrato(Integer idEstrato) {
        this.idEstrato = idEstrato;
    }

    public Integer getIdEstrato() {
        return idEstrato;
    }

    public void setIdEstrato(Integer idEstrato) {
        this.idEstrato = idEstrato;
    }

    public Integer getNumEstrato() {
        return numEstrato;
    }

    public void setNumEstrato(Integer numEstrato) {
        this.numEstrato = numEstrato;
    }

    @XmlTransient
    public List<Cliente> getClienteList() {
        return clienteList;
    }

    public void setClienteList(List<Cliente> clienteList) {
        this.clienteList = clienteList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idEstrato != null ? idEstrato.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Estrato)) {
            return false;
        }
        Estrato other = (Estrato) object;
        if ((this.idEstrato == null && other.idEstrato != null) || (this.idEstrato != null && !this.idEstrato.equals(other.idEstrato))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Estrato[ idEstrato=" + idEstrato + " ]";
    }
    
}
