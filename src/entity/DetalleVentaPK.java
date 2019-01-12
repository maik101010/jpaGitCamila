/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Michael García A
 */
@Embeddable
public class DetalleVentaPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "ID_PRODUCTO")
    private int idProducto;
    @Basic(optional = false)
    @Column(name = "ID_VENTA")
    private int idVenta;

    public DetalleVentaPK() {
    }

    public DetalleVentaPK(int idProducto, int idVenta) {
        this.idProducto = idProducto;
        this.idVenta = idVenta;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idProducto;
        hash += (int) idVenta;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DetalleVentaPK)) {
            return false;
        }
        DetalleVentaPK other = (DetalleVentaPK) object;
        if (this.idProducto != other.idProducto) {
            return false;
        }
        if (this.idVenta != other.idVenta) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.DetalleVentaPK[ idProducto=" + idProducto + ", idVenta=" + idVenta + " ]";
    }
    
}
