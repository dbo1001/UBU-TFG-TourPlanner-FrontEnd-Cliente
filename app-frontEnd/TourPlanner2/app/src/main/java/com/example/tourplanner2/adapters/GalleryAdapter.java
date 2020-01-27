package com.example.tourplanner2.adapters;

import java.util.ArrayList;

import com.example.tourplanner2.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Clase que implementa el adaptador del GridView de la galeria de fotos.
 * 
 * @author Alejandro Cuevas �lvarez.
 * @author aca0073@alu.ubu.es 
 * 
 * */
public class GalleryAdapter extends BaseAdapter {
	/**
	 * ArrayList que contiene las urls de las imagenes a descargar.
	 * */
	private ArrayList<String> imageUrls;
	/**
	 * Opciones correspondientes a la visualizacion de las imagenes.
	 * */
	private DisplayImageOptions options;
	/**
	 * Contexto de la activity.
	 * */
	private Context context;
	/**
	 * Se utiliza para cargar las distintas imagenes y almacenarlas en cach�.
	 * */
	private ImageLoader imageLoader;

	/**
	 * Constructor de la clase.
	 * 
	 * @param imageUrls URLs de las im�genes a descargar.
	 * @param options Opciones correspondientes a como se van a visualizar las im�genes.
	 * @param context Contexto de la actividad.
	 * @param imageLoader Se utiliza para la visualizaci�n y almacenamiento de las imagenes.
	 * */
	public GalleryAdapter(ArrayList<String> imageUrls, DisplayImageOptions options,
			Context context, ImageLoader imageLoader){
		this.imageUrls = imageUrls;
		this.options = options;
		this.context = context;
		this.imageLoader = imageLoader;
	}

	@Override
	public int getCount() {
		return imageUrls.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * M�todo que devuelve la vista de un elemento de la lista
	 * @param position del elemento en la lista
	 * @param convertView vista a convertir
	 * @param parent padre de la vista
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder = new ViewHolder();
		final View gridView;

		if (convertView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater(); 
			gridView =  inflater.inflate(R.layout.item_grid_image, parent, false);
			viewHolder.image = gridView
					.findViewById(R.id.image_item_sub_category);
		} else {
			gridView = convertView;
		}
		
		imageLoader.displayImage(imageUrls.get(position), viewHolder.image, options);
			
		return gridView;
	}

	/**
	 * Clase interna que implementa un viewHolder para los item del gridLayout.
	 * 
	 * @author Alejandro Cuevas Alvarez
	 * @author aca0073@alu.ubu.es
	 */
	private static class ViewHolder {
		protected ImageView image;
	}
	
}
