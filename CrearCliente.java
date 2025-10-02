/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.Class;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import miumg.edu.gt.proyectotiendalafelicidad.ClienteJpaController;
import miumg.edu.gt.proyectotiendalafelicidad.db.Cliente;
/**
 *
 * @author Jose
 */
public class CrearCliente {
   

    private EntityManagerFactory emf;
    private ClienteJpaController clienteController;

    public CrearCliente() {
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

        clienteController.create(nuevoCliente); // persiste en la BD
    }
}


