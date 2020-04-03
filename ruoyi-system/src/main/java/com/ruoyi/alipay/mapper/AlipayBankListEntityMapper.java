package com.ruoyi.alipay.mapper;

import com.ruoyi.alipay.domain.AlipayBankListEntity;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 银行卡列表Mapper接口
 * 
 * @author kiwi
 * @date 2020-03-17
 */
public interface AlipayBankListEntityMapper 
{
    /**
     * 查询银行卡列表
     * 
     * @param id 银行卡列表ID
     * @return 银行卡列表
     */
    public AlipayBankListEntity selectAlipayBankListEntityById(Long id);

    /**
     * 查询银行卡列表列表
     * 
     * @param alipayBankListEntity 银行卡列表
     * @return 银行卡列表集合
     */
    List<AlipayBankListEntity> selectAlipayBankListEntityList(AlipayBankListEntity alipayBankListEntity);

    /**
     * 新增银行卡列表
     * 
     * @param alipayBankListEntity 银行卡列表
     * @return 结果
     */
    public int insertAlipayBankListEntity(AlipayBankListEntity alipayBankListEntity);

    /**
     * 修改银行卡列表
     * 
     * @param alipayBankListEntity 银行卡列表
     * @return 结果
     */
    int updateAlipayBankListEntity(AlipayBankListEntity alipayBankListEntity);

    /**
     * 批量删除银行卡列表
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteAlipayBankListEntityByIds(String[] ids);

    @Update("update alipay_bank_list set status = #{status} where id = #{id} ")
    int updateBankCardStatusById(AlipayBankListEntity alipayBankListEntity);
}