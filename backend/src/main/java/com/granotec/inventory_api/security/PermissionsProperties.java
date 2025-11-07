package com.granotec.inventory_api.security;

import java.util.List;
import java.util.Map;

public class PermissionsProperties {

    private List<String> permissions;

    // roles: map roleName -> object { description, permissions: [] }
    private Map<String, RoleDef> roles;

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public Map<String, RoleDef> getRoles() {
        return roles;
    }

    public void setRoles(Map<String, RoleDef> roles) {
        this.roles = roles;
    }

    public static class RoleDef {
        private String description;
        private List<String> permissions;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getPermissions() {
            return permissions;
        }

        public void setPermissions(List<String> permissions) {
            this.permissions = permissions;
        }
    }
}

