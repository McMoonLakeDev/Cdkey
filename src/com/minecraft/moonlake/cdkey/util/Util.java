package com.minecraft.moonlake.cdkey.util;

import com.minecraft.moonlake.encrypt.md5.MD5Encrypt;
import com.minecraft.moonlake.manager.RandomManager;
import com.minecraft.moonlake.util.StringUtil;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;

import java.util.Random;

public final class Util extends StringUtil {

	private final static Random RANDOM;

	static {

		RANDOM = RandomManager.getRandom();
	}

	private Util() {

	}
	
	public static String pmsg(String msg) {

		return toColor("&3MoonLake &8>> &f" + msg);
	}
	
	/**
	 * 生成随机CDKEY
	 * @param denomination 面值
	 * @return CDKEY
	 */
	public static String cdkey(int denomination) {

		MD5Encrypt md5Encrypt = new MD5Encrypt(RandomManager.getRandomUUID().toString().replaceAll("-", ""));
		String md5_str = md5Encrypt.encrypt().to32Bit();
		
		char[] chars = md5_str.toCharArray();
		
		for(int i = 0; i < chars.length; i++) {
			
			if(String.valueOf(chars[i]).matches("^([a-zA-Z]+)$")) {
				
				chars[i] = RANDOM.nextBoolean() ? chars[i] = Character.toUpperCase(chars[i]) : Character.toLowerCase(chars[i]);
			}
		}
		return String.valueOf("ML" + denomination + "V" + new String(chars) + RANDOM.nextInt(9));
	}
    
    public static int onCdkeyToDenomination(String cdkey) {
    	// 获取cdk的面值
    	int value = 0;
    	
    	try {
    		value = Integer.parseInt(cdkey.subSequence(cdkey.indexOf("L") + 1, cdkey.indexOf("V")).toString());
    	}
    	catch(Exception e) {

		}
    	return value;
    }
    
    public static void firework(Player player) {

		FireworkEffect fireworkEffect = FireworkEffect.builder()
				.withColor(Color.fromRGB(RANDOM.nextInt(255), RANDOM.nextInt(255), RANDOM.nextInt(255)))
				.with(FireworkEffect.Type.BALL)
				.withFlicker()
				.build();

		for(int i = 0; i < 3; i++) {

			Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
			firework.getFireworkMeta().addEffects(fireworkEffect);
			firework.getFireworkMeta().setPower(1);
		}
    }
}
