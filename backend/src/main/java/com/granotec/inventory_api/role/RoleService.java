package com.granotec.inventory_api.role;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.permission.Permission;
import com.granotec.inventory_api.permission.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public Role create(Role role){
        roleRepository.findByName(role.getName()).ifPresent(r -> {
            throw new BadRequestException("El rol ya existe");
        });
        return roleRepository.save(role);
    }

    public List<Role> findAll(){
        return roleRepository.findAll();
    }

    public Role assignPermissions(Integer roleId, Set<Integer> permissionIds){
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BadRequestException("Rol no encontrado"));
        Set<Permission> permissions = permissionRepository.findAllById(permissionIds).stream().collect(Collectors.toSet());
        role.getPermissions().addAll(permissions);
        return roleRepository.save(role);
    }

    public void delete(Integer id){
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Rol no encontrado"));
        role.softDelete();
        roleRepository.save(role);
    }
}
