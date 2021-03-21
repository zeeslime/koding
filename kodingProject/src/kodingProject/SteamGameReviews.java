package kodingProject;

import java.util.ArrayList;
/**
 * This is a SteamGameReviews Class for retrieving "Review", "Game Title", "Review Date", 
 * "How many found helpful" &  "Game Hours"
 * 
 * @author Chang Hua, Zhan An, Wei Xiang, Jing Wei
 */
public class SteamGameReviews  {
	private String review;
	private String gameTitle;
	private String gameReviewsDate;
	private String foundHelpful;
	private String recomendation;
	private String gamehours;
	
	/**
	 * This is a default constructor
	 */
	public SteamGameReviews() {
		
	}
	
	/**
	 * This is a constructor to initialize the fields for gameTitle, review, gameReviewsDate, 
	 * foundHelpful, recommendation & gamehours
	 */
	public SteamGameReviews(String review, String gameTitle, String gameReviewsDate, String foundHelpful,
			String recomendation, String gamehours) {
		super();
		this.gameTitle = gameTitle;
		this.review = review;
		this.gameReviewsDate = gameReviewsDate;
		this.foundHelpful = foundHelpful;
		this.recomendation = recomendation;
		this.gamehours = gamehours;
	}
	/**
	 * Get the Game Title
	 * @return the Game Title in String
	 */
	public String getGameTitle() {
		return gameTitle;
	}
	/**
	 * Set the  Game Title
	 * @param gameTitle is the Game Title in String
	 * @exception IllegalArgumentException if the Game Title is empty
	 */
	public void setGameTitle(String gameTitle) {
		if(gameTitle == null || gameTitle.isEmpty()) 
			throw new IllegalArgumentException("No Game Title found");
		this.gameTitle = gameTitle;
	}
	/**
	 * Get the Game Review 
	 * @return the  Game Review  in String
	 */
	public String getReview() {
		return review;
	}
	/**
	 * Set the  Game Review
	 * @param review is the Game Review in String
	 * @exception IllegalArgumentException if the Game Review is empty
	 */
	public void setReview(String review) {
		this.review = review;
	}
	/**
	 * Get the Game Review Date
	 * @return the Game Review Date in String
	 */
	public String getGameReviewsDate() {
		return gameReviewsDate;
	}
	/**
	 * Set the  Game Reviews Date
	 * @param gameReviewsDate is the Game Reviews Date in String
	 * @exception IllegalArgumentException if the Game Reviews Date is empty
	 */
	public void setGameReviewsDate(String gameReviewsDate) {
		if(gameReviewsDate == null || gameReviewsDate.isEmpty()) 
			throw new IllegalArgumentException("No Review Date found");
		this.gameReviewsDate = gameReviewsDate;
	}
	/**
	 * Get the the number of people found helpful
	 * @return the number of people found helpful in String
	 */
	public String getFoundHelpful() {
		return foundHelpful;
	}
	/**
	 * Set the  number of people found helpful
	 * @param foundHelpful is the number of people found helpful in String format
	 * @exception IllegalArgumentException if the number of people found helpful is empty
	 */
	public void setFoundHelpful(String foundHelpful) {
		if(foundHelpful == null || foundHelpful.isEmpty()) 
			throw new IllegalArgumentException("No Data is found");
		this.foundHelpful = foundHelpful;
	}
	/**
	 * Get the Game Recommendation
	 * @return the Game Recommendation in String
	 */
	public String getRecomendation() {
		return recomendation;
	}
	/**
	 * Set the  Game Recommendation
	 * @param recomendation the Game Recommendation in String
	 * @exception IllegalArgumentException if the Game Recommendation is empty
	 */
	public void setRecomendation(String recomendation) {
		if(recomendation == null || recomendation.isEmpty()) 
			throw new IllegalArgumentException("No Recomendation is found");
		this.recomendation = recomendation;
	}
	/**
	 * Get the Game hour
	 * @return the Game hour in String
	 */
	public String getGamehours() {
		return gamehours;
	}
	/**
	 * Set the  Game Hours
	 * @param gamehours the Game Hours in String
	 * @exception IllegalArgumentException if the Game Hours is empty
	 */
	public void setGamehours(String gamehours) {
		if(gamehours == null || gamehours.isEmpty()) 
			throw new IllegalArgumentException("No Game Hours is found");
		this.gamehours = gamehours;
	}
	
	
}