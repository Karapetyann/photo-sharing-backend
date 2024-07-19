package com.example.photosharingbackend.security;


import com.example.photosharingbackend.entity.User;
import lombok.Getter;
import org.springframework.security.core.authority.AuthorityUtils;

@Getter
public class SpringUser extends org.springframework.security.core.userdetails.User {
    User user;


    public SpringUser(User user) {
        super(user.getUsername(), user.getPassword(), true, true, true, true, AuthorityUtils.createAuthorityList(user.getRole().name()));
        this.user = user;
    }
}
