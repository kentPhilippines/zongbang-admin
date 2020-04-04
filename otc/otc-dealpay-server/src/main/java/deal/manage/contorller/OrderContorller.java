package deal.manage.contorller;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import deal.manage.bean.DealOrder;
import deal.manage.bean.Recharge;
import deal.manage.bean.Runorder;
import deal.manage.bean.UserInfo;
import deal.manage.bean.Withdraw;
import deal.manage.bean.util.AreaIp;
import deal.manage.bean.util.PageResult;
import deal.manage.service.OrderService;
import deal.manage.service.UserInfoService;
import deal.manage.util.DateTools;
import deal.manage.util.EnterOrderUtil;
import deal.manage.util.IsDealIpUtil;
import deal.manage.util.LogUtil;
import deal.manage.util.OrderUtil;
import deal.manage.util.SessionUtil;
import otc.result.Result;

/**
 * <p>订单</p>
 * @author K
 *
 */
@Controller
@RequestMapping("/order")
public class OrderContorller {
	Logger log = LoggerFactory.getLogger(AgentContorller.class);
	@Autowired SessionUtil sessionUtil;
	@Autowired OrderService orderServiceImpl;
	@Autowired OrderUtil orderUtil;
	@Autowired LogUtil logUtil;
	@Autowired UserInfoService accountServiceImpl;
	private Lock lock = new ReentrantLock();
	/**
	 * <p>获取当前我，正在交易的订单</p>
	 * <p>若查询量过大，这里可能会造成服务器宕机</p>
	 * <p>查询量查询:  最大查询量为 ：  当前 可接单人数  如 当前可接单人数为  100 人 则可能造成  100个线程并发查询</p>
	 * @return
	 */
	@GetMapping("/findMyWaitReceivingOrder")
	@ResponseBody
	public Result findMyWaitReceivingOrder(HttpServletRequest request) {
		UserInfo user = sessionUtil.getUser(request);
		List<DealOrder> list = new ArrayList();
		/**
		 * <p>接口请求参数</p>
		 * 		accountId  : 码商 账户id
		 * <p>成功返回参数</p>
		 * JsonResult :
		 * 			code : 200
		 * 			success : true
		 * 			result : List<QrcodeDealOrder>
		 * 				<p>查询成功必传参数</p>
		 * 						orderId : 订单号
		 * 						dealAmount : 订单交易金额
		 * 						createTime : 订单创建时间
		 * 						dealType : 订单类型  (中文 ced:UTF-8)   例  alipay_qr:支付宝扫码
		 */					 
		return null;
	}
	@GetMapping("/findMyWaitConfirmOrder")
	@ResponseBody
	public Result findMyWaitConfirmOrder(HttpServletRequest request,String pageNum,String pageSize,String orderType, String dateTime) {
		UserInfo user = sessionUtil.getUser(request);
		if(ObjectUtil.isNull(user)) 
			return Result.buildFailMessage("当前用户未登录");
		Date date = null;
		if(StringUtils.isEmpty(dateTime))
			date = DateTools.parseStrToDate(DateTools.parseDateToStr(new Date(),DateTools.DATE_FORMAT_YYYY_MM_DD),DateTools.DATE_FORMAT_YYYY_MM_DD);
		else
			date = DateTools.parseStrToDate(dateTime,DateTools.DATE_FORMAT_YYYY_MM_DD);
		PageHelper.startPage(Integer.valueOf(pageNum), Integer.valueOf(pageSize));
		//按时间段查询
		List<DealOrder> listOrder = orderServiceImpl.findOrderByUser(user.getUserId(),orderType,DateTools.formatDateTime(DateTools.getDayBeginTime(date)),DateTools.formatDateTime(DateTools.getDayEndTime(new Date())));
		PageInfo<DealOrder> pageInfo = new PageInfo<DealOrder>(listOrder);
		PageResult<DealOrder> pageR = new PageResult<DealOrder>();
		pageR.setContent(pageInfo.getList());
		pageR.setPageNum(pageInfo.getPageNum());
		pageR.setTotal(pageInfo.getTotal());
		pageR.setTotalPage(pageInfo.getPages());
		return Result.buildSuccessResult(pageR);
	}
	@Autowired IsDealIpUtil isDealIpUtil;
	@Autowired EnterOrderUtil enterOrderUtil;
	@GetMapping("/userConfirmToPaid")
	@ResponseBody
	public Result userConfirmToPaid(HttpServletRequest request,String orderId) throws NumberFormatException, UnsupportedEncodingException {
		UserInfo user = sessionUtil.getUser(request);
		System.out.println("订单号收到款项,状态变更为成功："+orderId);
		log.info("【卡商手动置订单为成功："+user.getUserId()+"，订单号："+orderId+"】");
		AreaIp areaIp = isDealIpUtil.getAreaIp(request);
		DealOrder order = orderServiceImpl.findOrderByOrderId(orderId);
		Date createTime = order.getCreateTime();
		String msg = "卡商手动置交易订单为成功，当前交易订单为："+order.getOrderId()+"，当前订单金额为："+order.getDealAmount()+",操作ip为:"+areaIp.getIp();
		logUtil.addLog(request, msg, user.getUserId());
		boolean expired = DateUtil.isExpired(createTime,DateField.SECOND,1200,new Date());
		if(!expired)
			return Result.buildFailResult("无权限，请联系客服人员操作");
		Result enterOrderSu = enterOrderUtil.EnterOrderSu(orderId, user.getUserId(), areaIp.getIp());
		if(enterOrderSu.isSuccess()) {
			return enterOrderSu;
		}
		return Result.buildFailResult("无权限，请联系客服人员操作");
	}
	@GetMapping("/findMyReceiveOrderRecordByPage")
	@ResponseBody
	@Transactional
	public Result findMyReceiveOrderRecordByPage(HttpServletRequest request,String receiveOrderTime,String pageNum,String pageSize,String gatheringChannelCode) {
		UserInfo user = sessionUtil.getUser(request);
		DealOrder order = new DealOrder();
		if(ObjectUtil.isNull(user)) {
			return Result.buildFailMessage("当前用户未登录");
		}
		order.setOrderQrUser(user.getUserId());
		if(StrUtil.isNotBlank(receiveOrderTime)) 
			order.setTime(receiveOrderTime);
		if(StrUtil.isNotBlank(gatheringChannelCode))
			order.setOrderType(gatheringChannelCode);
		PageHelper.startPage(Integer.valueOf(pageNum), Integer.valueOf(pageSize));
		List<DealOrder> orderList = orderServiceImpl.findOrderByPage(order);
		PageInfo<DealOrder> pageInfo = new PageInfo<DealOrder>(orderList);
		PageResult<DealOrder> pageR = new PageResult<DealOrder>();
		pageR.setContent(pageInfo.getList());
		pageR.setPageNum(pageInfo.getPageNum());
		pageR.setTotal(pageInfo.getTotal());
		pageR.setTotalPage(pageInfo.getPages());
		return Result.buildSuccessResult(pageR);
	}
	@GetMapping("/enterOrderEr")
	@ResponseBody
	@Transactional
	public Result enterOrderEr(HttpServletRequest request ,String orderId) throws NumberFormatException, UnsupportedEncodingException {
		UserInfo user = sessionUtil.getUser(request);
		DealOrder order = new DealOrder();
		if(ObjectUtil.isNull(user)) {
			return Result.buildFailMessage("当前用户未登录");
		}
		System.out.println("订单号收到款项,状态变更为失败："+orderId);
		log.info("【卡商手动置订单为失败："+user.getUserId()+"，订单号："+orderId+"】");
		AreaIp areaIp = isDealIpUtil.getAreaIp(request);
		DealOrder findOrderByOrderId = orderServiceImpl.findOrderByOrderId(orderId);
		String msg = "卡商手动置交易订单为失败，当前交易订单为："+findOrderByOrderId.getOrderId()+"，当前订单金额为："+findOrderByOrderId.getDealAmount()+",操作ip为:"+areaIp.getIp();
		logUtil.addLog(request, msg, user.getUserId());
		Result enterOrderSu = Result.buildFail();
		lock.lock();
		try {
			enterOrderSu = enterOrderUtil.CardAppEnterOrderEr(orderId, user.getUserId(), areaIp.getIp());
		} finally {
			lock.unlock();
		}  
		if(enterOrderSu.isSuccess()) {
			return enterOrderSu;
		}
		return Result.buildFailResult("无权限，请联系客服人员操作");
	}
	/**
	 * <p>获取个人流水</p>
	 * @param request
	 * @param startTime
	 * @param pageNum
	 * @param pageSize
	 * @param accountChangeTypeCode
	 * @return
	 */
	@GetMapping("/findMyAccountChangeLogByPage")
	@ResponseBody
	public Result findMyAccountChangeLogByPage(HttpServletRequest request,String startTime,
			String pageNum,String pageSize,String accountChangeTypeCode) {
		UserInfo user = sessionUtil.getUser(request);
		Runorder order = new Runorder();
		if(ObjectUtil.isNull(user)) {
			return Result.buildFailMessage("当前用户未登录");
		}
		order.setOrderAccount(user.getUserId());
		if(StrUtil.isNotBlank(startTime)) 
			order.setTime(startTime);
		if(StrUtil.isNotBlank(accountChangeTypeCode))
			order.setRunType(accountChangeTypeCode);
		PageHelper.startPage(Integer.valueOf(pageNum), Integer.valueOf(pageSize));
		List<Runorder> orderList = orderServiceImpl.findOrderRunByPage(order);
		PageInfo<Runorder> pageInfo = new PageInfo<Runorder>(orderList);
		PageResult<Runorder> pageR = new PageResult<Runorder>();
		pageR.setContent(pageInfo.getList());
		pageR.setPageNum(pageInfo.getPageNum());
		pageR.setTotal(pageInfo.getTotal());
		pageR.setTotalPage(pageInfo.getPages());
		return Result.buildSuccessResult(pageR);
	}
	/**
	 * <p>申诉</p>
	 * @param request
	 * @param appealType
	 * @param actualPayAmount
	 * @param userSreenshotIds
	 * @param merchantOrderId
	 * @return
	 */
	@PostMapping("/userStartAppeal")
	@ResponseBody
	public Result userStartAppeal(HttpServletRequest request,String appealType,String actualPayAmount,String userSreenshotIds,String merchantOrderId) {
		UserInfo user = sessionUtil.getUser(request);
		Runorder order = new Runorder();
		if(ObjectUtil.isNull(user)) {
			return Result.buildFailMessage("当前用户未登录");
		}
		return  null;
	}
	@GetMapping("/findLowerLevelAccountChangeLogByPage")
	@ResponseBody
	public Result findLowerLevelAccountChangeLogByPage(
			HttpServletRequest request,
			String startTime,
			String pageNum,
			String pageSize,
			String accountChangeTypeCode,
			String userName
			) {
		UserInfo user = sessionUtil.getUser(request);
		if(ObjectUtil.isNull(user)) 
			return Result.buildFailMessage("当前用户未登录");
		List<String> userList =  accountServiceImpl.findSunAccountByUserId(user.getUserId());	
		if(StrUtil.isNotBlank(userName)) {
			if(!userList.contains(userName)) {
				return Result.buildFailMessage("输入账户号有误！");
			}
			userList.clear();
			userList.add(userName);
		} 
		userList.add(user.getUserId());
		Runorder orderRun = new Runorder();
		orderRun.setOrderAccountList(userList);
		if(StrUtil.isNotBlank(startTime))
			orderRun.setTime(startTime);
		if(StrUtil.isNotBlank(accountChangeTypeCode))
			orderRun.setRunType(accountChangeTypeCode);
		PageHelper.startPage(Integer.valueOf(pageNum), Integer.valueOf(pageSize));
		List<Runorder> orderList = orderServiceImpl.findOrderRunByPage(orderRun);
		PageInfo<Runorder> pageInfo = new PageInfo<Runorder>(orderList);
		PageResult<Runorder> pageR = new PageResult<Runorder>();
		pageR.setContent(pageInfo.getList());
		pageR.setPageNum(pageInfo.getPageNum());
		pageR.setTotal(pageInfo.getTotal());
		pageR.setTotalPage(pageInfo.getPages());
		return Result.buildSuccessResult(pageR);
	}
	@GetMapping("/findLowerLevelAccountReceiveOrderRecordByPage")
	@ResponseBody
	public Result findLowerLevelAccountReceiveOrderRecordByPage(
			HttpServletRequest request,
			String startTime,
			String pageNum,
			String pageSize,
			String accountChangeTypeCode,
			String userName,
			String orderState
			) {
		UserInfo user = sessionUtil.getUser(request);
		if(ObjectUtil.isNull(user)) 
			return Result.buildFailMessage("当前用户未登录");
		List<String> userList =  accountServiceImpl.findSunAccountByUserId(user.getUserId());	
		if(StrUtil.isNotBlank(userName)) {
			if(!userList.contains(userName)) 
				return Result.buildFailMessage("输入账户号有误！");
			userList.clear();
			userList.add(userName);
		} 
		userList.add(user.getUserId());
		System.out.println("子账户："+userList.toString());
		DealOrder order = new DealOrder();
		if(StrUtil.isNotBlank(startTime)) 
			order.setTime(startTime);
		if(StrUtil.isNotBlank(accountChangeTypeCode))
			order.setOrderType(accountChangeTypeCode);
		if(StrUtil.isNotBlank(orderState))
			order.setOrderStatus( orderState);
		order.setOrderQrUserList(userList); 
		PageHelper.startPage(Integer.valueOf(pageNum), Integer.valueOf(pageSize));
		List<DealOrder> orderList = orderServiceImpl.findOrderByPage(order);
		PageInfo<DealOrder> pageInfo = new PageInfo<DealOrder>(orderList);
		PageResult<DealOrder> pageR = new PageResult<DealOrder>();
		pageR.setContent(pageInfo.getList());
		pageR.setPageNum(pageInfo.getPageNum());
		pageR.setTotal(pageInfo.getTotal());
		pageR.setTotalPage(pageInfo.getPages());
		return Result.buildSuccessResult(pageR);
	}
	/**
	 * <p>获取个人流水</p>
	 * @param request
	 * @param startTime
	 * @param pageNum
	 * @param pageSize
	 * @param accountChangeTypeCode
	 * @return
	 */
	@GetMapping("/findMyRechargeWithdrawLogByPage")
	@ResponseBody
	public Result findMyRechargeWithdrawLogByPage(HttpServletRequest request,
			String startTime,
			String pageNum,
			String pageSize,
			String orderType
			) {
		UserInfo user = sessionUtil.getUser(request);
		if(ObjectUtil.isNull(user)) 
			return Result.buildFailMessage("当前用户未登录");
		if(StrUtil.isBlank(orderType))
			orderType = "1";
		if(orderType.equals("1")) {//充值
			Recharge bean = new Recharge();
			bean.setUserId(user.getUserId());
			if(StrUtil.isNotBlank(startTime))
				bean.setTime(startTime);
			PageHelper.startPage(Integer.valueOf(pageNum), Integer.valueOf(pageSize));
			List<Recharge> witList = orderServiceImpl.findRechargeOrder(bean);
			PageInfo<Recharge> pageInfo = new PageInfo<Recharge>(witList);
			PageResult<Recharge> pageR = new PageResult<Recharge>();
			pageR.setContent(pageInfo.getList());
			pageR.setPageNum(pageInfo.getPageNum());
			pageR.setTotal(pageInfo.getTotal());
			pageR.setTotalPage(pageInfo.getPages());
			return Result.buildSuccessResult(pageR);
		}else {//提现
			Withdraw bean = new Withdraw();
			bean.setUserId(user.getUserId());
			if(StrUtil.isNotBlank(startTime))
				bean.setTime(startTime);
			PageHelper.startPage(Integer.valueOf(pageNum), Integer.valueOf(pageSize));
			List<Withdraw> witList = orderServiceImpl.findWithdrawOrder(bean);
			PageInfo<Withdraw> pageInfo = new PageInfo<Withdraw>(witList);
			PageResult<Withdraw> pageR = new PageResult<Withdraw>();
			pageR.setContent(pageInfo.getList());
			pageR.setPageNum(pageInfo.getPageNum());
			pageR.setTotal(pageInfo.getTotal());
			pageR.setTotalPage(pageInfo.getPages());
			return Result.buildSuccessResult(pageR);
		}
	}
}