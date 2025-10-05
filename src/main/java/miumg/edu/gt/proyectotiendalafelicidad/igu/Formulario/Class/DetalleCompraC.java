package miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.Class;

import java.math.BigDecimal;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import miumg.edu.gt.proyectotiendalafelicidad.db.Compra;
import miumg.edu.gt.proyectotiendalafelicidad.db.DetalleCompra;

public class DetalleCompraC {
    
    private EntityManagerFactory emf;

    public DetalleCompraC() {
        emf = Persistence.createEntityManagerFactory("TiendaLaFelicidad");
    }

    public void crearCompra(String proveedor, java.util.Date fecha, List<DetalleCompra> detalles) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Compra compra = new Compra();
            compra.setProveedor(proveedor);
            compra.setFecha(fecha);

            BigDecimal total = BigDecimal.ZERO;
            for (DetalleCompra detalle : detalles) {
                detalle.setIdCompra(compra);
                total = total.add(detalle.getSubTotal());
            }
            compra.setTotal(total);
            compra.setDetalleCompraList(detalles);

            em.persist(compra);
            for (DetalleCompra detalle : detalles) {
                em.persist(detalle);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}