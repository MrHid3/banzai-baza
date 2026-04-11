package pl.banzaijiujitsu.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.banzaijiujitsu.backend.model.Location;
import pl.banzaijiujitsu.backend.model.Member;
import pl.banzaijiujitsu.backend.model.Payment;
import pl.banzaijiujitsu.backend.repository.MemberRepository;

import java.time.YearMonth;
import java.util.Collection;
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

    public List<Member> findByLocation(Location location) {
        return  memberRepository.findByLocation(location);
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

    public Collection<Member> findByLocationIsIn(Collection<Location> locations){
        return memberRepository.findByLocationIsIn(locations);
    }

    public Collection<Member> findByIsActiveTrueAndLocationIsIn(Collection<Location> locations){
        return memberRepository.findByIsActiveTrueAndLocationIsIn(locations);
    }

    public Collection<Member> findAllByIsActiveTrue(){
        return memberRepository.findAllByIsActiveTrue();
    }

    public List<MemberPayments> getLastThreeMonthsGroupedByMember(Collection<Location> locations) {
        YearMonth threeMonthsAgo = YearMonth.now().minusMonths(2);

        List<Member> members = memberRepository
                .findActiveMembersInLocationsWithRecentPayments(locations, threeMonthsAgo);

        return members.stream()
                .map(m -> new MemberPayments(m, m.getPayments()))
                .toList();
    }

    public record MemberPayments(Member member, List<Payment> payments) {}
}
