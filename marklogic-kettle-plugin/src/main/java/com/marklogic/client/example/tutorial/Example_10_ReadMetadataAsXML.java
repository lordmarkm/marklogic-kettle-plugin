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
import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.io.StringHandle;

/**
 * ReadMetadataAsXML shows how to retrieve a document's metadata as XML.
 */
public class Example_10_ReadMetadataAsXML {

	public static void main(String[] args) throws IOException {

		System.out.println("example: "+Example_10_ReadMetadataAsXML.class.getName());

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(Config.host, Config.port, Config.user, Config.password, Config.authType);

		// get a manager for binary documents
		BinaryDocumentManager docMgr = client.newBinaryDocumentManager();

		// create a handle to receive the document metadata as XML
		StringHandle metadata = new StringHandle();
		
		// read just the document's metadata
		docMgr.readMetadata("/example/mlfavicon.png", metadata);

		// dump the metadata as raw XML
		System.out.println(metadata);

		// release the client
		client.release();
	}
}
