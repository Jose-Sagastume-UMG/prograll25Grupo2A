package gt.edu.miumg.proyectotiendalafelicidad.igu;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import gt.edu.miumg.proyectotiendalafelicidad.bd.Usuario;

public class ELogin {

    // Método que valida usuario con JPA
    public Usuario verificarUsuario(String userName, String password) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("TiendaLaFelicidad");
        EntityManager em = emf.createEntityManager();
        Usuario usuario = null;

        try {
            usuario = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.userName = :userName AND u.password = :password", Usuario.class)
                    .setParameter("userName", userName)
                    .setParameter("password", password)
                    .getSingleResult();
        } catch (Exception e) {
            // Si no encuentra usuario o hay error, devuelve null
            usuario = null;
        } finally {
            em.close();
            emf.close();
        }

        return usuario;
    }
}
