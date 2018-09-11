package com.sunhill.accountssunhill.mapper;


import com.sunhill.accountssunhill.dto.Account;
import com.sunhill.accountssunhill.dto.Balance;
import com.sunhill.accountssunhill.dto.NumberAccount;
import com.sunhill.accountssunhill.dto.Transfers;
import com.sunhill.accountssunhill.service.dto.ServiceBalance;
import com.sunhill.accountssunhill.service.dto.ServiceCheckingAccount;
import com.sunhill.accountssunhill.service.dto.ServiceNumberAccount;
import com.sunhill.accountssunhill.service.dto.ServiceSavingAccount;
import com.sunhill.accountssunhill.service.dto.ServiceTransfers;

public interface IAccountMapper {

	public Account getSavingAccount(ServiceSavingAccount oServiceSavingAccount);
	
	public Account getCheckingAccount(ServiceCheckingAccount oServiceCheckingAccount);
	
	public Transfers getTransfers(ServiceTransfers oServiceTransfers);
	
	public Balance getBalance(ServiceBalance oServiceBalance);
	
	public NumberAccount getNumberAccount(ServiceNumberAccount oServiceNumberAccount);
	
	
}
