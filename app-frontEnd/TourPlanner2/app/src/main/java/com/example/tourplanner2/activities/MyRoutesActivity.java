package com.example.tourplanner2.activities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tourplanner2.adapters.MyRoutesAdapter;
import com.example.tourplanner2.R;
import com.example.tourplanner2.communication.DownloadMapsTask;
import com.example.tourplanner2.communication.IWebServiceTaskResult;
import com.example.tourplanner2.communication.WebServiceTask;
import com.example.tourplanner2.dialog.DialogTextView;
import com.example.tourplanner2.util.Misc;
import com.example.tourplanner2.util.MyRoutesItem;
import com.example.tourplanner2.util.PropertiesParser;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;
import android.widget.ToggleButton;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Clase que se corresponde con la pantalla de mis rutas.
 * 
 * @author Alejandro Cuevas �lvarez. 
 * @author aca0073@alu.ubu.es
 */
@SuppressLint("SimpleDateFormat")
public class MyRoutesActivity extends androidx.fragment.app.Fragment
implements IWebServiceTaskResult{
	/**
	 * Nombre de la ruta.
	 * */
	private String routeName = null;
	/**
	 * Apadtador personalizado para las rutas guardadas.
	 * */
	private MyRoutesAdapter adaptador = null;
	/**
	 * Adaptador para los mapas descargados.
	 * */
	private ArrayAdapter<String> adapterMaps = null;
	/**
	 * Listado de rutas guardadas.
	 * */
	private List<MyRoutesItem> rows = new ArrayList<MyRoutesItem>();
	/**
	 * Listado de mapas descargados.
	 * */
	private ArrayList<String> maps = new ArrayList<String>();
	/**
	 * Boolean que indica si est� o no registrado el usuario en la aplicaci�n
	 * */
	private boolean registered;
	/**
	 * Boolean que indica si se han guardado rutas en el dispositivo y el servidor.
	 * */
	private boolean ok;
	/**
	 * Urls de los diferentes servicios utilizados.
	 * */
	private static String SAVE_ROUTE_SERVICE, DELETE_ROUTE_SERVICE, GET_ROUTE_SERVICE,
	ALL_ROUTES_SERVICE, GET_CITY_SERVICE, GET_MAP_SERVICE;
	/**
	 * Fecha en la que se guarda una ruta.
	 * */
	private SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm");

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.my_tracks, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		setServiceDirections();

		final SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(view.getContext().getApplicationContext());
		registered = pref.getBoolean("registered", false);

		// Opci�n "Guardar ruta".
		final EditText etRouteName = (EditText) view.findViewById(R.id.editTextRouteName);
		TextView tvSaveMode = (TextView) view.findViewById(R.id.textViewSaveOptions);

		final String[] saveMode = new String[] {
				getResources().getString(R.string.mobile),
				getResources().getString(R.string.server),
				getResources().getString(R.string.all)};
		final DialogTextView dialogSaveMode = new DialogTextView(getActivity(),
				saveMode, tvSaveMode);
		dialogSaveMode.setTitle(getResources().getString(
				R.string.selectMode));

		if (pref.getString("save_mode", "mobile").equals("mobile")) {
			tvSaveMode.setText(getResources().getString(R.string.mobile));
			loadListRoutesMobile(rows);
		} else if (pref.getString("save_mode", "mobile").equals("server")) {
			tvSaveMode.setText(getResources().getString(R.string.server));
			loadListRoutesServer(pref.getString("email", ""));
		} else {
			tvSaveMode.setText(getResources().getString(R.string.all));
			loadListRoutesMobile(rows);
		}

		tvSaveMode.setOnClickListener(v -> dialogSaveMode.show());

		((ListView) dialogSaveMode.findViewById(R.id.list_view))
				.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
					SharedPreferences pref1 = PreferenceManager
							.getDefaultSharedPreferences(view.getContext().getApplicationContext());
					Editor edit = pref1.edit();
					if (saveMode[arg2].equals(getResources().getString(
							R.string.mobile))) {
						edit.putString("save_mode", "mobile");
						loadListRoutesMobile(rows);
					} else if (saveMode[arg2].equals(getResources().getString(
							R.string.server)))  {
						edit.putString("save_mode", "server");
						loadListRoutesServer(pref1.getString("email", ""));
					} else {
						edit.putString("save_mode", "all");
						if (ok){
							loadListRoutesMobile(rows);
						}
					}
					adaptador.notifyDataSetChanged();
					edit.apply();
					((TextView) view.findViewById(R.id.textViewSaveOptions))
							.setText(saveMode[arg2]);
					dialogSaveMode.dismiss();
				});

		Button saveButton = (Button) view.findViewById(R.id.btnSave);
		saveButton.setOnClickListener(v -> {
			// Obtenemos las coordenadas del Intent pasado por la clase MapMain.
			Bundle extras = getActivity().getIntent().getExtras();
			String email = pref.getString("email", "");
			String coordinates = extras.getString("coordinates");
			routeName = etRouteName.getText().toString();

			if (coordinates != null){
				if (!routeName.equals("")){
					// Comprobamos donde se desea guardar la ruta.
					// Guardar en el m�vil.
					if (pref.getString("save_mode", "mobile").equals("mobile")){
						saveRouteMobile();
						// Guardar en el servidor.
					} else if (pref.getString("save_mode", "mobile").equals("server")){
						if (registered){
							saveRouteServer(extras, coordinates, email, routeName);
						} else {
							Toast.makeText(view.getContext().getApplicationContext(), R.string.register_route_save,
									Toast.LENGTH_SHORT).show();
						}
						// Guardar en m�vil y servidor.
					} else {
						if (registered){
							saveRouteMobile();
							saveRouteServer(extras, coordinates, email, routeName);
							ok = true;
						} else {
							Toast.makeText(view.getContext().getApplicationContext(), R.string.register_route_save,
									Toast.LENGTH_SHORT).show();
						}
					}


				} else {
					Toast.makeText(view.getContext().getApplicationContext(), R.string.route_empty_name,
							Toast.LENGTH_SHORT).show();
				}

			} else {
				Toast.makeText(view.getContext().getApplicationContext(), R.string.route_notfound,
						Toast.LENGTH_SHORT).show();
			}

		});

		// Opci�n "Cargar Ruta".
		ListView allRoutes = (ListView) view.findViewById(R.id.listViewAllRoutes);
		adaptador = new MyRoutesAdapter(getActivity(), rows);
		allRoutes.setAdapter(adaptador);

		// Listener para borrar ruta guardada.
		allRoutes.setOnItemLongClickListener((arg0, arg1, arg2, arg3) -> {
			buildAlertMessageDeleteRoute(arg2);
			return false;
		});

		// Listener para cargar ruta guardada.
		allRoutes.setOnItemClickListener((arg0, arg1, arg2, arg3) -> showRoute(arg2));

		// Opci�n "Mapas descargados"
		ListView allMaps = (ListView) view.findViewById(R.id.listViewMaps);
		adapterMaps = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1,
				maps);
		allMaps.setAdapter(adapterMaps);
		loadListMaps();

		// Habilitamos el listener de visualizar mapas descargados cuando no haya conexi�n.
		if (!isNetworkAvailable()){
			allMaps.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
				SharedPreferences pref12 = PreferenceManager
						.getDefaultSharedPreferences(view.getContext().getApplicationContext());
				Editor edit = pref12.edit();
				edit.putString("recently_map", maps.get(arg2));
				edit.apply();
				showMap(arg2);
			});

		}

		allMaps.setOnItemLongClickListener((arg0, arg1, arg2, arg3) -> {
			buildAlertMessageDeleteMap(arg2);
			return false;
		});
	}

	/**
	 * Método que se invoca cuando la actividad es creada.
	 * 
	 * @param savedInstanceState
	 *            Bundle que contiene el estado de ejecuciones pasadas.
	 */
	/*
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_tracks);
		setServiceDirections();
		new SlidingMenuController(this);
		final SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		registered = pref.getBoolean("registered", false);

		// Opci�n "Guardar ruta".
		final EditText etRouteName = (EditText) findViewById(R.id.editTextRouteName);
		TextView tvSaveMode = (TextView) findViewById(R.id.textViewSaveOptions);

		final String[] saveMode = new String[] {
				getResources().getString(R.string.mobile),
				getResources().getString(R.string.server),
				getResources().getString(R.string.all)};
		final DialogTextView dialogSaveMode = new DialogTextView(this,
				saveMode, tvSaveMode);
		dialogSaveMode.setTitle(getResources().getString(
				R.string.selectMode));

		if (pref.getString("save_mode", "mobile").equals("mobile")) {
			tvSaveMode.setText(getResources().getString(R.string.mobile));
			loadListRoutesMobile(rows);
		} else if (pref.getString("save_mode", "mobile").equals("server")) {
			tvSaveMode.setText(getResources().getString(R.string.server));
			loadListRoutesServer(pref.getString("email", ""));
		} else {
			tvSaveMode.setText(getResources().getString(R.string.all));
			loadListRoutesMobile(rows);
		}

		tvSaveMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogSaveMode.show();

			}
		});

		((ListView) dialogSaveMode.findViewById(R.id.list_view))
		.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				SharedPreferences pref = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				Editor edit = pref.edit();
				if (saveMode[arg2].equals(getResources().getString(
						R.string.mobile))) {
					edit.putString("save_mode", "mobile");
					loadListRoutesMobile(rows);
				} else if (saveMode[arg2].equals(getResources().getString(
						R.string.server)))  {
					edit.putString("save_mode", "server");
					loadListRoutesServer(pref.getString("email", ""));
				} else {
					edit.putString("save_mode", "all");
					if (ok){
						loadListRoutesMobile(rows);
					}
				}
				adaptador.notifyDataSetChanged();
				edit.commit();
				((TextView) findViewById(R.id.textViewSaveOptions))
				.setText(saveMode[arg2]);
				dialogSaveMode.dismiss();
			}

		});

		Button saveButton = (Button) findViewById(R.id.btnSave);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Obtenemos las coordenadas del Intent pasado por la clase MapMain.
				Bundle extras = getIntent().getExtras();
				String email = pref.getString("email", "");
				String coordinates = extras.getString("coordinates");
				routeName = etRouteName.getText().toString();

				if (coordinates != null){
					if (!routeName.equals("")){
						// Comprobamos donde se desea guardar la ruta.
						// Guardar en el m�vil.
						if (pref.getString("save_mode", "mobile").equals("mobile")){
							saveRouteMobile();
							// Guardar en el servidor.	
						} else if (pref.getString("save_mode", "mobile").equals("server")){
							if (registered){
								saveRouteServer(extras, coordinates, email, routeName);
							} else {
								Toast.makeText(getApplicationContext(), R.string.register_route_save,
										Toast.LENGTH_SHORT).show();
							}
							// Guardar en m�vil y servidor.	
						} else {
							if (registered){
								saveRouteMobile();
								saveRouteServer(extras, coordinates, email, routeName);
								ok = true;
							} else {
								Toast.makeText(getApplicationContext(), R.string.register_route_save,
										Toast.LENGTH_SHORT).show();
							}
						}


					} else {
						Toast.makeText(getApplicationContext(), R.string.route_empty_name,
								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(getApplicationContext(), R.string.route_notfound,
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		// Opci�n "Cargar Ruta".
		ListView allRoutes = (ListView) findViewById(R.id.listViewAllRoutes);
		adaptador = new MyRoutesAdapter(this, rows);
		allRoutes.setAdapter(adaptador);

		// Listener para borrar ruta guardada.
		allRoutes.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				buildAlertMessageDeleteRoute(arg2);
				return false;
			}
		});

		// Listener para cargar ruta guardada.
		allRoutes.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				showRoute(arg2);
			}
		});

		// Opci�n "Mapas descargados"
		ListView allMaps = (ListView) findViewById(R.id.listViewMaps);
		adapterMaps = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 
				maps);
		allMaps.setAdapter(adapterMaps);
		loadListMaps();

		// Habilitamos el listener de visualizar mapas descargados cuando no haya conexi�n.
		if (!isNetworkAvailable()){
			allMaps.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					SharedPreferences pref = PreferenceManager
							.getDefaultSharedPreferences(getApplicationContext());
					Editor edit = pref.edit();
					edit.putString("recently_map", maps.get(arg2));
					edit.commit();
					showMap(arg2);
				}
			});

		}

		allMaps.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				buildAlertMessageDeleteMap(arg2);
				return false;
			}
		});

	}
	*/

	/**
	 * Método que establece las direcciones del servicio usado.
	 */
	private void setServiceDirections(){
		try {

			String address = PropertiesParser.getConnectionSettings(getActivity());
			SAVE_ROUTE_SERVICE = "https://" + address + "/osm_server/get/route/save";
			DELETE_ROUTE_SERVICE = "https://" + address + "/osm_server/get/route/delete";
			ALL_ROUTES_SERVICE = "https://" + address + "/osm_server/get/route/all";
			GET_ROUTE_SERVICE = "https://" + address + "/osm_server/get/route/";

			GET_CITY_SERVICE = "https://" + address + "/osm_server/get/map/exists";
			GET_MAP_SERVICE = "https://" + address + "/osm_server/get/map/"; 

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * M�todo que carga las rutas guardadas en el m�vil.
	 *
	 * @param rows
	 *          Listado de rutas guardadas a mostrar.
	 * */
	private void loadListRoutesMobile(List<MyRoutesItem> rows){
		File filePath = new File(Environment.getExternalStorageDirectory() + "/tourplanner/routes");
		String line = "";
		rows.clear();
		if (filePath.exists()){
			File[] loadRoutes = filePath.listFiles();

			if (loadRoutes != null){
				for (int i = 0; i < loadRoutes.length; i++){
					StringBuilder routeCoordinates = new StringBuilder();
					InputStream is;
					BufferedReader br;
					try {
						is = new FileInputStream(loadRoutes[i].getAbsolutePath());

						br = new BufferedReader(new InputStreamReader(is));
						while ((line = br.readLine()) != null) {
							routeCoordinates.append(line);
						}

						is.close();
						br.close();

						JSONObject jso = new JSONObject(routeCoordinates.toString());
						addRouteItem(jso);

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	/**
	 * M�todo que carga las rutas guardadas en el servidor.
	 *
	 * @param email
	 *          Email del usuario registrado.
	 * */
	private void loadListRoutesServer(String email){
		rows.clear();
		if (isNetworkAvailable()){
			if (registered){
				WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this,
						getResources().getString(R.string.load_routes_server));
				wst.addNameValuePair("email", email);

				wst.execute(new String[] { ALL_ROUTES_SERVICE });
			} else {
				Toast.makeText(getActivity().getApplicationContext(), R.string.register_route_show,
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getActivity().getApplicationContext(), R.string.networkNotEnabledLoadRoute,
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * M�todo que carga los mapas descargados en el m�vil en una lista.
	 *
	 * */
	private void loadListMaps(){
		File filePath = new File(Environment.getExternalStorageDirectory() + "/tourplanner/maps");

		if (filePath.exists()){
			File[] loadMaps = filePath.listFiles();
			maps.clear();
			if (loadMaps != null){
				for (int i = 0; i < loadMaps.length; i++){
					maps.add(loadMaps[i].getName().
							substring(0, loadMaps[i].getName().indexOf(".")));
				}

				adapterMaps.notifyDataSetChanged();
			}
		}
	}

	/**
	 * M�todo que guarda una ruta en el m�vil.
	 * */
	private void saveRouteMobile(){
		getCity(getActivity().getIntent().getExtras());
	}

	/**
	 * M�todo que guarda una ruta en el servidor.
	 *
	 * @param extras
	 *          Bundle que contiene la ubicaci�n (latitud y longitud) del usuario.
	 * @param coordinates
	 *          Coordenadas de la ruta.
	 * @param email
	 *          Email del usuario registrado.
	 * @param routeName
	 *          Nombre de la ruta.
	 * */
	private void saveRouteServer(Bundle extras, String coordinates, String email, String routeName){
		if (isNetworkAvailable()){
			WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this,
					getResources().getString(R.string.save_route_server));

			double score = calculateRating(coordinates);
			String rating = String.valueOf(score);

			wst.addNameValuePair("coordinates", coordinates);
			wst.addNameValuePair("email", email);
			wst.addNameValuePair("route_name", routeName);
			wst.addNameValuePair("latitude", extras.getString("latitude"));
			wst.addNameValuePair("longitude", extras.getString("longitude"));
			wst.addNameValuePair("rating", rating);
			wst.addNameValuePair("date", format.format(Calendar.getInstance().getTime()));

			wst.execute(new String[] { SAVE_ROUTE_SERVICE });

		} else {
			Toast.makeText(getActivity().getApplicationContext(), R.string.networkNotEnabledSaveRoute,
					Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * M�todo que crea un mensaje tras eliminar una ruta.
	 * 
	 * @param index
	 *          �ndice de la ruta eliminada.
	 * */
	private void buildAlertMessageDeleteRoute(final int index){
		final SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getActivity().getApplicationContext());
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(getResources().getString(R.string.route_delete_dialog))
		.setCancelable(false)
		.setPositiveButton((getResources().getString(R.string.yes)),
				new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog,
					final int id) {
				// Eliminamos la ruta en d�nde est� alojada.
				if (pref.getString("save_mode", "mobile").equals("mobile")){
					deleteRouteMobile(index);
				} else if (pref.getString("save_mode", "mobile").equals("server")){
					deleteRouteServer(index);
				} else {
					deleteRouteMobile(index);
					deleteRouteServer(index);
				}

			}
		})
		.setNegativeButton((getResources().getString(R.string.no)),
				(dialog, id) -> dialog.cancel());
		final AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * M�todo que elimina una ruta del m�vil.
	 * 
	 * @param index
	 *          �ndice de la ruta a eliminar.
	 * */
	private void deleteRouteMobile(int index){
		File route = new File(Environment.getExternalStorageDirectory() 
				+ "/tourplanner/routes/" + rows.get(index).getName() + ".json");

		if(route.delete()){
			Toast.makeText(getActivity().getApplicationContext(), R.string.route_delete,
					Toast.LENGTH_SHORT).show();
		}

		// Eliminamos la ruta de la lista de "Cargar ruta" y actualizamos la lista.
		rows.remove(index);
		adaptador.notifyDataSetChanged();


	}

	/**
	 * M�todo que elimina una ruta del servidor.
	 * 
	 * @param index
	 *          �ndice de la ruta a eliminar.
	 * */
	private void deleteRouteServer(int index){
		if (isNetworkAvailable()){
			WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this,
					getResources().getString(R.string.delete_route_server));

			wst.addNameValuePair("route_name", rows.get(index).getName());

			// Eliminamos la ruta de la lista de "Cargar ruta" y actualizamos la lista.
			rows.remove(index);
			adaptador.notifyDataSetChanged();


			wst.execute(new String[] { DELETE_ROUTE_SERVICE });

		} else {
			Toast.makeText(getActivity().getApplicationContext(), R.string.networkNotEnabledDeleteRoute,
					Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * M�todo que elimina un mapa descargado.
	 * 
	 * @param index
	 *          �ndice del mapa a eliminar.
	 * */
	private void deleteMap(int index){
		File map = new File(Environment.getExternalStorageDirectory() 
				+ "/tourplanner/maps/" + maps.get(index) + ".map");

		if(map.delete()){
			Toast.makeText(getActivity().getApplicationContext(), R.string.map_delete,
					Toast.LENGTH_SHORT).show();
		}

		maps.remove(index);
		adapterMaps.notifyDataSetChanged();

	}

	/**
	 * M�todo que muestra una ruta guardada.
	 * 
	 * @param index
	 *          �ndice de la ruta a mostrar.
	 * */
	private void showRoute(int index){
		final SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getActivity().getApplicationContext());
		StringBuilder coordinates = new StringBuilder();

		// Obtenemos las coordenadas de la ruta seleccionada desde el m�vil.
		if (pref.getString("save_mode", "mobile").equals("mobile") 
				|| pref.getString("save_mode", "mobile").equals("all")){
			String line = null;

			InputStream input;
			File loadRoute = new File(Environment.getExternalStorageDirectory() 
					+ "/tourplanner/routes/" + rows.get(index).getName() + ".json");

			try {
				input = new FileInputStream(loadRoute.getAbsolutePath());
				BufferedReader br = new BufferedReader(new InputStreamReader(input));

				while ((line = br.readLine()) != null){
					coordinates.append(line);
				}

				input.close();
				br.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Pasamos el resultado a la actividad principal.
			Intent intent = new Intent();
			intent.putExtra("load_coordinates", coordinates.toString());
			getActivity().setResult(MapMain.SHOW_SAVE_ROUTE, intent);
			startActivityForResult(intent,1);

			// Obtenemos las coordenadas de la ruta seleccionada desde el servidor.	
		} else {
			if (isNetworkAvailable()){
				WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this,
						getResources().getString(R.string.get_route_server));

				wst.addNameValuePair("route_name", rows.get(index).getName());
				wst.addNameValuePair("email", pref.getString("email", ""));

				wst.execute(new String[] { GET_ROUTE_SERVICE });
			} else {
				Toast.makeText(getActivity().getApplicationContext(), R.string.networkNotEnabledShowRoute,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	/**
	 * M�todo que muestra un mapa descargado.
	 * 
	 * @param index
	 *          �ndice del mapa a mostrar.
	 * */
	private void showMap(int index){
		// Pasamos el resultado a la actividad principal.
		Intent intent = new Intent();
		intent.putExtra("map_name", maps.get(index));
		getActivity().setResult(MapMain.SHOW_MAP, intent);
		startActivityForResult(intent,1);

	}

	/**
	 * M�todo que obtiene el nombre de una ciudad a partir de unas coordenadas.
	 * 
	 * @param extras
	 *          Bundle que contiene la ubicaci�n (latitud y longitud) del usuario.
	 * */
	private void getCity(Bundle extras){
		if (isNetworkAvailable()){
			WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this);
			wst.addNameValuePair("latitude", extras.getString("latitude"));
			wst.addNameValuePair("longitude", extras.getString("longitude"));


			wst.execute(new String[] { GET_CITY_SERVICE });

		}
	}



	/**
	 * M�todo que muestra un aviso sobre la descarga de un mapa.
	 * 
	 * @param mapName
	 *          Nombre del mapa a descargar.
	 * */
	private void buildAlertMessageDownloadMap(final String mapName){
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		String strInfoDownloadFormat = getResources().getString(R.string.info_download);
		builder.setMessage(String.format(strInfoDownloadFormat, mapName))
		.setCancelable(false)
		.setPositiveButton((getResources().getString(R.string.yes)),
				(dialog, id) -> downloadMap(mapName))
		.setNegativeButton((getResources().getString(R.string.no)),
				(dialog, id) -> dialog.cancel());
		final AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * M�todo que crea un mensaje tras eliminar un mapa.
	 * 
	 * @param index
	 *          �ndice del mapa eliminado.
	 * */
	private void buildAlertMessageDeleteMap(final int index){
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(getResources().getString(R.string.delete_map))
		.setCancelable(false)
		.setPositiveButton((getResources().getString(R.string.yes)),
				(dialog, id) -> deleteMap(index))
		.setNegativeButton((getResources().getString(R.string.no)),
				(dialog, id) -> dialog.cancel());
		final AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * M�todo que inicia la descarga de un mapa.
	 * 
	 * @param mapName
	 *          Nombre del mapa a descargar.
	 * */
	private void downloadMap(String mapName){
		if (isNetworkAvailable()){
			DownloadMapsTask dmt = new DownloadMapsTask(DownloadMapsTask.POST_TASK, this, mapName);
			dmt.addNameValuePair("map_name", mapName);

			dmt.execute(new String[] { GET_MAP_SERVICE });

		} else {
			Toast.makeText(getActivity().getApplicationContext(), R.string.networkNotEnabled,
					Toast.LENGTH_SHORT).show();
		}


	}

	/**
	 * Método que recibe y procesa la respuesta del servidor.
	 * 
	 * @param response
	 *            respuesta del servidor
	 */
	@SuppressLint("SimpleDateFormat")
	@Override
	public void handleResponse(String response) {
		JSONObject jso = null;
		if (!response.equals("null")){
			try {
				jso = new JSONObject(response);
				if (jso.has("status")){
					if (jso.getString("status").equals("OK_SAVE")){
						JSONObject routeJSON = jso.getJSONObject("route");
						addRouteItem(routeJSON);
						adaptador.notifyDataSetChanged();
						Toast.makeText(getActivity().getApplicationContext(), R.string.route_success,
								Toast.LENGTH_SHORT).show();
					} else if (jso.getString("status").equals("OK_DELETE")){
						Toast.makeText(getActivity().getApplicationContext(), R.string.route_delete,
								Toast.LENGTH_SHORT).show();
					} else if (!Misc.checkErrorCode(
							jso.getString("status"), getActivity())){
						return;
					}
				}

				// Comprobamos si est� en la descarga de mapas.
				if (jso.has("map")){
					maps.add(jso.getString("map"));
					adapterMaps.notifyDataSetChanged();
				}

				// Mostramos las rutas del servidor.
				if (jso.has("routesList")){
					Object obj = jso.get("routesList");
					if (obj != null && obj instanceof JSONArray){
						JSONArray routesList = jso.getJSONArray("routesList");
						for (int i = 0; i < routesList.length(); i++) {
							JSONObject routeJSON = routesList.getJSONObject(i);
							addRouteItem(routeJSON);
						}
						adaptador.notifyDataSetChanged();
					} else {
						JSONObject routeJSON = jso.getJSONObject("routesList");
						addRouteItem(routeJSON);
						adaptador.notifyDataSetChanged();	
					}

				}

				// Obtenemos las coordeandas de la ruta seleccionada.
				if (jso.has("coordinates")){
					String coordinates = jso.getString("coordinates");

					// Pasamos el resultado a la actividad principal.
					Intent intent = new Intent();
					intent.putExtra("load_coordinates", coordinates);
					getActivity().setResult(MapMain.SHOW_SAVE_ROUTE, intent);
					startActivityForResult(intent,1);
				}


				if (jso.has("city_name")){
					String coordinates = getActivity().getIntent().getExtras().getString("coordinates");
					String city = jso.getString("city_name");
					double rating = calculateRating(coordinates);

					// A�adimos los par�metros a almacenar para mostrar en el ListView de rutas.
					try {
						JSONObject savedJson = new JSONObject(coordinates);
						savedJson.put("name", routeName);
						savedJson.put("city", city);
						savedJson.put("rating", rating);
						savedJson.put("date", format.format(Calendar.getInstance().getTime()));

						File fileCoordinates = new File(Environment.getExternalStorageDirectory() + "/tourplanner/routes/");

						if (!fileCoordinates.exists())
							fileCoordinates.mkdirs();

						// Guardamos las coordenadas de la ruta en un fichero json.
						FileWriter fileWritter = new FileWriter(fileCoordinates.getAbsolutePath() + "/" + routeName + ".json", false);
						BufferedWriter bw = new BufferedWriter(fileWritter);
						bw.write(savedJson.toString());
						bw.flush();
						bw.close();

						// A�adimos la ruta guardada a la lista de "Cargar ruta" y actualizamos la lista.
						addRouteItem(savedJson);
						adaptador.notifyDataSetChanged();

						Toast.makeText(getActivity().getApplicationContext(), R.string.route_success,
								Toast.LENGTH_SHORT).show();


					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e1) {
						e1.printStackTrace();
					}


					File mapFile = new File(Environment.getExternalStorageDirectory() + 
							"/tourplanner/maps/" + city + ".map");

					// Si no existe el mapa lo descargamos.
					if (!mapFile.exists()){
						buildAlertMessageDownloadMap(city);
					}

					SharedPreferences pref = PreferenceManager
							.getDefaultSharedPreferences(getActivity().getApplicationContext());
					Editor edit = pref.edit();
					edit.putString("recently_map", city);
					edit.commit();

				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * M�todo que a�ade una ruta a la lista de rutas guardadas.
	 * 
	 * @param jso
	 *          Objeto JSON que contiene los par�metros de la ruta a guardar.
	 * */
	private void addRouteItem(JSONObject jso){
		MyRoutesItem routeItem = new MyRoutesItem();
		try {
			routeItem.setName(jso.getString("name"));
			if (jso.getString("city") != null){
				routeItem.setCity(jso.getString("city"));
			} else {
				routeItem.setCity("");
			}
			routeItem.setRating(jso.getDouble("rating"));
			routeItem.setDate(jso.getString("date"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		rows.add(routeItem);
	}

	/**
	 * M�todo que calcula el rating de los POIs de una ruta.
	 * 
	 * @param coordinates
	 *          Coordenadas de la ruta.
	 * */
	private double calculateRating(String coordinates){
		double rating = 0.0f, score = 0.0f;
		try {
			JSONObject jso = new JSONObject(coordinates);
			JSONArray poiList = jso.getJSONArray("poi_list");
			for (int i = 0; i < poiList.length(); i++){
				score = score + poiList.getJSONObject(i).getDouble("score");
			}

			rating = (score / poiList.length()) / 20;

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return rating;
	}

	/**
	 * Método que se llama cuando se pincha sobre un icono de la barra superior.
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			new ToggleButton(getActivity());
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Método que comprueba si la conexión de red esta disponible.
	 * 
	 * @return true si la conexión a internet está disponible
	 */
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}


}