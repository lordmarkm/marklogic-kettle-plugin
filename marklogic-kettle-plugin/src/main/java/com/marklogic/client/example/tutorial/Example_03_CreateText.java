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
 * CreateText illustrates how to write text content to a database document,
 * as well as how to associate a document with a collection tag.
 */
public class Example_03_CreateText {

	public static void main(String[] args) throws IOException {

		System.out.println("example: "+Example_03_CreateText.class.getName());

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(Config.host, Config.port, Config.user, Config.password, Config.authType);
		
		// create a manager for XML documents
		TextDocumentManager docMgr = client.newTextDocumentManager();

		// create a handle on the document's content
		StringHandle content = new StringHandle("some text");
		
		// create a handle for the document's associated metadata
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();

		// add a collection tag
		metadata.getCollections().addAll("myCollection");

		// write the document content & metadata
		docMgr.write("/example/foo.txt", metadata, content);

		System.out.println("Wrote /example/foo.txt content in the 'myCollection' collection.");

		// release the client
		client.release();
	}
}
