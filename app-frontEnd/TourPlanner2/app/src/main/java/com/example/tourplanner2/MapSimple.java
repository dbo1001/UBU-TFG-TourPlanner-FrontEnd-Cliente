package com.example.tourplanner2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mapsforge.android.maps.mapgenerator.tiledownloader.MapnikTileDownloader;
import org.mapsforge.android.maps.overlay.ArrayItemizedOverlay;
import org.mapsforge.android.maps.overlay.ArrayWayOverlay;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.android.maps.overlay.OverlayWay;
import org.mapsforge.map.android.layers.MyLocationOverlay;
import org.mapsforge.core.GeoPoint;
import org.mapsforge.map.android.view.MapView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tourplanner.adapters.CustomSocialAdapter;
import tourplanner.adapters.ItineraryListAdapter;
import tourplanner.adapters.OptionsAdapter;
import tourplanner.communication.IServiceTask;
import tourplanner.communication.IWebServiceTaskResult;
import tourplanner.communication.PanoramioTask;
import tourplanner.communication.WebServiceTask;
import tourplanner.communication.WikilocationTask;
import tourplanner.dialog.DialogOpinion;
import tourplanner.dialog.DialogProfile;
import tourplanner.services.CheckLocationService;
import tourplanner.services.CheckLocationService.ServiceClient;
import tourplanner.util.JSONParser;
import tourplanner.util.Misc;
import tourplanner.util.PropertiesParser;
import tourplanner.util.RowItineraryList;
import tourplanner.util.RowListView;
import tourplanner.util.SSLFactory;

/**
 * Clase central que funciona como mediador en la aplicación, muestra el mapa y
 * contra las actividades que se abren y las respuestas de las actividades que
 * se cierran.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 * 
 */
public class MapSimple extends SlidingActivity implements
IWebServiceTaskResult, IServiceTask, ServiceClient {
	/**
	 * Código que indica que debe seleccionar el destino en el mapa.
	 */
	public final static int ONE_LOCATION_TGT = 101;
	/**
	 * Código que indica que debe seleccionar el destino en el mapa y el origen
	 * debe ser igual a este.
	 */
	public final static int ONE_LOCATION_TGT_EQ_SRC = 103;
	/**
	 * Código que indica que debe seleccionar el origen en el mapa y el destino
	 * debe ser un POI.
	 */
	public final static int ONE_LOCATION_TGT_POI = 104;
	/**
	 * Código que indica que debe seleccionar el destino y el origen en el mapa.
	 */
	public final static int TWO_LOCATIONS = 105;
	/**
	 * Código que indica que el punto de destino debe ser un POI.
	 */
	public final static int TGT_POI = 106;
	/**
	 * Código que indica que el destino debe ser igual al origen.
	 */
	public final static int TGT_EQ_SRC = 107;
	/**
	 * Código que indica que debe realizar una petición de ruta entre puntos
	 * seleccionados.
	 */
	public final static int GET_ROUTE = 108;
	/**
	 * Código que indica que debe mostrar PreferencesActivity.
	 */
	public final static int OPEN_PREFERENCES = 111;
	/**
	 * Código que indica que debe mostrar PlannerActivity.
	 */
	public final static int OPEN_PLANNER = 112;
	/**
	 * Código que indica que debe realizar una petición de itinerario
	 * recomendado.
	 */
	public final static int EXPRESS_ROUTE = 113;
	/**
	 * Código que indica que debe mostrar RecomendedPOISActivity.
	 */
	public final static int OPEN_RECOMENDED_PLACES = 114;
	/**
	 * Código que indica que debe mostrar TrackActivity.
	 */
	public final static int OPEN_TRACKS = 115;
	/**
	 * Código que indica que debe mostrar AdvancedOptionsActivity.
	 */
	public final static int OPEN_ADVANCED_OPTIONS = 116;
	/**
	 * Código que indica que debe mostrar ProfileActivity.
	 */
	public final static int OPEN_PROFILE = 117;
	/**
	 * Código que indica que debe mostrar ProfileActivity.
	 */
	public final static int SHARE_ROUTE = 118;
	/**
	 * Código que indica que debe mostrar una ruta guardada
	 * */
	public final static int SHOW_SAVE_ROUTE = 119;
	/**
	 * Código que indica que debe mostrar un mapa.
	 * */
	public final static int SHOW_MAP = 120;
	/**
	 * Código que indica que el servicio utilizado es Wikilocation.
	 * */
	public final static int WIKILOCATION = 0;
	/**
	 * Código que indica que el servicio utilizado es Panoramio.
	 * */
	public final static int PANORAMIO = 1;
	/**
	 * URLs correspondientes a las imagenes de Panoramio.
	 * */
	private ArrayList<String> imageUrls;
	/**
	 * Lista con los autores de las imagenes de Panoramio.
	 * */
	private ArrayList<String> authors;
	/**
	 * URLs de los autores de las imagenes de Panoramio.
	 * */
	private ArrayList<String> authorsUrl;
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
	 * Mapa en el que se muestran las rutas.
	 */
	private MapView mapView;
	/**
	 * Servicio de localización.
	 */
	private CheckLocationService locationService;
	/**
	 * Coordenadas de inicio y fin de la ruta.
	 */
	private String srclat, srclon, tgtlat, tgtlon;
	/**
	 * Urls de los diferentes servicios utilizados.
	 */
	private static String ITINERARY_SERVICE_URL, EXPRESS_ITINERARY_SERVICE_URL,
	ROUTE_SERVICE_URL, RATING_SERVICE_URL, WIKILOCATION_SERVICE_URL,
	TRIPADVISOR_SEARCH_URL, GET_CITY_SERVICE, PANORAMIO_SERVICE_URL;
	/**
	 * Array con los puntos de la ruta.
	 */
	private RowItineraryList[] rowsItinerary;
	/**
	 * Tiempo máximo de la ruta actual.
	 */
	private double routeTime;
	/**
	 * Puntuación seleccionada por el usuario para las diferentes categorias.
	 */
	private int leisureScore, gastronomyScore, cultureScore, natureScore;
	/**
	 * Adaptador para compartir en las redes sociales.
	 */
	private SocialAuthAdapter adapter;
	/**
	 * Cadena que indica la categoria del POI en el que hay que finalizar, si el
	 * usuario lo seleccionó.
	 */
	private String poiTarget = "";
	/**
	 * Nombre del hotel seleccionado.
	 */
	private String hotelName = "";
	/**
	 * Coordenadas de la ciudad seleccionada.
	 */
	private String cityCoordinates = "";
	/**
	 * Coordenadas de la ruta a mostrar sin conexi�n.
	 * */
	private String loadCoordinates = "";
	/**
	 * Indican esta seleccionado o no el origen y el destino.
	 */
	private boolean targetSelected = true, sourceSelected = true;
	/**
	 * Indica si el servicio esta activo o no.
	 */
	private boolean mBound;
	/**
	 * Indica si se selecciono origen y destino iguales
	 */
	private boolean sourceEqtarget = false;
	/**
	 * Indica si se selecciono la ruta a partir de unos puntos recomendados.
	 */
	private boolean recommendedPois = false;
	/**
	 * Tiempo de comienzo de la ruta.
	 */
	private Date startTime;
	/**
	 * Contexto IWebServiceTaskResult de la actividad.
	 */
	private IWebServiceTaskResult context;
	/**
	 * Contexto IServiceTask de la actividad.
	 */
	private IServiceTask serviceContext;
	/**
	 * Indica si se esta mostrando el mapa, o la lista.
	 */
	private boolean mapShowed = true;
	/**
	 * 
	 */
	private MyItem itemizedOverlay;
	/**
	 * 
	 */
	private ArrayWayOverlay routeOverlay;
	/**
	 * Posicion actual en el mapa.
	 */
	private OverlayItem actualPosition = new OverlayItem();
	/**
	 * Dialogo para compartir en las redes sociales.
	 */
	private Dialog socialDialog;
	/**
	 * Bot�n de informaci�n sobre los puntos de inter�s.
	 * */
	private Button infoButton;
	/**
	 * Imagen a mostrar sobre el POI.
	 * */
	private ImageView panoramioImage;
	/**
	 * Coordenadas de la ruta.
	 * */
	private String coordinates = null;
	/**
	 * Coordenadas del punto de la imagen de un POI.
	 * */
	private Double lat1, lon1;
	/**
	 * Conexión con el servicio.
	 */
	private ServiceConnection mConnection;

	/**
	 * Método que se invoca cuando la actividad es creada.
	 * 
	 * @param savedInstanceState
	 *            Bundle que contiene el estado de ejecuciones pasadas.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		RowListView[] rows = new RowListView[7];
		// Si no hay conexi�n a Internet, empleamos los mapas offline.
		if (!isNetworkAvailable()) {
			buildAlertMessageNoInternet();
			mapView = new MapView(this);
			String mapName = null;
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			if (pref.getString("recently_map", "") != null){
				mapName = pref.getString("recently_map", "");
			} else {
				mapName = pref.getString("last_map", "");
			}

			String filepath = Environment.getExternalStorageDirectory() + 
					"/tourplanner/maps/" + mapName + ".map";
			File map = new File(filepath);

			if (map.exists()){
				mapView.setMapFile(new File(filepath));
			} else {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.no_map),
						Toast.LENGTH_LONG).show();
				finish();
			}



		} else {
			mapView = new MapView(this, new MapnikTileDownloader());
		}

		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);

		setContentView(mapView);

		setServiceDirections();
		createSSLSocketFactory();
		context = this;
		serviceContext = this;
		targetSelected = true;
		sourceSelected = true;
		createSlidingMenu(rows);
		itemizedOverlay = new MyItem(getResources().getDrawable(
				R.drawable.marker_red));

		mapView.getOverlays().add(itemizedOverlay);
		OptionsAdapter adaptador = new OptionsAdapter(this, rows);
		ListView lstOpciones = (ListView) findViewById(R.id.listView);
		lstOpciones.setAdapter(adaptador);
		adapter = new SocialAuthAdapter(new ResponseListener());
		adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);
		adapter.addProvider(Provider.TWITTER, R.drawable.twitter);
		adapter.addProvider(Provider.FLICKR, R.drawable.flickr);
		adapter.addProvider(Provider.INSTAGRAM, R.drawable.instagram);

		Button share = (Button) findViewById(R.id.buttonShare);
		share.setOnClickListener(new SocialListener());
		lstOpciones.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				Intent myIntent;
				switch (position) {
				case 0:
					myIntent = new Intent(MapSimple.this,
							PreferencesActivity.class);
					startActivityForResult(myIntent, 1);
					break;
				case 1:
					myIntent = new Intent(MapSimple.this, PlannerActivity.class);
					startActivityForResult(myIntent, 1);
					break;
				case 2:
					if (getSlidingMenu().isMenuShowing()) {
						toggle();
					}
					locationService.updateLocation();
					srclat = locationService.getLatitude();
					srclon = locationService.getLongitude();
					recommendedPois = false;
					cleanMap();
					getExpressItinerary();
					break;
				case 3:
					myIntent = new Intent(MapSimple.this,
							RecomendedPOIsActivity.class);
					myIntent.putExtra("lat", locationService.getLatitude());
					myIntent.putExtra("lon", locationService.getLongitude());
					startActivityForResult(myIntent, 1);
					break;
				case 4:
					myIntent = new Intent(MapSimple.this, MyRoutesActivity.class);
					if (coordinates != null){
						JSONParser parser = new JSONParser(coordinates, itemizedOverlay, MapSimple.this);
						String hugeString = parser.getWayCoordinates();
						String[] data = hugeString.split(" ");
						myIntent.putExtra("longitude", data[0]);
						myIntent.putExtra("latitude", data[1]);	
					}
					myIntent.putExtra("coordinates", coordinates);
					startActivityForResult(myIntent, 1);
					break;
				case 5:
					myIntent = new Intent(MapSimple.this,
							AdvancedOptionsActivity.class);
					startActivityForResult(myIntent, 1);
					break;
				case 6:
					SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
					if (!pref.getBoolean("registered", false)) {
						DialogProfile dialog = new DialogProfile(
								(Context) context);
						dialog.setTitle(getResources().getString(
								R.string.registerTourPlanner));
						dialog.show();
					} else {
						myIntent = new Intent(MapSimple.this,
								ProfileActivity.class);
						startActivityForResult(myIntent, 1);
					}
					break;
				}
			}
		});

	}

	private class SocialListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			socialDialog = new Dialog((Context) context);
			socialDialog.setContentView(R.layout.list_view);
			socialDialog.setTitle(getResources()
					.getString(R.string.shareIn));
			ListView lista = (ListView) socialDialog
					.findViewById(R.id.list_view);
			lista.setAdapter(new CustomSocialAdapter((Context) context,
					adapter));
			socialDialog.show();
		}

	}


	/**
	 * Método que establece la dirección url de los diferentes servicios.
	 */
	private void setServiceDirections() {
		try {
			String address = PropertiesParser.getConnectionSettings(this);

			ITINERARY_SERVICE_URL = "https://" + address
					+ "/osm_server/get/itinerary";
			EXPRESS_ITINERARY_SERVICE_URL = "https://" + address
					+ "/osm_server/get/itinerary/express";
			ROUTE_SERVICE_URL = "https://" + address
					+ "/osm_server/get/itinerary/customroute";
			RATING_SERVICE_URL = "https://" + address + "/osm_server/get/rating";
			GET_CITY_SERVICE = "https://" + address + "/osm_server/get/map/exists";

		} catch (IOException e){
			e.printStackTrace();
		}

	}

	/**
	 * Método que crea un SSLSocketFactory personalizado.
	 */
	private void createSSLSocketFactory(){
		SSLFactory.getSSLSocketFactory();
	}

	/**
	 * Método que crea el menu deslizante.
	 * 
	 * @param rows
	 *            filas del menu
	 */
	private void createSlidingMenu(RowListView[] rows) {
		rows[0] = new RowListView(getResources().getString(R.string.ms_cfg),
				R.drawable.ic_action_place);
		rows[1] = new RowListView(getResources().getString(R.string.planner),
				R.drawable.ic_action_map);
		rows[2] = new RowListView(
				getResources().getString(R.string.speedRoute),
				R.drawable.ic_action_good);
		rows[3] = new RowListView(getResources().getString(
				R.string.recomendedPlaces), R.drawable.ic_action_edit);
		rows[4] = new RowListView(getResources().getString(R.string.route),
				R.drawable.ic_action_save);
		rows[5] = new RowListView(getResources().getString(
				R.string.advancedOptions), R.drawable.ic_action_settings);
		rows[6] = new RowListView(getResources().getString(R.string.profile),
				R.drawable.ic_action_person);
		setBehindContentView(R.layout.menu_slide);
		getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);
		getSlidingMenu().setShadowDrawable(R.drawable.shadow);
		getSlidingMenu().setBehindOffsetRes(R.dimen.slidingmenu_offset);
		getSlidingMenu().setFadeDegree(0.35f);
		setSlidingActionBarEnabled(true);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * Método que se llama cuando se pincha sobre la notificación.
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		final Dialog dialog = new Dialog(context.getContext());
		dialog.setContentView(R.layout.rating_dialog);
		dialog.setTitle(getResources().getString(R.string.sendRating));
		for (RowItineraryList row : rowsItinerary) {
			if (row.getRating() == 0.0f) {
				row.setRating(2.5f);
			}
		}
		ItineraryListAdapter adaptadorItinerary = new ItineraryListAdapter(
				this, rowsItinerary, startTime);
		ListView lstItinerary = (ListView) dialog
				.findViewById(R.id.listRatingDialog);
		lstItinerary.setOnItemClickListener(new ItineraryListListener(
				adaptadorItinerary));
		lstItinerary.setAdapter(adaptadorItinerary);

		Button dialogButton = (Button) dialog
				.findViewById(R.id.buttonDialogCancel);
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialogButton = (Button) dialog.findViewById(R.id.buttonDialogSend);
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int cont = 0;
				SharedPreferences pref = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				if (pref.getBoolean("registered", false)) {
					WebServiceTask wst = new WebServiceTask(
							WebServiceTask.POST_TASK, context);
					for (RowItineraryList row : rowsItinerary) {
						if (row.getPoiId() != -1L) {
							wst.addNameValuePair("poi_id" + cont,
									String.valueOf(row.getPoiId()));
							wst.addNameValuePair("rating" + cont,
									String.valueOf(row.getRating() * 20));
							wst.addNameValuePair("opinion" + cont,
									row.getOpinion());
							cont++;
						}
					}
					wst.addNameValuePair("count", String.valueOf(cont));

					wst.addNameValuePair("email", pref.getString("email", ""));
					wst.execute(new String[] { RATING_SERVICE_URL });
					dialog.dismiss();
				} else {
					DialogProfile dialog = new DialogProfile((Context) context);
					dialog.setTitle(getResources().getString(
							R.string.registerTourPlanner));
					dialog.show();
				}
			}
		});
		dialog.show();
	}

	/**
	 * Método onStart que se llama cada vez que se comienza la actividad.
	 */
	@Override
	protected void onStart() {
		super.onStart();

		mConnection = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				locationService = ((CheckLocationService.MyBinder) service)
						.getService();
				mBound = true;
				if (!locationService.checkGpsEnabled()) {
					buildAlertMessageNoGps();
				} else {
					locationService.updateLocation();
					if (loadCoordinates.equals("")){
						if (cityCoordinates.equals("")) {
							if (locationService.getLatitude() != null && 
									locationService.getLongitude() != null){
								centerMap(locationService.getLatitude(),
										locationService.getLongitude());
							}
						} else {
							String lon = cityCoordinates.substring(
									cityCoordinates.indexOf("(") + 1,
									cityCoordinates.indexOf(" "));
							String lat = cityCoordinates.substring(
									cityCoordinates.indexOf(" ") + 1,
									cityCoordinates.indexOf(")"));
							centerMap(lat, lon);
						}
					}
					locationService.setServiceClient(MapSimple.this);

				}

			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				locationService = null;
				mBound = false;
			}
		};
		doBindService();
	}

	/**
	 * Método onStop que se llama cuando se para la actividad.
	 */
	@Override
	protected void onStop() {
		super.onStop();
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		if (isNetworkAvailable() && pref.getString("recently_map", "") == null){
			WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this);
			wst.addNameValuePair("latitude", locationService.getLatitude());
			wst.addNameValuePair("longitude", locationService.getLongitude());

			wst.execute(new String[] { GET_CITY_SERVICE });
		}
	}

	/**
	 * Método que crea un mensaje de alerta advirtiendo de que la conexi�n a Internet no est�
	 * activa.
	 */
	public void buildAlertMessageNoInternet(){
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getResources().getString(R.string.networkOffline))
		.setCancelable(false)
		.setPositiveButton((getResources().getString(R.string.yes)),
				new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog,
					final int id) {
				startActivity(new Intent(
						android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS));
			}
		})
		.setNegativeButton((getResources().getString(R.string.no)),
				new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog,
					final int id) {
				dialog.cancel();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Método que crea un mensaje de alerta advirtiendo de que no esta el GPS
	 * activo.
	 */
	public void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getResources().getString(R.string.gpsNotEnabled))
		.setCancelable(false)
		.setPositiveButton((getResources().getString(R.string.yes)),
				new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog,
					final int id) {
				startActivity(new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			}
		})
		.setNegativeButton((getResources().getString(R.string.no)),
				new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog,
					final int id) {
				dialog.cancel();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
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
	 * Método que se llama cuando una actividad, iniciada por esta,
	 * finaliza,ejerce el rol de mediador.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (getSlidingMenu().isMenuShowing()) {
			toggle();
		}
		recommendedPois = false;
		poiTarget = "";
		hotelName = "";
		sourceEqtarget = false;

		if (data != null && data.getExtras() != null
				&& data.getExtras().containsKey("city_coordinates")) {
			cityCoordinates = data.getExtras().getString("city_coordinates");
			String lon = cityCoordinates.substring(
					cityCoordinates.indexOf("(") + 1,
					cityCoordinates.indexOf(" "));
			String lat = cityCoordinates.substring(
					cityCoordinates.indexOf(" ") + 1,
					cityCoordinates.indexOf(")"));
			centerMap(lon, lat);
			if (data.getExtras().containsKey("hotel_name")) {
				cityCoordinates = "(" + data.getExtras().getString("srclon")
						+ " " + data.getExtras().getString("srclat") + ")";
				hotelName = data.getExtras().getString("hotel_name");
			}
		} else {
			cityCoordinates = "";
		}
		if (resultCode == TGT_POI || resultCode == TGT_EQ_SRC) {
			cleanMap();
			routeTime = data.getExtras().getDouble("time");
			cultureScore = data.getExtras().getInt("culture");
			natureScore = data.getExtras().getInt("nature");
			leisureScore = data.getExtras().getInt("leisure");
			gastronomyScore = data.getExtras().getInt("gastronomy");
			if (resultCode == TGT_EQ_SRC) {
				if (!data.getExtras().containsKey("hotel_name")) {
					srclat = locationService.getLatitude();
					srclon = locationService.getLongitude();
				} else {
					srclat = data.getExtras().getString("srclat");
					srclon = data.getExtras().getString("srclon");
				}
				sourceSelected = true;
				targetSelected = true;
				tgtlat = srclat;
				tgtlon = srclon;
				getItinerary(false);
			} else {
				sourceSelected = true;
				targetSelected = true;
				if (!data.getExtras().containsKey("hotel_name")) {
					srclat = locationService.getLatitude();
					srclon = locationService.getLongitude();
				} else {
					srclat = data.getExtras().getString("srclat");
					srclon = data.getExtras().getString("srclon");
				}
				poiTarget = data.getExtras().getString("tgt_poi");
				getItinerary(true);
			}
		}
		if (resultCode == ONE_LOCATION_TGT
				|| resultCode == ONE_LOCATION_TGT_EQ_SRC
				|| resultCode == ONE_LOCATION_TGT_POI
				|| resultCode == TWO_LOCATIONS) {
			cleanMap();
			routeTime = data.getExtras().getDouble("time");
			cultureScore = data.getExtras().getInt("culture");
			natureScore = data.getExtras().getInt("nature");
			leisureScore = data.getExtras().getInt("leisure");
			gastronomyScore = data.getExtras().getInt("gastronomy");
			if (resultCode == TWO_LOCATIONS) {
				sourceSelected = false;
				targetSelected = false;
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.onTapSelectSource),
						Toast.LENGTH_LONG).show();
			} else {
				if (resultCode == ONE_LOCATION_TGT_POI
						|| resultCode == ONE_LOCATION_TGT_EQ_SRC) {
					if (resultCode == ONE_LOCATION_TGT_EQ_SRC) {
						sourceEqtarget = true;
					} else {
						poiTarget = data.getExtras().getString("tgt_poi");
					}
					targetSelected = true;
					sourceSelected = false;
					Toast.makeText(
							getApplicationContext(),
							getResources()
							.getString(R.string.onTapSelectSource),
							Toast.LENGTH_LONG).show();
				} else {
					sourceSelected = true;
					targetSelected = false;
					Toast.makeText(
							getApplicationContext(),
							getResources()
							.getString(R.string.onTapSelectTarget),
							Toast.LENGTH_LONG).show();
					locationService.updateLocation();
					srclat = locationService.getLatitude();
					srclon = locationService.getLongitude();
				}
			}
			if (!hotelName.equals("")) {
				srclat = data.getExtras().getString("srclat");
				srclon = data.getExtras().getString("srclon");
			}

		} else {

			if (resultCode == GET_ROUTE) {
				cleanMap();
				locationService.updateLocation();
				srclat = locationService.getLatitude();
				srclon = locationService.getLongitude();
				tgtlat = srclat;
				tgtlon = srclon;
				WebServiceTask wst = new WebServiceTask(
						WebServiceTask.POST_TASK, this, getResources()
						.getString(R.string.calculating));
				wst.addNameValuePair("lat_source", srclat);
				wst.addNameValuePair("lon_source", srclon);
				wst.addNameValuePair("lat_target", tgtlat);
				wst.addNameValuePair("lon_target", tgtlon);
				SharedPreferences pref = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				wst.addNameValuePair("transport",
						pref.getString("transport", "fo_"));
				Bundle bundle = data.getExtras();
				wst.addNameValuePair("count",
						String.valueOf(bundle.getInt("count")));
				Object[] o = (Object[]) bundle.getSerializable("pois");
				rowsItinerary = convert(o, RowItineraryList.class);
				int i = 0;
				for (RowItineraryList row : rowsItinerary) {
					wst.addNameValuePair("poi_id" + i,
							String.valueOf(row.getPoiId()));
					wst.addNameValuePair("coordinates" + i,
							row.getCoordinates());
					wst.addNameValuePair("score" + i,
							String.valueOf(row.getScore() * 20));
					i++;
				}
				wst.execute(new String[] { ROUTE_SERVICE_URL });
				recommendedPois = true;
			} else {
				if (resultCode == EXPRESS_ROUTE) {
					locationService.updateLocation();
					srclat = locationService.getLatitude();
					srclon = locationService.getLongitude();
					recommendedPois = false;
					cleanMap();
					getExpressItinerary();
				} else {
					openActivity(resultCode);
				}
			}

		}

		if (resultCode == SHOW_SAVE_ROUTE){
			cleanMap();
			loadCoordinates = data.getExtras().getString("load_coordinates");
			handleResponse(loadCoordinates);
		}

		if (resultCode == SHOW_MAP){
			String filepath = Environment.getExternalStorageDirectory() + 
					"/tourplanner/maps/" + data.getExtras().getString("map_name") + ".map";
			mapView.setMapFile(new File(filepath));
			mapView.setClickable(true);
			mapView.setBuiltInZoomControls(true);

			setContentView(mapView);
			itemizedOverlay = new MyItem(getResources().getDrawable(
					R.drawable.marker_red));

			mapView.getOverlays().add(itemizedOverlay);
		}


	}

	/**
	 * Método que convierte un array de Objects en un array de objetos de una
	 * clase dada.
	 * 
	 * @param objects
	 *            objetos a convertir
	 * @param type
	 *            tipo al que convertir los objetos
	 * @return los objetos convertidos
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] convert(Object[] objects, Class<?> type) {
		T[] convertedObjects = (T[]) Array.newInstance(type, objects.length);

		try {
			for (int i = 0; i < objects.length; i++) {
				convertedObjects[i] = (T) objects[i];
			}
		} catch (ClassCastException e) {

		}

		return convertedObjects;
	}

	/**
	 * Método que inicia una actividad en función de un código.
	 * 
	 * @param request
	 *            código que indica la actividad a abrir
	 */
	public void openActivity(int request) {
		Intent myIntent;
		switch (request) {
		case OPEN_PREFERENCES:
			myIntent = new Intent(MapSimple.this, PreferencesActivity.class);
			startActivityForResult(myIntent, 1);
			break;
		case OPEN_PLANNER:
			myIntent = new Intent(MapSimple.this, PlannerActivity.class);
			startActivityForResult(myIntent, 1);
			break;
		case OPEN_TRACKS:
			myIntent = new Intent(MapSimple.this, MyRoutesActivity.class);
			startActivityForResult(myIntent, 1);
			break;
		case OPEN_ADVANCED_OPTIONS:
			myIntent = new Intent(MapSimple.this, AdvancedOptionsActivity.class);
			startActivityForResult(myIntent, 1);
			break;
		case OPEN_RECOMENDED_PLACES:
			myIntent = new Intent(MapSimple.this, RecomendedPOIsActivity.class);
			locationService.updateLocation();
			myIntent.putExtra("lat", locationService.getLatitude());
			myIntent.putExtra("lon", locationService.getLongitude());
			startActivityForResult(myIntent, 1);
			break;
		case OPEN_PROFILE:
			SharedPreferences pref = PreferenceManager
			.getDefaultSharedPreferences(getApplicationContext());
			if (!pref.getBoolean("registered", false)) {
				DialogProfile dialog = new DialogProfile((Context) context);
				dialog.setTitle(getResources().getString(
						R.string.registerTourPlanner));
				dialog.show();
			} else {
				myIntent = new Intent(MapSimple.this, ProfileActivity.class);
				startActivityForResult(myIntent, 1);
			}
			break;
		case RESULT_CANCELED:
			if (getSlidingMenu().isMenuShowing()) {
				toggle();
			}
			break;
		case SHARE_ROUTE:
			Button share = (Button) findViewById(R.id.buttonShare);
			new SocialListener().onClick(share);
			break;
		}
	}

	/**
	 * Método que liga el servicio a la actividad.
	 */
	private void doBindService() {
		bindService(new Intent(this, CheckLocationService.class), mConnection,
				Context.BIND_AUTO_CREATE);
	}

	/**
	 * Método que centra el mapa el una localización concreta.
	 * 
	 * @param latitude
	 *            latitud de la localización
	 * @param longitude
	 *            longitud de la localización
	 */
	private void centerMap(String latitude, String longitude) {
		GeoPoint center = new GeoPoint(Double.valueOf(latitude),
				Double.valueOf(longitude));
		mapView.getController().setCenter(center);
		mapView.zoom((byte) 5, 11);
		mapView.redrawTiles();
	}

	/**
	 * Método que recibe y procesa la respuesta del servidor.
	 * 
	 * @param response
	 *            respuesta del servidor
	 */
	@SuppressWarnings("static-access")
	public void handleResponse(String response) {

		try {
			List<GeoPoint> wayPois;
			JSONParser parser = new JSONParser(response, itemizedOverlay, this);
			if (response.equals("")) {
				return;
			}
			startTime = Calendar.getInstance().getTime();
			JSONObject jso = new JSONObject(response);
			if (jso.has("status")
					&& !Misc.checkErrorCode(jso.getString("status"), this)) {
				return;
			}
			String hugeString = parser.getWayCoordinates();
			wayPois = geoPointsListFromHugeString(hugeString);
			OverlayWay route = new OverlayWay();
			if (wayPois.size() > 1) {
				GeoPoint[] geoPoints = wayPois.toArray(new GeoPoint[0]);
				route.setWayNodes(new GeoPoint[][] { geoPoints });
			}
			JSONArray listOfPois = jso.getJSONArray("poi_list");
			List<OverlayItem> items = parser.getOverlayItems();
			if (recommendedPois) {
				items = sortPois(listOfPois);
				JSONParser.setCost(rowsItinerary, response);
			} else {
				rowsItinerary = parser.getItineraryList();
			}
			locationService.setItinerary(getItineraryCoordinates());

			if (!hotelName.equals("")) {
				rowsItinerary[0].setImageResId(R.drawable.hotel);
				rowsItinerary[0].setImageTagResId(R.drawable.hotel);
				rowsItinerary[0].setTextName(hotelName);
				rowsItinerary[0].setTag(getResources()
						.getString(R.string.hotel));
				items.get(0).setMarker(
						itemizedOverlay.boundCenter(getResources().getDrawable(
								R.drawable.hotel)));
			}


			if (jso.has("city_name")){
				SharedPreferences pref = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				Editor edit = pref.edit();
				edit.putString("last_map", jso.getString("city_name"));
				edit.commit();
			} else {
				coordinates = response;
			}

			itemizedOverlay.addItems(items);
			int color;
			if (listOfPois.length() > 2) {
				color = Color.BLUE;
			} else {
				color = Color.RED;
			}
			Paint routePaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
			routePaint1.setStyle(Paint.Style.STROKE);
			routePaint1.setColor(color);
			routePaint1.setAlpha(160);
			routePaint1.setStrokeWidth(7);
			routePaint1.setStrokeCap(Paint.Cap.BUTT);
			routePaint1.setStrokeJoin(Paint.Join.ROUND);
			routePaint1.setPathEffect(new DashPathEffect(
					new float[] { 20, 20 }, 0));

			Paint routePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
			routePaint2.setStyle(Paint.Style.STROKE);
			routePaint2.setColor(color);
			routePaint2.setAlpha(96);
			routePaint2.setStrokeWidth(7);
			routePaint2.setStrokeCap(Paint.Cap.BUTT);
			routePaint2.setStrokeJoin(Paint.Join.ROUND);

			routeOverlay = new ArrayWayOverlay(routePaint1, routePaint2);
			routeOverlay.addWay(route);
			mapView.getOverlays().add(0, routeOverlay);
			String lon = rowsItinerary[0].getCoordinates().substring(
					rowsItinerary[0].getCoordinates().indexOf("(") + 1,
					rowsItinerary[0].getCoordinates().indexOf(" "));
			String lat = rowsItinerary[0].getCoordinates().substring(
					rowsItinerary[0].getCoordinates().indexOf(" ") + 1,
					rowsItinerary[0].getCoordinates().indexOf(")"));
			centerMap(lat, lon);
			setContentView(mapView);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Método que devuelve una lista con las coordenadas de la ruta mostrada.
	 * 
	 * @return lista de coordenadas.
	 */
	private List<String> getItineraryCoordinates() {
		List<String> coordinates = new ArrayList<String>();
		for (RowItineraryList row : rowsItinerary) {
			coordinates.add(row.getCoordinates());
		}
		return coordinates;
	}

	/**
	 * Método que limpia el mapa, eliminado las rutas y puntos marcados en el.
	 */
	private void cleanMap() {
		itemizedOverlay.clear();
		if (routeOverlay != null) {
			routeOverlay.clear();
		}
		System.gc();
		mapView.redrawTiles();
	}

	/**
	 * Método que ordena la lista de puntos de la ruta.
	 * 
	 * @param listOfPois
	 *            lista con el orden que deben tener los puntos de la ruta
	 * @return lista de items ordenados
	 */
	@SuppressWarnings("static-access")
	private List<OverlayItem> sortPois(JSONArray listOfPois) {
		List<OverlayItem> items = new ArrayList<OverlayItem>();
		try {
			RowItineraryList[] rows = new RowItineraryList[listOfPois.length()];
			int cont = 0;
			for (int i = 0; i < listOfPois.length(); i++) {
				JSONObject obj = listOfPois.getJSONObject(i);
				long id = obj.getLong("poi_id");
				if (i == 0) {
					RowItineraryList row = new RowItineraryList();
					OverlayItem item = new OverlayItem();
					item.setMarker(itemizedOverlay.boundCenter(getResources()
							.getDrawable(R.drawable.source_marker)));
					row.setImageResId(R.drawable.source_marker);
					row.setImageTagResId(R.drawable.source_marker);
					row.setTextName(getResources().getString(R.string.origin));
					row.setTag(" ");
					row.setPoiId(id);
					String coordinates = obj.getString("coordinates");
					row.setCoordinates(coordinates);
					double lati5Int = Double.parseDouble(coordinates.substring(
							coordinates.indexOf("(") + 1,
							coordinates.indexOf(" ")));
					double lati5Int2 = Double.parseDouble(coordinates
							.substring(coordinates.indexOf(" ") + 1,
									coordinates.indexOf(")")));
					;
					int latiE6 = (int) (lati5Int * 1000000);
					int latiE62 = (int) (lati5Int2 * 1000000);
					GeoPoint gPoint = new GeoPoint(latiE62, latiE6);
					item.setPoint(gPoint);
					item.setSnippet(String.valueOf(cont));
					rows[cont] = row;
					cont++;
					items.add(item);
				} else if (i == listOfPois.length() - 1) {
					RowItineraryList row = new RowItineraryList();
					OverlayItem item = new OverlayItem();
					item.setMarker(itemizedOverlay.boundCenter(getResources()
							.getDrawable(R.drawable.target_marker)));
					row.setImageResId(R.drawable.target_marker);
					row.setImageTagResId(R.drawable.target_marker);
					row.setTextName(getResources().getString(R.string.target));
					row.setTag(" ");
					row.setPoiId(id);
					String coordinates = obj.getString("coordinates");
					row.setCoordinates(coordinates);
					double lati5Int = Double.parseDouble(coordinates.substring(
							coordinates.indexOf("(") + 1,
							coordinates.indexOf(" ")));
					double lati5Int2 = Double.parseDouble(coordinates
							.substring(coordinates.indexOf(" ") + 1,
									coordinates.indexOf(")")));
					;
					int latiE6 = (int) (lati5Int * 1000000);
					int latiE62 = (int) (lati5Int2 * 1000000);
					GeoPoint gPoint = new GeoPoint(latiE62, latiE6);
					item.setPoint(gPoint);
					item.setSnippet(String.valueOf(cont));
					rows[cont] = row;
					cont++;
					itemizedOverlay.addItem(item);
				} else {
					for (int j = 0; j < rowsItinerary.length; j++) {
						if (rowsItinerary[j].getPoiId() == id) {
							rows[cont] = rowsItinerary[j];
							double lati5Int = Double
									.parseDouble(rowsItinerary[j]
											.getCoordinates().substring(
													0,
													rowsItinerary[j]
															.getCoordinates()
															.indexOf(" ")));
							double lati5Int2 = Double
									.parseDouble(rowsItinerary[j]
											.getCoordinates().substring(
													rowsItinerary[j]
															.getCoordinates()
															.indexOf(" ") + 1));
							;
							int latiE6 = (int) (lati5Int * 1000000);
							int latiE62 = (int) (lati5Int2 * 1000000);
							GeoPoint gPoint = new GeoPoint(latiE62, latiE6);
							OverlayItem item = new OverlayItem();
							String coordinates = "("
									+ rows[cont].getCoordinates() + ")";
							rows[cont].setCoordinates(coordinates);
							cont++;
							item.setSnippet(String.valueOf(cont - 1));
							item.setPoint(gPoint);
							item.setMarker(itemizedOverlay
									.boundCenter(getResources().getDrawable(
											(rowsItinerary[j]
													.getImageTagResId()))));
							items.add(item);
							break;
						}
					}
				}
			}
			rowsItinerary = rows;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return items;
	}

	/**
	 * Clase interna que implement los items que se muestran en el mapa.
	 * 
	 * @author Inigo Vázquez - Roberto Villuela
	 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
	 */
	private class MyItem extends ArrayItemizedOverlay {
		/**
		 * Constructor de la clase
		 * 
		 * @param defaultMarker
		 *            marcador asociado al item
		 */
		public MyItem(Drawable defaultMarker) {
			super(defaultMarker);
		}

		/**
		 * Método que se llama cuando se realiza una pulsación larga sobre el
		 * mapa
		 */
		@Override
		public boolean onLongPress(GeoPoint geo, MapView map) {
			if (!(targetSelected && sourceSelected)) {
				final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context.getContext());
				if (!sourceSelected) {
					srclat = String.valueOf(geo.getLatitude());
					srclon = String.valueOf(geo.getLongitude());
					alertDialogBuilder.setTitle(getResources().getString(
							R.string.confirmSource));
					alertDialogBuilder.setMessage(getResources().getString(
							R.string.msgConfirmSource));
				} else {
					tgtlat = String.valueOf(geo.getLatitude());
					tgtlon = String.valueOf(geo.getLongitude());

					alertDialogBuilder.setTitle(getResources().getString(
							R.string.confirmTarget));
					alertDialogBuilder.setMessage(getResources().getString(
							R.string.msgConfirmTarget));
				}
				alertDialogBuilder.setCancelable(true);
				alertDialogBuilder.setPositiveButton(
						getResources().getString(R.string.yes),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (!sourceSelected) {
									sourceSelected = !sourceSelected;
									if (sourceEqtarget) {
										tgtlat = srclat;
										tgtlon = srclon;
										targetSelected = !targetSelected;
										getItinerary(false);
									} else {
										if (!poiTarget.equals("")) {
											targetSelected = !targetSelected;
											getItinerary(true);
										}
										Toast.makeText(
												getApplicationContext(),
												getResources()
												.getString(
														R.string.onTapSelectTarget),
														Toast.LENGTH_LONG).show();
									}

								} else {
									getItinerary(false);
									targetSelected = !targetSelected;
								}
							}
						});
				alertDialogBuilder.setNegativeButton(
						getResources().getString(R.string.no),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();

							}
						});
				MapSimple.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AlertDialog alert = alertDialogBuilder.create();
						alert.show();

					}
				});
			}

			return true;
		}

		/**
		 * Método que se llama cuando se pincha sobre un item.
		 */
		@SuppressWarnings("deprecation")
		@Override
		public boolean onTap(int index) {
			OverlayItem item = createItem(index);
			if (item != null && item != actualPosition) {
				final Dialog dialog = new Dialog(context.getContext());
				// dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.point_dialog);
				dialog.setTitle(getResources().getString(R.string.pointInfo));
				final int rowIndex = Integer.valueOf(item.getSnippet());

				lon1 = Double.parseDouble(rowsItinerary[rowIndex].getCoordinates().substring(
						rowsItinerary[rowIndex].getCoordinates().indexOf("(") + 1,
						rowsItinerary[rowIndex].getCoordinates().indexOf(" ")));

				lat1 = Double.parseDouble(rowsItinerary[rowIndex].getCoordinates().substring(
						rowsItinerary[rowIndex].getCoordinates().indexOf(" ") + 1,
						rowsItinerary[rowIndex].getCoordinates().indexOf(")")));

				// set the custom dialog components - text, image and button
				TextView textName = (TextView) dialog
						.findViewById(R.id.textViewItineraryName);
				TextView textTag = (TextView) dialog
						.findViewById(R.id.textViewTag);
				textName.setText(rowsItinerary[rowIndex].getTextName());
				textTag.setText(rowsItinerary[rowIndex].getTag());
				ImageView image = (ImageView) dialog
						.findViewById(R.id.imageViewItineraryList);
				image.setImageResource(rowsItinerary[rowIndex].getImageResId());
				image = (ImageView) dialog
						.findViewById(R.id.imageViewItineraryListTag);
				image.setImageResource(rowsItinerary[rowIndex]
						.getImageTagResId());
				((RatingBar) dialog.findViewById(R.id.ratingBar))
				.setRating((float) rowsItinerary[rowIndex].getScore());
				((RatingBar) dialog.findViewById(R.id.ratingBar))
				.setIsIndicator(true);
				TextView textPromoted = (TextView) dialog
						.findViewById(R.id.tv_Promoted);
				String strPromotedFormat = getResources().getString(R.string.promoted);
				if (rowsItinerary[rowIndex].isPromoted()){
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
						Intent i = new Intent(MapSimple.this, GalleryActivity.class);
						i.putExtra("imageUrls", imageUrls);
						i.putExtra("authors", authors);
						i.putExtra("authorsUrl", authorsUrl);
						startActivity(i);

					}

				});

				// A�adimos bot�n de informaci�n adicional.
				infoButton = (Button) dialog.findViewById(R.id.dialogButtonInfo);

				// Cambiamos el icono para la informaci�n si el POI es de gastronom�a u ocio.
				if (rowsItinerary[rowIndex]
						.getImageResId() == R.drawable.leisure || rowsItinerary[rowIndex]
								.getImageResId() == R.drawable.gastronomy){
					infoButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_google, 0);
				}

				infoButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						switch (rowsItinerary[rowIndex]
								.getImageResId()){

						// Si el POI es de cultura o naturaleza, mostramos informaci�n sobre la Wikipedia.
								case R.drawable.culture:
								case R.drawable.nature:
									// Obtenemos logitud y latitud del POI en el que estamos, y el idioma en el que est� la app.
									Double longitude = Double.parseDouble(rowsItinerary[rowIndex].getCoordinates().substring(
											rowsItinerary[rowIndex].getCoordinates().indexOf("(") + 1,
											rowsItinerary[rowIndex].getCoordinates().indexOf(" ")));

									Double latitude = Double.parseDouble(rowsItinerary[rowIndex].getCoordinates().substring(
											rowsItinerary[rowIndex].getCoordinates().indexOf(" ") + 1,
											rowsItinerary[rowIndex].getCoordinates().indexOf(")")));

									String lang = Locale.getDefault().getLanguage();

									// Realizamos una llamada al servicio REST de Wikilocation.
									WIKILOCATION_SERVICE_URL = "http://api.wikilocation.org/articles?lat="
											+latitude+"&lng="+longitude+"&limit=1&locale="+lang+"";

									WikilocationTask wlt = new WikilocationTask(serviceContext);
									wlt.execute(new String[]{ WIKILOCATION_SERVICE_URL });
									break;

									// Si el POI es de ocio o gastronom�a, mostramos informaci�n sobre TripAdvisor.		
								case R.drawable.leisure:
								case R.drawable.gastronomy:	 
									String namePoi = rowsItinerary[rowIndex].getTextName();
									TRIPADVISOR_SEARCH_URL = "https://www.google.com/#q=site:tripadvisor.com+"+namePoi+"";
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
						startTime = Calendar.getInstance().getTime();
						ItineraryListAdapter adaptadorItinerary = new ItineraryListAdapter(
								MapSimple.this, rowsItinerary, startTime);
						DialogOpinion dialog = new DialogOpinion((Context) context,
								rowsItinerary[rowIndex], adaptadorItinerary);
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
					PANORAMIO_SERVICE_URL = "http://www.panoramio.com/map/get_panoramas.php?set=public&" +
							"from=0&" +
							"to=5&" +
							"minx=" + lon1 + "&" + 
							"miny=" + (lat1 - 0.001) + "&" +
							"maxx=" + (lon1 + 0.001) + "&" +
							"maxy=" + lat1 + "&" +
							"size=small&" +
							"mapfilter=false";

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
			return true;
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
					authors = new ArrayList<String>();
					authorsUrl = new ArrayList<String>();
					for (int i = 0; i < photos.length(); i++){
						photo = photos.getJSONObject(i);
						imageUrls.add(photo.getString("photo_file_url"));
						authors.add(photo.getString("owner_name"));
						authorsUrl.add(photo.getString("owner_url"));
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
	 * Método que realiza una petición al servidor de una ruta recomendada.
	 */
	public void getExpressItinerary() {
		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this,
				getResources().getString(R.string.calculating));

		wst.addNameValuePair("lat_source", srclat);
		wst.addNameValuePair("lon_source", srclon);
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		wst.addNameValuePair("transport", pref.getString("transport", "fo_"));
		wst.addNameValuePair("route_mode", pref.getString("route_mode", "fast"));
		wst.execute(new String[] { EXPRESS_ITINERARY_SERVICE_URL });
	}

	/**
	 * Método que realiza una petición al srevidor de una ruta.
	 * 
	 * @param poiTarget
	 *            booleano que indica si el punto de fin es un POI o no
	 */
	private void getItinerary(boolean poiTarget) {
		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this,
				getResources().getString(R.string.calculating));

		wst.addNameValuePair("lat_source", srclat);
		wst.addNameValuePair("lon_source", srclon);
		if (!poiTarget) {
			wst.addNameValuePair("lat_target", tgtlat);
			wst.addNameValuePair("lon_target", tgtlon);
		} else {
			wst.addNameValuePair("target_options", this.poiTarget);
		}
		wst.addNameValuePair("gastronomy", String.valueOf(gastronomyScore));
		wst.addNameValuePair("leisure", String.valueOf(leisureScore));
		wst.addNameValuePair("culture", String.valueOf(cultureScore));
		wst.addNameValuePair("nature", String.valueOf(natureScore));
		wst.addNameValuePair("time", String.valueOf(routeTime));
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		wst.addNameValuePair("transport", pref.getString("transport", "fo_"));
		wst.addNameValuePair("route_mode", pref.getString("route_mode", "fast"));
		String categories = "";
		for (String cat : pref.getAll().keySet()) {
			try {
				if (!pref.getBoolean(cat, true)) {
					categories += cat + ",";
				}
			} catch (ClassCastException e) {

			}
		}
		if (categories.length() != 0) {
			categories = categories.substring(0, categories.length() - 1);
		}
		wst.addNameValuePair("setOfTags", categories);
		wst.execute(new String[] { ITINERARY_SERVICE_URL });

	}

	/**
	 * Método que a partir de una cadena devuelve una lista de Geopoints
	 * 
	 * @param hugeString
	 *            cadena que contiene todas las coordenadas de los puntos
	 * @return lista de Geopoints
	 */
	private List<GeoPoint> geoPointsListFromHugeString(String hugeString) {
		List<GeoPoint> poly = new ArrayList<GeoPoint>();
		String[] data;
		double lati5Int, lati5Int2;
		int latiE6, latiE62;
		data = hugeString.split(" ");
		for (int i = 1; i < data.length; i += 2) {
			lati5Int = Double.parseDouble(data[i - 1]);
			lati5Int2 = Double.parseDouble(data[i]);
			latiE6 = (int) (lati5Int * 1000000);
			latiE62 = (int) (lati5Int2 * 1000000);
			GeoPoint gPoint = new GeoPoint(latiE62, latiE6);
			;
			poly.add(gPoint);
		}
		return poly;
	}

	/**
	 * Método que se llama cuando se pincha sobre un icono de la barra superior.
	 */
	@SuppressWarnings("static-access")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		case R.id.itineraryList:
			if (rowsItinerary != null) {
				if (mapShowed) {
					setContentView(R.layout.list_itinerary_result);
					final ItineraryListAdapter adaptadorItinerary = new ItineraryListAdapter(
							this, rowsItinerary, startTime);
					ListView lstItinerary = (ListView) findViewById(R.id.listViewItineraryResult);
					lstItinerary.setClickable(true);
					lstItinerary.setAdapter(adaptadorItinerary);
					lstItinerary
					.setOnItemClickListener(new ItineraryListListener(
							adaptadorItinerary));
				} else {
					setContentView(mapView);
				}
				mapShowed = !mapShowed;
			} else {
				Toast.makeText(
						getApplicationContext(),
						getResources().getString(
								R.string.configureItinearyFirst),
								Toast.LENGTH_LONG).show();
			}
			return true;

		case R.id.myLocation: 
			if (actualPosition.getPoint() != null){
				Double latitud = actualPosition.getPoint().getLatitude();
				Double longitud = actualPosition.getPoint().getLongitude();
				String lat = latitud.toString();
				String lon = longitud.toString();
				actualPosition.setMarker(itemizedOverlay.boundCenter(getResources()
						.getDrawable(R.drawable.you_are_here)));
				centerMap(lat, lon);
			} else {
				Toast.makeText(
						getApplicationContext(),
						getResources().getString(
								R.string.infoMyLocation),
								Toast.LENGTH_LONG).show();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActionModeStarted(ActionMode mode) {

	}

	@Override
	public void onActionModeFinished(ActionMode mode) {

	}

	/**
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Método que devuelve el contexto de esta actividad
	 */
	@Override
	public Context getContext() {
		return this;

	}

	/**
	 * Método por el cuál se comunica el servicio con la activity,para indicar
	 * la posición actual.
	 * 
	 * @param lat
	 *            latitud del punto
	 * @param lon
	 *            longitud del punto
	 */
	@SuppressWarnings("static-access")
	@Override
	public void serviceClientMethod(String lat, String lon) {
		double lati5Int = Double.parseDouble(lon);
		double lati5Int2 = Double.parseDouble(lat);
		int latiE6 = (int) (lati5Int * 1000000);
		int latiE62 = (int) (lati5Int2 * 1000000);
		GeoPoint gPoint = new GeoPoint(latiE62, latiE6);
		itemizedOverlay.removeItem(actualPosition);
		actualPosition.setPoint(gPoint);
		actualPosition.setMarker(itemizedOverlay.boundCenter(getResources()
				.getDrawable(R.drawable.you_are_here)));
		actualPosition.setTitle("Posición actual");
		actualPosition.setSnippet(" ");
		itemizedOverlay.addItem(actualPosition);
		//centerMap(lat, lon);
		mapView.redrawTiles();
	}



	/**
	 * Clase interna que implementa el oyente de cuando se hace click sobre un
	 * elemento de la lista que contiene los elementos del itinerario.
	 * 
	 * @author Inigo Vázquez - Roberto Villuela
	 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
	 */
	private class ItineraryListListener implements OnItemClickListener {
		/**
		 * Adaptador de la lista.
		 */
		private ItineraryListAdapter adapter;
		/**
		 * Constructor de la clase.
		 * @param adapter adpatador de la lista
		 */
		public ItineraryListListener(ItineraryListAdapter adapter) {
			super();
			this.adapter = adapter;
		}
		/**
		 * Método que se llama cuando se hace click sobre un elemento de la lista.
		 */
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if (arg2 != 0 && arg2 != rowsItinerary.length - 1) {
				DialogOpinion dialog = new DialogOpinion((Context) context,
						rowsItinerary[arg2], adapter);
				dialog.show();
			}
		}

	}
	/**
	 * Clase interna que implementa la respuesta al cerrar el dialog de las redes sociales.
	 * 
	 * @author Inigo Vázquez - Roberto Villuela
	 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
	 */
	private final class ResponseListener implements DialogListener {

		/**
		 * Método que se llama cuando se completa el dialog.
		 */
		@Override
		public void onComplete(Bundle values) {
			if (rowsItinerary != null && rowsItinerary.length > 0) {

				Log.d("ShareButton", "Authentication Successful");

				// Get name of provider after authentication
				final String providerName = values
						.getString(SocialAuthAdapter.PROVIDER);
				Log.d("ShareButton", "Provider Name = " + providerName);
				if (providerName.equals("facebook") || providerName.equals("twitter")){
					String ruta = "He realizado una ruta con #TourPlannerApp y he visitado ";
					int culture = 0, gastronomy = 0, nature = 0, leisure = 0;
					for (RowItineraryList row : rowsItinerary) {
						switch (row.getImageResId()) {
						case R.drawable.culture:
							culture++;
							break;
						case R.drawable.nature:
							nature++;
							break;
						case R.drawable.leisure:
							leisure++;
							break;
						case R.drawable.gastronomy:
							gastronomy++;
							break;
						}
					}
					ruta += culture + " sitios de "
							+ getResources().getString(R.string.culture) + ", "
							+ leisure + " de "
							+ getResources().getString(R.string.leisure) + ", "
							+ nature + " de "
							+ getResources().getString(R.string.nature) + " y "
							+ gastronomy + " de "
							+ getResources().getString(R.string.gastronomy) + ".";
					adapter.updateStatus(ruta, null, false);
					Toast.makeText(
							MapSimple.this,
							getResources().getString(R.string.messagePostedIn)
							+ " " + providerName, Toast.LENGTH_LONG).show();
					if (socialDialog != null) {
						socialDialog.dismiss();
					}
				} else {


				}
			} else {
				Toast.makeText(
						MapSimple.this,
						getResources().getString(
								R.string.configureItinearyFirst),
								Toast.LENGTH_LONG).show();
			}

		}

		@Override
		public void onBack() {


		}

		@Override
		public void onCancel() {

		}

		@Override
		public void onError(SocialAuthError arg0) {

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
