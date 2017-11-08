/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ RegistroVentaPK.java
 * Universidad de los Andes (Bogota - Colombia)
 * Departamento de Ingenieria de Sistemas y Computacion
 * Licenciado bajo el esquema Academic Free License version 3.0
 *
 * Ejercicio: Muebles los Alpes
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package com.losalpes.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Clase que representa la llave primaria compuesta de la entidad RegistroVenta
 *
 */
public class RegistroVentaPK implements Serializable{
        
    private Date fechaVenta;
    
    private String comprador;
    
    private long producto;

    public RegistroVentaPK() {
  }

  public RegistroVentaPK(Date fechaVenta, String comprador, long producto) {
      this.fechaVenta = fechaVenta;
      this.comprador = comprador;
      this.producto = producto;
  }
    
    
    public Date getFechaVenta() {
        return fechaVenta;
    }

    public String getComprador() {
        return comprador;
    }

    public long getProducto() {
        return producto;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.fechaVenta);
        hash = 23 * hash + Objects.hashCode(this.comprador);
        hash = 23 * hash + Objects.hashCode(this.producto);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RegistroVentaPK other = (RegistroVentaPK) obj;
        if (!Objects.equals(this.fechaVenta, other.fechaVenta)) {
            return false;
        }
        if (!Objects.equals(this.comprador, other.comprador)) {
            return false;
        }
        if (!Objects.equals(this.producto, other.producto)) {
            return false;
        }
        return true;
    }
   
     
    
    
    
}
