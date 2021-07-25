package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class) //스프링과 엮어서 ! (junit 4 버전에서는 필요)
@SpringBootTest
@Transactional //트랜잭션 걸고 테스트하고 테스트 끝나면 rollback 시킴
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
//    @Autowired
//    EntityManager em;

    @Test
    //@Rollback (false) //기본적으로 rollback이 되기때문에 insert 쿼리가 나가지않는다. insert쿼리를 보기 위해서는 rollback 을 false로 해줘야한다.
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);

        //then
        //em.flush(); //테스트할때 rollback 안시키고, flush를 통해 db에 쿼리를 날릴 수있다.
        assertEquals(member, memberRepository.findOne(savedId));
    }
    @Test (expected = IllegalStateException.class) //깔끔하게 이렇게 쓸 수있다 밑에 try 말고 !!
    public void 중복회원가입() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when

        memberService.join(member1);
        memberService.join(member2);
//        try {
//            memberService.join(member2);//예외가 발생해야한다 !
//
//        } catch (IllegalStateException e) {
//            return;
//        }
        //then

        fail("예외가 발생해야 한다.");

    }

}