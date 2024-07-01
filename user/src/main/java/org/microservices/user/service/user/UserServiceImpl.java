package org.microservices.user.service.user;

import jakarta.annotation.PostConstruct;
import org.microservices.user.model.Role;
import org.microservices.user.model.User;
import org.microservices.user.repository.RoleRepository;
import org.microservices.user.repository.UserRepository;
import org.microservices.user.security.DTO.ChangePassRequest;
import org.microservices.user.security.exceptions.NotFoundException;
import org.microservices.user.security.exceptions.UserServiceException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    private void init() {
        Role roleAdmin = Role.builder()
                .name("ROLE_ADMIN")
                .build();
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            saveRole(roleAdmin);
        }
        if (userRepository.findByUsername("developer").isEmpty()) {
            User developer = User
                    .builder()
                    .username("developer")
                    .password("developer")
                    .roles(List.of(roleAdmin))
                    .build();
            save(developer);
        }
    }

    @Override
    public User save(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserServiceException("User Not Found!");
        }
//        if (!Pattern.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,20}$", newPassword)) {
//            throw new UserServiceException(
//                    "Пароль должен содержать от 9 до 20 символов," +
//                            " хотя-бы одну цифру, заглавные и строчные буквы и не содержать пробелов");
//        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User Not Found!"));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User Not Found!"));
    }

    @Override
    public List<org.microservices.user.model.User> findAll() throws AccessDeniedException {
        return userRepository.findAll();
    }

    @Override
    public org.microservices.user.model.User changePassword(Long id, @Valid ChangePassRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found!"));
//        if (Objects.isNull(request.getOldPassword())
//                || Objects.isNull(request.getNewPassword())
//                || Objects.isNull(request.getNewPasswordConfirmation())) {
//            throw new BadRequestException(
//                    messageSource.getMessage("badRequest.fieldsEmpty", null, LocaleContextHolder.getLocale())
//            );
//        }
        if (!BCrypt.checkpw(request.getOldPassword(), user.getPassword())) {
            throw new UserServiceException("Old Password Incorrect!");
        }
        if (!request.getNewPassword().equals(request.getNewPasswordConfirmation())) {
            throw new UserServiceException("New Password Incorrect!");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return userRepository.save(user);
    }

    @Override
    public void saveRole(Role role) {
        roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User Not Found!"));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role Not Found!"));
        user.getRoles().add(role);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserServiceException("User Not Found!"));
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
