package com.example.tourplanner2.adapters;

import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import com.example.tourplanner2.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * Clase que implementa el adaptador de la lista para elegir la red social en la que compartir.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 * @author Jesús Manuel Calvo Ruiz de Temiño - jcr0069@alu.ubu.es
 */
public class CustomSocialAdapter extends BaseAdapter {
	/**
	 * Android Components
	 */
	private final LayoutInflater mInflater;
	private final Context ctx;

	/**
	 * Componentes SocialAuth
	 */
	private SocialAuthAdapter adapter;
	private final Provider[] providers = new Provider[] { Provider.FACEBOOK, Provider.TWITTER };
	private final int[] images = new int[] { R.drawable.facebook, R.drawable.twitter };
	/**
	 * Cosntructor de la clase.
	 * @param context
	 * @param mAdapter
	 */
	public CustomSocialAdapter(Context context, SocialAuthAdapter mAdapter) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		ctx = context;
		mInflater = LayoutInflater.from(ctx);
		adapter = mAdapter;
	}

	/**
	 * The number of items in the list is determined by the number of speeches
	 * in our array.
	 */
	@Override
	public int getCount() {
		return providers.length;
	}

	/**
	 * Since the data comes from an array, just returning the index is sufficent
	 * to get at the data. If we were using a more complex data structure, we
	 * would return whatever object represents one row in the list.
	 */
	@Override
	public Object getItem(int position) {
		return position;
	}

	/**
	 * Use the array index as a unique id.
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Make a view to hold each row.
	 * 
	 * @see android.widget.ListAdapter#getView(int, View,
	 *      ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// A ViewHolder keeps references to children views to avoid unneccessary
		// calls to findViewById() on each row.
		ViewHolder holder;

		// When convertView is not null, we can reuse it directly, there is no
		// need to reinflate it. We only inflate a new View when the convertView
		// supplied by ListView is null.
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.providers_list, null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.text = convertView.findViewById(R.id.providerText);
			holder.text.setTextColor(ctx.getResources().getColor(R.color.White));
			holder.text.setTextAppearance(ctx, R.style.TextAppearance_AppCompat_Light_Widget_PopupMenu_Large);
			holder.icon = convertView.findViewById(R.id.provider);
			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		Bitmap mIcon = BitmapFactory.decodeResource(ctx.getResources(), images[position]);

		// Bind the data efficiently with the holder.

		String textCase = providers[position].toString();
		textCase = String.valueOf(textCase.charAt(0)).toUpperCase() + textCase.substring(1);

		holder.text.setText(textCase);
		holder.icon.setImageBitmap(mIcon);

		holder.text.setOnClickListener(v -> {

			if (providers[position].equals(Provider.GOOGLE))
				adapter.addCallBack(Provider.GOOGLE, "http://socialauth.in/socialauthdemo");
			else if (providers[position].equals(Provider.FOURSQUARE))
				adapter.addCallBack(Provider.FOURSQUARE,
						"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
			else if (providers[position].equals(Provider.SALESFORCE))
				adapter.addCallBack(Provider.SALESFORCE,
						"https://socialauth.in:8443/socialauthdemo/socialAuthSuccessAction.do");
			else if (providers[position].equals(Provider.YAMMER))
				adapter.addCallBack(Provider.YAMMER,
						"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");

			// This method will enable the selected provider
			adapter.authorize(ctx, providers[position]);

		});
		return convertView;
	}

	class ViewHolder {
		TextView text;
		ImageView icon;
	}
} 
