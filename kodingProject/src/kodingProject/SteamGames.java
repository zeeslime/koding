package kodingProject;

import java.util.ArrayList;

public class SteamGames extends Game{
	private int productID;
	private String img;
	private String desc;
	private String sumReview;
	private String releaseDate;
	private String price;
	private ArrayList<SteamGameReviews> listOfSteamReviews;

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

	public SteamGames() {
		
	}

	public int getProductID() {
		return productID;
	}

	public void setProductID(int productID) {
		this.productID = productID;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getSumReview() {
		return sumReview;
	}

	public void setSumReview(String sumReview) {
		this.sumReview = sumReview;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public ArrayList<SteamGameReviews> getListOfSteamReviews() {
		return listOfSteamReviews;
	}

	public void setListOfSteamReviews(ArrayList<SteamGameReviews> listOfSteamReviews) {
		this.listOfSteamReviews = listOfSteamReviews;
	}

	

}