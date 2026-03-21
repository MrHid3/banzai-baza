package pl.banzaijiujitsu.backend.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.banzaijiujitsu.backend.model.Localization;
import pl.banzaijiujitsu.backend.service.LocalizationService;

import java.util.List;

@RestController
@RequestMapping("/api/localization")
public class LocalizationController {

    private final LocalizationService localizationService;

    @Autowired
    public LocalizationController(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

    @GetMapping("/all")
    ResponseEntity<List<Localization>> getAll(HttpServletResponse httpServletResponse) {

        return ResponseEntity.ok(localizationService.findAll());
    }
}
