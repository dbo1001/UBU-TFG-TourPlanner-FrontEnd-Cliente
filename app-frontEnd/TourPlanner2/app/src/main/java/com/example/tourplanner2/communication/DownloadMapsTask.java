package com.example.tourplanner2.communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tourplanner2.R;
import com.example.tourplanner2.util.MapNotification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.util.Pair;

/**
 * Clase que lanza las peticiones del servidor para la descarga de mapas.
 * Extiende de AsynTask por lo que trabaja en segundo plano.
 * 
 * @author Alejandro Cuevas �lvarez. 
 * @author aca0073@alu.ubu.es
 */
public class DownloadMapsTask extends AsyncTask<String, Integer, String> {
	/**
	 * Tag para escribir en el log.
	 */
	private static final String TAG = "DownloadMapsTask";
	/**
	 * Tiempo de conexi�n m�ximo en milisegundos
	 */
	private static final int CONN_TIMEOUT = 300000;
	public static final String REQUEST_METHOD = "GET";
	public static final int READ_TIMEOUT = 15000;
	/**
	 * Tiempo de espera de datos m�ximo en milisegundos
	 */
	private static final int SOCKET_TIMEOUT = 500000;
	/**
	 * C�digo de petici�n POST.
	 */
	public static final int POST_TASK = 1;
	/**
	 * C�digo de petici�n GET.
	 */
	public static final int GET_TASK = 2;
	/**
	 * Tipo de petici�n.
	 */
	private int taskType = GET_TASK;
	/**
	 * Lista de parametros a enviar en la petici�n.
	 */
	//private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	private List<Pair<String, String>> params = new ArrayList<>();
	/**
	 * Notifaci�n de la descarga del mapa.
	 */
	private MapNotification mapNotification;
	/**
	 * Referencia a la clase que va a procesar la petici�n.
	 * */
	private IWebServiceTaskResult dwtContext = null;


	/**
	 * Constructor de la clase, en el que no se mostrar� barra de progreso.
	 *
	 * @param context
	 *            clase que procesa la respuesta
	 */
	public DownloadMapsTask(int taskType, IWebServiceTaskResult context, String mapName) {
		mapNotification = new MapNotification(context.getContext(), mapName);
		this.taskType = taskType;
		this.dwtContext = context;

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

	@Override
	protected void onPreExecute() {
		mapNotification.createNotification();

	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		mapNotification.publishProgress(values[0]);
	}



	/**
	 * M�todo que se realiza en background.
	 * @param urls
	 */
	@Override
	protected String doInBackground(String... urls) {

		String stringUrl = urls[0];
		String result = null;
		String inputLine;

		try {
			//Create a URL object holding our url
			URL myUrl = new URL(stringUrl);

			//Create a connection
			HttpURLConnection connection =(HttpURLConnection)
					myUrl.openConnection();

			//Set methods and timeouts
			connection.setRequestMethod(REQUEST_METHOD);
			connection.setReadTimeout(READ_TIMEOUT);
			connection.setConnectTimeout(CONN_TIMEOUT);

			//Connect to our url
			connection.connect();

			//Create a new InputStreamReader
			InputStreamReader streamReader = new
					InputStreamReader(connection.getInputStream());

			File filePath = new File(Environment.getExternalStorageDirectory() + "/tourplanner/maps/");

			if (!filePath.exists())
				filePath.mkdirs();

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

		HttpResponse response = doResponse(url);

		if (response == null || response.getEntity() == null) {
			return null;
		} else {
			HttpEntity entity =  response.getEntity();
			if (entity != null){

				try {
					int fileLength = (int) entity.getContentLength();
					byte data[] = new byte[1024];
					long total = 0;
					int count;

					InputStream input = new BufferedInputStream(entity.getContent());
					File filePath = new File(Environment.getExternalStorageDirectory() + "/tourplanner/maps/");

					if (!filePath.exists())
						filePath.mkdirs();

					OutputStream output = new BufferedOutputStream(new FileOutputStream(filePath.getAbsolutePath() 
							+ "/" + mapNotification.getMapName() + ".map"));

					Calendar lastUpdate = Calendar.getInstance();

					while ((count = input.read(data)) != -1){
						
						// Si la descarga es cancelada, abortamos la petici�n HTTP.
						if (isCancelled()){
							httpclient.getConnectionManager().shutdown();
							break;
						}
						
						output.write(data, 0, count);
						total += count;

						Calendar now = Calendar.getInstance();
						if (now.getTimeInMillis() - lastUpdate.getTimeInMillis() >= 500) {
							lastUpdate = now;
							publishProgress((int) (total * 100 / fileLength));
						}


					}

					input.close();
					output.flush();
					output.close();

				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		JSONObject jso = new JSONObject();
		try {
			jso.put("map", mapNotification.getMapName());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jso.toString();

		 */
	}

	/**
	 * M�todo que establece los tiempos de espera.
	 * @return par�metros http
	 */
	/*
	private HttpParams getHttpParams() {

		HttpParams htpp = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
		HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);

		return htpp;
	}

	 */

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

	@Override
	protected void onPostExecute(String result) {
		mapNotification.completed();
		dwtContext.handleResponse(result);
	}
	
	/**
	 * M�todo que se ejecuta cuando se cancela la descarga.
	 *
	 */
	@Override
	protected void onCancelled() {
		// Cancelamos la notificaci�n de descarga.
		NotificationManager notif = (NotificationManager) dwtContext.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
		notif.cancel(mapNotification.getId());

		// Cuando se cancele la descarga, eliminamos lo que se haya descargado.
		File map = new File(Environment.getExternalStorageDirectory() + "/tourplanner/maps/" + mapNotification.getMapName());
		boolean delete = map.delete();

		if (delete){
			Toast.makeText(dwtContext.getContext(), R.string.delete_file, Toast.LENGTH_SHORT).show();
		}
	
	}

}
