package alipay.config.task;

import alipay.config.redis.RedisUtil;
import alipay.manage.bean.DealOrder;
import alipay.manage.bean.RunOrder;
import alipay.manage.mapper.DealOrderMapper;
import alipay.manage.service.RunOrderService;
import alipay.manage.util.OrderUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import otc.result.Result;

import javax.annotation.Resource;
import java.util.List;


@Component
public class OrderTask {
	static final String KEY = "TASK:ORDER:ST:";
	/**
	 * <p>定时任务修改订单状态</p>
	 */
	private static final Log log = LogFactory.get();
	@Autowired
	RunOrderService RunOrderServiceimpl;
	@Resource
	private DealOrderMapper dealOrderDao;
	@Autowired
	private OrderUtil orderUtilImpl;
	@Autowired
	private RedisUtil redis;
	@Autowired
	private alipay.config.redis.RedisLockUtil RedisLockUtil;

	/**
	 * 每十秒结算一次
	 */
	public void orderTask() {
		RedisLockUtil.redisLock(KEY + "lock");
		List<DealOrder> orderList = dealOrderDao.findSuccessAndNotAmount();
		for (DealOrder order : orderList) {
			if (redis.hasKey(KEY + order.getOrderId())) {
				log.info("当前订单已处理");
				continue;
			}
			;
			redis.set(KEY + order.getOrderId(), order.getOrderId(), 20); //防止多个任务同时获取一个订单发起结算
			try {
				List<RunOrder> runOrderList = RunOrderServiceimpl.findAssOrder(order.getOrderId());
				List<RunOrder> runOrderList1 = RunOrderServiceimpl.findAssOrder(order.getAssociatedId());
				if (CollUtil.isNotEmpty(runOrderList) || CollUtil.isNotEmpty(runOrderList1)) {
					dealOrderDao.updateSuccessAndAmount(order.getOrderId());
					continue;
				}
				Result settlement = orderUtilImpl.settlement(order);
				if (settlement.isSuccess()) {
					ThreadUtil.execute(() -> {
						dealOrderDao.updateSuccessAndAmount(order.getOrderId());
					});

				}

			} catch (Exception e) {
				log.info("【异步结算发生异常：" + order.getOrderId() + "】");
				log.info("【异步结算发生异常：" + e.getMessage() + "】");
			} finally {
				RedisLockUtil.unLock(KEY + "lock");
			}

		}

	}


}
