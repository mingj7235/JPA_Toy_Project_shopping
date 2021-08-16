package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.OrderSimpleQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping ("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {

        //N + 1 문제가 여기서 생긴다.
        /**
         * 쿼리가 최초쿼리 이후에 추가 쿼리가 나가게 되는 것.
         * 성능이 안좋다.
         */
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream().map(o -> new SimpleOrderDto(o)).collect(Collectors.toList());
        return result;
    }

    @GetMapping ("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3 () {
        List<Order> orderList= orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> collect = orderList.stream().map(SimpleOrderDto::new).collect(Collectors.toList());
        return collect;
    }

    @GetMapping ("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4 () {
        List <OrderSimpleQueryDto> orderDtoList = orderRepository.findOrderDtos();
        return orderDtoList;
        //이렇게 DTO를 조회하는 걸 잘 안사용한다.
        //정 사용하려면, 쿼리용 dto를 하나 repository 패키지 안에 만들어서 사용한다.
        //repository는 순수하게 Entity를 조회하는 것으로 사용하는 것이 좋다.
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); //LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //LAZY 초기화
        }
    }


}
