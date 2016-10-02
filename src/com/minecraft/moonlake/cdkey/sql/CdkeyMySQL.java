package com.minecraft.moonlake.cdkey.sql;

import com.minecraft.moonlake.cdkey.CdkeyPlugin;
import com.minecraft.moonlake.cdkey.api.MoonLakeCdkey;
import com.minecraft.moonlake.cdkey.data.CacheCdkeyInfo;
import com.minecraft.moonlake.cdkey.util.Util;
import com.minecraft.moonlake.mysql.MySQLConnection;
import com.minecraft.moonlake.mysql.MySQLFactory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class CdkeyMySQL implements MoonLakeCdkey {

	private final CdkeyPlugin main;
	private final PluginDescriptionFile pdf;
	private String database;
	private String host;
	private int port;
	private String username;
	private String password;
	private String table;
	private String tableCache;
	private MySQLConnection mySQLConnection;

	public CdkeyMySQL(CdkeyPlugin main) {

		this.main = main;
		this.pdf = main.getDescription();
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "config.yml"));
		this.database = yc.getString("MySQL.mySQLDatabase");
		this.host = yc.getString("MySQL.mySQLHost");
		this.port = yc.getInt("MySQL.mySQLPort");
		this.username = yc.getString("MySQL.mySQLUsername");
		this.password = yc.getString("MySQL.mySQLPassword");
		this.table = yc.getString("MySQL.mySQLTableName");
		this.tableCache = yc.getString("MySQL.mySQLTableCacheName");

		this.mySQLConnection = MySQLFactory.get().connection(host, port, username, password);
		this.init();
	}

	public CdkeyPlugin getMain() {

		return main;
	}

	public MySQLConnection getConnection() {

		return mySQLConnection;
	}

	private void init() {

		MySQLConnection mySQLConnection = getConnection();

		try {

			mySQLConnection.setDatabase("mysql", true);
			mySQLConnection.dispatchStatement("create database if not exists " + database);

			mySQLConnection.setDatabase(database, true);
			mySQLConnection.dispatchStatement(

					"create table if not exists " + tableCache + " (" +
					"id integer not null auto_increment," +
					"cdkey varchar(32) not null unique," +
					"date varchar(10) not null," +
					"user varchar(20) not null" +
					"primary key (id));"
			);
		}
		catch (Exception e) {

			getMain().getMLogger().error("初始化月色之湖经济数据库的数据表时异常: " + e.getMessage());
		}
		finally {

			mySQLConnection.dispose();
		}
	}


	@Override
	public boolean onGenerateCdkey(int denomination, int amount) {

		MySQLConnection mySQLConnection = getConnection();

		try {

			String tableName = Util.messageFormat(table, denomination);

			mySQLConnection.setDatabase(database, true);
			mySQLConnection.dispatchStatement(

					"create table if not exists " + tableName + " (" +
					"id integer not null auto_increment," +
					"cdkey varchar(32) not null unique," +
					"date varchar(10) not null," +
					"primary key (id));"
			);
			String fileName = Util.messageFormat("{0}-[{1}].txt", Util.getSystemTime("yyyy-MM-dd-hh-mm-ss"), denomination);
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(getMain().getDataFolder(), fileName)));

			for(int i = 0; i < amount; i++) {

				String cdkey = Util.cdkey(denomination);

				Object value = mySQLConnection.findSimpleResult("cdkey", "select cdkey from " + tableName + " where binary `cdkey`=?;", cdkey);

				if(value == null) {

					mySQLConnection.dispatchPreparedStatement("insert into " + tableName + " (cdkey,data) values (?,?);", cdkey, Util.getSystemTime("yyyy-MM-dd"));

					bufferedWriter.write(cdkey);
					bufferedWriter.newLine();
				}
			}
			bufferedWriter.flush();
			bufferedWriter.close();

			getMain().getMLogger().info("成功生成数据面值为 '" + denomination + "' 的兑换码 '" + amount + "' 条记录.");

			return true;
		}
		catch (Exception e) {

			getMain().getMLogger().error("生成数据面值为 '" + denomination + "' 发生异常: " + e.getMessage());
		}
		finally {

			mySQLConnection.dispose();
		}
		return false;
	}

	@Override
	public boolean onExistsCdkey(String cdkey, boolean ignoreCase) {

		int denomination = Util.onCdkeyToDenomination(cdkey);

		MySQLConnection mySQLConnection = getConnection();

		try {

			String tableName = Util.messageFormat(table, denomination);

			mySQLConnection.setDatabase(database, true);

			boolean result = mySQLConnection.findTable(tableName);

			if(result) {

				Object value = mySQLConnection.findSimpleResult("cdkey", "select cdkey from " + tableName + " where binary `cdkey`=?;", cdkey);

				if(value != null) {

					String sql_cdkey = (String) value;
					return ignoreCase ? cdkey.equalsIgnoreCase(sql_cdkey) : cdkey.equals(sql_cdkey);
				}
			}
		}
		catch (Exception e) {

			getMain().getMLogger().error("查找数据面值为 '" + denomination + "' 发生异常: " + e.getMessage());
		}
		finally {

			mySQLConnection.dispose();
		}
		return false;
	}

	@Override
	public boolean onPutCacheCdkey(String cdkey, String user) {

		int denomination = Util.onCdkeyToDenomination(cdkey);

		MySQLConnection mySQLConnection = getConnection();

		try {

			String tableName = Util.messageFormat(table, denomination);

			mySQLConnection.setDatabase(database, true);
			mySQLConnection.dispatchPreparedStatement("delete from " + tableName + " where binary `cdkey`=?;", cdkey);
			mySQLConnection.dispatchPreparedStatement("insert into " + tableCache + " (cdkey,date,user) values (?,?,?);", cdkey, Util.getSystemTime(), user);

			return true;
		}
		catch (Exception e) {

			getMain().getMLogger().error("加入过期数据使用者为 '" + user + "' 时发生异常: " + e.getMessage());
		}
		finally {

			mySQLConnection.dispose();
		}
		return false;
	}

	@Override
	public boolean isCacheCdkey(String cdkey) {

		int denomination = Util.onCdkeyToDenomination(cdkey);

		MySQLConnection mySQLConnection = getConnection();

		try {

			mySQLConnection.setDatabase(database, true);

			Object value = mySQLConnection.findSimpleResult("cdkey", "select cdkey from " + tableCache + " where binary `cdkey`=?;", cdkey);

			return value != null;
		}
		catch (Exception e) {

			getMain().getMLogger().error("查找数据面值为 '" + denomination + "' 发生异常: " + e.getMessage());
		}
		finally {

			mySQLConnection.dispose();
		}
		return false;
	}
	
	@Override
	public int cleanCacheCdkey(int oldDay) {

		MySQLConnection mySQLConnection = getConnection();

		try {

			mySQLConnection.setDatabase(database, true);

			if(oldDay == 0) {

				mySQLConnection.dispatchStatement("delete from " + tableCache);
				return -2;
			}
			int cleanAmount = 0;
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date now = dateFormat.parse(Util.getSystemTime("yyyy-MM-dd"));

			Set<Map<String, Object>> resultSet = mySQLConnection.findResults("select cdkey,date from " + tableCache);

			if(resultSet != null && resultSet.size() > 0) {

				for(Map<String, Object> result : resultSet) {

					if(result != null && result.size() > 0) {

						Date cdkeyDate = dateFormat.parse((String) result.get("date"));
						int day = (int)((now.getTime() - cdkeyDate.getTime()) / 86400000);

						if(day >= oldDay) {

							mySQLConnection.dispatchPreparedStatement("delete from " + tableCache + " where binary `cdkey`=?;", result.get("cdkey"));
							cleanAmount++;
						}
					}
				}
			}
			return cleanAmount;
		}
		catch (Exception e) {

			getMain().getMLogger().error("清理缓存过期兑换码时发生异常: " + e.getMessage());
		}
		finally {

			mySQLConnection.dispose();
		}
		return -1;
	}
	
	@Override
	public CacheCdkeyInfo getCacheCdkeyInfo(String cdkey) {

		return null;
	}
}
