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
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

/**
 * SearchWithPageSize illustrates searching for terms among all documents,
 * setting a custom results page size, and retrieving a subsequent results page.
 */
public class Example_17_SearchWithPageSize {

	public static int PAGE_SIZE = 5;
	
	public static void main(String[] args) throws IOException {
		System.out.println("example: "+Example_17_SearchWithPageSize.class.getName());

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(Config.host, Config.port, Config.user, Config.password, Config.authType);

		// create a manager for searching
		QueryManager queryMgr = client.newQueryManager();
		
		// Set page size to 5 results
		queryMgr.setPageLength(PAGE_SIZE);

		// create a search definition
		StringQueryDefinition query = queryMgr.newStringDefinition();
		query.setCriteria("index OR Cassel NEAR Hare");

		// create a handle for the search results
		SearchHandle resultsHandle = new SearchHandle();

		// get the 3rd page of search results
		int pageNum = 3;
		int start = PAGE_SIZE * (pageNum - 1) + 1;

		// get search results starting with the nth result
		queryMgr.search(query, resultsHandle, start);

		// Format the results
		TutorialUtil.displayResults(resultsHandle);

		// release the client
		client.release();
	}
}
