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
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.KeyValueQueryDefinition;
import com.marklogic.client.query.QueryManager;

/**
 * SearchJSONKeyValue illustrates searching for key/value pairs.
 */
public class Example_14_SearchJSONKeyValue {

	public static void main(String[] args) throws IOException {
		System.out.println("example: "+Example_14_SearchJSONKeyValue.class.getName());

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(Config.host, Config.port, Config.user, Config.password, Config.authType);

		// create a manager for searching
		QueryManager queryMgr = client.newQueryManager();

		// create a search definition
		KeyValueQueryDefinition query = queryMgr.newKeyValueDefinition();
		query.put(queryMgr.newKeyLocator("affiliation"),"MarkLogic");

		// create a handle for the search results
		SearchHandle resultsHandle = new SearchHandle();

		// run the search
		queryMgr.search(query, resultsHandle);

		// Show the total number of matching documents
		System.out.println("Found " + resultsHandle.getTotalResults() +
				" documents with 'affiliation' value of 'MarkLogic'\n");

		// Retrieve and display the documents in this result set
		TutorialUtil.displayJSONResultDocs(resultsHandle, client);

		// release the client
		client.release();
	}
}
