/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ IPersistenciaCMTRemote.java
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
import com.losalpes.entities.Usuario;
import com.losalpes.entities.Vendedor;
import com.losalpes.excepciones.CupoInsuficienteException;
import com.losalpes.excepciones.OperacionInvalidaException;
import java.util.ArrayList;
import javax.ejb.Remote;

/**
 * Contrato funcional del bean de persistencia CMT Remoto
 */
@Remote
public interface IPersistenciaCMTRemote {

    /**
     * Agrega un vendedor al sistema
     *
     * @param vendedor Nuevo vendedor
     */
    public void insertRemoteDatabase(Vendedor vendedor);

    /**
     * Elimina un vendedor del sistema
     *
     * @param vendedor Vendedor a eliminar
     */
    public void deleteRemoteDatabase(Vendedor vendedor);

    /**
     * Realiza la compra de los items que se encuentran en el carrito
     *
     * @param muebles Lista de muebles
     * @param cliente Cliente que realiza la compra
     */
    public void comprar(ArrayList<Mueble> muebles, Usuario cliente);

    /**
     * Descuenta el valor de la compra de la tarjeta del cliente en el sistema
     * 
     * @param muebles Listado de muebles de la compra
     * @param cliente Cliente que realiza la compra
     * @throws CupoInsuficienteException 
     */
    public void descontarValorCompraTarjeta(ArrayList<Mueble> muebles, Usuario cliente) throws CupoInsuficienteException;

    /**
     * Calcula el valor total de la compra de los muebles
     * 
     * @param muebles Listado con todos los muebles que el cliente va a comprar
     * @return total Valor de la suma de toda la compra
     */
    public double calcularValorCompra(ArrayList<Mueble> muebles);

    /**
     * Registra la compra en el sistema
     * 
     * @param muebles Lista de muebles de la compra
     * @param cliente Cliente que realiza la compra
     * @throws OperacionInvalidaException 
     */
    public void registrarCompra(ArrayList<Mueble> muebles, Usuario cliente) throws OperacionInvalidaException;

}
