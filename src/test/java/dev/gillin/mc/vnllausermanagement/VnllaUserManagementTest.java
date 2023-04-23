package dev.gillin.mc.vnllausermanagement;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

