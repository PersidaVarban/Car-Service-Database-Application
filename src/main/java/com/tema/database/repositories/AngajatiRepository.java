/** Clasa pentru angajati, opertiile pentru baza de date
 * @author Varban Persida Ioana
 * @version 12 Ianuarie 2025
 */

package com.tema.database.repositories;

import com.tema.database.models.Angajat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AngajatiRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Mapper pentru transformarea rândurilor din baza de date în obiecte Client
    private RowMapper<Angajat> angajatRowMapper = (rs, rowNum) -> {
        Angajat angajat = new Angajat();
        angajat.setId(rs.getInt("id_angajat"));
        angajat.setNume(rs.getString("nume"));
        angajat.setPrenume(rs.getString("prenume"));
        angajat.setProfesie(rs.getString("profesie"));
        angajat.setSalariu(rs.getInt("salariu"));
        return angajat;
    };

    public List<Angajat> findAll() {
        String sql = "SELECT id_angajat, nume, prenume, profesie, salariu FROM angajati";
        return jdbcTemplate.query(sql, angajatRowMapper);
    }

    public Angajat findById(int id) {
        String sql = "SELECT id_angajat, nume, prenume, profesie, salariu FROM angajati WHERE id_angajat = ?";
        return jdbcTemplate.queryForObject(sql, angajatRowMapper, id);
    }


    // Inserare angajat
    public int insert(Angajat angajat) {
        String sql = "INSERT INTO angajati (nume, prenume, profesie, salariu) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, angajat.getNume(), angajat.getPrenume(), angajat.getProfesie(), angajat.getSalariu());

        // Obține ID-ul generat automat
        String fetchIdSql = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(fetchIdSql, Integer.class);
    }

    // Actualizare angajat
    public int update(Angajat angajat) {
        String sql = "UPDATE angajati SET nume = ?, prenume = ?, profesie = ?, salariu = ? WHERE id_angajat = ?";
        return jdbcTemplate.update(sql, angajat.getNume(), angajat.getPrenume(), angajat.getProfesie(), angajat.getSalariu(), angajat.getId());
    }

    // Ștergere angajat
    public int delete(int id) {
        String sql = "DELETE FROM angajati WHERE id_angajat = ?";
        return jdbcTemplate.update(sql, id);
    }

    public void deleteAngajatReparatii(int idAngajat) {
        String sql = "DELETE FROM reparatii_angajati WHERE id_angajat = ?";
        jdbcTemplate.update(sql, idAngajat);
    }

    public List<Map<String, Object>> getReparatiiByAngajatId(int idAngajat) {
        String sql = """
        SELECT r.id_reparatie, r.tip_reparatie, r.garantie, r.piese, r.data
        FROM reparatii r
        INNER JOIN reparatii_angajati ar ON r.id_reparatie = ar.id_reparatie
        WHERE ar.id_angajat = ?;
    """;
        return jdbcTemplate.queryForList(sql, idAngajat);
    }


    public void addAngajatToReparatie(int idAngajat, int idReparatie) {
        String sql = "INSERT INTO reparatii_angajati (id_angajat, id_reparatie) VALUES (?, ?)";
        jdbcTemplate.update(sql, idAngajat, idReparatie);
    }

    public List<Map<String, Object>> getReparatiiDisponibile(int idAngajat) {
        String sql = """
        SELECT r.id_reparatie, r.tip_reparatie
        FROM reparatii r
        WHERE r.id_reparatie NOT IN (
            SELECT ar.id_reparatie
            FROM reparatii_angajati ar
            WHERE ar.id_angajat = ?
        );
    """;
        return jdbcTemplate.queryForList(sql, idAngajat);
    }

    public int getNumarTotalReparatiiByAngajatId(int idAngajat) {
        String sql = """
        SELECT COUNT(r.id_reparatie) AS total_reparatii
        FROM reparatii r
        LEFT JOIN reparatii_angajati ra ON r.id_reparatie = ra.id_reparatie
        WHERE ra.id_angajat = ?;
    """;
        return jdbcTemplate.queryForObject(sql, Integer.class, idAngajat);
    }

    public List<Map<String, Object>> getAngajatiCuReparatiiPesteMedia() {
        String sql = """
        SELECT a.id_angajat, 
               a.nume AS angajat_nume, 
               a.prenume AS angajat_prenume, 
               COUNT(ra.id_reparatie) AS numar_reparatii
        FROM angajati a
        JOIN reparatii_angajati ra ON a.id_angajat = ra.id_angajat
        GROUP BY a.id_angajat, a.nume, a.prenume
        HAVING COUNT(ra.id_reparatie) > 
               (SELECT AVG(numar_reparatii)
                FROM (SELECT COUNT(ra.id_reparatie) AS numar_reparatii
                      FROM reparatii_angajati ra
                      GROUP BY ra.id_angajat) subquery);
    """;
        return jdbcTemplate.queryForList(sql);
    }


}
