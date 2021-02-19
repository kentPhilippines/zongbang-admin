package alipay.manage.api.channel.amount.recharge.usdt;

import alipay.config.redis.RedisUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import otc.common.PayApiConstant;

import java.util.Map;

@RequestMapping(PayApiConstant.Alipay.ORDER_API + "/USDTInfo")
@RestController
public class UsdtPayApi implements USDT {
    public static final Log log = LogFactory.get();
    @Autowired
    RedisUtil redis;

    @GetMapping()
    public USDTInfo getBankCardInfo(String orderId) {
        USDTInfo info = new USDTInfo();
        log.info("【收到用户请求USDT钱包地址参数，当前请求订单号为：" + orderId + "】");
        try {
            Map<Object, Object> hmget = redis.hmget(MARS + orderId);
            info.setAddress(hmget.get(ADDRESS).toString());
        } catch (Exception e) {
            log.info("【请求缓存银行卡数据失败，当前请求订单号：" + orderId + "】");
        }
        log.info("【当前银行卡信息为：" + info.toString() + "】");
        return info;
    }
}

class USDTInfo {
    private String address;
    private String scan;

    public String getScan() {
        return scan;
    }

    public void setScan(String scan) {
        this.scan = scan;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}