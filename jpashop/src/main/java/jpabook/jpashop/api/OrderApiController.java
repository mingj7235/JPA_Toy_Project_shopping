package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private  final OrderRepository orderRepository;
    private  final OrderQueryRepository orderQueryRepository;

    /**
     * v1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록 , Lazy = null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore로 처리
     *
     * -> 엔티티가 변하면 API 스펙이 변한다.
     * -> 트랜젝션 안에서 지연 로딩 필요
     * -> 양방향 연관관계의 문제
     *
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordrsV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems(); // 강제 초기화 -> lazy인 애들을 한번 터치해주기 위해서
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    /**
     * v2 : dto로 반환 (fetch join 사용 x)
     *
     * - 지연 로딩으로 많은 sql이 실행된다.
     *
     *
     */
    @GetMapping ("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> result = orders.stream()
                .map(OrderDto::new)
                .collect(toList());
        return result;
    }

    /**
     *
     */

    @GetMapping ("/api/v3/orders") //데이터 뻥튀기가 된다. 1:n 관계에서 n 만큼 조인시 뻥튀기가된다.
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
        return result;
    }

    /**
     * v3.1 페치조인 + yml에 hibernate.default_batch_fetch_size 글로벌 세팅
     * -> 페이징이 가능해진다. 성능도 최적화됨
     * v3 와 결과는 같으나, 페이징이 가능해진다. 성능도 페치조인을 한만큼 줄어들며, hibernate 설정으로 인해 1+n 뻥튀기도 없어져서 중복도 없다.
     * 페이징을 쓰기위해서는 이방법뿐이다.
     */
    @GetMapping ("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam (value = "offset", defaultValue = "0") int offset,
                                        @RequestParam (value = "limit", defaultValue = "100") int limit) {

        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit); //*toOne 관계는 모두 fetch join으로 가져옴
                                //in query 로 댕겨온다.

        List<OrderDto> result = orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
        return result;
    }

    /**
     * N+1 문제가 생긴다.
     */
    @GetMapping ("/api/v4/orders")
    public List<OrderQueryDto> ordersV4 () {
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping ("/api/v5/orders")
    public List<OrderQueryDto> ordersV5 () {
        return orderQueryRepository.findAllByDto_optimization();
    }

    @GetMapping ("/api/v6/orders")
    public List<OrderFlatDto> ordersV6 () {
        return orderQueryRepository.findAllByDto_flat();
    }

    @Data
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                .map(OrderItemDto::new)
                .collect(toList());
        }
    }

    @Getter
    static class OrderItemDto {
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }

}
