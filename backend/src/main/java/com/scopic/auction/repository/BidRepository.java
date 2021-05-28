package com.scopic.auction.repository;

import com.scopic.auction.domain.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BidRepository extends JpaRepository<Bid, UUID> {
}
