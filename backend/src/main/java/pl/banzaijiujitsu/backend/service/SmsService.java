package pl.banzaijiujitsu.backend.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
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

    public SmsService(MemberService memberService) {
        this.memberService = memberService;
    }

    public void sendSms(String toPhoneNumber, String messageBody) {
        Twilio.init(accountSid, authToken);
        try{
            Message message = Message.creator(
                            new PhoneNumber(toPhoneNumber),
                            new PhoneNumber(fromPhoneNumber),
                            messageBody
                    )
                    .create();
        }catch (Exception ignored){}
    }

    public void sendSmsToMany(List<Member> members, String messageBody){

        Twilio.init(accountSid, authToken);
        Set<String> phoneNumbers = members.stream()
                .map(Member::getPhoneNumber)
                .collect(Collectors.toSet());
        for(String number : phoneNumbers){
            try{
                Message.creator(
                                new PhoneNumber(number),
                                new PhoneNumber(fromPhoneNumber),
                                messageBody
                        )
                        .create();
            }catch(Exception ignored){}
        }
    }

    @Scheduled(cron = "0 30 16 11 * *")
    public void sendToOverdue(){
        List<Member> overdue = memberService.findOverdue();
        sendSmsToMany(overdue, reminderContent);
    }

}
