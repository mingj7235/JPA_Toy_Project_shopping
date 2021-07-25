package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm (Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping ("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) { //MemberForm에 name을 @notempty 한것을 validation 해주는것이다.
                                    //BindingResult : valid 뒤에 binding result가 있으면 에러가 여기에 담기게된다.

        if (result.hasErrors()) {
            return "members/createMemberForm"; //즉, 에러가 있으면 여기로 리턴할거야 라고 해주는 것임... 오오..개쩐다.
        }
        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }
}
