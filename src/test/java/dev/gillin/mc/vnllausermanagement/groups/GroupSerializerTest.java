package dev.gillin.mc.vnllausermanagement.groups;

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

        assertEquals(3, groups.size());

        GroupModel vip1 = groups.stream().filter(g -> g.getDisplayName().equals("VIP")).findFirst().orElse(null);
        GroupModel vip2 = groups.stream().filter(g -> g.getDisplayName().equals("vip2")).findFirst().orElse(null);
        GroupModel donor = groups.stream().filter(g -> g.getDisplayName().equals("Donor")).findFirst().orElse(null);

        assertNotNull(vip1);
        assertEquals(100, vip1.getVotesRequired());
        assertEquals(2592000, vip1.getRankLength());
        List<String> vip1EarnRankCmds = vip1.getEarnRankCommands();
        assertTrue(vip1EarnRankCmds.contains("lp user %player% parent addtemp vip %time%"));
        List<String> vip1LoseRankCmds = vip1.getLoseRankCommands();
        assertTrue(vip1LoseRankCmds.contains("lp user %player% parent removetemp vip"));
        String vip1LuckPermsGroupName = vip1.getLuckPermsGroupName();
        assertTrue(vip1LuckPermsGroupName.contains("some.group.vip"));
        assertTrue(vip1.isVoteAchievable());

        assertNotNull(vip2);
        assertEquals(0, vip2.getVotesRequired());
        assertEquals(2592000, vip2.getRankLength());
        List<String> vip2EarnRankCmds = vip2.getEarnRankCommands();
        assertTrue(vip2EarnRankCmds.isEmpty());
        List<String> vip2LoseRankCmds = vip2.getLoseRankCommands();
        assertTrue(vip2LoseRankCmds.isEmpty());
        String vip2LuckPermsGroupName = vip2.getLuckPermsGroupName();
        assertTrue(vip2LuckPermsGroupName.contains("some.group.vip2"));
        assertTrue(vip2.isVoteAchievable());

        assertNotNull(donor);
        assertEquals(0, donor.getVotesRequired());
        assertEquals(2592000, donor.getRankLength());
        List<String> donorEarnRankCmds = donor.getEarnRankCommands();
        assertTrue(donorEarnRankCmds.contains("lp user %player% parent addtemp donor %time%"));
        List<String> donorLoseRankCmds = donor.getLoseRankCommands();
        assertTrue(donorLoseRankCmds.contains("lp user %player% parent removetemp donor"));
        String donorLuckPermsGroupName = donor.getLuckPermsGroupName();
        assertTrue(donorLuckPermsGroupName.equalsIgnoreCase("some.group.donor"));
        assertFalse(donor.isVoteAchievable());
    }
}