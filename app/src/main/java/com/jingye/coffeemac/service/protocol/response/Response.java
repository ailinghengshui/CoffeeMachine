package com.jingye.coffeemac.service.protocol.response;

import com.jingye.coffeemac.service.protocol.LinkFrame;
import com.jingye.coffeemac.service.protocol.ResponseCode;
import com.jingye.coffeemac.service.protocol.pack.AutoPackHelper;
import com.jingye.coffeemac.service.protocol.pack.Unpack;


/**
 * 响应 User: jingege Date: 1/13/12 Time: 4:06 PM
 */
public abstract class Response extends AutoPackHelper{

	/**
	 * LinkFrame
	 */
	protected LinkFrame linkFrame;

	/**
	 * 解包
	 * 
	 * @param unpack
	 *            待解
	 * @throws Exception
	 *             解包失败
	 * @return 在需要2次解包时,把需要解压的数据作为返回值
	 */
	public abstract Unpack unpackBody(Unpack unpack) throws Exception;

	/**
	 * 获取LinkFrame
	 * 
	 * @return
	 */
	public LinkFrame getLinkFrame() {
		return this.linkFrame;
	}

	/**
	 * 设置LinkFrame
	 * 
	 * @param linkFrame
	 */
	public void setLinkFrame(LinkFrame linkFrame) {
		this.linkFrame = linkFrame;
	}

	/**
	 * code是否为SUCCESS
	 * @return
	 */
	public boolean isSuccess() {
		if (linkFrame != null) {
			return linkFrame.resCode == ResponseCode.RES_SUCCESS;
		}
		return false;
	}
	
	public boolean isOfflineMsg() {
		if (linkFrame != null) {
			return linkFrame.serialId == 0;
		}
		
		return false;
	}

}
