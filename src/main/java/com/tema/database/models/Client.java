/** Clasa pentru modelul client
 * @author Varban Persida Ioana
 * @version 12 Ianuarie 2025
 */

package com.tema.database.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Client {
    private int id;

    @NotEmpty(message = "Numele nu poate fi gol")
    @Size(max = 50, message = "Numele nu poate avea mai mult de 50 de caractere")
    private String nume;

    @NotEmpty(message = "Prenumele nu poate fi gol")
    @Size(max = 50, message = "Prenumele nu poate avea mai mult de 50 de caractere")
    private String prenume;

    @NotEmpty(message = "Telefonul nu poate fi gol")
    @Pattern(regexp = "\\d{10}", message = "Telefonul trebuie să conțină exact 10 cifre")
    private String telefon;

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

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }
}

