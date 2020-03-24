package alipay.manage.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import alipay.manage.bean.Withdraw;
import alipay.manage.mapper.WithdrawMapper;
import alipay.manage.service.WithdrawService;
@Component
public class WithdrawServiceImpl implements WithdrawService{
	@Autowired WithdrawMapper withdrawDao;
	@Override
	public boolean addOrder(Withdraw witb) {
		int insertSelective = withdrawDao.insertSelective(witb);
		return insertSelective>0 && insertSelective < 2;
	}

}
