package alipay.manage.mapper;

import alipay.manage.bean.WithdrawExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import otc.bean.dealpay.Withdraw;

import java.util.List;
@Mapper
public interface WithdrawMapper {
    int countByExample(WithdrawExample example);

    int deleteByExample(WithdrawExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Withdraw record);

    int insertSelective(Withdraw record);

    List<Withdraw> selectByExample(WithdrawExample example);

    Withdraw selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Withdraw record, @Param("example") WithdrawExample example);

    int updateByExample(@Param("record") Withdraw record, @Param("example") WithdrawExample example);

    int updateByPrimaryKeySelective(Withdraw record);

    int updateByPrimaryKey(Withdraw record);

    @Update("update alipay_withdraw set " +
            "orderStatus = #{orderStatus}, " +
            "approval = #{approval}," +
            "comment = #{comment}, " +
            "submitTime = sysdate() ," +
            "channelId = #{channelId} " +
            "where orderId = #{orderId}")
    int updataOrderStatus(@Param("orderId") String orderId, @Param("approval") String approval,
                          @Param("comment") String comment, @Param("orderStatus") String orderStatus,
                          @Param("channelId") String channelId);

    @Select("select * from alipay_withdraw where orderId = #{orderId}")
    Withdraw findWitOrder(@Param("orderId") String orderId);

    @Update("update alipay_withdraw set " +
            "orderStatus = #{orderStatus}, " +
            "comment = #{comment}, " +
            "submitTime = sysdate() " +
            "where orderId = #{orderId}")
    int updataOrderStatusEr(@Param("orderId") String orderId, @Param("orderStatus") String orderStatus, @Param("comment") String comment);

    @Select("select * from alipay_withdraw where appOrderId = #{orderId} and userId = #{appId}")
    Withdraw findOrderByApp(@Param("appId") String appId, @Param("orderId") String orderId);

    @Update("update alipay_withdraw set " +
            "orderStatus = #{orderStatus}, " +
            "approval = #{approval}," +
            "comment = #{comment}, " +
            "submitTime = sysdate() " +
            "where orderId = #{orderId}")
    int updataOrderStatus1(@Param("orderId") String orderId, @Param("approval") String approval,
                           @Param("comment") String comment, @Param("orderStatus") String orderStatusEr);
}
