package com.example.tourplanner2.activities;

import java.util.Calendar;

import com.example.tourplanner2.R;
import com.example.tourplanner2.dialog.DialogTextView;
import com.example.tourplanner2.util.Misc;

import android.view.LayoutInflater;
import android.view.MenuItem;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Clase que se corresponde con la pantalla de explora la zona.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class PreferencesActivity extends androidx.fragment.app.Fragment {

	/**
	 * Identificador del dialog del tiempo de la ruta.
	 */
	private static final int TIME_DIALOG_ID = 999;
	/**
	 * TextView que muestra el tiempo de la ruta.
	 */
	private TextView txtTime;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.preferences, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Calendar now = Calendar.getInstance();
		double currentHour = now.get(Calendar.HOUR_OF_DAY);
		double currentMinutes = now.get(Calendar.MINUTE);
		txtTime = (TextView) view.findViewById(R.id.txtTime);
		txtTime.setText(Misc.pad((int) currentHour + 1) + ":"
				+ Misc.pad((int) currentMinutes));
		initilizeSeekBars();

		final String[] data = new String[] {
				getResources().getString(R.string.selectMap),
				getResources().getString(R.string.originEqTarget),
				getResources().getString(R.string.culturePoi),
				getResources().getString(R.string.leisurePoi),
				getResources().getString(R.string.gastronomyPoi),
				getResources().getString(R.string.naturePoi) };
		final DialogTextView dialog = new DialogTextView(view.getContext(), data,
				(TextView) view.findViewById(R.id.textViewTargetOptions));
		dialog.setTitle(getResources().getString(R.string.selectOption));

		((TextView) view.findViewById(R.id.textViewTargetOptions))
				.setOnClickListener(v -> dialog.show());

		LinearLayout linearLayoutTime = (LinearLayout) view.findViewById(R.id.timePickerLayout);

		linearLayoutTime.setOnClickListener(v -> onCreateDialog(TIME_DIALOG_ID).show());

		// Creamos el adaptador
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				view.getContext(), R.array.targetOptions,
				android.R.layout.simple_spinner_item);
		// Añadimos el layout para el menú
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Le indicamos al spinner el adaptador a usar

		Button btnEnviar = (Button) view.findViewById(R.id.btnEnviar);
		btnEnviar.setOnClickListener(arg0 -> {
			SeekBar sbCulture = (SeekBar) view.findViewById(R.id.seekBarCulture);
			SeekBar sbLeisure = (SeekBar) view.findViewById(R.id.seekBarLeisure);
			SeekBar sbNature = (SeekBar) view.findViewById(R.id.seekBarNature);
			SeekBar sbGastronomy = (SeekBar) view.findViewById(R.id.seekBarGastronomy);
			int cultureValue = sbCulture.getProgress();
			int leisureValue = sbLeisure.getProgress();
			int natureValue = sbNature.getProgress();
			int gastronomyValue = sbGastronomy.getProgress();
			if (!checkPreferences()) {
				Toast.makeText(
						view.getContext().getApplicationContext(),
						getResources().getString(
								R.string.mustSelectPreferences),
						Toast.LENGTH_LONG).show();
				return;
			}
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(view.getContext().getApplicationContext());
			Editor edit = pref.edit();
			edit.putInt("cultureProgress", cultureValue);
			edit.putInt("natureProgress", natureValue);
			edit.putInt("gastronomyProgress", gastronomyValue);
			edit.putInt("leisureProgress", leisureValue);
			edit.apply();
			double itineraryTime = getItineraryTime();
			Intent intent = new Intent(getActivity(),MapMain.class);
			intent.putExtra("time", itineraryTime);
			intent.putExtra("culture", cultureValue);
			intent.putExtra("leisure", leisureValue);
			intent.putExtra("nature", natureValue);
			intent.putExtra("gastronomy", gastronomyValue);
			TextView text = (TextView) view.findViewById(R.id.textViewTargetOptions);
			String option = text.getText().toString();
			if (option.equals(getResources().getString(R.string.selectMap))) {
				getActivity().setResult(MapMain.ONE_LOCATION_TGT, intent);
			} else if (option.equals(getResources().getString(
					R.string.originEqTarget))) {
				getActivity().setResult(MapMain.TGT_EQ_SRC, intent);
			} else if (option.equals(getResources().getString(
					R.string.culturePoi))) {
				getActivity().setResult(MapMain.TGT_POI, intent);
				intent.putExtra("tgt_poi", "culture");
			} else if (option.equals(getResources().getString(
					R.string.leisurePoi))) {
				getActivity().setResult(MapMain.TGT_POI, intent);
				intent.putExtra("tgt_poi", "leisure");
			} else if (option.equals(getResources().getString(
					R.string.gastronomyPoi))) {
				getActivity().setResult(MapMain.TGT_POI, intent);
				intent.putExtra("tgt_poi", "gastronomy");
			} else {
				getActivity().setResult(MapMain.TGT_POI, intent);
				intent.putExtra("tgt_poi", "nature");
			}
			startActivityForResult(intent,1);
		});
	}
	/*
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
		setContentView(R.layout.preferences);
		new SlidingMenuController(this);
		Calendar now = Calendar.getInstance();
		double currentHour = now.get(Calendar.HOUR_OF_DAY);
		double currentMinutes = now.get(Calendar.MINUTE);
		txtTime = (TextView) findViewById(R.id.txtTime);
		txtTime.setText(Misc.pad((int) currentHour + 1) + ":"
				+ Misc.pad((int) currentMinutes));
		initilizeSeekBars();

		final String[] data = new String[] {
				getResources().getString(R.string.selectMap),
				getResources().getString(R.string.originEqTarget),
				getResources().getString(R.string.culturePoi),
				getResources().getString(R.string.leisurePoi),
				getResources().getString(R.string.gastronomyPoi),
				getResources().getString(R.string.naturePoi) };
		final DialogTextView dialog = new DialogTextView(this, data,
				(TextView) findViewById(R.id.textViewTargetOptions));
		dialog.setTitle(getResources().getString(R.string.selectOption));

		((TextView) findViewById(R.id.textViewTargetOptions))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.show();
					}
				});

		LinearLayout linearLayoutTime = (LinearLayout) findViewById(R.id.timePickerLayout);

		linearLayoutTime.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				showDialog(TIME_DIALOG_ID);

			}

		});

		// Creamos el adaptador
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.targetOptions,
				android.R.layout.simple_spinner_item);
		// Añadimos el layout para el menú
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Le indicamos al spinner el adaptador a usar

		Button btnEnviar = (Button) findViewById(R.id.btnEnviar);
		btnEnviar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SeekBar sbCulture = (SeekBar) findViewById(R.id.seekBarCulture);
				SeekBar sbLeisure = (SeekBar) findViewById(R.id.seekBarLeisure);
				SeekBar sbNature = (SeekBar) findViewById(R.id.seekBarNature);
				SeekBar sbGastronomy = (SeekBar) findViewById(R.id.seekBarGastronomy);
				int cultureValue = sbCulture.getProgress();
				int leisureValue = sbLeisure.getProgress();
				int natureValue = sbNature.getProgress();
				int gastronomyValue = sbGastronomy.getProgress();
				if (!checkPreferences()) {
					Toast.makeText(
							getApplicationContext(),
							getResources().getString(
									R.string.mustSelectPreferences),
							Toast.LENGTH_LONG).show();
					return;
				}
				SharedPreferences pref = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				Editor edit = pref.edit();
				edit.putInt("cultureProgress", cultureValue);
				edit.putInt("natureProgress", natureValue);
				edit.putInt("gastronomyProgress", gastronomyValue);
				edit.putInt("leisureProgress", leisureValue);
				edit.commit();
				double itineraryTime = getItineraryTime();
				Intent intent = new Intent();
				intent.putExtra("time", itineraryTime);
				intent.putExtra("culture", cultureValue);
				intent.putExtra("leisure", leisureValue);
				intent.putExtra("nature", natureValue);
				intent.putExtra("gastronomy", gastronomyValue);
				TextView text = (TextView) findViewById(R.id.textViewTargetOptions);
				String option = text.getText().toString();
				if (option.equals(getResources().getString(R.string.selectMap))) {
					setResult(MapMain.ONE_LOCATION_TGT, intent);
				} else if (option.equals(getResources().getString(
						R.string.originEqTarget))) {
					setResult(MapMain.TGT_EQ_SRC, intent);
				} else if (option.equals(getResources().getString(
						R.string.culturePoi))) {
					setResult(MapMain.TGT_POI, intent);
					intent.putExtra("tgt_poi", "culture");
				} else if (option.equals(getResources().getString(
						R.string.leisurePoi))) {
					setResult(MapMain.TGT_POI, intent);
					intent.putExtra("tgt_poi", "leisure");
				} else if (option.equals(getResources().getString(
						R.string.gastronomyPoi))) {
					setResult(MapMain.TGT_POI, intent);
					intent.putExtra("tgt_poi", "gastronomy");
				} else {
					setResult(MapMain.TGT_POI, intent);
					intent.putExtra("tgt_poi", "nature");
				}
				finish();
			}

		});
	}*/

	/**
	 * Método que comprueba que las preferencias introducidas son validas.
	 * 
	 * @return true si las preferencias son validas, false en caso contrario
	 */
	private boolean checkPreferences() {
		SeekBar sbCulture = (SeekBar) getActivity().findViewById(R.id.seekBarCulture);
		SeekBar sbLeisure = (SeekBar) getActivity().findViewById(R.id.seekBarLeisure);
		SeekBar sbNature = (SeekBar) getActivity().findViewById(R.id.seekBarNature);
		SeekBar sbGastronomy = (SeekBar) getActivity().findViewById(R.id.seekBarGastronomy);
		int cultureValue = sbCulture.getProgress();
		int leisureValue = sbLeisure.getProgress();
		int natureValue = sbNature.getProgress();
		int gastronomyValue = sbGastronomy.getProgress();
		if (cultureValue == 0 && leisureValue == 0 && natureValue == 0
				&& gastronomyValue == 0) {
			return false;
		} else {
			TextView text = (TextView) getActivity().findViewById(R.id.textViewTargetOptions);
			String option = text.getText().toString();
			if (option.equals(getResources().getString(R.string.culturePoi))
					&& cultureValue == 0) {
				return false;
			} else if (option.equals(getResources().getString(
					R.string.leisurePoi))
					&& leisureValue == 0) {
				return false;
			} else if (option.equals(getResources().getString(
					R.string.gastronomyPoi))
					&& gastronomyValue == 0) {
				return false;
			} else {
				if (option.equals(getResources().getString(R.string.naturePoi))
						&& natureValue == 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Método que se llama cuando el dialog es creado.
	 */
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:
			// set time picker as current time
			Calendar now = Calendar.getInstance();
			return new TimePickerDialog(getActivity(), timePickerListener,
					now.get(Calendar.HOUR_OF_DAY) + 1,
					now.get(Calendar.MINUTE), true);

		}
		return null;
	}
	/**
	 * Método que inicializa las seekbars.
	 */
	private void initilizeSeekBars() {
		SeekBar sbCulture = (SeekBar) getActivity().findViewById(R.id.seekBarCulture);
		SeekBar sbLeisure = (SeekBar) getActivity().findViewById(R.id.seekBarLeisure);
		SeekBar sbNature = (SeekBar) getActivity().findViewById(R.id.seekBarNature);
		SeekBar sbGastronomy = (SeekBar) getActivity().findViewById(R.id.seekBarGastronomy);
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getActivity().getApplicationContext());
		sbCulture.setProgress(pref.getInt("cultureProgress", 50));
		sbLeisure.setProgress(pref.getInt("leisureProgress", 50));
		sbNature.setProgress(pref.getInt("natureProgress", 50));
		sbGastronomy.setProgress(pref.getInt("gastronomyProgress", 50));

	}
	/**
	 * Listener del timePIcker.
	 */
	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			int hour = selectedHour;
			int minute = selectedMinute;

			// set current time into textview
			txtTime.setText(new StringBuilder().append(Misc.pad(hour)).append(":")
					.append(Misc.pad(minute)));

		}
	};
	
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
	 * Método que calcula el tiempo que va a durar el itinerario.
	 * 
	 * @return tiempo que dura el itinerario
	 */
	private double getItineraryTime() {
		TextView txtTime = (TextView) getActivity().findViewById(R.id.txtTime);
		String hour = txtTime.getText().toString();

		double tpHour = Double.valueOf(hour.split(":")[0]);
		double tpMin = Double.valueOf(hour.split(":")[1]);
		Calendar now = Calendar.getInstance();
		Calendar then = Calendar.getInstance();
		then.set(Calendar.HOUR_OF_DAY, (int) tpHour);
		then.set(Calendar.MINUTE, (int) tpMin);
		if (then.getTimeInMillis() < now.getTimeInMillis()) {
			then.add(Calendar.DATE, 1);
		}
		double milis = then.getTimeInMillis() - now.getTimeInMillis();
		double secs = milis / 1000;
		double mins = secs / 60;
		double hours = mins / 60;
		return hours;
	}
	
}