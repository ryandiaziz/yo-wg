package com.ryan.yowg.services;

import com.ryan.yowg.controllers.SettingsController;
import com.ryan.yowg.dao.AccessDAO;
import com.ryan.yowg.dao.CredentialDAO;
import com.ryan.yowg.dao.DatabaseSetup;
import com.ryan.yowg.dao.ResourceDAO;
import com.ryan.yowg.dao.SettingsDAO;
import com.ryan.yowg.dao.WireguardDAO;
import com.ryan.yowg.models.Access;
import com.ryan.yowg.models.Credential;
import com.ryan.yowg.models.Resource;
import com.ryan.yowg.models.Wireguard;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataCleanupTest {

    @BeforeAll
    public static void setup() {
        String tempDb = System.getProperty("java.io.tmpdir") + "/yowg_test.db";
        System.setProperty("yowg.db.url", "jdbc:sqlite:" + tempDb);
        com.ryan.yowg.dao.DatabaseConnector.setTestDbUrl("jdbc:sqlite:" + tempDb);
        DatabaseSetup.createTable();
    }

    @Test
    public void testSelectiveDataCleanup() {
        // Insert sample data
        Wireguard wg = new Wireguard(0, "test_cleanup_wg", "Note", "[Interface]");
        WireguardDAO.insertWireguard(wg);
        Wireguard insertedWg = WireguardDAO.findWireguardByName("test_cleanup_wg");
        assertNotNull(insertedWg);

        Access access = new Access(0, "Test Access", "192.168.100.99", "root", 22, insertedWg.getId(), null);
        AccessDAO.insertAccess(access);

        Credential cred = new Credential("Cleanup Profile", "admin", "password", "secret123");
        CredentialDAO.insertCredential(cred);

        SettingsDAO.saveSetting("test_setting_key", "test_value");

        // Verify data exists
        assertFalse(WireguardDAO.getAllWireguards().isEmpty());
        assertFalse(CredentialDAO.getAllCredentials().isEmpty());
        assertEquals("test_value", SettingsDAO.getSetting("test_setting_key"));

        // Perform selective cleanup of credentials and settings only
        SettingsController.performCleanup(true, false, false, false, true);

        // Assert credentials and settings are wiped, but wireguards remain
        assertTrue(CredentialDAO.getAllCredentials().isEmpty());
        assertNull(SettingsDAO.getSetting("test_setting_key"));
        assertFalse(WireguardDAO.getAllWireguards().isEmpty());

        // Perform full cleanup of access, resources, and wireguards
        SettingsController.performCleanup(false, true, true, false, false);
        assertTrue(WireguardDAO.getAllWireguards().isEmpty());
    }
}
