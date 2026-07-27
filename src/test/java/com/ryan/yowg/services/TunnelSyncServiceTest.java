package com.ryan.yowg.services;

import com.ryan.yowg.dao.DatabaseSetup;
import com.ryan.yowg.dao.SettingsDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TunnelSyncServiceTest {

    @BeforeAll
    public static void setup() {
        String tempDb = System.getProperty("java.io.tmpdir") + "/yowg_test.db";
        System.setProperty("yowg.db.url", "jdbc:sqlite:" + tempDb);
        com.ryan.yowg.dao.DatabaseConnector.setTestDbUrl("jdbc:sqlite:" + tempDb);
        DatabaseSetup.createTable();
    }

    @Test
    public void testSyncIfFirstRunFlag() {
        // Ensure flag is saved when syncIfFirstRun is invoked
        SettingsDAO.saveSetting("initial_sync_completed", "false");
        assertNotEquals("true", SettingsDAO.getSetting("initial_sync_completed"));

        TunnelSyncService.syncIfFirstRun();
        assertEquals("true", SettingsDAO.getSetting("initial_sync_completed"));
    }
}
