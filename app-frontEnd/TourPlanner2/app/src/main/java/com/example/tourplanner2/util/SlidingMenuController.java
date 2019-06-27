package com.example.tourplanner2.util;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.example.tourplanner2.activities.MapMain;
import com.example.tourplanner2.adapters.OptionsAdapter;
//import com.example.tourplanner2.client.R;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.tourplanner2.R;
/**
 * Clase que funciona como controlador del menu slide.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class SlidingMenuController extends SherlockActivity {
	/**
	 * Elementos del men�.
	 */
	private RowListView[] rows = new RowListView[7];
	/**
	 * Constructor de la clase.
	 * @param activity en la que se encuentra el men� slide.
	 */
	public SlidingMenuController(final SlidingActivity activity){
		rows[0] = new RowListView(activity.getResources().getString(R.string.ms_cfg),
				R.drawable.ic_action_place);
		rows[1] = new RowListView(activity.getResources().getString(R.string.planner),
				R.drawable.ic_action_map);
		rows[2] = new RowListView(
				activity.getResources().getString(R.string.speedRoute),
				R.drawable.ic_action_good);
		rows[3] = new RowListView(activity.getResources().getString(
				R.string.recomendedPlaces), R.drawable.ic_action_edit);
		rows[4] = new RowListView(activity.getResources().getString(
				R.string.route), R.drawable.ic_action_save);
		rows[5] = new RowListView(activity.getResources().getString(
				R.string.advancedOptions), R.drawable.ic_action_settings);
		rows[6] = new RowListView(activity.getResources().getString(R.string.profile),
				R.drawable.ic_action_person);
		activity.setBehindContentView(R.layout.menu_slide);
		activity.getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);
		activity.getSlidingMenu().setShadowDrawable(R.drawable.shadow);
		activity.getSlidingMenu().setBehindOffsetRes(R.dimen.slidingmenu_offset);
		activity.getSlidingMenu().setFadeDegree(0.35f);
		activity.setSlidingActionBarEnabled(true);
		activity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		activity.getActionBar().setDisplayHomeAsUpEnabled(true);
		OptionsAdapter adaptador = new OptionsAdapter(activity, rows);
		ListView lstOpciones = (ListView) activity.findViewById(R.id.listView);
		lstOpciones.setAdapter(adaptador);
		lstOpciones.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				Intent intent;
				switch (position) {
				case 0:
					intent = new Intent();
					activity.setResult(MapMain.OPEN_PREFERENCES, intent);
					activity.finish();
					break;
				case 1:
					intent = new Intent();
					activity.setResult(MapMain.OPEN_PLANNER, intent);
					activity.finish();
					break;
				case 2:
					intent = new Intent();
					activity.setResult(MapMain.EXPRESS_ROUTE, intent);
					activity.finish();
					break;
				case 3:
					intent = new Intent();
					activity.setResult(MapMain.OPEN_RECOMENDED_PLACES, intent);
					activity.finish();
					break;
				case 4:
					intent = new Intent();
					activity.setResult(MapMain.OPEN_TRACKS, intent);
					activity.finish();
					break;
				case 5:
					intent = new Intent();
					activity.setResult(MapMain.OPEN_ADVANCED_OPTIONS, intent);
					activity.finish();
					break;
				case 6:
					intent = new Intent();
					activity.setResult(MapMain.OPEN_PROFILE, intent);
					activity.finish();
					break;
				case 7:
					intent = new Intent();
					activity.setResult(MapMain.SHARE_ROUTE, intent);
					activity.finish();
				}
			}
		});
	}

}
