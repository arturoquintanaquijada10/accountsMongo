package com.sunhill.accountssunhill.service.dto;

import javax.validation.constraints.NotNull;

public class ServiceNumberAccount {
	
	@NotNull
	private String accountNumber;	
	
	public ServiceNumberAccount(@NotNull String accountNumber) {
		super();
		this.accountNumber = accountNumber;
	}		

	public ServiceNumberAccount(){}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

}
