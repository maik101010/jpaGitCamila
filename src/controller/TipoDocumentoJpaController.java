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
import entity.Persona;
import entity.TipoDocumento;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Michael García A
 */
public class TipoDocumentoJpaController implements Serializable {

    public TipoDocumentoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TipoDocumento tipoDocumento) {
        if (tipoDocumento.getPersonaList() == null) {
            tipoDocumento.setPersonaList(new ArrayList<Persona>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Persona> attachedPersonaList = new ArrayList<Persona>();
            for (Persona personaListPersonaToAttach : tipoDocumento.getPersonaList()) {
                personaListPersonaToAttach = em.getReference(personaListPersonaToAttach.getClass(), personaListPersonaToAttach.getIdPersona());
                attachedPersonaList.add(personaListPersonaToAttach);
            }
            tipoDocumento.setPersonaList(attachedPersonaList);
            em.persist(tipoDocumento);
            for (Persona personaListPersona : tipoDocumento.getPersonaList()) {
                TipoDocumento oldTipoDocumentoOfPersonaListPersona = personaListPersona.getTipoDocumento();
                personaListPersona.setTipoDocumento(tipoDocumento);
                personaListPersona = em.merge(personaListPersona);
                if (oldTipoDocumentoOfPersonaListPersona != null) {
                    oldTipoDocumentoOfPersonaListPersona.getPersonaList().remove(personaListPersona);
                    oldTipoDocumentoOfPersonaListPersona = em.merge(oldTipoDocumentoOfPersonaListPersona);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TipoDocumento tipoDocumento) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TipoDocumento persistentTipoDocumento = em.find(TipoDocumento.class, tipoDocumento.getIdTipoDocumento());
            List<Persona> personaListOld = persistentTipoDocumento.getPersonaList();
            List<Persona> personaListNew = tipoDocumento.getPersonaList();
            List<Persona> attachedPersonaListNew = new ArrayList<Persona>();
            for (Persona personaListNewPersonaToAttach : personaListNew) {
                personaListNewPersonaToAttach = em.getReference(personaListNewPersonaToAttach.getClass(), personaListNewPersonaToAttach.getIdPersona());
                attachedPersonaListNew.add(personaListNewPersonaToAttach);
            }
            personaListNew = attachedPersonaListNew;
            tipoDocumento.setPersonaList(personaListNew);
            tipoDocumento = em.merge(tipoDocumento);
            for (Persona personaListOldPersona : personaListOld) {
                if (!personaListNew.contains(personaListOldPersona)) {
                    personaListOldPersona.setTipoDocumento(null);
                    personaListOldPersona = em.merge(personaListOldPersona);
                }
            }
            for (Persona personaListNewPersona : personaListNew) {
                if (!personaListOld.contains(personaListNewPersona)) {
                    TipoDocumento oldTipoDocumentoOfPersonaListNewPersona = personaListNewPersona.getTipoDocumento();
                    personaListNewPersona.setTipoDocumento(tipoDocumento);
                    personaListNewPersona = em.merge(personaListNewPersona);
                    if (oldTipoDocumentoOfPersonaListNewPersona != null && !oldTipoDocumentoOfPersonaListNewPersona.equals(tipoDocumento)) {
                        oldTipoDocumentoOfPersonaListNewPersona.getPersonaList().remove(personaListNewPersona);
                        oldTipoDocumentoOfPersonaListNewPersona = em.merge(oldTipoDocumentoOfPersonaListNewPersona);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tipoDocumento.getIdTipoDocumento();
                if (findTipoDocumento(id) == null) {
                    throw new NonexistentEntityException("The tipoDocumento with id " + id + " no longer exists.");
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
            TipoDocumento tipoDocumento;
            try {
                tipoDocumento = em.getReference(TipoDocumento.class, id);
                tipoDocumento.getIdTipoDocumento();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tipoDocumento with id " + id + " no longer exists.", enfe);
            }
            List<Persona> personaList = tipoDocumento.getPersonaList();
            for (Persona personaListPersona : personaList) {
                personaListPersona.setTipoDocumento(null);
                personaListPersona = em.merge(personaListPersona);
            }
            em.remove(tipoDocumento);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TipoDocumento> findTipoDocumentoEntities() {
        return findTipoDocumentoEntities(true, -1, -1);
    }

    public List<TipoDocumento> findTipoDocumentoEntities(int maxResults, int firstResult) {
        return findTipoDocumentoEntities(false, maxResults, firstResult);
    }

    private List<TipoDocumento> findTipoDocumentoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TipoDocumento.class));
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

    public TipoDocumento findTipoDocumento(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TipoDocumento.class, id);
        } finally {
            em.close();
        }
    }

    public int getTipoDocumentoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TipoDocumento> rt = cq.from(TipoDocumento.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
