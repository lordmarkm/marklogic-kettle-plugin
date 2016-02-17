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
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;

/**
 * SearchProperties illustrates searching among document properties.
 */
public class Example_18_SearchProperties {

	public static void main(String[] args) throws IOException {
		System.out.println("example: "+Example_18_SearchProperties.class.getName());

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(Config.host, Config.port, Config.user, Config.password, Config.authType);

		// create a manager for searching
		QueryManager queryMgr = client.newQueryManager();

		// get a structured query builder
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		
		// Find all documents that have a property value containing the word "fish"
		StructuredQueryDefinition query = qb.properties(qb.term("fish"));

		// create a handle for the search results
		SearchHandle resultsHandle = new SearchHandle();

		// run the search
		queryMgr.search(query, resultsHandle);

		// Format the results
		TutorialUtil.displayResultURIs(resultsHandle);

		// release the client
		client.release();
	}
}
