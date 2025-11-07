package com.granotec.inventory_api.security;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.granotec.inventory_api.permission.Permission;
import com.granotec.inventory_api.permission.PermissionRepository;
import com.granotec.inventory_api.role.Role;
import com.granotec.inventory_api.role.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PermissionsInitializer {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @PostConstruct
    public void init() throws Exception {
        var resource = new ClassPathResource("permissions.yml");
        try (InputStream is = resource.getInputStream()) {
            YAMLMapper mapper = new YAMLMapper();
            PermissionsProperties props = mapper.readValue(is, PermissionsProperties.class);

            // sync permissions
            List<String> perms = props.getPermissions();
            if (perms != null) {
                for (String p : perms) {
                    permissionRepository.findByName(p).orElseGet(() -> permissionRepository.save(Permission.builder().name(p).build()));
                }
            }

            // sync roles
            Map<String, PermissionsProperties.RoleDef> roles = props.getRoles();
            if (roles != null) {
                for (Map.Entry<String, PermissionsProperties.RoleDef> e : roles.entrySet()) {
                    String roleName = e.getKey();
                    PermissionsProperties.RoleDef def = e.getValue();

                    Role role = roleRepository.findByName(roleName).orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));

                    if(role.getPermissions() == null || role.getPermissions().isEmpty()) {
                        // add permissions to role if missing
                        if (def.getPermissions() != null) {
                            for (String perm : def.getPermissions()) {
                                if ("*".equals(perm)) continue; // wildcard handled by ROLE_ADMIN special-case
                                Permission permission = permissionRepository.findByName(perm)
                                        .orElseGet(() -> permissionRepository.save(Permission.builder().name(perm).build()));
                                role.getPermissions().add(permission);
                            }
                        }
                        roleRepository.save(role);
                    }
                }
            }
        }
    }
}





























