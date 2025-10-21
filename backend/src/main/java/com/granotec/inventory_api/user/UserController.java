package com.granotec.inventory_api.user;

import com.granotec.inventory_api.common.dto.ApiResponse;
import com.granotec.inventory_api.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    //@PreAuthorize("hasAuthority('ver_usuarios')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> list() {
        return ResponseEntity.ok(new ApiResponse<>("Listado de usuarios", service.listUsers()));
    }

    //@PreAuthorize("hasAuthority('ver_usuarios')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> get(@PathVariable Integer id) {
        return ResponseEntity.ok(new ApiResponse<>("Usuario encontrado", service.getUserById(id)));
    }

}
