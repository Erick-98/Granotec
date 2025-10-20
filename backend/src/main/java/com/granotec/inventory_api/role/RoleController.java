package com.granotec.inventory_api.role;

import com.granotec.inventory_api.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService service;

    //@PreAuthorize("hasAuthority('ver_roles')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Role>>> findAll() {
        return ResponseEntity.ok(new ApiResponse<>("Lista de roles", service.findAll()));
    }

    //@PreAuthorize("hasAuthority('crear_roles')")
    @PostMapping
    public ResponseEntity<ApiResponse<Role>> create(@RequestBody Role role) {
        return ResponseEntity.ok(new ApiResponse<>("Rol creado", service.create(role)));
    }

    //@PreAuthorize("hasAuthority('asignar_permisos')")
    @PostMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<Role>> assignPermissions(@PathVariable Integer id, @RequestBody Set<Integer> permissionIds) {
        return ResponseEntity.ok(new ApiResponse<>("Permisos asignados", service.assignPermissions(id, permissionIds)));
    }

    //@PreAuthorize("hasAuthority('eliminar_roles')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(new ApiResponse<>("Rol eliminado", null));
    }

}
