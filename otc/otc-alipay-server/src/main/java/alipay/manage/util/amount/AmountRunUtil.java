package alipay.manage.util.amount;

import alipay.manage.bean.*;
import alipay.manage.service.RunOrderService;
import alipay.manage.service.UserInfoService;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import otc.bean.dealpay.Recharge;
import otc.bean.dealpay.Withdraw;
import otc.exception.user.UserException;
import otc.result.Result;

import java.math.BigDecimal;

/**
 * <p>资金流水处理类</p>
 *
 * @author hx08
 */
@Component
public class AmountRunUtil {
    /**
     * ######################################
     * <p>流水处理接口</p>
     * 1，人工加款接口				已完成
     * 2，人工扣款接口				已完成
     * 3，交易减少充值点数接口		已完成
     * 4，交易增加交易分润接口		已完成
     * 5，充值						已完成
     * 6，提现						已完成
     */
    Logger log = LoggerFactory.getLogger(AmountRunUtil.class);
    private static final String SYSTEM_APP = "SYSTEM_APP";//系统账户
    @Autowired private UserInfoService userInfoServiceImpl;
    @Autowired
    private RunOrderService runOrderServiceImpl;
    @Autowired
    private AmountPublic amountPublic;
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
    private static final String WITHDRAY_AMOUNT_OPEN_FEE = "WITHDRAY_AMOUNT_OPEN_FEE";//代付失败解冻
    private static final Integer WITHDRAY_AMOUNT_OPEN_FEE_NUMBER = 22;//代付失败手续费解冻
    private static final String WITHDRAY_AMOUNT_OPEN = "WITHDRAY_AMOUNT_OPEN";//代付失败解冻
    private static final Integer WITHDRAY_AMOUNT_OPEN_NUMBER = 8;//代付失败解冻

    private static final String ADD_DEAL_AMOUNT_APP = "ADD_DEAL_AMOUNT_APP";//下游商户交易加款
    private static final String DELETE_DEAL_FEE_AMOUNT_APP = "ADD_DEAL_FEE_AMOUNT_APP";//下游商户交易手续费扣款
    private static final Integer ADD_DEAL_AMOUNT_APP_NUMBER = 20;// 下游商户交易加款编号
    private static final Integer DELETE_DEAL_FEE_AMOUNT_APP_NUMBER = 21;// 下游商户交易手续费扣款编号
    private static final String CHANNEL_ADD_R = "CHANNEL_ADD_R";// 渠道手续费标识
    private static final Integer CHANNEL_ADD_R_NUMBER = 24;// 渠道手续费编号
    private static final Integer CHANNEL_ADD_WIT_NUMBER = 23;// 渠道代付加款成功标识
    private static final Integer CHANNEL_ADD_WIT_FEE_NUMBER = 25;// 渠道代付加款成功标识手续费
    private static final Integer AGNEET_WIT_FRREE_NUMBER = 26;// 商户代理商代付利润结算
    private static final String AGNEET_WIT_FRREE = "AGNEET_WIT_FRREE";// 商户代理商代付利润结算
    private static final String CHANNEL_ADD_WIT = "CHANNEL_ADD_WIT";// 渠道代付成功标识
    private static final String CHANNEL_ADD_WIT_FEE = "CHANNEL_ADD_WIT_FEE";// 渠道代付成功标识手续费


    private static final String DELETE_FREEZE = "DELETE_FREEZE";// 商户金额冻结
    private static final String ADD_FREEZE = "ADD_FREEZE";// 商户金额解冻
    private static final Integer DELETE_FREEZE_NUMBER = 27;// 商户金额冻结
    private static final Integer ADD_FREEZE_NUMBER = 28;// 商户金额解冻


    private static final String DELETE_QUOTA = "DELETE_QUOTA";// 商户金额冻结
    private static final String ADD_QUOTA = "ADD_QUOTA";// 商户金额解冻
    private static final Integer DELETE_QUOTA_NUMBER = 29;// 商户金额冻结
    private static final Integer ADD_QUOTA_NUMBER = 30;// 商户金额解冻


    private static final String AMOUNT_TYPE_R = "0";//对于当前账户来说是   收入
    private static final String AMOUNT_TYPE_W = "1";//对于当前账户来说是   支出

    private static final String RUNTYPE_ARTIFICIAL = "2";//人工流水
    private static final String RUNTYPE_NATURAL = "1";//自然流水


    private static final String USDT_GAS_FEE = "USDT_GAS_FEE";// USDT结算矿工手续费
    private static final Integer USDT_GAS_FEE_NUMBER = 31;// USDT结算矿工手续费


    /**
     * <p>码商代付流水生成</p>
     *
     * @param withdraw     代付订单表
     * @param generationIp ip
     * @param flag         true 自然流水     false  人工流水
     * @return
     */
    public Result deleteAmount(Withdraw withdraw, String generationIp, Boolean flag) {
        UserFund userFund = userInfoServiceImpl.findBalace(withdraw.getUserId()); //当前账户资金
        Result delete = delete(WITHDRAY_AMOUNT, userFund, withdraw.getOrderId(), withdraw.getActualAmount(), generationIp, "账户代付冻结", flag ? RUNTYPE_ARTIFICIAL : RUNTYPE_NATURAL);
        if (delete.isSuccess()) {
            return delete;
        }
        return Result.buildFailMessage("流水生成失败");
    }

    /**
     * 商户代付代理费用结算
     *
     * @param userFund 商户号
     * @param subtract 金额
     * @param wit      代付订单
     * @return
     */
    public Result addWitFee(UserFund userFund1, BigDecimal subtract, Withdraw wit, String generationIp, boolean flag) {
        UserFund userFund = userInfoServiceImpl.findBalace(userFund1.getUserId()); //当前账户资金
        return add(AGNEET_WIT_FRREE, userFund, wit.getOrderId(), subtract, generationIp, "代付代理商分润", flag ? RUNTYPE_ARTIFICIAL : RUNTYPE_NATURAL);
    }

    /**
     * <p>代付失败解冻</p>
     *
     * @param withdraw     代付订单
     * @param generationIp 操作ip
     * @return
     */
    public Result addAmountW(Withdraw withdraw, String generationIp) {
        UserFund userFund = userInfoServiceImpl.findBalace(withdraw.getUserId()); //当前账户资金
        return add(WITHDRAY_AMOUNT_OPEN, userFund, withdraw.getOrderId(), withdraw.getAmount(), generationIp, "代付失败解冻", RUNTYPE_ARTIFICIAL);
    }

    /**
     * 代付失败渠道退回
     * @param withdraw
     * @param generationIp
     * @param bigDecimal
     * @return
     */
    public Result addAmountChannelWitEr(Withdraw withdraw, String generationIp, BigDecimal bigDecimal) {
        UserFund userFund = userInfoServiceImpl.findBalace(withdraw.getUserId()); //查询出来为渠道账户
        return add(WITHDRAY_AMOUNT_OPEN, userFund, withdraw.getOrderId(), bigDecimal, generationIp, "商户成功代付订单置为失败", RUNTYPE_ARTIFICIAL);
    }

    /**
     * <p>代付失败手续费解冻</p>
     *
     * @param withdraw
     * @param generationIp
     * @return
     */
    public Result addAmountWFee(Withdraw withdraw, String generationIp) {
        UserFund userFund = userInfoServiceImpl.findBalace(withdraw.getUserId()); //当前账户资金
        return add(WITHDRAY_AMOUNT_OPEN_FEE, userFund, withdraw.getOrderId(), withdraw.getFee(), generationIp, "代付失败手续费解冻", RUNTYPE_ARTIFICIAL);
    }

    /**
     * <p>代付手续费冻结</p>
     *
     * @param withdraw
     * @param generationIp
     * @param flag
     * @return
     */
    public Result deleteAmountFee(Withdraw withdraw, String generationIp, Boolean flag) {
        UserFund userFund = userInfoServiceImpl.findBalace(withdraw.getUserId()); //当前账户资金
        Result delete = delete(WITHDRAY_AMOUNT_FEE, userFund, withdraw.getOrderId(), withdraw.getFee(), generationIp, "账户代付手续费冻结", flag ? RUNTYPE_ARTIFICIAL : RUNTYPE_NATURAL);
        if (delete.isSuccess()) {
            return delete;
        }
        throw new UserException("账户流水异常", null);
    }

    /**
     * <p>充值</p>
     *
     * @param recharge     充值订单
     * @param generationIp 充值ip
     * @param flag         true 自然流水     false  人工流水
     * @return
     */
    public Result addAmount(Recharge recharge, String generationIp, Boolean flag) {
        UserFund userFund = userInfoServiceImpl.findBalace(recharge.getUserId()); //当前账户资金
        Result add = add(RECHANGE_AMOUNT, userFund, recharge.getOrderId(), recharge.getActualAmount(), generationIp, "码商充值", flag ? RUNTYPE_ARTIFICIAL : RUNTYPE_NATURAL);
        if (add.isSuccess()) {
            return add;
        }
        throw new UserException("账户流水异常", null);
    }
    /**
     * <p>商户代理分润计算</p>
     *
     * @param orderId
     * @param userId
     * @param amount
     * @param generationIp
     * @param flag
     * @return
     */
    public Result addAppProfit(String orderId, String userId, BigDecimal amount, String generationIp, Boolean flag) {
        UserFund userFund = userInfoServiceImpl.findBalace(userId); //当前账户资金
        Result add = add(PROFIT_AMOUNT_AGENT, userFund, orderId, amount, generationIp, "商户代理商，代理分润结算", flag ? RUNTYPE_ARTIFICIAL : RUNTYPE_NATURAL);
        return add;
    }


    public Result addAppProfit(alipay.manage.bean.DealOrderApp orderApp, String userId, BigDecimal multiply, String ip,
                               boolean flag) {
        UserFund userFund = userInfoServiceImpl.findBalace(userId); //当前账户资金
        Result add = add(PROFIT_AMOUNT_AGENT, userFund, orderApp.getOrderId(), multiply, orderApp.getOrderIp(), "商户代理商，代理分润结算", flag ? RUNTYPE_ARTIFICIAL : RUNTYPE_NATURAL, orderApp.getOrderAccount());
        return add;
    }

    /**
     * <p>增加商户交易流水</p>
     *
     * @param order        商户交易订单
     * @param generationIp 商户ip
     * @param flag         是否为人工操作
     * @return
     */
    public Result addDealAmountApp(alipay.manage.bean.DealOrderApp order, String generationIp, Boolean flag) {
        UserFund userFund = userInfoServiceImpl.fundUserFundAccounrBalace(order.getOrderAccount());
        Result add = add(ADD_DEAL_AMOUNT_APP, userFund, order.getOrderId(), order.getOrderAmount(), generationIp, "下游商户交易加款", flag ? RUNTYPE_ARTIFICIAL : RUNTYPE_NATURAL);
        if (add.isSuccess()) {
            return add;
        }
        return Result.buildFailMessage("流水生成失败");
    }

    public Result deleteDealAmountFeeApp(alipay.manage.bean.DealOrderApp order, String generationIp, Boolean flag, BigDecimal fee) {
        UserFund userFund = userInfoServiceImpl.fundUserFundAccounrBalace(order.getOrderAccount());
        Result delete = delete(DELETE_DEAL_FEE_AMOUNT_APP, userFund, order.getOrderId(), fee, generationIp, "下游商户交易手续费扣除", flag ? RUNTYPE_ARTIFICIAL : RUNTYPE_NATURAL);
        if (delete.isSuccess()) {
            return delete;
        }
        return Result.buildFailMessage("流水生成失败");
    }

    /**
     * <p>码商会员正常交易分润流水</p>
     *
     * @param order        交易订单
     * @param generationIp 交易ip
     * @param flag         true 自然流水     false  人工流水
     * @return
     */
    public Result addDealAmount(DealOrder order, String generationIp, Boolean flag) {
        UserFund userFund = userInfoServiceImpl.fundUserFundAccounrBalace(order.getOrderQrUser());
        UserRate rate = userInfoServiceImpl.findUserRateById(order.getFeeId());
        log.info("当前加入流水账号：" + userFund.getUserId() + "，当前流水金额：" + order.getDealAmount() + "，当前流水费率：" + rate.getFee() + "，");
        BigDecimal dealAmount = order.getDealAmount();
        BigDecimal fee = rate.getFee();
        BigDecimal amount = dealAmount.multiply(fee);
        Result add = add(PROFIT_AMOUNT_DEAL, userFund, order.getOrderId(), amount, generationIp, "商正常接单分润", flag ? RUNTYPE_ARTIFICIAL : RUNTYPE_NATURAL);
        if (add.isSuccess()) {
            return add;
        }
        return Result.buildFailMessage("流水生成失败");
    }

    /**
     * <p>人工加钱</p>
     *
     * @param amount       加减款订单
     * @param generationIp 操作ip
     * @return
     */
    public Result addAmount(Amount amount, String generationIp) {
        UserFund userFund = userInfoServiceImpl.fundUserFundAccounrBalace(amount.getUserId());
        Result add = add(ADD_AMOUNT, userFund, amount.getOrderId(), amount.getActualAmount(),
                generationIp, amount.getDealDescribe(), RUNTYPE_ARTIFICIAL);
        if (add.isSuccess()) {
            return add;
        }
        return Result.buildFailMessage("流水生成失败");
    }

    public Result addAmount(Amount amount, String clientIP, String descr) {
        UserFund userFund = userInfoServiceImpl.fundUserFundAccounrBalace(amount.getUserId());
        Result add = add(ADD_AMOUNT, userFund, amount.getOrderId(), amount.getActualAmount(),
                clientIP, descr, RUNTYPE_ARTIFICIAL);
        if (add.isSuccess()) {
            return add;
        }
        return Result.buildFailMessage("流水生成失败");
    }


    /**
     * <p>交易扣除交易点数【订单置为成功时调用该方法扣除点数】</p>
     *
     * @param order        交易订单
     * @param generationIp 操作ip
     * @param flag         true 自然流水     false  人工流水
     * @return
     */
    public Result deleteRechangerNumber(DealOrder order, String generationIp, Boolean flag) {
        UserFund userFund = userInfoServiceImpl.fundUserFundAccounrBalace(order.getOrderQrUser());
        Result delete = delete(DEAL_AMOUNT_DETETE, userFund, order.getOrderId(), order.getDealAmount(), generationIp,
                "交易流水,扣除用户交易点数", flag ? RUNTYPE_ARTIFICIAL : RUNTYPE_NATURAL);
        if (delete.isSuccess()) {
            return delete;
        }
        return Result.buildFailMessage("流水生成失败");
    }

    /**
     * <p>人工扣款</p>
     *
     * @param amount       加减款订单
     * @param generationIp 操作ip
     * @return
     */
    public Result deleteAmount(Amount amount, String generationIp) {
        UserFund userFund = userInfoServiceImpl.fundUserFundAccounrBalace(amount.getUserId());
        Result delete = delete(DETETE_AMOUNT, userFund, amount.getOrderId(), amount.getActualAmount(),
                generationIp, amount.getDealDescribe(), RUNTYPE_ARTIFICIAL);
        if (delete.isSuccess()) {
            return delete;
        }
        return Result.buildFailMessage("流水生成失败");
    }

    /**
     * <p>当前账户加款流水</p>
     *
     * @param userFund
     * @param associatedId
     * @param amount
     * @return
     */
    @SuppressWarnings("unused")
    private Result add(String orderType, UserFund userFund, String associatedId, BigDecimal amount, String generationIp, String dealDescribe, String runType) {
        String orderAccount, amountType, acountR, accountW;
        Integer runOrderType = null;
        BigDecimal amountNow;
        orderAccount = userFund.getUserId();
        amountType = AMOUNT_TYPE_R;
        acountR = orderAccount;
        accountW = SYSTEM_APP;
        runOrderType = getRunOrderType(orderType);
        amountNow = userFund.getAccountBalance();
        Result amountRun = amountRun(associatedId, orderAccount, runOrderType, amount, generationIp, acountR, accountW, runType, amountType, dealDescribe, amountNow);
        if (amountRun.isSuccess()) {
            return amountRun;
        }
        return Result.buildFailMessage("流水生成失败");
    }

    /**
     * <p>代理上分润专用</p>
     *
     * @param orderType
     * @param userFund
     * @param associatedId
     * @param amount
     * @param generationIp
     * @param dealDescribe
     * @param runType
     * @param user
     * @return
     */
    private Result add(String orderType, UserFund userFund, String associatedId, BigDecimal amount, String generationIp, String dealDescribe, String runType, String user) {
        String orderAccount, amountType, acountR, accountW;
        Integer runOrderType = null;
        BigDecimal amountNow;
        orderAccount = userFund.getUserId();
        amountType = AMOUNT_TYPE_R;
        acountR = orderAccount;
        accountW = SYSTEM_APP;
        runOrderType = getRunOrderType(orderType);
        amountNow = userFund.getAccountBalance();
        Result amountRun = amountRun(associatedId, orderAccount, runOrderType, amount, generationIp, acountR, accountW, runType, amountType, dealDescribe, amountNow, user);
        if (amountRun.isSuccess()) {
            return amountRun;
        }
        return Result.buildFailMessage("流水生成失败");
    }

    /**
     * <p>当前账户扣款流水</p>
     *
     * @param userFund
     * @param associatedId
     * @param amount
     * @return
     */
    public Result delete(String orderType, UserFund userFund, String associatedId, BigDecimal amount, String generationIp, String dealDescribe, String runType) {
        String orderAccount, amountType, acountR, accountW;
        Integer runOrderType = null;
        BigDecimal amountNow;
        orderAccount = userFund.getUserId();
        amountType = AMOUNT_TYPE_W;
        acountR = SYSTEM_APP;
        accountW = orderAccount;
        runOrderType = getRunOrderType(orderType);
        amountNow = userFund.getAccountBalance();
        Result amountRun = amountRun(associatedId, orderAccount, runOrderType, amount, generationIp, acountR, accountW, runType, amountType, dealDescribe, amountNow);
        if (amountRun.isSuccess()) {
            return amountRun;
        }
        return Result.buildFail();
    }

    /**
     * <p>创建流水总类</p>
     *
     * @param associatedId 关联订单号
     * @param orderAccount 关联账户号
     * @param runOrderType 订单类型类型					订单类型(1充值交易,2系统加款,3交易手续费,4系统扣款,5代付,6代付手续费,7冻结,8解冻,9代付手手续费冻结,10代付冻结,11增加交易点数,12点数扣除,13代理商分润，14码商分润，15人工加款，16人工减款，17人工加点数，18人工减点数，19 卡商交易加钱)
     * @param amount       流水金额
     * @param generationIp 关联ip
     * @param acountR      入款账户
     * @param accountW     出款账户
     * @param runType      人工操作					1.自然状态 2.人工操作
     * @param amountType   流水类型					1支出0收入
     * @param dealDescribe 流水说明
     * @return
     */
    @SuppressWarnings("unused")
    private Result amountRun(String associatedId, String orderAccount,
                             Integer runOrderType, BigDecimal amount, String generationIp,
                             String acountR, String accountW, String runType, String amountType
            , String dealDescribe, BigDecimal amountNow) {
        if (StrUtil.isBlank(associatedId) || StrUtil.isBlank(orderAccount)
                || StrUtil.isBlank(generationIp)
                || StrUtil.isBlank(runType)
                || StrUtil.isBlank(amountType)
                || StrUtil.isBlank(dealDescribe)
        ) {
            return Result.buildFailMessage("必传参数为空");
        }
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
        run.setAmount(amount);
        boolean addOrder = runOrderServiceImpl.addOrder(run);
        if (addOrder) {
            return Result.buildSuccess();
        }
        return Result.buildFail();
    }

    /**
     * <p>代理分润专用</p>
     *
     * @param associatedId
     * @param orderAccount
     * @param runOrderType
     * @param amount
     * @param generationIp
     * @param acountR
     * @param accountW
     * @param runType
     * @param amountType
     * @param dealDescribe
     * @param amountNow
     * @param user
     * @return
     */
    private Result amountRun(String associatedId, String orderAccount,
                             Integer runOrderType, BigDecimal amount, String generationIp,
                             String acountR, String accountW, String runType, String amountType
            , String dealDescribe, BigDecimal amountNow, String user) {
        if (StrUtil.isBlank(associatedId) || StrUtil.isBlank(orderAccount)
                || StrUtil.isBlank(generationIp)
                || StrUtil.isBlank(runType)
                || StrUtil.isBlank(amountType)
                || StrUtil.isBlank(dealDescribe)
        ) {
            return Result.buildFailMessage("必传参数为空");
        }
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
        run.setAmount(amount);
        run.setRetain4(user);
        boolean addOrder = runOrderServiceImpl.addOrder(run);
        if (addOrder) {
            return Result.buildSuccess();
        }
        return Result.buildFail();
    }

    /**
     * <p>获取流水订单类型</p>
     *
     * @param orderType 流水订单标识
     * @return
     */
    private Integer getRunOrderType(String orderType) {
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
            case ADD_DEAL_AMOUNT_APP:
                runOrderType = ADD_DEAL_AMOUNT_APP_NUMBER;
                break;
            case DELETE_DEAL_FEE_AMOUNT_APP:
                runOrderType = DELETE_DEAL_FEE_AMOUNT_APP_NUMBER;
                break;
            case WITHDRAY_AMOUNT_OPEN:
                runOrderType = WITHDRAY_AMOUNT_OPEN_NUMBER;
                break;
            case WITHDRAY_AMOUNT_OPEN_FEE:
                runOrderType = WITHDRAY_AMOUNT_OPEN_FEE_NUMBER;
                break;
            case CHANNEL_ADD_WIT:
                runOrderType = CHANNEL_ADD_WIT_NUMBER;
                break;
            case AGNEET_WIT_FRREE:
                runOrderType = AGNEET_WIT_FRREE_NUMBER;
                break;
            case CHANNEL_ADD_R:
                runOrderType = CHANNEL_ADD_R_NUMBER;
                break;
            case CHANNEL_ADD_WIT_FEE:
                runOrderType = CHANNEL_ADD_WIT_FEE_NUMBER;
                break;
            case DELETE_FREEZE:
                runOrderType = DELETE_FREEZE_NUMBER;
                break;
            case ADD_FREEZE:
                runOrderType = ADD_FREEZE_NUMBER;
                break;
            case ADD_QUOTA:
                runOrderType = ADD_QUOTA_NUMBER;
                break;
            case DELETE_QUOTA:
                runOrderType = DELETE_QUOTA_NUMBER;
                break;
            case USDT_GAS_FEE:
                runOrderType = USDT_GAS_FEE_NUMBER;
                break;
            default:
                break;
        }
        return runOrderType;
    }

    /**
     * <p>渠道代付手续费</p>
     *
     * @param withdraw
     * @param generationIp
     * @return
     */
    public Result addChannelWitFee(Withdraw withdraw, String generationIp, BigDecimal fee) {
        UserFund userFund = userInfoServiceImpl.findBalace(withdraw.getWitChannel()); //当前账户资金
        return add(CHANNEL_ADD_WIT_FEE, userFund, withdraw.getOrderId(), fee, generationIp, "渠道代付手续费流水", RUNTYPE_NATURAL);
    }

    /**
     * <p>渠道代付账户修改，流水生成</p>
     *
     * @param withdraw
     * @param generationIp
     * @return
     */
    public Result addChannelWit(Withdraw withdraw, String generationIp) {
        UserFund userFund = userInfoServiceImpl.findBalace(withdraw.getWitChannel()); //当前账户资金
        return add(CHANNEL_ADD_WIT, userFund, withdraw.getOrderId(), withdraw.getAmount(), generationIp, "渠道代付成功加款", RUNTYPE_NATURAL);
    }


    public Result addFreeze(Amount amount, String generationIp) {
        UserFund userFund = userInfoServiceImpl.findBalace(amount.getUserId()); //当前账户资金
        return add(ADD_FREEZE, userFund, amount.getOrderId(), amount.getAmount(), generationIp, "小金库转入余额账户", RUNTYPE_ARTIFICIAL);
    }

    public Result deleteFreeze(Amount amount, String generationIp) {
        UserFund userFund = userInfoServiceImpl.findBalace(amount.getUserId()); //当前账户资金
        return add(DELETE_FREEZE, userFund, amount.getOrderId(), amount.getAmount(), generationIp, "余额账户转入小金库", RUNTYPE_ARTIFICIAL);
    }

    public Result addQuota(Amount amountPrivate, String generationIp) {
        UserFund userFund = userInfoServiceImpl.findBalace(amountPrivate.getUserId()); //当前账户资金
        return add(ADD_QUOTA, userFund, amountPrivate.getOrderId(), amountPrivate.getAmount(), generationIp, "增加授权额度", RUNTYPE_ARTIFICIAL);
    }

    public Result deleteQuota(Amount amount, String generationIp) {
        UserFund userFund = userInfoServiceImpl.findBalace(amount.getUserId()); //当前账户资金
        return add(DELETE_QUOTA, userFund, amount.getOrderId(), amount.getAmount(), generationIp, "减少授权额度", RUNTYPE_ARTIFICIAL);
    }

    public Result deleteUsdtFeeGas(Withdraw withdraw, String generationIp, String describe, BigDecimal amount) {
        UserFund userFund = userInfoServiceImpl.findBalace(withdraw.getUserId()); //当前账户资金
        return add(USDT_GAS_FEE, userFund, withdraw.getOrderId(), amount, generationIp, describe, RUNTYPE_NATURAL);
    }


}
