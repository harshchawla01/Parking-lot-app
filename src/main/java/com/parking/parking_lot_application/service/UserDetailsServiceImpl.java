package com.parking.parking_lot_application.service;

import com.parking.parking_lot_application.entity.User;
import com.parking.parking_lot_application.entity.UserPrincipal;
import com.parking.parking_lot_application.repository.UserRepository;
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
            System.out.println("Loda lele");
            throw new UsernameNotFoundException("User not present");
        }

        return new UserPrincipal(user);
    }
}
