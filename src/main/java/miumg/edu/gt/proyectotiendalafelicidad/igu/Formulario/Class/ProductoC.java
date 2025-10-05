package miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.Class;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import miumg.edu.gt.proyectotiendalafelicidad.DetalleCompraJpaController;
import miumg.edu.gt.proyectotiendalafelicidad.ProductoJpaController;
import miumg.edu.gt.proyectotiendalafelicidad.db.Categoria;
import miumg.edu.gt.proyectotiendalafelicidad.db.DetalleCompra;
import miumg.edu.gt.proyectotiendalafelicidad.db.Producto;

public class ProductoC {

    private EntityManagerFactory emf;
    private ProductoJpaController productoController;
    private DetalleCompraJpaController detalleCompraController;

    public ProductoC() {
        emf = Persistence.createEntityManagerFactory("TiendaLaFelicidad");
        productoController = new ProductoJpaController(emf);
        detalleCompraController = new DetalleCompraJpaController(emf);
    }

    public void crearOActualizarProducto(String nombre, String descripcion, int cantidad, Categoria categoriaSeleccionada, BigDecimal precioVenta) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {

            if (categoriaSeleccionada == null || categoriaSeleccionada.getIdCategoria() == null) {
                throw new Exception("La categoría seleccionada no es válida");
            }

            Producto productoExistente = productoController.findProductoEntities().stream()
                    .filter(p -> p.getIdProducto() != null
                            && p.getIdCategoria() != null
                            && p.getIdCategoria().getIdCategoria().equals(categoriaSeleccionada.getIdCategoria())
                            && p.getNombre() != null
                            && p.getNombre().equalsIgnoreCase(nombre))
                    .findFirst()
                    .orElse(null);

            if (productoExistente != null) {
                // Actualizar stock y precio si ya existe
                productoExistente.setStock(productoExistente.getStock() + cantidad);
                if (precioVenta != null) {
                    productoExistente.setPrecioVenta(precioVenta);
                }
                productoController.edit(productoExistente);
            } else {
                // Crear nuevo producto
                Producto nuevoProducto = new Producto();
                nuevoProducto.setNombre(nombre);
                nuevoProducto.setDescripcion(descripcion);
                nuevoProducto.setStock(cantidad);


                Categoria categoriaPersistida = em.getReference(Categoria.class, categoriaSeleccionada.getIdCategoria());
                nuevoProducto.setIdCategoria(categoriaPersistida);

                if (precioVenta != null) {
                    nuevoProducto.setPrecioVenta(precioVenta);
                }

                productoController.create(nuevoProducto);
            }
        } catch (Exception e) {
            throw new Exception("Error al crear o actualizar producto: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Producto> listarProductosPorCategoria(String nombreCategoria) {
        return productoController.findProductoEntities().stream()
                .filter(p -> p.getIdCategoria() != null
                        && p.getIdCategoria().getNombreCategoria().equalsIgnoreCase(nombreCategoria))
                .collect(Collectors.toList());
    }

    public void eliminarProductoConDetalles(int idProducto) throws Exception {
        Producto producto = productoController.findProducto(idProducto);
        if (producto != null) {

            producto.getDetalleCompraList().size();
            producto.getDetalleVentaList().size();

            List<DetalleCompra> detalles = detalleCompraController.findDetalleCompraEntities().stream()
                    .filter(d -> d.getIdProducto() != null
                            && d.getIdProducto().getIdProducto().equals(idProducto))
                    .collect(Collectors.toList());

            for (DetalleCompra d : detalles) {
                detalleCompraController.destroy(d.getIdDetalleCompra());
            }

            productoController.destroy(idProducto);
        }
    }

    public void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
    public Producto obtenerProductoPorNombre(String nombre) {
    return productoController.findProductoEntities().stream()
            .filter(p -> p.getNombre() != null && p.getNombre().equalsIgnoreCase(nombre))
            .findFirst()
            .orElse(null);
}
}