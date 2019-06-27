package com.example.tourplanner2.dialog;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.tourplanner2.adapters.ItineraryListAdapter;
import com.example.tourplanner2.adapters.OpinionListAdapter;
import com.example.tourplanner2.R;
import com.example.tourplanner2.communication.IWebServiceTaskResult;
import com.example.tourplanner2.communication.WebServiceTask;
import com.example.tourplanner2.util.Misc;
import com.example.tourplanner2.util.JSONParser;
import com.example.tourplanner2.util.OpinionItem;
import com.example.tourplanner2.util.PropertiesParser;
import com.example.tourplanner2.util.RowItineraryList;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Dialogo que muestra al usuario la informaci�n relativa a un POI.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class DialogOpinion extends Dialog implements IWebServiceTaskResult {
	/**
	 * Url del servicio.
	 */
	private static String OPINION_SERVICE_URL;
	/**
	 * Dialogo.
	 */
	private DialogOpinion dialog;
	/**
	 * Contexto de la actividad.
	 */
	private Context context;
	/**
	 * Constructor de la clase.
	 * @param context contexto de la actividad
	 * @param poi punto de interes sobre el que se va mostrar informaci�n
	 * @param adaptador adaptador de la lista
	 */
	public DialogOpinion(final Context context, final RowItineraryList poi,
			final ItineraryListAdapter adaptador) {
		super(context);
		dialog = this;
		this.context = context;
		setServiceDirections();
		setContentView(R.layout.poi_info_dialog);
		setTitle(context.getString(R.string.detailedInfo));
		((ImageView) findViewById(R.id.imageViewItineraryList))
				.setImageDrawable(context.getResources().getDrawable(
						poi.getImageResId()));
		((ImageView) findViewById(R.id.imageViewItineraryListTag))
				.setImageDrawable(context.getResources().getDrawable(
						poi.getImageTagResId()));
		((TextView) findViewById(R.id.textViewItineraryName)).setText(poi
				.getTextName());
		((TextView) findViewById(R.id.textViewTag)).setText(poi.getTag());
		((TextView) findViewById(R.id.textViewTime)).setText(poi
				.getFormattedTime());
		((RatingBar) findViewById(R.id.ratingBar)).setRating(poi.getRating());
		((EditText) findViewById(R.id.editTextOpinion)).setText(poi
				.getOpinion());
		((TextView) findViewById(R.id.textViewSeeExperiences))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						WebServiceTask wst = new WebServiceTask(
								WebServiceTask.POST_TASK, dialog);

						wst.addNameValuePair("poi_id",
								String.valueOf(poi.getPoiId()));
						wst.execute(new String[] { OPINION_SERVICE_URL });
					}
				});
		OpinionListAdapter adapter = new OpinionListAdapter(context,
				new OpinionItem[0]);
		ListView list = (ListView) findViewById(R.id.listViewOpinions);
		list.setAdapter(adapter);
		((Button) findViewById(R.id.buttonAccept))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						poi.setOpinion(((TextView) findViewById(R.id.editTextOpinion))
								.getText().toString());
						poi.setRating(((RatingBar) findViewById(R.id.ratingBar))
								.getRating());
						adaptador.notifyDataSetChanged();
						dialog.dismiss();
					}

				});

	}
	/**
	 * M�todo que establece la direcci�n del servicio.
	 */
	private void setServiceDirections() {
		try {
			String address = PropertiesParser
					.getConnectionSettings(context);
			OPINION_SERVICE_URL = "https://" + address
					+ "/osm_server/get/poi/opinions";

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/**
	 * M�todo que procesa la respuesta del servidor.
	 * @param response respuesta del servidor
	 */
	@Override
	public void handleResponse(String response) {
		try {

			
			if (!response.equals("null")) {
				JSONObject jso = new JSONObject(response);
				if (jso.has("status") &&!Misc.checkErrorCode(jso.getString("status"),
						context)) {
					return;
				}
				OpinionItem[] opinions = JSONParser.getOpinions(response);
				OpinionListAdapter adapter = new OpinionListAdapter(
						context, opinions);
				((TextView) findViewById(R.id.textViewSeeExperiences))
						.setVisibility(View.GONE);
				ListView list = (ListView) findViewById(R.id.listViewOpinions);
				list.setAdapter(adapter);
				list.setVisibility(View.VISIBLE);
			} else {
				Toast.makeText(
						context,
						context.getResources().getString(
								R.string.noOpinions), Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
