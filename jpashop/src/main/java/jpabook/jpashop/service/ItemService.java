package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional (readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional //service 클래스에 transactional 이 readonly 이므로
    public Long saveItem (Item item) {
        return itemRepository.save(item);
    }

    @Transactional
    public void updateItem (Long itemId, int price, String name, int stockQuantity) {
        Item findItem = itemRepository.findOne(itemId);
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
    }

    public List<Item> findItems () {
        return itemRepository.findAll();
    }

    public Item findOne (Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
