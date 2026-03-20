package pl.banzaijiujitsu.backend.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.banzaijiujitsu.backend.model.Member;
import pl.banzaijiujitsu.backend.service.JwtService;

import java.util.List;
import java.util.UUID;

@RestController("/api/")
public class MemberController {

    @Autowired
    public JwtService jwtService;

    @GetMapping("/members")
    public ResponseEntity<String> member(@CookieValue(name = "refreshToken") String refreshToken, HttpServletResponse response){
        UUID uuid = jwtService.extractUuid(refreshToken);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(uuid.toString());
    }
}
