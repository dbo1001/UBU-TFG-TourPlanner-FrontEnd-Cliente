package com.example.tourplanner2.services;

import java.util.List;
import java.util.Objects;

import com.example.tourplanner2.activities.MapMain;
import com.example.tourplanner2.R;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.content.PermissionChecker;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

/**
 * Dialogo que se muestra al usuario cuando este pincha en un textView para
 * seleccionar una opci�n, despu�s se actualiza este TextView.
 *
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class CheckLocationService extends Service {
	/**
	 * Binder del servicio.
	 */
	private final IBinder mBinder = new MyBinder();
	/**
	 * Proveedor de localizaci�n.
	 */
	private LocationManager locationManager;
	/**
	 * Listener de la localizaci�n.
	 */
	private LocationListener locationListener;
	/**
	 * Latitud del gps.
	 */
	private String gpslat;
	/**
	 * Longitud del gps.
	 */
	private String gpslon;
	/**
	 * Coordenadas de destino.
	 */
	private String targetCoordinates;
	/**
	 * Banderas de puntos visitados.
	 */
	private boolean checkCoordinates = false, visitedPoi = false;
	/**
	 * Radio para establecer que est� cerca del destino.
	 */
	private final double TARGET_RADIUS = 50;
	/**
	 * Lista de coordenadas de los puntos del itinerario.
	 */
	private List<String> coordinates;

	/**
	 * Interfaz que deben implementar las clases con las que se quira comunicar el servicio.
	 * @author Inigo Vázquez - Roberto Villuela
	 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
	 */
	public interface ServiceClient {
		/**
		 * M�todo al que el servivio puede llamar en la actividad  a la que esta ligada.
		 * @param lat latitud de la localizaci�n
		 * @param lon longitud de la localizaci�n
		 */
		void serviceClientMethod(String lat, String lon);
	}

	/**
	 * Actividad ligada al servivio.
	 */
	private ServiceClient mClient;

	/**
	 * M�todo que establece la actividad a la que se liga el servicio.
	 * @param client
	 */
	public void setServiceClient(ServiceClient client) {
		if (client == null) {
			mClient = null;
			return;
		}

		mClient = client;
	}

	/***
	 * M�todo onStartCommand
	 * @param intent
	 * @param flags
	 * @param startId
	 * @return Service.START_NOT_STICKY
	 */
	public int onStartCommand(Intent intent, int flags, int startId) {

		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	/**
	 * M�todo para actualizar la localizaci�n.
	 */
	public void updateLocation() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Obtenemos la última posición conocida
		if (PermissionChecker.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED &&
				PermissionChecker.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    Activity#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for Activity#requestPermissions for more details.
			return;
		}
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location == null) {
				location = locationManager
						.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
			}
		}
		if(location!=null){
			gpslat = String.valueOf(location.getLatitude());
			gpslon = String.valueOf(location.getLongitude());
		}

		// Nos registramos para recibir actualizaciones de la posición
		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
					gpslat = String.valueOf(location.getLatitude());
					gpslon = String.valueOf(location.getLongitude());
					mClient.serviceClientMethod(gpslat,gpslon);
					if (checkCoordinates) {
						if(visitedPoi){
							String lat = targetCoordinates.substring(
									targetCoordinates.indexOf(" ") + 1,
									targetCoordinates.indexOf(")"));
							String lon = targetCoordinates.substring(
									targetCoordinates.indexOf("(") + 1,
									targetCoordinates.indexOf(" "));
							double distance = calculateDistanceToPoint(lat,lon);
							if (distance < TARGET_RADIUS) {
								createNotification();
								stopCheckCoordinates();
							}
						}else{
							checkIfIsVisitingPoi();
						}
					}
			}
			
	
			

			public void onProviderDisabled(String provider) {

			}

			public void onProviderEnabled(String provider) {

			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {

			}
		};

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 0, locationListener);

	}

	/**
	 * M�todo que calcula la distancia desde el punto actual a uno dado.
	 * @param lat latitud del punto
	 * @param lon longitud del punto
	 * @return distancia al punto
	 */
	private int calculateDistanceToPoint(String lat,String lon){
		Location loc1 = new Location("location1");
		loc1.setLatitude(Double.valueOf(gpslat));
		loc1.setLongitude(Double.valueOf(gpslon));
		Location loc2 = new Location("location1");
		loc2.setLatitude(Double.valueOf(lat));
		loc2.setLongitude(Double.valueOf(lon));
		return (int) (loc1.distanceTo(loc2));
	}
	/**
	 * M�todo que comprueba si se esta visitando un punto.
	 */
	private void checkIfIsVisitingPoi() {
		
		for(int i=1;i<coordinates.size()-2;i++){
			String coord=coordinates.get(i);
			String lat = coord.substring(
					coord.indexOf(" ") + 1,
					coord.indexOf(")"));
			String lon = coord.substring(
					coord.indexOf("(") + 1,
					coord.indexOf(" "));
			if(calculateDistanceToPoint(lat, lon)<TARGET_RADIUS){
				visitedPoi=true;
				return;
			}
		}
	}
	
	
	/**
	 * M�todo que crea la notificaci�n cuando se llega al final de la ruta.
	 */
	@SuppressWarnings("deprecation")
	private void createNotification() {
		// Obtenemos una referencia al servicio de notificaciones
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager notManager = (NotificationManager) getSystemService(ns);

		// Configuramos la notificación
		int icono = R.drawable.alert;
		CharSequence textoEstado = "Alerta!";
		long hora = System.currentTimeMillis();

		//Notification notif = new Notification(icono, textoEstado, hora);
		NotificationCompat.Builder notif = new NotificationCompat.Builder(getApplicationContext())
				.setSmallIcon(R.drawable.alert)
				.setContentTitle(textoEstado)
				.setWhen(hora);

		// Configuramos el Intent
		Context contexto = getApplicationContext();
		CharSequence titulo = "TourPlanner";
		CharSequence descripcion = "Llegando al destino";

		Intent notIntent = new Intent(contexto, MapMain.class);
		notIntent.setAction("android.intent.action.MAIN");
		notIntent.putExtra("status", "restored");
		notIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		notIntent.addCategory("android.intent.category.LAUNCHER");

		PendingIntent contIntent = PendingIntent.getActivity(contexto,
				0, notIntent, 0);
		notif.setContentIntent(contIntent);
		//setLatestEventInfo(contexto, titulo, descripcion,contIntent);

		// AutoCancel: cuando se pulsa la notificai�n ésta desaparece
		notif.setAutoCancel(true);
		//flags |= Notification.FLAG_AUTO_CANCEL;

		// Añadir sonido, vibraci�n y luces
		notif.setDefaults(Notification.DEFAULT_SOUND);
		notif.setDefaults(Notification.DEFAULT_VIBRATE);
		notif.setDefaults(Notification.DEFAULT_LIGHTS);
		//defaults |= Notification.DEFAULT_SOUND;
		//notif.defaults |= Notification.DEFAULT_VIBRATE;
		//notif.defaults |= Notification.DEFAULT_LIGHTS;

		// Enviar notificaci�n
		assert notManager != null;
		notManager.notify(33, notif.build());
	}
	/**
	 * M�todo que comprueba si el GPS esta habilitado.
	 * @return true si esta habilitado, false en caso contario
	 */
	public boolean checkGpsEnabled(){
		return ((LocationManager) Objects.requireNonNull(getSystemService(Context.LOCATION_SERVICE))).isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	/**
	 * M�todo que indica que debe de dearse de comprobar si esta llegando al destino. 
	 */
	public void stopCheckCoordinates(){
		checkCoordinates=false;
		visitedPoi=false;
	}
	/**
	 * M�todo que devuelve la latitud actual.
	 * @return latitud
	 */
	public String getLatitude() {
		return gpslat;
	}
	/**
	 * M�todo que devuelve la longitud actual.
	 * @return longitud
	 */
	public String getLongitude() {
		return gpslon;
	}
	public class MyBinder extends Binder {

		public CheckLocationService getService() {
			return CheckLocationService.this;
		}
	}
	/**
	 * M�todo que establece las coordenadas de los puntos del intinerario.
	 * @param itineraryCoordinates lista de coordenadas
	 */
	public void setItinerary(List<String> itineraryCoordinates) {
		this.coordinates=itineraryCoordinates;	
		checkCoordinates=true;
		targetCoordinates=itineraryCoordinates.get(itineraryCoordinates.size()-1);
	}

}
