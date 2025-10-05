package miumg.edu.gt.proyectotiendalafelicidad.db;

import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import miumg.edu.gt.proyectotiendalafelicidad.db.Compra;
import miumg.edu.gt.proyectotiendalafelicidad.db.Producto;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-10-04T13:05:24", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(DetalleCompra.class)
public class DetalleCompra_ { 

    public static volatile SingularAttribute<DetalleCompra, BigDecimal> precioUnitario;
    public static volatile SingularAttribute<DetalleCompra, Compra> idCompra;
    public static volatile SingularAttribute<DetalleCompra, Integer> idDetalleCompra;
    public static volatile SingularAttribute<DetalleCompra, Integer> cantidad;
    public static volatile SingularAttribute<DetalleCompra, BigDecimal> subTotal;
    public static volatile SingularAttribute<DetalleCompra, Producto> idProducto;

}