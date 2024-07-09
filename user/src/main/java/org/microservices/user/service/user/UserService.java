package org.microservices.user.service.user;

import org.microservices.user.model.Role;
import org.microservices.user.model.User;
import org.microservices.user.security.DTO.ChangePassRequest;

import javax.validation.Valid;
import java.util.List;

public interface UserService {
    boolean existsById(Long id);
    void saveRole(Role role);
    void addRoleToUser(Long userId, String roleName);
    User save(User user);
    User update(Long id, User user);
    User getById(Long id);
    User findByUsername(String username);
    List<User> getAll();
    void deleteById(Long id);
    User changePassword(Long id, @Valid ChangePassRequest request);
}
