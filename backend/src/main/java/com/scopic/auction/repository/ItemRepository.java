package com.scopic.auction.repository;

import com.scopic.auction.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {

    @Lock(LockModeType.OPTIMISTIC)
    Optional<Item> findById(UUID id);
}
