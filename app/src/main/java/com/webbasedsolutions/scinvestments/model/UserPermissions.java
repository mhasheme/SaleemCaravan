package com.webbasedsolutions.scinvestments.model;

public class UserPermissions {
    private int Id;
    private String PermissionDesc;

    public UserPermissions(int id, String permissionDesc){
        Id = id;
        PermissionDesc = permissionDesc;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getId() {
        return Id;
    }

    public void setPermissionDesc(String permissionDesc) {
        PermissionDesc = permissionDesc;
    }

    public String getPermissionDesc() {
        return PermissionDesc;
    }
}
