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
import miumg.edu.gt.proyectotiendalafelicidad.db.Categoria;
import miumg.edu.gt.proyectotiendalafelicidad.db.DetalleVenta;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import miumg.edu.gt.proyectotiendalafelicidad.db.DetalleCompra;
import miumg.edu.gt.proyectotiendalafelicidad.db.Producto;
import miumg.edu.gt.proyectotiendalafelicidad.exceptions.IllegalOrphanException;
import miumg.edu.gt.proyectotiendalafelicidad.exceptions.NonexistentEntityException;

/**
 *
 * @author Jose
 */
public class ProductoJpaController implements Serializable {

    public ProductoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Producto producto) {
        if (producto.getDetalleVentaList() == null) {
            producto.setDetalleVentaList(new ArrayList<DetalleVenta>());
        }
        if (producto.getDetalleCompraList() == null) {
            producto.setDetalleCompraList(new ArrayList<DetalleCompra>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Categoria idCategoria = producto.getIdCategoria();
            if (idCategoria != null) {
                idCategoria = em.getReference(idCategoria.getClass(), idCategoria.getIdCategoria());
                producto.setIdCategoria(idCategoria);
            }
            List<DetalleVenta> attachedDetalleVentaList = new ArrayList<DetalleVenta>();
            for (DetalleVenta detalleVentaListDetalleVentaToAttach : producto.getDetalleVentaList()) {
                detalleVentaListDetalleVentaToAttach = em.getReference(detalleVentaListDetalleVentaToAttach.getClass(), detalleVentaListDetalleVentaToAttach.getIdDetalleVenta());
                attachedDetalleVentaList.add(detalleVentaListDetalleVentaToAttach);
            }
            producto.setDetalleVentaList(attachedDetalleVentaList);
            List<DetalleCompra> attachedDetalleCompraList = new ArrayList<DetalleCompra>();
            for (DetalleCompra detalleCompraListDetalleCompraToAttach : producto.getDetalleCompraList()) {
                detalleCompraListDetalleCompraToAttach = em.getReference(detalleCompraListDetalleCompraToAttach.getClass(), detalleCompraListDetalleCompraToAttach.getIdDetalleCompra());
                attachedDetalleCompraList.add(detalleCompraListDetalleCompraToAttach);
            }
            producto.setDetalleCompraList(attachedDetalleCompraList);
            em.persist(producto);
            if (idCategoria != null) {
                idCategoria.getProductoList().add(producto);
                idCategoria = em.merge(idCategoria);
            }
            for (DetalleVenta detalleVentaListDetalleVenta : producto.getDetalleVentaList()) {
                Producto oldIdProductoOfDetalleVentaListDetalleVenta = detalleVentaListDetalleVenta.getIdProducto();
                detalleVentaListDetalleVenta.setIdProducto(producto);
                detalleVentaListDetalleVenta = em.merge(detalleVentaListDetalleVenta);
                if (oldIdProductoOfDetalleVentaListDetalleVenta != null) {
                    oldIdProductoOfDetalleVentaListDetalleVenta.getDetalleVentaList().remove(detalleVentaListDetalleVenta);
                    oldIdProductoOfDetalleVentaListDetalleVenta = em.merge(oldIdProductoOfDetalleVentaListDetalleVenta);
                }
            }
            for (DetalleCompra detalleCompraListDetalleCompra : producto.getDetalleCompraList()) {
                Producto oldIdProductoOfDetalleCompraListDetalleCompra = detalleCompraListDetalleCompra.getIdProducto();
                detalleCompraListDetalleCompra.setIdProducto(producto);
                detalleCompraListDetalleCompra = em.merge(detalleCompraListDetalleCompra);
                if (oldIdProductoOfDetalleCompraListDetalleCompra != null) {
                    oldIdProductoOfDetalleCompraListDetalleCompra.getDetalleCompraList().remove(detalleCompraListDetalleCompra);
                    oldIdProductoOfDetalleCompraListDetalleCompra = em.merge(oldIdProductoOfDetalleCompraListDetalleCompra);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Producto producto) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Producto persistentProducto = em.find(Producto.class, producto.getIdProducto());
            Categoria idCategoriaOld = persistentProducto.getIdCategoria();
            Categoria idCategoriaNew = producto.getIdCategoria();
            List<DetalleVenta> detalleVentaListOld = persistentProducto.getDetalleVentaList();
            List<DetalleVenta> detalleVentaListNew = producto.getDetalleVentaList();
            List<DetalleCompra> detalleCompraListOld = persistentProducto.getDetalleCompraList();
            List<DetalleCompra> detalleCompraListNew = producto.getDetalleCompraList();
            List<String> illegalOrphanMessages = null;
            for (DetalleVenta detalleVentaListOldDetalleVenta : detalleVentaListOld) {
                if (!detalleVentaListNew.contains(detalleVentaListOldDetalleVenta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DetalleVenta " + detalleVentaListOldDetalleVenta + " since its idProducto field is not nullable.");
                }
            }
            for (DetalleCompra detalleCompraListOldDetalleCompra : detalleCompraListOld) {
                if (!detalleCompraListNew.contains(detalleCompraListOldDetalleCompra)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DetalleCompra " + detalleCompraListOldDetalleCompra + " since its idProducto field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idCategoriaNew != null) {
                idCategoriaNew = em.getReference(idCategoriaNew.getClass(), idCategoriaNew.getIdCategoria());
                producto.setIdCategoria(idCategoriaNew);
            }
            List<DetalleVenta> attachedDetalleVentaListNew = new ArrayList<DetalleVenta>();
            for (DetalleVenta detalleVentaListNewDetalleVentaToAttach : detalleVentaListNew) {
                detalleVentaListNewDetalleVentaToAttach = em.getReference(detalleVentaListNewDetalleVentaToAttach.getClass(), detalleVentaListNewDetalleVentaToAttach.getIdDetalleVenta());
                attachedDetalleVentaListNew.add(detalleVentaListNewDetalleVentaToAttach);
            }
            detalleVentaListNew = attachedDetalleVentaListNew;
            producto.setDetalleVentaList(detalleVentaListNew);
            List<DetalleCompra> attachedDetalleCompraListNew = new ArrayList<DetalleCompra>();
            for (DetalleCompra detalleCompraListNewDetalleCompraToAttach : detalleCompraListNew) {
                detalleCompraListNewDetalleCompraToAttach = em.getReference(detalleCompraListNewDetalleCompraToAttach.getClass(), detalleCompraListNewDetalleCompraToAttach.getIdDetalleCompra());
                attachedDetalleCompraListNew.add(detalleCompraListNewDetalleCompraToAttach);
            }
            detalleCompraListNew = attachedDetalleCompraListNew;
            producto.setDetalleCompraList(detalleCompraListNew);
            producto = em.merge(producto);
            if (idCategoriaOld != null && !idCategoriaOld.equals(idCategoriaNew)) {
                idCategoriaOld.getProductoList().remove(producto);
                idCategoriaOld = em.merge(idCategoriaOld);
            }
            if (idCategoriaNew != null && !idCategoriaNew.equals(idCategoriaOld)) {
                idCategoriaNew.getProductoList().add(producto);
                idCategoriaNew = em.merge(idCategoriaNew);
            }
            for (DetalleVenta detalleVentaListNewDetalleVenta : detalleVentaListNew) {
                if (!detalleVentaListOld.contains(detalleVentaListNewDetalleVenta)) {
                    Producto oldIdProductoOfDetalleVentaListNewDetalleVenta = detalleVentaListNewDetalleVenta.getIdProducto();
                    detalleVentaListNewDetalleVenta.setIdProducto(producto);
                    detalleVentaListNewDetalleVenta = em.merge(detalleVentaListNewDetalleVenta);
                    if (oldIdProductoOfDetalleVentaListNewDetalleVenta != null && !oldIdProductoOfDetalleVentaListNewDetalleVenta.equals(producto)) {
                        oldIdProductoOfDetalleVentaListNewDetalleVenta.getDetalleVentaList().remove(detalleVentaListNewDetalleVenta);
                        oldIdProductoOfDetalleVentaListNewDetalleVenta = em.merge(oldIdProductoOfDetalleVentaListNewDetalleVenta);
                    }
                }
            }
            for (DetalleCompra detalleCompraListNewDetalleCompra : detalleCompraListNew) {
                if (!detalleCompraListOld.contains(detalleCompraListNewDetalleCompra)) {
                    Producto oldIdProductoOfDetalleCompraListNewDetalleCompra = detalleCompraListNewDetalleCompra.getIdProducto();
                    detalleCompraListNewDetalleCompra.setIdProducto(producto);
                    detalleCompraListNewDetalleCompra = em.merge(detalleCompraListNewDetalleCompra);
                    if (oldIdProductoOfDetalleCompraListNewDetalleCompra != null && !oldIdProductoOfDetalleCompraListNewDetalleCompra.equals(producto)) {
                        oldIdProductoOfDetalleCompraListNewDetalleCompra.getDetalleCompraList().remove(detalleCompraListNewDetalleCompra);
                        oldIdProductoOfDetalleCompraListNewDetalleCompra = em.merge(oldIdProductoOfDetalleCompraListNewDetalleCompra);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = producto.getIdProducto();
                if (findProducto(id) == null) {
                    throw new NonexistentEntityException("The producto with id " + id + " no longer exists.");
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
            Producto producto;
            try {
                producto = em.getReference(Producto.class, id);
                producto.getIdProducto();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The producto with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<DetalleVenta> detalleVentaListOrphanCheck = producto.getDetalleVentaList();
            for (DetalleVenta detalleVentaListOrphanCheckDetalleVenta : detalleVentaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Producto (" + producto + ") cannot be destroyed since the DetalleVenta " + detalleVentaListOrphanCheckDetalleVenta + " in its detalleVentaList field has a non-nullable idProducto field.");
            }
            List<DetalleCompra> detalleCompraListOrphanCheck = producto.getDetalleCompraList();
            for (DetalleCompra detalleCompraListOrphanCheckDetalleCompra : detalleCompraListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Producto (" + producto + ") cannot be destroyed since the DetalleCompra " + detalleCompraListOrphanCheckDetalleCompra + " in its detalleCompraList field has a non-nullable idProducto field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Categoria idCategoria = producto.getIdCategoria();
            if (idCategoria != null) {
                idCategoria.getProductoList().remove(producto);
                idCategoria = em.merge(idCategoria);
            }
            em.remove(producto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Producto> findProductoEntities() {
        return findProductoEntities(true, -1, -1);
    }

    public List<Producto> findProductoEntities(int maxResults, int firstResult) {
        return findProductoEntities(false, maxResults, firstResult);
    }

    private List<Producto> findProductoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Producto.class));
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

    public Producto findProducto(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Producto.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Producto> rt = cq.from(Producto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
