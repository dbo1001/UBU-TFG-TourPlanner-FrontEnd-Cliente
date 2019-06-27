package com.example.tourplanner2.adapters;

import java.util.ArrayList;

import com.example.tourplanner2.R;
import com.example.tourplanner2.util.Misc;
import com.example.tourplanner2.util.RowItineraryList;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Clase implementa el adaptador del ListView que muestra diferentes POIs que se
 * le ofrecen al usuario para hacer una ruta "a tu gusto".
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class RecommendedPoiAdapter extends ArrayAdapter<RowItineraryList> 
implements Filterable{
	/**
	 * Contexto de la activity.
	 */
	private Activity context;
	/**
	 * Filas de la lista.
	 */
	private RowItineraryList[] rows;
	/**
	 * Copia de las filas de la lista originales (sin filtrado).
	 * */
	private RowItineraryList[] originalRows;
	/**
	 * Filtro para los POIs.
	 * */
	private POIFilter poiFilter;

	/**
	 * Costructor de la clase.
	 * @param context contexto de la aplicaci�n
	 * @param rows filas de la lista
	 */
	public RecommendedPoiAdapter(Activity context, RowItineraryList[] rows) {
		super(context, R.layout.recommended_pois_item, rows);
		this.rows = rows;
		this.context = context;
		this.originalRows = rows;
	}
	/**
	 * M�tod que devuelve la vista de un elemento de la lista
	 * @param position del elemento en la lista
	 * @param convertView vista a convertir
	 * @param parent padere de la vista
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {

		View item = convertView;
		ViewHolder holder = new ViewHolder();

		if (item == null) {

			LayoutInflater inflater = context.getLayoutInflater();
			item = inflater.inflate(R.layout.recommended_pois_item, null);
			holder = getNewViewHolder(position, item);
			item.setTag(holder);
		} else {
			holder = (ViewHolder) item.getTag();
		}
		
		if (position < rows.length){
			holder.textName.setText(rows[position].getTextName());
			if (rows[position].getTag().equals(" ")) {
				holder.textTag.setText(rows[position].getTag());
			} else {
				holder.textTag.setText(Misc.getResId(
						"string/" + rows[position].getTag(), context));
			}
			holder.image.setImageResource(rows[position].getImageResId());
			holder.imageTag.setImageResource((rows[position].getImageTagResId()));
			holder.rating.setRating((float) rows[position].getScore());
			holder.selected.setChecked(rows[position].isSelected());
			holder.position = position;	
			return item;
		}
		
		return null;
		
	}
	/**
	 * M�todo que devuelve un nuevo viewholder para un elemento en una posici�n
	 * @param position posici�n del elemento 
	 * @param item vista 
	 * @return nueva vista
	 */
	private ViewHolder getNewViewHolder(final int position, View item) {
		final ViewHolder holder;
		holder = new ViewHolder();
		holder.position = position;
		holder.textName = (TextView) item
				.findViewById(R.id.textViewItineraryName);
		holder.textName.setTextColor(Color.WHITE);
		holder.textTag = (TextView) item
				.findViewById(R.id.textViewItineraryDistance);
		holder.textName.setTextColor(Color.WHITE);
		holder.selected = (CheckBox) item
				.findViewById(R.id.checkBoxRecommendedPoi);
		holder.rating = (RatingBar) item.findViewById(R.id.ratingBar);
		holder.rating.setIsIndicator(true);
		holder.image = (ImageView) item
				.findViewById(R.id.imageViewItineraryList);
		holder.imageTag = (ImageView) item
				.findViewById(R.id.imageViewItineraryListTag);
		holder.selected.setChecked(false);
		holder.selected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewHolder holder = (ViewHolder) ((View) v.getParent()
						.getParent().getParent()).getTag();
				rows[holder.position].setSelected(!rows[holder.position]
						.isSelected());
			}
		});
		return holder;
	}

	@Override
	public Filter getFilter() {
		if (poiFilter == null)
			poiFilter = new POIFilter();

		return poiFilter;
	}

	public void resetData() {
		rows = originalRows;
	}


	private static class ViewHolder {
		ImageView image;
		ImageView imageTag;
		TextView textName;
		TextView textTag;
		RatingBar rating;
		CheckBox selected;
		int position;
	}

	private class POIFilter extends Filter{

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			if (constraint == null || constraint.toString().length() == 0) {
				// No filter implemented we return all the list
				results.values = rows;
				results.count = rows.length;
			} else {
				// We perform filtering operation
				ArrayList<RowItineraryList> poiList = new ArrayList<RowItineraryList>();

				for (int i = 0; i < rows.length; i++) {
					if (rows[i].getTextName() != null && 
							rows[i].getTextName().toLowerCase().contains(constraint.toString().toLowerCase())){
						poiList.add(rows[i]);
					}
				}

				RowItineraryList[] nPoiList = parseToArray(poiList);

				results.values = nPoiList;
				results.count = nPoiList.length;
			}

			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			// Now we have to inform the adapter about the new list filtered
			if (results.count == 0)
				notifyDataSetInvalidated();
			else {
				rows = (RowItineraryList[]) results.values;
				notifyDataSetChanged();
			}			
		}

		private RowItineraryList[] parseToArray(ArrayList<RowItineraryList> rows){
			RowItineraryList[] itineraryList = new RowItineraryList[rows.size()];

			for (int i = 0; i < rows.size(); i++){
				itineraryList[i] = rows.get(i);
			}

			return itineraryList;
		}

	}

}
