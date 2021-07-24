package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
}
