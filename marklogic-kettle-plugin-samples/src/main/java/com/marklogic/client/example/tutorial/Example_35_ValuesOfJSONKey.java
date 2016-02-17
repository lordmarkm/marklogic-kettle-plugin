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
import com.marklogic.client.admin.config.QueryOptions;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.query.CountedDistinctValue;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.ValuesDefinition;

/**
 * ValuesOfJSONKey shows how to retrieve values from a JSON key range index.
 */
public class Example_35_ValuesOfJSONKey {

	public static void main(String[] args) throws IOException {
		System.out.println("example: "+Example_35_ValuesOfJSONKey.class.getName());

		// create the client, connecting as the rest-admin user
		DatabaseClient client = DatabaseClientFactory.newClient(
    	    Config.host, 
		    Config.port,
		    Config.admin_user,
		    Config.admin_password, 
		    Config.authType);

		/**** SERVER CONFIGURATION ****/
		// provide a unique options name for this example
		String optionsName = Example_35_ValuesOfJSONKey.class.getSimpleName();
		
		// get an options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

		// create a builder for constructing query options
		QueryOptionsBuilder qob = new QueryOptionsBuilder();

		// expose the "affiliation" JSON key range index as "company" values
		QueryOptionsHandle options = new QueryOptionsHandle().withValues(
	        qob.values("company",
	            qob.range(
	                qob.jsonRangeIndex("affiliation",
	                    qob.stringRangeType(QueryOptions.DEFAULT_COLLATION))),
	            "frequency-order"));

		// write the query options to the database
		optionsMgr.writeOptions(optionsName, options);
				
		System.out.println("Wrote " + optionsName + " options to server.");
		
		/**** VALUES RETRIEVAL ****/
		// create a manager for searching
		QueryManager queryMgr = client.newQueryManager();

		// create a values definition
		ValuesDefinition valuesDef = queryMgr.newValuesDefinition("company", optionsName);
		
		// retrieve the values
		ValuesHandle valuesHandle = queryMgr.values(valuesDef, new ValuesHandle());

		// print out the values and their frequencies
		for (CountedDistinctValue value : valuesHandle.getValues()) {
		    System.out.println(
		        value.get("xs:string",String.class) + ": " + value.getCount());
		}

		// release the client
		client.release();
	}
}
