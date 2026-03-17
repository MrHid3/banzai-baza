package pl.banzaijiujitsu.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.banzaijiujitsu.backend.model.Localization;
import pl.banzaijiujitsu.backend.repository.LocalizationRepository;

import java.util.Optional;

@Service
public class LocalizationService {

    private final LocalizationRepository localizationRepository;

    @Autowired
    public LocalizationService(LocalizationRepository localizationRepository) {
        this.localizationRepository = localizationRepository;
    }

    public Optional<Localization> findById(Long id) {
        return localizationRepository.findById(id);
    }

    public Optional<Localization> findByName(String name) {
        return localizationRepository.findByName(name);
    }

    public Localization save(Localization localization) {
        return localizationRepository.save(localization);
    }
}
