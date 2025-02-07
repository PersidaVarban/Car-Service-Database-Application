/** Clasa pentru modelul programare
 * @author Varban Persida Ioana
 * @version 12 Ianuarie 2025
 */

package com.tema.database.models;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public class Programare {

    private int id; // ID-ul unic al programării

    @NotNull(message = "ID-ul autoturismului este necesar.")
    private int idAutoturism; // Legătura cu autoturismul

    @NotNull(message = "Data programării este necesară.")
    @DateTimeFormat(pattern = "yyyy-MM-dd") // Pentru a procesa datele corect în formulare HTML
    private LocalDate data; // Data programării

    private List<Integer> reparatiiSelectate; // ID-urile reparațiilor asociate (many-to-many)

    // Getteri și setteri
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdAutoturism() {
        return idAutoturism;
    }

    public void setIdAutoturism(int idAutoturism) {
        this.idAutoturism = idAutoturism;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public List<Integer> getReparatiiSelectate() {
        return reparatiiSelectate;
    }

    public void setReparatiiSelectate(List<Integer> reparatiiSelectate) {
        this.reparatiiSelectate = reparatiiSelectate;
    }
}
