package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {
    /**
     * 값 타입은 절대 값이 변경되면 안된다. 즉 setter는 절대 제공 안된다 !
     *
     */
    private String city;
    private String street;
    private String zipcode;

    //함부로 new 로 생성하지 못하게 막음
    protected Address () {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
