package com.umbrastack.ecommerce.services;


import com.umbrastack.ecommerce.domain.User;
import com.umbrastack.ecommerce.dto.UserInsertDTO;
import com.umbrastack.ecommerce.dto.UserResponseDTO;
import com.umbrastack.ecommerce.dto.UserUpdateDTO;
import com.umbrastack.ecommerce.repositories.UserRepository;
import com.umbrastack.ecommerce.services.exceptions.DatabaseException;
import com.umbrastack.ecommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findAll(Pageable pageable){
        return userRepository.findAll(pageable).map(UserResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        return new UserResponseDTO(user);
    }
    @Transactional
    public UserResponseDTO insert(UserInsertDTO dto){
        User user = new User(
                null,
                dto.name(),
                dto.email(),
                dto.phone(),
                dto.password()
        );
        user = userRepository.save(user);
        return new UserResponseDTO(user);
    }
    @Transactional
    public void delete(Long id){
        if (!userRepository.existsById(id)){
            throw new ResourceNotFoundException(id);
        }

        userRepository.deleteById(id);
    }
    @Transactional
    public UserResponseDTO update(Long id, @Valid UserUpdateDTO dto){
        try {// o proxy é criado aqui
            User user = userRepository.getReferenceById(id);
            //Dirty checking: hibernate atualiza o banco no fim da transação
            user.setName(dto.name());
            user.setEmail(dto.email());
            user.setPhone(dto.phone());

            return new UserResponseDTO(user);


        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }
}
