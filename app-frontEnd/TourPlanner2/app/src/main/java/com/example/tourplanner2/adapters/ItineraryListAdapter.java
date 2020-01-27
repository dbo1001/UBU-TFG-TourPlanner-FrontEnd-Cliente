package com.example.tourplanner2.adapters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

//import androidx.appcompat.app.AppCompatActivity;

import com.example.tourplanner2.R;
import com.example.tourplanner2.util.Misc;
import com.example.tourplanner2.util.RowItineraryList;
/**
 * Clase implementa el adaptador del ListView que muestra el itinerario.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class ItineraryListAdapter extends ArrayAdapter<RowItineraryList> {
	/**
	 * Contexto de la activity.
	 */
	private Activity context;
	/**
	 * Filas de la lista.
	 */
	private RowItineraryList[] rows;
	/**
	 * Tiempo de comienzo de la ruta.
	 */
	private Date startTime;
	/**
	 * Constructor de la clase.
	 * @param context contexto de la activity
	 * @param rows filas de la lista
	 * @param startTime comienzo de la ruta
	 */
	public ItineraryListAdapter(Activity context, RowItineraryList[] rows, Date startTime) {
		super(context, R.layout.list_element, rows);
		this.rows = rows;
		this.context = context;
		this.startTime=startTime;
	}
	/**
	 * M�todo que devuelve un nuevo viewholder para un elemento en una posici�n
	 * @param position posici�n del elemento 
	 * @return nueva vista
	 */
	@SuppressLint("SimpleDateFormat")
	public View getView(final int position, View convertView, ViewGroup parent) {

		View item = convertView;
		ViewHolder holder = new ViewHolder();

		if (item == null) {

			LayoutInflater inflater = context.getLayoutInflater();
			item = inflater.inflate(R.layout.list_itinerary_result_element,
					null);
			holder = getNewViewHolder(position, item);
			item.setTag(holder);
		} else {
			holder = (ViewHolder) item.getTag();
		}
		if(position==0 || position==rows.length-1){
			item.setClickable(false);
		}else{
			((LinearLayout) item
					.findViewById(R.id.linearItineraryResultElement))
					.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		}
		holder.textName.setText(rows[position].getTextName());

		double time = getPreviousHours(position);
		Calendar cal = Calendar.getInstance();
		cal.setTime(startTime);
		cal.add(Calendar.HOUR_OF_DAY, (int) time);
		cal.add(Calendar.MINUTE, (int) ((time % 1) * 60));
		String formatedTime = new SimpleDateFormat("HH:mm").format(cal
				.getTime());
		rows[position].setFormattedTime(formatedTime);
		holder.textDistance.setText(formatedTime);
		if(rows[position].getTag().equals(" ")){
			holder.textTag.setText(rows[position].getTag());
		}else{
			holder.textTag.setText(Misc.getResId("string/"+rows[position].getTag(),context));
		}
		holder.image.setImageResource(rows[position].getImageResId());
		holder.imageTag.setImageResource(rows[position].getImageTagResId());
		if(position==0 || position==rows.length-1){
			holder.rating.setVisibility(View.INVISIBLE);
		}else{
			holder.rating.setVisibility(View.VISIBLE);
			holder.rating.setRating(rows[position].getRating());
		}
		holder.position = position;
		return item;
	}
	/**
	 * M�todo que devuelve la cantidad de tiempo previo a un elemento.
	 * @param index de elemento
	 * @return tiempo total antes de ese elementos
	 */
	private double getPreviousHours(int index) {
		double time = 0.0;
		for (int i = index; i >= 0; i--) {
			time += rows[i].getTime();
		}
		return time;
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
		holder.position = position;
		holder.textName = item
				.findViewById(R.id.textViewItineraryName);
		holder.textName.setTextColor(Color.WHITE);
		holder.textDistance = item.findViewById(R.id.textViewTime);
		holder.textTag = item.findViewById(R.id.textViewTag);
		holder.textName.setTextColor(Color.WHITE);
		holder.rating = item.findViewById(R.id.ratingBar);
		holder.rating
				.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
					if (fromUser) {
						ViewHolder holder1 = (ViewHolder) ((View) ratingBar
								.getParent().getParent().getParent()).getTag();
						rows[holder1.position].setRating(rating);
					}
				});
		holder.rating.setIsIndicator(false);
		holder.image = item
				.findViewById(R.id.imageViewItineraryList);
		holder.imageTag = item
				.findViewById(R.id.imageViewItineraryListTag);
		return holder;
	}

	private static class ViewHolder {
		ImageView image;
		ImageView imageTag;
		TextView textName;
		TextView textDistance;
		TextView textTag;
		RatingBar rating;
		int position;
	}

}
