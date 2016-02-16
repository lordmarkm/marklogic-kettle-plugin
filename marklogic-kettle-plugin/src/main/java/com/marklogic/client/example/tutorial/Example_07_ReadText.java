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
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;

/**
 * ReadText illustrates how to read text content from the database,
 * as well as how to retrieve a document's associated collection tags.
 */
public class Example_07_ReadText {

	public static void main(String[] args) throws IOException {

		System.out.println("example: "+Example_07_ReadText.class.getName());

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(Config.host, Config.port, Config.user, Config.password, Config.authType);

		// get a manager for text documents
		TextDocumentManager docMgr = client.newTextDocumentManager();

		// create a handle to receive the document content
		StringHandle content = new StringHandle();

		// create a handle to receive the document metadata
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		
		// read the document content
		docMgr.read("/example/foo.txt", metadata, content);

		// print the document content
		System.out.println(content.get());

		// iterate over the collections and print each one
		for (String collection : metadata.getCollections()) {
			System.out.println("Collection: " + collection);
		}

		// release the client
		client.release();
	}
}
