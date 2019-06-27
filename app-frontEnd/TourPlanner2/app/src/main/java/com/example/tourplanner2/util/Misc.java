package com.example.tourplanner2.util;


import com.example.tourplanner2.R;
import android.content.Context;
import android.widget.Toast;
/**
 * Clase con metodos para diversos fines. 
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class Misc {
	/**
	 * C�digos de error devueltos por el servidor.
	 */
	private final static int NOT_TARGET_POI_CATEGORY_SELECTED = 600, UNCONNECTED_POIS = 601, NOT_FEASIBLE_ROUTE = 602, ERROR_INTERNAL = 603;
	/**
	 * M�todo que comprueba si un status se corresponde con un c�digo de error
	 * @param status cadena con el status
	 * @param context contexto de la activity
	 * @return true en caso de que el status contenga un c�digo de error, false en caso contrario
	 */
	public static boolean checkErrorCode(String status,Context context){
		if(status.equals("")){
			return true;
		}else{
			switch(Integer.valueOf(status)){
			case NOT_TARGET_POI_CATEGORY_SELECTED: 
				Toast.makeText(context,
						context.getResources().getString(R.string.MSG_ERROR_NOT_TARGET_POI_CATEGORY_SELECTED),
					Toast.LENGTH_LONG).show();
				break;
			case UNCONNECTED_POIS:
				Toast.makeText(context,
						context.getResources().getString(R.string.MSG_ERROR_UNCONNECTED_POIS),
					Toast.LENGTH_LONG).show();
				break;
			case NOT_FEASIBLE_ROUTE:
				Toast.makeText(context,
						context.getResources().getString(R.string.MSG_ERROR_NOT_FEASIBLE_ROUTE),
					Toast.LENGTH_LONG).show();
				break;
			case ERROR_INTERNAL:
				Toast.makeText(context,
						context.getResources().getString(R.string.MSG_ERROR_INTERNAL),
					Toast.LENGTH_LONG).show();
				break;
			}
		}
		return false;
	}
	/**
	 * M�todo que devuele el identificador de un recurso a partir de una cadena
	 * @param imageUri uri del recurso
	 * @param context contexto de la aplicaci�n
	 * @return id del recurso
	 */
	public static int getResId(String imageUri,Context context) {

		return context.getResources().getIdentifier(imageUri, null, context.getPackageName());
	}
	
	/**
	 * Método que muestra un número en formato de hora.
	 * 
	 * @param c
	 *            número a mostrar.
	 * @return número formateado
	 */
	public static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}
	
}
