/** Clasa pentru clienti, opertiile pentru baza de date
 * @author Varban Persida Ioana
 * @version 12 Ianuarie 2025
 */

package com.tema.database.repositories;

import com.tema.database.models.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ClientiRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Mapper pentru transformarea rândurilor din baza de date în obiecte Client
    private RowMapper<Client> clientRowMapper = (rs, rowNum) -> {
        Client client = new Client();
        client.setId(rs.getInt("id_client"));
        client.setNume(rs.getString("nume"));
        client.setPrenume(rs.getString("prenume"));
        client.setTelefon(rs.getString("telefon"));
        return client;
    };

    public List<Client> findAll() {
        String sql = "SELECT id_client, nume, prenume, telefon FROM clienti";
        return jdbcTemplate.query(sql, clientRowMapper);
    }

    public Client findById(int id) {
        String sql = "SELECT id_client, nume, prenume, telefon FROM clienti WHERE id_client = ?";
        return jdbcTemplate.queryForObject(sql, clientRowMapper, id);
    }

    public Client findByPhone(String phone) {
        String sql = "SELECT id_client, nume, prenume, telefon FROM clienti WHERE telefon = ?";
        List<Client> clients = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Client client = new Client();
            client.setId(rs.getInt("id_client"));
            client.setNume(rs.getString("nume"));
            client.setPrenume(rs.getString("prenume"));
            client.setTelefon(rs.getString("telefon"));
            return client;
        }, phone);

        return clients.isEmpty() ? null : clients.get(0);
    }

    // Inserare client
    public int insert(Client client) {
        String sql = "INSERT INTO clienti (nume, prenume, telefon) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, client.getNume(), client.getPrenume(), client.getTelefon());

        // Obține ID-ul generat automat
        String fetchIdSql = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(fetchIdSql, Integer.class);
    }

    // Actualizare client
    public int update(Client client) {
        String sql = "UPDATE clienti SET nume = ?, prenume = ?, telefon = ? WHERE id_client = ?";
        return jdbcTemplate.update(sql, client.getNume(), client.getPrenume(), client.getTelefon(), client.getId());
    }

    // Ștergere client
    public int delete(int id) {
        String sql = "DELETE FROM clienti WHERE id_client = ?";
        return jdbcTemplate.update(sql, id);
    }


    public void addFactura(int idAutoturism, String dataFactura, Double suma) {
        String sql = "INSERT INTO facturi (id_autoturism, data, total) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, idAutoturism, dataFactura, suma);
    }

    public List<Map<String, Object>> getClientiSiAutoturismeFiltrat(int numarMinim) {
        String sql = """
        SELECT c.nume AS client_nume, 
               c.prenume AS client_prenume,
               (SELECT COUNT(*) FROM autoturisme sub_a WHERE sub_a.id_client = c.id_client) AS total_autoturisme,
               a.marca AS autoturism_marca, 
               a.model AS autoturism_model
        FROM clienti c
        JOIN autoturisme a ON c.id_client = a.id_client
        WHERE (SELECT COUNT(*) 
               FROM autoturisme sub_a 
               WHERE sub_a.id_client = c.id_client) >= ?;
    """;
        return jdbcTemplate.queryForList(sql, numarMinim);
    }




}
