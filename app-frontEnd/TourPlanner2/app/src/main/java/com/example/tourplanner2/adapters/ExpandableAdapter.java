package com.example.tourplanner2.adapters;

import java.util.List;


//import com.caverock.androidsvg.R;
import com.example.tourplanner2.util.RowExpandableItem;
import com.example.tourplanner2.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tourplanner2.activities.AdvancedOptionsActivity;
import com.example.tourplanner2.util.RowExpandableItem;

/**
 * Clase implementa el adaptador del ExpandableListView de la pantalla de opciones.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class ExpandableAdapter extends BaseExpandableListAdapter {
	/**
	 * Lista de hijos.
	 */
	private List<List<RowExpandableItem>> children;
	/**
	 * Lista de grupos.
	 */
	private List<String> groups;
	/**
	 * Lista de tags.
	 */
	private List<String> tags;
	/**
	 * Contexto de la actividad que usa el adaptador.
	 */
	private final Activity context;
	/**
	 * Contexto para acceder a las shared preferences.
	 */
	private Context preferencesContext;

	/**
	 * Costructor de la aplicación.
	 * @param context contexto de la activity
	 * @param groups lista de grupos
	 * @param tags lista de tags
	 * @param children listas de hijos
	 * @param preferencesContext contexto de las preferencias
	 */
	public ExpandableAdapter(Activity context, List<String> groups,
			List<String> tags, List<List<RowExpandableItem>> children, Context preferencesContext) {
		super();
		this.context = context;
		this.children = children;
		this.groups = groups;
		this.tags=tags;
		this.preferencesContext = preferencesContext;
	}

    /**
	 * Clase interna que implementa un viewHolder para los hijos.
	 * 
	 * @author Inigo Vázquez - Roberto Villuela
	 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
	 */
	private static class ViewHolder {
		protected TextView text;
		protected CheckBox checkbox;
		protected ImageView image;
	}
	/**
	 * Clase interna que implementa un viewHolder para los grupos.
	 * 
	 * @author Inigo Vázquez - Roberto Villuela
	 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
	 */
	private static class GroupViewHolder {
		protected ImageView image;
		protected TextView text;
	}
	/**
	 * Metodo que devuleve el hijo de una posición.
	 * 
	 */
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return children.get(groupPosition).get(childPosition);
	}
	/**
	 * Método que devuelve el identificador de un determinado hijo.
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	/**
	 * Método que devuelve la vista asociada a un determiando hijo.
	 *  @param groupPosition posición del grupo
	 *  @param childPosition posición del hijo
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			view = getNewChildView(groupPosition, childPosition);
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(children.get(
					groupPosition).get(childPosition));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.text.setText(children.get(groupPosition).get(childPosition)
				.getName());
		holder.checkbox.setChecked(children.get(groupPosition)
				.get(childPosition).isSelected());
		holder.image.setImageResource(children.get(groupPosition)
				.get(childPosition).getResId());
		CheckListener checkL = new CheckListener();
		checkL.setPosition(groupPosition, childPosition);
		holder.checkbox.setOnClickListener(checkL);
		return view;
	}
	/**
	 * Método que devuelve la vista de un hijo cuando se hace nuevo.
	 * @param groupPosition posición del grupo
	 * @param childPosition posición del hijo
	 */
	private View getNewChildView(int groupPosition, int childPosition) {
		View view;
		LayoutInflater inflator = context.getLayoutInflater();
		view = inflator.inflate(R.layout.expandable_list_element, null);
		final ViewHolder viewHolder = new ViewHolder();
		viewHolder.text = (TextView) view.findViewById(R.id.label);
		viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
		viewHolder.image=(ImageView)view.findViewById(R.id.image);

		view.setTag(viewHolder);
		viewHolder.checkbox.setTag(children.get(groupPosition).get(
				childPosition));
		return view;
	}
	/**
	 * Método que devuelve el número de hijos de un determiando grupo.
	 * @param groupPosition posición del grupo
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		return children.get(groupPosition).size();
	}
	/**
	 * Método que devuelve un grupo apartir de su posición.
	 * @param groupPosition posición del grupo
	 * @return grupo
	 */
	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}
	/**
	 * Método que devuelve el número de grupos.
	 */
	@Override
	public int getGroupCount() {
		return groups.size();
	}
	/**
	 * Método que devulve el identificador de un grupo a partir de su posición.
	 * @return identificador del grupo
	 */
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
	/**
	 * Método que devuelve la vista asociada a un grupo.
	 * @param groupPosition posición del grupo
	 * @param isExpanded indica si el grupo esta expandido
	 * @param convertView vista del grupo
	 * @param parent vista padre
	 * @return vista
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			view = getNewGroupView();
		} else {
			view = convertView;
		}
		GroupViewHolder holder = (GroupViewHolder) view.getTag();
		holder.text.setText(groups.get(groupPosition));
		holder.image.setImageResource(getImageIdTag(tags.get(groupPosition)));
		return view;
	}
	/**
	 * Método que devuelve una vista nueva de un grupo.
	 * @return vista del grupo
	 */
	private View getNewGroupView() {
		View view;
		LayoutInflater inflator = context.getLayoutInflater();
		view = inflator.inflate(R.layout.expandable_list_group, null);
		final GroupViewHolder viewHolder = new GroupViewHolder();
		viewHolder.text = (TextView) view.findViewById(R.id.lblText);
		viewHolder.image = (ImageView) view.findViewById(R.id.image);
		view.setTag(viewHolder);
		return view;
	}
	/**
	 * Método que devuelve el id de una imagen asociada a un tag.
	 * @return id de la imagen
	 */
	public int getImageIdTag(String tag) {
		if (tag.equals("leisure")) {
			return R.drawable.leisure;
		} else {
			if (tag.equals("culture")) {
				return R.drawable.culture;
			} else {
				if (tag.equals("nature")) {
					return R.drawable.nature;
				} else {
					return R.drawable.gastronomy;
				}
			}
		}

	}
	
	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
 

	/**
	 * Clase interna que hace de lister de los ckeckbox.
	 * 
	 * @author Inigo Vázquez - Roberto Villuela
	 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
	 */
	public class CheckListener implements OnClickListener {
		/**
		 * Posici�n del grupo.
		 */
		int groupPos;
		/**
		 * Posici�n del hijo.
		 */
		int childPos;

		public void setPosition(int groupPos, int childPos) {
			this.groupPos = groupPos;
			this.childPos = childPos;
		}

		@Override
		public void onClick(View v) {

			children.get(groupPos)
					.get(childPos)
					.setSelected(
							!children.get(groupPos).get(childPos).isSelected());
			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(preferencesContext);
			SharedPreferences.Editor editor = settings.edit();
			String key = children.get(groupPos).get(childPos).getTag();
			boolean value = children.get(groupPos).get(childPos).isSelected();
			editor.putBoolean(key, value);
			editor.commit();
		}
	}

}
