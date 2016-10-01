package com.minecraft.moonlake.cdkey.api;

import com.minecraft.moonlake.cdkey.data.CacheCdkeyInfo;

public interface MoonLakeCdkey {

	/**
	 * 生成指定面额的cdkey到数据库
	 * @param denomination 面值
	 * @param amount 生成的数量
	 * @return 是否成功
	 */
	boolean onGenerateCdkey(int denomination, int amount);
	
	/**
	 * 兑换码在数据库是否存在
	 * @param cdkey 兑换码
	 * @param ignoreCase 是否忽略大小写
	 * @return 是否存在
	 */
	boolean onExistsCdkey(String cdkey, boolean ignoreCase);
	
	/**
	 * 将指定兑换码放入到过期表
	 * @param cdkey 兑换码
	 * @param user 使用者
	 * @return 是否成功
	 */
	boolean onPutCacheCdkey(String cdkey, String user);
	
	/**
	 * 兑换码在过期数据库是否存在
	 * @param cdkey 兑换码
	 * @return 是否存在
	 */
	boolean isCacheCdkey(String cdkey);
	
	/**
	 * 将过期数据库的兑换码清洁
	 * @param oldDay 需要清理的系统日期减去参数天数以上的兑换码
	 * @return 清理的数量,如果为-1代表清理失败
	 */
	int cleanCacheCdkey(int oldDay);
	
	/**
	 * 获取过期兑换码的信息数据
	 * @param cdkey 兑换码
	 * @return 兑换码信息
	 */
	CacheCdkeyInfo getCacheCdkeyInfo(String cdkey);
}
