package com.smart.smartcontact.config;

import com.smart.smartcontact.dao.UserRepository;
import com.smart.smartcontact.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    public UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user= userRepository.getUserByUserName(username);
       if(user==null){
           throw  new UsernameNotFoundException("could not found user");
       }
       CumtomUserDetails cumtomUserDetails=new CumtomUserDetails(user);
        return cumtomUserDetails;
    }
}
