/** Clasa pentru modelul autoturism
 * @author Varban Persida Ioana
 * @version 12 Ianuarie 2025
 */
package com.tema.database.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class Autoturism {
    private int id;
    private int idClient;

    @NotEmpty(message = "Marca nu poate fi goală")
    private String marca;

    @NotEmpty(message = "Modelul nu poate fi gol")
    private String model;

    @NotEmpty(message = "Caroseria nu poate fi goală")
    private String caroserie;

    @NotNull(message = "Anul fabricării este obligatoriu")
    private int anFabricatie;

    @NotNull(message = "Capacitatea cilindrică este obligatorie")
    private int capacitateCilindrica;

    @NotNull(message = "Numărul de kilometri este obligatoriu")
    private int kilometri;

    @NotEmpty(message = "Culoarea nu poate fi goală")
    private String culoare;

    // Getteri și setteri
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCaroserie() {
        return caroserie;
    }

    public void setCaroserie(String caroserie) {
        this.caroserie = caroserie;
    }

    public int getAnFabricatie() {
        return anFabricatie;
    }

    public void setAnFabricatie(int anFabricatie) {
        this.anFabricatie = anFabricatie;
    }

    public int getCapacitateCilindrica() {
        return capacitateCilindrica;
    }

    public void setCapacitateCilindrica(int capacitateCilindrica) {
        this.capacitateCilindrica = capacitateCilindrica;
    }

    public int getKilometri() {
        return kilometri;
    }

    public void setKilometri(int kilometri) {
        this.kilometri = kilometri;
    }

    public String getCuloare() {
        return culoare;
    }

    public void setCuloare(String culoare) {
        this.culoare = culoare;
    }

}
