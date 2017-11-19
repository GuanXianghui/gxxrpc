package com.gxx.rpc.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * 异步执行任务工具类
 * 
 * @author Gxx
 */
public final class AsynUtils {
	/**
	 * 单例模式
	 */
	private static AsynUtils instance = new AsynUtils();

	/**
	 * 进行异步任务列表
	 */
	private List<FutureTask<Object>> futureTasks = new ArrayList<FutureTask<Object>>();

	/**
	 * 线程池 暂时约定初始化5个线程 和JDBC连接池是一个意思 实现重用
	 */
	private ExecutorService executorService = Executors.newFixedThreadPool(5);

	public static AsynUtils getInstance() {
		return instance;
	}

	/**
	 * 异步执行任务-提交任务
	 */
	public FutureTask<Object> submitTask(Callable<Object> callable) {
		/**
		 * 创建一个异步任务
		 */
		FutureTask<Object> futureTask = new FutureTask<Object>(callable);
		futureTasks.add(futureTask);
		/**
		 * 提交异步任务到线程池，让线程池管理任务 由于是异步并行任务，所以这里并不会阻塞 注意：一旦提交，线程池如果有可用线程，马上分配执行！
		 */
		executorService.submit(futureTask);
		return futureTask;
	}
}