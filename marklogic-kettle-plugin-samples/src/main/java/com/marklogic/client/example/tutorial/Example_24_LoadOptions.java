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


/**
 * LoadOptions shows how to define and upload query options
 * (a collection constraint in this case).
 */
public class Example_24_LoadOptions {

	public static void main(String[] args) throws IOException {
		System.out.println("example: "+Example_24_LoadOptions.class.getName());

		// create the client, connecting as the rest-admin user
		DatabaseClient client = DatabaseClientFactory.newClient(
    	    Config.host, 
		    Config.port,
		    Config.admin_user,
		    Config.admin_password, 
		    Config.authType);

		// get an options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

		// Create a builder for constructing query configurations.
		QueryOptionsBuilder qob = new QueryOptionsBuilder();

		// create the query options, defining a collection constraint
		QueryOptionsHandle optsHandle = new QueryOptionsHandle().withConstraints(
		    qob.constraint("tag",
		        qob.collection("")));

		// write the query options to the database
		optionsMgr.writeOptions("tutorial", optsHandle);
		
		System.out.println("Wrote \"tutorial\" options to server.");
		
		// release the client
		client.release();
	}
}
