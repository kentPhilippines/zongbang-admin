package com.ruoyi.dealpay.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 交易订单对象 dealpay_deal_order
 *
 * @author k
 * @date 2020-04-03
 */
public class DealpayDealOrderEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 数据id
     */
    private Long id;

    /**
     * 订单号
     */
    @Excel(name = "订单号")
    private String orderId;

    /**
     * 关联订单号
     */
    @Excel(name = "关联订单号")
    private String associatedId;

    /**
     * 订单状态:0预下单1处理中2成功3未收到回调4失败5超时6订单申述7人工处理
     */
    @Excel(name = "订单状态:0预下单1处理中2成功3未收到回调4失败5超时6订单申述7人工处理")
    private String orderStatus;

    /**
     * 订单交易金额
     */
    @Excel(name = "订单交易金额")
    private Double dealAmount;

    /**
     * 订单交易手续费
     */
    @Excel(name = "订单交易手续费")
    private Double dealFee;

    /**
     * 实际到账金额
     */
    @Excel(name = "实际到账金额")
    private Double actualAmount;

    /**
     * 订单类型:1 出款交易     2  入款交易
     */
    @Excel(name = "订单类型:1 出款交易     2  入款交易")
    private String orderType;

    /**
     * 订单关联商户账号
     */
    @Excel(name = "订单关联商户账号")
    private String orderAccount;

    /**
     * 关联卡商商账户
     */
    @Excel(name = "关联卡商商账户")
    private String orderQrUser;

    /**
     * 银行卡系统编号
     */
    @Excel(name = "银行卡系统编号")
    private String orderQr;

    /**
     * 外部订单号(下游商户请求参数,用户数据回调)
     */
    @Excel(name = "外部订单号(下游商户请求参数,用户数据回调)")
    private String externalOrderId;

    /**
     * 订单生成IP(客户端ip或者是下游商户id)
     */
    @Excel(name = "订单生成IP(客户端ip或者是下游商户id)")
    private String generationIp;

    /**
     * 交易备注
     */
    @Excel(name = "交易备注")
    private String dealDescribe;

    /**
     * 订单异步回调地址
     */
    @Excel(name = "订单异步回调地址")
    private String notify;

    /**
     * 订单同步回调地址
     */
    @Excel(name = "订单同步回调地址")
    private String back;

    /**
     * 是否發送通知 //  YES 已發送    NO 未發送
     */
    @Excel(name = "是否發送通知 //  YES 已發送    NO 未發送")
    private String isNotify;

    /**
     * 数据修改时间
     */
    @Excel(name = "数据修改时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date submitTime;

    /**
     * 使用费率id
     */
    @Excel(name = "使用费率id")
    private Integer feeId;

    /**
     * 状态:1可使用；0不可使用
     */
    @Excel(name = "状态:1可使用；0不可使用")
    private Integer status;

    /**
     * 交易产品类型
     */
    @Excel(name = "交易产品类型")
    private String productType;

    /**
     * 备用字段添加业务使用
     */
    @Excel(name = "备用字段添加业务使用")
    private String retain2;

    /**
     * 备用字段添加业务使用
     */
    @Excel(name = "备用字段添加业务使用")
    private String retain3;

    /**
     * 备用字段添加业务使用
     */
    @Excel(name = "备用字段添加业务使用")
    private String retain4;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setAssociatedId(String associatedId) {
        this.associatedId = associatedId;
    }

    public String getAssociatedId() {
        return associatedId;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setDealAmount(Double dealAmount) {
        this.dealAmount = dealAmount;
    }

    public Double getDealAmount() {
        return dealAmount;
    }

    public void setDealFee(Double dealFee) {
        this.dealFee = dealFee;
    }

    public Double getDealFee() {
        return dealFee;
    }

    public void setActualAmount(Double actualAmount) {
        this.actualAmount = actualAmount;
    }

    public Double getActualAmount() {
        return actualAmount;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderAccount(String orderAccount) {
        this.orderAccount = orderAccount;
    }

    public String getOrderAccount() {
        return orderAccount;
    }

    public void setOrderQrUser(String orderQrUser) {
        this.orderQrUser = orderQrUser;
    }

    public String getOrderQrUser() {
        return orderQrUser;
    }

    public void setOrderQr(String orderQr) {
        this.orderQr = orderQr;
    }

    public String getOrderQr() {
        return orderQr;
    }

    public void setExternalOrderId(String externalOrderId) {
        this.externalOrderId = externalOrderId;
    }

    public String getExternalOrderId() {
        return externalOrderId;
    }

    public void setGenerationIp(String generationIp) {
        this.generationIp = generationIp;
    }

    public String getGenerationIp() {
        return generationIp;
    }

    public void setDealDescribe(String dealDescribe) {
        this.dealDescribe = dealDescribe;
    }

    public String getDealDescribe() {
        return dealDescribe;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }

    public String getNotify() {
        return notify;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public String getBack() {
        return back;
    }

    public void setIsNotify(String isNotify) {
        this.isNotify = isNotify;
    }

    public String getIsNotify() {
        return isNotify;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setFeeId(Integer feeId) {
        this.feeId = feeId;
    }

    public Integer getFeeId() {
        return feeId;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductType() {
        return productType;
    }

    public void setRetain2(String retain2) {
        this.retain2 = retain2;
    }

    public String getRetain2() {
        return retain2;
    }

    public void setRetain3(String retain3) {
        this.retain3 = retain3;
    }

    public String getRetain3() {
        return retain3;
    }

    public void setRetain4(String retain4) {
        this.retain4 = retain4;
    }

    public String getRetain4() {
        return retain4;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("orderId", getOrderId())
                .append("associatedId", getAssociatedId())
                .append("orderStatus", getOrderStatus())
                .append("dealAmount", getDealAmount())
                .append("dealFee", getDealFee())
                .append("actualAmount", getActualAmount())
                .append("orderType", getOrderType())
                .append("orderAccount", getOrderAccount())
                .append("orderQrUser", getOrderQrUser())
                .append("orderQr", getOrderQr())
                .append("externalOrderId", getExternalOrderId())
                .append("generationIp", getGenerationIp())
                .append("dealDescribe", getDealDescribe())
                .append("notify", getNotify())
                .append("back", getBack())
                .append("isNotify", getIsNotify())
                .append("createTime", getCreateTime())
                .append("submitTime", getSubmitTime())
                .append("feeId", getFeeId())
                .append("status", getStatus())
                .append("productType", getProductType())
                .append("retain2", getRetain2())
                .append("retain3", getRetain3())
                .append("retain4", getRetain4())
                .toString();
    }
}
