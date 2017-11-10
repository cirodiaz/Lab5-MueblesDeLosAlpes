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

import javax.naming.InitialContext;
import java.util.Properties;
import com.losalpes.entities.Vendedor;
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

    /**
     * Interface con referencia al servicio de vendedores en el sistema
     */
    private IPersistenciaCMTRemote persistenciaCMT;
    
    /**
     * Interface con referencia al servicio de persistencia del sistema
     */
    private IServicioPersistenciaMockRemote servicioPersistencia;

    //-----------------------------------------------------------
    // Métodos de inicialización y terminación
    //-----------------------------------------------------------
    /**
     * Método que se ejecuta antes de comenzar la prueba unitaria Se encarga de
     * inicializar todo lo necesario para la prueba
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
            servicioPersistencia = (IServicioPersistenciaMockRemote) contexto.lookup("com.losalpes.servicios.IServicioPersistenciaMockRemote");
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    //-----------------------------------------------------------
    // Métodos de prueba
    //-----------------------------------------------------------
    /**
     * Método de prueba para agregar un vendedor al sistema
     */
    @Test
    public void testInsertRemoteDatabase() throws Exception {      
        // Se inicializa un nuevo vendedor
        Vendedor vendedorNuevo = new Vendedor();
        vendedorNuevo.setNombres("Javier");
        vendedorNuevo.setApellidos("Mesa Losada");
        vendedorNuevo.setFoto("vendedor1");
        vendedorNuevo.setPerfil("Gerente");
        vendedorNuevo.setComisionVentas(200000.0);
        vendedorNuevo.setIdentificacion(12345);
        vendedorNuevo.setSalario(12000000.0);
        persistenciaCMT.insertRemoteDatabase(vendedorNuevo);        
        
        // Se obtiene el vendedor ingresado anteriormente
        Vendedor vendedorConsultado = (Vendedor)servicioPersistencia.findById(Vendedor.class, 12345L);
        Assert.assertEquals(12345L, vendedorConsultado.getIdentificacion());
               
    }

    /**
     * Método de prueba para eliminar un vendedor al sistema
     */
    @Test
    public void testDeleteRemoteDatabase() throws Exception {
        // Se obtiene el vendedor para eliminar
        Vendedor vendedorEliminar = (Vendedor)servicioPersistencia.findById(Vendedor.class, 1L);
        persistenciaCMT.deleteRemoteDatabase(vendedorEliminar);
        
        // Se verifica que el cliente se halla eliminado
        Vendedor vendedorConsultado = (Vendedor)servicioPersistencia.findById(Vendedor.class, 1L);
        Assert.assertEquals(null, vendedorConsultado);
    }

}
