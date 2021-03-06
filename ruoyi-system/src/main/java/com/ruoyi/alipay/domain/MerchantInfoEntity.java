package com.ruoyi.alipay.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 商户信息对象 t_merchant_info
 * 
 * @author ruoyi
 * @date 2020-03-18
 */
public class MerchantInfoEntity extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 数据ID */
    private Long id;

    /** 商户ID */
    @Excel(name = "商户ID")
    private String merchantId;

    /** 商户名称 */
    @Excel(name = "商户名称")
    private String merchantName;

    /** 商家私钥 */
    @Excel(name = "商家私钥")
    private String privateKey;

    /** 商家公钥 */
    @Excel(name = "商家公钥")
    private String publicKey;

    /** 交易密钥 */
    @Excel(name = "交易密钥")
    private String dealKey;

    /** 提现密码 */
    @Excel(name = "提现密码")
    private String withdrawalPwd;

    /** 提现盐值 */
    @Excel(name = "提现盐值")
    private String withdrawalSalt;

    /** 商户状态 */
    @Excel(name = "商户状态")
    private Integer switches;

    /** 删除标志 */
    private Integer delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setMerchantId(String merchantId) 
    {
        this.merchantId = merchantId;
    }

    public String getMerchantId() 
    {
        return merchantId;
    }
    public void setMerchantName(String merchantName) 
    {
        this.merchantName = merchantName;
    }

    public String getMerchantName() 
    {
        return merchantName;
    }
    public void setPrivateKey(String privateKey) 
    {
        this.privateKey = privateKey;
    }

    public String getPrivateKey() 
    {
        return privateKey;
    }
    public void setPublicKey(String publicKey) 
    {
        this.publicKey = publicKey;
    }

    public String getPublicKey() 
    {
        return publicKey;
    }
    public void setDealKey(String dealKey) 
    {
        this.dealKey = dealKey;
    }

    public String getDealKey() 
    {
        return dealKey;
    }
    public void setWithdrawalPwd(String withdrawalPwd) 
    {
        this.withdrawalPwd = withdrawalPwd;
    }

    public String getWithdrawalPwd() 
    {
        return withdrawalPwd;
    }
    public void setWithdrawalSalt(String withdrawalSalt) 
    {
        this.withdrawalSalt = withdrawalSalt;
    }

    public String getWithdrawalSalt() 
    {
        return withdrawalSalt;
    }
    public void setSwitches(Integer switches) 
    {
        this.switches = switches;
    }

    public Integer getSwitches() 
    {
        return switches;
    }
    public void setDelFlag(Integer delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("merchantId", getMerchantId())
            .append("merchantName", getMerchantName())
            .append("privateKey", getPrivateKey())
            .append("publicKey", getPublicKey())
            .append("dealKey", getDealKey())
            .append("withdrawalPwd", getWithdrawalPwd())
            .append("withdrawalSalt", getWithdrawalSalt())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("switches", getSwitches())
            .append("delFlag", getDelFlag())
            .append("remark", getRemark())
            .toString();
    }
}
