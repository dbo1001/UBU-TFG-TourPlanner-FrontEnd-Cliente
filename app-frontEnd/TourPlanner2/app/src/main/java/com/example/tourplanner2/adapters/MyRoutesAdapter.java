package com.example.tourplanner2.adapters;

import java.util.List;

import com.example.tourplanner2.R;
import com.example.tourplanner2.util.MyRoutesItem;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Clase implementa el adaptador del ArrayAdapter de la pantalla de mis rutas.
 * 
 * @author Alejandro Cuevas �lvarez. 
 * @author aca0073@alu.ubu.es
 * @author Jesús Manuel Calvo Ruiz de Temiño - jcr0069@alu.ubu.es
 */
public class MyRoutesAdapter extends ArrayAdapter<MyRoutesItem> {
	/**
	 * Contexto de la activity.
	 */
	private Context context;
	/**
	 * Filas de la lista.
	 */
	private List<MyRoutesItem> rows;
	/**
	 * Costructor de la clase.
	 * @param rows filas de la lista
	 */
	public MyRoutesAdapter(Context context2, List<MyRoutesItem> rows) {
		super(context2, R.layout.my_routes_list_item, rows);
		this.rows = rows;
		this.context = context2;
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

			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			item = inflater.inflate(R.layout.my_routes_list_item,
					null);
			holder = getNewViewHolder(position, item);
			item.setTag(holder);
		} else {
			holder = (ViewHolder) item.getTag();
		}
		holder.image.setImageResource(R.drawable.icon);
		holder.textName.setText(rows.get(position).getName());
		holder.rating.setRating((float) rows.get(position).getRating());
		holder.textCity.setText(rows.get(position).getCity());
		holder.date.setText(rows.get(position).getDate());
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
		holder.image = item.findViewById(R.id.imageViewMyRoutesList);
		holder.textName = item.findViewById(R.id.textViewMyRoutesName);
		holder.textName.setTextColor(Color.WHITE);
		holder.rating = item.findViewById(R.id.myRoutesRatingBar);
		holder.rating.setIsIndicator(true);
		holder.date = item.findViewById(R.id.tvDate);
		holder.textCity = item
				.findViewById(R.id.tvMyRoutesCity);
		return holder;
	}

	private static class ViewHolder {
		ImageView image;
		TextView textName;
		TextView textCity;
		RatingBar rating;
		TextView date;
	}

}
