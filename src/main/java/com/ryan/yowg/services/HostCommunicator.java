package com.ryan.yowg.services;

import com.ryan.yowg.models.Access;
import java.util.concurrent.CompletableFuture;

public interface HostCommunicator {
    void openSSHTerminal(String address, String user, int port, String credentialType, String credentialSecret);
    void openPingTerminal(String address);
    void openUrl(String url);
    boolean ping(String address);

    boolean isSshpassInstalled();
    CompletableFuture<Void> deploySharedKeyAsync(Access access, String password);
    CompletableFuture<Void> generateAndDeployKeyAsync(String profileName, String address, String username, int port, String password);
    CompletableFuture<Boolean> testSshAutologinAsync(Access access);
}

