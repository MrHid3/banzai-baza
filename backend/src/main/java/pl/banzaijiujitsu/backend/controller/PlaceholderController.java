package pl.banzaijiujitsu.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlaceholderController {

    @GetMapping("/secure")
    public String secure() {
        return "this shit secure;";
    }
}
