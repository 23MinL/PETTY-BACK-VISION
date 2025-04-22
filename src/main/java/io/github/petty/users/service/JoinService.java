package io.github.petty.users.service;

import io.github.petty.users.Role;
import io.github.petty.users.dto.JoinDTO;
import io.github.petty.users.entity.Users;
import io.github.petty.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UsersRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public boolean joinProcess(JoinDTO joinDTO) {
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();
        String displayName = joinDTO.getDisplayName();
        String phone = joinDTO.getPhone();
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        Boolean exists = userRepository.existsByUsername(username);
        if (exists) {
            return false;
        }

        Users users = new Users();
        users.setUsername(username);
        users.setPassword(encodedPassword);
        users.setDisplayName(displayName);
        users.setPhone(phone);
        users.setRole(Role.ROLE_USER.name());
        userRepository.save(users);

        return true;
    }
}
