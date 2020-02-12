package com.example.tourplanner2.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.example.tourplanner2.R;
import com.example.tourplanner2.util.FavouriteItem;
/**
 * Clase implementa el adaptador del ListView de la lista de favoritos.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 * @author Jesús Manuel Calvo Ruiz de Temiño - jcr0069@alu.ubu.es
 */
public class FavouritesListAdapter extends ArrayAdapter<FavouriteItem> {
	/**
	 * Contexto de la actividad.
	 */
	private Activity context;
	/**
	 * Filas de la lista.
	 */
	private FavouriteItem[] rows;
	/**
	 * Costructor de la clase.
	 * @param context contexto de la aplicaci�n
	 * @param rows filas de la lista
	 */
	public FavouritesListAdapter(Activity context, FavouriteItem[] rows) {
		super(context, R.layout.favourite_item, rows);
		this.rows = rows;
		this.context = context;
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
			item = inflater.inflate(R.layout.favourite_item,
					null);
			holder = getNewViewHolder(position, item);
			item.setTag(holder);
		} else {
			holder = (ViewHolder) item.getTag();
		}
		holder.textName.setText(rows[position].getTextName());

		holder.image.setImageResource(rows[position].getImageResId());
		holder.rating.setRating((float) rows[position].getScore());
		return item;
	}
	/**
	 * M�todo que devuelve un nuevo viewholder para un elemento en una posici�n
	 * @param position posici�n del elemento 
	 * @param item vista 
	 * @return nueva vista
	 */
	private ViewHolder getNewViewHolder(final int position, View item) {
		ViewHolder holder;
		holder = new ViewHolder();
		holder.textName = item.findViewById(R.id.textViewName);
		holder.textName.setTextColor(Color.WHITE);
		holder.rating = item.findViewById(R.id.ratingBar);
		holder.rating.setIsIndicator(true);
		holder.image = item
				.findViewById(R.id.imageViewFovourite);
		return holder;
	}

	private static class ViewHolder {
		ImageView image;
		TextView textName;
		RatingBar rating;
	}

}
