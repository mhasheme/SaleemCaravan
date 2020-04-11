package com.webbasedsolutions.scinvestments;

import android.app.Application;

import com.webbasedsolutions.scinvestments.collections.GlobalCollectionsSinglton;
import com.webbasedsolutions.scinvestments.model.UserPermissions;

import java.util.ArrayList;
import java.util.List;


public class Settings extends Application {

    private String ip = "172.28.6.80d";
    private String appName = "SalCApi";
    private String protocol = "http";
    private String securityToken = "";
    private List<UserPermissions> permissionsList = new ArrayList<UserPermissions>();
    private GlobalCollectionsSinglton globalCollectionsSinglton;
    private String loginUser;
    private Object transferObject;
    public GlobalCollectionsSinglton getGlobalCollectionsSinglton() throws Exception{
        if( globalCollectionsSinglton != null)
            return  globalCollectionsSinglton;

        boolean isReady = GlobalCollectionsSinglton.getInstance().getReady() ;
        if(!isReady)
            throw new Exception("Collection is not initialized");

        globalCollectionsSinglton = GlobalCollectionsSinglton.getInstance();
        return  globalCollectionsSinglton;

    }

    public GlobalCollectionsSinglton getGlobalCollectionsSingltonSafe(){
        return  GlobalCollectionsSinglton.getInstance();
    }

    public void setTransferObject(Object transferObject) {
        this.transferObject = transferObject;
    }

    public Object getTransferObject() {
        return transferObject;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAppName(){
        return appName;
    }

    public void setAppName(String appName){
        this.appName = appName;
    }

    public String getProtocol(){
        return protocol;
    }

    public void setProtocol(String protocol){
        this.protocol = protocol;
    }

    public String getBaseURL(){
        return this.getProtocol() + "://" + this.getIp() + "/" + this.getAppName();
    }

    public void setPermissionsList(List<UserPermissions> permissionsList) {
        this.permissionsList = permissionsList;
    }

    public void wipeAll(){
        setLoginUser(null);
        setPermissionsList(null);
        securityToken = null;
        GlobalCollectionsSinglton.getInstance().wipeAll();
    }
    public List<UserPermissions> getPermissionsList() {
        return permissionsList;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String token){
        this.securityToken = token;
    }
}