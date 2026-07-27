package com.ryan.yowg.services;

import com.ryan.yowg.dao.DatabaseSetup;
import com.ryan.yowg.dao.SettingsDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SystemTunnelManagerTest {

    @BeforeAll
    public static void setup() {
        String tempDb = System.getProperty("java.io.tmpdir") + "/yowg_test.db";
        System.setProperty("yowg.db.url", "jdbc:sqlite:" + tempDb);
        com.ryan.yowg.dao.DatabaseConnector.setTestDbUrl("jdbc:sqlite:" + tempDb);
        DatabaseSetup.createTable();
        SettingsDAO.saveSetting("sudo_password", "invalid_password_test");
    }

    @Test
    public void testIsTunnelActiveSpeed() {
        SystemTunnelManager manager = new SystemTunnelManager();

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            boolean active = manager.isTunnelActive("nonexistent_wg_" + i);
            assertFalse(active);
        }
        long elapsedTime = System.currentTimeMillis() - startTime;

        System.out.println("[PERF TEST] Elapsed time for 10 isTunnelActive calls: " + elapsedTime + " ms");
        // 10 fast sysfs non-blocking checks should finish well under 200ms.
        // If it spawns 10 sudo processes synchronously, it will take much longer (e.g. 500ms - 2000ms+).
        assertTrue(elapsedTime < 200, "isTunnelActive took too long (" + elapsedTime + " ms), indicating blocking process execution per tunnel!");
    }
}
