package kodingProject;


import java.util.ArrayList;
import java.util.List;

/**
 * This is a MetaCritic Class for retrieving "User's Game Score", "Positive Review", "Neutral Review" & "Negative Review"
 * @author Zhan An, Wei Xiang, Jing Wei, Chang Hua
 */

public class MetaCritic {
	
	private double userGameScore;
	private int positiveReview;
	private int neutralReview;
	private int negativeReview;
	private ArrayList<MetaCriticReview> listOfReviews;
	/**
	 * This is a default constructor
	 */
	public MetaCritic() {

	}
	/**
	 * Get the User's Game Score
	 * @return the User GameScore in double 
	 */
	public double getUserGameScore() {
		return userGameScore;
	}
	/**
	 * Set the  User's Game Score
	 * @param userGameScore is the User's Game Score in double
	 * @exception IllegalArgumentException if the User's Game Score if negative 
	 */
	public void setUserGameScore(double userGameScore) {
		if(userGameScore<0 || userGameScore> 10)
			throw new IllegalArgumentException("Invalid Score");
		this.userGameScore = userGameScore;
	}
	/**
	 * Get the number of Positive Reviews
	 * @return the number of Positive Reviews in integer 
	 */
	public int getPositiveReview() {
		return positiveReview;
	}
	/**
	 * Set the number of Positive Reviews
	 * @param positiveReview is the number of Positive Reviews in integer
	 * @exception IllegalArgumentException if the number of Positive Reviews if negative 
	 */
	public void setPositiveReview(int positiveReview) {
		if(positiveReview<0)
			throw new IllegalArgumentException("Illegal Count");
		this.positiveReview = positiveReview;
	}
	/**
	 * Get the number of Neutral Reviews
	 * @return the number of Neutral Reviews in integer 
	 */
	public int getNeutralReview() {
		return neutralReview;
	}
	/**
	 * Set the number of Neutral Reviews
	 * @param neutralReview is the number of Neutral Reviews in integer
	 * @exception IllegalArgumentException if the number of Neutral Reviews if negative 
	 */
	public void setNeutralReview(int neutralReview) {
		if(neutralReview<0)
			throw new IllegalArgumentException("Illegal Count");
		this.neutralReview = neutralReview;
	}
	/**
	 * Get the number of Negative Reviews
	 * @return the number of Negative Reviews in integer 
	 */
	public int getNegativeReview() {
		return negativeReview;
	}
	/**
	 * Set the number of Negative Reviews
	 * @param negativeReview is the number of Negative Reviews in integer
	 * @exception IllegalArgumentException if the number of Negative Reviews if negative 
	 */
	public void setNegativeReview(int negativeReview) {
		if(negativeReview<0)
			throw new IllegalArgumentException("Illegal Count");
		this.negativeReview = negativeReview;
	}
	/**
	 * Get the array list of reviews
	 * @return the list if reviews from MetaCritic in an ArrayList 
	 */
	public ArrayList<MetaCriticReview> getListOfReviews() {
		return listOfReviews;
	}
	/**
	 * Set the list of MetaCritic Game Reviews in an Array list
	 * @param listOfReviews is an Array list of multiple reviews
	 * @exception IllegalArgumentException if the number of array list is empty/null
	 */
	public void setListOfReviews(ArrayList<MetaCriticReview> listOfReviews) {
		if(listOfReviews == null || listOfReviews.isEmpty()) 
			throw new IllegalArgumentException("Missing Reviews");
		this.listOfReviews = listOfReviews;
	}

}
