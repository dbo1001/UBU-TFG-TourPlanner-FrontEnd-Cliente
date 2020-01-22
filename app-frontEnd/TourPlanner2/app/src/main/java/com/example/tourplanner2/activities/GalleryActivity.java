package com.example.tourplanner2.activities;

import java.util.ArrayList;

import com.example.tourplanner2.adapters.GalleryAdapter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.tourplanner2.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Clase que se corresponde con la galer�a de fotos de los distintos puntos de inter�s.
 * 
 * @author Alejandro Cuevas �lvarez.
 * @author aca0073@alu.ubu.es
 * 
 * */
public class GalleryActivity extends androidx.fragment.app.Fragment {
	
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

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.gallery_view, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		imageUrls = getArguments().getStringArrayList("imageUrls");
		authors = getArguments().getStringArrayList("authors");
		authorsUrl = getArguments().getStringArrayList("authorsUrl");

		//Bundle bundle = getIntent().getExtras();
		//imageUrls = bundle.getStringArrayList("imageUrls");
		//authors = bundle.getStringArrayList("authors");
		//authorsUrl = bundle.getStringArrayList("authorsUrl");

		final ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(view.getContext()));

		@SuppressWarnings("deprecation")
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory()
				.cacheOnDisc()
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();

		GridView gridView = view.findViewById(R.id.gridview);
		gridView.setAdapter(new GalleryAdapter(imageUrls, options, view.getContext(), imageLoader));
		gridView.setOnItemClickListener((parent, view1, position, id) -> {
			FragmentManager fManager = getActivity().getSupportFragmentManager();
			Bundle args = new Bundle();
			args.putInt("position", position);
			args.putStringArrayList("imageUrls", imageUrls);
			args.putStringArrayList("authors", authors);
			args.putStringArrayList("authorUrls", authorsUrl);
			FullScreenViewActivity fScreen = new FullScreenViewActivity();
			fScreen.setArguments(args);
			//Intent i = new Intent(GalleryActivity.this, FullScreenViewActivity.class);
			//i.putExtra("position", position);
			//i.putExtra("imageUrls", imageUrls);
			//i.putExtra("authors", authors);
			//i.putExtra("authorUrls", authorsUrl);
			imageLoader.destroy();

			fManager.beginTransaction()
					.replace(R.id.fragment_container,fScreen)
					.commit();
		});

	}
/*
	/**
	 * Metodo que se invoca cuando la actividad es creada.
	 * 
	 * @param savedInstanceState
	 *            Bundle que contiene el estado de ejecuciones pasadas.
	 *//*
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_view);
		setContentView(R.layout.gallery_view);
		
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

	}*/
}
