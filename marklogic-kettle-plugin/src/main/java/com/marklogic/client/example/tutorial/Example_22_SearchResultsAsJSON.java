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
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

/**
 * SearchResultsAsJSON illustrates returning search results as JSON.
 */
public class Example_22_SearchResultsAsJSON {

	public static void main(String[] args) throws IOException {
		System.out.println("example: "+Example_22_SearchResultsAsJSON.class.getName());

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(Config.host, Config.port, Config.user, Config.password, Config.authType);

		// create a manager for searching
		QueryManager queryMgr = client.newQueryManager();

		// create a search definition
		StringQueryDefinition query = queryMgr.newStringDefinition();
		
		// Restrict the search to the "shakespeare" collection
		query.setCollections("shakespeare");

		// Search for the term "flower"
		query.setCriteria("flower");
		
		// create a handle for the search results to be received as raw JSON
		StringHandle resultsHandle = new StringHandle().withFormat(Format.JSON);

		// run the search
		queryMgr.search(query, resultsHandle);

		// dump the JSON results to the console
		System.out.println(resultsHandle);

		// release the client
		client.release();
	}
}
