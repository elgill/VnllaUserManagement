package dev.gillin.mc.vnllausermanagement;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class VnllaUserManagementTest {
    private VnllaUserManagement vnllaUserManagement;
    private ServerMock server;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        vnllaUserManagement = MockBukkit.load(VnllaUserManagement.class);
    }
    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

}

