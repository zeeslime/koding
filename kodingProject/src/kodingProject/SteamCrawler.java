package kodingProject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.client.MongoCollection;


public class SteamCrawler {

	public SteamCrawler() {
		
	}
	
	public static ArrayList<String> getLinks() throws IOException {

		final String url = "https://store.steampowered.com/search/?category1=998&filter=topsellers";
		Document document = loadDocument(url);
		Elements gameLinks = document.select("div#search_resultsRows > a");
		ArrayList<String> links = new ArrayList<>();
		
		for (Element gameLink : gameLinks) {
			links.add(gameLink.attr("abs:href"));
			System.out.println(links); 
		}
		
		return links;
	}
	
	public static void getGameInfo(ArrayList<String> listOfGameLinks, MongoCollection<org.bson.Document> gameInfoCollection, MongoCollection<org.bson.Document> steamReviewCollection) throws IOException, HttpStatusException{
		String gameTitle = null;
		int productID = 0;
		String ageCheckLink = "https://store.steampowered.com/agecheck/sub/504022";
		
		org.bson.Document steamStats = new org.bson.Document();
        
		String matureAge = "568022401";
		String matureContent = "1";
		
		//bypass age check and mature content check
		Connection.Response resp;
		
		resp = Jsoup.connect(ageCheckLink)
        .userAgent("Mozilla/5.0")
        .timeout(10 * 1000)
        .method(Method.POST)
        .data("ageDay", "1")
        .data("ageMonth", "1")
        .data("ageYear", "1955") 
        .execute();
		
		// get cookies for mature content 
		final Map<String, String> mcookies = resp.cookies();
		resp.cookies().put("birthtime", matureAge);
		resp.cookies().put("mature_content", matureContent);
	    
		Connection.Response res;
		// retrieve game info
		for (String link : listOfGameLinks) {
			if(link.contains("https://store.steampowered.com/sub/")) {
				continue;
			}
			res = Jsoup.connect(link)
					.cookies(mcookies)
					.execute();
		
		// get steam product id
		String[] stringArray = link.split("/");
		productID = Integer.parseInt(stringArray[4]);
		steamStats.append("productID", productID);
		System.out.println(productID);

		//parse the document from response
		Document dc = res.parse(); 
		
		// retrieve game info from steam
		// get game title
		gameTitle = dc.select("div.apphub_AppName").text();
		steamStats.append("gameTitle", dc.select("div.apphub_AppName").text());

		//gameTitle.add(dc.select("div.apphub_AppName").toString());
		for (Element img : dc.select("div.game_header_image_ctn img")) {
			steamStats.append("gameImage", img.attr("src"));
		}
		
		// get game description
		steamStats.append("gameDesc", dc.select("div.game_description_snippet").text());
		
		//get game summary review
		if (dc.select(".glance_ctn .user_reviews span.game_review_summary").isEmpty() != true) {
			steamStats.append("gameSumReview", dc.select(".glance_ctn .user_reviews span.game_review_summary").first().text());
		} else 
			steamStats.append("gameSumReview", "No user reviews");
		
		// Get game release date
		steamStats.append("gameReleaseDate", dc.select("div.date").text());
		
		// Get game developer
		steamStats.append("developer", dc.getElementById("developers_list").text());
		
		if (dc.select("div.game_purchase_price").isEmpty() != true) {
			steamStats.append("gamePrice", dc.select("div.game_purchase_price").first().text());
		} else
			steamStats.append("gamePrice", dc.select("div.discount_final_price").first().text());
		
		System.out.println(steamStats);
		gameInfoCollection.insertOne(steamStats);
		steamStats.clear();
		System.out.println("empty?" +steamStats);
		
		// retrieve reviews from steam
		org.bson.Document steamReview = new org.bson.Document();
		String reviewLink = "http://steamcommunity.com/app/" + productID + "/reviews/?browsefilter=trendmonth&p=1";
		Document revDoc = Jsoup.connect(reviewLink).get();
		
		if (revDoc.select(".apphub_CardContentMain").isEmpty() == false){
			Elements revEss  = revDoc.select(".apphub_CardContentMain");
			for (Element reviews : revEss.select(".apphub_UserReviewCardContent")) {
				
				steamReview.append("gameTitle", gameTitle);
				steamReview.append("gameReview", (reviews.select(".apphub_CardTextContent").first().ownText()));
				steamReview.append("foundHelpful", (reviews.select(".found_helpful").first().childNodes().get(0).toString().stripLeading()));
				steamReview.append("recomendation", (reviews.select(".vote_header .title").text()));
				steamReview.append("gamehours", (reviews.select(".vote_header .hours").text()));
				steamReview.append("gameReviewsDate", (reviews.select(".apphub_CardTextContent .date_posted").text()));
				
				System.out.println(steamReview);
				steamReviewCollection.insertOne(steamReview);
				steamReview.clear();
			}
		} else {
			System.out.println("No reviews Availble");
			steamReview.append("gameTitle", gameTitle);
			steamReview.append("gameReview", "No reviews yet");
			steamReview.append("foundHelpful", "Null");
			steamReview.append("recomendation", "Null");
			steamReview.append("gamehours", "Null");
			steamReview.append("gameReviewsDate", "Null");
			
			System.out.println(steamReview);
			steamReviewCollection.insertOne(steamReview);
			steamReview.clear();
		}
		
	 }
	}
	
	//this method is to load document, if exception caught retry and fetch again
		public static Document loadDocument(String url) throws IOException {
			Document load;
			//if url caught by an exception, connect and get the url again
			do {
				try {
					load = Jsoup.connect(url).get();
				} catch (HttpStatusException e) {
					System.out.println("HttpStatus error fetching: " + url);
					System.out.println("Retrying to fetch..." + url);
					continue;
				}
				catch (SocketTimeoutException e) {
					System.out.println("Timeout fetching: " + url);
					System.out.println("Retrying to fetch..." + url);
					continue;
				}
				break; //break the while loop, if connect is successful
			} while (true);
			return load;
		}
}