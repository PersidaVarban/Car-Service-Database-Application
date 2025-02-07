/** Clasa pentru gestionarea programarilor
 * @author Varban Persida Ioana
 * @version 12 Ianuarie 2025
 */
package com.tema.database.controllers;

import com.tema.database.repositories.ProgramariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/programari")
public class ProgramariController {
    @Autowired
    private ProgramariRepository programariRepository;

    @GetMapping("")
    public String index(@RequestParam(required = false) String luna,
                        @RequestParam(required = false) Integer an,
                        Model model) {
        if (luna == null || luna.isEmpty()) {
            luna = LocalDate.now().getMonth().toString();
        }
        if (an == null) {
            an = LocalDate.now().getYear();
        }

        // Obține programările și reparațiile pentru luna și anul selectate
        List<Map<String, Object>> programariCuReparatii = programariRepository.findProgramariWithReparatii(luna, an);

        // Adaugă datele în model
        model.addAttribute("luna", luna);
        model.addAttribute("an", an);
        model.addAttribute("programariCuReparatii", programariCuReparatii);

        return "programari/index";
    }


}
