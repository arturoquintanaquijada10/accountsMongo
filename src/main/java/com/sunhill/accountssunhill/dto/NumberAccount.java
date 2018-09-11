package com.sunhill.accountssunhill.dto;

import javax.validation.constraints.NotNull;

public class NumberAccount {
	
	@NotNull
	private String account;	

	public NumberAccount(@NotNull String account) {
		super();
		this.account = account;
	}
	
	public NumberAccount(){}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
	
	

}
