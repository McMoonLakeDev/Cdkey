package com.minecraft.moonlake.cdkey.commands;

import com.minecraft.moonlake.cdkey.CdkeyPlugin;
import com.minecraft.moonlake.cdkey.util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CdkeyopCommand implements CommandExecutor  {

private final CdkeyPlugin main;
	
	public CdkeyopCommand(CdkeyPlugin main) {
		// 构造函数
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// 
		if(cmd.getName().equalsIgnoreCase("cdkeyop")) {
			// 
			if(sender instanceof Player) {
				//
				Player player = (Player)sender;
				
				if(player.isOp() && player.hasPermission("moonlake.cdkey.admin")) {
					//
					if(args.length == 0) {
						// 提示帮助
						player.sendMessage(Util.pmsg("请使用 &6/cdkeyop help &f查看兑换码插件命令帮助."));
						return true;
					}
					else if(args.length == 1) {
						//
						if(args[0].equalsIgnoreCase("help")) {
							// 提示帮助
							player.sendMessage(Util.toColor(new String[] {

									"/cdkeyop cleancache <天数> - 将过期的兑换码进行清除按过期天数(0则清空.",
									"/cdkeyop create <面值> <数量> - 创建指定面值指定数量的兑换码加入数据库.",
							}));
						}
						else {
							player.sendMessage(Util.pmsg("请使用 &6/cdkeyop help &f查看兑换码插件命令帮助."));
							return true;
						}
					}
					else if(args.length == 2) {
						//
						if(args[0].equalsIgnoreCase("cleancache")) {
							// 清理垃圾
							if(args[1].matches("^([0-9]+)$")) {
								// 整数
								int oldDay = Integer.parseInt(args[1]);
								int amount = main.getCdkey().cleanCacheCdkey(oldDay);
								
								if(amount != -1) {
									// 
									if(amount == -2) {
										//
										player.sendMessage(Util.pmsg("成功清空数据库所有过期兑换码缓存."));
									}
									else {
										player.sendMessage(Util.pmsg("本次共清理掉 &d" + amount + " &f张过期兑换码."));
									}
								}
								else {
									// 异常
									player.sendMessage(Util.pmsg("错误,清理过期兑换码异常,详情请看控制台日志."));
								}
							}
							else {
								player.sendMessage(Util.pmsg("错误,参数1的天数只能为整数."));
							}
						}
					}
					else if(args.length == 3) {
						// 
						if(args[0].equalsIgnoreCase("create")) {
							// 创建
							int denomination = 0, amount = 0;
							
							try {
								denomination = Integer.parseInt(args[1]);
								amount = Integer.parseInt(args[2]);
							}
							catch(Exception e) {
								// 异常
								player.sendMessage(Util.pmsg("错误,面值和数量参数只能为正整数."));
								return true;
							}
							boolean result = main.getCdkey().onGenerateCdkey(denomination, amount);

							if(result) {
								//
								player.sendMessage(Util.pmsg("成功创建面值为 &b" + denomination + " &f的兑换码 &a" + amount + " &f条记录到数据库."));
							}
							else {
								// 异常
								player.sendMessage(Util.pmsg("错误,创建兑换码异常,详情请看控制台日志."));
							}
						}
					}
					else {
						player.sendMessage(Util.pmsg("请使用 &6/cdkeyop help &f查看兑换码插件命令帮助."));
						return true;
					}
				}
				else {
					//
					player.sendMessage(Util.pmsg("你没有使用这个命令的权限."));
				}
			}
			else {
				
			}
		}
		return true;
	}
}
