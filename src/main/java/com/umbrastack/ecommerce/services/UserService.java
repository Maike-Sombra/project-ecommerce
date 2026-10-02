package com.umbrastack.ecommerce.services;


import com.umbrastack.ecommerce.domain.User;
import com.umbrastack.ecommerce.dto.UserResponseDTO;
import com.umbrastack.ecommerce.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserResponseDTO> findAll(){
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

}
