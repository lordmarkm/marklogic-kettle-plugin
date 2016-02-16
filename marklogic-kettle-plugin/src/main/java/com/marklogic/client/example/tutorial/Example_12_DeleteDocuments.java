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
import com.marklogic.client.document.GenericDocumentManager;

/**
 * DeleteDocuments illustrates how to delete database documents.
 */
public class Example_12_DeleteDocuments {

	public static void main(String[] args) throws IOException {
		System.out.println("example: "+Example_12_DeleteDocuments.class.getName());

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(Config.host, Config.port, Config.user, Config.password, Config.authType);

		// create a generic manager for documents
		GenericDocumentManager docMgr = client.newDocumentManager();

		// delete the documents
		docMgr.delete("/example/flipper.json");
		docMgr.delete("/example/flipper.xml");
		docMgr.delete("/example/foo.txt");
		docMgr.delete("/example/mlfavicon.png");

		System.out.println("Deleted four example docs.");

		// release the client
		client.release();
	}
}
