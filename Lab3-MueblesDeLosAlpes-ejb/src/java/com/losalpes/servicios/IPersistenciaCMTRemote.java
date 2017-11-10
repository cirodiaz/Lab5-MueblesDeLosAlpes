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

import com.losalpes.entities.Usuario;
import com.losalpes.entities.Vendedor;
import javax.ejb.Remote;

/**
 * Contrato funcional del bean de persistencia CMT Remoto
 */
@Remote
public interface IPersistenciaCMTRemote {
 /**
     * Agrega un vendedor al sistema
     * @param vendedor Nuevo vendedor
     */
    public void insertRemoteDatabase(Vendedor vendedor);
    
    /**
     * Elimina un vendedor del sistema
     * @param vendedor Vendedor a eliminar
     */
    public void deleteRemoteDatabase(Vendedor vendedor);
    
    /**
     * Realiza la compra de los items que se encuentran en el carrito
     * @param usuario Usuario que realiza la compra
     */
    public void comprar (Usuario usuario);    
    
}
