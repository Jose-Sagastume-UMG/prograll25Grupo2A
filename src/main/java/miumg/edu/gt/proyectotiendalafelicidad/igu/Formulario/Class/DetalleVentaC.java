package miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.Class;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import miumg.edu.gt.proyectotiendalafelicidad.DetalleVentaJpaController;
import miumg.edu.gt.proyectotiendalafelicidad.db.DetalleVenta;
import miumg.edu.gt.proyectotiendalafelicidad.db.Producto;
import miumg.edu.gt.proyectotiendalafelicidad.db.Venta;

public class DetalleVentaC {

    private EntityManagerFactory emf;
    private DetalleVentaJpaController detalleVentaController;

    public DetalleVentaC() {
        emf = Persistence.createEntityManagerFactory("TiendaLaFelicidad");
        detalleVentaController = new DetalleVentaJpaController(emf);
    }

    public void crearDetalleVenta(Venta venta, Producto producto, int cantidad, BigDecimal precioUnitario) throws Exception {
        if (venta == null) {
            throw new Exception("La venta no puede ser null");
        }
        if (producto == null) {
            throw new Exception("El producto no puede ser null");
        }
        if (cantidad <= 0) {
            throw new Exception("La cantidad debe ser mayor a cero");
        }
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("El precio unitario no es válido");
        }

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            DetalleVenta detalle = new DetalleVenta();
            detalle.setIdVenta(venta);
            detalle.setIdProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubtotal(precioUnitario.multiply(BigDecimal.valueOf(cantidad)));

            em.persist(detalle);

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new Exception("Error al crear detalle de venta: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    
public List<DetalleVenta> listarDetallesPorVenta(int idVenta) {
    return detalleVentaController.findDetalleVentaEntities().stream()
            .filter(d -> d.getIdVenta() != null && d.getIdVenta().getIdVenta() == idVenta)
            .collect(Collectors.toList()); 
}

    public void eliminarDetalleVenta(int idDetalleVenta) throws Exception {
        detalleVentaController.destroy(idDetalleVenta);
    }

    public void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}