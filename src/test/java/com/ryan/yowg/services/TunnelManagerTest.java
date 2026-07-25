package com.ryan.yowg.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TunnelManagerTest {

    @Test
    public void testTunnelManagerFake() {
        FakeTunnelManager tunnelManager = new FakeTunnelManager();
        
        // Initial state
        assertNull(tunnelManager.lastUp);
        assertEquals(0, tunnelManager.configs.size());

        // Create config
        String createRes = tunnelManager.createConfig("wg0", "[Interface]\nPrivateKey = foo");
        assertTrue(createRes.contains("successfully"));
        assertEquals(1, tunnelManager.configs.size());
        assertEquals("[Interface]\nPrivateKey = foo", tunnelManager.configs.get("wg0"));

        // Up tunnel
        tunnelManager.up("wg0");
        assertEquals("wg0", tunnelManager.lastUp);
        assertEquals(1, tunnelManager.upCalls.size());
        assertEquals("wg0", tunnelManager.upCalls.get(0));

        // Down tunnel
        tunnelManager.down("wg0");
        assertNull(tunnelManager.lastUp);
        assertEquals(1, tunnelManager.downCalls.size());
        assertEquals("wg0", tunnelManager.downCalls.get(0));
    }

    @Test
    public void testTunnelManagerStateChangeListener() {
        FakeTunnelManager tunnelManager = new FakeTunnelManager();
        final String[] notifiedTunnel = new String[1];
        final boolean[] notifiedStatus = new boolean[1];

        tunnelManager.addStateChangeListener((name, isActive) -> {
            notifiedTunnel[0] = name;
            notifiedStatus[0] = isActive;
        });

        tunnelManager.up("wg1");
        assertEquals("wg1", notifiedTunnel[0]);
        assertTrue(notifiedStatus[0]);
        assertTrue(tunnelManager.isTunnelActive("wg1"));

        tunnelManager.down("wg1");
        assertNull(notifiedTunnel[0]);
        assertFalse(notifiedStatus[0]);
        assertFalse(tunnelManager.isTunnelActive("wg1"));
    }

    @Test
    public void testHostCommunicatorKeyDeployment() throws Exception {
        FakeHostCommunicator hostCommunicator = new FakeHostCommunicator();
        assertTrue(hostCommunicator.isSshpassInstalled());

        com.ryan.yowg.models.Access access = new com.ryan.yowg.models.Access(1, "Test Server", "10.0.0.5", "admin", 22, 1, null);
        assertDoesNotThrow(() -> hostCommunicator.deploySharedKeyAsync(access, "secret").get());
        assertDoesNotThrow(() -> hostCommunicator.generateAndDeployKeyAsync("Profile1", "10.0.0.5", "admin", 22, "secret").get());
    }
}
