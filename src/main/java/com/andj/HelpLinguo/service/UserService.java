package com.andj.HelpLinguo.service;

import com.andj.HelpLinguo.model.User;
import com.andj.HelpLinguo.model.Role;
import com.andj.HelpLinguo.model.UserInfo;
import com.andj.HelpLinguo.repository.UserInfoRepository;
import com.andj.HelpLinguo.repository.UserRepository;
import com.andj.HelpLinguo.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

@Service("userService")
public class UserService {

    private UserInfoRepository userInfoRepository;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       UserInfoRepository userInfoRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userInfoRepository = userInfoRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(1);
        Role userRole = roleRepository.findByRole("ADMIN");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public UserInfo saveUserInfo(UserInfo userInfo) {
        return userInfoRepository.save(userInfo);
    }

    public void deleteUserInfo(UserInfo userInfo) {
        userInfoRepository.delete(userInfo);
    }

    public Optional<User> findById(int user_id) { return userRepository.findById(user_id); }
}
