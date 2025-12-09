package app.ejemplo02.service;



import app.ejemplo02.models.UserEntity;
import app.ejemplo02.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // BCrypt

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity crearUsuario(String username, String passwordPlano, String role) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(passwordPlano));
        user.setRole(role);
        return userRepository.save(user);
    }

    public UserEntity buscarPorUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public List<UserEntity> listarTodos() {
        return userRepository.findAll();
    }
}
