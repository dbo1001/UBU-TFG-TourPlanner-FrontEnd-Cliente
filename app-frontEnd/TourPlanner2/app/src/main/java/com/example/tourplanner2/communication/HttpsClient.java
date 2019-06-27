package com.example.tourplanner2.communication;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.HttpParams;

import com.example.tourplanner2.util.SSLFactory;

public class HttpsClient extends DefaultHttpClient{
	
	public HttpsClient(HttpParams params){
		super(null, params);
	}
	
	@Override
	protected ClientConnectionManager createClientConnectionManager(){
		SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 8080));
 
        registry.register(new Scheme("https", SSLFactory.getSSLSocketFactory(), 8443));
        return new SingleClientConnManager(getParams(), registry);
		
	}
	
	

}
