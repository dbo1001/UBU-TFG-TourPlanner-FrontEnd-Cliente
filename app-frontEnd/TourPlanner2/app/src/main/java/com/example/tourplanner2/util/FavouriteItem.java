package com.example.tourplanner2.util;


/**
 * Clase que representa un elemento de la lista de favoritos.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class FavouriteItem {
	
	/**
	 * Nombre.
	 */
	private String textName;
	/**
	 * Identificador de la imagen.
	 */
	private int imageResId;
	/**
	 * Puntuaci�n.
	 */
	private double score;
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
	

	
		
}

