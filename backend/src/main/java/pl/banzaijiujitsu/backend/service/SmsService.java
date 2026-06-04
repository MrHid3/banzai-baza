package pl.banzaijiujitsu.backend.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.banzaijiujitsu.backend.model.Member;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SmsService {

    private final MemberService memberService;

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String fromPhoneNumber;

    @Value("${app.reminder-content}")
    private String reminderContent;

    @PostConstruct
    public void init(){
        Twilio.init(accountSid, authToken);
    }

    public SmsService(MemberService memberService) {
        this.memberService = memberService;
    }

    public void sendSms(String toPhoneNumber, String messageBody) {
        try{
            Message message = Message.creator(
                            new PhoneNumber(convertToPolish(toPhoneNumber)),
                            new PhoneNumber("KLUB BANZAI"),
                            messageBody
                    )
                    .create();
        }catch (Exception ignored){}
    }

    public void sendSmsToMany(List<Member> members, String messageBody){
        Set<String> phoneNumbers = members.stream()
                .map(Member::getPhoneNumber)
                .collect(Collectors.toSet());
        for(String number : phoneNumbers){
            try{
                Message.creator(
                                new PhoneNumber(convertToPolish(number)),
                                new PhoneNumber("KLUB BANZAI"),
                                messageBody
                        )
                        .create();
            }catch(Exception ignored){}
        }
    }

    private String convertToPolish(String phone){
        if (phone == null || phone.isBlank()) return phone;
        return phone.startsWith("+") ? phone : "+48" + phone;
    }

    @Scheduled(cron = "0 30 16 15 * *")
    @Scheduled(cron = "0 30 16 18 * *")
    @Scheduled(cron = "0 30 16 21 * *")
    @Scheduled(cron = "0 30 16 24 * *")
    @Scheduled(cron = "0 30 16 27 * *")
    @Scheduled(cron = "0 30 16 30 * *")
    public void sendToOverdue(){
        List<Member> overdue = memberService.findOverdue();
        sendSmsToMany(overdue, reminderContent);
    }

}
