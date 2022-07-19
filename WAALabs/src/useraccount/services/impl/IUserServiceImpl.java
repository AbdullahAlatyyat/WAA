package useraccount.services.impl;

import myframework.annotations.MyBean;
import useraccount.services.interfaces.IUserService;

@MyBean
public class IUserServiceImpl implements IUserService {
	@Override
	public String getUserName() {
		return "username";
	}
}
