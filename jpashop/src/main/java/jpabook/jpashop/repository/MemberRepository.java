package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

@Repository //spring bean으로 등록하기 위해!! component
@RequiredArgsConstructor
public class MemberRepository {

    //@PersistenceContext // jpa의 entitymanager를 injection 해줌 스프링이 해결해줌
    private final EntityManager em;

//    @PersistenceUnit
//    private EntityManagerFactory emf; //factory를 직접 주입받고 싶으면 이렇게 하면된다.

    public void save (Member member) {
        em.persist(member);
    }

    //Member 단건 조회
    public Member findOne (Long id) {
        return em.find(Member.class, id);
    }

    //JPQL 사용하여 전체 조회
    public List<Member> findAll () {
        return em.createQuery("select m from Member m", Member.class) //jpql 이다.entity 객체를 대상으로 한다,. sql은 테이블이 대상이다.
                .getResultList();
    }

    //특정 이름에 의한 검색 -> :name 바인딩을 통해 JPQL 생성
    public List<Member> findByName (String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name) //:name 으로 바인딩된 name값을 setparameter로 해주는 것이다.
                .getResultList();
    }
}














