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
import miumg.edu.gt.proyectotiendalafelicidad.db.Cliente;
import miumg.edu.gt.proyectotiendalafelicidad.db.Usuario;
import miumg.edu.gt.proyectotiendalafelicidad.db.Factura;
import miumg.edu.gt.proyectotiendalafelicidad.db.DetalleVenta;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import miumg.edu.gt.proyectotiendalafelicidad.db.Venta;
import miumg.edu.gt.proyectotiendalafelicidad.exceptions.IllegalOrphanException;
import miumg.edu.gt.proyectotiendalafelicidad.exceptions.NonexistentEntityException;

/**
 *
 * @author Jose
 */
public class VentaJpaController implements Serializable {

    public VentaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Venta venta) {
        if (venta.getDetalleVentaList() == null) {
            venta.setDetalleVentaList(new ArrayList<DetalleVenta>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cliente idCliente = venta.getIdCliente();
            if (idCliente != null) {
                idCliente = em.getReference(idCliente.getClass(), idCliente.getIdCliente());
                venta.setIdCliente(idCliente);
            }
            Usuario idUsuario = venta.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getIdUsuario());
                venta.setIdUsuario(idUsuario);
            }
            Factura factura = venta.getFactura();
            if (factura != null) {
                factura = em.getReference(factura.getClass(), factura.getIdFactura());
                venta.setFactura(factura);
            }
            List<DetalleVenta> attachedDetalleVentaList = new ArrayList<DetalleVenta>();
            for (DetalleVenta detalleVentaListDetalleVentaToAttach : venta.getDetalleVentaList()) {
                detalleVentaListDetalleVentaToAttach = em.getReference(detalleVentaListDetalleVentaToAttach.getClass(), detalleVentaListDetalleVentaToAttach.getIdDetalleVenta());
                attachedDetalleVentaList.add(detalleVentaListDetalleVentaToAttach);
            }
            venta.setDetalleVentaList(attachedDetalleVentaList);
            em.persist(venta);
            if (idCliente != null) {
                idCliente.getVentaList().add(venta);
                idCliente = em.merge(idCliente);
            }
            if (idUsuario != null) {
                idUsuario.getVentaList().add(venta);
                idUsuario = em.merge(idUsuario);
            }
            if (factura != null) {
                Venta oldIdVentaOfFactura = factura.getIdVenta();
                if (oldIdVentaOfFactura != null) {
                    oldIdVentaOfFactura.setFactura(null);
                    oldIdVentaOfFactura = em.merge(oldIdVentaOfFactura);
                }
                factura.setIdVenta(venta);
                factura = em.merge(factura);
            }
            for (DetalleVenta detalleVentaListDetalleVenta : venta.getDetalleVentaList()) {
                Venta oldIdVentaOfDetalleVentaListDetalleVenta = detalleVentaListDetalleVenta.getIdVenta();
                detalleVentaListDetalleVenta.setIdVenta(venta);
                detalleVentaListDetalleVenta = em.merge(detalleVentaListDetalleVenta);
                if (oldIdVentaOfDetalleVentaListDetalleVenta != null) {
                    oldIdVentaOfDetalleVentaListDetalleVenta.getDetalleVentaList().remove(detalleVentaListDetalleVenta);
                    oldIdVentaOfDetalleVentaListDetalleVenta = em.merge(oldIdVentaOfDetalleVentaListDetalleVenta);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Venta venta) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Venta persistentVenta = em.find(Venta.class, venta.getIdVenta());
            Cliente idClienteOld = persistentVenta.getIdCliente();
            Cliente idClienteNew = venta.getIdCliente();
            Usuario idUsuarioOld = persistentVenta.getIdUsuario();
            Usuario idUsuarioNew = venta.getIdUsuario();
            Factura facturaOld = persistentVenta.getFactura();
            Factura facturaNew = venta.getFactura();
            List<DetalleVenta> detalleVentaListOld = persistentVenta.getDetalleVentaList();
            List<DetalleVenta> detalleVentaListNew = venta.getDetalleVentaList();
            List<String> illegalOrphanMessages = null;
            if (facturaOld != null && !facturaOld.equals(facturaNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Factura " + facturaOld + " since its idVenta field is not nullable.");
            }
            for (DetalleVenta detalleVentaListOldDetalleVenta : detalleVentaListOld) {
                if (!detalleVentaListNew.contains(detalleVentaListOldDetalleVenta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DetalleVenta " + detalleVentaListOldDetalleVenta + " since its idVenta field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idClienteNew != null) {
                idClienteNew = em.getReference(idClienteNew.getClass(), idClienteNew.getIdCliente());
                venta.setIdCliente(idClienteNew);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdUsuario());
                venta.setIdUsuario(idUsuarioNew);
            }
            if (facturaNew != null) {
                facturaNew = em.getReference(facturaNew.getClass(), facturaNew.getIdFactura());
                venta.setFactura(facturaNew);
            }
            List<DetalleVenta> attachedDetalleVentaListNew = new ArrayList<DetalleVenta>();
            for (DetalleVenta detalleVentaListNewDetalleVentaToAttach : detalleVentaListNew) {
                detalleVentaListNewDetalleVentaToAttach = em.getReference(detalleVentaListNewDetalleVentaToAttach.getClass(), detalleVentaListNewDetalleVentaToAttach.getIdDetalleVenta());
                attachedDetalleVentaListNew.add(detalleVentaListNewDetalleVentaToAttach);
            }
            detalleVentaListNew = attachedDetalleVentaListNew;
            venta.setDetalleVentaList(detalleVentaListNew);
            venta = em.merge(venta);
            if (idClienteOld != null && !idClienteOld.equals(idClienteNew)) {
                idClienteOld.getVentaList().remove(venta);
                idClienteOld = em.merge(idClienteOld);
            }
            if (idClienteNew != null && !idClienteNew.equals(idClienteOld)) {
                idClienteNew.getVentaList().add(venta);
                idClienteNew = em.merge(idClienteNew);
            }
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getVentaList().remove(venta);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getVentaList().add(venta);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            if (facturaNew != null && !facturaNew.equals(facturaOld)) {
                Venta oldIdVentaOfFactura = facturaNew.getIdVenta();
                if (oldIdVentaOfFactura != null) {
                    oldIdVentaOfFactura.setFactura(null);
                    oldIdVentaOfFactura = em.merge(oldIdVentaOfFactura);
                }
                facturaNew.setIdVenta(venta);
                facturaNew = em.merge(facturaNew);
            }
            for (DetalleVenta detalleVentaListNewDetalleVenta : detalleVentaListNew) {
                if (!detalleVentaListOld.contains(detalleVentaListNewDetalleVenta)) {
                    Venta oldIdVentaOfDetalleVentaListNewDetalleVenta = detalleVentaListNewDetalleVenta.getIdVenta();
                    detalleVentaListNewDetalleVenta.setIdVenta(venta);
                    detalleVentaListNewDetalleVenta = em.merge(detalleVentaListNewDetalleVenta);
                    if (oldIdVentaOfDetalleVentaListNewDetalleVenta != null && !oldIdVentaOfDetalleVentaListNewDetalleVenta.equals(venta)) {
                        oldIdVentaOfDetalleVentaListNewDetalleVenta.getDetalleVentaList().remove(detalleVentaListNewDetalleVenta);
                        oldIdVentaOfDetalleVentaListNewDetalleVenta = em.merge(oldIdVentaOfDetalleVentaListNewDetalleVenta);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = venta.getIdVenta();
                if (findVenta(id) == null) {
                    throw new NonexistentEntityException("The venta with id " + id + " no longer exists.");
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
            Venta venta;
            try {
                venta = em.getReference(Venta.class, id);
                venta.getIdVenta();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The venta with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Factura facturaOrphanCheck = venta.getFactura();
            if (facturaOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Venta (" + venta + ") cannot be destroyed since the Factura " + facturaOrphanCheck + " in its factura field has a non-nullable idVenta field.");
            }
            List<DetalleVenta> detalleVentaListOrphanCheck = venta.getDetalleVentaList();
            for (DetalleVenta detalleVentaListOrphanCheckDetalleVenta : detalleVentaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Venta (" + venta + ") cannot be destroyed since the DetalleVenta " + detalleVentaListOrphanCheckDetalleVenta + " in its detalleVentaList field has a non-nullable idVenta field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Cliente idCliente = venta.getIdCliente();
            if (idCliente != null) {
                idCliente.getVentaList().remove(venta);
                idCliente = em.merge(idCliente);
            }
            Usuario idUsuario = venta.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getVentaList().remove(venta);
                idUsuario = em.merge(idUsuario);
            }
            em.remove(venta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Venta> findVentaEntities() {
        return findVentaEntities(true, -1, -1);
    }

    public List<Venta> findVentaEntities(int maxResults, int firstResult) {
        return findVentaEntities(false, maxResults, firstResult);
    }

    private List<Venta> findVentaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Venta.class));
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

    public Venta findVenta(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Venta.class, id);
        } finally {
            em.close();
        }
    }

    public int getVentaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Venta> rt = cq.from(Venta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
