package kodingProject;

import java.util.ArrayList;
/**
 * This is a SteamGames Class for retrieving additional details "product ID", "Game Image", "Game Description", 
 * "Summary Review" &  "Game Release Date", "Price" & " list of Steam Game Reviews"
 * 
 * @author Zhan An, Wei Xiang, Jing Wei, Chang Hua
 */
public class SteamGames extends Game{
	private int productID;
	private String img;
	private String desc;
	private String sumReview;
	private String releaseDate;
	private String price;
	private ArrayList<SteamGameReviews> listOfSteamReviews;

	/**
	 * This is a constructor to initialize the fields for productID, img, desc, sumReview, releaseDate, 
	 * price & listOfSteamReviews
	 */
	public SteamGames(int productID, String img, String desc, String sumReview, String releaseDate, String price,
			ArrayList<SteamGameReviews> listOfSteamReviews) {
		super();
		this.productID = productID;
		this.img = img;
		this.desc = desc;
		this.sumReview = sumReview;
		this.releaseDate = releaseDate;
		this.price = price;
		this.listOfSteamReviews = listOfSteamReviews;
	}

	/**
	 * This is a default constructor
	 */
	public SteamGames() {
		
	}
	/**
	 * Get the Product ID 
	 * @return the Product ID  in integer
	 */
	public int getProductID() {
		return productID;
	}
	/**
	 * Set the number of Product ID 
	 * @param productID is the unique number of a particular Game Product
	 * @exception IllegalArgumentException if the number of Product ID is negative 
	 */
	public void setProductID(int productID) {
		if(productID<=0)
			throw new IllegalArgumentException("Illegal Count");
		this.productID = productID;
	}
	/**
	 * Get the Game Image
	 * @return the Game Image in String
	 */
	public String getImg() {
		return img;
	}
	/**
	 * Set the  Game Image   
	 * @param img is the Game Image  in String
	 * @exception IllegalArgumentException if the Game Image is empty
	 */
	public void setImg(String img) {
		if(img == null || img.isEmpty()) 
			throw new IllegalArgumentException("No Game Image is found");
		this.img = img;
	}
	/**
	 * Get the Game Description
	 * @return the Game Description in String
	 */
	public String getDesc() {
		return desc;
	}
	/**
	 * Set the  Game Description   
	 * @param desc is the Game Description  in String
	 * @exception IllegalArgumentException if the Game Description is empty
	 */
	public void setDesc(String desc) {
		if(desc == null || desc.isEmpty()) 
			throw new IllegalArgumentException("No Game Description found");
		this.desc = desc;
	}
	/**
	 * Get the Game Summary Review
	 * @return the Game Summary Review in String
	 */
	public String getSumReview() {
		return sumReview;
	}
	/**
	 * Set the  Game Sum Review  
	 * @param sumReview is the Game Sum Review in String
	 * @exception IllegalArgumentException if the Game Sum Review is empty
	 */
	public void setSumReview(String sumReview) {
		if(sumReview == null || sumReview.isEmpty()) 
			throw new IllegalArgumentException("No Summary Review found");
		this.sumReview = sumReview;
	}
	/**
	 * Get the Game Release Date
	 * @return the Game Release Date in String
	 */
	public String getReleaseDate() {
		return releaseDate;
	}
	/**
	 * Set the  Game Release Date 
	 * @param releaseDate is the Game Release Date in String
	 * @exception IllegalArgumentException if the Game Release Date is empty
	 */
	public void setReleaseDate(String releaseDate) {
		if(releaseDate == null || releaseDate.isEmpty()) 
			throw new IllegalArgumentException("No Game Release Date found");
		this.releaseDate = releaseDate;
	}
	/**
	 * Get the Game Price
	 * @return the Game Price in String
	 */
	public String getPrice() {
		return price;
	}
	/**
	 * Set the  Game Price 
	 * @param price is the Game Price in String
	 * @exception IllegalArgumentException if the Game Price is empty
	 */
	public void setPrice(String price) {
		if(price == null || price.isEmpty()) 
			throw new IllegalArgumentException("No Game Price is found");
		this.price = price;
	}
	/**
	 * Get the List Of Steam Reviews
	 * @return the List Of Steam Reviews in an Array list
	 */
	public ArrayList<SteamGameReviews> getListOfSteamReviews() {
		return listOfSteamReviews;
	}
	/**
	 * Set the list of Steam Game Reviews in an Array list
	 * @param listOfSteamReviews is an Array list of multiple reviews
	 * @exception IllegalArgumentException if the number of array list is empty/null
	 */
	public void setListOfSteamReviews(ArrayList<SteamGameReviews> listOfSteamReviews) {
		if(listOfSteamReviews == null || listOfSteamReviews.isEmpty()) 
			throw new IllegalArgumentException("No list of steam reviews found");
		this.listOfSteamReviews = listOfSteamReviews;
	}

}