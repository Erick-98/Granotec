package com.granotec.inventory_api.permission;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.permission.dto.AssignPermissionsRequest;
import com.granotec.inventory_api.permission.dto.PermissionRequest;
import com.granotec.inventory_api.permission.dto.PermissionResponse;
import com.granotec.inventory_api.role.Role;
import com.granotec.inventory_api.role.RoleRepository;
import com.granotec.inventory_api.role.dto.RoleRequest;
import com.granotec.inventory_api.role.dto.RoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;


    public RoleResponse createRole(RoleRequest request) {

        if(request.name() == null || request.name().isBlank()) {
            throw new BadRequestException("El nombre del rol es obligatorio");
        }

        if(roleRepository.findByName(request.name()).isPresent()) {
            throw new BadRequestException("El rol ya existe");
        }

        Role role = Role.builder().name(request.name()).build();
        roleRepository.save(role);
        return new RoleResponse(role.getId(), role.getName());
    }

    public PermissionResponse createPermission(PermissionRequest request) {

        if(request.name() == null || request.name().isBlank()) {
            throw new BadRequestException("El nombre del permiso es obligatorio");
        }

        if(permissionRepository.findByName(request.name()).isPresent()) {
            throw new BadRequestException("El permiso ya existe");
        }

        Permission p = Permission.builder().name(request.name()).build();
        permissionRepository.save(p);
        return new PermissionResponse(p.getId(),p.getName());
    }

    public void assignPermissionToRole(Integer roleId, AssignPermissionsRequest request){
        Role role = roleRepository.findById(roleId)
                .orElseThrow(()-> new ResourceNotFoundException("Rol no encontrado"));

        Set<Permission> permissions = request.permissions().stream()
                .map(name -> permissionRepository.findByName(name)
                        .orElseThrow(()-> new ResourceNotFoundException("Permiso no encontrado: " + name)))
                .collect(Collectors.toSet());
        role.addPermissions(permissions);
        roleRepository.save(role);
    }

    public List<PermissionResponse> getRolePermissions(Integer roleId){
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

        return role.getPermissions().stream()
                .map(p -> new PermissionResponse(p.getId(), p.getName()))
                .toList();
    }

    public List<RoleResponse> getAllRoles(){
        return roleRepository.findAll().stream()
                .map(r -> new RoleResponse(r.getId(), r.getName()))
                .toList();
    }

    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(p -> new PermissionResponse(p.getId(),p.getName()))
                .toList();
    }

}
