package com.scopic.auction.repository;

import com.scopic.auction.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
