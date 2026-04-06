package pl.banzaijiujitsu.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pl.banzaijiujitsu.backend.model.AppUser;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromAdress;

    @Value("${app.magic-link.expiry-hours}")
    private Integer expiryHours;

    public void sendInviteLink(AppUser appUser, String token){

        String link = frontendUrl + "/set-password?token=" + token;

        Context context = new Context();
        context.setVariable("link", link);
        context.setVariable("expiryHours", expiryHours);

        String html = templateEngine.process("email/registration", context);

        MimeMessagePreparator preparator = msg -> {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(fromAdress);
            helper.setTo(appUser.getEmail());
            helper.setSubject("Rejestracja w bazie Banzai");
            helper.setText(html, true);
        };

        mailSender.send(preparator);
    }

    public void sendResetLink(AppUser appUser, String token){

        String link = frontendUrl + "/reset-password?token=" + token;

        Context context = new Context();
        context.setVariable("link", link);
        context.setVariable("expiryHours", expiryHours);

        String html = templateEngine.process("email/reset-password", context);

        MimeMessagePreparator preparator = msg -> {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(fromAdress);
            helper.setTo(appUser.getEmail());
            helper.setSubject("Reset hasła (BAnZAi)");
            helper.setText(html, true);
        };

        mailSender.send(preparator);
    }
}
