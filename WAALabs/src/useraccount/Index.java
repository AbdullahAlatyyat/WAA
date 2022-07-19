package useraccount;

import myframework.MyInjector;

public class Index {

	public static void main(String[] args) {
		MyInjector.startApplication(Index.class);
		MyInjector.getService(UserAccountComponent.class).displayUserAccount();
	}
}
