package pl.banzaijiujitsu.backend.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.banzaijiujitsu.backend.model.MemberCategory;
import pl.banzaijiujitsu.backend.repository.MemberCategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MemberCategoryService {

    private final MemberCategoryRepository memberCategoryRepository;

    @Autowired
    public MemberCategoryService(MemberCategoryRepository memberCategoryRepository) {
        this.memberCategoryRepository = memberCategoryRepository;
    }

    public Optional<MemberCategory> findById(Long id) {
        return memberCategoryRepository.findById(id);
    }

    public Optional<MemberCategory> findByName(String name) {
        return memberCategoryRepository.findByName(name);
    }

    public Optional<MemberCategory> findByShortname(String shortname) {
        return memberCategoryRepository.findByShortname(shortname);
    }

    public List<MemberCategory> findAll() {
        return memberCategoryRepository.findAll();
    }

    @Transactional
    public void save(MemberCategory memberCategory) {
        this.memberCategoryRepository.save(memberCategory);
    }

    @Transactional
    public MemberCategory create(String name, String shortname) {
        MemberCategory memberCategory = new MemberCategory();
        memberCategory.setName(name);
        memberCategory.setShortname(shortname);
        this.save(memberCategory);
        return memberCategory;
    }

    @Transactional
    public void delete(MemberCategory memberCategory) {
        this.memberCategoryRepository.delete(memberCategory);
    }

    public List<MemberCategory> findAllByIds(List<Long> ids){
        return memberCategoryRepository.findAllByIdIsIn(ids);
    }
}
