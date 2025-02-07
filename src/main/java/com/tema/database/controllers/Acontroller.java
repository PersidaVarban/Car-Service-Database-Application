/** Clasa pentru gestionarea angajaților
 * @author Varban Persida Ioana
 * @version 12 Ianuarie 2025
 */
package com.tema.database.controllers;

import com.tema.database.models.Autoturism;
import com.tema.database.models.Client;
import com.tema.database.models.Programare;
import com.tema.database.models.Reparatie;
import com.tema.database.repositories.AutoturismeRepository;
import com.tema.database.repositories.ClientiRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;


import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/autoturisme")
public class Acontroller {
    @Autowired
    private AutoturismeRepository rep;

    @Autowired
    private ClientiRepository clientRepository;

    @GetMapping({"", "/"})
    public String getAutoturisme(Model model) {
        var autoturisme = rep.findAll(); // Folosește metoda SQL nativă
        model.addAttribute("autoturisme", autoturisme);
        return "autoturisme/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("autoturism", new Autoturism());
        model.addAttribute("clients", clientRepository.findAll()); // Toți clienții pentru select
        return "autoturisme/add";
    }

    @PostMapping("/add")
    public String addAutoturism(
            @RequestParam(required = false) Integer idClient,
            @RequestParam(required = false) String numeClient,
            @RequestParam(required = false) String prenumeClient,
            @RequestParam(required = false) String telefon,
            @Valid @ModelAttribute Autoturism autoturism,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("clients", clientRepository.findAll());
            return "autoturisme/add";
        }

        Client client;

        // Gestionăm clientul
        if (idClient != null) {
            // Clientul este selectat din listă
            client = clientRepository.findById(idClient);
            if (client == null) {
                result.rejectValue("idClient", "error.autoturism", "Clientul selectat nu există.");
                model.addAttribute("clients", clientRepository.findAll());
                return "autoturisme/add";
            }
        } else {
            // Verificăm dacă clientul există deja după telefon
            client = clientRepository.findByPhone(telefon);
            if (client != null) {
                result.rejectValue("telefon", "error.autoturism", "Clientul există deja.");
                model.addAttribute("clients", clientRepository.findAll());
                return "autoturisme/add";
            }

            // Validăm datele pentru un client nou
            if (numeClient == null || prenumeClient == null || telefon == null || telefon.isEmpty()) {
                result.rejectValue("telefon", "error.autoturism", "Trebuie să specificați un nume, prenume și telefon pentru clientul nou.");
                model.addAttribute("clients", clientRepository.findAll());
                return "autoturisme/add";
            }

            // Creăm un client nou
            client = new Client();
            client.setNume(numeClient);
            client.setPrenume(prenumeClient);
            client.setTelefon(telefon);

            // Inserăm clientul și obținem ID-ul generat
            int generatedId = clientRepository.insert(client);
            if (generatedId == 0) {
                result.rejectValue("telefon", "error.autoturism", "Eroare la crearea clientului nou.");
                model.addAttribute("clients", clientRepository.findAll());
                return "autoturisme/add";
            }
            client.setId(generatedId);
        }

        // Setăm ID-ul clientului pe autoturism
        autoturism.setIdClient(client.getId());

        // Verificăm dacă autoturismul este duplicat
        var autoturismeExistente = rep.findAll();
        boolean autoturismExista = autoturismeExistente.stream().anyMatch(c ->
                c.getIdClient() == autoturism.getIdClient() &&
                        c.getMarca().equalsIgnoreCase(autoturism.getMarca()) &&
                        c.getModel().equalsIgnoreCase(autoturism.getModel()) &&
                        c.getAnFabricatie() == autoturism.getAnFabricatie() &&
                        c.getCapacitateCilindrica() == autoturism.getCapacitateCilindrica() &&
                        c.getKilometri() == autoturism.getKilometri() &&
                        c.getCuloare().equalsIgnoreCase(autoturism.getCuloare())
        );

        if (autoturismExista) {
            result.rejectValue("marca", "error.autoturism", "Un autoturism identic există deja în baza de date.");
            model.addAttribute("autoturism", autoturism);
            model.addAttribute("clients", clientRepository.findAll());
            return "autoturisme/add";
        }

        // Salvăm autoturismul
        rep.insert(autoturism);

        return "redirect:/autoturisme";
    }



    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        Autoturism autoturism = rep.findById(id);
        model.addAttribute("autoturism", autoturism);
        return "autoturisme/edit";
    }

    @PostMapping("/edit")
    public String updateautoturism(@Valid @ModelAttribute Autoturism autoturism, BindingResult result, Model model) {
        // Verifică erorile de validare
        if (result.hasErrors()) {
            model.addAttribute("autoturism", autoturism);
            return "autoturisme/edit";
        }

        // Obține toate autoturismele existente
        var autoturismeExistente = rep.findAll();

        // Verifică pentru duplicate (excludem autoturismul curent)
        boolean autoturismExista = autoturismeExistente.stream().anyMatch(a ->
                a.getId() != autoturism.getId() && // Exclude autoturismul curent
                        a.getIdClient() == autoturism.getIdClient() &&
                        a.getMarca().equalsIgnoreCase(autoturism.getMarca()) &&
                        a.getModel().equalsIgnoreCase(autoturism.getModel()) &&
                        a.getAnFabricatie() == autoturism.getAnFabricatie() &&
                        a.getCapacitateCilindrica() == autoturism.getCapacitateCilindrica() &&
                        a.getKilometri() == autoturism.getKilometri() &&
                        a.getCuloare().equalsIgnoreCase(autoturism.getCuloare())
        );

        // Dacă există un duplicat, oprește procesul și afișează eroarea
        if (autoturismExista) {
            result.rejectValue("marca", "error.autoturism", "Un autoturism identic există deja în baza de date.");
            model.addAttribute("autoturism", autoturism);
            return "autoturisme/edit";
        }

        // Dacă nu există duplicate, actualizează autoturismul
        rep.update(autoturism);

        // Redirecționează la lista de autoturisme
        return "redirect:/autoturisme";
    }

    @PostMapping("/delete/{id}")
    public String deleteAutoturism(@PathVariable int id) {
        rep.delete(id);
        return "redirect:/autoturisme";
    }

    @GetMapping("/details/{id}")
    public String showDetails(@PathVariable int id, Model model) {
        // Obține autoturismul selectat
        Autoturism autoturism = rep.findById(id);

        // Obține clientul asociat autoturismului
        Client client = clientRepository.findById(autoturism.getIdClient());

        // Obține programările și reparațiile grupate
        var programariGrupate = rep.getProgramariGrupateByAutoturismId(id);

        // Adaugă datele în model pentru Thymeleaf
        model.addAttribute("autoturism", autoturism);
        model.addAttribute("client", client);
        model.addAttribute("programariGrupate", programariGrupate);

        return "autoturisme/details";
    }

    @GetMapping("/programare/{idAutoturism}")
    public String showAddProgramareForm(@PathVariable int idAutoturism, Model model) {
        // Adaugă reparațiile existente
        List<Reparatie> reparatii = rep.getReparatiiExistente();
        model.addAttribute("reparatii", reparatii);

        // Creează obiecte noi pentru programare și reparații noi
        model.addAttribute("programare", new Programare());
        model.addAttribute("reparatieNoua", new Reparatie());

        return "autoturisme/programare";
    }

    @PostMapping("/programare/{idAutoturism}")
    public String addProgramare(
            @PathVariable int idAutoturism,
            @Valid @ModelAttribute Programare programare,
            @RequestParam(required = false) List<Integer> reparatiiSelectate,
            @Valid @ModelAttribute Reparatie reparatieNoua,
            BindingResult result,
            Model model
    ) {

        if (result.hasErrors()) {
            // Reîncarcă datele în caz de eroare
            Autoturism autoturism = rep.findById(idAutoturism);
            model.addAttribute("autoturism", autoturism);
            model.addAttribute("reparatii", rep.getReparatiiExistente());
            return "autoturisme/programare";
        }

        // Adaugă programarea
        int idProgramare = rep.insertProgramare(idAutoturism, programare);

        // Adaugă reparațiile selectate
        if (reparatiiSelectate != null) {
            for (int idReparatie : reparatiiSelectate) {
                rep.adaugaReparatieLaProgramare(idProgramare, idReparatie);
            }
        }

        // Adaugă reparația nouă (dacă este completată)
        if (reparatieNoua.getTipReparatie() != null && !reparatieNoua.getTipReparatie().isEmpty()) {
            int idReparatieNoua = rep.insertReparatie(reparatieNoua);
            rep.adaugaReparatieLaProgramare(idProgramare, idReparatieNoua);
        }

        return "redirect:/autoturisme";
    }

    @GetMapping("/angajatisireparatii/{id}")
    public String getDetaliiAutoturism(@PathVariable int id, Model model) {
        // Obține detaliile autoturismului
        Autoturism autoturism = rep.findById(id);

        // Obține reparațiile și angajații asociați
        List<Map<String, Object>> reparatiiCuAngajati = rep.getReparatiiCuAngajatiByAutoturismId(id);

        // Adaugă datele în model pentru Thymeleaf
        model.addAttribute("autoturism", autoturism);
        model.addAttribute("reparatiiCuAngajati", reparatiiCuAngajati);

        return "autoturisme/angajatisireparatii"; // Pagina Thymeleaf dedicată
    }

    @GetMapping("/revizii/{id}")
    public String getReviziiStatistici(@PathVariable int id, Model model) {
        // Obține statistici pentru autoturismul selectat
        Map<String, Object> statistici = rep.getReviziiStatisticiByAutoturismId(id);

        // Obține detaliile autoturismului
        Autoturism autoturism = rep.findById(id);

        // Adaugă datele în model
        model.addAttribute("autoturism", autoturism);
        model.addAttribute("statistici", statistici);

        return "autoturisme/revizii";
    }

    @GetMapping("/programari-viitoare/{idAutoturism}")
    public String getProgramariViitoare(@PathVariable int idAutoturism, Model model) {
        var programariViitoare = rep.getProgramariViitoareByAutoturismId(idAutoturism);
        var autoturism = rep.findById(idAutoturism);

        model.addAttribute("autoturism", autoturism);
        model.addAttribute("programariViitoare", programariViitoare);

        return "autoturisme/programari-viitoare";
    }

    @GetMapping("/filtrare")
    public String filtrareProgramari(@RequestParam("numarProgramari") int numarProgramari, Model model) {
        List<Map<String, Object>> autoturismeFiltrate = rep.getAutoturismeCuMinimProgramari(numarProgramari);
        model.addAttribute("autoturismeFiltrate", autoturismeFiltrate);
        model.addAttribute("numarProgramari", numarProgramari);
        return "autoturisme/filtrare";
    }

}
