package alipay.manage.util;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import alipay.manage.bean.Amount;
import alipay.manage.bean.DealOrder;
import alipay.manage.bean.Recharge;
import alipay.manage.bean.RunOrder;
import alipay.manage.bean.UserFund;
import alipay.manage.bean.UserRate;
import alipay.manage.bean.Withdraw;
import alipay.manage.service.RunOrderService;
import alipay.manage.service.UserInfoService;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import otc.api.alipay.Common;
import otc.result.Result;

/**
 * <p>资金流水处理类</p>
 * @author hx08
 *
 */
@Component
public class AmountRunUtil {
	/**
	 * ######################################
		<p>流水处理接口</p>
		1，人工加款接口				已完成
		2，人工扣款接口				已完成
		3，交易减少充值点数接口		已完成
		4，交易增加交易分润接口		已完成
		5，充值						已完成
		6，提现						已完成
	 */
	Logger log = LoggerFactory.getLogger(AmountRunUtil.class);
	private static final String SYSTEM_APP = "SYSTEM_APP";//系统账户
	@Autowired UserInfoService userInfoServiceImpl;
	@Autowired RunOrderService runOrderServiceImpl;
	@Autowired AmountUtil amountUtil;
	private static final String RUN = "RUN";
	private static final String ADD_AMOUNT = "ADD_AMOUNT";//人工加钱
	private static final Integer ADD_AMOUNT_NUMBER = 17;
	private static final String DETETE_AMOUNT = "DETETE_AMOUNT";//人工扣钱
	private static final Integer DETETE_AMOUNT_NUMBER = 18;//人工扣钱
	private static final String DEAL_AMOUNT_DETETE = "DEAL_AMOUNT_DETETE";//交易时交易点数扣除
	private static final Integer DEAL_AMOUNT_DETETE_NUMBER = 12;//交易时交易点数扣除
	private static final String PROFIT_AMOUNT_DEAL = "PROFIT_AMOUNT_DEAL";//自己账号交易时产生的分润
	private static final String PROFIT_AMOUNT_AGENT = "PROFIT_AMOUNT_AGENT";//自己作为代理商的分润
	private static final Integer PROFIT_AMOUNT_AGENT_NUMBER = 13;//自己作为代理商的分润
	private static final Integer PROFIT_AMOUNT_DEAL_NUMBER = 14;//自己账号交易时产生的分润
	private static final String RECHANGE_AMOUNT = "RECHANGE_AMOUNT";//充值 及 增加交易点数
	private static final Integer RECHANGE_AMOUNT_NUMBER = 11;//充值 及  增加交易点数
	private static final String WITHDRAY_AMOUNT = "WITHDRAY_AMOUNT";//代付冻结
	private static final String WITHDRAY_AMOUNT_FEE = "WITHDRAY_AMOUNT_FEE";//代付手续费冻结
	private static final Integer WITHDRAY_AMOUNT_NUMBER = 10;//代付冻结
	private static final Integer WITHDRAY_AMOUNT_FEE_NUMBER = 9;//代付手续费冻结
	
	private static final String AMOUNT_TYPE_R = "0";//对于当前账户来说是   收入
	private static final String AMOUNT_TYPE_W = "1";//对于当前账户来说是   支出
	
	private static final String RUNTYPE_ARTIFICIAL = "2";//人工流水
	private static final String RUNTYPE_NATURAL = "1";//自然流水
	/**
	 * <p>码商代付流水生成</p>
	 * @param withdraw									代付订单表
	 * @param generationIp								ip
	 * @param flag										true 自然流水     false  人工流水
	 * @return
	 */
	public Result deleteAmount(Withdraw withdraw,String generationIp,Boolean flag) {
		UserFund userFund = userInfoServiceImpl.findUserFundByAccount(withdraw.getUserId()); //当前账户资金
		Result delete = delete(WITHDRAY_AMOUNT, userFund, withdraw.getOrderId(), withdraw.getActualAmount(), generationIp, "码商代付冻结",  flag?RUNTYPE_ARTIFICIAL:RUNTYPE_NATURAL);
		if(delete.isSuccess())
			return delete;
		return Result.buildFailMessage("流水生成失败");
	}
	/**
	 * <p>充值</p>
	 * @param recharge		充值订单
	 * @param generationIp	充值ip
	 * @param flag			true 自然流水     false  人工流水
	 * @return
	 */
	public Result addAmount(Recharge recharge,String generationIp,Boolean flag) {
		UserFund userFund = userInfoServiceImpl.findUserFundByAccount(recharge.getUserId()); //当前账户资金
		Result add = add(RECHANGE_AMOUNT, userFund, recharge.getOrderId(), recharge.getActualAmount(), generationIp, "码商充值",  flag?RUNTYPE_ARTIFICIAL:RUNTYPE_NATURAL);
		if(add.isSuccess())
			return add;
		return Result.buildFailMessage("流水生成失败");
	}
	/**
	 * <p>代理商分润计算</p>
	 * @param orderId				分润产生订单号
	 * @param userId				分润产生账户
	 * @param amount				分润产生金额
	 * @param feeId					费率id
	 * @param generationIp			产品分润ip	
	 * @param flag					true 自然流水     false  人工流水
	 * @return
	 */
	public Result addAmountProfit(String orderId ,String userId ,BigDecimal amount ,  Integer feeId  , String generationIp ,Boolean flag ) {
		UserFund userFund = userInfoServiceImpl.findUserFundByAccount(userId); //当前账户资金
		UserRate userFee = userInfoServiceImpl.findUserRateById(feeId);//当前账户 费率
		if(StrUtil.isBlank(userFund.getAgent()))
			return Result.buildSuccessMessage("当前分润以结算完成");
		UserFund userAccount = userInfoServiceImpl.findUserFundByAccount(userFund.getAgent());//当前账户上级代理账户
		UserRate agentFee = userInfoServiceImpl.findUserRateById(feeId);
		String userId2 = userFund.getUserId();
		BigDecimal fee = userFee.getFee();
		log.info("【流水关联订单号为："+orderId+"】");
		log.info("【当前账户为："+userId2+"】");
		log.info("【当前费率为："+fee+"】");
		String userId3 = userAccount.getUserId();
		BigDecimal fee2 = agentFee.getFee();
		log.info("【上级代理商账户为："+userId3+"】");
		log.info("【上级代理商费率为："+fee2+"】");
		BigDecimal subtract = fee2.subtract(fee);
		log.info("【当前费率差为："+subtract+"】");
		BigDecimal multiply = amount.multiply(subtract);
		Result addAmounProfit = amountUtil.addAmounProfit(userAccount, multiply);
		if(addAmounProfit.isSuccess())
			return Result.buildFailMessage("资金账户修改失败");
		log.info("【当前代理商："+userId3+"，结算分润为："+multiply+"】");
		Result add = add(PROFIT_AMOUNT_AGENT, userAccount, orderId, multiply, generationIp, "码商代理商，代理分润结算", flag?RUNTYPE_ARTIFICIAL:RUNTYPE_NATURAL);
		if(add.isSuccess()) 
			return addAmountProfit(orderId, userId3, amount, feeId,  generationIp, flag);
		else
			return Result.buildFailMessage("代理商分润结算失败");
	}
	/**
	 * <p>码商会员正常交易分润流水</p>
	 * @param order					交易订单
	 * @param generationIp			交易ip
	 * @param flag					true 自然流水     false  人工流水
	 * @return
	 */
	public Result addDealAmount( DealOrder order  , String generationIp ,Boolean flag ) {
		UserFund userFund = userInfoServiceImpl.findUserFundByAccount(order.getOrderQrUser());
		UserRate rate = userInfoServiceImpl.findUserRateById(order.getFeeId());
		log.info("当前加入流水账号："+userFund.getUserId() + "，当前流水金额："+order.getDealAmount()+"，当前流水费率："+rate.getFee()+"，");
		BigDecimal dealAmount = order.getDealAmount();
		BigDecimal fee = rate.getFee();
		BigDecimal amount =  dealAmount.multiply(fee);
		Result add = add(PROFIT_AMOUNT_DEAL, userFund, order.getOrderId(), amount, generationIp, "商正常接单分润", flag?RUNTYPE_ARTIFICIAL:RUNTYPE_NATURAL);
		if(add.isSuccess())
			return add;
		return Result.buildFailMessage("流水生成失败");
	}
	/**
	 * <p>人工加钱</p>
	 * @param amount			加减款订单
	 * @param generationIp		操作ip
	 * @return
	 */
	public Result addAmount(Amount amount , String generationIp) {
		UserFund userFund = userInfoServiceImpl.findUserFundByAccount(amount.getUserId());
		Result add = add(ADD_AMOUNT, userFund, amount.getOrderId(), amount.getActualAmount(),
				generationIp, amount.getDealDescribe(), RUNTYPE_ARTIFICIAL);
		if(add.isSuccess())
			return add;
		return Result.buildFailMessage("流水生成失败");
	}
	/**
	 *<p>交易扣除交易点数【订单置为成功时调用该方法扣除点数】</p>
	 * @param order					交易订单
	 * @param generationIp			操作ip
	 * @param flag					true 自然流水     false  人工流水
	 * @return
	 */
	public Result deleteRechangerNumber(  DealOrder order , String generationIp,Boolean flag) {
		UserFund userFund = userInfoServiceImpl.findUserFundByAccount(order.getOrderQrUser());
		Result delete = delete(DEAL_AMOUNT_DETETE, userFund, order.getOrderId(), order.getDealAmount(), generationIp, 
				"交易流水,扣除用户交易点数", flag?RUNTYPE_ARTIFICIAL:RUNTYPE_NATURAL);
		if(delete.isSuccess()) 
			return delete;
		return Result.buildFailMessage("流水生成失败");
	}
	/**
	 * <p>人工扣款</p>
	 * @param amount					加减款订单
	 * @param generationIp				操作ip
	 * @return
	 */
	public Result deleteAmount(  Amount amount , String generationIp) {
		UserFund userFund = userInfoServiceImpl.findUserFundByAccount(amount.getUserId());
		Result delete = delete(DETETE_AMOUNT, userFund, amount.getOrderId(), amount.getActualAmount(), 
				generationIp, amount.getDealDescribe(), RUNTYPE_ARTIFICIAL);
		if(delete.isSuccess())
			return delete;
		return Result.buildFailMessage("流水生成失败");
	}
	
	/**
	 * <p>当前账户加款流水</p>
	 * @param userFund
	 * @param associatedId
	 * @param amount
	 * @return
	 */
	@SuppressWarnings("unused")
	private Result add(String orderType , UserFund userFund ,String associatedId , BigDecimal amount,String generationIp,String dealDescribe,String runType) {
		String orderAccount,amountType,acountR ,accountW;
		Integer runOrderType = null;
		BigDecimal amountNow ;
		orderAccount = userFund.getUserId();
		amountType = AMOUNT_TYPE_R;
		acountR =  orderAccount;
		accountW = SYSTEM_APP ; 
		runOrderType = getRunOrderType(orderType);
		amountNow = userFund .getAccountBalance();
		Result amountRun = amountRun(associatedId, orderAccount, runOrderType, amount, generationIp, acountR, accountW, runType, amountType, dealDescribe, amountNow);
		if(amountRun.isSuccess())
			return amountRun;
		return Result.buildFailMessage("流水生成失败");
	}
	
	/**
	 * <p>当前账户扣款流水</p>
	 * @param userFund
	 * @param associatedId
	 * @param amount
	 * @return
	 */
	private Result delete(String orderType , UserFund userFund ,String associatedId , BigDecimal amount,String generationIp,String dealDescribe,String runType) {
		String orderAccount,amountType,acountR ,accountW;
		Integer runOrderType = null;
		BigDecimal amountNow ;
		orderAccount = userFund.getUserId();
		amountType = AMOUNT_TYPE_W;
		acountR =  SYSTEM_APP;
		accountW = orderAccount ; 
		runOrderType = getRunOrderType(orderType);
		amountNow = userFund .getAccountBalance();
		Result amountRun = amountRun(associatedId, orderAccount, runOrderType, amount, generationIp, acountR, accountW, runType, amountType, dealDescribe, amountNow);
		if(amountRun.isSuccess())
			return amountRun;
		return Result.buildFailMessage("流水生成失败");
	}
	/**
	 * <p>创建流水总类</p>	
	 * @param associatedId		关联订单号
	 * @param orderAccount		关联账户号
	 * @param runOrderType		订单类型类型					订单类型(1充值交易,2系统加款,3交易手续费,4系统扣款,5代付,6代付手续费,7冻结,8解冻,9代付手手续费冻结,10代付冻结,11增加交易点数,12点数扣除,13代理商分润，14码商分润，15人工加款，16人工减款，17人工加点数，18人工减点数，19 卡商交易加钱)
	 * @param amount			流水金额
	 * @param generationIp		关联ip
	 * @param acountR			入款账户
	 * @param accountW			出款账户
	 * @param runType			人工操作					1.自然状态 2.人工操作
	 * @param amountType		流水类型					1支出0收入
	 * @param dealDescribe		流水说明
	 * @return
	 */
	@SuppressWarnings("unused")
	private Result amountRun(String associatedId,String orderAccount,
			Integer runOrderType,BigDecimal amount,String generationIp,
			String acountR , String accountW,String runType ,String amountType 
			,String dealDescribe,BigDecimal amountNow) {
		if(StrUtil.isBlank(associatedId) || StrUtil.isBlank(orderAccount) 
				|| StrUtil.isBlank(generationIp)
				|| StrUtil.isBlank(runType)
				|| StrUtil.isBlank(amountType)
				|| StrUtil.isBlank(dealDescribe)
				)
			return Result.buildFailMessage("必传参数为空");
		RunOrder run = new RunOrder();
		run.setAssociatedId(associatedId);
		run.setAccountW(accountW);
		run.setAcountR(acountR);
		run.setGenerationIp(generationIp);
		run.setOrderAccount(orderAccount);
		run.setAmountType(amountType);
		run.setDealDescribe(dealDescribe);
		run.setRunOrderType(runOrderType);
		run.setRunType(runType);
		run.setAmountNow(amountNow);
		boolean addOrder = runOrderServiceImpl.addOrder(run);
		if(addOrder)
			return Result.buildSuccessMessage("流水订单生成成功");
		return Result.buildFailMessage("流水订单生成失败");
	}
	
	/**
	 * <p>获取流水订单类型</p>
	 * @param orderType			流水订单标识
	 * @return
	 */
	Integer getRunOrderType(String orderType){
		Integer runOrderType = null;
		switch (orderType) {
		case ADD_AMOUNT:
			runOrderType = ADD_AMOUNT_NUMBER;
			break;
		case DETETE_AMOUNT:
			runOrderType = DETETE_AMOUNT_NUMBER;
			break;
		case DEAL_AMOUNT_DETETE:
			runOrderType = DEAL_AMOUNT_DETETE_NUMBER;
			break;
		case PROFIT_AMOUNT_DEAL:
			runOrderType = PROFIT_AMOUNT_DEAL_NUMBER;
			break;
		case RECHANGE_AMOUNT:
			runOrderType = RECHANGE_AMOUNT_NUMBER;
			break;
		case WITHDRAY_AMOUNT:
			runOrderType = WITHDRAY_AMOUNT_NUMBER;
			break;
		case WITHDRAY_AMOUNT_FEE:
			runOrderType = WITHDRAY_AMOUNT_FEE_NUMBER;
			break;
		case PROFIT_AMOUNT_AGENT:
			runOrderType = PROFIT_AMOUNT_AGENT_NUMBER;
			break;
		default:
			break;
		}
		return runOrderType;
	}
}
