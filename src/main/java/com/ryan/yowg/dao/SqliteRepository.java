package com.ryan.yowg.dao;

import com.ryan.yowg.models.Access;
import com.ryan.yowg.models.Credential;
import com.ryan.yowg.models.Resource;
import com.ryan.yowg.models.Wireguard;

import java.util.List;

public class SqliteRepository implements Repository {

    @Override
    public void initialize() {
        DatabaseSetup.createTable();
    }

    // Access
    @Override
    public void insertAccess(Access access) {
        AccessDAO.insertAccess(access);
    }

    @Override
    public List<Access> getAllAccess() {
        return AccessDAO.getAllAccess();
    }

    @Override
    public List<Access> getAccessByWireguard(int wireguardId) {
        return AccessDAO.getAccessByWireguard(wireguardId);
    }

    @Override
    public void updateAccess(Access access) {
        AccessDAO.updateAccess(access);
    }

    @Override
    public void deleteAccess(int accessId) {
        AccessDAO.deleteAccess(accessId);
    }

    // Wireguard
    @Override
    public void insertWireguard(Wireguard wireguard) {
        WireguardDAO.insertWireguard(wireguard);
    }

    @Override
    public List<Wireguard> getAllWireguards() {
        return WireguardDAO.getAllWireguards();
    }

    @Override
    public Wireguard findWireguardByName(String name) {
        return WireguardDAO.findWireguardByName(name);
    }

    @Override
    public List<Wireguard> findWireguardsByAccessName(String accessName) {
        return WireguardDAO.findWireguardsByAccessName(accessName);
    }

    @Override
    public void updateWireguard(Wireguard wireguard) {
        WireguardDAO.updateWireguard(wireguard);
    }

    @Override
    public void deleteWireguard(int id) {
        WireguardDAO.deleteWireguardById(id);
    }

    // Credential
    @Override
    public void insertCredential(Credential credential) {
        CredentialDAO.insertCredential(credential);
    }

    @Override
    public List<Credential> getAllCredentials() {
        return CredentialDAO.getAllCredentials();
    }

    @Override
    public Credential getCredentialById(int id) {
        return CredentialDAO.getCredentialById(id);
    }

    @Override
    public void updateCredential(Credential credential) {
        CredentialDAO.updateCredential(credential);
    }

    @Override
    public void deleteCredential(int id) {
        CredentialDAO.deleteCredential(id);
    }

    // Resource
    @Override
    public void insertResource(Resource resource) {
        ResourceDAO.insertResource(resource);
    }

    @Override
    public List<Resource> getResourcesByAccessId(int accessId) {
        return ResourceDAO.getResourcesByAccessId(accessId);
    }

    @Override
    public List<Resource> getAllResources() {
        return ResourceDAO.getAllResources();
    }

    @Override
    public void updateResource(Resource resource) {
        ResourceDAO.updateResource(resource);
    }

    @Override
    public void deleteResource(int id) {
        ResourceDAO.deleteResource(id);
    }

    // Settings
    @Override
    public String getSetting(String key) {
        return SettingsDAO.getSetting(key);
    }

    @Override
    public void saveSetting(String key, String value) {
        SettingsDAO.saveSetting(key, value);
    }
}
