package com.granotec.inventory_api.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    /**
     * Comprueba si el usuario autenticado tiene el permiso solicitado.
     * Regla simple: si el usuario tiene el role ADMIN (ROLE_ADMIN) se concede siempre.
     * En caso contrario, se busca una autoridad exacta que coincida con el nombre del permiso.
     */
    public boolean has(String permission) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        for (GrantedAuthority ga : auth.getAuthorities()) {
            String a = ga.getAuthority();
            if ("ROLE_ADMIN".equals(a)) return true; // administrador tiene todos los permisos
            if (permission.equals(a)) return true;
        }
        return false;
    }
}

