package com.ryan.yowg.services;

import com.ryan.yowg.dao.DatabaseSetup;
import com.ryan.yowg.models.Access;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SshAutologinDetectionTest {

    @BeforeAll
    public static void setup() {
        String tempDb = System.getProperty("java.io.tmpdir") + "/yowg_test.db";
        System.setProperty("yowg.db.url", "jdbc:sqlite:" + tempDb);
        com.ryan.yowg.dao.DatabaseConnector.setTestDbUrl("jdbc:sqlite:" + tempDb);
        DatabaseSetup.createTable();
    }

    @Test
    public void testFakeHostCommunicatorSshAutologin() throws Exception {
        FakeHostCommunicator hostCommunicator = new FakeHostCommunicator();
        Access access = new Access(1, "Test Node", "192.168.1.100", "admin", 22, 1, null);

        hostCommunicator.testAutologinResult = true;
        Boolean canLogin = hostCommunicator.testSshAutologinAsync(access).get();
        assertTrue(canLogin);

        hostCommunicator.testAutologinResult = false;
        Boolean failedLogin = hostCommunicator.testSshAutologinAsync(access).get();
        assertFalse(failedLogin);
    }
}
