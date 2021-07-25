package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public Long save(Item item) {
        if (item.getId() == null) { //신규 둥록시
            em.persist(item);
        } else { //db에 등록되어 있음
            em.merge(item); //업데이트 비슷한 것 (후에 설명)
        }
        return item.getId();
    }

    public Item findOne (Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll () {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
