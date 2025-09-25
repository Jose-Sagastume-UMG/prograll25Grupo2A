/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package miumg.edu.gt.proyectotiendalafelicidad;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import miumg.edu.gt.proyectotiendalafelicidad.db.Bitacora;
import miumg.edu.gt.proyectotiendalafelicidad.db.Usuario;
import miumg.edu.gt.proyectotiendalafelicidad.exceptions.NonexistentEntityException;

/**
 *
 * @author Jose
 */
public class BitacoraJpaController implements Serializable {

    public BitacoraJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Bitacora bitacora) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario idUsuario = bitacora.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getIdUsuario());
                bitacora.setIdUsuario(idUsuario);
            }
            em.persist(bitacora);
            if (idUsuario != null) {
                idUsuario.getBitacoraList().add(bitacora);
                idUsuario = em.merge(idUsuario);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Bitacora bitacora) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Bitacora persistentBitacora = em.find(Bitacora.class, bitacora.getIdBitacora());
            Usuario idUsuarioOld = persistentBitacora.getIdUsuario();
            Usuario idUsuarioNew = bitacora.getIdUsuario();
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdUsuario());
                bitacora.setIdUsuario(idUsuarioNew);
            }
            bitacora = em.merge(bitacora);
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getBitacoraList().remove(bitacora);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getBitacoraList().add(bitacora);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = bitacora.getIdBitacora();
                if (findBitacora(id) == null) {
                    throw new NonexistentEntityException("The bitacora with id " + id + " no longer exists.");
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
            Bitacora bitacora;
            try {
                bitacora = em.getReference(Bitacora.class, id);
                bitacora.getIdBitacora();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The bitacora with id " + id + " no longer exists.", enfe);
            }
            Usuario idUsuario = bitacora.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getBitacoraList().remove(bitacora);
                idUsuario = em.merge(idUsuario);
            }
            em.remove(bitacora);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Bitacora> findBitacoraEntities() {
        return findBitacoraEntities(true, -1, -1);
    }

    public List<Bitacora> findBitacoraEntities(int maxResults, int firstResult) {
        return findBitacoraEntities(false, maxResults, firstResult);
    }

    private List<Bitacora> findBitacoraEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Bitacora.class));
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

    public Bitacora findBitacora(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Bitacora.class, id);
        } finally {
            em.close();
        }
    }

    public int getBitacoraCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Bitacora> rt = cq.from(Bitacora.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
