package com.example.tourplanner2.activities;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tourplanner2.adapters.FavouritesListAdapter;

import com.example.tourplanner2.communication.IWebServiceTaskResult;
import com.example.tourplanner2.communication.WebServiceTask;
import com.example.tourplanner2.util.Misc;
import com.example.tourplanner2.util.FavouriteItem;
import com.example.tourplanner2.util.JSONParser;
import com.example.tourplanner2.util.PropertiesParser;
import com.example.tourplanner2.util.SlidingMenuController;

import com.actionbarsherlock.view.MenuItem;
import com.example.tourplanner2.R;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
/**
 * Clase que se corresponde con la pantalla de perfil.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class ProfileActivity extends SlidingActivity implements
		IWebServiceTaskResult {
	/**
	 * Array con los puntos favoritos del usuario.
	 */
	private FavouriteItem[] rows ;
	/**
	 * Url del servicio de consulta de perfil.
	 */
	private static String PROFILE_SERVICE_URL;
	/**
	 * Método que se invoca cuando la actividad es creada.
	 * 
	 * @param savedInstanceState
	 *            Bundle que contiene el estado de ejecuciones pasadas.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		setServiceDirections();
		new SlidingMenuController(this);
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		TextView txtUser = (TextView) findViewById(R.id.textViewUserName);
		txtUser.setText(pref.getString("username", ""));
		((Button) findViewById(R.id.buttonLogout))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						SharedPreferences pref = PreferenceManager
								.getDefaultSharedPreferences(getApplicationContext());
						Editor edit = pref.edit();
						edit.clear();
						edit.commit();
						Toast.makeText(
								getApplicationContext(),
								getResources()
										.getString(R.string.sessionClosed),
								Toast.LENGTH_LONG).show();
						finish();
					}
				});
		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this,
				getResources().getString(R.string.gettingRecommendedPois));

		wst.addNameValuePair("email", pref.getString("email", ""));
		wst.execute(new String[] { PROFILE_SERVICE_URL });
	}
	/**
	 * Método que establece las direcciones del servicio usado.
	 */
	private void setServiceDirections(){
		try {
		String address=PropertiesParser.getConnectionSettings(this);
		PROFILE_SERVICE_URL = "https://"+address+"/osm_server/get/profile";
		
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
		JSONObject jso;
		int visitedPois = 0;
		rows = JSONParser.getFavourites(response, this);
		try {
			jso = new JSONObject(response);
			if(jso.has("status") &&!Misc.checkErrorCode(jso.getString("status"), this)){
				return;
			}
			visitedPois= jso.getInt("visited_pois_count");
			((TextView) findViewById(R.id.textViewVisitedPois)).setText(String
					.valueOf(visitedPois));
			if (rows != null) {
				FavouritesListAdapter adaptadorItinerary = new FavouritesListAdapter(
						this, rows);
				ListView lstItinerary = (ListView) findViewById(R.id.listViewFavourites);
				lstItinerary.setAdapter(adaptadorItinerary);
			}
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
}
