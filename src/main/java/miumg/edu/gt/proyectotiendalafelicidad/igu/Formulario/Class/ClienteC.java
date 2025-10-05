package miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.Class;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import miumg.edu.gt.proyectotiendalafelicidad.ClienteJpaController;
import miumg.edu.gt.proyectotiendalafelicidad.db.Cliente;

public class ClienteC {

    private EntityManagerFactory emf;
    private ClienteJpaController clienteController;


    public ClienteC() {
        emf = Persistence.createEntityManagerFactory("TiendaLaFelicidad");
        clienteController = new ClienteJpaController(emf);
    }


    public void crearCliente(String nombre, String apellido, String nit, String direccion, String telefono, String correo) throws Exception {
        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setNombre(nombre);
        nuevoCliente.setApellido(apellido);
        nuevoCliente.setNit(nit);
        nuevoCliente.setDireccion(direccion);
        nuevoCliente.setTelefono(telefono);
        nuevoCliente.setCorreo(correo);
        clienteController.create(nuevoCliente);
    }


    public List<String> buscarNits(String texto) {
        return clienteController.findClienteEntities().stream()
                .map(Cliente::getNit)
                .filter(nit -> nit != null && nit.startsWith(texto))
                .collect(Collectors.toList());
    }

 
    public Cliente obtenerClientePorNit(String nit) {
        return clienteController.findClienteEntities().stream()
                .filter(c -> c.getNit() != null && c.getNit().equals(nit))
                .findFirst()
                .orElse(null);
    }

    public void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
