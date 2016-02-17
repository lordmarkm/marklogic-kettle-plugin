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
import java.util.Arrays;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.config.QueryOptions;
import com.marklogic.client.admin.config.QueryOptions.Facets;
import com.marklogic.client.admin.config.QueryOptions.FragmentScope;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.FacetResult;
import com.marklogic.client.query.FacetValue;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

/**
 * SearchWithFacets shows how to provide the basis for faceted navigation,
 * combining both search and analytics.
 */
public class Example_40_SearchWithFacets {

	public static void main(String[] args) throws IOException {
		System.out.println("example: "+Example_40_SearchWithFacets.class.getName());

		// create the client, connecting as the rest-admin user
		DatabaseClient client = DatabaseClientFactory.newClient(
    	    Config.host, 
		    Config.port,
		    Config.admin_user,
		    Config.admin_password, 
		    Config.authType);

		/**** SERVER CONFIGURATION ****/
		// provide a unique options name for this example
		String optionsName = Example_40_SearchWithFacets.class.getSimpleName();
		
		// get an options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

		// create a builder for constructing query options
		QueryOptionsBuilder qob = new QueryOptionsBuilder();

		QueryOptionsHandle options = new QueryOptionsHandle().withConstraints(
		    // expose the "contentRating" JSON key range index as "rating" values
		    qob.constraint("rating",
	            qob.range(
	                qob.jsonRangeIndex("contentRating",
	                    qob.rangeType("xs:int")),
	                Facets.FACETED,
	                FragmentScope.DOCUMENTS,
	                qob.buckets(),
	                "descending")), // highest ratings first

	        // expose the "affiliation" JSON key range index as "company" values
	        qob.constraint("company",
	            qob.range(
	                qob.jsonRangeIndex("affiliation",
	                    qob.stringRangeType(QueryOptions.DEFAULT_COLLATION)),
	                Facets.FACETED,
	                FragmentScope.DOCUMENTS,
	                qob.buckets(),
	                "frequency-order"))); // most common values first
		
		// write the query options to the database
		optionsMgr.writeOptions(optionsName, options);
				
		System.out.println("Wrote " + optionsName + " options to server.");
		
		/**** SEARCH ****/
		// create a manager for searching
		QueryManager queryMgr = client.newQueryManager();

		// create a search definition
		StringQueryDefinition query = queryMgr.newStringDefinition(optionsName);
		
		// create a handle for the search results
		SearchHandle resultsHandle = new SearchHandle();

		// Restrict the search to the "mlw2012" collection
		query.setCollections("mlw2012");

		String[] searches = {"", // empty search; return all results
		                     "company:MarkLogic",
		                     "company:MarkLogic rating:5",
		                     "java rating GE 4"};
		
		// Run each search
		for (String search : Arrays.asList(searches)) {
			
		    System.out.println("SEARCH RESULTS for \""+search+"\":");
			
		    // Return all results
			query.setCriteria(search);

			// run the search
			queryMgr.search(query, resultsHandle);

			// Show the resulting facets & their values
			for (FacetResult facet : resultsHandle.getFacetResults()) {
				System.out.println(facet.getName() + ":");
				for (FacetValue value : facet.getFacetValues()) {
					System.out.println("  " + value.getCount() + " occurrences of " + value.getName());
				}
			}
			
			// Format the results
			TutorialUtil.displayResults(resultsHandle);			
		}
		
		// release the client
		client.release();
	}
}
