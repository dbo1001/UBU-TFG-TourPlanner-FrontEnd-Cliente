package com.example.tourplanner2.activities;

import java.io.IOException;
import java.util.Objects;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import android.view.MenuItem;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tourplanner2.R;

/**
 * Clase que se corresponde con la pantalla de perfil.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class ProfileActivity extends androidx.fragment.app.Fragment implements
		IWebServiceTaskResult {
	/**
	 * Url del servicio de consulta de perfil.
	 */
	private static String PROFILE_SERVICE_URL;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.profile, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		setServiceDirections();
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(view.getContext().getApplicationContext());
		TextView txtUser = view.findViewById(R.id.textViewUserName);
		txtUser.setText(pref.getString("username", ""));
		view.findViewById(R.id.buttonLogout)
				.setOnClickListener(v -> {
					SharedPreferences pref1 = PreferenceManager
							.getDefaultSharedPreferences(view.getContext().getApplicationContext());
					Editor edit = pref1.edit();
					edit.clear();
					edit.apply();
					Toast.makeText(
							view.getContext().getApplicationContext(),
							getResources()
									.getString(R.string.sessionClosed),
							Toast.LENGTH_LONG).show();
					Intent myIntent = new Intent(view.getContext(),MapMain.class);
					startActivityForResult(myIntent, 1);
				});
		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this,
				getResources().getString(R.string.gettingRecommendedPois));

		wst.addNameValuePair("email", pref.getString("email", ""));
		wst.execute(PROFILE_SERVICE_URL);
	}

	/**
	 * Método que establece las direcciones del servicio usado.
	 */
	private void setServiceDirections(){
		try {
		String address=PropertiesParser.getConnectionSettings(Objects.requireNonNull(getActivity()));
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
		/**
		 * Array con los puntos favoritos del usuario.
		 */
		FavouriteItem[] rows = JSONParser.getFavourites(response, getActivity());
		try {
			jso = new JSONObject(response);
			if(jso.has("status") &&!Misc.checkErrorCode(jso.getString("status"), getActivity())){
				return;
			}
			visitedPois= jso.getInt("visited_pois_count");
			((TextView) Objects.requireNonNull(getActivity()).findViewById(R.id.textViewVisitedPois)).setText(String
					.valueOf(visitedPois));
			if (rows != null) {
				FavouritesListAdapter adaptadorItinerary = new FavouritesListAdapter(
						getActivity(), rows);
				ListView lstItinerary = getActivity().findViewById(R.id.listViewFavourites);
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
		if (item.getItemId() == android.R.id.home) {
			new ToggleButton(getActivity());
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
