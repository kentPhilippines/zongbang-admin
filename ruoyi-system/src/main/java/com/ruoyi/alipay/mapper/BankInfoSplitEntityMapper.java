package com.ruoyi.alipay.mapper;

import com.ruoyi.alipay.domain.BankInfoSplitEntity;
import com.ruoyi.alipay.domain.BankTransactionRecord;

import java.util.List;

/**
 *
 */
public interface BankInfoSplitEntityMapper {


    /**
     * 新增截取银行流水信息数据
     *
     * @param bankInfoSplitEntity
     * @return 结果
     */
    List<BankInfoSplitEntity> selectBankInfoSplitEntity(BankInfoSplitEntity bankInfoSplitEntity);

    /**
     * 查询流水收入支出记录
     *
     * @param bankInfoSplitEntity
     * @return 结果
     */
    List<BankTransactionRecord> selectBankTransactionRecord(BankInfoSplitEntity bankInfoSplitEntity);
}
