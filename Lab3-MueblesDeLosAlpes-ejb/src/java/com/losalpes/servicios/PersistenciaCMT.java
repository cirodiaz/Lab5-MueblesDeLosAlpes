/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ ServicioPersistenciaMock.java
 * Universidad de los Andes (Bogotá - Colombia)
 * Departamento de Ingeniería de Sistemas y Computación
 * Licenciado bajo el esquema Academic Free License version 3.0
 *
 * Ejercicio: Muebles de los Alpes
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package com.losalpes.servicios;

import com.losalpes.entities.Mueble;
import com.losalpes.entities.RegistroVenta;
import com.losalpes.entities.TarjetaCreditoAlpes;
import com.losalpes.entities.Usuario;
import com.losalpes.entities.Vendedor;
import com.losalpes.excepciones.CupoInsuficienteException;
import com.losalpes.excepciones.OperacionInvalidaException;
import java.util.ArrayList;
import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 * Implementación de los servicios de persistencia
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class PersistenciaCMT implements IPersistenciaCMTLocal, IPersistenciaCMTRemote {

    //-----------------------------------------------------------
    // Atributos
    //-----------------------------------------------------------
    @Resource
    private SessionContext context; 
    
    /**
     * La entidad encargada de persistir en la base de datos
     */
    @PersistenceContext(unitName = "Lab3-MueblesDeLosAlpes-ejbPU2")
    private EntityManager entityManagerDerby;
    
    /**
     * Interface con referencia al servicio de persistencia en el sistema de la base de datos Oracle
     */
    @EJB
    private IServicioPersistenciaMockLocal persistenciaOracle;
  


    //-----------------------------------------------------------
    // Constructor
    //-----------------------------------------------------------
    /**
     * Constructor de la clase. Inicializa los atributos.
     */
    public PersistenciaCMT() {

    }

    //-----------------------------------------------------------
    // Métodos
    //-----------------------------------------------------------
    /**
     * Agrega un vendedor al sistema
     *
     * @param vendedor Nuevo vendedor
     */
   
    @Override
    public void insertRemoteDatabase(Vendedor vendedor) {
        try {
            persistenciaOracle.create(vendedor);
        } catch (OperacionInvalidaException ex) {
            System.out.println("error");
            context.setRollbackOnly();
        }
    }

    /**
     * Elimina un vendedor del sistema
     *
     * @param vendedor Vendedor a eliminar
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void deleteRemoteDatabase(Vendedor vendedor) {
        try {
            Vendedor v = (Vendedor) persistenciaOracle.findById(Vendedor.class, vendedor.getIdentificacion());
            persistenciaOracle.delete(v);
        } catch (OperacionInvalidaException ex) {
            context.setRollbackOnly();
        }
    }
    
     /**
     * Realiza la compra de los items que se encuentran en el carrito
     *
     * @param muebles Lista de muebles
     * @param usuario Usuario que realiza la compra
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void comprar(ArrayList<Mueble> muebles, Usuario usuario) {       
                   
        try {
            registrarCompra(muebles, usuario);
            descontarValorCompraTarjeta(muebles, usuario);
        } catch (OperacionInvalidaException | CupoInsuficienteException ex) {
            context.setRollbackOnly();
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void descontarValorCompraTarjeta(ArrayList<Mueble> muebles, Usuario usuario)throws CupoInsuficienteException{
        double valor = calcularValorCompra(muebles);
        
        TarjetaCreditoAlpes tarjeta = (TarjetaCreditoAlpes) entityManagerDerby.createNamedQuery("TarjetaCreditoAlpes.findByNombretitular").setParameter("nombreTitular", usuario.getLogin()).getSingleResult();
        
        if(tarjeta.getCupo()< valor){
            throw new CupoInsuficienteException("El cliente no tiene cupo disponible en la tarjeta para realizar la compra.");
        } else {
            tarjeta.setCupo(tarjeta.getCupo() - valor);
            entityManagerDerby.merge(tarjeta);
        }
    
    }
    
    /**
     * Calcula el valor total de la compra de los muebles
     * @param muebles Listado con todos los muebles que el cliente va a comprar
     * @return total Valor de la suma de toda la compra
     */
    public double calcularValorCompra(ArrayList<Mueble> muebles){
        double total = 0d;
        for (Mueble m : muebles) {
            total+= m.getCantidad()*m.getPrecio();
        }
        return total;        
    }
    
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void registrarCompra(ArrayList<Mueble> muebles, Usuario usuario) throws OperacionInvalidaException {
        Mueble mueble;

        if (muebles.isEmpty()) {
            for (int i = 0; i < muebles.size(); i++) {
                mueble = muebles.get(i);
                Mueble editar = (Mueble) persistenciaOracle.findById(Mueble.class, mueble.getReferencia());

                if (editar != null) {
                    editar.setCantidad(editar.getCantidad() - mueble.getCantidad());
                    RegistroVenta compra = new RegistroVenta(new Date(System.currentTimeMillis()), mueble, mueble.getCantidad(), null, usuario);
                    usuario.agregarRegistro(compra);

                    persistenciaOracle.update(usuario);
                    persistenciaOracle.update(editar);

                } else {
                    throw new OperacionInvalidaException("Mueble no existe.");
                }
            }
        } else {
            throw new OperacionInvalidaException("Lista de muebles vacia.");
        }
    }
    
}
