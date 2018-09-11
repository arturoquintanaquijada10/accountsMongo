Accounts 
===


API Rest for Accounts management.

- **JAVA :  		1.8**
- **SPRING-BOOT : 	2.0.4.RELEASE**
- **SPRING-DATA : 	2.0.9.RELEASE**
- **MONGODB 4.0  : Allow transacctions**
- **SWAGGER  :  http://localhost:8080/swagger-ui.html**
- **JUNIT TEST included**


### Instructions

It's necessary a mongoDB 4 installed, a repliset configured:

docs :  https://docs.mongodb.com

help: 

elp: 

mongod --replSet rs1 --logpath "1.log" --dbpath /data/rs1 --port 27020 --bind_ip 127.0.0.1

mongod --replSet rs1 --logpath "2.log" --dbpath /data/rs2 --port 27018 --bind_ip 127.0.0.1

mongod --replSet rs1 --logpath "3.log" --dbpath /data/rs3 --port 27019 --bind_ip 127.0.0.1

mongo --port 27020

rs.initiate( { _id : "rs1",  members: [ 
      { _id: 0, host: "127.0.0.1:27020" },
            { _id: 1, host: "127.0.0.1:27018" },
	          { _id: 2, host: "127.0.0.1:27019" } ]})
		  	  
rs.slaveOk()  to allow read in secondaries nodes

