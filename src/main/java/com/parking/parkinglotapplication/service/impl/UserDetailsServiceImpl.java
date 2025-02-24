package com.parking.parkinglotapplication.service.impl;

import com.parking.parkinglotapplication.entity.User;
import com.parking.parkinglotapplication.entity.UserPrincipal;
import com.parking.parkinglotapplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            System.out.println("User not found");
            throw new UsernameNotFoundException("User not present");
        }

        return new UserPrincipal(user);
    }
}
