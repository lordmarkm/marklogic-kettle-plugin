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
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.InputStreamHandle;

/**
 * CreateXML illustrates how to write XML content to a database document.
 */
public class Example_02_CreateXML {

	public static void main(String[] args) throws IOException {

		System.out.println("example: "+Example_02_CreateXML.class.getName());

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(Config.host, Config.port, Config.user, Config.password, Config.authType);
		
		// acquire the content
		InputStream docStream = Example_02_CreateXML.class.getClassLoader().getResourceAsStream(
			"data"+File.separator+"flipper.xml");

		// create a manager for XML documents
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// create a handle on the content
		InputStreamHandle handle = new InputStreamHandle(docStream);

		// write the document content
		docMgr.write("/example/flipper.xml", handle);

		System.out.println("Wrote /example/flipper.xml content");

		// release the client
		client.release();
	}
}
