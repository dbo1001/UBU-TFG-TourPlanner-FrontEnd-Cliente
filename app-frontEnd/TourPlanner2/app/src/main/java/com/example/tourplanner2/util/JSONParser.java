package com.example.tourplanner2.util;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.util.GeoPoint;

import com.example.tourplanner2.R;

import android.content.Context;

/**
 * Clase que parsea un JSON devuelto por el servidor. 
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class JSONParser {
	/**
	 * Cadena con el JSON.
	 */
	private String json;
	/**
	 * Lista de elementos de la ruta.
	 */
	private RowItineraryList[] rowsItinerary;
	/**
	 * Lista de elementos a poner sobre el mapa.
	 */
	private MyItem items;
	/**
	 * Constructor de la clase.
	 * @param json json de respuesta del servidor
	 * @param itemizedOverlay overlay
	 * @param context contexto de la activity
	 */
	@SuppressWarnings("static-access")
	public JSONParser(String json, MyItem itemizedOverlay,
	                  Context context) {
		JSONObject jso;
		this.json = json;
		items = itemizedOverlay;
		//items = new ArrayList<OverlayItem>();
		double lati5Int, lati5Int2;
		int latiE6, latiE62;
		JSONArray timeList = null;
		try {
			jso = new JSONObject(json);
			if (jso.has("cost_list")) {
				timeList = jso.getJSONArray("cost_list");
			}
			JSONArray listOfPois = jso.getJSONArray("poi_list");
			rowsItinerary = new RowItineraryList[listOfPois.length()];
			RowItineraryList row;
			String category;
			for (int i = 0; i < listOfPois.length(); i++) {
				row = new RowItineraryList();
				JSONObject poi = (JSONObject) listOfPois.get(i);
				row.setPoiId(poi.getLong("poi_id"));
				if(poi.has("time_to_stay")){
					row.setTime((float) poi.getDouble("time_to_stay")/60);
				}
				if (row.getPoiId() == -1L) {
					if (i == 0) {
						row.setTextName(context.getResources().getString(
								R.string.origin));
						row.setTag(" ");
						row.setImageResId(R.drawable.source_marker);
						row.setImageTagResId(R.drawable.source_marker);
					} else {
						row.setTextName(context.getResources().getString(
								R.string.target));
						row.setTag(" ");
						row.setImageResId(R.drawable.target_marker);
						row.setImageTagResId(R.drawable.target_marker);
					}
				} else {
					if (!poi.has("name")) {
						row.setTextName("___");
						if(poi.has("tag")){
							row.setTag(poi.getString("tag"));
						}else{
							row.setTag("");
							return;
						}
					} else {
						row.setTextName(poi.getString("name"));
						row.setTag(poi.getString("tag"));
					}if(poi.has("category")){
						category = poi.getString("category");
						String name;
						if (!poi.getBoolean("promoted")){
							name = "drawable/" + category.toLowerCase() + "_"
									+ row.getTag();
						} else {
							name = "drawable/" + category.toLowerCase() + "_"
									+ row.getTag() + "_p";	
						}
						row.setImageResId(Misc.getResId(
								"drawable/" + category.toLowerCase(), context));
						row.setImageTagResId(Misc.getResId(name, context));
					}
				}

				if (poi.has("coordinates")) {
					row.setCoordinates(poi.getString("coordinates"));
				}

				row.setScore(poi.getDouble("score") / 20);
				row.setPromoted(poi.getBoolean("promoted"));
				String coordinates = poi.getString("coordinates");
				if (itemizedOverlay != null) {
					lati5Int = Double.parseDouble(coordinates.substring(
							coordinates.indexOf("(") + 1,
							coordinates.indexOf(" ")));
					lati5Int2 = Double.parseDouble(coordinates.substring(
							coordinates.indexOf(" ") + 1,
							coordinates.indexOf(")")));
					;
					latiE6 = (int) (lati5Int * 1000000);
					latiE62 = (int) (lati5Int2 * 1000000);
					GeoPoint gPoint = new GeoPoint(latiE62, latiE6);
					OverlayItem item = new OverlayItem(row.getTextName(),String.valueOf(i),gPoint);
					//setPoint(gPoint);
					//item.setTitle(row.getTextName());
					//item.setSnippet(String.valueOf(i));
					//item.setMarker(itemizedOverlay.boundCenter(context
					//		.getResources().getDrawable(row.getImageTagResId())));
					items.getDisplayedItems().add(item);
				}
				if (timeList != null) {
					row.setTime((float) timeList.getDouble(i));
				}
				rowsItinerary[i] = row;

			}
			System.gc();

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	/**
	 * M�todo que devuele la lista del itinerario.
	 * @return array con los elementos del itinerario
	 */
	public RowItineraryList[] getItineraryList() {

		return rowsItinerary;
	}
	/**
	 * M�todo que devuelve los elementos del overlay.
	 * @return lista con los elementos del overlay
	 */
	public List getOverlayItems() {

		return items.getDisplayedItems();
	}
	/**
	 * M�todo que devuleve las coordenadas del camino de la ruta.
	 * @return coordenadas
	 */
	public String getWayCoordinates() {
		JSONObject jso;
		String hugeString = "";
		try {
			jso = new JSONObject(json);

			Object obj = jso.get("encodedCoordinates");
			if (obj != null) {
				if (obj instanceof JSONArray) {
					JSONArray coordinatesArray = jso
							.getJSONArray("encodedCoordinates");
					for (int i = 0; i < coordinatesArray.length(); i++) {
						hugeString += coordinatesArray.get(i);
					}
				} else {
					hugeString = jso.getString("encodedCoordinates");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return hugeString;
	}
	/**
	 * M�todo que devuelve una lista de elementos favoritos a partir de un json.
	 * @param json
	 * @param context de la aplicacion
	 * @return array de favoritos
	 */
	public static FavouriteItem[] getFavourites(String json, Context context) {
		JSONObject jso;
		FavouriteItem favourite;
		FavouriteItem[] favourites = null;
		try {
			jso = new JSONObject(json);
			JSONArray listOfPois = jso.getJSONArray("user_activity");
			favourites = new FavouriteItem[listOfPois.length()];
			for (int i = 0; i < listOfPois.length(); i++) {
				JSONObject poi = (JSONObject) listOfPois.get(i);
				favourite = new FavouriteItem();
				favourite.setScore(poi.getInt("score_submitted") / 20);
				if(poi.has("place_name")){
					favourite.setTextName(poi.getString("place_name"));
				}else{
					favourite.setTextName("__");
				}

				String name = "drawable/"
						+ poi.getString("category").toLowerCase() + "_"
						+ poi.getString("tag");
				favourite.setImageResId(Misc.getResId(name, context));
				favourites[i] = favourite;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return favourites;

	}
	/**
	 * M�todo que devuelve un array de opiniones partir de un json. 
	 * @param json
	 * @return array de opiniones
	 */
	public static OpinionItem[] getOpinions(String json) {
		JSONObject jso;
		OpinionItem opinion;
		OpinionItem[] opinions = null;
		try {
			jso = new JSONObject(json);
			JSONArray listOfOpinios = jso.getJSONArray("poiDetailsResponse");
			opinions = new OpinionItem[listOfOpinios.length()];
			for (int i = 0; i < listOfOpinios.length(); i++) {
				JSONObject opinionElement = (JSONObject) listOfOpinios.get(i);
				opinion = new OpinionItem();
				opinion.setName(opinionElement.getString("user_name"));
				opinion.setRating(opinionElement.getDouble("score_submitted") / 20);
				if (opinionElement.has("opinion")) {
					opinion.setOpinion(opinionElement.getString("opinion"));
				}else{
					opinion.setOpinion("");
				}
				opinions[i] = opinion;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return opinions;
	}
	/**
	 * M�todo que establece los costes de una ruta a partir de un json
	 * @param rows array con los elementos de la ruta
	 * @param json
	 */
	public static void setCost(RowItineraryList[] rows, String json) {
		JSONObject jso;

		try {
			jso = new JSONObject(json);
			JSONArray listOfCost = jso.getJSONArray("cost_list");
			float time=0;
			for (int i = 0; i < listOfCost.length(); i++) {
				if(i!=0){
					time = (Float.valueOf((String) listOfCost.get(i)))+rows[i-1].getTime();
				}
				rows[i].setTime(time);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
