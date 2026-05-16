package pl.banzaijiujitsu.backend.service;

import com.resend.Resend;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pl.banzaijiujitsu.backend.model.AppUser;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final TemplateEngine templateEngine;

    @Value("${resend.api-key}")
    private String apiKey;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.magic-link.expiry-hours}")
    private Integer expiryHours;

    private Resend getResendClient() {
        return new Resend(apiKey);
    }

    public void sendInviteLink(AppUser appUser, String token) {

        String link = frontendUrl + "/set-password?token=" + token;

        Context context = new Context();
        context.setVariable("link", link);
        context.setVariable("expiryHours", expiryHours);

        String html = templateEngine.process("email/registration", context);

        SendEmailRequest request = SendEmailRequest.builder()
                .from("Banzai JiuJitsu <noreply@banzaijiujitsu.pl>")  // Replace with your verified domain
                .to(appUser.getEmail())
                .subject("Rejestracja w bazie Banzai")
                .html(html)
                .build();

        Resend resend = getResendClient();
        SendEmailResponse response = resend.emails().send(request);
        // Optionally log response.getId()
    }

    public void sendResetLink(AppUser appUser, String token) {

        String link = frontendUrl + "/reset-password?token=" + token;

        Context context = new Context();
        context.setVariable("link", link);
        context.setVariable("expiryHours", expiryHours);

        String html = templateEngine.process("email/reset-password", context);

        SendEmailRequest request = SendEmailRequest.builder()
                .from("Banzai JiuJitsu <noreply@banzaijiujitsu.pl>")  // Replace with your verified domain
                .to(appUser.getEmail())
                .subject("Reset hasła (BAnZAi)")
                .html(html)
                .build();

        Resend resend = getResendClient();
        SendEmailResponse response = resend.emails().send(request);
        // Optionally log response.getId()
    }
}
