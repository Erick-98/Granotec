package com.granotec.inventory_api.permission;

import com.granotec.inventory_api.common.dto.ApiResponse;
import com.granotec.inventory_api.permission.dto.AssignPermissionsRequest;
import com.granotec.inventory_api.permission.dto.AssignRoleRequest;
import com.granotec.inventory_api.permission.dto.PermissionRequest;
import com.granotec.inventory_api.permission.dto.PermissionResponse;
import com.granotec.inventory_api.role.dto.RoleRequest;
import com.granotec.inventory_api.role.dto.RoleResponse;
import com.granotec.inventory_api.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("role-permissions")
@RequiredArgsConstructor
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;
    private final UserService userService;


    @PreAuthorize("@permissionService.has('crear_roles')")
    @PostMapping("/roles")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@RequestBody @Valid RoleRequest request){
        return ResponseEntity.ok(new ApiResponse<>("Rol creado correectamente", rolePermissionService.createRole(request)));
    }

    @PreAuthorize("@permissionService.has('crear_permisos')")
    @PostMapping("/permissions")
    public ResponseEntity<ApiResponse<PermissionResponse>> createPermission(@RequestBody @Valid PermissionRequest request) {
        return ResponseEntity.ok(new ApiResponse<>("Permiso creado correctamente", rolePermissionService.createPermission(request)));
    }

    @PreAuthorize("@permissionService.has('asignar_permisos')")
    @PostMapping("/roles/{roleId}/permissions")
    public ResponseEntity<ApiResponse<Void>> assignPermissionsToRole(
            @PathVariable Integer roleId,
            @RequestBody @Valid AssignPermissionsRequest request
    ) {
        rolePermissionService.assignPermissionToRole(roleId, request);
        return ResponseEntity.ok(new ApiResponse<>("Permisos asignados correctamente", null));
    }

    @PreAuthorize("@permissionService.has('asignar_roles')")
    @PostMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<Void>> assignRoleToUser(
            @PathVariable Integer userId,
            @RequestBody @Valid AssignRoleRequest request
    ) {
        userService.assignRoleToUser(userId, request);
        return ResponseEntity.ok(new ApiResponse<>("Rol asignado correctamente", null));
    }

    @PreAuthorize("@permissionService.has('ver_roles')")
    @GetMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<RoleResponse>> getUserRole(@PathVariable Integer userId) {
        return ResponseEntity.ok(new ApiResponse<>("Rol del usuario obtenido", userService.getUserRole(userId)));
    }

    @PreAuthorize("@permissionService.has('ver_roles')")
    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        return ResponseEntity.ok(new ApiResponse<>("Lista de roles", rolePermissionService.getAllRoles()));
    }

    @PreAuthorize("@permissionService.has('ver_permisos')")
    @GetMapping("/permissions")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getAllPermissions() {
        return ResponseEntity.ok(new ApiResponse<>("Lista de permisos", rolePermissionService.getAllPermissions()));
    }

    @PreAuthorize("@permissionService.has('ver_roles')")
    @GetMapping("/roles/{roleId}/permissions")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getRolePermissions(@PathVariable Integer roleId) {
        return ResponseEntity.ok(new ApiResponse<>("Permisos del rol", rolePermissionService.getRolePermissions(roleId)));
    }
}
