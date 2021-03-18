import java.util.ArrayList;
import java.util.List;

public class MetaCritic {
	
	private double userGameScore;
	private int positiveReview;
	private int mixedReview;
	private int negativeReview;
	private ArrayList<MetaCriticReview> listOfReviews;

	public MetaCritic() {

	}

	public double getUserGameScore() {
		return userGameScore;
	}

	public void setUserGameScore(double userGameScore) {
		this.userGameScore = userGameScore;
	}

	public int getPositiveReview() {
		return positiveReview;
	}

	public void setPositiveReview(int positiveReview) {
		this.positiveReview = positiveReview;
	}

	public int getMixedReview() {
		return mixedReview;
	}

	public void setMixedReview(int mixedReview) {
		this.mixedReview = mixedReview;
	}

	public int getNegativeReview() {
		return negativeReview;
	}

	public void setNegativeReview(int negativeReview) {
		this.negativeReview = negativeReview;
	}

	public List<MetaCriticReview> getListOfReviews() {
		return listOfReviews;
	}

	public void setListOfReviews(ArrayList<MetaCriticReview> listOfReviews) {
		this.listOfReviews = listOfReviews;
	}

}