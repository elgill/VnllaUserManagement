package dev.gillin.mc.vnllaplayerinfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.FormattedCommandAlias;
import org.bukkit.command.defaults.HelpCommand;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ForgeTest {
    /**
     * Method under test: {@link Forge#Forge(VnllaPlayerInfo)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testConstructor() {
        // TODO: Complete this test.
        //   Reason: R008 Failed to instantiate class under test.
        //   Diffblue Cover was unable to construct an instance of Forge.
        //   Ensure there is a package-visible constructor or factory method that does not
        //   throw for the class under test.
        //   If such a method is already present but Diffblue Cover does not find it, it can
        //   be specified using custom rules for inputs:
        //   https://docs.diffblue.com/knowledge-base/cli/custom-inputs/
        //   This can happen because the factory method takes arguments, throws, returns null
        //   or returns a subtype.
        //   See https://diff.blue/R008 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        VnllaPlayerInfo p = null;

        // Act
        Forge actualForge = new Forge(p);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link Forge#onTabComplete(CommandSender, Command, String, String[])}
     */
    @Test
    void testOnTabComplete() {
        Forge forge = new Forge(null);
        CommandSender sender = mock(CommandSender.class);
        assertTrue(forge.onTabComplete(sender, new HelpCommand(), "Label", new String[]{"Args"}).isEmpty());
    }

    /**
     * Method under test: {@link Forge#onTabComplete(CommandSender, Command, String, String[])}
     */
    @Test
    void testOnTabComplete2() {
        Forge forge = new Forge(null);
        CommandSender sender = mock(CommandSender.class);
        List<String> actualOnTabCompleteResult = forge.onTabComplete(sender, new HelpCommand(), "Label",
                new String[]{""});
        assertEquals(15, actualOnTabCompleteResult.size());
        assertEquals("aqua", actualOnTabCompleteResult.get(0));
        assertEquals("black", actualOnTabCompleteResult.get(1));
        assertEquals("blue", actualOnTabCompleteResult.get(2));
        assertEquals("dark_aqua", actualOnTabCompleteResult.get(3));
        assertEquals("dark_blue", actualOnTabCompleteResult.get(4));
        assertEquals("dark_gray", actualOnTabCompleteResult.get(5));
        assertEquals("gold", actualOnTabCompleteResult.get(9));
        assertEquals("gray", actualOnTabCompleteResult.get(10));
        assertEquals("light_purple", actualOnTabCompleteResult.get(11));
        assertEquals("red", actualOnTabCompleteResult.get(12));
        assertEquals("white", actualOnTabCompleteResult.get(13));
        assertEquals("yellow", actualOnTabCompleteResult.get(14));
    }

    /**
     * Method under test: {@link Forge#onTabComplete(CommandSender, Command, String, String[])}
     */
    @Test
    void testOnTabComplete3() {
        Forge forge = new Forge(null);
        CommandSender sender = mock(CommandSender.class);
        assertTrue(forge.onTabComplete(sender, new HelpCommand(), "Label", new String[]{}).isEmpty());
    }

    /**
     * Method under test: {@link Forge#onCommand(CommandSender, Command, String, String[])}
     */
    @Test
    void testOnCommand() {
        Forge forge = new Forge(null);
        CommandSender sender = mock(CommandSender.class);
        assertFalse(forge.onCommand(sender, new HelpCommand(), "Command Label", new String[]{"Args"}));
    }

    /**
     * Method under test: {@link Forge#onCommand(CommandSender, Command, String, String[])}
     */
    @Test
    void testOnCommand2() {
        Forge forge = new Forge(null);
        CommandSender sender = mock(CommandSender.class);
        assertFalse(forge.onCommand(sender, new FormattedCommandAlias("forge", new String[]{"forge"}), "Command Label",
                new String[]{"Args"}));
    }

    /**
     * Method under test: {@link Forge#onCommand(CommandSender, Command, String, String[])}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testOnCommand3() {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "org.bukkit.command.Command.getName()" because "command" is null
        //       at dev.gillin.mc.vnllaplayerinfo.Forge.onCommand(Forge.java:57)
        //   See https://diff.blue/R013 to resolve this issue.

        (new Forge(null)).onCommand(mock(CommandSender.class), null, "Command Label", new String[]{"Args"});
    }

    /**
     * Method under test: {@link Forge#onCommand(CommandSender, Command, String, String[])}
     */
    @Test
    void testOnCommand4() {
        Forge forge = new Forge(null);
        CommandSender commandSender = mock(CommandSender.class);
        doNothing().when(commandSender).sendMessage((String) any());
        assertTrue(forge.onCommand(commandSender, new FormattedCommandAlias("forge", new String[]{"forge"}),
                "Command Label", new String[]{"forge", "forge", "forge"}));
        verify(commandSender).sendMessage((String) any());
    }
}

