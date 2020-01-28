package com.example.tourplanner2.dialog;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.tourplanner2.R;
import com.example.tourplanner2.communication.IWebServiceTaskResult;
import com.example.tourplanner2.communication.WebServiceTask;
import com.example.tourplanner2.util.Misc;
import com.example.tourplanner2.util.PropertiesParser;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
//import android.view.View;
//import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**
 * Dialogo que se muestra al usuario para que este se registre en la aplicaci�n.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class DialogRegister extends Dialog implements IWebServiceTaskResult{
	/**
	 * Url del servicio.
	 */
	private static String REGISTER_SERVICE_URL;
	/**
	 * Dialogo.
	 */
	private Dialog dialog;
	/**
	 * Contexto de la actividad.
	 */
	private Context context;
	/**
	 * Constructor de la clase.
	 * @param context contexto de la actividad
	 */
	public DialogRegister(final Context context){
		super(context);
		dialog=this;
		this.context=context;
		setServiceDirections();
		setContentView(R.layout.register_dialog);
		findViewById(R.id.btnEnviar).setOnClickListener(v -> {
			String name=((EditText)findViewById(R.id.editTextName)).getText().toString();
			String email=((EditText)findViewById(R.id.editTextEmail)).getText().toString();
			String password=((EditText)findViewById(R.id.editTextPassword)).getText().toString();
			if(email.length()>0 && name.length()>0){
				if(password.length()>4){
					WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK,(IWebServiceTaskResult)dialog );
					wst.addNameValuePair("email", email);
					wst.addNameValuePair("password", password);
					wst.addNameValuePair("name",name);
					wst.execute(REGISTER_SERVICE_URL);
				}else{
					((EditText) findViewById(R.id.editTextPassword)).setText("");
					Toast.makeText(
							context.getApplicationContext(),
							context.getResources().getString(R.string.passwordToShort),
							Toast.LENGTH_LONG).show();
				}
			}else{
				((EditText) findViewById(R.id.editTextPassword)).setText("");
				Toast.makeText(
						context.getApplicationContext(),
						context.getResources().getString(
								R.string.mustFillIn), Toast.LENGTH_LONG)
						.show();
			}
		});
	}
	/**
	 * M�todo que establece la direcci�n del servicio.
	 */
	private void setServiceDirections(){
		try {
		String address=PropertiesParser.getConnectionSettings(context);
		REGISTER_SERVICE_URL = "https://"+address+"/osm_server/get/authentication";
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * M�todo que procesa la respuesta del servidor.
	 * @param response respuesta del servidor
	 */
	@Override
	public void handleResponse(String response) {
		try {
			JSONObject jso = new JSONObject(response);
			if(jso.getString("status").equals("Registered")){
				SharedPreferences pref = PreferenceManager
						.getDefaultSharedPreferences(context.getApplicationContext());
				Editor edit=pref.edit();
				edit.putBoolean("registered", true);
				edit.putString("username", ((EditText)findViewById(R.id.editTextName)).getText().toString());
				edit.putString("email", ((EditText)findViewById(R.id.editTextEmail)).getText().toString());
				edit.apply();
				dismiss();
			}else{
				if (jso.has("status") &&!Misc.checkErrorCode(jso.getString("status"),
						context)) {
					return;
				}
				((EditText)findViewById(R.id.editTextPassword)).setText("");
				Toast.makeText(context.getApplicationContext(),
						context.getResources().getString(R.string.incorrectPassword),
						Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	

}
