package pl.banzaijiujitsu.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.banzaijiujitsu.backend.model.Localization;
import pl.banzaijiujitsu.backend.model.Member;
import pl.banzaijiujitsu.backend.repository.MemberRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public List<Member> findByEmail(String email) {
        return  memberRepository.findByEmail(email);
    }

    public List<Member> findByLocalization(Localization localization) {
        return  memberRepository.findByLocalization(localization);
    }

    public Optional<Member> findByUuid(UUID uuid) {
        return memberRepository.findByUuid(uuid);
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public Member save(Member member) {
        return memberRepository.save(member);
    }
}
