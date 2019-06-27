package com.example.tourplanner2.util;
/**
 * Clase que representa un elemento de la ExpandableListView de la pantalla de ajustes.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class RowExpandableItem {
	
	
	/**
	 * Nombre de la categoria.
	 */
	private String name;
	/**
	 * Valor que indica si esta seleccionado o no.
	 */
	private boolean selected;
	/**
	 * Identificador de la imagen.
	 */
	private int resId;
	/**
	 * Nombre del tag.
	 */
	private String tag;
	/**
	 * @return the name
	 */
	/**
	 * Constructor de la clase.
	 * @param name
	 */
	public RowExpandableItem(String name){
		this.name=name;
	}
	
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
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}
	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	/**
	 * @return the resId
	 */
	public int getResId() {
		return resId;
	}
	/**
	 * @param resId the resId to set
	 */
	public void setResId(int resId) {
		this.resId = resId;
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
	
	
}
