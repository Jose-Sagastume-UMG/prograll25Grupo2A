package miumg.edu.gt.proyectotiendalafelicidad.db;

import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import miumg.edu.gt.proyectotiendalafelicidad.db.Bitacora;
import miumg.edu.gt.proyectotiendalafelicidad.db.Rol;
import miumg.edu.gt.proyectotiendalafelicidad.db.Venta;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-10-04T13:05:24", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(Usuario.class)
public class Usuario_ { 

    public static volatile SingularAttribute<Usuario, String> password;
    public static volatile SingularAttribute<Usuario, Boolean> estado;
    public static volatile SingularAttribute<Usuario, Rol> idRol;
    public static volatile ListAttribute<Usuario, Bitacora> bitacoraList;
    public static volatile ListAttribute<Usuario, Venta> ventaList;
    public static volatile SingularAttribute<Usuario, Integer> idUsuario;
    public static volatile SingularAttribute<Usuario, String> apellido;
    public static volatile SingularAttribute<Usuario, String> userName;
    public static volatile SingularAttribute<Usuario, String> nombre;

}