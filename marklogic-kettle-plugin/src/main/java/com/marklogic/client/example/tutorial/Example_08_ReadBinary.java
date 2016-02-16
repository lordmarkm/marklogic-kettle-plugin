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
import java.util.Map;

import javax.xml.namespace.QName;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DocumentMetadataHandle;

/**
 * ReadBinary illustrates how to read binary content from the database,
 * as well as how to retrieve a document's associated properties.
 */
public class Example_08_ReadBinary {

	public static void main(String[] args) throws IOException {

		System.out.println("example: "+Example_08_ReadBinary.class.getName());

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(Config.host, Config.port, Config.user, Config.password, Config.authType);

		// get a manager for binary documents
		BinaryDocumentManager docMgr = client.newBinaryDocumentManager();

		// create a handle to receive the document content
		BytesHandle content = new BytesHandle();

		// create a handle to receive the document metadata
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		
		// read the document content & metadata
		docMgr.read("/example/mlfavicon.png", metadata, content);

		// get the document content as a byte array
		byte[] contentBytes = content.get();

		// print the image size to the console
		System.out.println("Binary document size in bytes: " + contentBytes.length);

		// iterate over the properties and print each one
		for (Map.Entry<QName,Object> prop : metadata.getProperties().entrySet()) {
		    System.out.println(prop.getKey().getLocalPart() + ": " + prop.getValue());
		}

		// release the client
		client.release();
	}
}
