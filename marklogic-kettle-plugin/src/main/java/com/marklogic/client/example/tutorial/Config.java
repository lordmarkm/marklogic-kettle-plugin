package com.marklogic.client.example.tutorial;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.marklogic.client.DatabaseClientFactory.Authentication;

public class Config {

	private static Properties props = loadProperties();
	
	protected static String host = props.getProperty("example.host");
	
	protected static int port = Integer.parseInt(props.getProperty("example.port"));
	
	protected static String user = props.getProperty("example.writer_user");
	
	protected static String password = props.getProperty("example.writer_password");
	
	protected static String admin_user = props.getProperty("example.admin_user");
	
	protected static String admin_password = props.getProperty("example.admin_password");
	
	protected static Authentication authType = Authentication.valueOf(
				props.getProperty("example.authentication_type").toUpperCase()
				);

	// get the configuration for the example
	private static Properties loadProperties() {		
	    try {
			String propsName = "Config.properties";
			InputStream propsStream =
				Config.class.getClassLoader().getResourceAsStream(propsName);
			if (propsStream == null)
				throw new IOException("Could not read config properties");

			Properties props = new Properties();
			props.load(propsStream);

			return props;

	    } catch (final IOException exc) {
	        throw new Error(exc);
	    }  
	}
}
