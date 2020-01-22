package com.example.tourplanner2.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.example.tourplanner2.adapters.ExpandableAdapter;
import com.example.tourplanner2.dialog.DialogTextView;
import com.example.tourplanner2.util.Misc;
import com.example.tourplanner2.util.RowExpandableItem;

//import android.app.Activity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.view.MenuItem;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//import androidx.drawerlayout.widget.DrawerLayout;

import com.example.tourplanner2.R;




/**
 * Clase que se corresponde con la pantalla de ajustes o opciones avanzadas.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class AdvancedOptionsActivity extends androidx.fragment.app.Fragment {

	//private DrawerLayout drawer;

	//private Activity pActivity = getActivity();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.advanced_options, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		List<List<RowExpandableItem>> children = new ArrayList<List<RowExpandableItem>>();
		List<String> groups = new ArrayList<String>();
		List<String> tags = new ArrayList<String>();

		loadTags(R.raw.tags_t, groups, tags, children);
		ExpandableAdapter exAdapter = new ExpandableAdapter(getActivity(), groups, tags,
				children, view.getContext().getApplicationContext());
		ExpandableListView expCulture = view.findViewById(R.id.expandableListCulture);
		expCulture.setAdapter(exAdapter);

		final String[] data = new String[] {
				getResources().getString(R.string.fast),
				getResources().getString(R.string.exhaustive) };
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(view.getContext().getApplicationContext());
		TextView textView = (view.findViewById(R.id.textViewRoutingOptions));
		final DialogTextView dialog = new DialogTextView(getActivity(), data, textView);
		dialog.setTitle(getResources().getString(R.string.selectMode));
		if (pref.getString("route_mode", "fast").equals("fast")) {

			textView.setText(getResources().getString(R.string.fast));
		} else {
			textView.setText(getResources().getString(R.string.exhaustive));
		}

		textView.setOnClickListener(v -> dialog.show());
		((ListView) dialog.findViewById(R.id.list_view))
				.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
					SharedPreferences pref1 = PreferenceManager
							.getDefaultSharedPreferences(view.getContext().getApplicationContext());
					Editor edit = pref1.edit();
					if (data[arg2].equals(getResources().getString(
							R.string.fast))) {
						edit.putString("route_mode", "fast");
					} else {
						edit.putString("route_mode", "exhaustive");
					}
					edit.apply();
					((TextView) view.findViewById(R.id.textViewRoutingOptions))
							.setText(data[arg2]);
					dialog.dismiss();
				});

		final String[] dataTransport = new String[] {
				getResources().getString(R.string.pedestrian),
				getResources().getString(R.string.car) };

		textView = (view.findViewById(R.id.textViewTransportOptions));
		final DialogTextView dialogTransport = new DialogTextView(getActivity(),
				dataTransport, textView);
		dialogTransport.setTitle(getResources().getString(
				R.string.selectTransport));
		if (pref.getString("transport", "fo_").equals("fo_")) {
			textView.setText(getResources().getString(R.string.pedestrian));
		} else {
			textView.setText(getResources().getString(R.string.car));
		}
		textView.setOnClickListener(v -> dialogTransport.show());

		((ListView) dialogTransport.findViewById(R.id.list_view))
				.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
					SharedPreferences pref12 = PreferenceManager
							.getDefaultSharedPreferences(view.getContext().getApplicationContext());
					Editor edit = pref12.edit();
					if (dataTransport[arg2].equals(getResources()
							.getString(R.string.car))) {
						edit.putString("transport", "dr_");
					} else {
						edit.putString("transport", "fo_");
					}
					edit.apply();
					((TextView) view.findViewById(R.id.textViewTransportOptions))
							.setText(dataTransport[arg2]);
					dialogTransport.dismiss();
				});
	}

	/**
	 * Método que se invoca cuando la actividad es creada.
	 * 
	 * @param savedInstanceState
	 *            Bundle que contiene el estado de ejecuciones pasadas.
	 */
	/*
	public void onCreate(Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_a, container, false);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advanced_options);
		//new SlidingMenuController(this);

		List<List<RowExpandableItem>> children = new ArrayList<List<RowExpandableItem>>();
		List<String> groups = new ArrayList<String>();
		List<String> tags = new ArrayList<String>();
		loadTags(R.raw.tags_t, groups, tags, children);
		ExpandableAdapter exAdapter = new ExpandableAdapter(this, groups, tags,
				children, getActivity().getApplicationContext());
		ExpandableListView expCulture = (ExpandableListView) findViewById(R.id.expandableListCulture);
		expCulture.setAdapter(exAdapter);

		final String[] data = new String[] {
				getResources().getString(R.string.fast),
				getResources().getString(R.string.exhaustive) };
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		TextView textView = ((TextView) findViewById(R.id.textViewRoutingOptions));
		final DialogTextView dialog = new DialogTextView(this, data, textView);
		dialog.setTitle(getResources().getString(R.string.selectMode));
		if (pref.getString("route_mode", "fast").equals("fast")) {

			textView.setText(getResources().getString(R.string.fast));
		} else {
			textView.setText(getResources().getString(R.string.exhaustive));
		}

		textView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.show();

			}
		});
		((ListView) dialog.findViewById(R.id.list_view))
		.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				SharedPreferences pref = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				Editor edit = pref.edit();
				if (data[arg2].equals(getResources().getString(
						R.string.fast))) {
					edit.putString("route_mode", "fast");
				} else {
					edit.putString("route_mode", "exhaustive");
				}
				edit.commit();
				((TextView) findViewById(R.id.textViewRoutingOptions))
				.setText(data[arg2]);
				dialog.dismiss();
			}

		});

		final String[] dataTransport = new String[] {
				getResources().getString(R.string.pedestrian),
				getResources().getString(R.string.car) };

		textView = ((TextView) findViewById(R.id.textViewTransportOptions));
		final DialogTextView dialogTransport = new DialogTextView(this,
				dataTransport, textView);
		dialogTransport.setTitle(getResources().getString(
				R.string.selectTransport));
		if (pref.getString("transport", "fo_").equals("fo_")) {
			textView.setText(getResources().getString(R.string.pedestrian));
		} else {
			textView.setText(getResources().getString(R.string.car));
		}
		textView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogTransport.show();

			}
		});

		((ListView) dialogTransport.findViewById(R.id.list_view))
		.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				SharedPreferences pref = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				Editor edit = pref.edit();
				if (dataTransport[arg2].equals(getResources()
						.getString(R.string.car))) {
					edit.putString("transport", "dr_");
				} else {
					edit.putString("transport", "fo_");
				}
				edit.commit();
				((TextView) findViewById(R.id.textViewTransportOptions))
				.setText(dataTransport[arg2]);
				dialogTransport.dismiss();
			}

		});
	}
	*/

	/**
	 * Carga del fichero tags_t todos los tags, así como la categoria a la que
	 * pertenecen.
	 * 
	 * @param resourceId
	 *            id del recurso tags_t
	 * @param groups
	 *            lista que contendrá las diferentes categorias
	 * @param tags
	 *            lista que contendra los diferentes tags
	 * @param children
	 *            matriz de elementos preparados para pasarselo a un expandable
	 *            list view adapter
	 * 
	 */
	private void loadTags(int resourceId, List<String> groups,
			List<String> tags, List<List<RowExpandableItem>> children) {
		// The InputStream opens the resourceId and sends it to the buffer
		InputStream is = this.getResources().openRawResource(resourceId);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String readLine = null, group, tag, category;
		int index;
		try {
			// While the BufferedReader readLine is not null
			while ((readLine = br.readLine()) != null) {
				tag = readLine.substring(0, readLine.indexOf("\t"));
				group = getResources().getString(
						Misc.getResId(
								"string/"
										+ readLine.substring(0,
												readLine.indexOf("\t")), getActivity()));
				category = readLine.substring(readLine.indexOf("\t") + 1,
						readLine.length());
				RowExpandableItem row = new RowExpandableItem(getResources()
						.getString(Misc.getResId("string/" + category, getActivity()),
								getActivity()));
				row.setTag(category);
				SharedPreferences pref = PreferenceManager
						.getDefaultSharedPreferences(getActivity().getApplicationContext());
				row.setSelected(pref.getBoolean(category, true));
				row.setResId(Misc.getResId("drawable/" + tag.toLowerCase()
						+ "_" + category, getActivity()));
				if (!groups.contains(group)) {
					groups.add(group);
					tags.add(tag);
					ArrayList<RowExpandableItem> array = new ArrayList<RowExpandableItem>();
					array.add(row);
					children.add(array);
				} else {
					index = groups.indexOf(group);
					children.get(index).add(row);
				}

			}

			// Close the InputStream and BufferedReader
			is.close();
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			new ToggleButton(getActivity());
			return true;
		}
		return super.onOptionsItemSelected((android.view.MenuItem) item);
	}
}
