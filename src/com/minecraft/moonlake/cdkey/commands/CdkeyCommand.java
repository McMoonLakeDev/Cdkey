package com.minecraft.moonlake.cdkey.commands;

import com.minecraft.moonlake.cdkey.CdkeyPlugin;
import com.minecraft.moonlake.cdkey.util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CdkeyCommand implements CommandExecutor {

	private final CdkeyPlugin main;
	
	public CdkeyCommand(CdkeyPlugin main) {
		// 构造函数
		this.main = main;
	}

	public CdkeyPlugin getMain() {

		return main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// 
		if(cmd.getName().equalsIgnoreCase("cdkey")) {
			// 兑换
			if(sender instanceof Player) {
				//
				Player player = (Player)sender;
				
				if(args.length != 1) {
					// 参数不够则帮助
					player.sendMessage(Util.pmsg("请使用 &6/cdkey &7<&a兑换码&7> &f将您的兑换码进行兑换."));
					return true;
				}
				String cdkey = args[0];
				
				if(cdkey.length() < 20 || !cdkey.substring(0, 2).equals("ML") || !String.valueOf(cdkey.charAt(cdkey.length() - 1)).matches("^([0-9])$")) {
					// 最低20位肯定不够
					player.sendMessage(Util.pmsg("&c错误,您的兑换码输入的格式不正确. &7(&a区分大小写&7)"));
					return true;
				}
				
				// 不忽略大小写
				if(getMain().getCdkey().onExistsCdkey(cdkey, false)) {
					// 存在说明没有使用则正常给予点券并将此兑换码列入过期表
					int point = Util.onCdkeyToDenomination(cdkey) * 100;
					getMain().getEconomy().getManager().givePoint(player.getName(), point);
					getMain().getCdkey().onPutCacheCdkey(cdkey, player.getName());
					Util.firework(player);
					player.sendMessage(Util.pmsg("兑换成功,已经将 &d" + point + " &f点券存入到您的账户."));
					player.sendMessage(Util.pmsg("十分感谢您的赞助,服务器将会越来越完美,祝您游戏愉快."));
				}
				else {
					// 已经使用
					player.sendMessage(Util.pmsg("&c错误,您的兑换码不正确或已被使用. &7(&a区分大小写&7)"));
				}
			}
			else {
				sender.sendMessage(Util.pmsg("控制台不能使用这个命令."));
			}
		}
		return true;
	}
}
