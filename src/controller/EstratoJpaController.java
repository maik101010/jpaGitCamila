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
import entity.Cliente;
import entity.Estrato;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Michael García A
 */
public class EstratoJpaController implements Serializable {

    public EstratoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Estrato estrato) {
        if (estrato.getClienteList() == null) {
            estrato.setClienteList(new ArrayList<Cliente>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Cliente> attachedClienteList = new ArrayList<Cliente>();
            for (Cliente clienteListClienteToAttach : estrato.getClienteList()) {
                clienteListClienteToAttach = em.getReference(clienteListClienteToAttach.getClass(), clienteListClienteToAttach.getIdCliente());
                attachedClienteList.add(clienteListClienteToAttach);
            }
            estrato.setClienteList(attachedClienteList);
            em.persist(estrato);
            for (Cliente clienteListCliente : estrato.getClienteList()) {
                Estrato oldIdEstratoOfClienteListCliente = clienteListCliente.getIdEstrato();
                clienteListCliente.setIdEstrato(estrato);
                clienteListCliente = em.merge(clienteListCliente);
                if (oldIdEstratoOfClienteListCliente != null) {
                    oldIdEstratoOfClienteListCliente.getClienteList().remove(clienteListCliente);
                    oldIdEstratoOfClienteListCliente = em.merge(oldIdEstratoOfClienteListCliente);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Estrato estrato) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Estrato persistentEstrato = em.find(Estrato.class, estrato.getIdEstrato());
            List<Cliente> clienteListOld = persistentEstrato.getClienteList();
            List<Cliente> clienteListNew = estrato.getClienteList();
            List<Cliente> attachedClienteListNew = new ArrayList<Cliente>();
            for (Cliente clienteListNewClienteToAttach : clienteListNew) {
                clienteListNewClienteToAttach = em.getReference(clienteListNewClienteToAttach.getClass(), clienteListNewClienteToAttach.getIdCliente());
                attachedClienteListNew.add(clienteListNewClienteToAttach);
            }
            clienteListNew = attachedClienteListNew;
            estrato.setClienteList(clienteListNew);
            estrato = em.merge(estrato);
            for (Cliente clienteListOldCliente : clienteListOld) {
                if (!clienteListNew.contains(clienteListOldCliente)) {
                    clienteListOldCliente.setIdEstrato(null);
                    clienteListOldCliente = em.merge(clienteListOldCliente);
                }
            }
            for (Cliente clienteListNewCliente : clienteListNew) {
                if (!clienteListOld.contains(clienteListNewCliente)) {
                    Estrato oldIdEstratoOfClienteListNewCliente = clienteListNewCliente.getIdEstrato();
                    clienteListNewCliente.setIdEstrato(estrato);
                    clienteListNewCliente = em.merge(clienteListNewCliente);
                    if (oldIdEstratoOfClienteListNewCliente != null && !oldIdEstratoOfClienteListNewCliente.equals(estrato)) {
                        oldIdEstratoOfClienteListNewCliente.getClienteList().remove(clienteListNewCliente);
                        oldIdEstratoOfClienteListNewCliente = em.merge(oldIdEstratoOfClienteListNewCliente);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = estrato.getIdEstrato();
                if (findEstrato(id) == null) {
                    throw new NonexistentEntityException("The estrato with id " + id + " no longer exists.");
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
            Estrato estrato;
            try {
                estrato = em.getReference(Estrato.class, id);
                estrato.getIdEstrato();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The estrato with id " + id + " no longer exists.", enfe);
            }
            List<Cliente> clienteList = estrato.getClienteList();
            for (Cliente clienteListCliente : clienteList) {
                clienteListCliente.setIdEstrato(null);
                clienteListCliente = em.merge(clienteListCliente);
            }
            em.remove(estrato);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Estrato> findEstratoEntities() {
        return findEstratoEntities(true, -1, -1);
    }

    public List<Estrato> findEstratoEntities(int maxResults, int firstResult) {
        return findEstratoEntities(false, maxResults, firstResult);
    }

    private List<Estrato> findEstratoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Estrato.class));
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

    public Estrato findEstrato(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Estrato.class, id);
        } finally {
            em.close();
        }
    }

    public int getEstratoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Estrato> rt = cq.from(Estrato.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
