package miumg.edu.gt.proyectotiendalafelicidad.db;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import miumg.edu.gt.proyectotiendalafelicidad.db.Cliente;
import miumg.edu.gt.proyectotiendalafelicidad.db.DetalleVenta;
import miumg.edu.gt.proyectotiendalafelicidad.db.Factura;
import miumg.edu.gt.proyectotiendalafelicidad.db.Usuario;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-10-04T13:05:24", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(Venta.class)
public class Venta_ { 

    public static volatile SingularAttribute<Venta, Date> fecha;
    public static volatile SingularAttribute<Venta, BigDecimal> total;
    public static volatile SingularAttribute<Venta, Factura> factura;
    public static volatile SingularAttribute<Venta, Cliente> idCliente;
    public static volatile ListAttribute<Venta, DetalleVenta> detalleVentaList;
    public static volatile SingularAttribute<Venta, Usuario> idUsuario;
    public static volatile SingularAttribute<Venta, Integer> idVenta;

}