package deal.manage.contorller;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import deal.manage.bean.BankList;
import deal.manage.bean.UserInfo;
import deal.manage.bean.util.PageResult;
import deal.manage.service.BankListService;
import deal.manage.service.UserInfoService;
import deal.manage.util.IsDealIpUtil;
import deal.manage.util.SessionUtil;
import otc.result.Result;

@Controller
@RequestMapping("/statisticalAnalysis")
public class QrcodeDataStatisticsContorller {
	Logger log = LoggerFactory.getLogger(QrcodeDataStatisticsContorller.class);
	@Autowired SessionUtil sessionUtil;
	@Autowired UserInfoService userInfoServiceImpl;
	@Autowired IsDealIpUtil isDealIpUtil;
	@Autowired BankListService bankCardServiceImpl;
	/**
	 * <p>获取码商今日奖励金排行榜</p>
	 * @return
	 */
	@GetMapping("/findTodayTop10BountyRank")
	@ResponseBody
	public Result findTodayTop10BountyRank() {
		List<Object> list = new ArrayList<Object>();
		return Result.buildSuccessResult("数据获取成功", list);
	}
	/**
	 * <p>获取码商累计奖励金排行</p>
	 * @return
	 */
	@GetMapping("/findTotalTop10BountyRank")
	@ResponseBody
	public Result findTotalTop10BountyRank() {
		List<Object> list = new ArrayList<Object>();
		return Result.buildSuccessResult("数据获取成功", list);
	}
	
	/**
	 * <p>累计接单情况</p>
	 * @return
	 */
	@GetMapping("/findMyTodayReceiveOrderSituation")
	@ResponseBody
	public Result findMyTodayReceiveOrderSituation(HttpServletRequest request) {
		UserInfo user = sessionUtil.getUser(request);
		if(ObjectUtil.isNull(user)) 
			return Result.buildFailMessage("用户未登录");
		return Result.buildSuccessResult();
	}
	/**
	 * <p>今日接单情况</p>
	 * @return
	 */
	@GetMapping("/findMyTotalReceiveOrderSituation")
	@ResponseBody
	public Result showTodayReceiveOrderSituation(HttpServletRequest request) {
		UserInfo user = sessionUtil.getUser(request);
		if(ObjectUtil.isNull(user)) 
			return Result.buildFailMessage("用户未登录");
		return Result.buildSuccessResult( );
	}
	/**
	 * <p>获取与当前登录用户相关的二维码图片账号</p>
	 * @param qr
	 * @return
	 */
	@GetMapping("/findMyGatheringCodeByPage")
	@ResponseBody
	public Result findMyGatheringCodeByPage( BankList bank,HttpServletRequest request,String pageNum,String pageSize) {
		UserInfo user = sessionUtil.getUser(request);
		if(ObjectUtil.isNull(user)) 
			return Result.buildFailMessage("用户未登录");
		PageHelper.startPage(Integer.valueOf(pageNum), Integer.valueOf(pageSize));
		bank.setAccount(user.getUserId());
		List<BankList> bankList = bankCardServiceImpl.findBankInfoAccount(bank);
		PageInfo<BankList> pageInfo = new PageInfo<BankList>(bankList);
		PageResult<BankList> pageR = new PageResult<BankList>();
		pageR.setContent(pageInfo.getList());
		pageR.setPageNum(pageInfo.getPageNum());
		pageR.setTotal(pageInfo.getTotal());
		pageR.setTotalPage(pageInfo.getPages());
		return Result.buildSuccessResult(pageR);
	}

	/*
	 * @GetMapping("/findMyGatheringCodeById")
	 * 
	 * @ResponseBody public Result findMyGatheringCodeById(BankList bank) {
	 * log.info(bank.toString()); bank.setStatus(null); List<BankCardQrList>
	 * findBankCardByBankInfo = bankCardServiceImpl.find'B(bank); return
	 * JsonResult.buildSuccessResult(CollUtil.getFirst(findBankCardByBankInfo)); }
	 */
	/**
	 * <p>删除一个二维码</p>
	 * @param qr
	 * @return
	 */
	@GetMapping("/delMyGatheringCodeById")
	@ResponseBody
	public Result delMyGatheringCodeById( String id) {
		boolean flag = bankCardServiceImpl.deleteBankById(id);
		if(flag) 
			return	Result.buildSuccessResult();
		return Result.buildFailMessage("删除失败");
	}
}
