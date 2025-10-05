package miumg.edu.gt.proyectotiendalafelicidad.db;

import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import miumg.edu.gt.proyectotiendalafelicidad.db.Categoria;
import miumg.edu.gt.proyectotiendalafelicidad.db.DetalleCompra;
import miumg.edu.gt.proyectotiendalafelicidad.db.DetalleVenta;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-10-04T13:05:24", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(Producto.class)
public class Producto_ { 

    public static volatile SingularAttribute<Producto, String> descripcion;
    public static volatile ListAttribute<Producto, DetalleCompra> detalleCompraList;
    public static volatile ListAttribute<Producto, DetalleVenta> detalleVentaList;
    public static volatile SingularAttribute<Producto, Integer> idProducto;
    public static volatile SingularAttribute<Producto, Integer> stock;
    public static volatile SingularAttribute<Producto, Categoria> idCategoria;
    public static volatile SingularAttribute<Producto, BigDecimal> precioVenta;
    public static volatile SingularAttribute<Producto, String> nombre;

}