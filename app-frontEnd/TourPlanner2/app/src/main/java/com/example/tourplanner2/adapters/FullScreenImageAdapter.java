package com.example.tourplanner2.adapters;

import java.util.ArrayList;

import com.example.tourplanner2.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Clase que implementa el adaptador de la visualizaci�n a pantalla completa
 * de la galeria de fotos.
 * 
 * @author Alejandro Cuevas �lvarez.
 * @author aca0073@alu.ubu.es 
 * 
 * */
public class FullScreenImageAdapter extends PagerAdapter{
	
	private Activity activity;
    private ArrayList<String> imageUrls;
    private DisplayImageOptions options;
    private ArrayList<String> authors;
    private ArrayList<String> authorUrls;
  
	private ImageLoader imageLoader;
    private LayoutInflater inflater;
	
    /**
	 * Constructor de la clase.
	 * 
	 * @param activity Contexto de la actividad.
	 * @param imagePaths URLs de las im�genes a mostrar.
	 * @param options Opciones correspondientes a como se van a visualizar las im�genes
	 * @param imageLoader Se utiliza para la visualizaci�n y almacenamiento de las imagenes.
	 * @param authors Autor de la imagen.
	 * @param authorUrls URL de Panoramio del autor de la imagen.
	 * */
    public FullScreenImageAdapter(Activity activity, ArrayList<String> imagePaths, 
    		DisplayImageOptions options, ImageLoader imageLoader, ArrayList<String> authors, 
    		ArrayList<String> authorUrls) {
		this.activity = activity;
		this.imageUrls = imagePaths;
		this.options = options;
		this.imageLoader = imageLoader;
		this.authors = authors;
		this.authorUrls = authorUrls;
	}
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return imageUrls.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		return view == ((RelativeLayout) object);
	}
	
	
	@Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imgFullScreen;
        TextView authorName;
        
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.fullscreen_gallery, container,
                false);
  
        imgFullScreen = (ImageView) viewLayout.findViewById(R.id.imgFullScreen);
        authorName = (TextView) viewLayout.findViewById(R.id.authorName);
         
        imageLoader.displayImage(imageUrls.get(position), imgFullScreen, options);
        String strAuthorFormat = activity.getResources().getString(R.string.author);
        authorName.setText(String.format(strAuthorFormat, authors.get(position)));
        
        authorName.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent("android.intent.action.VIEW", Uri.parse(authorUrls.get(position)));
				activity.startActivity(i);
			}
        	
        	
        });
        
        ((ViewPager) container).addView(viewLayout, 0);
  
        return viewLayout;
    }
     
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
  
    }
   
}
