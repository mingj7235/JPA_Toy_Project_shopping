package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "orders") //order는 db에서 예약어 인경우가 있으므로 orders란 테이블을 새로 생성해준다..
@Getter
@Setter
@NoArgsConstructor (access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue
    @Column (name = "order_id")
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "member_id")
    private Member member;

    @OneToMany (mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne (fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn (name = "delivery_id")// 1:1 관계에서는 둘 중 어디든 owner로 FK를 줘도 상관없다. 하지만 엑세스가 많은 곳에 좋은게 좋다.
    private Delivery delivery;

    private LocalDateTime orderDate; //1.8 자바 이후로는 LocalDateTime을 사용하면 @date 관련 어노테이션 안써도 hibernate가 관리해준다.

    @Enumerated (EnumType.STRING)
    private OrderStatus status; //주문 상태 [ORDER, CANCEL]

    //연관관계 편의 메서드 양쪽 세팅을 편의적으로 하기위해

    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem (OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery (Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //== 주문 생성 메서드 ==//
                                                        //... 문법 -> 여러개를 넘길 수 있다.
    public static Order createOrder (Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //== 비지니스 로직 ==//

    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) { //상품 배송이 이미 완료된 것 / validation
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능합니다. ");
        }

        //배송완료가 아니라면, cancel로 변경해줘야한다.
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : this.orderItems) {
            orderItem.cancel(); //orderitem도 cancel을 해줘야한다.
        }
    }

    //== 조회 로직 ==//

    /**
     * 전체 주문 가격 조회
     *
     */
    public int getTotalPrice () {
//       for (OrderItem orderItem : orderItems) {
//           totalPrice += orderItem.getTotalPrice();
//       }
       int totalPrice = orderItems.stream()   //람다로 이렇게 가능 !
               .mapToInt(OrderItem::getTotalPrice)
               .sum();
       return totalPrice;
    }

}
