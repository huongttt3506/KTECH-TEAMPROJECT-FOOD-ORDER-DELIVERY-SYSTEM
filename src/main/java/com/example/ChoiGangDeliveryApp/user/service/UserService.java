package com.example.ChoiGangDeliveryApp.user.service;

import com.example.ChoiGangDeliveryApp.enums.UserRole;
import com.example.ChoiGangDeliveryApp.jwt.JwtTokenUtils;
import com.example.ChoiGangDeliveryApp.jwt.dto.JwtRequestDto;
import com.example.ChoiGangDeliveryApp.jwt.dto.JwtResponseDto;
import com.example.ChoiGangDeliveryApp.security.config.AuthenticationFacade;
import com.example.ChoiGangDeliveryApp.security.config.CustomUserDetails;
import com.example.ChoiGangDeliveryApp.user.dto.DeleteUserDto;
import com.example.ChoiGangDeliveryApp.user.dto.UserCreateDto;
import com.example.ChoiGangDeliveryApp.user.dto.UserDto;
import com.example.ChoiGangDeliveryApp.user.entity.UserDeleteReasonEntity;
import com.example.ChoiGangDeliveryApp.user.entity.UserEntity;
import com.example.ChoiGangDeliveryApp.user.repo.UserDeleteReasonRepo;
import com.example.ChoiGangDeliveryApp.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationFacade authFacade;
    private final UserDeleteReasonRepo reasonRepository;

    // class này chỉ dùng cho signup và login
    //sign up
    @Transactional
    public UserDto createUser(UserCreateDto dto){
        if (!dto.getPassword().equals(dto.getPassCheck()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (userRepository.existsByUsername(dto.getUsername()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);


        return UserDto.fromEntity(userRepository.save(UserEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .role(dto.getRole())
                .build()));
    }




    //log in
    public JwtResponseDto login(JwtRequestDto dto){
        UserEntity userEntity = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(
                dto.getPassword(),
                userEntity.getPassword()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        String jwt = jwtTokenUtils.generateToken(CustomUserDetails.fromEntity(userEntity));
        JwtResponseDto response = new JwtResponseDto();
        response.setToken(jwt);
        return response;
    }

    //gửi yêu cầu delete tới admin, nếu admin oke thì lấy userRepository.delete(user)
    public UserDto deleteUser(DeleteUserDto dto){
        UserEntity user = authFacade.getCurrentUserEntity();

        UserDeleteReasonEntity reason = new UserDeleteReasonEntity();

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password is wrong");
        }
        if (user.getRole().equals(UserRole.ROLE_DELETE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "your delete request was sent");
        }

        user.setRole(UserRole.ROLE_DELETE);
        reason.setReason(dto.getReason());

        reasonRepository.save(reason);
        userRepository.save(user);
        return UserDto.fromEntity(user);
    }




}
