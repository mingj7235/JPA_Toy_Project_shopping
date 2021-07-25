package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order (Long memberId, Long itemId, int count) {
        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order); //cascade all 로 인해서 delivery entity와 orderitem entity가 order만 persist해도 모두 영속화된다. 매우 중요한 포인트 !

        return order.getId();
    }

    //취소

    /**
     *
     */
    @Transactional
    public void cancelOrder (Long orderId) {
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소 -> order entity에 이미 비지니스 로직을 만들어 놨다.
        order.cancel();
        //jpa를 활용하면, entity안에서 데이터만 바꾸면, jpa가 알아서 더티체킹을 통해 변경 내역을 db에 업데이트 쿼리를 날려준다.
        //그렇기 때문에 business로직을 entity에서 해주고, service계층에서는 이렇게 간단하게 써주는것이 jpa를 잘쓰는 것이다.
        //보통 jpa를 사용하지 않으면 business 로직에 mapper, sql날리는 로직등을 구현해야하기때문에 그것이 일반적이었지만
        //jpa를 제대로 사용한다면, entity 내에서 데이터 변경에 대한 비지니스 로직을 구현하는 것이 더 직관적이다.
    }

    //검색

//    public List<Order> findOrders (OrderSearch orderSearch) {
//        return orderRepository.findAll (orderSearch);
//    }
}
