package com.sunhill.accountssunhill.manage;


import org.json.simple.JSONObject;

import com.sunhill.accountssunhill.dto.Account;
import com.sunhill.accountssunhill.dto.Balance;
import com.sunhill.accountssunhill.dto.NumberAccount;
import com.sunhill.accountssunhill.dto.Transfers;
import com.sunhill.accountssunhill.exception.GenericException;

public interface IManageAccounts {
	public  JSONObject createAccount(Account oAccount);		
	
	public  JSONObject getAccountJS(String account)throws GenericException;
	
	public  Account getAccount(String account) throws GenericException;
	
	public  JSONObject manageBalanceAccount(Balance oBalance) throws GenericException ;		
	
	public  JSONObject payInterest(NumberAccount  oNumberAccount ) throws GenericException;
	
	public  JSONObject calculateInterest(String account) throws GenericException ;
	
	public  JSONObject doTransfer(Transfers oTransfers) throws Exception ;
	
}
