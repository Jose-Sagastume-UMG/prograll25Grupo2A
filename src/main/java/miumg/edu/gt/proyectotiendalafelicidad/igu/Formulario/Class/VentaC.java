package miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.Class;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import miumg.edu.gt.proyectotiendalafelicidad.db.Venta;
import miumg.edu.gt.proyectotiendalafelicidad.db.DetalleVenta;
import miumg.edu.gt.proyectotiendalafelicidad.db.Producto;
import miumg.edu.gt.proyectotiendalafelicidad.db.Cliente;
import miumg.edu.gt.proyectotiendalafelicidad.db.Usuario;

public class VentaC {

    private EntityManagerFactory emf;

    public VentaC() {
        emf = Persistence.createEntityManagerFactory("TiendaLaFelicidad");
    }


    public Venta crearVenta(Usuario usuario, Cliente cliente, List<DetalleVenta> detalles) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Venta venta = new Venta();
            venta.setFecha(new Date());
            venta.setIdUsuario(usuario);
            venta.setIdCliente(cliente);

            BigDecimal total = BigDecimal.ZERO;
            for (DetalleVenta detalle : detalles) {

                detalle.setIdVenta(venta);


                BigDecimal subTotal = detalle.getPrecioUnitario()
                        .multiply(BigDecimal.valueOf(detalle.getCantidad()));
                detalle.setSubtotal(subTotal);

                total = total.add(subTotal);

                Producto p = em.find(Producto.class, detalle.getIdProducto().getIdProducto());
                if (p.getStock() < detalle.getCantidad()) {
                    throw new Exception("No hay suficiente stock para el producto: " + p.getNombre());
                }
                p.setStock(p.getStock() - detalle.getCantidad());
                em.merge(p);

                em.persist(detalle);
            }

            venta.setTotal(total);
            venta.setDetalleVentaList(detalles);

            em.persist(venta);

            em.getTransaction().commit();
            return venta;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new Exception("Error al crear la venta: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }


public List<Venta> listarVentasDelDia(Date fecha) {
    EntityManager em = emf.createEntityManager();
    try {

        Date inicio = Date.from(
            fecha.toInstant().atZone(java.time.ZoneId.systemDefault())
                 .toLocalDate().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()
        );


        Date fin = Date.from(
            fecha.toInstant().atZone(java.time.ZoneId.systemDefault())
                 .toLocalDate().atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant()
        );

        return em.createQuery(
            "SELECT v FROM Venta v WHERE v.fecha BETWEEN :inicio AND :fin", Venta.class)
            .setParameter("inicio", inicio)
            .setParameter("fin", fin)
            .getResultList();

    } finally {
        em.close();
    }
}
}