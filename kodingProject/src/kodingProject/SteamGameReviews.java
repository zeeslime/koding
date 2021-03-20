package kodingProject;

import java.util.ArrayList;

public class SteamGameReviews  {
	private String review;
	private String gameTitle;
	private String gameReviewsDate;
	private String foundHelpful;
	private String recomendation;
	private String gamehours;
	
	public SteamGameReviews() {
		
	}
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
	
	public String getGameTitle() {
		return gameTitle;
	}

	public void setGameTitle(String gameTitle) {
		this.gameTitle = gameTitle;
	}
	
	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public String getGameReviewsDate() {
		return gameReviewsDate;
	}

	public void setGameReviewsDate(String gameReviewsDate) {
		this.gameReviewsDate = gameReviewsDate;
	}

	public String getFoundHelpful() {
		return foundHelpful;
	}

	public void setFoundHelpful(String foundHelpful) {
		this.foundHelpful = foundHelpful;
	}

	public String getRecomendation() {
		return recomendation;
	}

	public void setRecomendation(String recomendation) {
		this.recomendation = recomendation;
	}

	public String getGamehours() {
		return gamehours;
	}

	public void setGamehours(String gamehours) {
		this.gamehours = gamehours;
	}
	
	
}