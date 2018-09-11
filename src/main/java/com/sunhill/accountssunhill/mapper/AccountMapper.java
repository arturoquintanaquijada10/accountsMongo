package com.sunhill.accountssunhill.mapper;

import org.springframework.stereotype.Component;

import com.sunhill.accountssunhill.dto.Account;
import com.sunhill.accountssunhill.dto.Balance;
import com.sunhill.accountssunhill.dto.NumberAccount;
import com.sunhill.accountssunhill.dto.Owner;
import com.sunhill.accountssunhill.dto.Transfers;
import com.sunhill.accountssunhill.service.dto.ServiceBalance;
import com.sunhill.accountssunhill.service.dto.ServiceCheckingAccount;
import com.sunhill.accountssunhill.service.dto.ServiceNumberAccount;
import com.sunhill.accountssunhill.service.dto.ServiceSavingAccount;
import com.sunhill.accountssunhill.service.dto.ServiceTransfers;
import com.sunhill.accountssunhill.util.Constants;


@Component
public class AccountMapper implements IAccountMapper{

	@Override
	public Account getSavingAccount(ServiceSavingAccount oServiceSavingAccount){		
		Owner oOwner=new Owner(oServiceSavingAccount.getDocumentNumber(), oServiceSavingAccount.getName() , oServiceSavingAccount.getSurname());		
		return new Account( oServiceSavingAccount.getAccount(),  oServiceSavingAccount.getBalance(),  oOwner, Constants.ACCOUNT_TYPE_SAVING, oServiceSavingAccount.getInterest());
	}
	
	@Override
	public Account getCheckingAccount(ServiceCheckingAccount oServiceCheckingAccount){		
		Owner oOwner=new Owner(oServiceCheckingAccount.getDocumentNumber(), oServiceCheckingAccount.getName() , oServiceCheckingAccount.getSurname());
		return new Account( oServiceCheckingAccount.getAccount(),  oServiceCheckingAccount.getBalance(),  oOwner, Constants.ACCOUNT_TYPE_CHECKING,  oServiceCheckingAccount.getLimit());		
	}
	
	@Override
	public Transfers getTransfers(ServiceTransfers oServiceTransfers){		
		 return new Transfers(oServiceTransfers.getAccountFrom(), oServiceTransfers.getAccountTo(), oServiceTransfers.getAmount());			
	}
	
	@Override
	public Balance getBalance(ServiceBalance oServiceBalance){		
		return new Balance(oServiceBalance.getAccount(),  oServiceBalance.getAmount());
	}
	
	@Override
	public NumberAccount getNumberAccount(ServiceNumberAccount oServiceNumberAccount){		
		return new NumberAccount(oServiceNumberAccount.getAccountNumber());
	}
	
	
}
