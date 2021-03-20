package kodingProject;


/**
 * This is a MetaCriticReview Class for retrieving "Review Score", "Review Text", "Review Category" & "Review Date"
 * @author Zhan An, Wei Xiang, Jing Wei, Chang Hua
 */
public class MetaCriticReview {
	
	private int reviewScore;
	private String reviewText;
	private String reviewCategory;
	private String reviewDate; 	
	
	/**
	 * This is a default constructor
	 */
	public MetaCriticReview()
	{
		
	}
	/**
	 * Get the Game Review Score
	 * @return the Game Review Score in integer
	 */
	public int getReviewScore() {
		return reviewScore;
	}
	/**
	 * Set the  Game Review Score
	 * @param reviewScore the Game review score in integer
	 * @exception IllegalArgumentException if the game review score if negative or zero
	 */
	public void setReviewScore(int reviewScore) {	
		if(reviewScore<=0 || reviewScore> 100)
			throw new IllegalArgumentException("Invalid Score");
		this.reviewScore = reviewScore;
	}
	/**
	 * Get the Review Text
	 * @return the Review Text in string format
	 */
	public String getReviewText() {
		return reviewText;
	}
	/**
	 * Set the  Review Text
	 * @param reviewText the review text in words
	 * @exception IllegalArgumentException if the review text is empty
	 */
	public void setReviewText(String reviewText) {
		if(reviewText == null || reviewText.isEmpty()) 
			throw new IllegalArgumentException("No Review from user");
		this.reviewText = reviewText;
	}
	/**
	 * Get the Review Category
	 * @return the Review Category in string format
	 */
	public String getReviewCategory() {
		return reviewCategory;
	}
	/**
	 * Set the  Review Category
	 * @param reviewCategory the review category in word
	 * @exception IllegalArgumentException if the review category is empty
	 */
	public void setReviewCategory(String reviewCategory) {
		if(reviewCategory == null || reviewCategory.isEmpty()) 
			throw new IllegalArgumentException("No Review category");
		this.reviewCategory = reviewCategory;
	}
	/**
	 * Get the Review Date
	 * @return the Review Date in string format
	 */
	public String getReviewDate() {
		return reviewDate;
	}
	/**
	 * Set Review Date in string format
	 * @param reviewDate is the review date in years [1,100]
	 * @exception IllegalArgumentException if the date is zero, negative, or more than 100
	 */
	public void setReviewDate(String reviewDate) {
		if(reviewDate == null || reviewDate.isEmpty()) 
			throw new IllegalArgumentException("No Review Date");
		this.reviewDate = reviewDate;
	}
}
