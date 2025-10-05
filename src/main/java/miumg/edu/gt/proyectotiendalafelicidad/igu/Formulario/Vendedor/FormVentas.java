/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.Vendedor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import miumg.edu.gt.proyectotiendalafelicidad.db.Categoria;
import miumg.edu.gt.proyectotiendalafelicidad.db.Cliente;
import miumg.edu.gt.proyectotiendalafelicidad.db.Producto;
import miumg.edu.gt.proyectotiendalafelicidad.db.DetalleVenta;
import miumg.edu.gt.proyectotiendalafelicidad.db.Factura;
import miumg.edu.gt.proyectotiendalafelicidad.db.Venta;
import miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.Class.CategoriaC;
import miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.Class.ClienteC;
import miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.Class.FacturaC;
import miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.Class.ProductoC;
import miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.Class.VentaC;

/**
 *
 * @author Jose
 */
public class FormVentas extends javax.swing.JInternalFrame {

    private ClienteC clienteService = new ClienteC(); 
    private DefaultTableModel modeloTabla;


    public FormVentas() {
         initComponents();
        inicializarCategorias();
        inicializarTabla();
        inicializarAutoCompletarNIT();
        inicializarProductos();
        txtFechaHora.setEditable(false);
        iniciarFechaHora();
        
    }
    private void iniciarFechaHora() {
    new javax.swing.Timer(1000, e -> {
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formato = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        txtFechaHora.setText(ahora.format(formato));
    }).start();
}

     private void inicializarCategorias() {
        CategoriaC categoriaService = new CategoriaC();
        List<Categoria> categorias = categoriaService.listarCategorias();
        ComboBoxCategoria.removeAllItems();

        for (Categoria c : categorias) {
            ComboBoxCategoria.addItem(c.getNombreCategoria());
        }
        categoriaService.cerrar();

        ComboBoxCategoria.addActionListener(e -> actualizarProductosPorCategoria());
    }

    private void actualizarProductosPorCategoria() {
        String categoriaSeleccionada = (String) ComboBoxCategoria.getSelectedItem();
        if (categoriaSeleccionada != null) {
            ProductoC productoService = new ProductoC();
            try {
                List<Producto> productos = productoService.listarProductosPorCategoria(categoriaSeleccionada);
                ComboBoxProducto.removeAllItems();
                for (Producto p : productos) {
                    ComboBoxProducto.addItem(p.getNombre());
                }
            } finally {
                productoService.cerrar();
            }
        }
    }

    private void inicializarTabla() {
        modeloTabla = new DefaultTableModel(
            new Object[]{"Producto", "Categoria", "Cantidad", "Precio Unitario", "Subtotal"}, 0
        );
        tableVistaPreviaVenta.setModel(modeloTabla);
    }

    private void inicializarAutoCompletarNIT() {
        comboBoxNitCliente.setEditable(true);
        JTextField editor = (JTextField) comboBoxNitCliente.getEditor().getEditorComponent();

        editor.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (!checkClienteExistente.isSelected()) return;

                String texto = editor.getText();
                comboBoxNitCliente.removeAllItems();

                for (String nit : clienteService.buscarNits(texto)) {
                    if (nit.startsWith(texto)) {
                        comboBoxNitCliente.addItem(nit);
                    }
                }
                editor.setText(texto);
                comboBoxNitCliente.setPopupVisible(comboBoxNitCliente.getItemCount() > 0);
            }
        });
    }

    private void inicializarProductos() {
        ComboBoxProducto.addActionListener(e -> actualizarSubtotalProducto());
        txtCantidadProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                actualizarSubtotalProducto();
            }
        });
    }

    private void actualizarSubtotalProducto() {
        String nombreProducto = (String) ComboBoxProducto.getSelectedItem();
        if (nombreProducto == null) {
            lblPrecioVenta.setText("--------------");
            TotalPagoProducto.setText("-----------");
            return;
        }

        ProductoC productoService = new ProductoC();
        try {
            Producto producto = productoService.obtenerProductoPorNombre(nombreProducto);
            if (producto != null) {
                lblPrecioVenta.setText("Q " + producto.getPrecioVenta().setScale(2, RoundingMode.HALF_UP));

                int cantidad = 0;
                try {
                    cantidad = Integer.parseInt(txtCantidadProducto.getText());
                } catch (NumberFormatException ex) {
                    cantidad = 0;
                }

                BigDecimal subtotal = producto.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad));
                TotalPagoProducto.setText("Q " + subtotal.setScale(2, RoundingMode.HALF_UP));
            } else {
                lblPrecioVenta.setText("--------------");
                TotalPagoProducto.setText("-----------");
            }
        } finally {
            productoService.cerrar();
        }
    }

    private void actualizarTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            total = total.add(BigDecimal.valueOf(Double.parseDouble(modeloTabla.getValueAt(i, 4).toString())));
        }
        lblTotalaPagar.setText("Q " + total.setScale(2, RoundingMode.HALF_UP));
    }

    private void limpiarCamposProducto() {
        txtCantidadProducto.setText("");
        ComboBoxProducto.setSelectedIndex(0);
        ComboBoxCategoria.setSelectedIndex(0);
        lblPrecioVenta.setText("--------------");
        TotalPagoProducto.setText("-----------");
    }

    private void limpiarTabla() {
        modeloTabla.setRowCount(0);
        actualizarTotal();
    }

    private void agregarProductoATabla() {
        if (txtCantidadProducto.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cantidad del producto.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String producto = ComboBoxProducto.getSelectedItem().toString();
            String categoria = ComboBoxCategoria.getSelectedItem().toString();
            int cantidad = Integer.parseInt(txtCantidadProducto.getText());
            BigDecimal precio = new BigDecimal(lblPrecioVenta.getText().replace("Q ", ""));
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad));

            modeloTabla.addRow(new Object[]{producto, categoria, cantidad, precio, subtotal});
            actualizarTotal();
            limpiarCamposProducto();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en los datos del producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quitarProductoSeleccionado() {
        int fila = tableVistaPreviaVenta.getSelectedRow();
        if (fila != -1) {
            modeloTabla.removeRow(fila);
            actualizarTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para quitar.", "Atención", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void completarConsumidorFinal() {
        txtNombre.setText("Consumidor");
        txtApellido.setText("Final");
        txtNit.setText("CF");
        txtDirrecion.setText("-");
        txtTelefono.setText("-");
        txtCorreo.setText("-");
    }

    private void realizarVenta() {

     if (modeloTabla.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "No hay productos en la venta.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
  
        Cliente cliente = null;
        if (!txtNit.getText().equalsIgnoreCase("CF")) { 
            cliente = clienteService.obtenerClientePorNit(txtNit.getText());
            if (cliente == null) {
                clienteService.crearCliente(
                        txtNombre.getText(),
                        txtApellido.getText(),
                        txtNit.getText(),
                        txtDirrecion.getText(),
                        txtTelefono.getText(),
                        txtCorreo.getText()
                );
                cliente = clienteService.obtenerClientePorNit(txtNit.getText());
            }
        }

   
        List<DetalleVenta> detalles = new java.util.ArrayList<>();
        ProductoC productoService = new ProductoC();
        try {
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                String nombreProducto = modeloTabla.getValueAt(i, 0).toString();
                int cantidad = Integer.parseInt(modeloTabla.getValueAt(i, 2).toString());
                BigDecimal precio = new BigDecimal(modeloTabla.getValueAt(i, 3).toString());

                Producto producto = productoService.obtenerProductoPorNombre(nombreProducto);
                if (producto == null) {
                    JOptionPane.showMessageDialog(this, "Producto no encontrado: " + nombreProducto, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                DetalleVenta detalle = new DetalleVenta();
                detalle.setIdProducto(producto);
                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(precio);
                detalles.add(detalle);
            }
        } finally {
            productoService.cerrar();
        }


        VentaC ventaService = new VentaC();
        Venta venta = ventaService.crearVenta(null, cliente, detalles); 
        ventaService.cerrar();


        FacturaC facturaService = new FacturaC();
        Factura factura = facturaService.crearFactura(venta);

        String rutaPDF = "C:\\Users\\Jose\\Documents\\Facturas_" + factura.getIdFactura() + ".pdf"; 
        facturaService.generarPDF(factura, rutaPDF);
        facturaService.cerrar();

        JOptionPane.showMessageDialog(this, "Venta realizada y factura generada.\nArchivo PDF: " + rutaPDF);

        limpiarTabla();
        limpiarCamposProducto();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al realizar la venta: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tableVistaPreviaVenta = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtCantidadProducto = new javax.swing.JTextField();
        btnAgregarATabla = new javax.swing.JButton();
        btnLimpiarCamposProducto = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtFechaHora = new javax.swing.JTextField();
        ComboBoxProducto = new javax.swing.JComboBox<>();
        ComboBoxCategoria = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        lblPrecioVenta = new javax.swing.JLabel();
        TotalPagoProducto = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        txtDirrecion = new javax.swing.JTextField();
        txtApellido = new javax.swing.JTextField();
        txtTelefono = new javax.swing.JTextField();
        txtNit = new javax.swing.JTextField();
        txtCorreo = new javax.swing.JTextField();
        btnLimpiarDatos = new javax.swing.JButton();
        btnConsumidorFinal = new javax.swing.JButton();
        checkClienteExistente = new javax.swing.JCheckBox();
        comboBoxNitCliente = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        lblTotalaPagar = new javax.swing.JLabel();
        btnLimpiarTabla = new javax.swing.JButton();
        btnRealizarVenta = new javax.swing.JButton();
        btnQuitarProductoTabla = new javax.swing.JButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tableVistaPreviaVenta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane5.setViewportView(tableVistaPreviaVenta);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 746, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Producto"));

        jLabel7.setText("Producto:");

        jLabel8.setText("Precio Unitario Q.");

        jLabel9.setText("Cantidad");

        jLabel12.setText("SubTOTAL:");

        btnAgregarATabla.setText("Cargar");
        btnAgregarATabla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarATablaActionPerformed(evt);
            }
        });

        btnLimpiarCamposProducto.setText("Limpiar");
        btnLimpiarCamposProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarCamposProductoActionPerformed(evt);
            }
        });

        jLabel10.setText("Fecha:");

        jLabel14.setText("Categoria:");

        lblPrecioVenta.setText("--------------");

        TotalPagoProducto.setText("-----------");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(lblPrecioVenta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(TotalPagoProducto)
                        .addGap(66, 66, 66)
                        .addComponent(btnLimpiarCamposProducto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAgregarATabla)
                        .addGap(19, 19, 19))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(ComboBoxProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(191, 191, 191)
                        .addComponent(txtCantidadProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(82, 82, 82))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 293, Short.MAX_VALUE)
                                .addComponent(jLabel9))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(ComboBoxCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel10)))
                        .addGap(40, 40, 40)
                        .addComponent(txtFechaHora, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(82, 82, 82))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtFechaHora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBoxCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCantidadProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(ComboBoxProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLimpiarCamposProducto)
                            .addComponent(btnAgregarATabla)
                            .addComponent(jLabel12)
                            .addComponent(TotalPagoProducto))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(lblPrecioVenta))
                        .addGap(17, 17, 17))))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Clientes"));

        jLabel1.setText("Nombre:");

        jLabel2.setText("Direccion:");

        jLabel3.setText("Apellido:");

        jLabel4.setText("Telefono:");

        jLabel5.setText("NIT:");

        jLabel6.setText("Correo:");

        btnLimpiarDatos.setText("Limpiar");
        btnLimpiarDatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarDatosActionPerformed(evt);
            }
        });

        btnConsumidorFinal.setText("CF");
        btnConsumidorFinal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsumidorFinalActionPerformed(evt);
            }
        });

        checkClienteExistente.setText("Cliente Existente");
        checkClienteExistente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkClienteExistenteActionPerformed(evt);
            }
        });

        comboBoxNitCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxNitClienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(txtDirrecion, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(checkClienteExistente)
                        .addGap(18, 18, 18)
                        .addComponent(comboBoxNitCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                        .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNit, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(txtTelefono)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(298, 298, 298)
                        .addComponent(btnLimpiarDatos)
                        .addGap(26, 26, 26)
                        .addComponent(btnConsumidorFinal)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtDirrecion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLimpiarDatos)
                            .addComponent(btnConsumidorFinal))
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(checkClienteExistente)
                            .addComponent(comboBoxNitCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jLabel11.setText("Total:");

        lblTotalaPagar.setText("AQUIRESULTADO");

        btnLimpiarTabla.setText("Cancelar");
        btnLimpiarTabla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarTablaActionPerformed(evt);
            }
        });

        btnRealizarVenta.setText("Vender");
        btnRealizarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRealizarVentaActionPerformed(evt);
            }
        });

        btnQuitarProductoTabla.setText("Quitar");
        btnQuitarProductoTabla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitarProductoTablaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTotalaPagar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnQuitarProductoTabla)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLimpiarTabla)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnRealizarVenta)
                        .addGap(50, 50, 50))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(lblTotalaPagar)
                    .addComponent(btnLimpiarTabla)
                    .addComponent(btnRealizarVenta)
                    .addComponent(btnQuitarProductoTabla))
                .addGap(19, 19, 19))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLimpiarCamposProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarCamposProductoActionPerformed
        limpiarCamposProducto();
    }//GEN-LAST:event_btnLimpiarCamposProductoActionPerformed

    private void btnQuitarProductoTablaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarProductoTablaActionPerformed
        quitarProductoSeleccionado();
    }//GEN-LAST:event_btnQuitarProductoTablaActionPerformed

    private void checkClienteExistenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkClienteExistenteActionPerformed
        comboBoxNitCliente.setEnabled(checkClienteExistente.isSelected());
    }//GEN-LAST:event_checkClienteExistenteActionPerformed

    private void comboBoxNitClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxNitClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboBoxNitClienteActionPerformed

    private void btnLimpiarDatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarDatosActionPerformed
        txtNombre.setText("");
        txtApellido.setText("");
        txtNit.setText("");
        txtDirrecion.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
    }//GEN-LAST:event_btnLimpiarDatosActionPerformed

    private void btnConsumidorFinalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsumidorFinalActionPerformed
        completarConsumidorFinal();
    }//GEN-LAST:event_btnConsumidorFinalActionPerformed

    private void btnAgregarATablaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarATablaActionPerformed
        agregarProductoATabla();
    }//GEN-LAST:event_btnAgregarATablaActionPerformed

    private void btnLimpiarTablaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarTablaActionPerformed
        limpiarTabla();
    }//GEN-LAST:event_btnLimpiarTablaActionPerformed

    private void btnRealizarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRealizarVentaActionPerformed
        realizarVenta();
    }//GEN-LAST:event_btnRealizarVentaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBoxCategoria;
    private javax.swing.JComboBox<String> ComboBoxProducto;
    private javax.swing.JLabel TotalPagoProducto;
    private javax.swing.JButton btnAgregarATabla;
    private javax.swing.JButton btnConsumidorFinal;
    private javax.swing.JButton btnLimpiarCamposProducto;
    private javax.swing.JButton btnLimpiarDatos;
    private javax.swing.JButton btnLimpiarTabla;
    private javax.swing.JButton btnQuitarProductoTabla;
    private javax.swing.JButton btnRealizarVenta;
    private javax.swing.JCheckBox checkClienteExistente;
    private javax.swing.JComboBox<String> comboBoxNitCliente;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblPrecioVenta;
    private javax.swing.JLabel lblTotalaPagar;
    private javax.swing.JTable tableVistaPreviaVenta;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtCantidadProducto;
    private javax.swing.JTextField txtCorreo;
    private javax.swing.JTextField txtDirrecion;
    private javax.swing.JTextField txtFechaHora;
    private javax.swing.JTextField txtNit;
    public javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
