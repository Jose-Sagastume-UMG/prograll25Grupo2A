package miumg.edu.gt.proyectotiendalafelicidad.db;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import miumg.edu.gt.proyectotiendalafelicidad.db.DetalleCompra;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-10-04T13:05:24", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(Compra.class)
public class Compra_ { 

    public static volatile SingularAttribute<Compra, Date> fecha;
    public static volatile ListAttribute<Compra, DetalleCompra> detalleCompraList;
    public static volatile SingularAttribute<Compra, BigDecimal> total;
    public static volatile SingularAttribute<Compra, Integer> idCompra;
    public static volatile SingularAttribute<Compra, String> proveedor;

}