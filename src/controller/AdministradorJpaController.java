/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import entity.Administrador;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entity.Persona;
import entity.Contrato;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Michael García A
 */
public class AdministradorJpaController implements Serializable {

    public AdministradorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Administrador administrador) {
        if (administrador.getContratoList() == null) {
            administrador.setContratoList(new ArrayList<Contrato>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Persona idPersona = administrador.getIdPersona();
            if (idPersona != null) {
                idPersona = em.getReference(idPersona.getClass(), idPersona.getIdPersona());
                administrador.setIdPersona(idPersona);
            }
            List<Contrato> attachedContratoList = new ArrayList<Contrato>();
            for (Contrato contratoListContratoToAttach : administrador.getContratoList()) {
                contratoListContratoToAttach = em.getReference(contratoListContratoToAttach.getClass(), contratoListContratoToAttach.getIdContrato());
                attachedContratoList.add(contratoListContratoToAttach);
            }
            administrador.setContratoList(attachedContratoList);
            em.persist(administrador);
            if (idPersona != null) {
                idPersona.getAdministradorList().add(administrador);
                idPersona = em.merge(idPersona);
            }
            for (Contrato contratoListContrato : administrador.getContratoList()) {
                Administrador oldIdAdministradorOfContratoListContrato = contratoListContrato.getIdAdministrador();
                contratoListContrato.setIdAdministrador(administrador);
                contratoListContrato = em.merge(contratoListContrato);
                if (oldIdAdministradorOfContratoListContrato != null) {
                    oldIdAdministradorOfContratoListContrato.getContratoList().remove(contratoListContrato);
                    oldIdAdministradorOfContratoListContrato = em.merge(oldIdAdministradorOfContratoListContrato);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Administrador administrador) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Administrador persistentAdministrador = em.find(Administrador.class, administrador.getIdAdministrador());
            Persona idPersonaOld = persistentAdministrador.getIdPersona();
            Persona idPersonaNew = administrador.getIdPersona();
            List<Contrato> contratoListOld = persistentAdministrador.getContratoList();
            List<Contrato> contratoListNew = administrador.getContratoList();
            List<String> illegalOrphanMessages = null;
            for (Contrato contratoListOldContrato : contratoListOld) {
                if (!contratoListNew.contains(contratoListOldContrato)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Contrato " + contratoListOldContrato + " since its idAdministrador field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idPersonaNew != null) {
                idPersonaNew = em.getReference(idPersonaNew.getClass(), idPersonaNew.getIdPersona());
                administrador.setIdPersona(idPersonaNew);
            }
            List<Contrato> attachedContratoListNew = new ArrayList<Contrato>();
            for (Contrato contratoListNewContratoToAttach : contratoListNew) {
                contratoListNewContratoToAttach = em.getReference(contratoListNewContratoToAttach.getClass(), contratoListNewContratoToAttach.getIdContrato());
                attachedContratoListNew.add(contratoListNewContratoToAttach);
            }
            contratoListNew = attachedContratoListNew;
            administrador.setContratoList(contratoListNew);
            administrador = em.merge(administrador);
            if (idPersonaOld != null && !idPersonaOld.equals(idPersonaNew)) {
                idPersonaOld.getAdministradorList().remove(administrador);
                idPersonaOld = em.merge(idPersonaOld);
            }
            if (idPersonaNew != null && !idPersonaNew.equals(idPersonaOld)) {
                idPersonaNew.getAdministradorList().add(administrador);
                idPersonaNew = em.merge(idPersonaNew);
            }
            for (Contrato contratoListNewContrato : contratoListNew) {
                if (!contratoListOld.contains(contratoListNewContrato)) {
                    Administrador oldIdAdministradorOfContratoListNewContrato = contratoListNewContrato.getIdAdministrador();
                    contratoListNewContrato.setIdAdministrador(administrador);
                    contratoListNewContrato = em.merge(contratoListNewContrato);
                    if (oldIdAdministradorOfContratoListNewContrato != null && !oldIdAdministradorOfContratoListNewContrato.equals(administrador)) {
                        oldIdAdministradorOfContratoListNewContrato.getContratoList().remove(contratoListNewContrato);
                        oldIdAdministradorOfContratoListNewContrato = em.merge(oldIdAdministradorOfContratoListNewContrato);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = administrador.getIdAdministrador();
                if (findAdministrador(id) == null) {
                    throw new NonexistentEntityException("The administrador with id " + id + " no longer exists.");
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
            Administrador administrador;
            try {
                administrador = em.getReference(Administrador.class, id);
                administrador.getIdAdministrador();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The administrador with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Contrato> contratoListOrphanCheck = administrador.getContratoList();
            for (Contrato contratoListOrphanCheckContrato : contratoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Administrador (" + administrador + ") cannot be destroyed since the Contrato " + contratoListOrphanCheckContrato + " in its contratoList field has a non-nullable idAdministrador field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Persona idPersona = administrador.getIdPersona();
            if (idPersona != null) {
                idPersona.getAdministradorList().remove(administrador);
                idPersona = em.merge(idPersona);
            }
            em.remove(administrador);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Administrador> findAdministradorEntities() {
        return findAdministradorEntities(true, -1, -1);
    }

    public List<Administrador> findAdministradorEntities(int maxResults, int firstResult) {
        return findAdministradorEntities(false, maxResults, firstResult);
    }

    private List<Administrador> findAdministradorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Administrador.class));
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

    public Administrador findAdministrador(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Administrador.class, id);
        } finally {
            em.close();
        }
    }

    public int getAdministradorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Administrador> rt = cq.from(Administrador.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
