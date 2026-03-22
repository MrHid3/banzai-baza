package pl.banzaijiujitsu.backend.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class MemberRequest {
    private String email;
    private String phoneNumber;
    private String name;
    private String surname;
    private Long location_id;
    private Integer monthlyFee;
}
