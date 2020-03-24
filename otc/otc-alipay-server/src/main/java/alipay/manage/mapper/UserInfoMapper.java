package alipay.manage.mapper;

import alipay.manage.bean.UserFund;
import alipay.manage.bean.UserInfo;
import alipay.manage.bean.UserInfoExample;
import java.util.List;

import alipay.manage.bean.UserRate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface UserInfoMapper {
    int countByExample(UserInfoExample example);

    int deleteByExample(UserInfoExample example);

    int deleteByPrimaryKey(@Param("id") Integer id, @Param("userId") String userId);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    List<UserInfo> selectByExample(UserInfoExample example);

    UserInfo selectByPrimaryKey(@Param("id") Integer id, @Param("userId") String userId);

    int updateByExampleSelective(@Param("record") UserInfo record, @Param("example") UserInfoExample example);

    int updateByExample(@Param("record") UserInfo record, @Param("example") UserInfoExample example);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);

    UserInfo selectByUserId(String userId);

    UserInfo selectByUserName(String username);

    @Select("select * from alipay_user_info where userId = #{userId} or userName = #{userName}")
	UserInfo findUserId( @Param("userId")String userId, @Param("userName")String userName);

    @Select("select queryChildAgents(#{accountId})")
    List<String> selectChildAgentListById(@Param("accountId") String accountId);
    /**
     * <p>根据用户名id【唯一】 ，查询用户详细信息</p>
     * @param userId
     * @return
     */
    @Select("select * from alipay_user_info where userId = #{userId}")
    UserInfo findUserByUserId( @Param("userId")String userId);


    @Select("select * from alipay_user_rate where userId = #{userId} and payTypr = #{passCode} and switchs = 1")
    UserRate selectUserRateByUserId(@Param("userId")String userId, @Param("passCode")String passCode);


    List<UserInfo> getLoginAccountInfo(String userId);


    @Select("select  id, userId, userName, cashBalance, rechargeNumber, freezeBalance, accountBalance, " +
            " sumDealAmount, sumRechargeAmount, sumProfit, sumAgentProfit, sumOrderCount, todayDealAmount, " +
            " todayProfit, todayOrderCount, todayAgentProfit, userType, agent, isAgent, createTime, " +
            " submitTime, status, version from alipay_user_fund where userId = #{userId}")
    UserFund selectUsrFundByUserId(@Param("userId") String userId);
}