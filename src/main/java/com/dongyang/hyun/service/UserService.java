package com.dongyang.hyun.service;
import com.dongyang.hyun.dto.UserDto;
import com.dongyang.hyun.entity.User;
import com.dongyang.hyun.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User signup(UserDto dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            return null;
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setNickname(dto.getNickname());
        return userRepository.save(user);
    }

    public User login(UserDto dto) {
        return userRepository.findByUsername(dto.getUsername())
                .filter(u -> u.getPassword().equals(dto.getPassword()))
                .orElse(null);
    }

    public User updateProfile(Long userId, UserDto dto) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;
        // username은 보통 변경 불가, 필요시 아래 라인 활성화
        // if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) user.setPassword(dto.getPassword());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        return userRepository.save(user);
    }

    public List<User> searchByUsername(String keyword, Long excludeId) {
        return userRepository.findByUsernameContainingAndIdNot(keyword, excludeId);
    }
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

}
