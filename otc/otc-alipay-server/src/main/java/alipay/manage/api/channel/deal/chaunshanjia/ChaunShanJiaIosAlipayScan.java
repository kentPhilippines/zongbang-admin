package alipay.manage.api.channel.deal.chaunshanjia;

import alipay.manage.api.channel.util.xianyu.XianYuUtil;
import alipay.manage.api.config.PayOrderService;
import alipay.manage.bean.DealOrder;
import alipay.manage.bean.DealOrderApp;
import alipay.manage.bean.UserInfo;
import alipay.manage.bean.util.ResultDeal;
import alipay.manage.service.OrderService;
import alipay.manage.service.UserInfoService;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import otc.common.PayApiConstant;
import otc.result.Result;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component("chuanshanjiaAlipayScan")
public class ChaunShanJiaIosAlipayScan extends PayOrderService {
    private static final Log log = LogFactory.get();
    @Autowired
    private UserInfoService userInfoServiceImpl;
    @Autowired
    private OrderService orderServiceImpl;

    @Override
    public Result deal(DealOrderApp dealOrderApp, String payType) {
        log.info("【进入穿山甲IOS支付宝扫码】");
        String create = create(dealOrderApp, payType);
        if (StrUtil.isNotBlank(create)) {
            log.info("【本地订单创建成功，开始请求远程三方支付】");
            UserInfo userInfo = userInfoServiceImpl.findUserInfoByUserId(dealOrderApp.getOrderAccount());
            if (StrUtil.isBlank(userInfo.getDealUrl())) {
                orderEr(dealOrderApp, "当前商户交易url未设置");
                return Result.buildFailMessage("请联系运营为您的商户好设置交易url");
            }
            XianYu xianyu = createOrder(userInfo.getDealUrl() + PayApiConstant.Notfiy.NOTFIY_API_WAI + "/chuanshanjia-notfiy", dealOrderApp.getOrderAmount(), create);
            if (ObjectUtil.isNull(xianyu)) {
                boolean orderEr = orderEr(dealOrderApp);
                if (orderEr) {
                    return Result.buildFailMessage("支付失败");
                }
            } else {
                if ("1".equals(xianyu.getStatus())) {
                    return Result.buildSuccessResult("支付处理中", ResultDeal.sendUrl(xianyu.getPayurl()));
                } else {
                    orderEr(dealOrderApp);
                    return Result.buildFailMessage(xianyu.getPayurl());
                }
			}
		}
		return Result.buildFailMessage("支付失败");
	}
	private XianYu createOrder(String notfiy, BigDecimal orderAmount, String orderId) {
        DealOrder order = orderServiceImpl.findOrderByOrderId(orderId);
        String fxnotifyurl = notfiy;
        String fxbackurl = StrUtil.isBlank(order.getBack()) ? notfiy : order.getBack();
        String fxattch = "test";
        String fxdesc = "desc";
        String fxfee = orderAmount.toString();
        String fxpay = XianYuUtil.SCANH5;
        String fxddh = order.getOrderId(); //订单号
        String fxid = XianYuUtil.APPID;
        String key = XianYuUtil.KEY;
        //订单签名
        String fxsign = XianYuUtil.md5(fxid + fxddh + fxfee + fxnotifyurl + key);
        fxsign = fxsign.toLowerCase();
        Map<String, Object> reqMap = new HashMap<String, Object>();
        reqMap.put("fxid", fxid);
        reqMap.put("fxddh", fxddh);
        reqMap.put("fxfee", fxfee);
        reqMap.put("fxpay", fxpay);
        reqMap.put("fxnotifyurl", fxnotifyurl);
        reqMap.put("fxbackurl", fxbackurl);
        reqMap.put("fxattch", fxattch);
        reqMap.put("fxnotifystyle", "1");
        reqMap.put("fxdesc", fxdesc);
        reqMap.put("fxip", "127.0.0.1");
        reqMap.put("fxsign", fxsign);
        log.info("【穿山甲请求参数：" + reqMap.toString() + "】");
        // 支付请求返回结果
        String result = null;
        result = HttpUtil.post(XianYuUtil.URL, reqMap);
        JSONObject parseObj = JSONUtil.parseObj(result);
        log.info("【穿山甲返回：" + parseObj.toString() + "】");
        Object object = parseObj.get("status");
        XianYu bean = new XianYu();
        if ("1".equals(object.toString())) {
            bean = JSONUtil.toBean(parseObj, XianYu.class);
        } else {
            bean.setPayurl(parseObj.get("error").toString());
            bean.setStatus("0");
        }
        //{"status":1,"payurl":"trade_no=2020051404200399991055074076&biz_sub_type=peerpay_trade&presessionid=&app=tb&channel=&type2=gulupay&bizcontext={\"biz_type\":\"share_pp_pay\",\"type\":\"qogirpay\"}"}
        return bean;
    }

}
