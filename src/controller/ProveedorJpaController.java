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
import entity.Empresa;
import entity.Persona;
import entity.Contrato;
import java.util.ArrayList;
import java.util.List;
import entity.Productos;
import entity.Proveedor;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Michael García A
 */
public class ProveedorJpaController implements Serializable {

    public ProveedorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Proveedor proveedor) {
        if (proveedor.getContratoList() == null) {
            proveedor.setContratoList(new ArrayList<Contrato>());
        }
        if (proveedor.getProductosList() == null) {
            proveedor.setProductosList(new ArrayList<Productos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Empresa idEmpresa = proveedor.getIdEmpresa();
            if (idEmpresa != null) {
                idEmpresa = em.getReference(idEmpresa.getClass(), idEmpresa.getIdEmpresa());
                proveedor.setIdEmpresa(idEmpresa);
            }
            Persona idPersona = proveedor.getIdPersona();
            if (idPersona != null) {
                idPersona = em.getReference(idPersona.getClass(), idPersona.getIdPersona());
                proveedor.setIdPersona(idPersona);
            }
            List<Contrato> attachedContratoList = new ArrayList<Contrato>();
            for (Contrato contratoListContratoToAttach : proveedor.getContratoList()) {
                contratoListContratoToAttach = em.getReference(contratoListContratoToAttach.getClass(), contratoListContratoToAttach.getIdContrato());
                attachedContratoList.add(contratoListContratoToAttach);
            }
            proveedor.setContratoList(attachedContratoList);
            List<Productos> attachedProductosList = new ArrayList<Productos>();
            for (Productos productosListProductosToAttach : proveedor.getProductosList()) {
                productosListProductosToAttach = em.getReference(productosListProductosToAttach.getClass(), productosListProductosToAttach.getIdProducto());
                attachedProductosList.add(productosListProductosToAttach);
            }
            proveedor.setProductosList(attachedProductosList);
            em.persist(proveedor);
            if (idEmpresa != null) {
                idEmpresa.getProveedorList().add(proveedor);
                idEmpresa = em.merge(idEmpresa);
            }
            if (idPersona != null) {
                idPersona.getProveedorList().add(proveedor);
                idPersona = em.merge(idPersona);
            }
            for (Contrato contratoListContrato : proveedor.getContratoList()) {
                Proveedor oldIdProveedorOfContratoListContrato = contratoListContrato.getIdProveedor();
                contratoListContrato.setIdProveedor(proveedor);
                contratoListContrato = em.merge(contratoListContrato);
                if (oldIdProveedorOfContratoListContrato != null) {
                    oldIdProveedorOfContratoListContrato.getContratoList().remove(contratoListContrato);
                    oldIdProveedorOfContratoListContrato = em.merge(oldIdProveedorOfContratoListContrato);
                }
            }
            for (Productos productosListProductos : proveedor.getProductosList()) {
                Proveedor oldIdProveedorOfProductosListProductos = productosListProductos.getIdProveedor();
                productosListProductos.setIdProveedor(proveedor);
                productosListProductos = em.merge(productosListProductos);
                if (oldIdProveedorOfProductosListProductos != null) {
                    oldIdProveedorOfProductosListProductos.getProductosList().remove(productosListProductos);
                    oldIdProveedorOfProductosListProductos = em.merge(oldIdProveedorOfProductosListProductos);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Proveedor proveedor) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Proveedor persistentProveedor = em.find(Proveedor.class, proveedor.getIdProveedor());
            Empresa idEmpresaOld = persistentProveedor.getIdEmpresa();
            Empresa idEmpresaNew = proveedor.getIdEmpresa();
            Persona idPersonaOld = persistentProveedor.getIdPersona();
            Persona idPersonaNew = proveedor.getIdPersona();
            List<Contrato> contratoListOld = persistentProveedor.getContratoList();
            List<Contrato> contratoListNew = proveedor.getContratoList();
            List<Productos> productosListOld = persistentProveedor.getProductosList();
            List<Productos> productosListNew = proveedor.getProductosList();
            List<String> illegalOrphanMessages = null;
            for (Contrato contratoListOldContrato : contratoListOld) {
                if (!contratoListNew.contains(contratoListOldContrato)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Contrato " + contratoListOldContrato + " since its idProveedor field is not nullable.");
                }
            }
            for (Productos productosListOldProductos : productosListOld) {
                if (!productosListNew.contains(productosListOldProductos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Productos " + productosListOldProductos + " since its idProveedor field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idEmpresaNew != null) {
                idEmpresaNew = em.getReference(idEmpresaNew.getClass(), idEmpresaNew.getIdEmpresa());
                proveedor.setIdEmpresa(idEmpresaNew);
            }
            if (idPersonaNew != null) {
                idPersonaNew = em.getReference(idPersonaNew.getClass(), idPersonaNew.getIdPersona());
                proveedor.setIdPersona(idPersonaNew);
            }
            List<Contrato> attachedContratoListNew = new ArrayList<Contrato>();
            for (Contrato contratoListNewContratoToAttach : contratoListNew) {
                contratoListNewContratoToAttach = em.getReference(contratoListNewContratoToAttach.getClass(), contratoListNewContratoToAttach.getIdContrato());
                attachedContratoListNew.add(contratoListNewContratoToAttach);
            }
            contratoListNew = attachedContratoListNew;
            proveedor.setContratoList(contratoListNew);
            List<Productos> attachedProductosListNew = new ArrayList<Productos>();
            for (Productos productosListNewProductosToAttach : productosListNew) {
                productosListNewProductosToAttach = em.getReference(productosListNewProductosToAttach.getClass(), productosListNewProductosToAttach.getIdProducto());
                attachedProductosListNew.add(productosListNewProductosToAttach);
            }
            productosListNew = attachedProductosListNew;
            proveedor.setProductosList(productosListNew);
            proveedor = em.merge(proveedor);
            if (idEmpresaOld != null && !idEmpresaOld.equals(idEmpresaNew)) {
                idEmpresaOld.getProveedorList().remove(proveedor);
                idEmpresaOld = em.merge(idEmpresaOld);
            }
            if (idEmpresaNew != null && !idEmpresaNew.equals(idEmpresaOld)) {
                idEmpresaNew.getProveedorList().add(proveedor);
                idEmpresaNew = em.merge(idEmpresaNew);
            }
            if (idPersonaOld != null && !idPersonaOld.equals(idPersonaNew)) {
                idPersonaOld.getProveedorList().remove(proveedor);
                idPersonaOld = em.merge(idPersonaOld);
            }
            if (idPersonaNew != null && !idPersonaNew.equals(idPersonaOld)) {
                idPersonaNew.getProveedorList().add(proveedor);
                idPersonaNew = em.merge(idPersonaNew);
            }
            for (Contrato contratoListNewContrato : contratoListNew) {
                if (!contratoListOld.contains(contratoListNewContrato)) {
                    Proveedor oldIdProveedorOfContratoListNewContrato = contratoListNewContrato.getIdProveedor();
                    contratoListNewContrato.setIdProveedor(proveedor);
                    contratoListNewContrato = em.merge(contratoListNewContrato);
                    if (oldIdProveedorOfContratoListNewContrato != null && !oldIdProveedorOfContratoListNewContrato.equals(proveedor)) {
                        oldIdProveedorOfContratoListNewContrato.getContratoList().remove(contratoListNewContrato);
                        oldIdProveedorOfContratoListNewContrato = em.merge(oldIdProveedorOfContratoListNewContrato);
                    }
                }
            }
            for (Productos productosListNewProductos : productosListNew) {
                if (!productosListOld.contains(productosListNewProductos)) {
                    Proveedor oldIdProveedorOfProductosListNewProductos = productosListNewProductos.getIdProveedor();
                    productosListNewProductos.setIdProveedor(proveedor);
                    productosListNewProductos = em.merge(productosListNewProductos);
                    if (oldIdProveedorOfProductosListNewProductos != null && !oldIdProveedorOfProductosListNewProductos.equals(proveedor)) {
                        oldIdProveedorOfProductosListNewProductos.getProductosList().remove(productosListNewProductos);
                        oldIdProveedorOfProductosListNewProductos = em.merge(oldIdProveedorOfProductosListNewProductos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = proveedor.getIdProveedor();
                if (findProveedor(id) == null) {
                    throw new NonexistentEntityException("The proveedor with id " + id + " no longer exists.");
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
            Proveedor proveedor;
            try {
                proveedor = em.getReference(Proveedor.class, id);
                proveedor.getIdProveedor();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The proveedor with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Contrato> contratoListOrphanCheck = proveedor.getContratoList();
            for (Contrato contratoListOrphanCheckContrato : contratoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Proveedor (" + proveedor + ") cannot be destroyed since the Contrato " + contratoListOrphanCheckContrato + " in its contratoList field has a non-nullable idProveedor field.");
            }
            List<Productos> productosListOrphanCheck = proveedor.getProductosList();
            for (Productos productosListOrphanCheckProductos : productosListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Proveedor (" + proveedor + ") cannot be destroyed since the Productos " + productosListOrphanCheckProductos + " in its productosList field has a non-nullable idProveedor field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Empresa idEmpresa = proveedor.getIdEmpresa();
            if (idEmpresa != null) {
                idEmpresa.getProveedorList().remove(proveedor);
                idEmpresa = em.merge(idEmpresa);
            }
            Persona idPersona = proveedor.getIdPersona();
            if (idPersona != null) {
                idPersona.getProveedorList().remove(proveedor);
                idPersona = em.merge(idPersona);
            }
            em.remove(proveedor);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Proveedor> findProveedorEntities() {
        return findProveedorEntities(true, -1, -1);
    }

    public List<Proveedor> findProveedorEntities(int maxResults, int firstResult) {
        return findProveedorEntities(false, maxResults, firstResult);
    }

    private List<Proveedor> findProveedorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Proveedor.class));
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

    public Proveedor findProveedor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Proveedor.class, id);
        } finally {
            em.close();
        }
    }

    public int getProveedorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Proveedor> rt = cq.from(Proveedor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
