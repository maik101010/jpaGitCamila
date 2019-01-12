/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entity.Proveedor;
import entity.Contrato;
import java.util.ArrayList;
import java.util.List;
import entity.DetalleVenta;
import entity.Productos;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Michael García A
 */
public class ProductosJpaController implements Serializable {

    public ProductosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Productos productos) {
        if (productos.getContratoList() == null) {
            productos.setContratoList(new ArrayList<Contrato>());
        }
        if (productos.getDetalleVentaList() == null) {
            productos.setDetalleVentaList(new ArrayList<DetalleVenta>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Proveedor idProveedor = productos.getIdProveedor();
            if (idProveedor != null) {
                idProveedor = em.getReference(idProveedor.getClass(), idProveedor.getIdProveedor());
                productos.setIdProveedor(idProveedor);
            }
            List<Contrato> attachedContratoList = new ArrayList<Contrato>();
            for (Contrato contratoListContratoToAttach : productos.getContratoList()) {
                contratoListContratoToAttach = em.getReference(contratoListContratoToAttach.getClass(), contratoListContratoToAttach.getIdContrato());
                attachedContratoList.add(contratoListContratoToAttach);
            }
            productos.setContratoList(attachedContratoList);
            List<DetalleVenta> attachedDetalleVentaList = new ArrayList<DetalleVenta>();
            for (DetalleVenta detalleVentaListDetalleVentaToAttach : productos.getDetalleVentaList()) {
                detalleVentaListDetalleVentaToAttach = em.getReference(detalleVentaListDetalleVentaToAttach.getClass(), detalleVentaListDetalleVentaToAttach.getDetalleVentaPK());
                attachedDetalleVentaList.add(detalleVentaListDetalleVentaToAttach);
            }
            productos.setDetalleVentaList(attachedDetalleVentaList);
            em.persist(productos);
            if (idProveedor != null) {
                idProveedor.getProductosList().add(productos);
                idProveedor = em.merge(idProveedor);
            }
            for (Contrato contratoListContrato : productos.getContratoList()) {
                Productos oldIdProductoOfContratoListContrato = contratoListContrato.getIdProducto();
                contratoListContrato.setIdProducto(productos);
                contratoListContrato = em.merge(contratoListContrato);
                if (oldIdProductoOfContratoListContrato != null) {
                    oldIdProductoOfContratoListContrato.getContratoList().remove(contratoListContrato);
                    oldIdProductoOfContratoListContrato = em.merge(oldIdProductoOfContratoListContrato);
                }
            }
            for (DetalleVenta detalleVentaListDetalleVenta : productos.getDetalleVentaList()) {
                Productos oldProductosOfDetalleVentaListDetalleVenta = detalleVentaListDetalleVenta.getProductos();
                detalleVentaListDetalleVenta.setProductos(productos);
                detalleVentaListDetalleVenta = em.merge(detalleVentaListDetalleVenta);
                if (oldProductosOfDetalleVentaListDetalleVenta != null) {
                    oldProductosOfDetalleVentaListDetalleVenta.getDetalleVentaList().remove(detalleVentaListDetalleVenta);
                    oldProductosOfDetalleVentaListDetalleVenta = em.merge(oldProductosOfDetalleVentaListDetalleVenta);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Productos productos) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Productos persistentProductos = em.find(Productos.class, productos.getIdProducto());
            Proveedor idProveedorOld = persistentProductos.getIdProveedor();
            Proveedor idProveedorNew = productos.getIdProveedor();
            List<Contrato> contratoListOld = persistentProductos.getContratoList();
            List<Contrato> contratoListNew = productos.getContratoList();
            List<DetalleVenta> detalleVentaListOld = persistentProductos.getDetalleVentaList();
            List<DetalleVenta> detalleVentaListNew = productos.getDetalleVentaList();
            List<String> illegalOrphanMessages = null;
            for (Contrato contratoListOldContrato : contratoListOld) {
                if (!contratoListNew.contains(contratoListOldContrato)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Contrato " + contratoListOldContrato + " since its idProducto field is not nullable.");
                }
            }
            for (DetalleVenta detalleVentaListOldDetalleVenta : detalleVentaListOld) {
                if (!detalleVentaListNew.contains(detalleVentaListOldDetalleVenta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DetalleVenta " + detalleVentaListOldDetalleVenta + " since its productos field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idProveedorNew != null) {
                idProveedorNew = em.getReference(idProveedorNew.getClass(), idProveedorNew.getIdProveedor());
                productos.setIdProveedor(idProveedorNew);
            }
            List<Contrato> attachedContratoListNew = new ArrayList<Contrato>();
            for (Contrato contratoListNewContratoToAttach : contratoListNew) {
                contratoListNewContratoToAttach = em.getReference(contratoListNewContratoToAttach.getClass(), contratoListNewContratoToAttach.getIdContrato());
                attachedContratoListNew.add(contratoListNewContratoToAttach);
            }
            contratoListNew = attachedContratoListNew;
            productos.setContratoList(contratoListNew);
            List<DetalleVenta> attachedDetalleVentaListNew = new ArrayList<DetalleVenta>();
            for (DetalleVenta detalleVentaListNewDetalleVentaToAttach : detalleVentaListNew) {
                detalleVentaListNewDetalleVentaToAttach = em.getReference(detalleVentaListNewDetalleVentaToAttach.getClass(), detalleVentaListNewDetalleVentaToAttach.getDetalleVentaPK());
                attachedDetalleVentaListNew.add(detalleVentaListNewDetalleVentaToAttach);
            }
            detalleVentaListNew = attachedDetalleVentaListNew;
            productos.setDetalleVentaList(detalleVentaListNew);
            productos = em.merge(productos);
            if (idProveedorOld != null && !idProveedorOld.equals(idProveedorNew)) {
                idProveedorOld.getProductosList().remove(productos);
                idProveedorOld = em.merge(idProveedorOld);
            }
            if (idProveedorNew != null && !idProveedorNew.equals(idProveedorOld)) {
                idProveedorNew.getProductosList().add(productos);
                idProveedorNew = em.merge(idProveedorNew);
            }
            for (Contrato contratoListNewContrato : contratoListNew) {
                if (!contratoListOld.contains(contratoListNewContrato)) {
                    Productos oldIdProductoOfContratoListNewContrato = contratoListNewContrato.getIdProducto();
                    contratoListNewContrato.setIdProducto(productos);
                    contratoListNewContrato = em.merge(contratoListNewContrato);
                    if (oldIdProductoOfContratoListNewContrato != null && !oldIdProductoOfContratoListNewContrato.equals(productos)) {
                        oldIdProductoOfContratoListNewContrato.getContratoList().remove(contratoListNewContrato);
                        oldIdProductoOfContratoListNewContrato = em.merge(oldIdProductoOfContratoListNewContrato);
                    }
                }
            }
            for (DetalleVenta detalleVentaListNewDetalleVenta : detalleVentaListNew) {
                if (!detalleVentaListOld.contains(detalleVentaListNewDetalleVenta)) {
                    Productos oldProductosOfDetalleVentaListNewDetalleVenta = detalleVentaListNewDetalleVenta.getProductos();
                    detalleVentaListNewDetalleVenta.setProductos(productos);
                    detalleVentaListNewDetalleVenta = em.merge(detalleVentaListNewDetalleVenta);
                    if (oldProductosOfDetalleVentaListNewDetalleVenta != null && !oldProductosOfDetalleVentaListNewDetalleVenta.equals(productos)) {
                        oldProductosOfDetalleVentaListNewDetalleVenta.getDetalleVentaList().remove(detalleVentaListNewDetalleVenta);
                        oldProductosOfDetalleVentaListNewDetalleVenta = em.merge(oldProductosOfDetalleVentaListNewDetalleVenta);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = productos.getIdProducto();
                if (findProductos(id) == null) {
                    throw new NonexistentEntityException("The productos with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Productos productos;
            try {
                productos = em.getReference(Productos.class, id);
                productos.getIdProducto();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The productos with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Contrato> contratoListOrphanCheck = productos.getContratoList();
            for (Contrato contratoListOrphanCheckContrato : contratoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Productos (" + productos + ") cannot be destroyed since the Contrato " + contratoListOrphanCheckContrato + " in its contratoList field has a non-nullable idProducto field.");
            }
            List<DetalleVenta> detalleVentaListOrphanCheck = productos.getDetalleVentaList();
            for (DetalleVenta detalleVentaListOrphanCheckDetalleVenta : detalleVentaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Productos (" + productos + ") cannot be destroyed since the DetalleVenta " + detalleVentaListOrphanCheckDetalleVenta + " in its detalleVentaList field has a non-nullable productos field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Proveedor idProveedor = productos.getIdProveedor();
            if (idProveedor != null) {
                idProveedor.getProductosList().remove(productos);
                idProveedor = em.merge(idProveedor);
            }
            em.remove(productos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Productos> findProductosEntities() {
        return findProductosEntities(true, -1, -1);
    }

    public List<Productos> findProductosEntities(int maxResults, int firstResult) {
        return findProductosEntities(false, maxResults, firstResult);
    }

    private List<Productos> findProductosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Productos.class));
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

    public Productos findProductos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Productos.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Productos> rt = cq.from(Productos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
