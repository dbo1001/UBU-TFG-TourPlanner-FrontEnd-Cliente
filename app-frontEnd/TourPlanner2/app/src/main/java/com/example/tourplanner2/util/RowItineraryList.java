package com.example.tourplanner2.util;

import java.io.Serializable;

/**
 * Clase que representa un elemento de la lista que muestra el itinerario.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class RowItineraryList implements Serializable{
	

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = -6367746621528833127L;
	/**
	 * Nombre del lugar.
	 */
	private String textName;
	/**
	 * Nombre del tag.
	 */
	private String tag;
	/**
	 * Identificador de la imagen de la categoria.
	 */
	private int imageResId;
	/**
	 * Identificador de la imagen del tag.
	 */
	private int imageTagResId;
	/**
	 * Coordenadas del punto.
	 */
	private String coordinates;
	/**
	 * Puntuaci�n que el usuario a asignado al punto.
	 */
	private float rating;
	/**
	 * Puntuaci�n del punto.
	 */
	private double score;
	/**
	 * Indica si el punto est� promocionado.
	 * */
	private boolean promoted;
	/**
	 * Id del punto.
	 */
	private long poiId;
	/**
	 * Indica si el punto ha sido seleccionado.
	 */
	private boolean isSelected;
	/**
	 * Tiempo del punto.
	 */
	private float time;
	/**
	 * Tiempo formateado.
	 */
	private String formattedTime;
	/**
	 * Opinion del punto.
	 */
	private String opinion="";
	/**
	 * @return the textName
	 */
	public String getTextName() {
		return textName;
	}
	/**
	 * @param textName the textName to set
	 */
	public void setTextName(String textName) {
		this.textName = textName;
	}
	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}
	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}
	/**
	 * @return the imageResId
	 */
	public int getImageResId() {
		return imageResId;
	}
	/**
	 * @param imageResId the imageResId to set
	 */
	public void setImageResId(int imageResId) {
		this.imageResId = imageResId;
	}
	/**
	 * @return the imageTagResId
	 */
	public int getImageTagResId() {
		return imageTagResId;
	}
	/**
	 * @param imageTagResId the imageTagResId to set
	 */
	public void setImageTagResId(int imageTagResId) {
		this.imageTagResId = imageTagResId;
	}
	/**
	 * @return the coordinates
	 */
	public String getCoordinates() {
		return coordinates;
	}
	/**
	 * @param coordinates the coordinates to set
	 */
	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}
	/**
	 * @return the rating
	 */
	public float getRating() {
		return rating;
	}
	/**
	 * @param rating the rating to set
	 */
	public void setRating(float rating) {
		this.rating = rating;
	}
	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}
	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}
	/**
	 * @return the promoted
	 */
	public boolean isPromoted() {
		return promoted;
	}
	/**
	 * @param promoted the promoted to set
	 */
	public void setPromoted(boolean promoted) {
		this.promoted = promoted;
	}
	/**
	 * @return the poiId
	 */
	public long getPoiId() {
		return poiId;
	}
	/**
	 * @param poiId the poiId to set
	 */
	public void setPoiId(long poiId) {
		this.poiId = poiId;
	}
	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}
	/**
	 * @param isSelected the isSelected to set
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	/**
	 * @return the time
	 */
	public float getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(float time) {
		this.time = time;
	}
	/**
	 * @return the formattedTime
	 */
	public String getFormattedTime() {
		return formattedTime;
	}
	/**
	 * @param formattedTime the formattedTime to set
	 */
	public void setFormattedTime(String formattedTime) {
		this.formattedTime = formattedTime;
	}
	/**
	 * @return the opinion
	 */
	public String getOpinion() {
		return opinion;
	}
	/**
	 * @param opinion the opinion to set
	 */
	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}	
}

