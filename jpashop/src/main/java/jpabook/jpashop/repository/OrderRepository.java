package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save (Order order ){
        em.persist(order);
    }

    public Order findOne (Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAllByString (OrderSearch orderSearch) {

        //동적 쿼리에 대한 고민... JPA // condition 을 분기 해야한다. 밑의방법은 개복잡한 방법이다.
        //이런 방법은 실수가 많다. 동적쿼리 생성을 문자열로 바꿔서 jpql을 사용하면 힘들다.
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
            //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class) .setMaxResults(1000); //최대 1000건

        //setParameter를 해줘야한다.
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    /**
     * JPA Criteria로 해결하는 방법
     * 그런데 이것도 실무에서 사용하라고 만든게 아니다.... 허허
     * 유지보수성이 거의 제로다. 어떤 쿼리가 나올것 같다는 것이 한눈에 보이지 않는다.
     * 이것이 JPA의 표준 스펙이지만 안쓰는것이 좋다.
     */

    /**
     * QueryDSL이 이런 고민 끝에 나온 라이브러리다.
     * queryDSL을 사용하면 동적 쿼리를 사용하기가 너무 쉽다.
     * 직관적이고 사용하기가 쉽다. 동적쿼리가 강력하다.
     */
    public List<Order> findAllByCriteria (OrderSearch orderSearch) { //criteria는 jpa가 표준으로 제공해주는 기준이다.
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

    /**
     * fetch join을 사용하여 모든 정보를 다 가지고 오기
     */
    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();
    }

    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                "select o from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
}
