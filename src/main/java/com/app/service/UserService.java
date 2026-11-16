
package com.app.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dao.UserDAO;
import com.app.dto.UserResposeDTO;
import com.app.entity.Role;
import com.app.entity.User;
import com.app.enumeration.Status;
import com.app.repository.RoleRepository;
import com.app.repository.UserRepository;

@Service("userService")
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUserName(username)
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "Invalid username or password")
                );

        if (user.getStatus() != Status.ACTIVE) {
            throw new UsernameNotFoundException(
                "User account is inactive");
        }

        Role role = roleRepository.findById(user.getUserRoleId())
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "User role not found")
                );

        if (role.getRoleName() == null ||
                role.getStatus() != Status.ACTIVE) {
            throw new UsernameNotFoundException(
                "User role is inactive");
        }

        String authority = "ROLE_" + role.getRoleName().name();

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                Collections.singletonList(
                    new SimpleGrantedAuthority(authority)
                )
        );
    }

    public Optional<User> getByPhoneNo(String phoneNo) {
        return userRepository.findByPhoneNo(phoneNo);
    }

    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public Optional<User> findByUserEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public void saveOrUpdate(User user) {
        userRepository.save(user);
    }

    public List<UserResposeDTO> getAll() {
        return userDAO.findAll();
    }

    public Optional<User> findByAddress(String address) {
        return userRepository.findByAddress(address);
    }

    public Object getById(UUID id) {
        return userDAO.findById(id);
    }

    public void update(User user) {
        userRepository.save(user);
    }

    public Optional<User> findByPassword(
            String encryptedPassword,
            String userName) {
        return userRepository.findByPasswordAndUserName(
            encryptedPassword, userName);
    }

    public void delete(UUID id) {
        userRepository.deleteById(id);
    }

    public User findByName(String userName) {
        return userDAO.findByUserName(userName);
    }

    public User findByUser(String userName, UUID id) {
        return userDAO.findByUser(userName, id);
    }

    public User findByUserPhone(String phoneNo, UUID id) {
        return userDAO.findByUserPhone(phoneNo, id);
    }

    public User findByPhone(String phoneNo) {
        return userDAO.findByPhone(phoneNo);
    }

    public User getUserByUserNameOrEmail(String value) {
        return userDAO.getUserByUserNameOrEmail(value);
    }

    public User findByUserEmail(String email, UUID id) {
        return userDAO.findByUserEmail(email, id);
    }
}
