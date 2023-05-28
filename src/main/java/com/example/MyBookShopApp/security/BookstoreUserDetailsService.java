package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.repo.UserRepository;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BookstoreUserDetailsService implements UserDetailsService {

    private boolean flag = true;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findUserEntityByEmail(email);

        if (user != null) {
            return new BookstoreUserDetails(user);
        }
            else {
                throw new UsernameNotFoundException("user has`t found");
            }
    }

    public void setHandleTokenValid(boolean flag){
        this.flag = flag;
    }

    public boolean getHandleTokenValid(){
        return flag;
    }

}
