package com.example.tourplanner2.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;

import com.example.tourplanner2.R;

/**
 * Clase que parsea el arhivo de properties. 
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 * */

public class PropertiesParser {
	/**
	 * M�todo que devuelve la direcci�n del servidor.
	 * @param context
	 * @return ip+puerto del servidor
	 * @throws IOException
	 */
	public static String getConnectionSettings(Context context) throws IOException{
		String ip="";
		String port="";
		Properties prop=new Properties();
		InputStream is = context.getResources().openRawResource(R.raw.connection_settings);
		prop.load(is);
		ip=prop.getProperty("ip");
		port=prop.getProperty("port");
		return ip+":"+port;
	}

}
