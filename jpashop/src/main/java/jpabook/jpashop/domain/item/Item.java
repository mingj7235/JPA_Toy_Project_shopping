package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance (strategy = InheritanceType.SINGLE_TABLE) //상속 전략 한 테이블에 다 때려박는 것임 (테이블 계층에서)
@DiscriminatorColumn (name = "dtype")
@Getter
@Setter
public abstract class Item {
    // 상속관계 매핑을 해야한다.
    @Id @GeneratedValue
    @Column (name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany (mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    // ==비지니스 로직 ==//
    /**
     * 도메인 주도 설계
     * 원래는 service에서 로직을 만드는데,
     * 객체지향적으로 생각하면, 변수가 있는 곳에 비지니스로직을 만드는 것이 가장 좋다. 응집력이 있다.
     * stockquantity, 즉 필드의 변화 로직은 entity에 직접 넣는 것이좋다.
     */

    /**
     *
     * stock 증가
     */
    public void addStock (int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     *
     * stock 감소
     */
    public void removeStock (int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
