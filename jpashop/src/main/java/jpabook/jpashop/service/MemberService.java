package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional (readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    
    private final MemberRepository memberRepository;

    //회원 가입
    @Transactional //따로 써주는 것 !! 최적화를 위해서 !!
    public Long join (Member member) {
        validateDuplicateMember(member); //중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    //중복회원 검증
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");

            //DB상에 컬럼에 unique 제약조건을 걸어주는 것이 안전하다. 최후의 방패
        }
    }
    //회원 전체 조회
    //@Transactional (readOnly = true) //좀더 최적화가된다. 즉, 읽기만 하는 로직에서는 이게 좀 더 최적화 시키는 것이다.
    //service 클래스에 readonly = true 옵션을 쓰고 여기선 빼주고, 조회가 아닌 로직에서는 다시 어노테이션을 써준다 -> ex) join 메소드
    public List<Member> findMembers () {
        return memberRepository.findAll();
    }

    public Member findOne (Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
