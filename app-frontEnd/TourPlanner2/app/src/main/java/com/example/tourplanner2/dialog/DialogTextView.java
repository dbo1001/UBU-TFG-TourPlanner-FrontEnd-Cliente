package com.example.tourplanner2.dialog;

import com.example.tourplanner2.R;
import android.app.Dialog;
import android.content.Context;
//import android.view.View;
//import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
//import android.widget.AdapterView.OnItemClickListener;

/**
 * Dialogo que se muestra al usuario cuando este pincha en un textView para
 * seleccionar una opci�n, despu�s se actualiza este TextView.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class DialogTextView extends Dialog {
	/**
	 * Constructor de la clase.
	 * @param context contexto de la activity
	 * @param data opciones a mostrar
	 * @param textView asociado
	 */
	public DialogTextView(Context context, final String[] data,
			final TextView textView) {
		super(context);
		setContentView(R.layout.list_view);
		setTitle(context.getString(R.string.selectOption));
		ListView list = findViewById(R.id.list_view);
		ArrayAdapter<String> adapt = new ArrayAdapter<>(context,
				android.R.layout.simple_list_item_1, data);
		list.setAdapter(adapt);
		list.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
			textView.setText(data[arg2]);
			dismiss();
		});
	}

}
