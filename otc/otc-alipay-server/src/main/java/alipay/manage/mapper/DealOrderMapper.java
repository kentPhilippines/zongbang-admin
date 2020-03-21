package alipay.manage.mapper;

import alipay.manage.bean.DealOrder;
import alipay.manage.bean.DealOrderExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;

@Mapper
public interface DealOrderMapper {
    static final String ORDER_INFO_QR = "ORDERINFO:QR";
    int countByExample(DealOrderExample example);

    int deleteByExample(DealOrderExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(DealOrder record);

    int insertSelective(DealOrder record);

    List<DealOrder> selectByExampleWithBLOBs(DealOrderExample example);

    List<DealOrder> selectByExample(DealOrderExample example);

    DealOrder selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") DealOrder record, @Param("example") DealOrderExample example);

    int updateByExampleWithBLOBs(@Param("record") DealOrder record, @Param("example") DealOrderExample example);

    int updateByExample(@Param("record") DealOrder record, @Param("example") DealOrderExample example);

    int updateByPrimaryKeySelective(DealOrder record);

    int updateByPrimaryKeyWithBLOBs(DealOrder record);

    int updateByPrimaryKey(DealOrder record);
    /**
     * <p>根据用户id 查询交易订单</p>
     * @param createTime
     * @param userId
     * @return
     */
    List<DealOrder> selectByExampleByMyId(@Param("userId")String userId,@Param("createTime") String createTime);

    /**
     * <p>根据用户id查询自己的交易订单号记录</p>
     * @param order
     * @return
     */
    @Cacheable(cacheNames= {ORDER_INFO_QR} ,  unless="#result == null")
    List<DealOrder> findMyOrder(DealOrder order);
}