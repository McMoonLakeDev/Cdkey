package com.minecraft.moonlake.cdkey;

import com.minecraft.moonlake.cdkey.api.MoonLakeCdkey;
import com.minecraft.moonlake.cdkey.commands.CdkeyCommand;
import com.minecraft.moonlake.cdkey.commands.CdkeyopCommand;
import com.minecraft.moonlake.cdkey.sql.CdkeyMySQL;
import com.minecraft.moonlake.economy.EconomyPlugin;
import com.minecraft.moonlake.economy.api.MoonLakeEconomy;
import com.minecraft.moonlake.logger.MLogger;
import com.minecraft.moonlake.logger.MLoggerWrapped;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Created by MoonLake on 2016/10/1.
 */
public class CdkeyPlugin extends JavaPlugin {

    private MoonLakeCdkey mlcdkey;
    private MoonLakeEconomy economy;
    private final MLogger mLogger;

    public static CdkeyPlugin MAIN;

    public CdkeyPlugin() {

        this.mLogger = new MLoggerWrapped("MoonLakeCdkey");
    }

    @Override
    public void onEnable() {

        if(!this.setupMoonLakeEconomy()) {
            // 加载前置失败
            this.getMLogger().warn("前置月色之湖经济插件加载失败.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.initFolder();
        this.mlcdkey = new CdkeyMySQL(this);
        this.getCommand("cdkey").setExecutor(new CdkeyCommand(this));
        this.getCommand("cdkeyop").setExecutor(new CdkeyopCommand(this));
        this.getMLogger().info("月色之湖兑换码插件 v" + getDescription().getVersion() + " 成功加载.");
    }

    @Override
    public void onDisable() {

    }

    private boolean setupMoonLakeEconomy() {

        Plugin plugin = this.getServer().getPluginManager().getPlugin("MoonLakeEconomy");
        return plugin != null && plugin instanceof EconomyPlugin && (this.economy = ((EconomyPlugin)plugin).getInstance()) != null;
    }

    private void initFolder() {

        if(!getDataFolder().exists())
            getDataFolder().mkdir();
        File config = new File(getDataFolder(), "config.yml");
        if(!config.exists())
            saveDefaultConfig();
    }

    public final MoonLakeCdkey getCdkey() {

        return this.mlcdkey;
    }

    public final MoonLakeEconomy getEconomy() {

        return this.economy;
    }

    public MLogger getMLogger() {

        return mLogger;
    }
}
