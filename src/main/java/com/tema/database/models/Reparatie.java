/** Clasa pentru modelul reparatie
 * @author Varban Persida Ioana
 * @version 12 Ianuarie 2025
 */
package com.tema.database.models;

import java.time.LocalDate;

public class Reparatie {
    private int id;
    private String tip_reparatie;
    private String garantie;
    private String piese;
    private LocalDate data;
    public Reparatie() {}
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTipReparatie() {
        return tip_reparatie;
    }
    public void setTipReparatie(String tip_reparatie) {
        this.tip_reparatie = tip_reparatie;
    }
    public String getGarantie() {
        return garantie;
    }
    public void setGarantie(String garantie) {
        this.garantie = garantie;
    }
    public String getPiese() {
        return piese;
    }
    public void setPiese(String piese) {
        this.piese = piese;
    }
    public LocalDate getData() {
        return data;
    }
    public void setData(LocalDate data) {
        this.data = data;
    }

}
