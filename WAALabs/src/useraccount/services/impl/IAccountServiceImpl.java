package useraccount.services.impl;

import myframework.annotations.MyBean;
import useraccount.services.interfaces.IAccountService;

@MyBean
public class IAccountServiceImpl implements IAccountService {
	@Override
	public Long getAccountNumber(String userName) {
		return 12345689L;
	}
}
