/** Clasa pentru gestionarea clientilor
 * @author Varban Persida Ioana
 * @version 12 Ianuarie 2025
 */
package com.tema.database.controllers;

import com.tema.database.models.Autoturism;
import com.tema.database.models.Client;
import com.tema.database.repositories.AutoturismeRepository;
import com.tema.database.repositories.ClientiRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/clienti")
public class Ccontroller {
    @Autowired
    private ClientiRepository rep;

    @Autowired
    private AutoturismeRepository autoturismeRepository;
    

    @GetMapping({"", "/"})
    public String getClienti(Model model) {
        var clienti = rep.findAll(); // Folosește metoda SQL nativă
        model.addAttribute("clienti", clienti);
        return "clienti/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("client", new Client());
        return "clienti/add";
    }

    @PostMapping("/add")
    public String addClient(@Valid @ModelAttribute Client client, BindingResult result) {
        if (result.hasErrors()) {
            return "clienti/add";
        }

        // Verificăm dacă clientul există deja în baza de date
        var clientiExistenti = rep.findAll();
        boolean clientExista = clientiExistenti.stream().anyMatch(c ->
                c.getNume().equalsIgnoreCase(client.getNume()) &&
                        c.getPrenume().equalsIgnoreCase(client.getPrenume()) &&
                        c.getTelefon().equals(client.getTelefon())
        );

        if (clientExista) {
            result.addError(new FieldError("client", "telefon", "Clientul este deja adăugat!"));
            return "clienti/add";
        }

        // Salvăm clientul
        rep.insert(client);
        return "redirect:/clienti";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        Client client = rep.findById(id);
        model.addAttribute("client", client);
        return "clienti/edit";
    }

    @PostMapping("/edit")
    public String updateClient(@Valid @ModelAttribute Client client, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "clienti/edit";
        }

        // Verificăm dacă există alt client cu aceleași detalii
        var clientiExistenti = rep.findAll();
        boolean clientExista = clientiExistenti.stream().anyMatch(c ->
                c.getId() != client.getId() && // Excludem clientul curent
                        c.getNume().equalsIgnoreCase(client.getNume()) &&
                        c.getPrenume().equalsIgnoreCase(client.getPrenume()) &&
                        c.getTelefon().equals(client.getTelefon())
        );

        if (clientExista) {
            result.addError(new FieldError("client", "telefon", "Clientul este deja adăugat!"));
            return "clienti/edit";
        }

        // Actualizăm clientul
        rep.update(client);
        return "redirect:/clienti";
    }

    @PostMapping("/delete/{id}")
    public String deleteClient(@PathVariable int id, Model model) {
        // Verificăm dacă clientul are autoturisme asociate
        int count = autoturismeRepository.countByClientId(id);
        if (count > 0) {
            model.addAttribute("errorMessage", "Clientul nu poate fi șters deoarece are autoturisme asociate.");
            model.addAttribute("clienti", rep.findAll()); // Populează lista de clienți pentru afișare
            return "clienti/index";
        }

        // Ștergem clientul
        rep.delete(id);
        return "redirect:/clienti";
    }

    @GetMapping("facturi/{id}")
    public String showDetails(@PathVariable int id, Model model) {
        // Obține clientul selectat
        Client client = rep.findById(id);

        // Obține lista de autoturisme asociate clientului
        List<Autoturism> autoturisme = autoturismeRepository.findByClientId(id);

        // Creează o mapare între fiecare autoturism și facturile sale
        Map<Integer, List<Map<String, Object>>> facturiPerAutoturism = new HashMap<>();
        for (Autoturism autoturism : autoturisme) {
            List<Map<String, Object>> facturi = autoturismeRepository.findFacturiByAutoturismId(autoturism.getId());
            facturiPerAutoturism.put(autoturism.getId(), facturi);
        }

        // Adaugă datele în model
        model.addAttribute("client", client);
        model.addAttribute("autoturisme", autoturisme);
        model.addAttribute("facturiPerAutoturism", facturiPerAutoturism);

        return "clienti/facturi";
    }


    @GetMapping("/addfacturi/{id}")
    public String showAddFacturaForm(@PathVariable int id, Model model) {
        // Obține clientul
        Client client = rep.findById(id);
        // Obține autoturismele asociate clientului
        List<Autoturism> autoturisme = autoturismeRepository.findByClientId(id);

        // Adaugă clientul și autoturismele în model
        model.addAttribute("client", client);
        model.addAttribute("autoturisme", autoturisme);

        return "clienti/addfacturi";
    }

    @PostMapping("/addfacturi/{clientId}")
    public String saveFactura(@PathVariable int clientId,
                              @RequestParam int idAutoturism,
                              @RequestParam String dataFactura,
                              @RequestParam Double suma) {
        // Salvează factura cu id_autoturism
        rep.addFactura(idAutoturism, dataFactura, suma);

        // Redirecționează către lista de facturi sau o altă pagină
        return "redirect:/clienti/facturi/" + clientId;
    }

    @GetMapping("/filtrare")
    public String filtreazaClientiSiAutoturisme(@RequestParam int numarAutoturisme, Model model) {
        // Obține clienții și autoturismele asociate
        List<Map<String, Object>> clientiSiAutoturisme = rep.getClientiSiAutoturismeFiltrat(numarAutoturisme);

        // Adaugă datele în model
        model.addAttribute("clientiSiAutoturisme", clientiSiAutoturisme);
        model.addAttribute("numarAutoturisme", numarAutoturisme);

        return "clienti/filtrare";
    }


}
