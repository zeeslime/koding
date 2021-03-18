import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class SteamCrawler {

	public static void main(String[] args) throws IOException {
		/*
		ArrayList<String> userReviews = new ArrayList<>();

		Document document = Jsoup.connect("https://www.metacritic.com/game/pc/hitman-3/user-reviews?dist=positive").get();
		Elements reviewContent = document.select(".module.reviews_module.user_reviews_module");
		for (Element reviews : reviewContent) {
			System.out.print(reviews.select(".review_body span.blurb.blurb_expanded").isEmpty());
			if (reviews.select(".review_body span.blurb.blurb_expanded").isEmpty()) {
				userReviews.add(reviews.select(".review_body").text());
			} else
			userReviews.add(reviews.select(".review_body span.blurb.blurb_expanded").text());
		}
		System.out.println(userReviews);
		*/
		
		ArrayList<String> listOfGameLinks = getLinks();
		getGameInfo(listOfGameLinks);

	}

	public static ArrayList<String> getLinks() throws IOException {
		String url = "https://store.steampowered.com/search/?category1=998&filter=topsellers";
		Document document = Jsoup.connect(url).get();
		Elements gameLinks = document.select("div#search_resultsRows > a");
		ArrayList<String> links = new ArrayList<>();
		
		for (Element gameLink : gameLinks) {
			links.add(gameLink.attr("abs:href"));
			System.out.println(links); 
		}
		
		return links;
	}
	
	public static ArrayList<String> getGameInfo(ArrayList<String> listOfGameLinks) throws IOException {
		ArrayList<String> gameTitle = new ArrayList<>();
		ArrayList<String> gameImage = new ArrayList<>();
		ArrayList<String> gameDesc = new ArrayList<>();
		ArrayList<String> gameSumReview = new ArrayList<>();
		ArrayList<String> gameReleaseDate = new ArrayList<>();
		ArrayList<String> gamePrice = new ArrayList<>();
		ArrayList<String> gameReviews = new ArrayList<>();
		ArrayList<String> gameReviewsDate = new ArrayList<>();
		ArrayList<String> foundHelpful = new ArrayList<>();
		ArrayList<String> recomendation = new ArrayList<>();
		ArrayList<String> gamehours = new ArrayList<>();
		
		String matureAge = "568022401";
		String matureContent = "1";
		
		//bypass age check and mature content check
		Connection.Response resp;
		resp = Jsoup.connect("https://store.steampowered.com/agecheck/sub/504022")
        .userAgent("Mozilla/5.0")
        .timeout(10 * 1000)
        .method(Method.POST)
        .data("ageDay", "1")
        .data("ageMonth", "1")
        .data("ageYear", "1955") 
        .execute();
		
		//get cookies for mature content 
		final Map<String, String> mcookies = resp.cookies();
		resp.cookies().put("birthtime", matureAge);
		resp.cookies().put("mature_content", matureContent);
		//print the cookies, we'll see birthtime and mature_content cookies here
	    System.out.println(mcookies);
	    
		Connection.Response res;
		
		//retrieve game info
		for (String link : listOfGameLinks) {
			if(link.contains("https://store.steampowered.com/sub/")) {
				continue;
			}
			res = Jsoup.connect(link)
					.cookies(mcookies)
					.execute();
		
		String[] stringArray = link.split("/");
		String productID = stringArray[4];
		System.out.println(productID);

			
		//parse the document from response
		Document dc = res.parse(); 
		//get cookies

		String title = dc.select("div.apphub_AppName").text();
		gameTitle.add(title);
		System.out.println(gameTitle); 

		//gameTitle.add(dc.select("div.apphub_AppName").toString());
		for (Element img : dc.select("div.game_header_image_ctn img")) {
			gameImage.add(img.attr("src"));
		}
		System.out.println(gameImage);
		gameDesc.add(dc.select("div.game_description_snippet").text());
		System.out.println(gameDesc);
		if (dc.select(".glance_ctn .user_reviews span.game_review_summary").isEmpty() != true) {
			gameSumReview.add(dc.select(".glance_ctn .user_reviews span.game_review_summary").first().text());
		} else 
			gameSumReview.add("No user reviews");

		System.out.println(gameSumReview);
		gameReleaseDate.add(dc.select("div.date").text());
		System.out.println(gameReleaseDate);
		
		if (dc.select("div.game_purchase_price").isEmpty() != true) {
			gamePrice.add(dc.select("div.game_purchase_price").first().text());
		} else
			gamePrice.add(dc.select("div.discount_final_price").first().text());
		System.out.println(gamePrice);
		
		//retrieve reviews 
		String reviewLink = "http://steamcommunity.com/app/" + productID + "/reviews/?browsefilter=trendmonth&p=1";
		Document revDoc = Jsoup.connect(reviewLink).get();
		Elements revEss  = revDoc.select(".apphub_Card");
		for (Element reviews : revEss) {
			foundHelpful.add(reviews.select(".found_helpful").text());
			recomendation.add(reviews.select(".vote_header .title").text());
			gamehours.add(reviews.select(".vote_header .hours").text());
			gameReviewsDate.add(reviews.select(".apphub_CardTextContent .date_posted").text());
			gameReviews.add(reviews.select(".apphub_CardTextContent").first().ownText());
		}
		System.out.println(gameReviews);
	 }
		return gameTitle;
	}
	

}

