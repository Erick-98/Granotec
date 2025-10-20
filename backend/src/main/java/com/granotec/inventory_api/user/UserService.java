package com.granotec.inventory_api.user;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.permission.dto.AssignRoleRequest;
import com.granotec.inventory_api.role.Role;
import com.granotec.inventory_api.role.RoleRepository;
import com.granotec.inventory_api.role.dto.RoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    // Nuevo: listar todos los usuarios como UserResponse
    public List<UserResponse> listUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Nuevo: obtener un usuario por id
    public UserResponse getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .filter(u -> !Boolean.TRUE.equals(u.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return toDto(user);
    }

    private UserResponse toDto(User user){
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                toRoleResponse(user.getRole())
        );
    }

    private RoleResponse toRoleResponse(Role role){
        if(role == null){
            return null;
        }
        return new RoleResponse(role.getId(),role.getName());
    }

}
