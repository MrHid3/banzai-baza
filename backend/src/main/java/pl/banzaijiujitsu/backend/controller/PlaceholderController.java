package pl.banzaijiujitsu.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.banzaijiujitsu.backend.service.MemberService;
import pl.banzaijiujitsu.backend.service.SmsService;

@RestController
public class PlaceholderController {

    @Autowired
    private SmsService smsService;

    @GetMapping("/notsecure")
    public String notsecure() {
        return "not secure";
    }

    @GetMapping("/secure")
    public String secure() {
//        smsService.sendToOverdue();
        return "this shit secure;";
    }
}
