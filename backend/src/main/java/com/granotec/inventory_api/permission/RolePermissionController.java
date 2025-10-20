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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("role-permissions")
@RequiredArgsConstructor
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;
    private final UserService userService;


    @PostMapping("/roles")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@RequestBody @Valid RoleRequest request){
        return ResponseEntity.ok(new ApiResponse<>("Rol creado correectamente", rolePermissionService.createRole(request)));
    }

    @PostMapping("/permissions")
    public ResponseEntity<ApiResponse<PermissionResponse>> createPermission(@RequestBody @Valid PermissionRequest request) {
        return ResponseEntity.ok(new ApiResponse<>("Permiso creado correctamente", rolePermissionService.createPermission(request)));
    }

    @PostMapping("/roles/{roleId}/permissions")
    public ResponseEntity<ApiResponse<Void>> assignPermissionsToRole(
            @PathVariable Integer roleId,
            @RequestBody @Valid AssignPermissionsRequest request
    ) {
        rolePermissionService.assignPermissionToRole(roleId, request);
        return ResponseEntity.ok(new ApiResponse<>("Permisos asignados correctamente", null));
    }

    @PostMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<Void>> assignRoleToUser(
            @PathVariable Integer userId,
            @RequestBody @Valid AssignRoleRequest request
    ) {
        userService.assignRoleToUser(userId, request);
        return ResponseEntity.ok(new ApiResponse<>("Rol asignado correctamente", null));
    }

    @GetMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<RoleResponse>> getUserRole(@PathVariable Integer userId) {
        return ResponseEntity.ok(new ApiResponse<>("Rol del usuario obtenido", userService.getUserRole(userId)));
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        return ResponseEntity.ok(new ApiResponse<>("Lista de roles", rolePermissionService.getAllRoles()));
    }

    @GetMapping("/permissions")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getAllPermissions() {
        return ResponseEntity.ok(new ApiResponse<>("Lista de permisos", rolePermissionService.getAllPermissions()));
    }

    @GetMapping("/roles/{roleId}/permissions")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getRolePermissions(@PathVariable Integer roleId) {
        return ResponseEntity.ok(new ApiResponse<>("Permisos del rol", rolePermissionService.getRolePermissions(roleId)));
    }
}
