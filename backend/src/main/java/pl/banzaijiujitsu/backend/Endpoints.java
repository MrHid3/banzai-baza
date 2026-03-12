package pl.banzaijiujitsu.backend;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Endpoints {
    @GetMapping("/")
    public String greeting(){
        return "Hello World";
    }
}
