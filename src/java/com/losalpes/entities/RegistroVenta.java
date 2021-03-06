/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ RegistroVenta.java
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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 * Clase que modela un registro de venta realizado por un cliente
 *
 */
@Entity
@IdClass(RegistroVentaPK.class)
@NamedQueries({
		@NamedQuery(name = "ReporteVenta.findByUsuario", 
				query = "select r from RegistroVenta r where r.comprador =:cliente"),
    
                @NamedQuery(name = "ReporteVenta.findMaxMueblesVendidos", 
				query = "select sum(r.cantidad) as cantidad, r.producto from RegistroVenta r group by r order by cantidad desc"),
                
                @NamedQuery(name = "ReporteVenta.findMayoresCompradoresPorPais", 
				query = "select u, count(r.cantidad) as cantidadcompras,sum(r.cantidad*m.precio) as valorcompras"
                                + " from RegistroVenta r, Mueble m, Usuario u  where m.referencia = r.producto.referencia"
                                + " and r.comprador.login = u.login  and u.ciudad in :ciudades group by r.comprador, u.ciudad, u.nombreCompleto order by valorcompras desc")
                
})

public class RegistroVenta implements Serializable {

    //-----------------------------------------------------------
    // Atributos
    //-----------------------------------------------------------
    
    //@Id
    //private Long id;
    
    /**
     * Fecha en la que se vendió el producto
     */
    @Id
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaVenta;

    /**
     * Producto vendido
     */
    @Id
    @OneToOne
    @JoinColumn(name = "MUEBLEID")
    private Mueble producto;

    /**
     * Cantidad vendida del producto
     */
    @Column(nullable = false)
    private int cantidad;

    /**
     * Ciudad en la que se vendió el producto
     */
    private String ciudad;

    /**
     * Usuario que compró el producto
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "COMPRADOR")
    private Usuario comprador;
       

    //-----------------------------------------------------------
    // Constructor
    //-----------------------------------------------------------
    /**
     * Constructor sin argumentos
     */
    public RegistroVenta() {

    }

    /**
     * Constructor de la clase con argumentos
     *
     * @param fechaVenta Fecha en que se realizó la venta
     * @param producto Mueble adquirido
     * @param cantidad Cantidad adquirida
     * @param ciudad Ciudad en la que se vendió el producto
     * @param comprador Usuario que compro el mueble
     */
    public RegistroVenta(Date fechaVenta, Mueble producto, int cantidad,
            String ciudad, Usuario comprador) {
        this.fechaVenta = fechaVenta;
        this.producto = producto;
        this.cantidad = cantidad;
        this.ciudad = ciudad;
        this.comprador = comprador;
    }

    //-----------------------------------------------------------
    // Getters y setters
    //-----------------------------------------------------------
    /**
     * Devuelve la cantidad de producto vendido
     *
     * @return cantidad Cantidad de producto vendido
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Modifica la cantidad de muebles adquiridos
     *
     * @param cantidad Nueva cantidad de muebles
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Devuelve la fecha en que se vendió el mueble
     *
     * @return fechaVenta Fecha de venta del mueble
     */
    public Date getFechaVenta() {
        return fechaVenta;
    }

    /**
     * Modifica la fecha en que se vendió el mueble
     *
     * @param fechaVenta Nueva fecha de venta
     */
    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    /**
     * Devuelve el mueble adquirido
     *
     * @return producto Mueble adquirido
     */
    public Mueble getProducto() {
        return producto;
    }

    /**
     * Modifica el mueble adquirido
     *
     * @param producto Nuevo mueble
     */
    public void setProducto(Mueble producto) {
        this.producto = producto;
    }

    /**
     * Devuelve la ciudad en dónde se realizó la venta
     *
     * @return ciudad Ciudad
     */
    public String getCiudad() {
        return ciudad;
    }

    /**
     * Modifica la ciudad dónde se realizó la venta
     *
     * @param ciudad Nueva ciudad
     */
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    /**
     * Devuelve el usuario que realizó la compra
     *
     * @return comprador Usuario que realizó la compra
     */
    public Usuario getComprador() {
        return comprador;
    }

    /**
     * Modifica el usuario que realizó la compra
     *
     * @param comprador Nuevo usuario
     */
    public void setComprador(Usuario comprador) {
        this.comprador = comprador;
    }

    /*public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    */

}
