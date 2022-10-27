package dev.gillin.mc.vnllaplayerinfo.Database;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.gillin.mc.vnllaplayerinfo.VnllaPlayerInfo;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class SQLiteTest {
    /**
     * Method under test: {@link SQLite#SQLite(VnllaPlayerInfo)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testConstructor() {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "dev.gillin.mc.vnllaplayerinfo.VnllaPlayerInfo.getConfig()" because "this.plugin" is null
        //       at dev.gillin.mc.vnllaplayerinfo.Database.SQLite.<init>(SQLite.java:19)
        //   See https://diff.blue/R013 to resolve this issue.

        new SQLite(null);
    }

    /**
     * Method under test: {@link SQLite#getSQLConnection()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetSQLConnection() {
        // TODO: Complete this test.
        //   Reason: R011 Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to access files outside the temporary directory (file 'defaultname.db', permission 'write').
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        VnllaPlayerInfo vnllaPlayerInfo = mock(VnllaPlayerInfo.class);
        when(vnllaPlayerInfo.getConfig()).thenReturn(new YamlConfiguration());
        (new SQLite(vnllaPlayerInfo)).getSQLConnection();
    }

    /**
     * Method under test: {@link SQLite#load()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testLoad() {
        // TODO: Complete this test.
        //   Reason: R011 Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to access files outside the temporary directory (file 'defaultname.db', permission 'write').
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        VnllaPlayerInfo vnllaPlayerInfo = mock(VnllaPlayerInfo.class);
        when(vnllaPlayerInfo.getConfig()).thenReturn(new YamlConfiguration());
        (new SQLite(vnllaPlayerInfo)).load();
    }
}

