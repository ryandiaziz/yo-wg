package com.ryan.yowg.dao;

import com.ryan.yowg.models.Access;
import com.ryan.yowg.models.Credential;
import com.ryan.yowg.models.Resource;
import com.ryan.yowg.models.Wireguard;

import java.util.List;

public interface Repository {
    void initialize();

    // Access
    void insertAccess(Access access);
    List<Access> getAllAccess();
    List<Access> getAccessByWireguard(int wireguardId);
    void updateAccess(Access access);
    void deleteAccess(int accessId);

    // Wireguard
    void insertWireguard(Wireguard wireguard);
    List<Wireguard> getAllWireguards();
    Wireguard findWireguardByName(String name);
    List<Wireguard> findWireguardsByAccessName(String accessName);
    void updateWireguard(Wireguard wireguard);
    void deleteWireguard(int id);

    // Credential
    void insertCredential(Credential credential);
    List<Credential> getAllCredentials();
    Credential getCredentialById(int id);
    void updateCredential(Credential credential);
    void deleteCredential(int id);

    // Resource
    void insertResource(Resource resource);
    List<Resource> getResourcesByAccessId(int accessId);
    List<Resource> getAllResources();
    void updateResource(Resource resource);
    void deleteResource(int id);

    // Settings
    String getSetting(String key);
    void saveSetting(String key, String value);
}
