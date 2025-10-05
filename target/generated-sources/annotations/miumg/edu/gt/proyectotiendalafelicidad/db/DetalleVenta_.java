package miumg.edu.gt.proyectotiendalafelicidad.db;

import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import miumg.edu.gt.proyectotiendalafelicidad.db.Producto;
import miumg.edu.gt.proyectotiendalafelicidad.db.Venta;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-10-04T13:05:24", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(DetalleVenta.class)
public class DetalleVenta_ { 

    public static volatile SingularAttribute<DetalleVenta, BigDecimal> precioUnitario;
    public static volatile SingularAttribute<DetalleVenta, BigDecimal> subtotal;
    public static volatile SingularAttribute<DetalleVenta, Integer> idDetalleVenta;
    public static volatile SingularAttribute<DetalleVenta, Integer> cantidad;
    public static volatile SingularAttribute<DetalleVenta, Producto> idProducto;
    public static volatile SingularAttribute<DetalleVenta, Venta> idVenta;

}