package com.granotec.inventory_api.user;

import com.granotec.inventory_api.common.dto.ApiResponse;
import com.granotec.inventory_api.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PreAuthorize("@permissionService.has('user:view')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> list() {
        return ResponseEntity.ok(new ApiResponse<>("Listado de usuarios", service.listUsers()));
    }

    @PreAuthorize("@permissionService.has('user:view')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> get(@PathVariable Integer id) {
        return ResponseEntity.ok(new ApiResponse<>("Usuario encontrado", service.getUserById(id)));
    }

}
