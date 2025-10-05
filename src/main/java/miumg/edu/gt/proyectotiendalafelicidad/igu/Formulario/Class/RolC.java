package miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.Class;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import miumg.edu.gt.proyectotiendalafelicidad.RolJpaController;
import miumg.edu.gt.proyectotiendalafelicidad.UsuarioJpaController;
import miumg.edu.gt.proyectotiendalafelicidad.db.Rol;
import miumg.edu.gt.proyectotiendalafelicidad.db.Usuario;

public class RolC {

    private EntityManagerFactory emf;
    private UsuarioJpaController usuarioController;
    private RolJpaController rolController;

    public RolC() {
        emf = Persistence.createEntityManagerFactory("TiendaLaFelicidad");
        usuarioController = new UsuarioJpaController(emf);
        rolController = new RolJpaController(emf);
    }

    // --- Usuarios ---
    public List<Usuario> listarUsuarios() {
        return usuarioController.findUsuarioEntities();
    }

    public Usuario obtenerUsuarioPorId(Integer idUsuario) {
        return usuarioController.findUsuario(idUsuario);
    }

    public void cambiarEstadoUsuario(Usuario usuario, boolean habilitar) throws Exception {
        usuario.setEstado(habilitar);
        usuarioController.edit(usuario);
    }

    public void cambiarRolUsuario(Usuario usuario, Rol rol) throws Exception {
        usuario.setIdRol(rol);
        usuarioController.edit(usuario);
    }

    // --- Roles ---
    public List<Rol> listarRoles() {
        return rolController.findRolEntities();
    }

    public Rol obtenerRolPorId(Integer idRol) {
        return rolController.findRol(idRol);
    }

    public void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}