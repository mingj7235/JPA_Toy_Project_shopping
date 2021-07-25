package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor (access = AccessLevel.PROTECTED)
public class OrderItem {

    //protected OrderItem() { } //private으로 두어서 생성자 생성을 막아준다. -> 로직 분산화를 줄이기 위 (jpa는 protected까지 허용함)

    @Id @GeneratedValue
    @Column (name = "order_item_id")
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "item_id")
    private Item item;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "order_id")
    private Order order;

    private int orderPrice; //주문 당시 강격
    private int count; //주문 수량

    //==생성 메서드 ==//

    public static OrderItem createOrderItem (Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count); //주문한만큼 stock에서 count를 ㄱ빼줘야한다.
        return orderItem;
    }

    //== 비지니스 로직 ==//
    public void cancel() {
        getItem().addStock(count); //주문수량을 다시 add해줘야 수량이 원복이 된다.
    }

    //== 조회 로직 ==//

    /**
     *
     * 주문 상품 전체 가격 조회
     */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
