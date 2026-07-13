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

        // Delete config
        String deleteRes = tunnelManager.deleteConfig("wg0");
        assertTrue(deleteRes.contains("successfully"));
        assertEquals(0, tunnelManager.configs.size());
    }

    @Test
    public void testHostCommunicatorFake() {
        FakeHostCommunicator hostCommunicator = new FakeHostCommunicator();

        // Test SSH
        hostCommunicator.openSSHTerminal("192.168.1.1", "admin", 2222);
        assertEquals(1, hostCommunicator.sshCalls.size());
        assertEquals("admin@192.168.1.1:2222", hostCommunicator.sshCalls.get(0));

        // Test Ping Terminal
        hostCommunicator.openPingTerminal("10.0.0.1");
        assertEquals(1, hostCommunicator.pingTerminalCalls.size());
        assertEquals("10.0.0.1", hostCommunicator.pingTerminalCalls.get(0));

        // Test URL
        hostCommunicator.openUrl("http://localhost:8080");
        assertEquals(1, hostCommunicator.urlCalls.size());
        assertEquals("http://localhost:8080", hostCommunicator.urlCalls.get(0));

        // Test Ping status check
        hostCommunicator.pingResponses.put("192.168.1.1", true);
        hostCommunicator.pingResponses.put("192.168.1.2", false);

        assertTrue(hostCommunicator.ping("192.168.1.1"));
        assertFalse(hostCommunicator.ping("192.168.1.2"));
    }
}
