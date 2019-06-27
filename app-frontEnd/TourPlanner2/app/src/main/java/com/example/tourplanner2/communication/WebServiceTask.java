package com.example.tourplanner2.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Clase que lanza las peticiones del servidor, extiende de AsynTask por lo que
 * trabaja en segundo plano.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class WebServiceTask extends AsyncTask<String, Integer, String> {
	/**
	 * C�digo de petici�n POST.
	 */
	public static final int POST_TASK = 1;
	/**
	 * C�digo de petici�n GET.
	 */
	public static final int GET_TASK = 2;
	/**
	 * Tag para escribir en el log.
	 */
	private static final String TAG = "WebServiceTask";

	/**
	 * Tiempo de conexi�n m�ximo en milisegundos
	 */
	private static final int CONN_TIMEOUT = 300000;

	/**
	 * Tiempo de espera de datos m�ximo en milisegundos
	 */
	private static final int SOCKET_TIMEOUT = 500000;
	/**
	 * Tipo de petici�n.
	 */
	private int taskType = GET_TASK;
	/**
	 * Referencia a la clase que va a procesar la petici�n.
	 */
	private IWebServiceTaskResult mContext = null;
	/**
	 * Mensaje que se muestra en pantalla mientras se adquieren los datos.
	 */
	private String processMessage = null;
	/**
	 * Lista de parametros a enviar en la petici�n.
	 */
	private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	/**
	 * Dialogo con barra de progreso.
	 */
	private ProgressDialog pDlg = null;
	
	/**
	 * Constructor de la clase.
	 * 
	 * @param taskType
	 *            tipo de petici�n
	 * @param mContext
	 *            clase que procesa la respuesta
	 * @param processMessage
	 *            mensaje a mostrar en el dialogo de la barra de progreso
	 */
	public WebServiceTask(int taskType, IWebServiceTaskResult mContext,
			String processMessage) {

		this.taskType = taskType;
		this.mContext = mContext;
		this.processMessage = processMessage;
	}
	/**
	 * Constructor de la clase, en este caso no se mostrar� barra de progreso.
	 * 
	 * @param taskType
	 *            tipo de petici�n
	 * @param mContext
	 *            clase que procesa la respuesta
	 */
	public WebServiceTask(int taskType, IWebServiceTaskResult mContext) {

		this.taskType = taskType;
		this.mContext = mContext;
	}
	/**
	 * M�todo que a�ade par�metros a la petici�n.
	 * @param name nombre del par�metro
	 * @param value valor del par�metro
	 */
	public void addNameValuePair(String name, String value) {

		params.add(new BasicNameValuePair(name, value));
	}
	/**
	 * M�todo que muestra el dialogo de progreso.
	 */
	@SuppressWarnings("deprecation")
	private void showProgressDialog() {

		pDlg = new ProgressDialog(mContext.getContext());
		pDlg.setMessage(processMessage);
		pDlg.setProgressDrawable(mContext.getContext().getWallpaper());
		pDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDlg.setCancelable(false);
		pDlg.show();

	}
	/**
	 * M�todo que se realiza en background.
	 * @param urls
	 */
	protected String doInBackground(String... urls) {

		String url = urls[0];
		String result = "";

		HttpResponse response = doResponse(url);

		if (response == null || response.getEntity() == null) {
			return result;
		} else {

			try {

				result = inputStreamToString(response.getEntity().getContent());

			} catch (IllegalStateException e) {
				Log.e(TAG, e.getLocalizedMessage(), e);

			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
			}

		}

		return result;
	}
	
	@Override
	protected void onPreExecute() {
		if (processMessage != null) {
			showProgressDialog();
		}

	}

	@Override
	protected void onPostExecute(String response) {
		if (processMessage != null) {
			pDlg.dismiss();
		}
		mContext.handleResponse(response);
	}

	/**
	 * M�todo que establece los tiempos de espera.
	 * @return par�metros http
	 */
	private HttpParams getHttpParams() {

		HttpParams htpp = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
		HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);

		return htpp;
	}
	/**
	 * M�todo que se ejecuta cuando responde el servidor.
	 * @param url de respuesta del servidor
	 * @return respuesta del servidor
	 */
	private HttpResponse doResponse(String url) {

		// Use our connection and data timeouts as parameters for our
		// DefaultHttpClient
		DefaultHttpClient httpclient = new HttpsClient(getHttpParams());

		HttpResponse response = null;

		try {
			switch (taskType) {

			case POST_TASK:
				HttpPost httppost = new HttpPost(url);
				// Add parameters
				httppost.setEntity(new UrlEncodedFormEntity(params));
				response = httpclient.execute(httppost);
				break;
			case GET_TASK:
				HttpGet httpget = new HttpGet(url);
				response = httpclient.execute(httpget);
				break;
			}
		} catch (Exception e) {

			Log.e(TAG, e.getLocalizedMessage(), e);

		}

		return response;
	}
	
	/**
	 * M�todo que convierte un InputSream a String
	 * @param is inputStream a convertir
	 * @return cadena del inputStream
	 */
	private String inputStreamToString(InputStream is) {

		String line = "";
		StringBuilder total = new StringBuilder();

		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		try {
			// Read response until the end
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}

		// Return full string
		return total.toString();
	}
	

}
