package com.example.tourplanner2.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;


import java.net.HttpURLConnection;
import java.util.List;

import android.app.ProgressDialog;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.util.Pair;

import com.example.tourplanner2.util.NullHostNameVerifier;

import org.apache.commons.lang3.ObjectUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
	public static final String REQUEST_METHOD = "GET";
	public static final int READ_TIMEOUT = 15000;

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
	//private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	private List<Pair<String, String>> params = new ArrayList<>();

	/**
	 * Dialogo con barra de progreso.
	 */
	private ProgressDialog pDlg = null;

	private URL urlConnect;
	
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

		params.add(new Pair<>("username", name));
		params.add(new Pair<>("password", value));
		//params.add(new BasicNameValuePair(name, value));
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

		String stringUrl = urls[0];
		//Conexion por HTTPS
		HttpsURLConnection urlHttpsConnection = null;
		String result = null;
		String inputLine;

		HttpURLConnection connection = null;
		
		try {
			//Create a URL object holding our url
			URL myUrl = new URL(stringUrl);

			//Si necesito usar HTTPS
			if (myUrl.getProtocol().toLowerCase().equals("https")) {

				trustAllHosts();

				//Creo la Conexion
				urlHttpsConnection = (HttpsURLConnection) myUrl.openConnection();

				//Seteo la verificacion para que NO verifique nada!!
				urlHttpsConnection.setHostnameVerifier(new NullHostNameVerifier());

				//Asigno a la otra variable para usar simpre la mism
				connection = urlHttpsConnection;

			} else {

				connection = (HttpURLConnection) myUrl.openConnection();
			}

			//Set methods and timeouts
			connection.setRequestMethod(REQUEST_METHOD);
			connection.setReadTimeout(READ_TIMEOUT);
			connection.setConnectTimeout(CONN_TIMEOUT);

			//Connect to our url
			connection.connect();

			//Create a new InputStreamReader
			InputStreamReader streamReader = new
					InputStreamReader(connection.getInputStream());

			//Create a new buffered reader and String Builder
			BufferedReader reader = new BufferedReader(streamReader);
			StringBuilder stringBuilder = new StringBuilder();

			//Check if the line we are reading is not null
			while((inputLine = reader.readLine()) != null){
				stringBuilder.append(inputLine);

			}
			//Close our InputStream and Buffered reader
			reader.close();
			streamReader.close();

			//Set our result equal to our stringBuilder
			result = stringBuilder.toString();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			result = null;
		}
		return result;

		/*
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

		return result;*/
	}

	private static void trustAllHosts() {

		X509TrustManager easyTrustManager = new X509TrustManager() {

			public void checkClientTrusted(
					X509Certificate[] chain,
					String authType) throws CertificateException {
				// Oh, I am easy!
			}

			public void checkServerTrusted(
					X509Certificate[] chain,
					String authType) throws CertificateException {
				// Oh, I am easy!
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

		};

		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] {easyTrustManager};

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");

			sc.init(null, trustAllCerts, new java.security.SecureRandom());

			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		} catch (Exception e) {
			e.printStackTrace();
		}
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
		//mContext.handleResponse(response);
	}

	/**
	 * M�todo que establece los tiempos de espera.
	 * @return par�metros http
	 */
	/*
	private HttpURLConnection getHttpParams() throws IOException {

		HttpURLConnection http = (HttpURLConnection)this.urlConnect.openConnection();
		http.setConnectTimeout(CONN_TIMEOUT);

		//HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
		//HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);

		return http;
	}*/
	/**
	 * M�todo que se ejecuta cuando responde el servidor.
	 * @param url de respuesta del servidor
	 * @return respuesta del servidor
	 */

	private String doResponse(String url) throws MalformedURLException {

		// Use our connection and data timeouts as parameters for our
		// DefaultHttpClient
		//DefaultHttpClient httpclient = new HttpsClient(getHttpParams());
		String response = null;
		String inputLine;
		try {
			//Create a URL object holding our url
			URL myUrl = new URL(url);

			//Create a connection
			HttpURLConnection connection = (HttpURLConnection)
					myUrl.openConnection();

			//Set methods and timeouts
			connection.setRequestMethod(REQUEST_METHOD);
			connection.setReadTimeout(READ_TIMEOUT);
			connection.setConnectTimeout(CONN_TIMEOUT);

			//Connect to our url
			connection.connect();

			switch (connection.getResponseCode()){
				case POST_TASK:
					connection.setRequestMethod("POST");
					/*
					HttpPost httppost = new HttpPost(url);
					// Add parameters
					httppost.setEntity(new UrlEncodedFormEntity(params));
					response = httpclient.execute(httppost);
					 */
					break;
				case GET_TASK:
					connection.setRequestMethod(REQUEST_METHOD);
					/*
					HttpGet httpget = new HttpGet(url);
					response = httpclient.execute(httpget);

					 */
					break;
			}
			//Create a new InputStreamReader
			InputStreamReader streamReader = new
					InputStreamReader(connection.getInputStream());

			//Create a new buffered reader and String Builder
			BufferedReader reader = new BufferedReader(streamReader);
			StringBuilder stringBuilder = new StringBuilder();

			//Check if the line we are reading is not null
			while((inputLine = reader.readLine()) != null){
				stringBuilder.append(inputLine);

			}
			//Close our InputStream and Buffered reader
			reader.close();
			streamReader.close();

			//Set our result equal to our stringBuilder
			response = connection.getResponseMessage();
		}catch (IOException e) {
				e.printStackTrace();
				response = null;
			}
/*
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

 */

		return response;
	}

	/**
	 * M�todo que convierte un InputSream a String
	 * @param is inputStream a convertir
	 * @return cadena del inputStream
	 */
	/*
	private String inputStreamToString(InputStream is) {

		String line;
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

	 */
	

}
