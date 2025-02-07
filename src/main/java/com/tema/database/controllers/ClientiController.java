/** Clasa pentru pagina intiala
 * @author Varban Persida Ioana
 * @version 16 Decembrie 2024
 */
package com.tema.database.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClientiController {
    @GetMapping("/")
    public String home() {
        return "index";
    }
}
