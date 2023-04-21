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

    @Test
    @Disabled("I need to figure the db stuff out")
    public void testHandleLeaving() {
        // Prepare the test data
        /*String uuid = "test-uuid";
        UUID playerUUID = UUID.randomUUID();
        Player player = server.addPlayer(playerUUID.toString());
        World world = server.addSimpleWorld("test-world");
        Location loc = new Location(world, 10.0, 20.0, 30.0);
        player.teleport(loc);

        // Call the handleLeaving method
        vnllaPlayerInfo.handleLeaving(playerUUID.toString(), false);

        // Retrieve the playerConfigModel from the player UUID
        PlayerConfigModel playerConfigModel = PlayerConfigModel.fromUUID(vnllaPlayerInfo, uuid);

        // Verify that the last logout time, location, and world are updated correctly
        assertEquals(10.0, playerConfigModel.getLastLocationX(), 0.001);
        assertEquals(20.0, playerConfigModel.getLastLocationY(), 0.001);
        assertEquals(30.0, playerConfigModel.getLastLocationZ(), 0.001);
        assertEquals("test-world", playerConfigModel.getLastLocationWorld());*/
    }
}

