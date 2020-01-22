package com.example.tourplanner2.util;


import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.*;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

//import org.apache.http.conn.ssl.SSLSocketFactory;

public class SSLFactory {
	
	static SSLSocketFactory sf;
	
	public static void createSSLSocketFactory() {
		
        try{
        	
        	X509TrustManager tm = new X509TrustManager() {
        		
				public void checkClientTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[] { tm }, null);
			sf = new MySSLSocketFactory(ctx);
			sf.createSocket();
			
        } catch (Exception ex){
        	ex.printStackTrace();
        }
    }
	
	/**
	 * 
	 * */
	public static SSLSocketFactory getSSLSocketFactory(){
		if (sf == null){
			createSSLSocketFactory();
		} 
		
		return sf;
		
	}

}
