/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package miumg.edu.gt.proyectotiendalafelicidad;

import miumg.edu.gt.proyectotiendalafelicidad.igu.Login;
import miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.FormUsuario;
import miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.FormClientes;

/**
 *
 * @author Jose
 */
public class ProyectoTiendaLaFelicidad {

    public static void main(String[] args) {

        login();
    }

//ventana login xd
    public static void login() {
        Login ventana = new Login();
        ventana.setLocationRelativeTo(null); // Centrar la ventana
        ventana.setVisible(true);
    }
}
