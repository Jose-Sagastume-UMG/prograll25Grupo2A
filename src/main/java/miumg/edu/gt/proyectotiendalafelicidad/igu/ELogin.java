package miumg.edu.gt.proyectotiendalafelicidad.igu;

import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import miumg.edu.gt.proyectotiendalafelicidad.db.Bitacora;
import miumg.edu.gt.proyectotiendalafelicidad.db.Usuario;

public class ELogin {

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

      
            if (usuario != null && usuario.getIdRol() != null && usuario.getIdRol().getIdRol() != 0) {
                em.getTransaction().begin();

                Bitacora log = new Bitacora();
                log.setIdUsuario(usuario);
                log.setFechaHora(new Date());
                log.setAccion("Inicio de sesión");

                em.persist(log);
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            usuario = null;
        } finally {
            em.close();
            emf.close();
        }

        return usuario;
    }
}
