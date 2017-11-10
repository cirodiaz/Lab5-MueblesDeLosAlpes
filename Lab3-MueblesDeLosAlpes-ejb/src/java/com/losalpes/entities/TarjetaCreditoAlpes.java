/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ TarjetaCreditoAlpes.java
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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author jaher
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "TarjetaCreditoAlpes.findAll", query = "SELECT t FROM TarjetaCreditoAlpes t")
    , @NamedQuery(name = "TarjetaCreditoAlpes.findByNumero", query = "SELECT t FROM TarjetaCreditoAlpes t WHERE t.numero = :numero")
    , @NamedQuery(name = "TarjetaCreditoAlpes.findByNombretitular", query = "SELECT t FROM TarjetaCreditoAlpes t WHERE t.nombreTitular = :nombreTitular")
    , @NamedQuery(name = "TarjetaCreditoAlpes.findByNombrebanco", query = "SELECT t FROM TarjetaCreditoAlpes t WHERE t.nombreBanco = :nombreBanco")
    , @NamedQuery(name = "TarjetaCreditoAlpes.findByCupo", query = "SELECT t FROM TarjetaCreditoAlpes t WHERE t.cupo = :cupo")
    , @NamedQuery(name = "TarjetaCreditoAlpes.findByFechaexpedicion", query = "SELECT t FROM TarjetaCreditoAlpes t WHERE t.fechaExpedicion = :fechaExpedicion")
    , @NamedQuery(name = "TarjetaCreditoAlpes.findByFechavencimiento", query = "SELECT t FROM TarjetaCreditoAlpes t WHERE t.fechaVencimiento = :fechaVencimiento")})
public class TarjetaCreditoAlpes implements Serializable {

    //-----------------------------------------------------------
    // Atributos
    //-----------------------------------------------------------
    @Id
    private String numero;

    private String nombreTitular;

    private String nombreBanco;

    private double cupo;

    @Temporal(TemporalType.DATE)
    private Date fechaExpedicion;

    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    //-----------------------------------------------------------
    // Constructor
    //-----------------------------------------------------------
    /**
     * Constructor sin argumentos
     */
    public TarjetaCreditoAlpes() {
    }

    /**
     * Constructor de la clase con argumentos
     *
     * @param numero Número de la tarjeta del cliente
     * @param nombreTitular Nombre del titular de la tarjeta
     * @param nombreBanco Nombre del banco del cliente
     * @param cupo Cupo disponible de la tarjeta
     * @param fechaExpedicion Fecha de expedición de la tarjeta
     * @param fechaVencimiento Fecha de vencimiento de la tarjeta
     */
    public TarjetaCreditoAlpes(String numero, String nombreTitular, String nombreBanco, long cupo, Date fechaExpedicion, Date fechaVencimiento) {
        this.numero = numero;
        this.nombreTitular = nombreTitular;
        this.nombreBanco = nombreBanco;
        this.cupo = cupo;
        this.fechaExpedicion = fechaExpedicion;
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public double getCupo() {
        return cupo;
    }

    public void setCupo(double cupo) {
        this.cupo = cupo;
    }

    public Date getFechaExpedicion() {
        return fechaExpedicion;
    }

    public void setFechaExpedicion(Date fechaExpedicion) {
        this.fechaExpedicion = fechaExpedicion;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

}
