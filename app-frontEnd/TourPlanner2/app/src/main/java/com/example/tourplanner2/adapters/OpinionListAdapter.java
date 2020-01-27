package com.example.tourplanner2.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import com.example.tourplanner2.R;
import com.example.tourplanner2.util.OpinionItem;

public class OpinionListAdapter extends ArrayAdapter<OpinionItem> {
	/**
	 * Contexto de la activity.
	 */
	private Context context;
	/**
	 * Filas de la lista.
	 */
	private OpinionItem[] rows;
	/**
	 * Costructor de la clase.
	 * @param rows filas de la lista
	 */
	public OpinionListAdapter(Context context2, OpinionItem[] rows) {
		super(context2, R.layout.opinion_element, rows);
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
			item = inflater.inflate(R.layout.opinion_element,
					null);
			holder = getNewViewHolder(position, item);
			item.setTag(holder);
		} else {
			holder = (ViewHolder) item.getTag();
		}
		holder.textName.setText(rows[position].getName());
		holder.rating.setRating((float) rows[position].getRating());
		holder.textOpinion.setText(rows[position].getOpinion());
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
		holder.textOpinion = item
				.findViewById(R.id.textViewOpinion);
		return holder;
	}

	private static class ViewHolder {
		TextView textName;
		TextView textOpinion;
		RatingBar rating;
	}

}
