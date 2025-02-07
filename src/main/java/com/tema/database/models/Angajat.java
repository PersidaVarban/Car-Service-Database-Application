/** Clasa pentru modelul angajat
 * @author Varban Persida Ioana
 * @version 12 Ianuarie 2025
 */
package com.tema.database.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class Angajat {
    private int id;

    @NotEmpty(message = "Numele nu poate fi gol")
    private String nume;

    @NotEmpty(message = "Prenumele nu poate fi gol")
    private String prenume;

    @NotEmpty(message = "Profesia nu poate fi goală")
    private String profesie;

    @NotNull(message = "Salariul nu poate fi gol")
    @Min(value = 0, message = "Salariul trebuie să fie un număr pozitiv")
    private Integer salariu;

    // Getters și Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getProfesie() {
        return profesie;
    }

    public void setProfesie(String profesie) {
        this.profesie = profesie;
    }

    public Integer getSalariu() {
        return salariu;
    }

    public void setSalariu(Integer salariu) {
        this.salariu = salariu;
    }
}
