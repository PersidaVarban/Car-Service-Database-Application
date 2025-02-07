/** Clasa pentru autoturisme, opertiile pentru baza de date
 * @author Varban Persida Ioana
 * @version 12 Ianuarie 2025
 */

package com.tema.database.repositories;

import com.tema.database.models.Autoturism;
import com.tema.database.models.Programare;
import com.tema.database.models.Reparatie;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class AutoturismeRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Mapper pentru transformarea rândurilor din baza de date în obiecte Client
    private RowMapper<Autoturism> autoturismRowMapper = (rs, rowNum) -> {
        Autoturism autoturism = new Autoturism();
        autoturism.setId(rs.getInt("id_autoturism"));
        autoturism.setIdClient(rs.getInt("id_client"));
        autoturism.setMarca(rs.getString("marca"));
        autoturism.setModel(rs.getString("model"));
        autoturism.setCaroserie(rs.getString("caroserie"));
        autoturism.setAnFabricatie(rs.getInt("an_fabricatie"));
        autoturism.setCapacitateCilindrica(rs.getInt("capacitate_cilindrica"));
        autoturism.setKilometri(rs.getInt("kilometri"));
        autoturism.setCuloare(rs.getString("culoare"));
        return autoturism;
    };

    public List<Autoturism> findAll() {
        String sql = "SELECT id_autoturism, id_client, marca, model, caroserie, an_fabricatie, capacitate_cilindrica, kilometri, culoare FROM autoturisme";
        return jdbcTemplate.query(sql, autoturismRowMapper);
    }

    public Autoturism findById(int id) {
        String sql = "SELECT id_autoturism, id_client, marca, model, caroserie, an_fabricatie, capacitate_cilindrica, kilometri, culoare FROM autoturisme WHERE id_autoturism = ?";
        return jdbcTemplate.queryForObject(sql, autoturismRowMapper, id);
    }

    // Inserare Autoturism
    public int insert(Autoturism autoturism) {
        String sql = "INSERT INTO autoturisme (id_client, marca, model, caroserie, an_fabricatie, capacitate_cilindrica, kilometri, culoare) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, autoturism.getIdClient(), autoturism.getMarca(), autoturism.getModel(), autoturism.getCaroserie(),
                autoturism.getAnFabricatie(), autoturism.getCapacitateCilindrica(), autoturism.getKilometri(), autoturism.getCuloare());
    }

    // Actualizare Autoturism
    public int update(Autoturism autoturism) {
        String sql = "UPDATE autoturisme SET marca=?, model=?, caroserie=?, an_fabricatie=?, capacitate_cilindrica=?, kilometri=?, culoare=? WHERE id_autoturism = ?";
        return jdbcTemplate.update(
                sql,
                autoturism.getMarca(),
                autoturism.getModel(),
                autoturism.getCaroserie(),
                autoturism.getAnFabricatie(),
                autoturism.getCapacitateCilindrica(),
                autoturism.getKilometri(),
                autoturism.getCuloare(),
                autoturism.getId()
        );
    }

    // Ștergere Autoturism
    public int delete(int id) {
        String sql = "DELETE FROM autoturisme WHERE id_autoturism = ?";
        return jdbcTemplate.update(sql, id);
    }

    public int countByClientId(int clientId) {
        String sql = "SELECT COUNT(*) FROM autoturisme WHERE id_client = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, clientId);
    }

    public List<Map<String, Object>> getProgramariGrupateByAutoturismId(int idAutoturism) {
        String sqlProgramari = """
        SELECT p.id_programare, p.data AS data_programare
        FROM programari p
        WHERE p.id_autoturism = ?;
    """;

        String sqlReparatii = """
        SELECT r.id_reparatie, r.tip_reparatie, r.data AS data_reparatie, r.garantie, r.piese, rp.id_programare
        FROM reparatii r
        INNER JOIN reparatii_programari rp ON r.id_reparatie = rp.id_reparatie
        WHERE rp.id_programare IN (
            SELECT id_programare
            FROM programari
            WHERE id_autoturism = ?
        );
    """;


        // Obține programările
        List<Map<String, Object>> programari = jdbcTemplate.queryForList(sqlProgramari, idAutoturism);

        // Obține reparațiile
        List<Map<String, Object>> reparatii = jdbcTemplate.queryForList(sqlReparatii, idAutoturism);

        // Grupa reparațiile pe baza id_programare
        Map<Integer, List<Map<String, Object>>> reparatiiGrupate = new HashMap<>();
        for (var reparatie : reparatii) {
            Integer idProgramare = (Integer) reparatie.get("id_programare");
            reparatiiGrupate.computeIfAbsent(idProgramare, k -> new ArrayList<>()).add(reparatie);
        }

        // Adaugă reparațiile în programările respective
        for (var programare : programari) {
            Integer idProgramare = (Integer) programare.get("id_programare");
            List<Map<String, Object>> reparatiiPentruProgramare = reparatiiGrupate.getOrDefault(idProgramare, new ArrayList<>());
            programare.put("reparatii", reparatiiPentruProgramare);
        }

        return programari;
    }


    public int insertProgramare(int idAutoturism, Programare programare) {
        String sql = "INSERT INTO programari (id_autoturism, data) VALUES (?, ?)";
        jdbcTemplate.update(sql, idAutoturism, programare.getData());
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
    }

    public int insertReparatie(Reparatie reparatie) {
        String sql = "INSERT INTO reparatii (tip_reparatie, garantie, piese, data) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, reparatie.getTipReparatie(), reparatie.getGarantie(), reparatie.getPiese(), LocalDate.now());
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
    }

    public void adaugaReparatieLaProgramare(int idProgramare, int idReparatie) {
        String sql = "INSERT INTO reparatii_programari (id_programare, id_reparatie) VALUES (?, ?)";
        jdbcTemplate.update(sql, idProgramare, idReparatie);
    }

    public List<Reparatie> getReparatiiExistente() {
        String sql = "SELECT id_reparatie, tip_reparatie, garantie, piese, data FROM reparatii";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Reparatie reparatie = new Reparatie();
            reparatie.setId(rs.getInt("id_reparatie"));
            reparatie.setTipReparatie(rs.getString("tip_reparatie"));
            reparatie.setGarantie(rs.getString("garantie"));
            reparatie.setPiese(rs.getString("piese"));
            reparatie.setData(rs.getDate("data").toLocalDate());
            return reparatie;
        });
    }

    public List<Autoturism> findByClientId(int clientId) {
        String sql = "SELECT id_autoturism, id_client, marca, model, caroserie, an_fabricatie, capacitate_cilindrica, kilometri, culoare FROM autoturisme WHERE id_client = ?";
        return jdbcTemplate.query(sql, autoturismRowMapper, clientId);
    }

    public List<Map<String, Object>> findFacturiByAutoturismId(int idAutoturism) {
        String sql = "SELECT id_factura, data, total FROM facturi WHERE id_autoturism = ?";
        return jdbcTemplate.queryForList(sql, idAutoturism);
    }

    public List<Map<String, Object>> getReparatiiCuAngajatiByAutoturismId(int idAutoturism) {
        String sql = """
                SELECT r.id_reparatie, r.tip_reparatie, r.data AS data_reparatie, a.nume AS angajat_nume, a.prenume AS angajat_prenume
               FROM programari p
               LEFT JOIN reparatii_programari rp ON p.id_programare = rp.id_programare
               LEFT JOIN reparatii r ON rp.id_reparatie = r.id_reparatie
               LEFT JOIN reparatii_angajati ra ON r.id_reparatie = ra.id_reparatie
               LEFT JOIN angajati a ON ra.id_angajat = a.id_angajat
               WHERE p.id_autoturism = ?
               ORDER BY r.data DESC;
              """;

        return jdbcTemplate.queryForList(sql, idAutoturism);
    }

    public Map<String, Object> getReviziiStatisticiByAutoturismId(int idAutoturism) {
        String sql = """
            SELECT 
                COUNT(DISTINCT r.id_reparatie) AS total_reparatii,
                SUM(f.total) AS total_cost,
                MAX(r.data) AS ultima_reparatie
            FROM autoturisme a
            LEFT JOIN programari p ON a.id_autoturism = p.id_autoturism
            LEFT JOIN reparatii_programari rp ON p.id_programare = rp.id_programare
            LEFT JOIN reparatii r ON rp.id_reparatie = r.id_reparatie
            LEFT JOIN facturi f ON a.id_autoturism = f.id_autoturism
            WHERE a.id_autoturism = ?;
            """;

        return jdbcTemplate.queryForMap(sql, idAutoturism);
    }

    public List<Map<String, Object>> getProgramariViitoareByAutoturismId(int idAutoturism) {
        String sql = """
        SELECT p.data AS data_programare, a.nume AS angajat_nume, a.prenume AS angajat_prenume
        FROM programari p
        LEFT JOIN reparatii_programari rp ON p.id_programare = rp.id_programare
        LEFT JOIN reparatii r ON rp.id_reparatie = r.id_reparatie
        LEFT JOIN reparatii_angajati ra ON r.id_reparatie = ra.id_reparatie
        LEFT JOIN angajati a ON ra.id_angajat = a.id_angajat
        WHERE p.id_autoturism = ? AND p.data >= CURDATE()
        ORDER BY p.data ASC
    """;
        return jdbcTemplate.queryForList(sql, idAutoturism);
    }

    public List<Map<String, Object>> getAutoturismeCuMinimProgramari(int numarProgramari) {
        String sql = """
        SELECT a.marca, a.model
        FROM autoturisme a
        JOIN (
            SELECT p.id_autoturism, COUNT(*) AS numar_programari
            FROM programari p
            GROUP BY p.id_autoturism
            HAVING COUNT(*) > ?
        ) s ON a.id_autoturism = s.id_autoturism
        """;

        return jdbcTemplate.queryForList(sql, numarProgramari);
    }


}
