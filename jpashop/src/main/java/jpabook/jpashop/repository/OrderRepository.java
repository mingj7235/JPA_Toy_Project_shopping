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

    /**
     * //data 뻥튀기가 된다. 그렇기 때문에 distinct를 넣어준다.
     * 하지만, db상에서는 distinct가 안된다.
     * jpa상에서만 보일때만 중복제거가 되는 것이다.
     * 즉, jpa 에서 자체적으로 같은 id값인 것을 확인해서 중복을 제거해준다.
     * application 에서 다 가져온 후에 한번 걸러주는 역할을 해주는 것.
     * but, DB 상에서는 똑같이 중복값이 조회가 된다.
     *
     * 성능이 쿼리 1방 ! 으로 해결이 된다.
     *
     * but... 페이징이 불가능하다.
     * 1:n을 fetch join 하는 순간 페이징을 하면 안되는 것을 잊으면 안된다.
     *
     * -> memory에서 paging을 해버린다. 메모리가 엄청 부족해진다.
     * 즉, 1:n 관계에서는 fetch join 을 하여 페이지네이션을 하면안된다.
     */

    public List<Order> findAllWithItem() {
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" + //data 뻥튀기가 된다. 그렇기 때문에 distinct를 넣어준다.
                        " join fetch oi.item i", Order.class
        )
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        )
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
