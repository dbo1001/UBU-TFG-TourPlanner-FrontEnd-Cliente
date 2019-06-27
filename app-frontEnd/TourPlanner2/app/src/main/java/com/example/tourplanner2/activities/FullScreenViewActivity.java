package com.example.tourplanner2.activities;

import java.util.ArrayList;

import com.example.tourplanner2.adapters.FullScreenImageAdapter;
import com.example.tourplanner2.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Clase que implementa la vista a pantalla completa de las imagenes de la galeria.
 * 
 * @author Alejandro Cuevas �lvarez.
 * @author aca0073@alu.ubu.es
 * 
 * */
public class FullScreenViewActivity extends androidx.fragment.app.Fragment{

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.gallery_full_screen_view, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		/**
		 * ViewPager correspondiente a la actividad.
		 * */
		ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);

		//Bundle bundle = getIntent().getExtras();
		ArrayList<String> imageUrls = getArguments().getStringArrayList("imageUrls");
		int position = getArguments().getInt("position");
		ArrayList<String> authors = getArguments().getStringArrayList("authors");
		ArrayList<String> authorUrls = getArguments().getStringArrayList("authorUrls");

		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(view.getContext()));

		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300))
				.build();

		viewPager.setAdapter(new FullScreenImageAdapter(getActivity(),
				imageUrls, options, imageLoader, authors, authorUrls));

		viewPager.setCurrentItem(position);
	}
/*
	/**
	 * Metodo que se invoca cuando la actividad es creada.
	 * 
	 * @param savedInstanceState
	 *            Bundle que contiene el estado de ejecuciones pasadas.
	 */
/*
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_full_screen_view);

		/**
		 * ViewPager correspondiente a la actividad.
		 * */
/*
		ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

		Bundle bundle = getIntent().getExtras();
		ArrayList<String> imageUrls = bundle.getStringArrayList("imageUrls");
		int position = bundle.getInt("position");
		ArrayList<String> authors = bundle.getStringArrayList("authors");
		ArrayList<String> authorUrls = bundle.getStringArrayList("authorUrls");
		
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.displayer(new FadeInBitmapDisplayer(300))
		.build();

		viewPager.setAdapter(new FullScreenImageAdapter(FullScreenViewActivity.this,
				imageUrls, options, imageLoader, authors, authorUrls));

		viewPager.setCurrentItem(position);
	}*/
}
