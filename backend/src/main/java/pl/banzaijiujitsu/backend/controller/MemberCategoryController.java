package pl.banzaijiujitsu.backend.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.banzaijiujitsu.backend.exception.MemberCategoryException;
import pl.banzaijiujitsu.backend.model.MemberCategory;
import pl.banzaijiujitsu.backend.service.MemberCategoryService;
import pl.banzaijiujitsu.backend.service.SmsService;

import java.util.List;

@RestController
@RequestMapping("/api/memberCategory")
public class MemberCategoryController {

    private final MemberCategoryService memberCategoryService;

    private final SmsService smsService;

    @Autowired
    public MemberCategoryController(MemberCategoryService memberCategoryService, SmsService smsService) {
        this.memberCategoryService = memberCategoryService;
        this.smsService = smsService;
    }

    @GetMapping
    public ResponseEntity<List<MemberCategory>> getCategories() {
        return ResponseEntity.ok(memberCategoryService.findAll());
    }

    @PostMapping
    public ResponseEntity<MemberCategory> save(@RequestBody CreateMemberCategoryRequest req) {
        memberCategoryService.create(req.name, req.shortname);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MemberCategory> delete(@PathVariable Long id) {
        memberCategoryService.delete(
                memberCategoryService.findById(id).
                        orElseThrow(() ->
                                new MemberCategoryException("MEMBER_CATEGORY_NOT_FOUND")
                        )
        );
        return ResponseEntity.ok().build();
    }

    public record CreateMemberCategoryRequest(
            @NotNull String name,
            @NotNull String shortname
    ) {
    }
}
