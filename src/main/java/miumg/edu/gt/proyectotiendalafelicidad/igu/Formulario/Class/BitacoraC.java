package miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.Class;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import miumg.edu.gt.proyectotiendalafelicidad.BitacoraJpaController;
import miumg.edu.gt.proyectotiendalafelicidad.db.Bitacora;
import miumg.edu.gt.proyectotiendalafelicidad.db.Usuario;

public class BitacoraC {

    private EntityManagerFactory emf;
    private BitacoraJpaController bitacoraController;

    public BitacoraC() {
        emf = Persistence.createEntityManagerFactory("TiendaLaFelicidad");
        bitacoraController = new BitacoraJpaController(emf);
    }


    public void registrarAccion(Usuario usuario, String accion) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            if (usuario == null || usuario.getIdUsuario() == null) {
                throw new Exception("Usuario inválido para registrar acción");
            }

            Bitacora bitacora = new Bitacora();
            bitacora.setIdUsuario(usuario);
            bitacora.setFechaHora(new Date());
            bitacora.setAccion(accion);

            bitacoraController.create(bitacora);
        } catch (Exception e) {
            throw new Exception("Error al registrar acción en bitácora: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }


    public List<Bitacora> listarPorUsuario(Usuario usuario) {
        return bitacoraController.findBitacoraEntities().stream()
                .filter(b -> b.getIdUsuario() != null
                        && b.getIdUsuario().getIdUsuario().equals(usuario.getIdUsuario()))
                .collect(Collectors.toList());
    }

    public List<Bitacora> listarTodas() {
        return bitacoraController.findBitacoraEntities();
    }

    public void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}