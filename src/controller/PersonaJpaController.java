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
import entity.Ciudad;
import entity.TipoDocumento;
import entity.TipoPersona;
import java.util.ArrayList;
import java.util.List;
import entity.Administrador;
import entity.Vendedor;
import entity.Cliente;
import entity.Persona;
import entity.Proveedor;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Michael García A
 */
public class PersonaJpaController implements Serializable {

    public PersonaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Persona persona) {
        if (persona.getTipoPersonaList() == null) {
            persona.setTipoPersonaList(new ArrayList<TipoPersona>());
        }
        if (persona.getAdministradorList() == null) {
            persona.setAdministradorList(new ArrayList<Administrador>());
        }
        if (persona.getVendedorList() == null) {
            persona.setVendedorList(new ArrayList<Vendedor>());
        }
        if (persona.getClienteList() == null) {
            persona.setClienteList(new ArrayList<Cliente>());
        }
        if (persona.getProveedorList() == null) {
            persona.setProveedorList(new ArrayList<Proveedor>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ciudad idCiudad = persona.getIdCiudad();
            if (idCiudad != null) {
                idCiudad = em.getReference(idCiudad.getClass(), idCiudad.getIdCiudad());
                persona.setIdCiudad(idCiudad);
            }
            TipoDocumento tipoDocumento = persona.getTipoDocumento();
            if (tipoDocumento != null) {
                tipoDocumento = em.getReference(tipoDocumento.getClass(), tipoDocumento.getIdTipoDocumento());
                persona.setTipoDocumento(tipoDocumento);
            }
            List<TipoPersona> attachedTipoPersonaList = new ArrayList<TipoPersona>();
            for (TipoPersona tipoPersonaListTipoPersonaToAttach : persona.getTipoPersonaList()) {
                tipoPersonaListTipoPersonaToAttach = em.getReference(tipoPersonaListTipoPersonaToAttach.getClass(), tipoPersonaListTipoPersonaToAttach.getIdTipoPersona());
                attachedTipoPersonaList.add(tipoPersonaListTipoPersonaToAttach);
            }
            persona.setTipoPersonaList(attachedTipoPersonaList);
            List<Administrador> attachedAdministradorList = new ArrayList<Administrador>();
            for (Administrador administradorListAdministradorToAttach : persona.getAdministradorList()) {
                administradorListAdministradorToAttach = em.getReference(administradorListAdministradorToAttach.getClass(), administradorListAdministradorToAttach.getIdAdministrador());
                attachedAdministradorList.add(administradorListAdministradorToAttach);
            }
            persona.setAdministradorList(attachedAdministradorList);
            List<Vendedor> attachedVendedorList = new ArrayList<Vendedor>();
            for (Vendedor vendedorListVendedorToAttach : persona.getVendedorList()) {
                vendedorListVendedorToAttach = em.getReference(vendedorListVendedorToAttach.getClass(), vendedorListVendedorToAttach.getIdVendedor());
                attachedVendedorList.add(vendedorListVendedorToAttach);
            }
            persona.setVendedorList(attachedVendedorList);
            List<Cliente> attachedClienteList = new ArrayList<Cliente>();
            for (Cliente clienteListClienteToAttach : persona.getClienteList()) {
                clienteListClienteToAttach = em.getReference(clienteListClienteToAttach.getClass(), clienteListClienteToAttach.getIdCliente());
                attachedClienteList.add(clienteListClienteToAttach);
            }
            persona.setClienteList(attachedClienteList);
            List<Proveedor> attachedProveedorList = new ArrayList<Proveedor>();
            for (Proveedor proveedorListProveedorToAttach : persona.getProveedorList()) {
                proveedorListProveedorToAttach = em.getReference(proveedorListProveedorToAttach.getClass(), proveedorListProveedorToAttach.getIdProveedor());
                attachedProveedorList.add(proveedorListProveedorToAttach);
            }
            persona.setProveedorList(attachedProveedorList);
            em.persist(persona);
            if (idCiudad != null) {
                idCiudad.getPersonaList().add(persona);
                idCiudad = em.merge(idCiudad);
            }
            if (tipoDocumento != null) {
                tipoDocumento.getPersonaList().add(persona);
                tipoDocumento = em.merge(tipoDocumento);
            }
            for (TipoPersona tipoPersonaListTipoPersona : persona.getTipoPersonaList()) {
                tipoPersonaListTipoPersona.getPersonaList().add(persona);
                tipoPersonaListTipoPersona = em.merge(tipoPersonaListTipoPersona);
            }
            for (Administrador administradorListAdministrador : persona.getAdministradorList()) {
                Persona oldIdPersonaOfAdministradorListAdministrador = administradorListAdministrador.getIdPersona();
                administradorListAdministrador.setIdPersona(persona);
                administradorListAdministrador = em.merge(administradorListAdministrador);
                if (oldIdPersonaOfAdministradorListAdministrador != null) {
                    oldIdPersonaOfAdministradorListAdministrador.getAdministradorList().remove(administradorListAdministrador);
                    oldIdPersonaOfAdministradorListAdministrador = em.merge(oldIdPersonaOfAdministradorListAdministrador);
                }
            }
            for (Vendedor vendedorListVendedor : persona.getVendedorList()) {
                Persona oldIdPersonaOfVendedorListVendedor = vendedorListVendedor.getIdPersona();
                vendedorListVendedor.setIdPersona(persona);
                vendedorListVendedor = em.merge(vendedorListVendedor);
                if (oldIdPersonaOfVendedorListVendedor != null) {
                    oldIdPersonaOfVendedorListVendedor.getVendedorList().remove(vendedorListVendedor);
                    oldIdPersonaOfVendedorListVendedor = em.merge(oldIdPersonaOfVendedorListVendedor);
                }
            }
            for (Cliente clienteListCliente : persona.getClienteList()) {
                Persona oldIdPersonaOfClienteListCliente = clienteListCliente.getIdPersona();
                clienteListCliente.setIdPersona(persona);
                clienteListCliente = em.merge(clienteListCliente);
                if (oldIdPersonaOfClienteListCliente != null) {
                    oldIdPersonaOfClienteListCliente.getClienteList().remove(clienteListCliente);
                    oldIdPersonaOfClienteListCliente = em.merge(oldIdPersonaOfClienteListCliente);
                }
            }
            for (Proveedor proveedorListProveedor : persona.getProveedorList()) {
                Persona oldIdPersonaOfProveedorListProveedor = proveedorListProveedor.getIdPersona();
                proveedorListProveedor.setIdPersona(persona);
                proveedorListProveedor = em.merge(proveedorListProveedor);
                if (oldIdPersonaOfProveedorListProveedor != null) {
                    oldIdPersonaOfProveedorListProveedor.getProveedorList().remove(proveedorListProveedor);
                    oldIdPersonaOfProveedorListProveedor = em.merge(oldIdPersonaOfProveedorListProveedor);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Persona persona) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Persona persistentPersona = em.find(Persona.class, persona.getIdPersona());
            Ciudad idCiudadOld = persistentPersona.getIdCiudad();
            Ciudad idCiudadNew = persona.getIdCiudad();
            TipoDocumento tipoDocumentoOld = persistentPersona.getTipoDocumento();
            TipoDocumento tipoDocumentoNew = persona.getTipoDocumento();
            List<TipoPersona> tipoPersonaListOld = persistentPersona.getTipoPersonaList();
            List<TipoPersona> tipoPersonaListNew = persona.getTipoPersonaList();
            List<Administrador> administradorListOld = persistentPersona.getAdministradorList();
            List<Administrador> administradorListNew = persona.getAdministradorList();
            List<Vendedor> vendedorListOld = persistentPersona.getVendedorList();
            List<Vendedor> vendedorListNew = persona.getVendedorList();
            List<Cliente> clienteListOld = persistentPersona.getClienteList();
            List<Cliente> clienteListNew = persona.getClienteList();
            List<Proveedor> proveedorListOld = persistentPersona.getProveedorList();
            List<Proveedor> proveedorListNew = persona.getProveedorList();
            List<String> illegalOrphanMessages = null;
            for (Administrador administradorListOldAdministrador : administradorListOld) {
                if (!administradorListNew.contains(administradorListOldAdministrador)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Administrador " + administradorListOldAdministrador + " since its idPersona field is not nullable.");
                }
            }
            for (Vendedor vendedorListOldVendedor : vendedorListOld) {
                if (!vendedorListNew.contains(vendedorListOldVendedor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Vendedor " + vendedorListOldVendedor + " since its idPersona field is not nullable.");
                }
            }
            for (Proveedor proveedorListOldProveedor : proveedorListOld) {
                if (!proveedorListNew.contains(proveedorListOldProveedor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Proveedor " + proveedorListOldProveedor + " since its idPersona field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idCiudadNew != null) {
                idCiudadNew = em.getReference(idCiudadNew.getClass(), idCiudadNew.getIdCiudad());
                persona.setIdCiudad(idCiudadNew);
            }
            if (tipoDocumentoNew != null) {
                tipoDocumentoNew = em.getReference(tipoDocumentoNew.getClass(), tipoDocumentoNew.getIdTipoDocumento());
                persona.setTipoDocumento(tipoDocumentoNew);
            }
            List<TipoPersona> attachedTipoPersonaListNew = new ArrayList<TipoPersona>();
            for (TipoPersona tipoPersonaListNewTipoPersonaToAttach : tipoPersonaListNew) {
                tipoPersonaListNewTipoPersonaToAttach = em.getReference(tipoPersonaListNewTipoPersonaToAttach.getClass(), tipoPersonaListNewTipoPersonaToAttach.getIdTipoPersona());
                attachedTipoPersonaListNew.add(tipoPersonaListNewTipoPersonaToAttach);
            }
            tipoPersonaListNew = attachedTipoPersonaListNew;
            persona.setTipoPersonaList(tipoPersonaListNew);
            List<Administrador> attachedAdministradorListNew = new ArrayList<Administrador>();
            for (Administrador administradorListNewAdministradorToAttach : administradorListNew) {
                administradorListNewAdministradorToAttach = em.getReference(administradorListNewAdministradorToAttach.getClass(), administradorListNewAdministradorToAttach.getIdAdministrador());
                attachedAdministradorListNew.add(administradorListNewAdministradorToAttach);
            }
            administradorListNew = attachedAdministradorListNew;
            persona.setAdministradorList(administradorListNew);
            List<Vendedor> attachedVendedorListNew = new ArrayList<Vendedor>();
            for (Vendedor vendedorListNewVendedorToAttach : vendedorListNew) {
                vendedorListNewVendedorToAttach = em.getReference(vendedorListNewVendedorToAttach.getClass(), vendedorListNewVendedorToAttach.getIdVendedor());
                attachedVendedorListNew.add(vendedorListNewVendedorToAttach);
            }
            vendedorListNew = attachedVendedorListNew;
            persona.setVendedorList(vendedorListNew);
            List<Cliente> attachedClienteListNew = new ArrayList<Cliente>();
            for (Cliente clienteListNewClienteToAttach : clienteListNew) {
                clienteListNewClienteToAttach = em.getReference(clienteListNewClienteToAttach.getClass(), clienteListNewClienteToAttach.getIdCliente());
                attachedClienteListNew.add(clienteListNewClienteToAttach);
            }
            clienteListNew = attachedClienteListNew;
            persona.setClienteList(clienteListNew);
            List<Proveedor> attachedProveedorListNew = new ArrayList<Proveedor>();
            for (Proveedor proveedorListNewProveedorToAttach : proveedorListNew) {
                proveedorListNewProveedorToAttach = em.getReference(proveedorListNewProveedorToAttach.getClass(), proveedorListNewProveedorToAttach.getIdProveedor());
                attachedProveedorListNew.add(proveedorListNewProveedorToAttach);
            }
            proveedorListNew = attachedProveedorListNew;
            persona.setProveedorList(proveedorListNew);
            persona = em.merge(persona);
            if (idCiudadOld != null && !idCiudadOld.equals(idCiudadNew)) {
                idCiudadOld.getPersonaList().remove(persona);
                idCiudadOld = em.merge(idCiudadOld);
            }
            if (idCiudadNew != null && !idCiudadNew.equals(idCiudadOld)) {
                idCiudadNew.getPersonaList().add(persona);
                idCiudadNew = em.merge(idCiudadNew);
            }
            if (tipoDocumentoOld != null && !tipoDocumentoOld.equals(tipoDocumentoNew)) {
                tipoDocumentoOld.getPersonaList().remove(persona);
                tipoDocumentoOld = em.merge(tipoDocumentoOld);
            }
            if (tipoDocumentoNew != null && !tipoDocumentoNew.equals(tipoDocumentoOld)) {
                tipoDocumentoNew.getPersonaList().add(persona);
                tipoDocumentoNew = em.merge(tipoDocumentoNew);
            }
            for (TipoPersona tipoPersonaListOldTipoPersona : tipoPersonaListOld) {
                if (!tipoPersonaListNew.contains(tipoPersonaListOldTipoPersona)) {
                    tipoPersonaListOldTipoPersona.getPersonaList().remove(persona);
                    tipoPersonaListOldTipoPersona = em.merge(tipoPersonaListOldTipoPersona);
                }
            }
            for (TipoPersona tipoPersonaListNewTipoPersona : tipoPersonaListNew) {
                if (!tipoPersonaListOld.contains(tipoPersonaListNewTipoPersona)) {
                    tipoPersonaListNewTipoPersona.getPersonaList().add(persona);
                    tipoPersonaListNewTipoPersona = em.merge(tipoPersonaListNewTipoPersona);
                }
            }
            for (Administrador administradorListNewAdministrador : administradorListNew) {
                if (!administradorListOld.contains(administradorListNewAdministrador)) {
                    Persona oldIdPersonaOfAdministradorListNewAdministrador = administradorListNewAdministrador.getIdPersona();
                    administradorListNewAdministrador.setIdPersona(persona);
                    administradorListNewAdministrador = em.merge(administradorListNewAdministrador);
                    if (oldIdPersonaOfAdministradorListNewAdministrador != null && !oldIdPersonaOfAdministradorListNewAdministrador.equals(persona)) {
                        oldIdPersonaOfAdministradorListNewAdministrador.getAdministradorList().remove(administradorListNewAdministrador);
                        oldIdPersonaOfAdministradorListNewAdministrador = em.merge(oldIdPersonaOfAdministradorListNewAdministrador);
                    }
                }
            }
            for (Vendedor vendedorListNewVendedor : vendedorListNew) {
                if (!vendedorListOld.contains(vendedorListNewVendedor)) {
                    Persona oldIdPersonaOfVendedorListNewVendedor = vendedorListNewVendedor.getIdPersona();
                    vendedorListNewVendedor.setIdPersona(persona);
                    vendedorListNewVendedor = em.merge(vendedorListNewVendedor);
                    if (oldIdPersonaOfVendedorListNewVendedor != null && !oldIdPersonaOfVendedorListNewVendedor.equals(persona)) {
                        oldIdPersonaOfVendedorListNewVendedor.getVendedorList().remove(vendedorListNewVendedor);
                        oldIdPersonaOfVendedorListNewVendedor = em.merge(oldIdPersonaOfVendedorListNewVendedor);
                    }
                }
            }
            for (Cliente clienteListOldCliente : clienteListOld) {
                if (!clienteListNew.contains(clienteListOldCliente)) {
                    clienteListOldCliente.setIdPersona(null);
                    clienteListOldCliente = em.merge(clienteListOldCliente);
                }
            }
            for (Cliente clienteListNewCliente : clienteListNew) {
                if (!clienteListOld.contains(clienteListNewCliente)) {
                    Persona oldIdPersonaOfClienteListNewCliente = clienteListNewCliente.getIdPersona();
                    clienteListNewCliente.setIdPersona(persona);
                    clienteListNewCliente = em.merge(clienteListNewCliente);
                    if (oldIdPersonaOfClienteListNewCliente != null && !oldIdPersonaOfClienteListNewCliente.equals(persona)) {
                        oldIdPersonaOfClienteListNewCliente.getClienteList().remove(clienteListNewCliente);
                        oldIdPersonaOfClienteListNewCliente = em.merge(oldIdPersonaOfClienteListNewCliente);
                    }
                }
            }
            for (Proveedor proveedorListNewProveedor : proveedorListNew) {
                if (!proveedorListOld.contains(proveedorListNewProveedor)) {
                    Persona oldIdPersonaOfProveedorListNewProveedor = proveedorListNewProveedor.getIdPersona();
                    proveedorListNewProveedor.setIdPersona(persona);
                    proveedorListNewProveedor = em.merge(proveedorListNewProveedor);
                    if (oldIdPersonaOfProveedorListNewProveedor != null && !oldIdPersonaOfProveedorListNewProveedor.equals(persona)) {
                        oldIdPersonaOfProveedorListNewProveedor.getProveedorList().remove(proveedorListNewProveedor);
                        oldIdPersonaOfProveedorListNewProveedor = em.merge(oldIdPersonaOfProveedorListNewProveedor);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = persona.getIdPersona();
                if (findPersona(id) == null) {
                    throw new NonexistentEntityException("The persona with id " + id + " no longer exists.");
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
            Persona persona;
            try {
                persona = em.getReference(Persona.class, id);
                persona.getIdPersona();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The persona with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Administrador> administradorListOrphanCheck = persona.getAdministradorList();
            for (Administrador administradorListOrphanCheckAdministrador : administradorListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Persona (" + persona + ") cannot be destroyed since the Administrador " + administradorListOrphanCheckAdministrador + " in its administradorList field has a non-nullable idPersona field.");
            }
            List<Vendedor> vendedorListOrphanCheck = persona.getVendedorList();
            for (Vendedor vendedorListOrphanCheckVendedor : vendedorListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Persona (" + persona + ") cannot be destroyed since the Vendedor " + vendedorListOrphanCheckVendedor + " in its vendedorList field has a non-nullable idPersona field.");
            }
            List<Proveedor> proveedorListOrphanCheck = persona.getProveedorList();
            for (Proveedor proveedorListOrphanCheckProveedor : proveedorListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Persona (" + persona + ") cannot be destroyed since the Proveedor " + proveedorListOrphanCheckProveedor + " in its proveedorList field has a non-nullable idPersona field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Ciudad idCiudad = persona.getIdCiudad();
            if (idCiudad != null) {
                idCiudad.getPersonaList().remove(persona);
                idCiudad = em.merge(idCiudad);
            }
            TipoDocumento tipoDocumento = persona.getTipoDocumento();
            if (tipoDocumento != null) {
                tipoDocumento.getPersonaList().remove(persona);
                tipoDocumento = em.merge(tipoDocumento);
            }
            List<TipoPersona> tipoPersonaList = persona.getTipoPersonaList();
            for (TipoPersona tipoPersonaListTipoPersona : tipoPersonaList) {
                tipoPersonaListTipoPersona.getPersonaList().remove(persona);
                tipoPersonaListTipoPersona = em.merge(tipoPersonaListTipoPersona);
            }
            List<Cliente> clienteList = persona.getClienteList();
            for (Cliente clienteListCliente : clienteList) {
                clienteListCliente.setIdPersona(null);
                clienteListCliente = em.merge(clienteListCliente);
            }
            em.remove(persona);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Persona> findPersonaEntities() {
        return findPersonaEntities(true, -1, -1);
    }

    public List<Persona> findPersonaEntities(int maxResults, int firstResult) {
        return findPersonaEntities(false, maxResults, firstResult);
    }

    private List<Persona> findPersonaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Persona.class));
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

    public Persona findPersona(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Persona.class, id);
        } finally {
            em.close();
        }
    }

    public int getPersonaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Persona> rt = cq.from(Persona.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
