package com.example.tourplanner2.communication;

import android.content.Context;
/**
 * Interfaz que deben implementar las clases que hagan peticiones al servidor.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public interface IWebServiceTaskResult{
	/**
	 * M�todo que maneja la respuesta del servidor.
	 * @param response respuesta del servidor
	 */
	void handleResponse(String response);
	/**
	 * M�todo que devuelve el contexto de la activity.
	 * @return contexto de la activity
	 */
	Context getContext();
}
