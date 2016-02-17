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

import javax.xml.namespace.QName;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.config.QueryOptions;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.TuplesHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.Tuple;
import com.marklogic.client.query.ValuesDefinition;

/**
 * Tuples shows how to retrieve unique co-occurrences of multiple values.
 */
public class Example_39_Tuples {

	public static void main(String[] args) throws IOException {
		System.out.println("example: "+Example_39_Tuples.class.getName());

		// create the client, connecting as the rest-admin user
		DatabaseClient client = DatabaseClientFactory.newClient(
    	    Config.host, 
		    Config.port,
		    Config.admin_user,
		    Config.admin_password, 
		    Config.authType);

		/**** SERVER CONFIGURATION ****/
		// provide a unique options name for this example
		String optionsName = Example_39_Tuples.class.getSimpleName();
		
		// get an options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

		// create a builder for constructing query options
		QueryOptionsBuilder qob = new QueryOptionsBuilder();

		// expose unique combinations (co-occurrences) of size and exposure
		QueryOptionsHandle options = new QueryOptionsHandle().withTuples(
            qob.tuples("size-exposure",
	            qob.tupleSources(
	                qob.range(
	                    qob.elementRangeIndex(new QName("http://marklogic.com/filter","size"),
	                        qob.rangeType("xs:unsignedLong"))),
	                qob.range(
	                    qob.elementRangeIndex(new QName("http://marklogic.com/filter","Exposure_Time"),
		                    qob.stringRangeType(QueryOptions.DEFAULT_COLLATION))))));
		
		// write the query options to the database
		optionsMgr.writeOptions(optionsName, options);
				
		System.out.println("Wrote " + optionsName + " options to server.");
		
		/**** VALUES RETRIEVAL ****/
		// create a manager for searching
		QueryManager queryMgr = client.newQueryManager();

		// create a values definition
		ValuesDefinition valuesDef = queryMgr.newValuesDefinition("size-exposure", optionsName);

		// retrieve the tuples
		TuplesHandle tuplesHandle = queryMgr.tuples(valuesDef, new TuplesHandle());

		// print out each size/exposure co-occurrence
		for (Tuple tuple : tuplesHandle.getTuples()) {
			System.out.println("Size: "     + tuple.getValues()[0].get(Long.class)
					       + "\nExposure: " + tuple.getValues()[1].get(String.class));
			System.out.println();			
		}

		// release the client
		client.release();
	}
}
