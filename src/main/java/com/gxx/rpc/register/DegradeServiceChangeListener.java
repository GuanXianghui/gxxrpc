package com.gxx.rpc.register;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.apache.log4j.Logger;

import com.gxx.rpc.utils.ServiceDegradeUtil;

/** 
 * 降级服务变化监听类
 * @author Gxx
 */
public class DegradeServiceChangeListener implements IZkChildListener {

	/**
	 * 日志处理器
	 */
	Logger logger = Logger.getLogger(DegradeServiceChangeListener.class);
	
	/**
	 * 同步最新降级服务
	 */
	@Override
	public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
		logger.info("[降级服务监听]到变动，目标目录:[" + parentPath + "]，子元素:[" + currentChilds + "]");
		/**
		 * 同步最新引用服务
		 */
		ServiceDegradeUtil.synDegradeService();
	}

}
