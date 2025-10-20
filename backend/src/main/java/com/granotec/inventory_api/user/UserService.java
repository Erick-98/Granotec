package com.granotec.inventory_api.user;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.permission.dto.AssignRoleRequest;
import com.granotec.inventory_api.role.Role;
import com.granotec.inventory_api.role.RoleRepository;
import com.granotec.inventory_api.role.dto.RoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public void assignRoleToUser(Integer userId, AssignRoleRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado"));

        Role role = roleRepository.findByName(request.role())
                .orElseThrow(()-> new ResourceNotFoundException("Rol no encontrado"));

        user.setRole(role);
        userRepository.save(user);
    }

    public RoleResponse getUserRole(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Role role = user.getRole();
        if(role == null){
            throw new BadRequestException("El usuario no tiene un rol asignado");
        }

        return new RoleResponse(role.getId(), role.getName());
    }

}
