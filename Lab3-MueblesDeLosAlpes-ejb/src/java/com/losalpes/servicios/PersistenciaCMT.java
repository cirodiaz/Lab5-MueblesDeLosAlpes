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
     * Crea un nuevo vendedor en el sistema
     * 
     * @param vendedor Vendedor nuevo
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
     * @param cliente Cliente que realiza la compra
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void comprar(ArrayList<Mueble> muebles, Usuario cliente) {       
                   
        try {
            registrarCompra(muebles, cliente);
            descontarValorCompraTarjeta(muebles, cliente);
        } catch (OperacionInvalidaException | CupoInsuficienteException ex) {
            System.out.println("Se hace rollback de la transacción");
            System.out.println(ex);
            context.setRollbackOnly();
        }
    }
    
    /**
     * Descuenta el valor de la compra de la tarjeta del cliente en el sistema
     * 
     * @param muebles Listado de muebles de la compra
     * @param cliente Cliente que realiza la compra
     * @throws CupoInsuficienteException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
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
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
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
