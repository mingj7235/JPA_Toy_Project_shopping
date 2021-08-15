package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 등록 v1 : 요청 값으로 Member 엔티티를 직접 받는다.
     * 문제점
     * 1. 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
     * 2. 엔티티에 API검증을 위한 로직이 들어간다. (@NotEmpty등)
     * 3. 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위한 모든 요청 요구사항을 담기는 어렵다.
     * 4. 엔티티가 변경되면 API 스펙이 변한다.
     *
     * 결론
     * - API 요청 스펙에 맟추어 별도의 DTO를 파라미터로 받는다.
     */
    @PostMapping ("/api/v1/members")
    public CreateMemberResponse saveMemberV1 (@RequestBody @Valid Member member) {
        //@RequestBody : json으로 온것을 Member로 담아준다.
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping ("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    } //실무에서는 절대로 API에서 entity를 받고 내보내지 않는다. DTO로 작업한다.

    @Data
    static class CreateMemberRequest { //DTO
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;
        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
