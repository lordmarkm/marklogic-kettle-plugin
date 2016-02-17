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
import java.util.Map;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.io.QueryOptionsListHandle;
import com.marklogic.client.query.QueryManager;

/**
 * ListOptionSets shows how to retrieve the list of query options from the server.
 */
public class Example_23_ListOptionSets {

	public static void main(String[] args) throws IOException {
		System.out.println("example: "+Example_23_ListOptionSets.class.getName());

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(Config.host, Config.port, Config.user, Config.password, Config.authType);

		// create a manager for searching
		QueryManager queryMgr = client.newQueryManager();

		// handle for list of named option sets
		QueryOptionsListHandle listHandle = new QueryOptionsListHandle();
		
		// get the list of named option sets
		queryMgr.optionsList(listHandle);
		
		// iterate over the option sets; print each's name & URI
		for (Map.Entry<String,String> optionsSet : listHandle.getValuesMap().entrySet()) {
		    System.out.println(optionsSet.getKey() + ": " + optionsSet.getValue());			
		}

		// release the client
		client.release();
	}
}
