package com.parking.parking_lot_application.repository;

import com.parking.parking_lot_application.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> { // <Table, Primary key>
    User findByEmail(String email);
}
