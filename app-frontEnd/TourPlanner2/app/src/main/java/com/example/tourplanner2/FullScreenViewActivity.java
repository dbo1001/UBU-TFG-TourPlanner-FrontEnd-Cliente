package com.example.tourplanner2;

import java.util.ArrayList;

import tourplanner.adapters.FullScreenImageAdapter;
import tourplanner.client.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

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
public class FullScreenViewActivity extends Activity{
	
	/**
	 * ViewPager correspondiente a la actividad.
	 * */
	private ViewPager viewPager;
	
	/**
	 * Metodo que se invoca cuando la actividad es creada.
	 * 
	 * @param savedInstanceState
	 *            Bundle que contiene el estado de ejecuciones pasadas.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_full_screen_view);

		viewPager = (ViewPager) findViewById(R.id.pager);

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
	}
}

