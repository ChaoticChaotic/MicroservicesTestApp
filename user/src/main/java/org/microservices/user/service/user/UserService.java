package org.microservices.user.service.user;

import org.microservices.user.model.Role;
import org.microservices.user.model.User;
import org.microservices.user.security.DTO.ChangePassRequest;

import javax.validation.Valid;
import java.util.List;

public interface UserService {
    void saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    User save(User user);
    User findById(Long id);
    User findByUsername(String email);
    List<User> findAll();
    User changePassword(Long id, @Valid ChangePassRequest request);
}
