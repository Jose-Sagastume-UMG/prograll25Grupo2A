
package miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.Class;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import miumg.edu.gt.proyectotiendalafelicidad.UsuarioJpaController;
import miumg.edu.gt.proyectotiendalafelicidad.db.Rol;
import miumg.edu.gt.proyectotiendalafelicidad.db.Usuario;

public class CrearUsuario {

    private EntityManagerFactory emf;
    private UsuarioJpaController usuarioController;

    public CrearUsuario() {
        emf = Persistence.createEntityManagerFactory("TiendaLaFelicidad"); 
        usuarioController = new UsuarioJpaController(emf);
    }

    public void crearUsuario(String nombre, String apellido, String username, String password, boolean estado, Rol rolSeleccionado) throws Exception {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setUserName(username);
        nuevoUsuario.setPassword(password); 
        nuevoUsuario.setEstado(estado);
        nuevoUsuario.setIdRol(rolSeleccionado);

        usuarioController.create(nuevoUsuario); 
    }
}
