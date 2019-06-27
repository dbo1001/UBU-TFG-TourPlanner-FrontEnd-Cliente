package com.example.tourplanner2.util;


/**
 * Clase que representa un elemento de la lista de mis rutas guardadas.
 * 
 * @author Alejandro Cuevas �lvarez - aca0073@alu.ubu.es
 */
public class MyRoutesItem {
	/**
	 * Nombre de la ruta
	 */
	private String name;
	/**
	 * Ciudad.
	 */
	private String city;
	/**
	 * Puntuaci�n de la ruta.
	 */
	private double rating;
	/**
	 * Fecha de la ruta.
	 */
	private String date;
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return the rating
	 */
	public double getRating() {
		return rating;
	}
	/**
	 * @param rating the rating to set
	 */
	public void setRating(double rating) {
		this.rating = rating;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param string the date to set
	 */
	public void setDate(String string) {
		this.date = string;
	}
	
	
	
}
