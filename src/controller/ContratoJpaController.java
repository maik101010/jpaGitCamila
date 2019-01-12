/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entity.Productos;
import entity.Proveedor;
import entity.Administrador;
import entity.Contrato;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Michael García A
 */
public class ContratoJpaController implements Serializable {

    public ContratoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Contrato contrato) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Productos idProducto = contrato.getIdProducto();
            if (idProducto != null) {
                idProducto = em.getReference(idProducto.getClass(), idProducto.getIdProducto());
                contrato.setIdProducto(idProducto);
            }
            Proveedor idProveedor = contrato.getIdProveedor();
            if (idProveedor != null) {
                idProveedor = em.getReference(idProveedor.getClass(), idProveedor.getIdProveedor());
                contrato.setIdProveedor(idProveedor);
            }
            Administrador idAdministrador = contrato.getIdAdministrador();
            if (idAdministrador != null) {
                idAdministrador = em.getReference(idAdministrador.getClass(), idAdministrador.getIdAdministrador());
                contrato.setIdAdministrador(idAdministrador);
            }
            em.persist(contrato);
            if (idProducto != null) {
                idProducto.getContratoList().add(contrato);
                idProducto = em.merge(idProducto);
            }
            if (idProveedor != null) {
                idProveedor.getContratoList().add(contrato);
                idProveedor = em.merge(idProveedor);
            }
            if (idAdministrador != null) {
                idAdministrador.getContratoList().add(contrato);
                idAdministrador = em.merge(idAdministrador);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Contrato contrato) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Contrato persistentContrato = em.find(Contrato.class, contrato.getIdContrato());
            Productos idProductoOld = persistentContrato.getIdProducto();
            Productos idProductoNew = contrato.getIdProducto();
            Proveedor idProveedorOld = persistentContrato.getIdProveedor();
            Proveedor idProveedorNew = contrato.getIdProveedor();
            Administrador idAdministradorOld = persistentContrato.getIdAdministrador();
            Administrador idAdministradorNew = contrato.getIdAdministrador();
            if (idProductoNew != null) {
                idProductoNew = em.getReference(idProductoNew.getClass(), idProductoNew.getIdProducto());
                contrato.setIdProducto(idProductoNew);
            }
            if (idProveedorNew != null) {
                idProveedorNew = em.getReference(idProveedorNew.getClass(), idProveedorNew.getIdProveedor());
                contrato.setIdProveedor(idProveedorNew);
            }
            if (idAdministradorNew != null) {
                idAdministradorNew = em.getReference(idAdministradorNew.getClass(), idAdministradorNew.getIdAdministrador());
                contrato.setIdAdministrador(idAdministradorNew);
            }
            contrato = em.merge(contrato);
            if (idProductoOld != null && !idProductoOld.equals(idProductoNew)) {
                idProductoOld.getContratoList().remove(contrato);
                idProductoOld = em.merge(idProductoOld);
            }
            if (idProductoNew != null && !idProductoNew.equals(idProductoOld)) {
                idProductoNew.getContratoList().add(contrato);
                idProductoNew = em.merge(idProductoNew);
            }
            if (idProveedorOld != null && !idProveedorOld.equals(idProveedorNew)) {
                idProveedorOld.getContratoList().remove(contrato);
                idProveedorOld = em.merge(idProveedorOld);
            }
            if (idProveedorNew != null && !idProveedorNew.equals(idProveedorOld)) {
                idProveedorNew.getContratoList().add(contrato);
                idProveedorNew = em.merge(idProveedorNew);
            }
            if (idAdministradorOld != null && !idAdministradorOld.equals(idAdministradorNew)) {
                idAdministradorOld.getContratoList().remove(contrato);
                idAdministradorOld = em.merge(idAdministradorOld);
            }
            if (idAdministradorNew != null && !idAdministradorNew.equals(idAdministradorOld)) {
                idAdministradorNew.getContratoList().add(contrato);
                idAdministradorNew = em.merge(idAdministradorNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = contrato.getIdContrato();
                if (findContrato(id) == null) {
                    throw new NonexistentEntityException("The contrato with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Contrato contrato;
            try {
                contrato = em.getReference(Contrato.class, id);
                contrato.getIdContrato();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The contrato with id " + id + " no longer exists.", enfe);
            }
            Productos idProducto = contrato.getIdProducto();
            if (idProducto != null) {
                idProducto.getContratoList().remove(contrato);
                idProducto = em.merge(idProducto);
            }
            Proveedor idProveedor = contrato.getIdProveedor();
            if (idProveedor != null) {
                idProveedor.getContratoList().remove(contrato);
                idProveedor = em.merge(idProveedor);
            }
            Administrador idAdministrador = contrato.getIdAdministrador();
            if (idAdministrador != null) {
                idAdministrador.getContratoList().remove(contrato);
                idAdministrador = em.merge(idAdministrador);
            }
            em.remove(contrato);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Contrato> findContratoEntities() {
        return findContratoEntities(true, -1, -1);
    }

    public List<Contrato> findContratoEntities(int maxResults, int firstResult) {
        return findContratoEntities(false, maxResults, firstResult);
    }

    private List<Contrato> findContratoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Contrato.class));
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

    public Contrato findContrato(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Contrato.class, id);
        } finally {
            em.close();
        }
    }

    public int getContratoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Contrato> rt = cq.from(Contrato.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
