//package com.parking.parkinglotapplication.repository;
//
//import com.parking.parkinglotapplication.entity.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface UserRepository extends JpaRepository<User, Integer> { // <Table, Primary key>
//    User findByEmail(String email);
//}

package com.parking.parkinglotapplication.repository;

import com.parking.parkinglotapplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}