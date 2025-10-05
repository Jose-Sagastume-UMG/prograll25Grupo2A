package miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.Class;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import miumg.edu.gt.proyectotiendalafelicidad.db.Factura;
import miumg.edu.gt.proyectotiendalafelicidad.db.Venta;
    import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;

public class FacturaC {

    private EntityManagerFactory emf;

    public FacturaC() {
        emf = Persistence.createEntityManagerFactory("TiendaLaFelicidad");
    }

    public Factura crearFactura(Venta venta) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Long count = em.createQuery("SELECT COUNT(f) FROM Factura f WHERE f.idVenta = :venta", Long.class)
                    .setParameter("venta", venta)
                    .getSingleResult();
            if (count > 0) {
                throw new Exception("Ya existe una factura para la venta con ID " + venta.getIdVenta());
            }

            Factura factura = new Factura();
            factura.setIdVenta(venta);
            factura.setFechaEmision(new Date());
            factura.setTotal(venta.getTotal() != null ? venta.getTotal() : BigDecimal.ZERO);

            em.persist(factura);

            em.getTransaction().commit();
            return factura;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new Exception("Error al crear la factura: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }


public void generarPDF(Factura factura, String rutaArchivo) throws Exception {
    Document document = new Document();
    PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo));
    document.open();

    // Título del documento xd
    Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    Paragraph titulo = new Paragraph("Factura", fontTitulo);
    titulo.setAlignment(Element.ALIGN_CENTER);
    document.add(titulo);

    document.add(new Paragraph(" "));
    
    // los datos de la factura
    document.add(new Paragraph("Factura ID: " + factura.getIdFactura()));
    document.add(new Paragraph("Venta ID: " + factura.getIdVenta().getIdVenta()));
    document.add(new Paragraph("Fecha de emisión: " + factura.getFechaEmision()));
    document.add(new Paragraph("Cliente: " + (factura.getIdVenta().getIdCliente() != null
            ? factura.getIdVenta().getIdCliente().getNombre() + " " + factura.getIdVenta().getIdCliente().getApellido()
            : "Consumidor Final")));
    document.add(new Paragraph(" "));

    // Tabla de productos 
    PdfPTable table = new PdfPTable(4); // 4 columnas
    table.setWidthPercentage(100);
    table.addCell("Producto");
    table.addCell("Cantidad");
    table.addCell("Precio Unitario");
    table.addCell("Subtotal");

    factura.getIdVenta().getDetalleVentaList().forEach(det -> {
        table.addCell(det.getIdProducto().getNombre());
        table.addCell(String.valueOf(det.getCantidad()));
        table.addCell(det.getPrecioUnitario().toString());
        table.addCell(det.getPrecioUnitario().multiply(BigDecimal.valueOf(det.getCantidad())).toString());
    });

    document.add(table);

    document.add(new Paragraph(" "));
    document.add(new Paragraph("Total: Q " + factura.getTotal()));

    document.close();
}
}