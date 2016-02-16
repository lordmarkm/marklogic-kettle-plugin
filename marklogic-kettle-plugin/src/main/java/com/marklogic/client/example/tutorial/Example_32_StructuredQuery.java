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
 * StructuredQuery shows how to define and use a structured query.
 */
public class Example_32_StructuredQuery {

	public static void main(String[] args) throws IOException {
		System.out.println("example: "+Example_32_StructuredQuery.class.getName());

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(Config.host, Config.port, Config.user, Config.password, Config.authType);
		
		// create a manager for searching
		QueryManager queryMgr = client.newQueryManager();

		// create a query builder using the "tutorial" options
        StructuredQueryBuilder qb = new StructuredQueryBuilder("tutorial");
		
		// build a search definition
		StructuredQueryDefinition query =
		    qb.or(
		        // NB: constraints will only work correctly if they've been configured
		    	// find MarkLogic speakers whose bio mentions "product"
		        qb.and(
		            qb.wordConstraint("bio","product"),
		            qb.valueConstraint("company","MarkLogic")),
		        // find plays matching all three of these constraints
		        qb.and(
		            qb.elementConstraint("spoken", qb.term("fie")),
		            qb.wordConstraint("stagedir", "fall"),
		            qb.valueConstraint("person", "GRUMIO")),
		        // find photos of fish taken on February 27th
		        qb.and(
		            qb.properties(qb.term("fish")),
		            qb.directory(true, "/images/2012/02/27/")),
		        // find conference docs mentioning "fun"
		        qb.and(
		            qb.collection("mlw2012"),
		            qb.term("fun")));
		
		// run the search
		SearchHandle resultsHandle = queryMgr.search(query, new SearchHandle());

		// format the results
		TutorialUtil.displayResults(resultsHandle);

		// release the client
		client.release();
	}
}
