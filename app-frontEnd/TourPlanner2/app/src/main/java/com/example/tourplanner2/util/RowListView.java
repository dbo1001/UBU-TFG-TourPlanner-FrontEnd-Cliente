package com.example.tourplanner2.util;

/**
 * Clase que representa un elemento de la lista del men� slide.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class RowListView {
	/**
	 * Nombre de la opci�n.
	 */
	private String text;
	/**
	 * IDentificador del icono.
	 */
	private int imageResId;
	/**
	 * Constructor de la clase.
	 * @param text
	 * @param resId
	 */
	public RowListView(String text, int resId) {
		this.text = text;
		this.imageResId = resId;
	}
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
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

	

}
