package dev.gillin.mc.vnllaplayerinfo.groups;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroupSerializerTest {
    @Test
    void deserializeGroups() {
        InputStream yamlInputStream = getClass().getClassLoader().getResourceAsStream("testGroups.yml");
        if (yamlInputStream == null) {
            fail("Unable to read testGroups.yml");
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(yamlInputStream, StandardCharsets.UTF_8));
        List<GroupModel> groups = GroupSerializer.deserializeGroups(config.getConfigurationSection("groups"));

        assertEquals(2, groups.size());

        GroupModel vip1 = groups.stream().filter(g -> g.getDisplayName().equals("VIP")).findFirst().orElse(null);
        GroupModel donor = groups.stream().filter(g -> g.getDisplayName().equals("Donor")).findFirst().orElse(null);

        assertNotNull(vip1);
        assertEquals(100, vip1.getVotesRequired());
        assertEquals(vip1.getRankLength(),2592000);
        List<String> vip1EarnRankCmds = vip1.getEarnRankCommands();
        assertTrue(vip1EarnRankCmds.contains("lp user %player% parent addtemp vip %time%"));
        List<String> vip1LoseRankCmds = vip1.getLoseRankCommands();
        assertTrue(vip1LoseRankCmds.contains("lp user %player% parent removetemp vip"));
        List<String> vips1Permissions = vip1.getPermissions();
        assertTrue(vips1Permissions.contains("some.permission.vip"));
        assertTrue(vip1.isVoteAchievable());

        assertNotNull(donor);
        assertEquals(0, donor.getVotesRequired());
        assertEquals(donor.getRankLength(),2592000);
        List<String> donorEarnRankCmds = donor.getEarnRankCommands();
        assertTrue(donorEarnRankCmds.contains("lp user %player% parent addtemp donor %time%"));
        List<String> donorLoseRankCmds = donor.getLoseRankCommands();
        assertTrue(donorLoseRankCmds.contains("lp user %player% parent removetemp donor"));
        List<String> donorPermissions = donor.getPermissions();
        assertTrue(donorPermissions.contains("some.permission.donor"));
        assertFalse(donor.isVoteAchievable());
    }
}