package otc.otc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.hutool.http.HttpUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import otc.otc.feign.AlipayServiceClien;
import otc.otc.feign.FileServiceClien;
import otc.otc.feign.QueueServiceClien;

/**
 * <p>核心定时任务类</p>
 * @author kent
 * @date 2020-3-31 23:53:00
 */
@Component
@Configuration
@EnableScheduling
public class Task {
	private static final Log log = LogFactory.get();
	@Autowired QueueServiceClien queueServiceClienImpl;
	@Autowired FileServiceClien fileServiceClienImpl;
	@Autowired AlipayServiceClien alipayServiceClienImpl;
	/**
	 * 每分钟踢人
	 */
	@Scheduled(cron = "0 */1 * * * ?")
	public void queue() {
		queueServiceClienImpl.task();
	}
	/**
	 * 每分钟踢人
	 */
	@Scheduled(cron = "*/10 * * * * ?")
	public void xianyuNotfiy() {
		log.info("执行踢咸鱼回调操作");
		HttpUtil.get("http://starpay168.com:5055/api-alipay/notfiy-api-pay/xianyu-scan-notfiy");
	}
	/**
	 * 凌晨一点执行任务
	 */
	@Scheduled(cron = "0 0 1 * * ?")
	public void userTask() {
		alipayServiceClienImpl.userTask();
	}
	/**
	 * 10秒图片剪裁
	 */
	@Scheduled(cron = "*/10 * * * * ?")
	public void file() {
		fileServiceClienImpl.task();
	}
}