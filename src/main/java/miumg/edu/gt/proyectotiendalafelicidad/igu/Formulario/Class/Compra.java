package miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.Class;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import miumg.edu.gt.proyectotiendalafelicidad.CategoriaJpaController;
import miumg.edu.gt.proyectotiendalafelicidad.db.Categoria;
import miumg.edu.gt.proyectotiendalafelicidad.db.Producto;
import java.util.List;

public class Compra {


    public static class CompraProducto {

  
        private static final EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("TiendaLaFelicidad");

        private final CategoriaJpaController categoriaController;

        public CompraProducto() {
            categoriaController = new CategoriaJpaController(emf);
        }


        public List<Categoria> obtenerCategorias() {
            return categoriaController.findCategoriaEntities();
        }


        public void crearNuevaCategoria(String nombre) {
            Categoria c = new Categoria();
            c.setNombreCategoria(nombre);
            categoriaController.create(c);
        }


        public Categoria obtenerOCrearCategoria(EntityManager em, String nombreCategoria) {
            try {
                return em.createQuery(
                        "SELECT c FROM Categoria c WHERE c.nombreCategoria = :nombre",
                        Categoria.class)
                        .setParameter("nombre", nombreCategoria)
                        .getSingleResult();
            } catch (NoResultException ex) {
                Categoria categoria = new Categoria();
                categoria.setNombreCategoria(nombreCategoria);
                em.persist(categoria);
                return categoria;
            }
        }

        
        public Producto obtenerOCrearProducto(EntityManager em, String detalleTexto, String nombreCategoria,
                int cantidad, Categoria categoria) {

            String nombreProductoFinal = (detalleTexto == null || detalleTexto.isEmpty())
                    ? "Producto de " + nombreCategoria
                    : detalleTexto;
            try {
              
                Producto producto = em.createQuery(
                        "SELECT p FROM Producto p WHERE p.nombre = :nombre AND p.idCategoria = :cat",
                        Producto.class)
                        .setParameter("nombre", nombreProductoFinal)
                        .setParameter("cat", categoria)
                        .getSingleResult();

            
                Integer stockActual = producto.getStock();
                producto.setStock((stockActual == null ? 0 : stockActual) + cantidad);

           
                producto.setDescripcion(detalleTexto);

         
                em.merge(producto);
                return producto;

            } catch (NoResultException e) {

                Producto producto = new Producto();
                producto.setNombre(nombreProductoFinal);
                producto.setDescripcion(detalleTexto);
                producto.setStock(cantidad);
                producto.setIdCategoria(categoria);
                em.persist(producto);
                return producto;
            }
        }

 
        public EntityManager getEntityManager() {
            return emf.createEntityManager();
        }
    }
}
