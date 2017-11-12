/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ PersistenciaBMTTest.java
 * Universidad de los Andes (Bogotá - Colombia)
 * Departamento de Ingeniería de Sistemas y Computación
 * Licenciado bajo el esquema Academic Free License version 3.0
 *
 * Ejercicio: Muebles de los Alpes
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package com.losalpes.servicios;

import com.losalpes.entities.Mueble;
import com.losalpes.entities.TarjetaCreditoAlpes;
import com.losalpes.entities.TipoMueble;
import com.losalpes.entities.TipoUsuario;
import com.losalpes.entities.Usuario;
import javax.naming.InitialContext;
import java.util.Properties;
import com.losalpes.entities.Vendedor;
import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Clase encargada de realizar pruebas unitarias
 *
 */
public class PersistenciaCMTTest {
    //-----------------------------------------------------------
    // Atributos
    //-----------------------------------------------------------
    long idVendedorNuevo;
    long idVendedorViejo;
    
    /**
     * Interface con referencia al servicio de vendedores en el sistema
     */
    private IPersistenciaCMTRemote persistenciaCMT;

    /**
     * Interface con referencia al servicio de persistencia de la base de datos oracle
     */
    private IServicioPersistenciaMockRemote servicioPersistenciaOracle;

    
    /**
     * Interface con referencia al servicio de persistencia de la base de datos derby
     */
    private IServicioPersistenciaDerbyMockRemote servicioPersistenciaDerby;
    
        
    //-----------------------------------------------------------
    // Métodos de inicialización y terminación
    //-----------------------------------------------------------
    /**
     * Método que se ejecuta antes de comenzar la prueba unitaria Se encarga de
     * inicializar todo lo necesario para la prueba
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        try {
            Properties env = new Properties();
            env.put("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
            env.put("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
            env.put("org.omg.CORBA.ORBInitialPort", "3700");
            InitialContext contexto;
            contexto = new InitialContext(env);
            persistenciaCMT = (IPersistenciaCMTRemote) contexto.lookup("com.losalpes.servicios.IPersistenciaCMTRemote");
            servicioPersistenciaOracle = (IServicioPersistenciaMockRemote) contexto.lookup("com.losalpes.servicios.IServicioPersistenciaMockRemote");
            servicioPersistenciaDerby = (IServicioPersistenciaDerbyMockRemote) contexto.lookup("com.losalpes.servicios.IServicioPersistenciaDerbyMockRemote");
        } catch (NamingException e) {
            throw new Exception(e.getMessage());
        }
    }

    //-----------------------------------------------------------
    // Métodos de prueba
    //-----------------------------------------------------------
    /**
     * Método de prueba para agregar un vendedor al sistema
     * @throws java.lang.Exception
     */
    @Test
    public void testInsertRemoteDatabase() throws Exception {
        // Obtenemos el id del último vendedor ingresado
        List<Vendedor> vendedores = servicioPersistenciaOracle.findAll(Vendedor.class);
        idVendedorViejo = vendedores.get(vendedores.size()-1).getIdentificacion();
       
        // Seteamos el id del nuevo vendedor
        idVendedorNuevo = idVendedorViejo + 1;         
        System.out.println("Id vendedor nuevo: " + idVendedorNuevo);
        
        // Se inicializa un nuevo vendedor
        Vendedor vendedorNuevo = new Vendedor();
        vendedorNuevo.setNombres("Javier");
        vendedorNuevo.setApellidos("Mesa Losada");
        vendedorNuevo.setFoto("vendedor1");
        vendedorNuevo.setPerfil("Gerente");
        vendedorNuevo.setComisionVentas(200000.0);
        vendedorNuevo.setIdentificacion(idVendedorNuevo);
        vendedorNuevo.setSalario(12000000.0);
        persistenciaCMT.insertRemoteDatabase(vendedorNuevo);

        // Se obtiene el vendedor ingresado anteriormente
        Vendedor vendedorConsultado = (Vendedor) servicioPersistenciaOracle.findById(Vendedor.class, idVendedorNuevo);
        Assert.assertEquals(idVendedorNuevo, vendedorConsultado.getIdentificacion());

    }

    /**
     * Método de prueba para eliminar un vendedor al sistema
     * @throws java.lang.Exception
     */
    @Test
    public void testDeleteRemoteDatabase() throws Exception {
        // Se obtiene el vendedor para eliminar
        idVendedorViejo = ((Vendedor)servicioPersistenciaOracle.findAll(Vendedor.class).get(0)).getIdentificacion();
        Vendedor vendedorEliminar = (Vendedor) servicioPersistenciaOracle.findById(Vendedor.class, idVendedorViejo);
        persistenciaCMT.deleteRemoteDatabase(vendedorEliminar);

        // Se verifica que el cliente se halla eliminado
        Vendedor vendedorConsultado = (Vendedor) servicioPersistenciaOracle.findById(Vendedor.class, idVendedorViejo);
        Assert.assertEquals(null, vendedorConsultado);
    }
    
    
     /**
     * Método de prueba para eliminar un vendedor al sistema
     * @throws java.lang.Exception
     */
    @Test
    public void testComprar() throws Exception {       
        // Cliente del sistema
        Usuario cliente = new Usuario("client", "clientclient", TipoUsuario.Cliente);   
        
        // Se consulta el saldo actual de la tarjeta
        TarjetaCreditoAlpes tarjetaAnterior = (TarjetaCreditoAlpes) servicioPersistenciaDerby.findById(TarjetaCreditoAlpes.class, "123456");
        double saldoTarjetaAnterior = tarjetaAnterior.getCupo();
        
        //Agrega los muebles del sistema a la lista del carrito de compras
        ArrayList<Mueble> muebles = new ArrayList<>();        
        muebles.add(new Mueble(1, "Silla clásica", "Una confortable silla con estilo del siglo XIX.", TipoMueble.Interior, 4, "sillaClasica", 100000));
        muebles.add(new Mueble(2, "Sillón new wave", "Innovador y cómodo. No existen mejores palabras para describir este hermoso sillón.", TipoMueble.Interior, 6, "newWave", 500000));
        muebles.add(new Mueble(3, "Silla moderna", "Lo último en la moda de interiores. Esta silla le brindará la comodidad e innovación que busca", TipoMueble.Interior, 5, "sillaModerna", 200000));
        muebles.add(new Mueble(4, "Mesa de jardín", "Una bella mesa para comidas y reuniones al aire libre.", TipoMueble.Exterior, 1, "mesaJardin", 250000));
        muebles.add(new Mueble(5, "Orange games", "Una hermosa silla con un toqué moderno y elegante. Excelente para su sala de estar", TipoMueble.Interior, 7, "sillaNaranja", 300000));
        muebles.add(new Mueble(6, "Cama king", "Una hermosa cama hecha en cedro para dos personas. Sus sueños no volveran a ser iguales.", TipoMueble.Interior, 5, "bed", 600000));
                    
        // Se realiza la compra 
        persistenciaCMT.comprar(muebles, cliente);
        
        // Se consulta el saldo nuevo de la tarjeta
        TarjetaCreditoAlpes tarjetaNuevo = (TarjetaCreditoAlpes) servicioPersistenciaDerby.findById(TarjetaCreditoAlpes.class, "123456");
        double saldoTarjetaNuevo = tarjetaNuevo.getCupo();
        
        // Se verifica que se haya realizado la compra
        Assert.assertEquals(false, saldoTarjetaAnterior == saldoTarjetaNuevo);
    }  

}
