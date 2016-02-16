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


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.document.BinaryDocumentManager.MetadataExtraction;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;

/**
 * LoadUsingTransaction prepares the database for the tutorial's search/query examples,
 * illustrating how to load multiple documents within a single transaction.
 */
public class Example_13_LoadUsingTransaction {

	public static void main(String[] args) throws Exception {

		System.out.println("example: "+Example_13_LoadUsingTransaction.class.getName());

		// acquire the content
		InputStream xmlList = Example_13_LoadUsingTransaction.class.getClassLoader().getResourceAsStream("xmlList.txt");
		InputStream jsonList = Example_13_LoadUsingTransaction.class.getClassLoader().getResourceAsStream("jsonList.txt");
		InputStream imageList = Example_13_LoadUsingTransaction.class.getClassLoader().getResourceAsStream("imageList.txt");

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(Config.host, Config.port, Config.user, Config.password, Config.authType);
		
		// get an XML document manager
		XMLDocumentManager xmlMgr = client.newXMLDocumentManager();

		// get a JSON document manager
		JSONDocumentManager jsonMgr = client.newJSONDocumentManager();
		
		// get a binary document manager that auto-extracts metadata
		BinaryDocumentManager binaryMgr = client.newBinaryDocumentManager();
		binaryMgr.setMetadataExtraction(MetadataExtraction.PROPERTIES);
		
		// start the transaction
		Transaction transaction = client.openTransaction();
		
		System.out.println("Beginning transaction: " + transaction.getTransactionId());
		
		// load all the documents
		loadDocs(xmlMgr, xmlList, transaction, "shakespeare");
		loadDocs(jsonMgr, jsonList, transaction, "mlw2012");
		loadDocs(binaryMgr, imageList, transaction, "");
		
		// Commit the transaction
		transaction.commit();
		
		System.out.println("Transaction committed.");
		
		// release the client
		client.release();
	}


	private static void loadDocs(DocumentManager<?,? super InputStreamHandle> mgr, InputStream list, Transaction transaction, String collection) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(list));  
		String uri = null;
		while ((uri = br.readLine()) != null)  
		{  
			// get the content and write it to the database
			InputStream docStream = Example_13_LoadUsingTransaction.class.getClassLoader().
					getResourceAsStream("data" + uri);

			InputStreamHandle doc = new InputStreamHandle(docStream);
			
			// Associate the document with the given collection
			DocumentMetadataHandle metadata = new DocumentMetadataHandle();
			if (!collection.equals(""))
				metadata.getCollections().addAll(collection);
		
			try {
				// load each document in the same transaction
				mgr.write(uri, metadata, doc, transaction);
			}
			catch(Exception e) {
				transaction.rollback();
				System.out.println("Transaction rolled back.");
				throw(e);
			}

			System.out.println("Loaded " + uri + " in " + 
					(collection.equals("") ? "no" : "'"+collection+"'") + " collection");

			// Swap this in to delete the docs instead
			//mgr.delete(uri, transaction);
			//System.out.println("Deleted " + uri);
		} 
	}
}
