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
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.StringHandle;

/**
 * ReadJSON illustrates how to read JSON content from the database.
 */
public class Example_05_ReadJSON {

	public static void main(String[] args) throws IOException {

		System.out.println("example: "+Example_05_ReadJSON.class.getName());

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(Config.host, Config.port, Config.user, Config.password, Config.authType);

		// create a manager for JSON documents
		JSONDocumentManager docMgr = client.newJSONDocumentManager();

		// create a handle to receive the document content
		StringHandle handle = new StringHandle();

		// read the document content
		docMgr.read("/example/flipper.json", handle);

		// print out the document content
		System.out.println(handle.get());

		// release the client
		client.release();
	}
}
