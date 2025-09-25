/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package miumg.edu.gt.proyectotiendalafelicidad;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import miumg.edu.gt.proyectotiendalafelicidad.db.Rol;
import miumg.edu.gt.proyectotiendalafelicidad.db.Venta;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import miumg.edu.gt.proyectotiendalafelicidad.db.Bitacora;
import miumg.edu.gt.proyectotiendalafelicidad.db.Usuario;
import miumg.edu.gt.proyectotiendalafelicidad.exceptions.IllegalOrphanException;
import miumg.edu.gt.proyectotiendalafelicidad.exceptions.NonexistentEntityException;

/**
 *
 * @author Jose
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) {
        if (usuario.getVentaList() == null) {
            usuario.setVentaList(new ArrayList<Venta>());
        }
        if (usuario.getBitacoraList() == null) {
            usuario.setBitacoraList(new ArrayList<Bitacora>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Rol idRol = usuario.getIdRol();
            if (idRol != null) {
                idRol = em.getReference(idRol.getClass(), idRol.getIdRol());
                usuario.setIdRol(idRol);
            }
            List<Venta> attachedVentaList = new ArrayList<Venta>();
            for (Venta ventaListVentaToAttach : usuario.getVentaList()) {
                ventaListVentaToAttach = em.getReference(ventaListVentaToAttach.getClass(), ventaListVentaToAttach.getIdVenta());
                attachedVentaList.add(ventaListVentaToAttach);
            }
            usuario.setVentaList(attachedVentaList);
            List<Bitacora> attachedBitacoraList = new ArrayList<Bitacora>();
            for (Bitacora bitacoraListBitacoraToAttach : usuario.getBitacoraList()) {
                bitacoraListBitacoraToAttach = em.getReference(bitacoraListBitacoraToAttach.getClass(), bitacoraListBitacoraToAttach.getIdBitacora());
                attachedBitacoraList.add(bitacoraListBitacoraToAttach);
            }
            usuario.setBitacoraList(attachedBitacoraList);
            em.persist(usuario);
            if (idRol != null) {
                idRol.getUsuarioList().add(usuario);
                idRol = em.merge(idRol);
            }
            for (Venta ventaListVenta : usuario.getVentaList()) {
                Usuario oldIdUsuarioOfVentaListVenta = ventaListVenta.getIdUsuario();
                ventaListVenta.setIdUsuario(usuario);
                ventaListVenta = em.merge(ventaListVenta);
                if (oldIdUsuarioOfVentaListVenta != null) {
                    oldIdUsuarioOfVentaListVenta.getVentaList().remove(ventaListVenta);
                    oldIdUsuarioOfVentaListVenta = em.merge(oldIdUsuarioOfVentaListVenta);
                }
            }
            for (Bitacora bitacoraListBitacora : usuario.getBitacoraList()) {
                Usuario oldIdUsuarioOfBitacoraListBitacora = bitacoraListBitacora.getIdUsuario();
                bitacoraListBitacora.setIdUsuario(usuario);
                bitacoraListBitacora = em.merge(bitacoraListBitacora);
                if (oldIdUsuarioOfBitacoraListBitacora != null) {
                    oldIdUsuarioOfBitacoraListBitacora.getBitacoraList().remove(bitacoraListBitacora);
                    oldIdUsuarioOfBitacoraListBitacora = em.merge(oldIdUsuarioOfBitacoraListBitacora);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getIdUsuario());
            Rol idRolOld = persistentUsuario.getIdRol();
            Rol idRolNew = usuario.getIdRol();
            List<Venta> ventaListOld = persistentUsuario.getVentaList();
            List<Venta> ventaListNew = usuario.getVentaList();
            List<Bitacora> bitacoraListOld = persistentUsuario.getBitacoraList();
            List<Bitacora> bitacoraListNew = usuario.getBitacoraList();
            List<String> illegalOrphanMessages = null;
            for (Bitacora bitacoraListOldBitacora : bitacoraListOld) {
                if (!bitacoraListNew.contains(bitacoraListOldBitacora)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Bitacora " + bitacoraListOldBitacora + " since its idUsuario field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idRolNew != null) {
                idRolNew = em.getReference(idRolNew.getClass(), idRolNew.getIdRol());
                usuario.setIdRol(idRolNew);
            }
            List<Venta> attachedVentaListNew = new ArrayList<Venta>();
            for (Venta ventaListNewVentaToAttach : ventaListNew) {
                ventaListNewVentaToAttach = em.getReference(ventaListNewVentaToAttach.getClass(), ventaListNewVentaToAttach.getIdVenta());
                attachedVentaListNew.add(ventaListNewVentaToAttach);
            }
            ventaListNew = attachedVentaListNew;
            usuario.setVentaList(ventaListNew);
            List<Bitacora> attachedBitacoraListNew = new ArrayList<Bitacora>();
            for (Bitacora bitacoraListNewBitacoraToAttach : bitacoraListNew) {
                bitacoraListNewBitacoraToAttach = em.getReference(bitacoraListNewBitacoraToAttach.getClass(), bitacoraListNewBitacoraToAttach.getIdBitacora());
                attachedBitacoraListNew.add(bitacoraListNewBitacoraToAttach);
            }
            bitacoraListNew = attachedBitacoraListNew;
            usuario.setBitacoraList(bitacoraListNew);
            usuario = em.merge(usuario);
            if (idRolOld != null && !idRolOld.equals(idRolNew)) {
                idRolOld.getUsuarioList().remove(usuario);
                idRolOld = em.merge(idRolOld);
            }
            if (idRolNew != null && !idRolNew.equals(idRolOld)) {
                idRolNew.getUsuarioList().add(usuario);
                idRolNew = em.merge(idRolNew);
            }
            for (Venta ventaListOldVenta : ventaListOld) {
                if (!ventaListNew.contains(ventaListOldVenta)) {
                    ventaListOldVenta.setIdUsuario(null);
                    ventaListOldVenta = em.merge(ventaListOldVenta);
                }
            }
            for (Venta ventaListNewVenta : ventaListNew) {
                if (!ventaListOld.contains(ventaListNewVenta)) {
                    Usuario oldIdUsuarioOfVentaListNewVenta = ventaListNewVenta.getIdUsuario();
                    ventaListNewVenta.setIdUsuario(usuario);
                    ventaListNewVenta = em.merge(ventaListNewVenta);
                    if (oldIdUsuarioOfVentaListNewVenta != null && !oldIdUsuarioOfVentaListNewVenta.equals(usuario)) {
                        oldIdUsuarioOfVentaListNewVenta.getVentaList().remove(ventaListNewVenta);
                        oldIdUsuarioOfVentaListNewVenta = em.merge(oldIdUsuarioOfVentaListNewVenta);
                    }
                }
            }
            for (Bitacora bitacoraListNewBitacora : bitacoraListNew) {
                if (!bitacoraListOld.contains(bitacoraListNewBitacora)) {
                    Usuario oldIdUsuarioOfBitacoraListNewBitacora = bitacoraListNewBitacora.getIdUsuario();
                    bitacoraListNewBitacora.setIdUsuario(usuario);
                    bitacoraListNewBitacora = em.merge(bitacoraListNewBitacora);
                    if (oldIdUsuarioOfBitacoraListNewBitacora != null && !oldIdUsuarioOfBitacoraListNewBitacora.equals(usuario)) {
                        oldIdUsuarioOfBitacoraListNewBitacora.getBitacoraList().remove(bitacoraListNewBitacora);
                        oldIdUsuarioOfBitacoraListNewBitacora = em.merge(oldIdUsuarioOfBitacoraListNewBitacora);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = usuario.getIdUsuario();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
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
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getIdUsuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Bitacora> bitacoraListOrphanCheck = usuario.getBitacoraList();
            for (Bitacora bitacoraListOrphanCheckBitacora : bitacoraListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Bitacora " + bitacoraListOrphanCheckBitacora + " in its bitacoraList field has a non-nullable idUsuario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Rol idRol = usuario.getIdRol();
            if (idRol != null) {
                idRol.getUsuarioList().remove(usuario);
                idRol = em.merge(idRol);
            }
            List<Venta> ventaList = usuario.getVentaList();
            for (Venta ventaListVenta : ventaList) {
                ventaListVenta.setIdUsuario(null);
                ventaListVenta = em.merge(ventaListVenta);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
