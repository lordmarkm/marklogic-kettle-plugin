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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.document.BinaryDocumentManager.MetadataExtraction;
import com.marklogic.client.io.InputStreamHandle;

/**
 * CreateBinary illustrates how to write binary content to a database document,
 * as well as how to enable automatic binary metadata extraction into document properties.
 */
public class Example_04_CreateBinary {

	public static void main(String[] args) throws IOException {

		System.out.println("example: "+Example_04_CreateBinary.class.getName());

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(Config.host, Config.port, Config.user, Config.password, Config.authType);
		
		// acquire the content
		InputStream pngStream = Example_04_CreateBinary.class.getClassLoader().getResourceAsStream(
			"data"+File.separator+"mlfavicon.png");

		// create a manager for binary documents
		BinaryDocumentManager docMgr = client.newBinaryDocumentManager();

		// enable automatic metadata extraction into properties
		docMgr.setMetadataExtraction(MetadataExtraction.PROPERTIES);

		// create a handle on the document's content
		InputStreamHandle content = new InputStreamHandle(pngStream);
		
		// write the document content
		docMgr.write("/example/mlfavicon.png", content);

		System.out.println("Wrote /example/mlfavicon.png content with metadata extraction enabled.");

		// release the client
		client.release();
	}
}
