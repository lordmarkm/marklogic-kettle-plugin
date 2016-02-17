/*
 * Copyright 2012 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.example.tutorial;

import java.io.IOException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.query.CountedDistinctValue;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.ValuesDefinition;
import com.marklogic.client.query.ValuesDefinition.Direction;

/**
 * ValuesWithQuery shows how to retrieve values, restricted by a query.
 */
public class Example_38_ValuesWithQuery {

	public static void main(String[] args) throws IOException {
		System.out.println("example: "+Example_38_ValuesWithQuery.class.getName());

		// create the client, connecting as the rest-admin user
		DatabaseClient client = DatabaseClientFactory.newClient(
    	    Config.host, 
		    Config.port,
		    Config.admin_user,
		    Config.admin_password, 
		    Config.authType);

		/**** SERVER CONFIGURATION ****/
		// provide a unique options name for this example
		String optionsName = Example_38_ValuesWithQuery.class.getSimpleName();
		
		// get an options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

		// create a builder for constructing query options
		QueryOptionsBuilder qob = new QueryOptionsBuilder();

		QueryOptionsHandle options = new QueryOptionsHandle()
		// expose the "contentRating" JSON key range index as "rating" values
		.withValues(
	        qob.values("rating",
	            qob.range(
	                qob.jsonRangeIndex("contentRating",
	                    qob.rangeType("xs:int")))))
	    // optionally constrain results by affiliation
        .withConstraints(
	        qob.constraint("company",
	            qob.value(
	                qob.jsonTermIndex("affiliation")
	            )));
		
		// write the query options to the database
		optionsMgr.writeOptions(optionsName, options);
				
		System.out.println("Wrote " + optionsName + " options to server.");
		
		/**** VALUES RETRIEVAL ****/
		// create a manager for searching
		QueryManager queryMgr = client.newQueryManager();

		// create a values definition
		ValuesDefinition valuesDef = queryMgr.newValuesDefinition("rating", optionsName);

		// create a search definition
		StringQueryDefinition companyQuery = queryMgr.newStringDefinition("tutorial");
		companyQuery.setCriteria("company:marklogic");

		// return only those values from fragments (documents) matching this query
		valuesDef.setQueryDefinition(companyQuery);

		// also retrieve the mean average of all ratings
		valuesDef.setAggregate("avg","median");
		
		// retrieve values in descending order
		valuesDef.setDirection(Direction.DESCENDING);
		
		// retrieve the values
		ValuesHandle valuesHandle = queryMgr.values(valuesDef, new ValuesHandle());

		// print out the values and their frequencies
		for (CountedDistinctValue value : valuesHandle.getValues()) {
		    System.out.println(
		        value.get("xs:int",Integer.class) + ": " + value.getCount());
		}

		System.out.println("Median average: " +
		    valuesHandle.getAggregate("median").getValue());
		
		System.out.println("Mean average: " +
		    valuesHandle.getAggregate("avg").getValue());

		// release the client
		client.release();
	}
}
