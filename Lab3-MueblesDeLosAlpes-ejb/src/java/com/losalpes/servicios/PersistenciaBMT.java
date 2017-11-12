/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ PersistenciaBMT.java
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 * Implementación de los servicios de persistencia
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class PersistenciaBMT implements IPersistenciaBMTLocal, IPersistenciaBMTRemote {

    //-----------------------------------------------------------
    // Atributos
    //-----------------------------------------------------------
    @Resource
    private UserTransaction userTransaction;

    /**
     * La entidad encargada de persistir en la base de datos
     */
    @PersistenceContext(unitName = "Lab3-MueblesDeLosAlpes-ejbPU2")
    private EntityManager entityManagerDerby;

    /**
     * Interface con referencia al servicio de persistencia en el sistema
     */
    @EJB
    private IServicioPersistenciaMockLocal persistenciaOracle;

   
    //-----------------------------------------------------------
    // Constructor
    //-----------------------------------------------------------
    /**
     * Constructor de la clase. Inicializa los atributos.
     */
    public PersistenciaBMT() {

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
            userTransaction.begin();
            persistenciaOracle.create(vendedor);
            userTransaction.commit();
        } catch (OperacionInvalidaException | IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException e) {
            try {
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex) {
                Logger.getLogger(PersistenciaBMT.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Elimina un vendedor del sistema
     *
     * @param vendedor Vendedor a eliminar
     */
    @Override
    public void deleteRemoteDatabase(Vendedor vendedor) {
        try {
            userTransaction.begin();
            Vendedor v = (Vendedor) persistenciaOracle.findById(Vendedor.class, vendedor.getIdentificacion());
            persistenciaOracle.delete(v);
            userTransaction.commit();
        } catch (NotSupportedException | OperacionInvalidaException | IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | RollbackException | SystemException e) {
            try {
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex) {
                Logger.getLogger(PersistenciaBMT.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

     /**
     * Realiza la compra de los items que se encuentran en el carrito
     *
     * @param muebles Lista de muebles
     * @param cliente Cliente que realiza la compra
     */
    @Override
    public void comprar(ArrayList<Mueble> muebles, Usuario cliente) {       
                   
        try {
            userTransaction.begin();
            registrarCompra(muebles, cliente);
            descontarValorCompraTarjeta(muebles, cliente);
            userTransaction.commit();
        } catch (OperacionInvalidaException | CupoInsuficienteException |RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex ) {
            
            try {
                System.out.println("Se hace rollback de la transacción");
                System.out.println(ex);
                userTransaction.setRollbackOnly();
            } catch (IllegalStateException | SystemException ex1) {
                Logger.getLogger(PersistenciaBMT.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } 
    }
    
    /**
     * Descuenta el valor de la compra de la tarjeta del cliente en el sistema
     * 
     * @param muebles Listado de muebles de la compra
     * @param cliente Cliente que realiza la compra
     * @throws CupoInsuficienteException 
     */
    @Override
    public void descontarValorCompraTarjeta(ArrayList<Mueble> muebles, Usuario cliente)throws CupoInsuficienteException{
        double valor = calcularValorCompra(muebles);
        
        TarjetaCreditoAlpes tarjeta = (TarjetaCreditoAlpes) entityManagerDerby.createNamedQuery("TarjetaCreditoAlpes.findByNombretitular").setParameter("nombreTitular", cliente.getLogin()).getSingleResult();
       
        // Se valida que la tarjeta exista
        if(tarjeta == null){
            throw new CupoInsuficienteException("La tarjeta no existe!!");
        }
        
        // Se valida el cupo de la tarjeta para realizar la transacción
        if(tarjeta.getCupo()< valor){
            throw new CupoInsuficienteException("El cliente no tiene cupo disponible en la tarjeta para realizar la compra.\n"
            + "En el cupo de la tarjeta tiene: " + tarjeta.getCupo() + " y la compra es de : " + valor);
        } else {
            tarjeta.setCupo(tarjeta.getCupo() - valor);
            entityManagerDerby.merge(tarjeta);
            System.out.println("Se actualizó el valor del cupo de la tarjeta");
        }
    
    }
    
    /**
     * Calcula el valor total de la compra de los muebles
     * 
     * @param muebles Listado con todos los muebles que el cliente va a comprar
     * @return total Valor de la suma de toda la compra
     */
    @Override
    public double calcularValorCompra(ArrayList<Mueble> muebles){
        double total = 0d;
        for (Mueble m : muebles) {
            total+= m.getCantidad()*m.getPrecio();
        }
        return total;        
    }
    
    /**
     * Registra la compra en el sistema
     * 
     * @param muebles Lista de muebles de la compra
     * @param cliente Cliente que realiza la compra
     * @throws OperacionInvalidaException 
     */
    @Override
    public void registrarCompra(ArrayList<Mueble> muebles, Usuario cliente) throws OperacionInvalidaException {
        Mueble mueble;
        
        Usuario clienteConsultado = (Usuario) persistenciaOracle.findById(Usuario.class, cliente.getLogin());        
        
        if (!muebles.isEmpty()) {
            for (int i = 0; i < muebles.size(); i++) {
                mueble = muebles.get(i);
                
                Mueble editar = (Mueble) persistenciaOracle.findById(Mueble.class, mueble.getReferencia());

                if (editar != null) {
                    editar.setCantidad(editar.getCantidad() - mueble.getCantidad());
                    RegistroVenta compra = new RegistroVenta(new Date(System.currentTimeMillis()), mueble, mueble.getCantidad(), null, cliente);
                    clienteConsultado.agregarRegistro(compra);

                    persistenciaOracle.update(clienteConsultado);
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
