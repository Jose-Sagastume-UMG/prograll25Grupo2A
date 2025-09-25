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
import miumg.edu.gt.proyectotiendalafelicidad.db.Compra;
import miumg.edu.gt.proyectotiendalafelicidad.db.DetalleCompra;
import miumg.edu.gt.proyectotiendalafelicidad.db.Producto;
import miumg.edu.gt.proyectotiendalafelicidad.exceptions.NonexistentEntityException;

/**
 *
 * @author Jose
 */
public class DetalleCompraJpaController implements Serializable {

    public DetalleCompraJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DetalleCompra detalleCompra) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Compra idCompra = detalleCompra.getIdCompra();
            if (idCompra != null) {
                idCompra = em.getReference(idCompra.getClass(), idCompra.getIdCompra());
                detalleCompra.setIdCompra(idCompra);
            }
            Producto idProducto = detalleCompra.getIdProducto();
            if (idProducto != null) {
                idProducto = em.getReference(idProducto.getClass(), idProducto.getIdProducto());
                detalleCompra.setIdProducto(idProducto);
            }
            em.persist(detalleCompra);
            if (idCompra != null) {
                idCompra.getDetalleCompraList().add(detalleCompra);
                idCompra = em.merge(idCompra);
            }
            if (idProducto != null) {
                idProducto.getDetalleCompraList().add(detalleCompra);
                idProducto = em.merge(idProducto);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DetalleCompra detalleCompra) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DetalleCompra persistentDetalleCompra = em.find(DetalleCompra.class, detalleCompra.getIdDetalleCompra());
            Compra idCompraOld = persistentDetalleCompra.getIdCompra();
            Compra idCompraNew = detalleCompra.getIdCompra();
            Producto idProductoOld = persistentDetalleCompra.getIdProducto();
            Producto idProductoNew = detalleCompra.getIdProducto();
            if (idCompraNew != null) {
                idCompraNew = em.getReference(idCompraNew.getClass(), idCompraNew.getIdCompra());
                detalleCompra.setIdCompra(idCompraNew);
            }
            if (idProductoNew != null) {
                idProductoNew = em.getReference(idProductoNew.getClass(), idProductoNew.getIdProducto());
                detalleCompra.setIdProducto(idProductoNew);
            }
            detalleCompra = em.merge(detalleCompra);
            if (idCompraOld != null && !idCompraOld.equals(idCompraNew)) {
                idCompraOld.getDetalleCompraList().remove(detalleCompra);
                idCompraOld = em.merge(idCompraOld);
            }
            if (idCompraNew != null && !idCompraNew.equals(idCompraOld)) {
                idCompraNew.getDetalleCompraList().add(detalleCompra);
                idCompraNew = em.merge(idCompraNew);
            }
            if (idProductoOld != null && !idProductoOld.equals(idProductoNew)) {
                idProductoOld.getDetalleCompraList().remove(detalleCompra);
                idProductoOld = em.merge(idProductoOld);
            }
            if (idProductoNew != null && !idProductoNew.equals(idProductoOld)) {
                idProductoNew.getDetalleCompraList().add(detalleCompra);
                idProductoNew = em.merge(idProductoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = detalleCompra.getIdDetalleCompra();
                if (findDetalleCompra(id) == null) {
                    throw new NonexistentEntityException("The detalleCompra with id " + id + " no longer exists.");
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
            DetalleCompra detalleCompra;
            try {
                detalleCompra = em.getReference(DetalleCompra.class, id);
                detalleCompra.getIdDetalleCompra();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detalleCompra with id " + id + " no longer exists.", enfe);
            }
            Compra idCompra = detalleCompra.getIdCompra();
            if (idCompra != null) {
                idCompra.getDetalleCompraList().remove(detalleCompra);
                idCompra = em.merge(idCompra);
            }
            Producto idProducto = detalleCompra.getIdProducto();
            if (idProducto != null) {
                idProducto.getDetalleCompraList().remove(detalleCompra);
                idProducto = em.merge(idProducto);
            }
            em.remove(detalleCompra);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DetalleCompra> findDetalleCompraEntities() {
        return findDetalleCompraEntities(true, -1, -1);
    }

    public List<DetalleCompra> findDetalleCompraEntities(int maxResults, int firstResult) {
        return findDetalleCompraEntities(false, maxResults, firstResult);
    }

    private List<DetalleCompra> findDetalleCompraEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DetalleCompra.class));
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

    public DetalleCompra findDetalleCompra(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DetalleCompra.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetalleCompraCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DetalleCompra> rt = cq.from(DetalleCompra.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
