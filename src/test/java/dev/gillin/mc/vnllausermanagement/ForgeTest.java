package dev.gillin.mc.vnllausermanagement;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.FormattedCommandAlias;
import org.bukkit.command.defaults.HelpCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ForgeTest {

    static final String FORGE_COLOR = "dark_blue";

    /**
     * Method under test: {@link Forge#Forge(VnllaUserManagement)}
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
        VnllaUserManagement p = null;

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
        //       at dev.gillin.mc.vnllausermanagement.Forge.onCommand(Forge.java:57)
        //   See https://diff.blue/R013 to resolve this issue.

        (new Forge(null)).onCommand(mock(CommandSender.class), mock(Command.class), "Command Label", new String[]{"Args"});
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


    /**
     * Method under test: {@link Forge#onCommand(CommandSender, Command, String, String[])}
     */
    @Test
    @Disabled("Investigation needed")
    void testOnCommandForgeLoreOneWord() {
        //testOnCommandForge("OneWordName","OneWordLore"); TODO: Investigate why this broke
        testOnCommandForge("Multi Word Name","Multi Word lore");
        testOnCommandForge("OneWordName","Multi Word lore");
        testOnCommandForge("Multi Word Name","OneWordLore");

    }

    void testOnCommandForge(String name, String lore){
        Forge forge = new Forge(null);
        Player commandSender = mock(Player.class);
        ItemStack stack=mock(ItemStack.class);
        ItemMeta meta=mock(ItemMeta.class);
        PlayerInventory playerInventory = mock(PlayerInventory.class);

        doAnswer(invocation -> {
            System.out.println(invocation.getArguments()[0]);
            return null;
        }).when(commandSender).sendMessage(anyString());

        when(commandSender.getInventory()).thenReturn(playerInventory);
        when(playerInventory.getItemInMainHand()).thenReturn(stack);
        when(stack.getItemMeta()).thenReturn(meta);

        ArrayList<String> loreStrings=new ArrayList<>();
        loreStrings.add(lore.trim());

        String[] nameSplitArray = name.split(" ");
        String[] loreSplitArray = lore.split(" ");
        String[] args = combineArray(nameSplitArray, loreSplitArray);

        if(args.length>1){
            args[0]="\"" + args[0];
            args[args.length-1] = args[args.length-1] + "\"";
        }

        args = combineArray(new String[]{FORGE_COLOR}, args);


        //doNothing().when(commandSender).sendMessage((String) any());
        assertTrue(forge.onCommand(commandSender, new FormattedCommandAlias("forge", new String[]{"forge"}),
                "forge", args));
        verify(meta).setDisplayName(Forge.colorMap.getOrDefault(FORGE_COLOR, ChatColor.MAGIC) + name);
        verify(meta).setLore(loreStrings);
    }

    private String[] combineArray(String[] a, String[] b){
        String[] result = new String[a.length + b.length];
        for (int i=0; i < a.length; i++){
            result[i]=a[i];
        }
        for (int i=0; i < b.length; i++){
            result[i+a.length]=b[i];
        }
        return result;
    }
}

