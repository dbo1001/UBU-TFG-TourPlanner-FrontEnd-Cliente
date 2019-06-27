package com.example.tourplanner2.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import com.example.tourplanner2.BuildConfig;
import com.example.tourplanner2.R;
import com.example.tourplanner2.adapters.CustomSocialAdapter;
import com.example.tourplanner2.adapters.ItineraryListAdapter;
import com.example.tourplanner2.adapters.OptionsAdapter;
import com.example.tourplanner2.communication.IServiceTask;
import com.example.tourplanner2.communication.IWebServiceTaskResult;
import com.example.tourplanner2.communication.WebServiceTask;
import com.example.tourplanner2.dialog.DialogOpinion;
import com.example.tourplanner2.dialog.DialogProfile;
import com.example.tourplanner2.services.CheckLocationService;
import com.example.tourplanner2.util.JSONParser;
import com.example.tourplanner2.util.Misc;
import com.example.tourplanner2.util.MyItem;
import com.example.tourplanner2.util.PropertiesParser;
import com.example.tourplanner2.util.RowItineraryList;
import com.example.tourplanner2.util.RowListView;
import com.example.tourplanner2.util.SSLFactory;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapView;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.mapsforge.MapsForgeTileProvider;
import org.osmdroid.mapsforge.MapsForgeTileSource;

//import org.mapsforge.map.android.view.MapView;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.Marker;

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
import java.util.Objects;

public class MapMain extends SlidingActivity implements
		IWebServiceTaskResult, IServiceTask, CheckLocationService.ServiceClient {

	/**
	 * Mapa en el que se muestran las rutas.
	 */
	private MapView myOpenMapView;

	private MapController myMapController;

	ArrayList<OverlayItem> points = new ArrayList<>();

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
	 * Lista de items que actua como heredera de la clase de ItemizedOverlay.
	 */
	private MyItem itemizedOverlay;

	/**
	 * Urls de los diferentes servicios utilizados.
	 */
	private static String ITINERARY_SERVICE_URL, EXPRESS_ITINERARY_SERVICE_URL,
			ROUTE_SERVICE_URL, RATING_SERVICE_URL, WIKILOCATION_SERVICE_URL,
			TRIPADVISOR_SEARCH_URL, GET_CITY_SERVICE, PANORAMIO_SERVICE_URL;
	/**
	 * Coordenadas de inicio y fin de la ruta.
	 */
	private String srclat, srclon, tgtlat, tgtlon;
	/**
	 * Bot�n de informaci�n sobre los puntos de inter�s.
	 * */
	private Button infoButton;
	/**
	 * Imagen a mostrar sobre el POI.
	 * */
	private ImageView panoramioImage;

	/**
	 * Servicio de localización.
	 */
	private CheckLocationService locationService;

	/**
	 * Array con los puntos de la ruta.
	 */
	private RowItineraryList[] rowsItinerary;
	/**
	 * Tiempo máximo de la ruta actual.
	 */
	private double routeTime;
	/**
	 * Indica si se selecciono la ruta a partir de unos puntos recomendados.
	 */
	private boolean recommendedPois = false;

	/**
	 * Contexto IWebServiceTaskResult de la actividad.
	 */
	private IWebServiceTaskResult context;
	/**
	 * Contexto IServiceTask de la actividad.
	 */
	private IServiceTask serviceContext;
	/**
	 * Indican esta seleccionado o no el origen y el destino.
	 */
	private boolean targetSelected = true, sourceSelected = true;
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
	 * Adaptador para compartir en las redes sociales.
	 */
	private SocialAuthAdapter adapter;
	/**
	 * Cadena que indica la categoria del POI en el que hay que finalizar, si el
	 * usuario lo seleccionó.
	 */
	private String poiTarget = "";
	/**
	 * Posicion actual en el mapa.
	 */
	private OverlayItem actualPosition;
	/**
	 * Indica si se selecciono origen y destino iguales
	 */
	private boolean sourceEqtarget = false;
	/**
	 * Puntuación seleccionada por el usuario para las diferentes categorias.
	 */
	private int leisureScore, gastronomyScore, cultureScore, natureScore;
	/**
	 * Dialogo para compartir en las redes sociales.
	 */
	private Dialog socialDialog;
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
	 * Coordenadas del punto de la imagen de un POI.
	 * */
	private Double lat1, lon1;
	/**
	 * TextView que muestra los t�rminos y condiciones de Panoramio.
	 * */
	private TextView panoramioText;
	/**
	 * ImageView que muestra el logo de Panoramio.
	 * */
	private ImageView panoramioLogo;
	/**
	 * Coordenadas de la ruta.
	 * */
	private String coordinates = null;
	/**
	 * Tiempo de comienzo de la ruta.
	 */
	private Date startTime;
	/**
	 * Conexión con el servicio.
	 */
	private ServiceConnection mConnection;
	/**
	 * Indica si el servicio esta activo o no.
	 */
	private boolean mBound;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
		RowListView[] rows = new RowListView[7];

		//Primero, comprobamos si tiene conexion a internet, para poder acceder a mapas sin conexion o con ella.
		if (!isNetworkAvailable()){
			buildAlertMessageNoInternet();
			myOpenMapView = new MapView(this);

			String mapName = null;
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			if (pref.getString("recently_map", "") != null){
				mapName = pref.getString("recently_map", "");
			} else {
				mapName = pref.getString("last_map", "");
			}



		}else {
			if (writePermissions()){
				loadMaps();
			} else{
				myOpenMapView = new MapView(this);
				myMapController = (MapController) myOpenMapView.getController();
				myOpenMapView.setMultiTouchControls(true);
			}

		}

		myOpenMapView.setClickable(true);
		myOpenMapView.setBuiltInZoomControls(true);

		setContentView(myOpenMapView);

		setServiceDirections();
		createSSLSocketFactory();
		context = this;
		serviceContext = this;
		targetSelected = true;
		sourceSelected = true;
		createSlidingMenu(rows);
		itemizedOverlay = new MyItem(getResources().getDrawable(
				R.drawable.marker_red));

		myOpenMapView.getOverlays().add(itemizedOverlay);

		OptionsAdapter adaptador = new OptionsAdapter(this, rows);
		ListView lstOpciones = (ListView) findViewById(R.id.listView);
		lstOpciones.setAdapter(adaptador);
		adapter = new SocialAuthAdapter(new MapMain.ResponseListener());
		adapter.addProvider(SocialAuthAdapter.Provider.FACEBOOK, R.drawable.facebook);
		adapter.addProvider(SocialAuthAdapter.Provider.TWITTER, R.drawable.twitter);
		adapter.addProvider(SocialAuthAdapter.Provider.FLICKR, R.drawable.flickr);
		adapter.addProvider(SocialAuthAdapter.Provider.INSTAGRAM, R.drawable.instagram);

		Button share = (Button) findViewById(R.id.buttonShare);
		share.setOnClickListener(new MapMain.SocialListener());

		lstOpciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
			                        long id) {
				Intent myIntent;
				switch (position) {
					case 0:
						myIntent = new Intent(MapMain.this,
								PreferencesActivity.class);
						startActivityForResult(myIntent, 1);
						break;
					case 1:
						myIntent = new Intent(MapMain.this, PlannerActivity.class);
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
						myIntent = new Intent(MapMain.this,
								RecomendedPOIsActivity.class);
						myIntent.putExtra("lat", locationService.getLatitude());
						myIntent.putExtra("lon", locationService.getLongitude());
						startActivityForResult(myIntent, 1);
						break;
					case 4:
						myIntent = new Intent(MapMain.this, MyRoutesActivity.class);
						if (coordinates != null){
							JSONParser parser = new JSONParser(coordinates, itemizedOverlay, MapMain.this);
							String hugeString = parser.getWayCoordinates();
							String[] data = hugeString.split(" ");
							myIntent.putExtra("longitude", data[0]);
							myIntent.putExtra("latitude", data[1]);
						}
						myIntent.putExtra("coordinates", coordinates);
						startActivityForResult(myIntent, 1);
						break;
					case 5:
						myIntent = new Intent(MapMain.this,
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
							myIntent = new Intent(MapMain.this,
									ProfileActivity.class);
							startActivityForResult(myIntent, 1);
						}
						break;
				}
			}
		});
	}

	private void loadMaps() {
		GeoPoint madrid = new GeoPoint(40.416775, -3.70379);

		myOpenMapView = (MapView) findViewById(R.id.openmapview);
		myOpenMapView.setBuiltInZoomControls(true);
		myMapController = (MapController) myOpenMapView.getController();
		myMapController.setCenter(madrid);
		myMapController.setZoom(6);

		myOpenMapView.setMultiTouchControls(true);

		// Añadir un punto en el mapa
		points.add(new OverlayItem("Madrid", "Ciudad de Madrid", madrid));
		updatePoints();
	}

	private void cleanMap(){
		points.clear();
		myOpenMapView.getOverlays().clear();
		updatePoints();
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
					locationService.setServiceClient(MapMain.this);

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
	 * Método que liga el servicio a la actividad.
	 */
	private void doBindService() {
		bindService(new Intent(this, CheckLocationService.class), mConnection,
				Context.BIND_AUTO_CREATE);
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
		Objects.requireNonNull(getActionBar()).setDisplayHomeAsUpEnabled(true);
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
				myIntent = new Intent(MapMain.this, PreferencesActivity.class);
				startActivityForResult(myIntent, 1);
				break;
			case OPEN_PLANNER:
				myIntent = new Intent(MapMain.this, PlannerActivity.class);
				startActivityForResult(myIntent, 1);
				break;
			case OPEN_TRACKS:
				myIntent = new Intent(MapMain.this, MyRoutesActivity.class);
				startActivityForResult(myIntent, 1);
				break;
			case OPEN_ADVANCED_OPTIONS:
				myIntent = new Intent(MapMain.this, AdvancedOptionsActivity.class);
				startActivityForResult(myIntent, 1);
				break;
			case OPEN_RECOMENDED_PLACES:
				myIntent = new Intent(MapMain.this, RecomendedPOIsActivity.class);
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
					myIntent = new Intent(MapMain.this, ProfileActivity.class);
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
				new MapMain.SocialListener().onClick(share);
				break;
		}
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
		lstItinerary.setOnItemClickListener(new MapMain.ItineraryListListener(
				adaptadorItinerary));
		lstItinerary.setAdapter(adaptadorItinerary);

		Button dialogButton = (Button) dialog
				.findViewById(R.id.buttonDialogCancel);
		dialogButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialogButton = (Button) dialog.findViewById(R.id.buttonDialogSend);
		dialogButton.setOnClickListener(new View.OnClickListener() {
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


		myOpenMapView.getController().setCenter(center);
		myOpenMapView.getController().setZoom(6);
		myOpenMapView.setMultiTouchControls(true);
		updatePoints();
	}

	/**
	 * Método que comprueba si la conexión de red esta disponible.
	 *
	 * @return true si la conexión a internet está disponible
	 */
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = null;
		if (connectivityManager != null) {
			activeNetworkInfo = connectivityManager
					.getActiveNetworkInfo();
		}
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
		/*
		if (resultCode == SHOW_MAP){
			String filepath = Environment.getExternalStorageDirectory() +
					"/tourplanner/maps/" + data.getExtras().getString("map_name") + ".map";
			mapView.setMapFile(new File(filepath));
			mapView.getM
			mapView.setClickable(true);
			mapView.setBuiltInZoomControls(true);

			setContentView(mapView);
			itemizedOverlay = new MapMain.MyItem(getResources().getDrawable(
					R.drawable.marker_red));

			mapView.getOverlays().add(itemizedOverlay);
		}*/


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

						MapMain.DownloadPhotoTask dpt = new MapMain.DownloadPhotoTask();
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

	@Override
	public void handleResponse(String response) {
		try {
			RoadManager roadManager = new OSRMRoadManager(this);
			ArrayList<GeoPoint> wayPois;
			JSONParser parser = new JSONParser(response, itemizedOverlay,MapMain.this);
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
			wayPois = (ArrayList)geoPointsListFromHugeString(hugeString);
			Road road = null;
			Polyline route = new Polyline();
			//OverlayWay route = new OverlayWay();
			if (wayPois.size() > 1) {
				road = roadManager.getRoad(wayPois);
				//GeoPoint[] geoPoints = wayPois.toArray(new GeoPoint[0]);
				//route.setWayNodes(new GeoPoint[][] { geoPoints });
				route = RoadManager.buildRoadOverlay(road);
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
						itemizedOverlay.boundToHotspot(getResources().getDrawable(
								R.drawable.hotel), OverlayItem.HotspotPlace.CENTER));
			}


			if (jso.has("city_name")){
				SharedPreferences pref = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor edit = pref.edit();
				edit.putString("last_map", jso.getString("city_name"));
				edit.commit();
			} else {
				coordinates = response;
			}

			itemizedOverlay.getDisplayedItems().add(items);
			int color;
			if (listOfPois.length() > 2) {
				color = Color.BLUE;
			} else {
				color = Color.RED;
			}
			/*
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
			*/
			Polyline routeOverlay = route;
			routeOverlay.getPaint().setStyle(Paint.Style.STROKE);
			routeOverlay.getPaint().setColor(color);
			routeOverlay.getPaint().setAlpha(160);
			routeOverlay.getPaint().setStrokeWidth(7);
			routeOverlay.getPaint().setStrokeCap(Paint.Cap.BUTT);
			routeOverlay.getPaint().setStrokeJoin(Paint.Join.ROUND);
			routeOverlay.getPaint().setPathEffect(new DashPathEffect(
					new float[] { 20, 20 }, 0));
			//routeOverlay = new ItemizedIconOverlay(routePaint1, routePaint2);
			//routeOverlay.addWay(route);

			myOpenMapView.getOverlays().add(0,routeOverlay);
			String lon = rowsItinerary[0].getCoordinates().substring(
					rowsItinerary[0].getCoordinates().indexOf("(") + 1,
					rowsItinerary[0].getCoordinates().indexOf(" "));
			String lat = rowsItinerary[0].getCoordinates().substring(
					rowsItinerary[0].getCoordinates().indexOf(" ") + 1,
					rowsItinerary[0].getCoordinates().indexOf(")"));
			centerMap(lat, lon);
			setContentView(myOpenMapView);

		} catch (Exception e) {
			e.printStackTrace();
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
					//OverlayItem item = new OverlayItem();
					OverlayItem item = new OverlayItem(row.getTextName(),String.valueOf(cont),gPoint);
					item.setMarker(itemizedOverlay.
							boundToHotspot(getResources().getDrawable(R.drawable.source_marker),
									OverlayItem.HotspotPlace.CENTER));
					//item.setPoint(gPoint);
					//item.setSnippet(String.valueOf(cont));
					rows[cont] = row;
					cont++;
					items.add(item);
				} else if (i == listOfPois.length() - 1) {
					RowItineraryList row = new RowItineraryList();
					//OverlayItem item = new OverlayItem();
					//item.setMarker(itemizedOverlay.boundCenter(getResources()
					//		.getDrawable(R.drawable.target_marker)));
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
					OverlayItem item = new OverlayItem(row.getTextName(),String.valueOf(cont),gPoint);
					item.setMarker(itemizedOverlay.
							boundToHotspot(getResources().getDrawable(R.drawable.source_marker),
									OverlayItem.HotspotPlace.CENTER));
					//item.setPoint(gPoint);
					//item.setSnippet(String.valueOf(cont));
					rows[cont] = row;
					cont++;
					itemizedOverlay.getDisplayedItems().add(item);
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
							String coordinates = "("
									+ rows[cont].getCoordinates() + ")";
							rows[cont].setCoordinates(coordinates);
							cont++;
							GeoPoint gPoint = new GeoPoint(latiE62, latiE6);
							OverlayItem item = new OverlayItem(rowsItinerary[j].getTextName(),String.valueOf(cont - 1),gPoint);
							//item.setSnippet(String.valueOf(cont - 1));
							//item.setPoint(gPoint);
							item.setMarker(itemizedOverlay
									.boundToHotspot(getResources().getDrawable((rowsItinerary[j].getImageTagResId())),
											OverlayItem.HotspotPlace.CENTER));
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
			poly.add(gPoint);
		}
		return poly;
	}

	@Override
	public Context getContext() {
		return null;
	}

	@Override
	public void serviceClientMethod(String lat, String lon) {
		double lati5Int = Double.parseDouble(lon);
		double lati5Int2 = Double.parseDouble(lat);
		int latiE6 = (int) (lati5Int * 1000000);
		int latiE62 = (int) (lati5Int2 * 1000000);
		GeoPoint gPoint = new GeoPoint(latiE62, latiE6);
		if(actualPosition != null){
			itemizedOverlay.getDisplayedItems().remove(actualPosition);
		}
		actualPosition = new OverlayItem("posición actual"," ",gPoint);
		//actualPosition.setPoint(gPoint);
		actualPosition.setMarker(itemizedOverlay.boundToHotspot(getResources()
				.getDrawable(R.drawable.you_are_here), OverlayItem.HotspotPlace.CENTER));
		//actualPosition.setTitle("Posición actual");
		//actualPosition.setSnippet(" ");
		itemizedOverlay.getDisplayedItems().add(actualPosition);
		//centerMap(lat, lon);
		myOpenMapView.invalidate();
	}


	public boolean writePermissions(){
		if (Build.VERSION.SDK_INT >= 23){
			if (checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
				return true;
			}else{
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
				return false;
			}
		}else{
			return true;
		}
	}

	public boolean locationPermissions(){
		if (Build.VERSION.SDK_INT >= 23){
			if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
				return true;
			}else{
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
				return false;
			}
		}else{
			return true;
		}
	}

	public void onRealTimeLocation(Location location){
		GeoPoint realLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
		myMapController.setCenter(realLocation);

		if (points.size() > 1)
			points.remove(1);

		OverlayItem marca = new OverlayItem("You are here", "Real Time Location", realLocation);
		points.add(marca);
		updatePoints();
	}

	private void updatePoints(){
		myOpenMapView.getOverlays().clear();
		ItemizedIconOverlay.OnItemGestureListener<OverlayItem> tap = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>(){
			@Override
			public boolean onItemLongPress(int arg0, OverlayItem arg1) {
				return false;
			}

			@Override
			public boolean onItemSingleTapUp(int index, OverlayItem item) {
				return true;
			}
		};

		ItemizedOverlayWithFocus<OverlayItem> capa = new ItemizedOverlayWithFocus<>(this, points, tap);
		capa.setFocusItemsOnTap(true);
		myOpenMapView.getOverlays().add(capa);
	}

	/**
	 * Método que crea un SSLSocketFactory personalizado.
	 */
	private void createSSLSocketFactory(){
		SSLFactory.getSSLSocketFactory();
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
							MapMain.this,
							getResources().getString(R.string.messagePostedIn)
									+ " " + providerName, Toast.LENGTH_LONG).show();
					if (socialDialog != null) {
						socialDialog.dismiss();
					}
				}
			} else {
				Toast.makeText(
						MapMain.this,
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
	 * Clase interna que implementa el oyente de cuando se hace click sobre un
	 * elemento de la lista que contiene los elementos del itinerario.
	 *
	 * @author Inigo Vázquez - Roberto Villuela
	 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
	 */
	private class ItineraryListListener implements AdapterView.OnItemClickListener {
		/**
		 * Adaptador de la lista.
		 */
		private ItineraryListAdapter adapter;
		/**
		 * Constructor de la clase.
		 * @param adapter adpatador de la lista
		 */
		private ItineraryListListener(ItineraryListAdapter adapter) {
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

	private class SocialListener implements View.OnClickListener {

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
	 * Clase privada que maneja la descarga de im�genes de Panoramio mediante una AsyncTask.
	 *
	 * @author Alejandro Cuevas �lvarez - aca0073@alu.ubu.es
	 * */
	private class DownloadPhotoTask extends AsyncTask<String, Void, Bitmap> {

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
