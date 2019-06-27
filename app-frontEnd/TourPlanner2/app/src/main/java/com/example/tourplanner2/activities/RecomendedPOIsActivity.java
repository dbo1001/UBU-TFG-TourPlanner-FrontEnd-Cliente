package com.example.tourplanner2.activities;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tourplanner2.adapters.ItineraryListAdapter;
import com.example.tourplanner2.adapters.RecommendedPoiAdapter;

import com.example.tourplanner2.communication.IServiceTask;
import com.example.tourplanner2.communication.IWebServiceTaskResult;
import com.example.tourplanner2.communication.PanoramioTask;
import com.example.tourplanner2.communication.WebServiceTask;
import com.example.tourplanner2.communication.WikilocationTask;
import com.example.tourplanner2.dialog.DialogOpinion;
import com.example.tourplanner2.util.JSONParser;
import com.example.tourplanner2.util.Misc;
import com.example.tourplanner2.util.PropertiesParser;
import com.example.tourplanner2.util.RowItineraryList;
import com.example.tourplanner2.util.SlidingMenuController;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.example.tourplanner2.R;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
/**
 * Clase que se corresponde con la pantalla de a tu gusto.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class RecomendedPOIsActivity extends SlidingActivity implements
IWebServiceTaskResult, IServiceTask{
	/**
	 * Array con las filas que muestra la pantalla.
	 */
	private RowItineraryList[] rowsItinerary;
	/**
	 * Adaptador correspondiente a la lista de puntos recomendados.
	 * */
	private RecommendedPoiAdapter adaptadorItinerary;
	/**
	 * View correspondiente al buscador de puntos.
	 * */
	private AutoCompleteTextView searchView;
	/**
	 * Contexto referente a IServiceTask.
	 * */
	private IServiceTask serviceContext;
	/**
	 * Código que indica que el servicio utilizado es Wikilocation.
	 * */
	public final static int WIKILOCATION = 0;
	/**
	 * Código que indica que el servicio utilizado es Panoramio.
	 * */
	public final static int PANORAMIO = 1;
	/**
	 * Button correspondiente a la informaci�n adicional del POI.
	 * */
	private Button infoButton;
	/**
	 * ImageView correspondiente con la imagen del POI. 
	 * */
	private ImageView panoramioImage;
	/**
	 * URLs correspondientes a las imagenes de Panoramio.
	 * */
	private ArrayList<String> imageUrls;
	/**
	 * TextView que muestra la distancia de un POI a un punto.
	 * */
	private TextView textDistance;
	/**
	 * TextView que muestra los t�rminos y condiciones de Panoramio.
	 * */
	private TextView panoramioText;
	/**
	 * ImageView que muestra el logo de Panoramio.
	 * */
	private ImageView panoramioLogo;
	/**
	 * Coordenadas del punto de la imagen de un POI.
	 * */
	private Double lat1, lon1;
	/**
	 * Url del servicio.
	 */
	private static String RECOMMENDED_POI_SERVICE_URL;
	
	
	/**
	 * Método que se invoca cuando la actividad es creada.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommended_pois);
		setServiceDirections();
		serviceContext = this;
		new SlidingMenuController(this);
		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this,
				getResources().getString(R.string.gettingRecommendedPois));
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		wst.addNameValuePair("transport", pref.getString("transport", "fo_"));
		wst.addNameValuePair("lat", getIntent().getExtras().getString("lat"));
		wst.addNameValuePair("lon", getIntent().getExtras().getString("lon"));
		wst.execute(new String[] { RECOMMENDED_POI_SERVICE_URL });
		((Button) findViewById(R.id.buttonCalculate))
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int contador = 0;
				Intent intent = new Intent();
				ArrayList<RowItineraryList> rows = new ArrayList<RowItineraryList>();
				for (RowItineraryList row : rowsItinerary) {
					if (row.isSelected()) {
						rows.add(row);
						contador++;
					}
				}
				if (contador > 0) {
					intent.putExtra("count", contador);
					intent.putExtra("pois", rows.toArray());
					setResult(MapMain.GET_ROUTE, intent);
					finish();
				} else {
					Toast.makeText(
							getApplicationContext(),
							getResources().getString(
									R.string.mustSelectPoi),
									Toast.LENGTH_LONG).show();
				}
			}
		});

		searchView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewPoi);
		searchView.addTextChangedListener(new TextChangeListener());
	}
	/**
	 * Método que establece las direcciones de los servicios usados.
	 */
	private void setServiceDirections() {
		try {
			String address = PropertiesParser.getConnectionSettings(this);
			RECOMMENDED_POI_SERVICE_URL = "https://" + address
					+ "/osm_server/get/poi/recommendedlist";

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/**
	 * Método que recibe y procesa la respuesta del servidor.
	 * 
	 * @param response
	 *            respuesta del servidor
	 */
	@Override
	public void handleResponse(String response) {
		try {
			JSONParser parser = new JSONParser(response, null, this);
			rowsItinerary = parser.getItineraryList();

			JSONObject jso;

			jso = new JSONObject(response);

			if (jso.has("status")
					&& !Misc.checkErrorCode(
							jso.getString("status"), this)) {
				return;
			}

			adaptadorItinerary = new RecommendedPoiAdapter(
					this, rowsItinerary);
			ListView lstItinerary = (ListView) findViewById(R.id.listViewRecommendPois);
			lstItinerary.setAdapter(adaptadorItinerary);
			lstItinerary.setTextFilterEnabled(true);
			lstItinerary.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@SuppressWarnings("deprecation")
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						final int position, long id) {
					final Dialog dialog = new Dialog(getContext());
					// dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.point_dialog);
					dialog.setTitle(getResources().getString(R.string.pointInfo));
					
					lon1 = Double.parseDouble(rowsItinerary[position].getCoordinates().substring(
							rowsItinerary[position].getCoordinates().indexOf("(") + 1,
							rowsItinerary[position].getCoordinates().indexOf(" ")));
					
					lat1 = Double.parseDouble(rowsItinerary[position].getCoordinates().substring(
							rowsItinerary[position].getCoordinates().indexOf(" ") + 1));

					// set the custom dialog components - text, image and button
					TextView textName = (TextView) dialog
							.findViewById(R.id.textViewItineraryName);
					TextView textTag = (TextView) dialog
							.findViewById(R.id.textViewTag);
					textName.setText(rowsItinerary[position].getTextName());
					textTag.setText(rowsItinerary[position].getTag());
					ImageView image = (ImageView) dialog
							.findViewById(R.id.imageViewItineraryList);
					image.setImageResource(rowsItinerary[position].getImageResId());
					image = (ImageView) dialog
							.findViewById(R.id.imageViewItineraryListTag);
					image.setImageResource(rowsItinerary[position]
							.getImageTagResId());
					((RatingBar) dialog.findViewById(R.id.ratingBar))
					.setRating((float) rowsItinerary[position].getScore());
					((RatingBar) dialog.findViewById(R.id.ratingBar))
					.setIsIndicator(true);
					TextView textPromoted = (TextView) dialog
							.findViewById(R.id.tv_Promoted);
					String strPromotedFormat = getResources().getString(R.string.promoted);
					if (rowsItinerary[position].isPromoted()){
						textPromoted.setText(String.format(strPromotedFormat, 
								getResources().getString(R.string.yes)));
					} else {
						textPromoted.setText(String.format(strPromotedFormat, 
								getResources().getString(R.string.no)));
					}
					
					panoramioText = (TextView) dialog
							.findViewById(R.id.panoramioText);
					panoramioText.setText(R.string.panoramio_terms);
			
					panoramioLogo = (ImageView) dialog.findViewById(R.id.panoramioLogo);
					panoramioLogo.setVisibility(View.VISIBLE);
					
					textDistance = (TextView) dialog
							.findViewById(R.id.tv_distance);
					textDistance.setVisibility(TextView.VISIBLE);
					textDistance.setText("");

					panoramioImage = (ImageView) dialog.findViewById(R.id.panoramio_photo);
					panoramioImage.setEnabled(true);
					
					panoramioImage.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent i = new Intent(RecomendedPOIsActivity.this, GalleryActivity.class);
							i.putExtra("imageUrls", imageUrls);
							startActivity(i);

						}

					});

					// A�adimos bot�n de informaci�n adicional.
					infoButton = (Button) dialog.findViewById(R.id.dialogButtonInfo);

					// Cambiamos el icono para la informaci�n si el POI es de gastronom�a u ocio.
					if (rowsItinerary[position]
							.getImageResId() == R.drawable.leisure || rowsItinerary[position]
									.getImageResId() == R.drawable.gastronomy){
						infoButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_google, 0);
					}

					infoButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							switch (rowsItinerary[position]
									.getImageResId()){

							// Si el POI es de cultura o naturaleza, mostramos informaci�n sobre la Wikipedia.
									case R.drawable.culture:
									case R.drawable.nature:
										// Obtenemos logitud y latitud del POI en el que estamos, y el idioma en el que est� la app.
										Double longitude = Double.parseDouble(rowsItinerary[position].getCoordinates().substring(
												rowsItinerary[position].getCoordinates().indexOf("(") + 1,
												rowsItinerary[position].getCoordinates().indexOf(" ")));

										Double latitude = Double.parseDouble(rowsItinerary[position].getCoordinates().substring(
												rowsItinerary[position].getCoordinates().indexOf(" ") + 1));

										String lang = Locale.getDefault().getLanguage();

										// Realizamos una llamada al servicio REST de Wikilocation.
										String WIKILOCATION_SERVICE_URL = "http://api.wikilocation.org/articles?lat="
												+latitude+"&lng="+longitude+"&limit=1&locale="+lang+"";

										WikilocationTask wlt = new WikilocationTask(serviceContext);
										wlt.execute(new String[]{ WIKILOCATION_SERVICE_URL });
										break;

										// Si el POI es de ocio o gastronom�a, mostramos informaci�n sobre TripAdvisor.		
									case R.drawable.leisure:
									case R.drawable.gastronomy:	 
										String namePoi = rowsItinerary[position].getTextName();
										String TRIPADVISOR_SEARCH_URL = "https://www.google.com/#q=site:tripadvisor.com+"+namePoi+"";
										Intent i = new Intent("android.intent.action.VIEW", Uri.parse(TRIPADVISOR_SEARCH_URL));
										startActivity(i);
										break;

							}

						}

					});

					Button rating = (Button) dialog.findViewById(R.id.btn_rating);
					rating.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Date startTime = Calendar.getInstance().getTime();
							ItineraryListAdapter adaptadorItinerary = new ItineraryListAdapter(
									RecomendedPOIsActivity.this, rowsItinerary, startTime);
							DialogOpinion dialog = new DialogOpinion((Context) getContext(),
									rowsItinerary[position], adaptadorItinerary);
							dialog.show();

						}
					});

					Button dialogButton = (Button) dialog
							.findViewById(R.id.dialogButtonAccept);
					// if button is clicked, close the custom dialog
					dialogButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});


					// Panoramio API.
					if (isNetworkAvailable()){
						String PANORAMIO_SERVICE_URL = "http://www.panoramio.com/map/get_panoramas.php?set=public&" +
								"from=0&" +
								"to=5&" +
								"minx=" + lon1 + "&" + 
								"miny=" + (lat1 - 0.001) + "&" +
								"maxx=" + (lon1 + 0.001) + "&" +
								"maxy=" + lat1 + "&" +
								"size=medium&" +
								"mapfilter=true";

						PanoramioTask fkt = new PanoramioTask(serviceContext);
						fkt.execute(new String[]{ PANORAMIO_SERVICE_URL });
					} else {
						infoButton.setEnabled(false);
						panoramioImage.setImageResource(R.drawable.no_photo);
						panoramioImage.setEnabled(false);
						textDistance.setVisibility(TextView.INVISIBLE);
						panoramioText.setText(R.string.no_photo);
						rating.setEnabled(false);
					}
					
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
					lp.copyFrom(dialog.getWindow().getAttributes());
					lp.width = WindowManager.LayoutParams.FILL_PARENT;
					lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
					dialog.show();
					dialog.getWindow().setAttributes(lp);
				}

			});
			
			((Button) findViewById(R.id.buttonCalculate))
			.setVisibility(View.VISIBLE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Método que se llama cuando se pincha sobre un icono de la barra superior.
	 */

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		}
		return super.onOptionsItemSelected((android.view.MenuItem) item);
	}
	/**
	 * Método que devuelve el contexto de esta activity.
	 */
	@Override
	public Context getContext() {
		return this;
	}
	
	/**
	 * Método que comprueba si la conexión de red esta disponible.
	 * 
	 * @return true si la conexión a internet está disponible
	 */
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	/**
	 * M�todo con el que obtenemos la iamgen que est� tomada a la menor distancia del
	 * punto de inter�s.
	 * 
	 * @param photos Array de fotos del que obtener la foto con menor distancia.
	 * @return JSONObject con la menor distancia. 
	 * 
	 * */
	private JSONObject getPhotoMinimumDistance(JSONArray photos){
		JSONObject photo = null, photoAux;
		double distance = 0, minimumDistance = 2000;
		Double latitude, longitude;

		for (int i = 0; i < photos.length(); i++){
			try {
				photoAux = photos.getJSONObject(i);
				latitude = photoAux.getDouble("latitude");
				longitude = photoAux.getDouble("longitude");
				distance = calculateDistance(lat1, lon1, latitude, longitude, false);

				if (distance < minimumDistance){
					minimumDistance = distance;
					photo = photoAux;
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return photo;

	}

	/**
	 * M�todo en el que se calcula la distancia que hay desde d�nde se tom� la imagen que aparece
	 * del punto de inter�s hasta el mismo punto, a trav�s de la distancia Eucl�dea.
	 * 
	 * @param lat1 Latitud del punto de inter�s.
	 * @param lon1 Longitud del punto de inter�s.
	 * @param lat2 Latitud del lugar d�nde se ha tomado la imagen.
	 * @param lon2 Longitud del lugar d�nde se ha tomado la imagen.
	 * @param ok Boolean que indica si hay que formatear o no la distancia para mostrarla.
	 * @return Distancia calculada. 
	 * */
	private double calculateDistance(Double lat1, Double lon1, 
			Double lat2, Double lon2, boolean ok){
		double poiDistance;
		lat1 = parseToRadians(lat1);
		lon1 = parseToRadians(lon1);
		lat2 = parseToRadians(lat2);
		lon2 = parseToRadians(lon2);

		poiDistance = 6378.137 * Math.acos(Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)
				+ Math.sin(lat1) * Math.sin(lat2));	

		// Formateamos la distancia para mostrarla s�lo cuando tengamos la m�nima.	
		if (ok == true){
			DecimalFormat df = new DecimalFormat("0.00");
			df.format(poiDistance);

			String measure = "km";
			String distance = "";
			if (poiDistance < 1){
				measure = "m";
				poiDistance *= 1000;
				distance = String.valueOf(poiDistance).substring(0, String.valueOf(poiDistance).indexOf("."));

			} else {
				measure = "km";
				distance = String.valueOf(poiDistance).substring(0,3);
			}



			String strDistanceFormat = getResources().getString(R.string.distance);
			textDistance.setText(String.format(strDistanceFormat, 
					distance, measure));

		}

		return poiDistance;



	}
	
	/**
	 * M�todo para convertir coordenadas geogr�ficas en radianes.
	 * 
	 * @param num N�mero a convertir.
	 * @return N�mero convertido en radianes.
	 * */
	private Double parseToRadians(Double num){
		return num * Math.PI / 180;
	}


	/**
	 * Clase privada que se corresponde con un listener sobre el
	 * autoCompleteTextView cuando el texto cambia.
	 * 
	 * @author Alejandro Cuevas �lvarez
	 * @author aca0073@alu.ubu.es
	 */
	private class TextChangeListener implements TextWatcher {
		/**
		 * Constructor de la clase
		 */
		public TextChangeListener() {
			super();
		}

		/**
		 * Método llamado despues de que cambia el texto.
		 */
		@Override
		public void afterTextChanged(Editable s) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (count < before) {
				// We're deleting char so we need to reset the adapter data
				adaptadorItinerary.resetData();
			}

			s = searchView.getText().toString();
			adaptadorItinerary.getFilter().filter(s.toString());
		}

	}


	/**
	 * M�todo que recibe y procesa la respuesta de los distintos servicios web utilizados.
	 * 
	 * @param response
	 *            respuesta del servicio web.
	 */
	@Override
	public void handleServiceResponse(Object response, int type) {
		switch (type){

		case WIKILOCATION:
			try {
				JSONObject json = new JSONObject(response.toString());
				// Comprobamos si se ha encontrado informaci�n del punto en la Wikipedia. 
				if (!json.getJSONArray("articles").isNull(0)){
					// Obtenemos la url de la wikipedia del POI en el que nos encontramos.
					String url = json.getJSONArray("articles").getJSONObject(0).getString("mobileurl");

					Intent i = new Intent("android.intent.action.VIEW", Uri.parse(url));
					// Lanzamos un intent que nos lleva a la p�gina de la Wikipedia
					startActivity(i);

				} else {
					infoButton.setEnabled(false);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;

		case PANORAMIO:
			try {
				JSONObject json = new JSONObject(response.toString());
				
				if (json.getJSONArray("photos").length() > 0){
					JSONArray photos = json.getJSONArray("photos");
					JSONObject photo;
					imageUrls = new ArrayList<String>();
					for (int i = 0; i < photos.length(); i++){
						photo = photos.getJSONObject(i);
						imageUrls.add(photo.getString("photo_file_url"));
					}

					photo = getPhotoMinimumDistance(photos);
					Double latitude = photo.getDouble("latitude");
					Double longitude = photo.getDouble("longitude");
					calculateDistance(lat1, lon1, latitude, longitude, true);

					String url = photo.getString("photo_file_url");

					DownloadPhotoTask dpt = new DownloadPhotoTask();
					dpt.execute(url);	
				} else {
					panoramioImage.setImageResource(R.drawable.no_photo);
					panoramioImage.setEnabled(false);
					panoramioText.setText(R.string.no_photo);
					panoramioLogo.setVisibility(View.INVISIBLE);
					textDistance.setVisibility(TextView.INVISIBLE);
				}
					
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			
			break;

		}

	}

	
	/**
	 * Clase privada que maneja la descarga de im�genes de Panoramio mediante una AsyncTask.
	 * 
	 * @author Alejandro Cuevas �lvarez - aca0073@alu.ubu.es
	 * */
	private class DownloadPhotoTask extends AsyncTask<String, Void, Bitmap>{

		@Override
		protected Bitmap doInBackground(String... urls) {
			Bitmap image = null;
			URL url;
			
			try {
				url = new URL(urls[0]);
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setDoInput(true);
				urlConnection.connect();
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				image = BitmapFactory.decodeStream(in);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return image;
		}

		@Override
		protected void onPostExecute(Bitmap image) {
			panoramioImage.setImageBitmap(image);
		}
	}




}
