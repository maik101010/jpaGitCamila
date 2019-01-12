/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import entity.Cliente;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entity.Persona;
import entity.Estrato;
import entity.Venta;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Michael García A
 */
public class ClienteJpaController implements Serializable {

    public ClienteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cliente cliente) {
        if (cliente.getVentaList() == null) {
            cliente.setVentaList(new ArrayList<Venta>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Persona idPersona = cliente.getIdPersona();
            if (idPersona != null) {
                idPersona = em.getReference(idPersona.getClass(), idPersona.getIdPersona());
                cliente.setIdPersona(idPersona);
            }
            Estrato idEstrato = cliente.getIdEstrato();
            if (idEstrato != null) {
                idEstrato = em.getReference(idEstrato.getClass(), idEstrato.getIdEstrato());
                cliente.setIdEstrato(idEstrato);
            }
            List<Venta> attachedVentaList = new ArrayList<Venta>();
            for (Venta ventaListVentaToAttach : cliente.getVentaList()) {
                ventaListVentaToAttach = em.getReference(ventaListVentaToAttach.getClass(), ventaListVentaToAttach.getIdVenta());
                attachedVentaList.add(ventaListVentaToAttach);
            }
            cliente.setVentaList(attachedVentaList);
            em.persist(cliente);
            if (idPersona != null) {
                idPersona.getClienteList().add(cliente);
                idPersona = em.merge(idPersona);
            }
            if (idEstrato != null) {
                idEstrato.getClienteList().add(cliente);
                idEstrato = em.merge(idEstrato);
            }
            for (Venta ventaListVenta : cliente.getVentaList()) {
                Cliente oldIdClienteOfVentaListVenta = ventaListVenta.getIdCliente();
                ventaListVenta.setIdCliente(cliente);
                ventaListVenta = em.merge(ventaListVenta);
                if (oldIdClienteOfVentaListVenta != null) {
                    oldIdClienteOfVentaListVenta.getVentaList().remove(ventaListVenta);
                    oldIdClienteOfVentaListVenta = em.merge(oldIdClienteOfVentaListVenta);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cliente cliente) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cliente persistentCliente = em.find(Cliente.class, cliente.getIdCliente());
            Persona idPersonaOld = persistentCliente.getIdPersona();
            Persona idPersonaNew = cliente.getIdPersona();
            Estrato idEstratoOld = persistentCliente.getIdEstrato();
            Estrato idEstratoNew = cliente.getIdEstrato();
            List<Venta> ventaListOld = persistentCliente.getVentaList();
            List<Venta> ventaListNew = cliente.getVentaList();
            if (idPersonaNew != null) {
                idPersonaNew = em.getReference(idPersonaNew.getClass(), idPersonaNew.getIdPersona());
                cliente.setIdPersona(idPersonaNew);
            }
            if (idEstratoNew != null) {
                idEstratoNew = em.getReference(idEstratoNew.getClass(), idEstratoNew.getIdEstrato());
                cliente.setIdEstrato(idEstratoNew);
            }
            List<Venta> attachedVentaListNew = new ArrayList<Venta>();
            for (Venta ventaListNewVentaToAttach : ventaListNew) {
                ventaListNewVentaToAttach = em.getReference(ventaListNewVentaToAttach.getClass(), ventaListNewVentaToAttach.getIdVenta());
                attachedVentaListNew.add(ventaListNewVentaToAttach);
            }
            ventaListNew = attachedVentaListNew;
            cliente.setVentaList(ventaListNew);
            cliente = em.merge(cliente);
            if (idPersonaOld != null && !idPersonaOld.equals(idPersonaNew)) {
                idPersonaOld.getClienteList().remove(cliente);
                idPersonaOld = em.merge(idPersonaOld);
            }
            if (idPersonaNew != null && !idPersonaNew.equals(idPersonaOld)) {
                idPersonaNew.getClienteList().add(cliente);
                idPersonaNew = em.merge(idPersonaNew);
            }
            if (idEstratoOld != null && !idEstratoOld.equals(idEstratoNew)) {
                idEstratoOld.getClienteList().remove(cliente);
                idEstratoOld = em.merge(idEstratoOld);
            }
            if (idEstratoNew != null && !idEstratoNew.equals(idEstratoOld)) {
                idEstratoNew.getClienteList().add(cliente);
                idEstratoNew = em.merge(idEstratoNew);
            }
            for (Venta ventaListOldVenta : ventaListOld) {
                if (!ventaListNew.contains(ventaListOldVenta)) {
                    ventaListOldVenta.setIdCliente(null);
                    ventaListOldVenta = em.merge(ventaListOldVenta);
                }
            }
            for (Venta ventaListNewVenta : ventaListNew) {
                if (!ventaListOld.contains(ventaListNewVenta)) {
                    Cliente oldIdClienteOfVentaListNewVenta = ventaListNewVenta.getIdCliente();
                    ventaListNewVenta.setIdCliente(cliente);
                    ventaListNewVenta = em.merge(ventaListNewVenta);
                    if (oldIdClienteOfVentaListNewVenta != null && !oldIdClienteOfVentaListNewVenta.equals(cliente)) {
                        oldIdClienteOfVentaListNewVenta.getVentaList().remove(ventaListNewVenta);
                        oldIdClienteOfVentaListNewVenta = em.merge(oldIdClienteOfVentaListNewVenta);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cliente.getIdCliente();
                if (findCliente(id) == null) {
                    throw new NonexistentEntityException("The cliente with id " + id + " no longer exists.");
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
            Cliente cliente;
            try {
                cliente = em.getReference(Cliente.class, id);
                cliente.getIdCliente();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cliente with id " + id + " no longer exists.", enfe);
            }
            Persona idPersona = cliente.getIdPersona();
            if (idPersona != null) {
                idPersona.getClienteList().remove(cliente);
                idPersona = em.merge(idPersona);
            }
            Estrato idEstrato = cliente.getIdEstrato();
            if (idEstrato != null) {
                idEstrato.getClienteList().remove(cliente);
                idEstrato = em.merge(idEstrato);
            }
            List<Venta> ventaList = cliente.getVentaList();
            for (Venta ventaListVenta : ventaList) {
                ventaListVenta.setIdCliente(null);
                ventaListVenta = em.merge(ventaListVenta);
            }
            em.remove(cliente);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cliente> findClienteEntities() {
        return findClienteEntities(true, -1, -1);
    }

    public List<Cliente> findClienteEntities(int maxResults, int firstResult) {
        return findClienteEntities(false, maxResults, firstResult);
    }

    private List<Cliente> findClienteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cliente.class));
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

    public Cliente findCliente(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cliente.class, id);
        } finally {
            em.close();
        }
    }

    public int getClienteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cliente> rt = cq.from(Cliente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
