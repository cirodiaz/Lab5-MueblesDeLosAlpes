/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ IPersistenciaRemote.java
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
import com.losalpes.excepciones.OperacionInvalidaException;
import java.util.List;

/**
 * Contrato funcional del bean de persistencia
 *
 */
public interface IPersistenciaRemote {

    public void insertRemoteDatabase(Vendedor vendedor);

    public void deleteRemoteDatabase(Vendedor vendedor);

    public void comprar();
}
