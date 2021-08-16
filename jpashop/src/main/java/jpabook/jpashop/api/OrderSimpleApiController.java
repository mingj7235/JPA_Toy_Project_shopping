package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * xToOne 관계를 실습하고자 함. (Manytoone, onetoone)
 * Order 조회
 * Order에서 Member와 연관 (many to one)
 * Order와 Deliver와 연관 (one to one)
 * 즉, 컬렉션관계가 아닌 것.
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    @GetMapping ("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        return all;
        /**
         * 무한 루프에 빠지게 된다.
         * jackson 라이브러리 입장에서는 json을 만들때 무한루프를 돌리게 된다.
         * 양방향 연관관계에서 문제가 생기는 것임
         * 별 방법을 다해서 해결을 할 수 는 있다. @jsonIgnore라던가, Hibernate5Module을 받아서 조정하고 ... 거지같음
         * 하지만 이것보다는 DTO로 변환하여 하는 것이 훨씬훨씬 좋은 방법이다. 낭비가 많다.
         */
    }


}
