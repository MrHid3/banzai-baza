package pl.banzaijiujitsu.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.banzaijiujitsu.backend.exception.InvalidLocationException;
import pl.banzaijiujitsu.backend.model.Location;
import pl.banzaijiujitsu.backend.model.Member;
import pl.banzaijiujitsu.backend.model.MemberCategory;
import pl.banzaijiujitsu.backend.model.Payment;
import pl.banzaijiujitsu.backend.repository.MemberRepository;
import pl.banzaijiujitsu.backend.repository.PaymentRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public List<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public List<Member> findByLocation(Location location) {
        return memberRepository.findByLocation(location);
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

    public Collection<Member> findByLocationIsIn(Collection<Location> locations) {
        return memberRepository.findByLocationIsIn(locations);
    }

    public Collection<Member> findByIsActiveTrueAndLocationIsIn(Collection<Location> locations) {
        return memberRepository.findByIsActiveTrueAndLocationIsIn(locations);
    }

    public Collection<Member> findAllByIsActiveTrue() {
        return memberRepository.findAllByIsActiveTrue();
    }

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

    @Transactional
    public Member update(Member member, String name, String surname, String email, Location location, Integer MonthlyFee, String phoneNumber, List<MemberCategory> categories, String comment) {
        member.setName(name);
        member.setSurname(surname);
        member.setEmail(email);
        member.setPhoneNumber(phoneNumber);
        member.setComment(comment);
        member.setLocation(location);
        member.setMonthlyFee(MonthlyFee);
        member.setCategories(categories);
        return this.save(member);
    }
    public List<Member> findOverdue() {
        LocalDate previousMonth = LocalDate.now().minusMonths(1);
        return memberRepository.findActiveMembersWithoutPaymentForMonth(previousMonth.getYear(), previousMonth.getMonth().getValue());
    }

    public List<Member> findByUuidsIn(List<UUID> uuids) {
        return memberRepository.findByUuidIn(uuids);
    }

    public record MemberPayments(Member member, List<Payment> payments) { }
}
