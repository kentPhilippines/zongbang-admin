package com.ruoyi.alipay.service;

import com.ruoyi.alipay.domain.AlipayRunOrderEntity;
import java.util.List;

/**
 * 流水订单记录Service接口
 * 
 * @author kiwi
 * @date 2020-03-18
 */
public interface IAlipayRunOrderEntityService 
{
    /**
     * 查询流水订单记录
     * 
     * @param id 流水订单记录ID
     * @return 流水订单记录
     */
    public AlipayRunOrderEntity selectAlipayRunOrderEntityById(Long id);

    /**
     * 查询流水订单记录列表
     * 
     * @param alipayRunOrderEntity 流水订单记录
     * @return 流水订单记录集合
     */
    public List<AlipayRunOrderEntity> selectAlipayRunOrderEntityList(AlipayRunOrderEntity alipayRunOrderEntity);

    /**
     * 新增流水订单记录
     * 
     * @param alipayRunOrderEntity 流水订单记录
     * @return 结果
     */
    public int insertAlipayRunOrderEntity(AlipayRunOrderEntity alipayRunOrderEntity);

    /**
     * 修改流水订单记录
     * 
     * @param alipayRunOrderEntity 流水订单记录
     * @return 结果
     */
    public int updateAlipayRunOrderEntity(AlipayRunOrderEntity alipayRunOrderEntity);

    /**
     * 批量删除流水订单记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteAlipayRunOrderEntityByIds(String ids);

    /**
     * 删除流水订单记录信息
     * 
     * @param id 流水订单记录ID
     * @return 结果
     */
    public int deleteAlipayRunOrderEntityById(Long id);
}