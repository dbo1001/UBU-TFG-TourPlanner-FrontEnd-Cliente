package com.example.tourplanner2.util;

import com.example.tourplanner2.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Clase que implementa ela notificaci�n de la descarga de mapas.
 * 
 * @author Alejandro Cuevas �lvarez.
 * @author aca0073@alu.ubu.es 
 * 
 * */
public class MapNotification {
	/**
	 * Contexto de la activity.
	 * */
	private Context notifContext;
	/**
	 * Nombre del mapa a mostrar en la notificacion.
	 * */
	private String mapName;
	/**
	 * Notificacion que muestra el progreso de la descarga del mapa.
	 * */
	private Notification mapNotification;
	/**
	 * Notificacion que indica el final de la descarga del mapa.
	 * */
	private Notification finishNotification;
	/**
	 * ID de la notificacion.
	 * */
	private int NOTIFICATION_ID = 1;
	/**
	 * Mensaje a mostar.
	 * */
	private CharSequence title;
	/**
	 * Intent necesario para lanzar y controlar la notificacion.
	 * */
	private PendingIntent contentIntent;
	/**
	 * Referencia al NotificationManager.
	 * */
	private NotificationManager notificationManager;
	
	/**
	 * Constructor de la clase.
	 * 
	 * @param context Contexto de la actividad.
	 * @param name Nombre del mapa a descargar.
	 * 
	 * */
	public MapNotification(Context context, String name){
		notifContext = context;
		mapName = name;
	}
	
	/**
	 * Metodo que crea la notificacion.
	 * 
	 * */
	@SuppressWarnings("deprecation")
	public void createNotification(){
		// Obtenemos el notificacion manager.
		
		notificationManager = (NotificationManager) notifContext.getSystemService(Context.NOTIFICATION_SERVICE);
		
		// Creamos la notificaci�n.
		int icon = android.R.drawable.stat_sys_download;
		String strInfoDownloadFormat = notifContext.getResources().getString(R.string.notification_download);
		CharSequence notifText = String.format(strInfoDownloadFormat, mapName);
		long time = System.currentTimeMillis();
		mapNotification = new Notification(icon, notifText, time);
		
		mapNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		// Configuramos el Intent
		title = String.format(strInfoDownloadFormat, mapName);
		CharSequence description = 0 + "%";
		
		Intent notifIntent = new Intent();
		contentIntent = PendingIntent.getActivity(notifContext, 0, notifIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		mapNotification.setLatestEventInfo(notifContext, title, description, contentIntent);
		
		mapNotification.flags = Notification.FLAG_ONGOING_EVENT;
		
		// Mostramos la notificacion
        notificationManager.notify(NOTIFICATION_ID, mapNotification);
        
	}

	/**
	 * Metodo que actualiza la notificacion segun se va descargando el mapa.
	 * 
	 * @param percentage Porcentaje a actualizar.
	 * 
	 * */
	@SuppressWarnings("deprecation")
	public void publishProgress(int percentage){
		// Configuramos el nuevo mensaje a mostrar.
		 CharSequence description = percentage + "%";
		 
		 mapNotification.setLatestEventInfo(notifContext, title, description, contentIntent);
		 notificationManager.notify(NOTIFICATION_ID, mapNotification);
		
	}
	
	/**
	 * Metodo que es llamado cuando se completa la descarga para mostrar la notificacion
	 * de finalizacion
	 * 
	 * */
	@SuppressWarnings("deprecation")
	public void completed(){
		notificationManager.cancel(NOTIFICATION_ID);
		int icon = android.R.drawable.stat_sys_download_done;
		CharSequence notifText = notifContext.getString(R.string.notification_finish);
		long time = System.currentTimeMillis();
		finishNotification = new Notification(icon, notifText, time);
		
		// AutoCancel: cuando se pulsa la notificaci�n �sta desaparece.
		finishNotification.flags |= Notification.FLAG_AUTO_CANCEL;

		finishNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		
		Intent notifIntent = new Intent();
		contentIntent = PendingIntent.getActivity(notifContext, 0, notifIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		title = notifContext.getString(R.string.notification_finish);
		finishNotification.setLatestEventInfo(notifContext, title, "", contentIntent);
		
		
		// Mostramos la notificacion
        notificationManager.notify(NOTIFICATION_ID, finishNotification);              
	}
	
	/**
	 * Metodo con el que se obtiene el ID de la notificacion.
	 * 
	 * */
	public int getId() {
		return NOTIFICATION_ID;
	}
	
	/**
	 * Metodo en el que se obtiene el nombre del mapa.
	 * 
	 * */
	public String getMapName() {
		return mapName;
	}
	
}
