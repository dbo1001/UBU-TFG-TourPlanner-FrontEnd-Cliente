package com.example.tourplanner2.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tourplanner2.R;
import com.example.tourplanner2.util.RowListView;

/**
 * Clase implementa el adaptador del ListView que muestra las diferentes
 * opciones en el menu slide.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class OptionsAdapter extends ArrayAdapter<RowListView> {
	/**
	 * Contexto de la activity.
	 */
	private Activity context;
	/**
	 * Filas de la lista.
	 */
	private RowListView[] rows;
	/**
	 * Costructor de la clase.
	 * @param context contexto de la aplicaci�n
	 * @param rows filas de la lista
	 */
	public OptionsAdapter(Activity context, RowListView[] rows) {
		super(context, R.layout.list_element, rows);
		this.rows = rows;
		this.context = context;
	}
	/**
	 * M�tod que devuelve la vista de un elemento de la lista
	 * @param position del elemento en la lista
	 * @param convertView vista a convertir
	 * @param parent padere de la vista
	 */
	public View getView(int position, View convertView, ViewGroup parent) {

		View item = convertView;
		ViewHolder holder;

		if (item == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			item = inflater.inflate(R.layout.list_element, null);

			holder = new ViewHolder();
			holder.text = item.findViewById(R.id.lblText);
			holder.text.setTextColor(Color.WHITE);

			holder.image = item.findViewById(R.id.image);
			holder.image.setImageResource(rows[position].getImageResId());

			item.setTag(holder);
		} else {
			holder = (ViewHolder) item.getTag();
		}
		holder.text.setText(rows[position].getText());
		return (item);
	}

	private static class ViewHolder {
		ImageView image;
		TextView text;
	}

}
