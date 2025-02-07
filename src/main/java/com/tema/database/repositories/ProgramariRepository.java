/** Clasa pentru programari, opertiile pentru baza de date
 * @author Varban Persida Ioana
 * @version 12 Ianuarie 2025
 */

package com.tema.database.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ProgramariRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> findProgramariWithReparatii(String luna, Integer an) {
        String sql = """
        SELECT p.id_programare, p.data, a.marca, a.model, r.tip_reparatie
        FROM programari p
        INNER JOIN autoturisme a ON p.id_autoturism = a.id_autoturism
        LEFT JOIN reparatii_programari rp ON p.id_programare = rp.id_programare
        LEFT JOIN reparatii r ON rp.id_reparatie = r.id_reparatie
        WHERE MONTHNAME(p.data) = ? AND YEAR(p.data) = ?
        ORDER BY p.data;
        """;

        return jdbcTemplate.queryForList(sql, luna.toUpperCase(), an);
    }

}
