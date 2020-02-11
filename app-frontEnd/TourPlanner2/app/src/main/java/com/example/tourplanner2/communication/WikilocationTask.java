package com.example.tourplanner2.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

/**
 * 
 * */
public class WikilocationTask extends AsyncTask<String, Void, String> {
	/**
	 * Tag para escribir en el log.
	 */
	private static final String TAG = "WikilocationTask";
	/**
	 * Código que indica que el servicio utilizado es Wikilocation.
	 * */
	public final static int WIKILOCATION = 0;
	/**
	 * Referencia a la clase que va a procesar la petici�n.
	 */
	private IServiceTask mContext = null;

	/**
	 * Constructor de la clase, en el que no se mostrar� barra de progreso.
	 *
	 * @param mContext
	 *            clase que procesa la respuesta
	 */
	public WikilocationTask(IServiceTask mContext) {
		this.mContext = mContext;
	}

	/**
	 * M�todo que se realiza en background.
	 * @param urls
	 */
	@Override
	protected String doInBackground(String... urls) {
		String url = urls[0];
		String result = "";

		HttpResponse response = doResponse(url);

		if (response == null || response.getEntity() == null) {
			return result;
		} else {
			try {

				result = inputStreamToString(response.getEntity().getContent());

			} catch (IllegalStateException | IOException e) {
				Log.e(TAG, e.getLocalizedMessage(), e);

			}

		}

		return result;
	}

	/**
	 * M�todo que se ejecuta cuando responde el servicio web Wikilocation.
	 * @param url de respuesta del servicio web
	 * @return respuesta del servicio web
	 */
	private HttpResponse doResponse(String url) {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		HttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}

		return response;
	}

	/**
	 * M�todo que convierte un InputSream a String
	 * @param is InputStream a convertir
	 * @return cadena del inputStream
	 */
	private String inputStreamToString(InputStream is) {

		String line = "";
		StringBuilder total = new StringBuilder();

		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		try {
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}

		// Return full string
		return total.toString();
	}

	@Override
	protected void onPostExecute(String response) {
		mContext.handleServiceResponse(response, WIKILOCATION);
	}
}