package com.granotec.inventory_api.security;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.role.Role;
import com.granotec.inventory_api.role.RoleRepository;
import com.granotec.inventory_api.user.User;
import com.granotec.inventory_api.user.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@DependsOn("permissionsInitializer")
public class UserInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    @PostConstruct
    public void init(){
        String email = "erick@gmail.com";
        if(userRepository.findByEmail(email).isEmpty()){

            Role role = roleRepository.findById(1)
                    .orElseGet(()-> roleRepository.findByName("ADMIN")
                            .orElseThrow(()-> new ResourceNotFoundException("Rol con id 1 o nombre ADMIN no encontrado")));

            User user = User.builder()
                    .name("Erick")
                    .email(email)
                    .password(passwordEncoder.encode("Lima2025"))
                    .role(role)
                    .build();

            userRepository.save(user);
        }else {
            new BadRequestException("El usuario inicial ya existe: " + email);
        }
    }



}
