# nidm@Cloud9z

Sandboxes for Cloud9z simulation workloads

./stockTicker : a standalone java program to insert stock price into a mysql table  

## prereq

* **jq**, a versatile command-line JSON processor.

(https://stedolan.github.io/jq/) for ticker.sh

	sudo yum -y install jq


* **ticker.sh** to Real-time stock tickers from the command-line

* **screen** (optional) to run the java program after term closed

https://github.com/pstadler/ticker.sh

* **java** and **javac** a Java DEV environment

This code is tested using openjdk 17, and should work from 1.8 to 18

## steps

### step1 : Create Database and table 

	$ mysql -u <yourUserID> -p -h <yourMySQLIP> < StockSchema.sql

### step2 : setup env for classpath and env variables

	$ source env.sh 

### step 3: compile

	$ javac stockTicker.java 

### step 4: run   (optional: $ screen )
	
	$ java stockTicker  
