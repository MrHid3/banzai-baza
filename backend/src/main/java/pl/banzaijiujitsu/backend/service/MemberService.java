package pl.banzaijiujitsu.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import pl.banzaijiujitsu.backend.model.Location;
import pl.banzaijiujitsu.backend.model.Member;
import pl.banzaijiujitsu.backend.model.Payment;
import pl.banzaijiujitsu.backend.model.YearMonthDateAttributeConverter;
import pl.banzaijiujitsu.backend.repository.MemberRepository;
import pl.banzaijiujitsu.backend.repository.PaymentRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PaymentRepository paymentRepository;

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

//    public List<MemberPayments> getLastThreeMonthsGroupedByMember(Collection<Location> locations) {
//        LocalDate threeMonthsAgo = LocalDate.now().withDayOfMonth(1).minusMonths(3);
//
//        List<Member> members = memberRepository
//                .findActiveMembersInLocationsWithRecentPayments(locations, threeMonthsAgo);
//
//        return members.stream()
//                .map(m -> new MemberPayments(m, m.getPayments()))
//                .toList();
//    }

    public List<MemberPayments> getLastThreeMonthsGroupedByMember(Collection<Location> locations) {
        LocalDate threeMonthsAgo = LocalDate.now().withDayOfMonth(1).minusMonths(3);

        List<Member> members = memberRepository.findActiveByLocations(locations);

        if (members.isEmpty()) {
            return List.of();
        }

        List<Payment> payments = paymentRepository.findRecentByMembers(members, threeMonthsAgo);

        Map<UUID, List<Payment>> paymentsByMember = payments.stream()
                .collect(Collectors.groupingBy(p -> p.getPayer().getUuid()));

        return members.stream()
                .map(m -> new MemberPayments(
                        m,
                        paymentsByMember.getOrDefault(m.getUuid(), List.of())
                ))
                .toList();
    }

    public record MemberPayments(Member member, List<Payment> payments) {}
}
