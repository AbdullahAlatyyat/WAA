package useraccount;

import myframework.annotations.MyAutowired;
import myframework.annotations.MyBean;

import useraccount.services.interfaces.IAccountService;
import useraccount.services.interfaces.IUserService;

@MyBean
public class UserAccountComponent {

	@MyAutowired
	private IUserService IUserService;

	@MyAutowired
	private IAccountService IAccountService;

	public void displayUserAccount() {
		String username = IUserService.getUserName();
		Long accountNumber = IAccountService.getAccountNumber(username);
		System.out.println("\n\tUser Name: " + username + "\n\tAccount Number: " + accountNumber + "\n");
	}
}
