/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import entity.DetalleVenta;
import entity.DetalleVentaPK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entity.Venta;
import entity.Productos;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Michael García A
 */
public class DetalleVentaJpaController implements Serializable {

    public DetalleVentaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DetalleVenta detalleVenta) throws PreexistingEntityException, Exception {
        if (detalleVenta.getDetalleVentaPK() == null) {
            detalleVenta.setDetalleVentaPK(new DetalleVentaPK());
        }
        detalleVenta.getDetalleVentaPK().setIdProducto(detalleVenta.getProductos().getIdProducto());
        detalleVenta.getDetalleVentaPK().setIdVenta(detalleVenta.getVenta().getIdVenta());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Venta venta = detalleVenta.getVenta();
            if (venta != null) {
                venta = em.getReference(venta.getClass(), venta.getIdVenta());
                detalleVenta.setVenta(venta);
            }
            Productos productos = detalleVenta.getProductos();
            if (productos != null) {
                productos = em.getReference(productos.getClass(), productos.getIdProducto());
                detalleVenta.setProductos(productos);
            }
            em.persist(detalleVenta);
            if (venta != null) {
                venta.getDetalleVentaList().add(detalleVenta);
                venta = em.merge(venta);
            }
            if (productos != null) {
                productos.getDetalleVentaList().add(detalleVenta);
                productos = em.merge(productos);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDetalleVenta(detalleVenta.getDetalleVentaPK()) != null) {
                throw new PreexistingEntityException("DetalleVenta " + detalleVenta + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DetalleVenta detalleVenta) throws NonexistentEntityException, Exception {
        detalleVenta.getDetalleVentaPK().setIdProducto(detalleVenta.getProductos().getIdProducto());
        detalleVenta.getDetalleVentaPK().setIdVenta(detalleVenta.getVenta().getIdVenta());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DetalleVenta persistentDetalleVenta = em.find(DetalleVenta.class, detalleVenta.getDetalleVentaPK());
            Venta ventaOld = persistentDetalleVenta.getVenta();
            Venta ventaNew = detalleVenta.getVenta();
            Productos productosOld = persistentDetalleVenta.getProductos();
            Productos productosNew = detalleVenta.getProductos();
            if (ventaNew != null) {
                ventaNew = em.getReference(ventaNew.getClass(), ventaNew.getIdVenta());
                detalleVenta.setVenta(ventaNew);
            }
            if (productosNew != null) {
                productosNew = em.getReference(productosNew.getClass(), productosNew.getIdProducto());
                detalleVenta.setProductos(productosNew);
            }
            detalleVenta = em.merge(detalleVenta);
            if (ventaOld != null && !ventaOld.equals(ventaNew)) {
                ventaOld.getDetalleVentaList().remove(detalleVenta);
                ventaOld = em.merge(ventaOld);
            }
            if (ventaNew != null && !ventaNew.equals(ventaOld)) {
                ventaNew.getDetalleVentaList().add(detalleVenta);
                ventaNew = em.merge(ventaNew);
            }
            if (productosOld != null && !productosOld.equals(productosNew)) {
                productosOld.getDetalleVentaList().remove(detalleVenta);
                productosOld = em.merge(productosOld);
            }
            if (productosNew != null && !productosNew.equals(productosOld)) {
                productosNew.getDetalleVentaList().add(detalleVenta);
                productosNew = em.merge(productosNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                DetalleVentaPK id = detalleVenta.getDetalleVentaPK();
                if (findDetalleVenta(id) == null) {
                    throw new NonexistentEntityException("The detalleVenta with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(DetalleVentaPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DetalleVenta detalleVenta;
            try {
                detalleVenta = em.getReference(DetalleVenta.class, id);
                detalleVenta.getDetalleVentaPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detalleVenta with id " + id + " no longer exists.", enfe);
            }
            Venta venta = detalleVenta.getVenta();
            if (venta != null) {
                venta.getDetalleVentaList().remove(detalleVenta);
                venta = em.merge(venta);
            }
            Productos productos = detalleVenta.getProductos();
            if (productos != null) {
                productos.getDetalleVentaList().remove(detalleVenta);
                productos = em.merge(productos);
            }
            em.remove(detalleVenta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DetalleVenta> findDetalleVentaEntities() {
        return findDetalleVentaEntities(true, -1, -1);
    }

    public List<DetalleVenta> findDetalleVentaEntities(int maxResults, int firstResult) {
        return findDetalleVentaEntities(false, maxResults, firstResult);
    }

    private List<DetalleVenta> findDetalleVentaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DetalleVenta.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public DetalleVenta findDetalleVenta(DetalleVentaPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DetalleVenta.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetalleVentaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DetalleVenta> rt = cq.from(DetalleVenta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
