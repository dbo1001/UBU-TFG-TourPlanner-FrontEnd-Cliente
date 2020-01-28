package com.example.tourplanner2.dialog;

import com.example.tourplanner2.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
/**
 * Dialogo que se muestra al usuario para ofrecerle registrarse o autentificarse.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class DialogProfile extends Dialog {
	/**
	 * Constructor de la clase.
	 * @param context contexto de la actividad
	 */
	public DialogProfile(final Context context) {
		super(context);
		setContentView(R.layout.profile_dialog);
		findViewById(R.id.buttonEnter).setOnClickListener(v -> {
			DialogEnter dialog=new DialogEnter(context);
			dialog.setTitle(context.getResources().getString(R.string.enterTourPlanner));
			dialog.show();
			dismiss();

		});
		findViewById(R.id.buttonCreateAccount).setOnClickListener(v -> {
			DialogRegister dialog=new DialogRegister(context);
			dialog.setTitle(context.getResources().getString(R.string.registerTourPlanner));
			dialog.show();
			dismiss();

		});

	}

}
