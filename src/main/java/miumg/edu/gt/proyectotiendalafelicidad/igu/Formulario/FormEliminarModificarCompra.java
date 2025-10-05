package miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario;

import java.math.BigDecimal;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import miumg.edu.gt.proyectotiendalafelicidad.CategoriaJpaController;
import miumg.edu.gt.proyectotiendalafelicidad.ProductoJpaController;
import miumg.edu.gt.proyectotiendalafelicidad.db.Categoria;
import miumg.edu.gt.proyectotiendalafelicidad.db.DetalleCompra;
import miumg.edu.gt.proyectotiendalafelicidad.db.Producto;
import miumg.edu.gt.proyectotiendalafelicidad.igu.Formulario.Class.ProductoC;

public class FormEliminarModificarCompra extends javax.swing.JInternalFrame {

    private DefaultTableModel modeloTabla;
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("TiendaLaFelicidad");

    public FormEliminarModificarCompra() {
        initComponents();
        inicializarTabla();
        cargarCategorias();
    }

    private void inicializarTabla() {
         modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Categoría", "Stock", "Descripción", "Precio Compra", "Precio Venta"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
      
                return column == 1 || column == 3 || column == 4 || column == 6;
            }
        };
        TableDatosModificarEliminar.setModel(modeloTabla);
        TableDatosModificarEliminar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void cargarCategorias() {
          comboBoxCategoria.removeAllItems();
        CategoriaJpaController catCtrl = new CategoriaJpaController(emf);
        for (Categoria cat : catCtrl.findCategoriaEntities()) {
            comboBoxCategoria.addItem(cat.getNombreCategoria());
        }
    }

    private Object[][] valoresOriginales;

    private void buscarProductos() {
        modeloTabla.setRowCount(0);
        String catNombre = (String) comboBoxCategoria.getSelectedItem();
        if (catNombre == null) return;

        CategoriaJpaController catCtrl = new CategoriaJpaController(emf);
        Categoria categoria = catCtrl.findCategoriaEntities()
                .stream()
                .filter(c -> c.getNombreCategoria().equals(catNombre))
                .findFirst()
                .orElse(null);
        if (categoria == null) return;

        ProductoJpaController prodCtrl = new ProductoJpaController(emf);

        for (Producto p : prodCtrl.findProductoEntities()) {
            if (p.getIdCategoria() != null
                    && p.getIdCategoria().getIdCategoria().equals(categoria.getIdCategoria())) {

       
                BigDecimal precioCompra = null;
                if (p.getDetalleCompraList() != null && !p.getDetalleCompraList().isEmpty()) {
                    DetalleCompra lastDetalle = p.getDetalleCompraList()
                            .get(p.getDetalleCompraList().size() - 1);
                    precioCompra = lastDetalle.getPrecioUnitario();
                }

                modeloTabla.addRow(new Object[]{
                        p.getIdProducto(),
                        p.getNombre(),
                        p.getIdCategoria().getNombreCategoria(),
                        p.getStock(),
                        p.getDescripcion(),
                        precioCompra,
                        p.getPrecioVenta()
                });
            }
        }
        lblTotalProductos.setText(String.valueOf(modeloTabla.getRowCount()));
    }

    private void modificarProducto() {
        int fila = TableDatosModificarEliminar.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para modificar.");
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de modificar este producto?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) return;

        try {
            Integer id = (Integer) modeloTabla.getValueAt(fila, 0);
            String nombre = modeloTabla.getValueAt(fila, 1).toString();

            int stock = 0;
            try {
                stock = Integer.parseInt(modeloTabla.getValueAt(fila, 3).toString().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "El stock debe ser un número válido.");
                return;
            }

            String descripcion = modeloTabla.getValueAt(fila, 4) != null
                    ? modeloTabla.getValueAt(fila, 4).toString()
                    : "";

            BigDecimal precioVenta;
            try {
                precioVenta = new BigDecimal(modeloTabla.getValueAt(fila, 6).toString().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "El precio de venta debe ser un número válido.");
                return;
            }

        
            ProductoJpaController prodCtrl = new ProductoJpaController(emf);
            Producto producto = prodCtrl.findProducto(id);

            if (producto != null) {
                producto.setNombre(nombre);
                producto.setStock(stock);
                producto.setDescripcion(descripcion);
                producto.setPrecioVenta(precioVenta);

                prodCtrl.edit(producto);
                JOptionPane.showMessageDialog(this, "Producto modificado correctamente.");
                buscarProductos();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al modificar: " + e.getMessage());
        }
    }
    private void eliminarProducto() {
        int fila = TableDatosModificarEliminar.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.");
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar este producto?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) return;

        Integer id = (Integer) modeloTabla.getValueAt(fila, 0);
        ProductoC productoC = new ProductoC();
        try {
            productoC.eliminarProductoConDetalles(id);
            productoC.cerrar();
            modeloTabla.removeRow(fila);
            JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
        }
    }

    private void eliminarProductosYCategoria() {
        String catNombre = (String) comboBoxCategoria.getSelectedItem();
        if (catNombre == null) return;

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar TODOS los productos de esta categoría?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) return;

        CategoriaJpaController catCtrl = new CategoriaJpaController(emf);
        Categoria categoria = catCtrl.findCategoriaEntities()
                .stream()
                .filter(c -> c.getNombreCategoria().equals(catNombre))
                .findFirst()
                .orElse(null);
        if (categoria == null) return;

        ProductoJpaController prodCtrl = new ProductoJpaController(emf);
        ProductoC productoC = new ProductoC();

        try {
            for (Producto p : prodCtrl.findProductoEntities()) {
                if (p.getIdCategoria().equals(categoria)) {
                    productoC.eliminarProductoConDetalles(p.getIdProducto());
                }
            }
            productoC.cerrar();
            catCtrl.destroy(categoria.getIdCategoria());
            buscarProductos();
            JOptionPane.showMessageDialog(this, "Categoría y productos eliminados.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
        }
    }

    private void cancelarCambios() {
         int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea cancelar los cambios y recargar los valores originales?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );
        if (opcion != JOptionPane.YES_OPTION) return;

        buscarProductos();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ComboBoxProducto = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TableDatosModificarEliminar = new javax.swing.JTable();
        btnBuscar = new javax.swing.JButton();
        btnModifcar = new javax.swing.JButton();
        Eliminar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        btnEliminarProductoCategoria = new javax.swing.JButton();
        comboBoxCategoria = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        lblTotalProductos = new javax.swing.JLabel();

        jLabel3.setText("Categoria:");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Compras Edit"));

        jLabel1.setText("Categoria:");

        TableDatosModificarEliminar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(TableDatosModificarEliminar);

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        btnModifcar.setText("Modificar ");
        btnModifcar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModifcarActionPerformed(evt);
            }
        });

        Eliminar.setText("Eliminar");
        Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EliminarActionPerformed(evt);
            }
        });

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnEliminarProductoCategoria.setText("Eliminar producto y categoria");
        btnEliminarProductoCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarProductoCategoriaActionPerformed(evt);
            }
        });

        jLabel2.setText("Total Productos:");

        lblTotalProductos.setText("-------------");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel1)
                .addGap(28, 28, 28)
                .addComponent(comboBoxCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnBuscar)
                .addGap(25, 25, 25))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 666, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(btnEliminarProductoCategoria)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancelar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnModifcar)
                .addGap(18, 18, 18)
                .addComponent(Eliminar)
                .addGap(29, 29, 29))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(lblTotalProductos)
                .addGap(87, 87, 87))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(comboBoxCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(lblTotalProductos))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnModifcar)
                            .addComponent(Eliminar)
                            .addComponent(btnCancelar)
                            .addComponent(btnEliminarProductoCategoria)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(btnBuscar)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        buscarProductos();
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        cancelarCambios();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnEliminarProductoCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarProductoCategoriaActionPerformed
        eliminarProductosYCategoria();
    }//GEN-LAST:event_btnEliminarProductoCategoriaActionPerformed

    private void btnModifcarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModifcarActionPerformed
        modificarProducto();
    }//GEN-LAST:event_btnModifcarActionPerformed

    private void EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EliminarActionPerformed
        eliminarProducto();
    }//GEN-LAST:event_EliminarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBoxProducto;
    private javax.swing.JButton Eliminar;
    private javax.swing.JTable TableDatosModificarEliminar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEliminarProductoCategoria;
    private javax.swing.JButton btnModifcar;
    private javax.swing.JComboBox<String> comboBoxCategoria;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTotalProductos;
    // End of variables declaration//GEN-END:variables
}
