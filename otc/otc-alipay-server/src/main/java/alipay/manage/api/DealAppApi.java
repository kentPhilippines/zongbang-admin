package alipay.manage.api;

import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RestController;

import alipay.manage.api.config.FactoryForStrategy;
import alipay.manage.api.config.PayService;
import alipay.manage.bean.DealOrderApp;
import alipay.manage.bean.UserRate;
import alipay.manage.bean.Withdraw;
import alipay.manage.bean.util.DealBean;
import alipay.manage.bean.util.WithdrawalBean;
import alipay.manage.service.OrderAppService;
import alipay.manage.service.WithdrawService;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import otc.api.alipay.Common;
import otc.result.Result;
import otc.util.MapUtil;
import otc.util.number.Number;

/**
 * <p>商户请求交易接口</p>
 * @author hx08
 *
 */
@RestController
public class DealAppApi {
	@Autowired VendorRequestApi vendorRequestApi;
	Logger log = LoggerFactory.getLogger(DealAppApi.class);
	@Autowired FactoryForStrategy factoryForStrategy;
    @Autowired AccountApiService accountApiServiceImpl;
    @Autowired OrderAppService orderAppServiceImpl;
    @Autowired WithdrawService withdrawServiceImpl;
	/**
	 * <p>下游商户交易接口</p>
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Result dealAppPay(HttpServletRequest request) {
		Result pay = vendorRequestApi.pay(request);
		if(!pay.isSuccess())
			return pay;
		Object result = pay.getResult();
		DealBean mapToBean = MapUtil.mapToBean((Map<String, Object>)result, DealBean.class);
		String clientIP = HttpUtil.getClientIP(request);
		if(StrUtil.isNotBlank(clientIP))
			mapToBean.setIp(clientIP);
		String passcode = mapToBean.getPasscode(); //通道支付编码
		if(StrUtil.isBlank(passcode))
			return Result.buildFailMessage("通道编码为空");
		log.info("【当前通道编码："+passcode+"】");
		DealOrderApp dealBean = createDealAppOrder(mapToBean);
		if(ObjectUtil.isNull(dealBean))
			return Result.buildFailMessage("交易预订单生成出错");
		Result deal = null;
		try {
			 deal = factoryForStrategy.getStrategy(passcode).deal(dealBean, passcode);
		} catch (Exception e) {
			log.info("【当前通道编码对于的实体类不存在】");
			return Result.buildFailMessage("当前通道编码不存在");
		}
		if(ObjectUtil.isNull(deal))
			return Result.buildFailMessage("其他错误，请联系客服人员处理");
		return deal;
	}
	
	@SuppressWarnings("unchecked")
	public Result witOrder(HttpServletRequest request) {
		String manage = request.getParameter("manage");
		boolean flag = false;
		if(StrUtil.isNotBlank(manage))
			flag = true;
		Result withdrawal = vendorRequestApi.withdrawal(request, flag);
		if(!withdrawal.isSuccess())
			return withdrawal;
		Object result = withdrawal.getResult();
		WithdrawalBean wit = MapUtil.mapToBean((Map<String, Object>)result, WithdrawalBean.class);
		UserRate userRate = accountApiServiceImpl.findUserRateWitByUserId(wit.getAppid());
		Withdraw bean = createWit(wit,userRate);
        Result deal = null;
        if(ObjectUtil.isNull(bean))
        	return Result.buildFailMessage("代付订单生成失败");
        try {
			 deal = factoryForStrategy.getStrategy(userRate.getPayTypr()).withdraw(bean);
		} catch (Exception e) {
			log.info("【当前通道编码对于的实体类不存在】");
			return Result.buildFailMessage("当前通道编码不存在");
		}
        if(ObjectUtil.isNull(deal))
			return Result.buildFailMessage("其他错误，请联系客服人员处理");
		return deal;
	}
	
	
	
	private Withdraw createWit(WithdrawalBean wit, UserRate userRate) {
		Withdraw witb = new Withdraw();
		witb.setAmount(new BigDecimal(wit.getAmount()));
		witb.setFee(userRate.getFee());
		witb.setActualAmount(new BigDecimal(wit.getAmount()).subtract(userRate.getFee()));
		witb.setMobile(wit.getMobile());
		witb.setBankNo(wit.getAcctno());
		witb.setBankName(wit.getAcctname());
		witb.setWithdrawType(Common.Order.WIT_APP.toString());
		witb.setOrderId(Number.getWitOrder());
		witb.setOrderStatus(Common.Order.ORDER_STATUS_DISPOSE.toString());
		witb.setNotify(wit.getNotifyurl());
		boolean flag = withdrawServiceImpl.addOrder(witb);
		if(flag)
			return witb;
		return null;
	}

	DealOrderApp createDealAppOrder(DealBean dealBean){
		DealOrderApp dealApp = new DealOrderApp ();
		dealApp.setAppOrderId(dealBean.getOrderid());
		dealApp.setOrderId(Number.getAppOreder()); 
		dealApp.setNotify(dealBean.getNotifyurl());
		dealApp.setOrderAmount(new BigDecimal(dealBean.getAmount()));
		String userId = dealBean.getUserid();
		String passcode = dealBean.getPasscode();
		UserRate userRate = accountApiServiceImpl.findUserRateByUserId(userId, passcode);
		dealApp.setFeeId(userRate.getId());
		dealApp.setOrderAccount(userId);
		if(StrUtil.isNotBlank(dealBean.getIp()))
			dealApp.setOrderIp(dealBean.getIp());
		dealApp.setBack(dealBean.getPageUrl());
		dealApp.setOrderStatus(Common.Order.ORDER_STATUS_DISPOSE.toString());
		dealApp.setOrderType(Common.Order.ORDER_TYPE_DEAL);
		dealApp.setDealDescribe("下游商户发起充值交易");
		boolean add = orderAppServiceImpl.add(dealApp);
		if(add)
			return dealApp;
		return null;
	}
}