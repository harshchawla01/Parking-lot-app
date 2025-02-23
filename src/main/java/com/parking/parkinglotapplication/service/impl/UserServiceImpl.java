package com.parking.parkinglotapplication.service.impl;

import com.parking.parkinglotapplication.entity.Role;
import com.parking.parkinglotapplication.entity.User;
import com.parking.parkinglotapplication.repository.UserRepository;
import com.parking.parkinglotapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // If no role is set, default to USER
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        return userRepository.save(user);
    }
}