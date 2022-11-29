package com.realbeatz.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    @Modifying
    @Query(value = "insert into friends_of values (?1, ?2);", nativeQuery = true)
    void addFriends(Long userId1, Long userId2);
}
