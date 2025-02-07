/** Clasa pentru gestionarea autoturismelor
 * @author Varban Persida Ioana
 * @version 12 Ianuarie 2025
 */
package com.tema.database.controllers;

import com.tema.database.models.Angajat;
import com.tema.database.repositories.AngajatiRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/angajati")
public class AngajatiController {
    @Autowired
    private AngajatiRepository rep;

    @GetMapping({"", "/"})
    public String getAngajati(Model model) {
        var angajati = rep.findAll(); // Folosește metoda SQL nativă
        model.addAttribute("angajati", angajati);
        return "angajati/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("angajat", new Angajat());
        return "angajati/add";
    }

    @PostMapping("/add")
    public String addClient(@Valid @ModelAttribute Angajat angajat, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "angajati/add";
        }

        // Verificăm dacă angajatul există deja
        var angajatiExistenti = rep.findAll();
        boolean angajatExista = angajatiExistenti.stream().anyMatch(c ->
                c.getNume().equalsIgnoreCase(angajat.getNume()) &&
                        c.getPrenume().equalsIgnoreCase(angajat.getPrenume()) &&
                        c.getProfesie().equalsIgnoreCase(angajat.getProfesie())
        );

        if (angajatExista) {
            result.addError(new FieldError("angajat", "nume", "Angajatul este deja adăugat!"));
            return "angajati/add";
        }

        // Salvăm angajatul
        rep.insert(angajat);
        return "redirect:/angajati";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        Angajat angajat = rep.findById(id);
        model.addAttribute("angajat", angajat);
        return "angajati/edit";
    }

    @PostMapping("/edit")
    public String updateAngajat(@Valid @ModelAttribute Angajat angajat, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "angajati/edit";
        }

        var angajatiExistenti = rep.findAll();
        boolean angajatExista = angajatiExistenti.stream().anyMatch(c ->
                c.getId() != angajat.getId() && // Excludem angajatul curent
                        c.getNume().equalsIgnoreCase(angajat.getNume()) &&
                        c.getPrenume().equalsIgnoreCase(angajat.getPrenume()) &&
                        c.getProfesie().equalsIgnoreCase(angajat.getProfesie())
        );

        if (angajatExista) {
            result.addError(new FieldError("angajat", "nume", "Alt angajat cu aceste detalii există deja!"));
            return "angajati/edit";
        }

        // Actualizăm clientul
        rep.update(angajat);
        return "redirect:/angajati";
    }

    @PostMapping("/delete/{id}")
    public String deleteAngajat(@PathVariable int id) {
        // Ștergem clientul
        rep.deleteAngajatReparatii(id);
        rep.delete(id);
        return "redirect:/angajati";
    }

    @GetMapping("/reparatii/{id}")
    public String getReparatiiByAngajat(@PathVariable int id, Model model) {
        Angajat angajat = rep.findById(id);
        // Obține reparațiile asociate angajatului din repository
        List<Map<String, Object>> reparatii = rep.getReparatiiByAngajatId(id);

        // Adaugă reparațiile și ID-ul angajatului în model pentru Thymeleaf
        model.addAttribute("reparatii", reparatii);
        model.addAttribute("angajat", angajat); // Adaugă ID-ul angajatului pentru redirecționare
        return "angajati/reparatii"; // Returnează pagina Thymeleaf "reparatii.html"
    }

    @GetMapping("/addreparatii/{id}")
    public String showAddReparatiiPage(@PathVariable int id, Model model) {
        // Obține angajatul
        Angajat angajat = rep.findById(id);

        // Obține reparațiile neasociate
        List<Map<String, Object>> reparatiiDisponibile = rep.getReparatiiDisponibile(id);

        // Adaugă datele în model
        model.addAttribute("angajat", angajat);
        model.addAttribute("reparatiiDisponibile", reparatiiDisponibile);

        return "angajati/addreparatii";
    }

    @PostMapping("/addreparatii/{idAngajat}")
    public String addReparatieToAngajat(@PathVariable int idAngajat, @RequestParam int idReparatie, Model model) {
        // Adaugă relația între angajat și reparație
        rep.addAngajatToReparatie(idAngajat, idReparatie);

        // Redirecționează înapoi la lista de angajați
        return "redirect:/angajati";
    }

    @GetMapping("/total/{id}")
    public String getNumarTotalReparatii(@PathVariable int id, Model model) {
        // Obține numărul total de reparații efectuate de angajat
        int totalReparatii = rep.getNumarTotalReparatiiByAngajatId(id);

        // Obține informațiile despre angajat
        Angajat angajat = rep.findById(id);

        // Adaugă datele în model pentru Thymeleaf
        model.addAttribute("angajat", angajat);
        model.addAttribute("totalReparatii", totalReparatii);

        return "angajati/total"; // Pagina Thymeleaf pentru afișarea rezultatului
    }

    @GetMapping("/media")
    public String getAngajatiCuReparatiiPesteMedia(Model model) {
        // Obține lista angajaților care participă la mai multe reparații decât media
        List<Map<String, Object>> angajati = rep.getAngajatiCuReparatiiPesteMedia();

        // Adaugă lista în model pentru a fi utilizată în Thymeleaf
        model.addAttribute("angajati", angajati);

        return "angajati/media"; // Returnează pagina HTML
    }


}
