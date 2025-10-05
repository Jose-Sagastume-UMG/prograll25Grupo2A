    package miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.Class;

    import java.util.List;
    import javax.persistence.EntityManager;
    import javax.persistence.EntityManagerFactory;
    import javax.persistence.Persistence;
    import miumg.edu.gt.proyectotiendalafelicidad.CategoriaJpaController;
    import miumg.edu.gt.proyectotiendalafelicidad.db.Categoria;

 
    public class CategoriaC {

        private EntityManagerFactory emf;
        private CategoriaJpaController categoriaController;

        public CategoriaC() {
            emf = Persistence.createEntityManagerFactory("TiendaLaFelicidad"); 
            categoriaController = new CategoriaJpaController(emf);
        }


        public Categoria crearCategoria(String nombreCategoria) throws Exception {
            if (nombreCategoria == null || nombreCategoria.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
            }

            Categoria nuevaCategoria = new Categoria();
            nuevaCategoria.setNombreCategoria(nombreCategoria);
            categoriaController.create(nuevaCategoria); 
            return nuevaCategoria; 
        }


        public Categoria buscarCategoriaPorNombre(String nombreCategoria) {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery(
                        "SELECT c FROM Categoria c WHERE c.nombreCategoria = :nombre", Categoria.class)
                         .setParameter("nombre", nombreCategoria)
                         .getSingleResult();
            } catch (Exception e) {
                return null;
            } finally {
                em.close();
            }
        }


        public List<Categoria> listarCategorias() {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery("SELECT c FROM Categoria c", Categoria.class)
                         .getResultList();
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