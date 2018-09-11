package com.sunhill.accountssunhill.manage;


import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;

import java.math.BigDecimal;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;
import com.mongodb.MongoCommandException;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.sunhill.accountssunhill.dto.Account;
import com.sunhill.accountssunhill.dto.Balance;
import com.sunhill.accountssunhill.dto.NumberAccount;
import com.sunhill.accountssunhill.dto.Transfers;
import com.sunhill.accountssunhill.exception.GenericException;
import com.sunhill.accountssunhill.util.Constants;

@Component
@Qualifier("manageAccounts")
public class ManageAccounts implements IManageAccounts{
	
	@Autowired
	private Environment env;	

	@Autowired
	private MongoClient mongoClient;

	@Autowired
	MongoDbFactory mongoDbFactory;

	@Autowired
	MongoTemplate mongoTemplate;		 

	@Override
	public  JSONObject doTransfer(Transfers oTransfers) throws Exception {

		ClientSession session = mongoClient.startSession(); 

		try {
			
			Account oAccountFrom=getAccount(oTransfers.getAccountFrom());
			Account oAccountTo=getAccount(oTransfers.getAccountTo());
			
			if(!Constants.ACCOUNT_TYPE_CHECKING.equals(oAccountFrom.getAccountType())   || !Constants.ACCOUNT_TYPE_CHECKING.equals(oAccountTo.getAccountType()) ){
				throw new GenericException(env.getProperty("app.message.operation.notallowed"), HttpStatus.OK);
			}
			
			
			session.startTransaction(TransactionOptions.builder().writeConcern(WriteConcern.MAJORITY).build());

			manageBalanceAccount(new Balance( oTransfers.getAccountFrom(),  (oTransfers.getAmount()).negate()),oAccountFrom, session);
			manageBalanceAccount(new Balance( oTransfers.getAccountTo(),  oTransfers.getAmount()), oAccountTo, session);			

			session.commitTransaction();
			
		} catch (MongoCommandException e) {
			session.abortTransaction();
			throw e;
		} catch (Exception e) {
			session.abortTransaction();
			throw e;		
		} finally {
			session.close();
		}
		return getJsonResponse( env.getProperty("app.message.transfer.done"));
		
	}
	
	@Override
	public  JSONObject createAccount(Account oAccount) {
		mongoTemplate.insert(oAccount);
		return getJsonResponse( env.getProperty("app.message.account.created"));				
	}		
	
	
	
	@Override
	public  JSONObject calculateInterest(String account) throws GenericException {
		
		Account oAccount=getAccount(account);	
		if(!(Constants.ACCOUNT_TYPE_SAVING.equals(oAccount.getAccountType()))){
			throw new GenericException(env.getProperty("app.message.operation.notallowed"), HttpStatus.OK);
		}			
		return getJsonResponse( Constants.FIELD_ACCOUNT_INTEREST+((oAccount.getBalance()).multiply(oAccount.getExtra())).divide(new BigDecimal(100)));
	}	
	
	
	@Override
	public  Account getAccount(String account) throws GenericException {
		Query query = new Query();
		Account oAccount=null;
		try{
			query.addCriteria(Criteria.where(Constants.COLLECTION_ID).is(account));		
			oAccount=mongoTemplate.findOne(query, Account.class);		
			if(oAccount==null){	throw new GenericException(env.getProperty("app.message.error.notfound"));	}		
		}catch(Exception e){
			throw new GenericException(env.getProperty("app.message.error.notfound"));
		}		
		return oAccount;
	}	
	
	@Override
	public  JSONObject payInterest(NumberAccount  oNumberAccount ) throws GenericException {
		
		Account oAccount=getAccount(oNumberAccount.getAccount());	
		if(!(Constants.ACCOUNT_TYPE_SAVING.equals(oAccount.getAccountType()))){
			throw new GenericException(env.getProperty("app.message.operation.notallowed"), HttpStatus.OK);
		}
		BigDecimal interest=((oAccount.getBalance()).multiply(oAccount.getExtra())).divide(new BigDecimal(100));		
		manageBalanceAccount(new Balance( oNumberAccount.getAccount(),interest), oAccount, null);			
		return getJsonResponse( Constants.FIELD_ACCOUNT_INTEREST+env.getProperty("app.message.paid.interest")+interest);				
	}	
	
	
	

	@Override
	public  JSONObject manageBalanceAccount(Balance oBalance) throws GenericException {
		manageBalanceAccount( oBalance, null, null);		
		return getJsonResponse( env.getProperty("app.message.balance.updated"));	
	}

	
	public  boolean manageBalanceAccount(Balance oBalance, Account oAccount, ClientSession session) throws GenericException {

		if(oAccount==null){
			oAccount=getAccount(oBalance.getAccount());
		}
		
		allowBalance(oBalance);

		MongoCollection<Document> collectionAccounts=mongoTemplate.getCollection(Constants.COLLECTION_ACCOUNT_NAME);
		Bson filterAccount = eq(Constants.COLLECTION_ID, oBalance.getAccount());		
		Bson incrementBalance = inc(Constants.FIELD_ACCOUNT_BALANCE, oBalance.getAmount());				

		if(session == null){
			collectionAccounts.updateOne( filterAccount, incrementBalance);	
		}else{
			collectionAccounts.updateOne( session, filterAccount, incrementBalance);	 
		}
		return true;

	}
	
	
	private boolean allowBalance(Balance oBalance, Account oAccount) throws GenericException{

		if(oBalance.getAmount().signum()==-1){
						
			if(isPositive(oAccount, oBalance , new BigDecimal(0))){
				return true;
			}else{
				if(oAccount.getAccountType().equals(Constants.ACCOUNT_TYPE_CHECKING)){
					if(isPositive(oAccount, oBalance , oAccount.getExtra())){					
						return true;
					}else{
						throw new GenericException(env.getProperty("app.message.operation.notallowed"), HttpStatus.OK);
					}
				}else{
					throw new GenericException(env.getProperty("app.message.operation.notallowed"), HttpStatus.OK);					
				}
			}			
		}else{
			return true;
		}

	}
	
	private boolean isPositive(Account oAccount, Balance oBalance, BigDecimal allowed){
		if(oBalance.getAmount().signum()==-1){
			return ((oAccount.getBalance()).add(oBalance.getAmount()).compareTo(allowed.negate())>0);
		}else{
			return ((oAccount.getBalance()).subtract(oBalance.getAmount()).compareTo(allowed.negate())>0);
		}
	}
	
	private boolean allowBalance(Balance oBalance) throws GenericException{

		if(oBalance.getAmount().signum()==-1){
			Account oAccount =getAccount(oBalance.getAccount());			
			
			if(isPositive(oAccount, oBalance , new BigDecimal(0))){
				return true;
			}else{
				if(oAccount.getAccountType().equals(Constants.ACCOUNT_TYPE_CHECKING)){
					if(isPositive(oAccount, oBalance , oAccount.getExtra())){					
						return true;
					}else{
						throw new GenericException(env.getProperty("app.message.operation.notallowed"), HttpStatus.OK);
					}
				}else{
					throw new GenericException(env.getProperty("app.message.operation.notallowed"), HttpStatus.OK);					
				}
			}			
		}else{
			return true;
		}

	}
	
	
	
	private JSONObject getJsonResponse(String sText){
		JSONObject jAccount = new JSONObject();
		jAccount.put(Constants.RESPONSE_RESULT, Constants.RESPONSE_RESULT_OK);		
		jAccount.put(Constants.RESPONSE_MESSAGE, sText);
		return jAccount;
	}
	
	private JSONObject getJsonResponseObject(Object oObject){
		JSONObject jAccount = new JSONObject();
		jAccount.put(Constants.RESPONSE_RESULT, Constants.RESPONSE_RESULT_OK);		
		jAccount.put(Constants.RESPONSE_VALUE, oObject);
		return jAccount;
	}

	@Override
	public  JSONObject getAccountJS(String account) throws GenericException {
		return getJsonResponseObject(getAccount( account));		
	}	
	
}



