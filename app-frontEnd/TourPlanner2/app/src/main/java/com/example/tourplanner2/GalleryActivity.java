package com.example.tourplanner2;

import java.util.ArrayList;

import tourplanner.adapters.GalleryAdapter;
import tourplanner.client.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.slidingmenu.lib.app.SlidingActivity;

/**
 * Clase que se corresponde con la galer�a de fotos de los distintos puntos de inter�s.
 * 
 * @author Alejandro Cuevas �lvarez.
 * @author aca0073@alu.ubu.es
 * 
 * */
public class GalleryActivity extends SlidingActivity{
	
	/**
	 * Lista que contiene las urls de las imagenes a mostrar en la galeria.
	 * */
	private ArrayList<String> imageUrls;
	/**
	 * Lista con los autores de las imagenes de Panoramio.
	 * */
	private ArrayList<String> authors;
	/**
	 * URLs de los autores de las imagenes de Panoramio.
	 * */
	private ArrayList<String> authorsUrl;
	
	/**
	 * Metodo que se invoca cuando la actividad es creada.
	 * 
	 * @param savedInstanceState
	 *            Bundle que contiene el estado de ejecuciones pasadas.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_view);
		setBehindContentView(R.layout.gallery_view);
		
		Bundle bundle = getIntent().getExtras();
		imageUrls = bundle.getStringArrayList("imageUrls");
		authors = bundle.getStringArrayList("authors");
		authorsUrl = bundle.getStringArrayList("authorsUrl");
	
		final ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));

		@SuppressWarnings("deprecation")
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.cacheInMemory()
		.cacheOnDisc()
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();

		GridView gridView = (GridView) findViewById(R.id.gridview);
		gridView.setAdapter(new GalleryAdapter(imageUrls, options, this, imageLoader));
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				 Intent i = new Intent(GalleryActivity.this, FullScreenViewActivity.class);
		         i.putExtra("position", position);
		         i.putExtra("imageUrls", imageUrls);
		         i.putExtra("authors", authors);
		         i.putExtra("authorUrls", authorsUrl);
		         imageLoader.destroy();
		         startActivity(i);
				
			}
		});

	}
}
