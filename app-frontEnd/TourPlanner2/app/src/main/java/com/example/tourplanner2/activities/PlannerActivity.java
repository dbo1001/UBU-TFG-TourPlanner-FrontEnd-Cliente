package com.example.tourplanner2.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tourplanner2.communication.FragmentListener;
import com.example.tourplanner2.communication.IWebServiceTaskResult;
import com.example.tourplanner2.communication.WebServiceTask;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.tourplanner2.R;
import com.example.tourplanner2.dialog.DialogTextView;
import com.example.tourplanner2.util.Misc;
import com.example.tourplanner2.util.PropertiesParser;

import android.view.MenuItem;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 * Clase que se corresponde con la pantalla de planifica tu viaje.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class PlannerActivity extends androidx.fragment.app.Fragment implements
		IWebServiceTaskResult {

	private SeekBar sbCulture;
	private SeekBar sbLeisure;
	private SeekBar sbNature;
	private SeekBar sbGastronomy;
	

	/**
	 * Identificadores de los dialog de seleccionar tiempo.
	 */
	private static final int ORIGIN_TIME_DIALOG_ID = 999,
			TARGET_TIME_DIALOG_ID = 888;
	/**
	 * TextViews donde se muestran las horas de origen y destino.
	 */
	private TextView txtTimeOrigin, txtTimeTarget;
	/**
	 * Banderas para controlar diferentes opciones de ruta.
	 */
	private boolean originActive = false, hotels = false, exist = false;
	/**
	 * Coordenadas del hotel y de la ciudad.
	 */
	private String cityCoordinates, hotelCoordinates;
	/**
	 * Dirección de los distintos servicios usados.
	 */
	private static String CITIES_SERVICE_URL, CITY_EXIST_SERVICE_URL,
			HOTEL_EXIST_SERVICE_URL, HOTELS_SERVICE_URL;
	/**
	 * Listas con las sugerencias de ciudad y hotel.
	 */
	private ArrayList<String> suggestCities = new ArrayList<String>(),
			suggestHotels = new ArrayList<String>();
	/**
	 * Adaptadores para los autoCompleteTextView de las ciudades y hoteles.
	 */
	private ArrayAdapter<String> citiesAutoCompleteAdapter,
			hotelsAutoCompleteAdapter;
	/**
	 * Contexto de la aplicación.
	 */
	private IWebServiceTaskResult context;

	private FragmentListener mCallback;

	private static final String BUNDLE_KEY_CLICK_LISTENER = "BUNDLE_KEY_CLICK_LISTENER";

	public <T extends AppCompatActivity> void setFragmentListener(FragmentListener<T> listener){
		this.mCallback = listener;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.planner, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		setServiceDirections();
		seekBarInitialization();
		Calendar now = Calendar.getInstance();
		double currentHour = now.get(Calendar.HOUR_OF_DAY);
		double currentMinutes = now.get(Calendar.MINUTE);

		txtTimeOrigin = (TextView) (view.findViewById(R.id.originTime)
				.findViewById(R.id.txtTime));
		txtTimeOrigin.setText(Misc.pad((int) currentHour) + ":"
				+ Misc.pad((int) currentMinutes));
		txtTimeTarget = (TextView) (view.findViewById(R.id.targetTime)
				.findViewById(R.id.txtTime));
		txtTimeTarget.setText(Misc.pad((int) currentHour + 1) + ":"
				+ Misc.pad((int) currentMinutes));
		initilizeSeekBars();
		ImageButton btnChangeTime = (ImageButton) (view.findViewById(R.id.originTime)
				.findViewById(R.id.btnTime));

		btnChangeTime.setOnClickListener(v -> onCreateDialog(ORIGIN_TIME_DIALOG_ID).show());

		LinearLayout linearLayoutTime = (LinearLayout) (view.findViewById(R.id.originTime));

		linearLayoutTime.setOnClickListener(v -> onCreateDialog(ORIGIN_TIME_DIALOG_ID).show());

		linearLayoutTime = (LinearLayout) (view.findViewById(R.id.targetTime));

		linearLayoutTime.setOnClickListener(v -> {

			(view.findViewById(R.id.autoCompleteHotel))
					.clearFocus();
			getActivity().showDialog(TARGET_TIME_DIALOG_ID);

		});
		final DialogTextView dialog = new DialogTextView(getActivity(), getResources()
				.getStringArray(R.array.origenOptions),
				(TextView) view.findViewById(R.id.textViewSelectOrigin));
		dialog.setTitle(getResources().getString(R.string.selectOption));

		(view.findViewById(R.id.textViewSelectOrigin))
				.setOnClickListener(v -> dialog.show());
		final DialogTextView dialog2 = new DialogTextView(getActivity(), getResources()
				.getStringArray(R.array.targetOptions),
				(TextView) view.findViewById(R.id.textViewSelectTarget));
		dialog.setTitle(getResources().getString(R.string.selectOption));

		(view.findViewById(R.id.textViewSelectTarget))
				.setOnClickListener(v -> dialog2.show());

		AutoCompleteTextView autoCompView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteCity);

		autoCompView.addTextChangedListener(new TextChangeListener(true));
		autoCompView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteHotel);
		autoCompView.addTextChangedListener(new TextChangeListener(false));

		Button btnEnviar = (Button) view.findViewById(R.id.btnEnviar);
		btnEnviar.setOnClickListener(arg0 -> {
			hotels = false;
			exist = true;
			WebServiceTask webService = new WebServiceTask(
					WebServiceTask.POST_TASK, context);
			webService
					.addNameValuePair(
							"city_name",
							((AutoCompleteTextView) view.findViewById(R.id.autoCompleteCity))
									.getText().toString());
			webService.execute(CITY_EXIST_SERVICE_URL);

		});
	}

	/**
	 * Método que se invoca cuando la actividad es creada.
	 * 
	 *
	 *            Bundle que contiene el estado de ejecuciones pasadas.
	 */
	/*
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		//setContentView(R.layout.planner);
		setServiceDirections();
		//new SlidingMenuController(this);
		seekBarInitialization();
		Calendar now = Calendar.getInstance();
		double currentHour = now.get(Calendar.HOUR_OF_DAY);
		double currentMinutes = now.get(Calendar.MINUTE);
		txtTimeOrigin = (TextView) (findViewById(R.id.originTime)
				.findViewById(R.id.txtTime));
		txtTimeOrigin.setText(Misc.pad((int) currentHour) + ":"
				+ Misc.pad((int) currentMinutes));
		txtTimeTarget = (TextView) (findViewById(R.id.targetTime)
				.findViewById(R.id.txtTime));
		txtTimeTarget.setText(Misc.pad((int) currentHour + 1) + ":"
				+ Misc.pad((int) currentMinutes));
		initilizeSeekBars();
		ImageButton btnChangeTime = (ImageButton) (findViewById(R.id.originTime)
				.findViewById(R.id.btnTime));

		btnChangeTime.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				showDialog(ORIGIN_TIME_DIALOG_ID);

			}

		});
		
		LinearLayout linearLayoutTime = (LinearLayout) (findViewById(R.id.originTime));

		linearLayoutTime.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				showDialog(ORIGIN_TIME_DIALOG_ID);

			}

		});

		linearLayoutTime = (LinearLayout) (findViewById(R.id.targetTime));

		linearLayoutTime.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				((AutoCompleteTextView) findViewById(R.id.autoCompleteHotel))
						.clearFocus();
				showDialog(TARGET_TIME_DIALOG_ID);

			}

		});
		final DialogTextView dialog = new DialogTextView(this, getResources()
				.getStringArray(R.array.origenOptions),
				(TextView) findViewById(R.id.textViewSelectOrigin));
		dialog.setTitle(getResources().getString(R.string.selectOption));

		((TextView) findViewById(R.id.textViewSelectOrigin))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.show();

					}
				});
		final DialogTextView dialog2 = new DialogTextView(this, getResources()
				.getStringArray(R.array.targetOptions),
				(TextView) findViewById(R.id.textViewSelectTarget));
		dialog.setTitle(getResources().getString(R.string.selectOption));

		((TextView) findViewById(R.id.textViewSelectTarget))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog2.show();

					}
				});

		AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteCity);

		autoCompView.addTextChangedListener(new TextChangeListener(true));
		autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteHotel);
		autoCompView.addTextChangedListener(new TextChangeListener(false));

		Button btnEnviar = (Button) findViewById(R.id.btnEnviar);
		btnEnviar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				hotels = false;
				exist = true;
				WebServiceTask webService = new WebServiceTask(
						WebServiceTask.POST_TASK, context);
				webService
						.addNameValuePair(
								"city_name",
								((AutoCompleteTextView) findViewById(R.id.autoCompleteCity))
										.getText().toString());
				webService.execute(CITY_EXIST_SERVICE_URL);

			}

		});
	}
	*/

	private void seekBarInitialization() {
		sbCulture = (SeekBar) getActivity().findViewById(R.id.seekBarCulture);
		sbLeisure = (SeekBar) getActivity().findViewById(R.id.seekBarLeisure);
		sbNature = (SeekBar) getActivity().findViewById(R.id.seekBarNature);
		sbGastronomy = (SeekBar) getActivity().findViewById(R.id.seekBarGastronomy);
	}

	/**
	 * Método que inicializa los seekbars.
	 */
	private void initilizeSeekBars() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getActivity().getApplicationContext());
		sbCulture.setProgress(pref.getInt("cultureProgress", 50));
		sbLeisure.setProgress(pref.getInt("leisureProgress", 50));
		sbNature.setProgress(pref.getInt("natureProgress", 50));
		sbGastronomy.setProgress(pref.getInt("gastronomyProgress", 50));

	}

	/**
	 * Método que establece las direcciones de los servicios usados.
	 */
	private void setServiceDirections() {
		try {
			String address = PropertiesParser.getConnectionSettings(getActivity());
			CITIES_SERVICE_URL = "https://" + address + "/osm_server/get/cities";
			CITY_EXIST_SERVICE_URL = "https://" + address
					+ "/osm_server/get/cities/exists";
			HOTEL_EXIST_SERVICE_URL = "https://" + address
					+ "/osm_server/get/hotels/exists";
			HOTELS_SERVICE_URL = "https://" + address + "/osm_server/get/hotels";

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método que calcula el tiempo que va a durar el itinerario.
	 * 
	 * @return tiempo que dura el itinerario
	 */
	private double getItineraryTime() {
		String sourceHour = txtTimeOrigin.getText().toString();
		String targetHour = txtTimeTarget.getText().toString();

		double tpSourceHour = Double.valueOf(sourceHour.split(":")[0]);
		double tpSourceMin = Double.valueOf(sourceHour.split(":")[1]);
		double tpTargetHour = Double.valueOf(targetHour.split(":")[0]);
		double tpTargetMin = Double.valueOf(targetHour.split(":")[1]);
		Calendar now = Calendar.getInstance();
		Calendar then = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, (int) tpSourceHour);
		now.set(Calendar.MINUTE, (int) tpSourceMin);
		then.set(Calendar.HOUR_OF_DAY, (int) tpTargetHour);
		then.set(Calendar.MINUTE, (int) tpTargetMin);
		if (then.getTimeInMillis() < now.getTimeInMillis()) {
			then.add(Calendar.DATE, 1);
		}
		double milis = then.getTimeInMillis() - now.getTimeInMillis();
		double secs = milis / 1000;
		double mins = secs / 60;
		double hours = mins / 60;
		return hours;
	}

	/**
	 * Método que se llama cuando el dialog es creado.
	 */
	//@Override
	protected Dialog onCreateDialog(int id) {
		Calendar now = Calendar.getInstance();
		switch (id) {
		case ORIGIN_TIME_DIALOG_ID:
			originActive = true;
			// set time picker as current time
			return new TimePickerDialog(getActivity(), timePickerListener,
					now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE),
					true);
		case TARGET_TIME_DIALOG_ID:
			originActive = false;
			// set time picker as current time

			return new TimePickerDialog(getActivity(), timePickerListener,
					now.get(Calendar.HOUR_OF_DAY) + 1,
					now.get(Calendar.MINUTE), true);

		}
		return null;
	}

	/**
	 * Listener del timePIcker.
	 */
	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			int hour = selectedHour;
			int minute = selectedMinute;
			if (originActive) {
				// set current time into textview
				txtTimeOrigin.setText(new StringBuilder()
						.append(Misc.pad(hour)).append(":")
						.append(Misc.pad(minute)));
			} else {
				txtTimeTarget.setText(new StringBuilder()
						.append(Misc.pad(hour)).append(":")
						.append(Misc.pad(minute)));
			}

		}
	};

	/**
	 * Método que se llama cuando se pincha sobre un icono de la barra
	 * superior.
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			new ToggleButton(getActivity());
			return true;
		}
		return true;
	}

	/**
	 * Método que recibe y procesa la respuesta del servidor.
	 * 
	 * @param response
	 *            respuesta del servidor
	 */
	@Override
	public void handleResponse(String response) {
		JSONObject jso = null;
		try {
			if (!response.equals("null")) {
				jso = new JSONObject(response);
				if (jso.has("status")
						&& !Misc.checkErrorCode(jso.getString("status"), getActivity())) {
					return;
				}
				if (jso != null) {
					if (exist) {
						existResponse(jso);
					} else {
						doAutoComplete(jso);
					}
				}
			}
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
	}

	/**
	 * Método que realiza la función de autocompletar en función de la
	 * respuesta del servidor.
	 * 
	 * @param jso
	 * @throws JSONException
	 */
	private void doAutoComplete(JSONObject jso) throws JSONException {
		String name;
		ArrayList<String> suggestion;
		ArrayAdapter<String> adapter;
		AutoCompleteTextView aut;
		if (hotels) {
			suggestion = suggestHotels;
			name = "hotelList";
			adapter = hotelsAutoCompleteAdapter;
			aut = (AutoCompleteTextView) getActivity().findViewById(R.id.autoCompleteHotel);
		} else {
			suggestion = suggestCities;
			name = "citiesList";
			adapter = citiesAutoCompleteAdapter;
			aut = (AutoCompleteTextView) getActivity().findViewById(R.id.autoCompleteCity);
		}
		suggestion.clear();
		Object obj = jso.get(name);
		if (obj != null) {
			if (obj instanceof JSONArray) {
				JSONArray cities = jso.getJSONArray(name);
				for (int i = 0; i < cities.length(); i++) {
					if (!cities.getString(i).equals("")) {
						suggestion.add(cities.getString(i));
					}
				}
			} else {
				if (!jso.getString(name).equals("")) {
					suggestion.add(jso.getString(name));
				}
			}

			adapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
					R.layout.autocomplete_item, suggestion);
			aut.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * Método que comprueba la respuesta del servidor a las preguntas de si
	 * existe ciudad o hotel.
	 * 
	 * @param jso
	 *            respuesta del servidor
	 * @throws JSONException
	 */
	private void existResponse(JSONObject jso) throws JSONException {
		if (!hotels) {
			existCity(jso);
		} else {
			existHotel(jso);

		}
	}

	/**
	 * Método que comprueba la respuesta del servidor sobre si existe una
	 * ciudad.
	 * 
	 * @param jso
	 *            respuesta del servidor
	 * @throws JSONException
	 */
	private void existHotel(JSONObject jso) throws JSONException {
		if (jso.has("coordinates")) {
			hotelCoordinates = jso.getString("coordinates");
			finishActivity();
		} else {
			showToast(getResources().getString(R.string.incorrectHotel));
		}
	}

	/**
	 * Método que comprueba la respuesta del servidor sobre si existe un hotel.
	 * 
	 * @param jso
	 *            respuesta del servidor
	 * @throws JSONException
	 */
	private void existCity(JSONObject jso) throws JSONException {
		if (jso.has("coordinates")) {
			cityCoordinates = jso.getString("coordinates");
			if (((TextView) getActivity().findViewById(R.id.textViewSelectOrigin)).getText()
					.toString()
					.equals(getResources().getString(R.string.selectHotel))) {
				checkIfHotelExist();
			} else {
				finishActivity();
			}
		} else {
			showToast(getResources().getString(R.string.incorrectCity));
		}
	}

	/**
	 * Método que finaliza la actividad.
	 */
	private void finishActivity() {
		double itineraryTime = getItineraryTime();
		String lat, lon;
		Intent intent = new Intent();
		if (!checkPreferences()) {
			Toast.makeText(getActivity().getApplicationContext(),
					getResources().getString(R.string.mustSelectPreferences),
					Toast.LENGTH_LONG).show();
			return;
		}
		intent.putExtra("time", itineraryTime);
		intent.putExtra("culture", sbCulture.getProgress());
		intent.putExtra("leisure", sbLeisure.getProgress());
		intent.putExtra("nature", sbNature.getProgress());
		intent.putExtra("gastronomy", sbGastronomy.getProgress());
		String origin = ((TextView) getActivity().findViewById(R.id.textViewSelectOrigin))
				.getText().toString();
		String target = ((TextView) getActivity().findViewById(R.id.textViewSelectTarget))
				.getText().toString();
		if (origin.equals(getResources().getString(R.string.selectMap))
				&& target.equals(getResources().getString(R.string.selectMap))) {
			getActivity().setResult(MapMain.TWO_LOCATIONS, intent);
		} else if (origin.equals(getResources().getString(R.string.selectMap))
				&& target.equals(getResources().getString(
						R.string.originEqTarget))) {
			getActivity().setResult(MapMain.ONE_LOCATION_TGT_EQ_SRC, intent);
		} else if (origin.equals(getResources().getString(R.string.selectMap))
				&& !isTargetPoi().equals("")) {
			getActivity().setResult(MapMain.ONE_LOCATION_TGT_POI, intent);
			intent.putExtra("tgt_poi", isTargetPoi());
		} else if (origin
				.equals(getResources().getString(R.string.selectHotel))
				&& target.equals(getResources().getString(R.string.selectMap))) {
			getActivity().setResult(MapMain.ONE_LOCATION_TGT, intent);
			lon = hotelCoordinates.substring(hotelCoordinates.indexOf("(") + 1,
					hotelCoordinates.indexOf(" "));
			lat = hotelCoordinates.substring(hotelCoordinates.indexOf(" ") + 1,
					hotelCoordinates.indexOf(")"));
			intent.putExtra("srclat", lat);
			intent.putExtra("srclon", lon);
			intent.putExtra(
					"hotel_name",
					(((AutoCompleteTextView) getActivity().findViewById(R.id.autoCompleteHotel))
							.getText().toString()));
		} else if (origin
				.equals(getResources().getString(R.string.selectHotel))
				&& target.equals(getResources().getString(
						R.string.originEqTarget))) {
			getActivity().setResult(MapMain.TGT_EQ_SRC, intent);
			lon = hotelCoordinates.substring(hotelCoordinates.indexOf("(") + 1,
					hotelCoordinates.indexOf(" "));
			lat = hotelCoordinates.substring(hotelCoordinates.indexOf(" ") + 1,
					hotelCoordinates.indexOf(")"));
			intent.putExtra("srclat", lat);
			intent.putExtra("srclon", lon);
			intent.putExtra(
					"hotel_name",
					(((AutoCompleteTextView) getActivity().findViewById(R.id.autoCompleteHotel))
							.getText().toString()));
		} else {
			getActivity().setResult(MapMain.TGT_POI, intent);
			intent.putExtra("tgt_poi", isTargetPoi());
			lon = hotelCoordinates.substring(hotelCoordinates.indexOf("(") + 1,
					hotelCoordinates.indexOf(" "));
			lat = hotelCoordinates.substring(hotelCoordinates.indexOf(" ") + 1,
					hotelCoordinates.indexOf(")"));
			intent.putExtra("srclat", lat);
			intent.putExtra("srclon", lon);
			intent.putExtra(
					"hotel_name",
					(((AutoCompleteTextView) getActivity().findViewById(R.id.autoCompleteHotel))
							.getText().toString()));
		}
		intent.putExtra("city_coordinates", cityCoordinates);
		startActivityForResult(intent,1);
	}

	/**
	 * Métod que devuelve la categoria del POI de destino si se ha seleccionado
	 * etsa opción.
	 * 
	 * @return categoria del POI de destino.
	 */
	private String isTargetPoi() {
		String option = ((TextView) getActivity().findViewById(R.id.textViewSelectTarget))
				.getText().toString();
		if (option.equals(getResources().getString(R.string.culturePoi))) {
			return "culture";
		} else if (option.equals(getResources().getString(R.string.leisurePoi))) {
			return "leisure";
		} else if (option.equals(getResources().getString(
				R.string.gastronomyPoi))) {
			return "gastronomy";
		} else if (option.equals(getResources().getString(R.string.naturePoi))) {
			return "nature";
		}
		return "";
	}

	private boolean checkIfAnyPreferenceIsSelected() {
		int cultureValue = sbCulture.getProgress();
		int leisureValue = sbLeisure.getProgress();
		int natureValue = sbNature.getProgress();
		int gastronomyValue = sbGastronomy.getProgress();
		return (cultureValue == 0 && leisureValue == 0 && natureValue == 0 && gastronomyValue == 0);
	}

	/**
	 * Método que comprueba si se han seleccionado unas preferencias validas.
	 * 
	 * @return true, si las preferencias son correctas, false en caso contrario
	 */
	private boolean checkPreferences() {
		if (checkIfAnyPreferenceIsSelected()) {
			return false;
		} else {
			TextView text = (TextView) getActivity().findViewById(R.id.textViewTargetOptions);
			String option = text.getText().toString();
			if (option.equals(getResources().getString(R.string.culturePoi))
					&& sbCulture.getProgress() == 0) {
				return false;
			} else if (option.equals(getResources().getString(
					R.string.leisurePoi))
					&& sbLeisure.getProgress()== 0) {
				return false;
			} else if (option.equals(getResources().getString(
					R.string.gastronomyPoi))
					&& sbGastronomy.getProgress() == 0) {
				return false;
			} else {
				if (option.equals(getResources().getString(R.string.naturePoi))
						&& sbNature.getProgress() == 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Método que realiza una consulta al servidor sobre si existe un hotel.
	 */
	private void checkIfHotelExist() {
		if (((AutoCompleteTextView) getActivity().findViewById(R.id.autoCompleteHotel))
				.getText().toString().equals("")) {
			showToast(getResources().getString(R.string.incorrectHotel));
		} else {
			hotels = true;
			exist = true;
			WebServiceTask webService = new WebServiceTask(
					WebServiceTask.POST_TASK, this);
			webService
					.addNameValuePair(
							"hotel_name",
							((AutoCompleteTextView) getActivity().findViewById(R.id.autoCompleteHotel))
									.getText().toString());
			webService
					.addNameValuePair(
							"city_name",
							((AutoCompleteTextView) getActivity().findViewById(R.id.autoCompleteCity))
									.getText().toString());
			webService.execute(HOTEL_EXIST_SERVICE_URL);
		}
	}

	/**
	 * Método que muestra un Toast con un mensaje.
	 * 
	 * @param message
	 *            mensaje a mostrar en el Toast
	 */
	private void showToast(String message) {
		Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG)
				.show();
	}

	/**
	 * Clase privada que se corresponde con un listener sobre los
	 * autoCompleteTextView cuando el texto cambia.
	 * 
	 * @author Inigo Vázquez - Roberto Villuela
	 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
	 */
	private class TextChangeListener implements TextWatcher {
		/**
		 * Indica si se va a utilizar sobre el TextView de ciudades o hoteles.
		 */
		private boolean cities;

		/**
		 * Constructor de la clase
		 * 
		 * @param cities
		 *            Indica si se va a utilizar sobre el TextView de ciudades o
		 *            hoteles
		 */
		public TextChangeListener(boolean cities) {
			super();
			this.cities = cities;
		}

		/**
		 * Método llamado despues de que cambia el texto.
		 */
		@Override
		public void afterTextChanged(Editable s) {
			if (s.length() >= 3) {
				if (cities) {
					hotels = false;
					exist = false;
					WebServiceTask webService = new WebServiceTask(
							WebServiceTask.POST_TASK, context);
					webService.addNameValuePair("prefix", s.toString());
					webService.execute(CITIES_SERVICE_URL);
				} else {
					hotels = true;
					exist = false;
					WebServiceTask webService = new WebServiceTask(
							WebServiceTask.POST_TASK, context);
					webService.addNameValuePair("prefix", s.toString());
					webService
							.addNameValuePair(
									"city_name",
									((AutoCompleteTextView) getActivity().findViewById(R.id.autoCompleteCity))
											.getText().toString());
					webService.execute(HOTELS_SERVICE_URL);
					changeOriginOption();
				}
			}

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

	}

	/**
	 * Método que cambia la opción seleccionada a hoteles, cuando se escribe
	 * texto en el textView de los hoteles.
	 */
	private void changeOriginOption() {
		((TextView) getActivity().findViewById(R.id.textViewSelectOrigin))
				.setText(getResources().getString(R.string.selectHotel));

	}

	/**
	 * Builds a {@link PlannerActivity} and put its communication lambda wit the activity
	 * in the arguments so that they survive rotation.
	 */
	static class Builder {
		private FragmentListener pListener;

		/**
		 * This method must define a type generic so that it triggers target type inference.
		 * @param pListener the listener of clicks on an article.
		 * @param <T> the type of the activity that will hold the method reference listener.
		 * @return the builder itself for method chaining.
		 */
		<T extends AppCompatActivity> Builder setOnClickListener(FragmentListener<T> pListener) {
			this.pListener = pListener;
			return this;
		}

		PlannerActivity build() {
			PlannerActivity plannerF = new PlannerActivity();
			plannerF.setArguments(createArgs());
			return plannerF;
		}

		private Bundle createArgs() {
			Bundle bundle = new Bundle();
			if (pListener != null) {
				//store the listener in the arguments bundle
				//it is a state less lambda, guaranteed to be serializable
				bundle.putSerializable(BUNDLE_KEY_CLICK_LISTENER, pListener);
			}
			return bundle;
		}
	}

}
