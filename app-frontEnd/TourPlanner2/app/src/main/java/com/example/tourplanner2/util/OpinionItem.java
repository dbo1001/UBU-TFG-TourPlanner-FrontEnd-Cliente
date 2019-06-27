package com.example.tourplanner2.util;
/**
 * Clase que representa un elemento de la lista de opiniones.
 * 
 * @author Inigo Vázquez - Roberto Villuela
 * @author ivg0007@alu.ubu.es - rvu0003@alu.ubu.es
 */
public class OpinionItem {
	/**
	 * Nombre del que escribio la opini�n.
	 */
	private String name;
	/**
	 * Opinion.
	 */
	private String opinion;
	/**
	 * Puntuaci�n del lugar.
	 */
	private double rating;
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
	
	
	
}
