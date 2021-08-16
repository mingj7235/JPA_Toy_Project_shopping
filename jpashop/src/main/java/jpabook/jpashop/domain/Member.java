package jpabook.jpashop.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id @GeneratedValue
    @Column (name = "member_id")
    private Long id;

    private String name;

    @Embedded //embeddable 을 한 클래스를 가져온다.
    private Address address;

    @OneToMany (mappedBy = "member") //owner를 정하기 위해 ! 주인은 orders 이므로, 그걸 기준으로 mapped 되어야 하므로 지정 ! 연관관계의 거울 !
    private List<Order> orders = new ArrayList<>(); //collection 은 필드에서 바로 초기화 하는것이 안전하다 !

}
