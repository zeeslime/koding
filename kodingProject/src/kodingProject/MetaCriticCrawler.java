import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MetaCriticCrawler {

	final String metaCriticUrl = "https://www.metacritic.com"; //base URL of metacritic

	public MetaCriticCrawler() {

	}

	//This method search each game title on MetaCritic supplied by the ArrayList<SteamGames>
	//and return an ArrayList<String> of user reviews URL
	public ArrayList<String> getLinks(ArrayList<SteamGames> listOfSteam) throws IOException {
		ArrayList<String> listOfUrl = new ArrayList<String>(); //to store the url
		
		//loop base on the number of steam games
		for (SteamGames s : listOfSteam) {
			//append the search URL filtered with PC Games only
			String searchMetaCriticUrl = "https://www.metacritic.com/search/game/" + s.getGameTitle() + "/results?plats[3]=1&search_type=advanced";
			Document document = loadDocument(searchMetaCriticUrl); //Load the url
			if (document.getElementsByClass("search_results module").isEmpty()) { //if search result is empty add "" into the list
				System.out.println("Not found in metacritic");
				listOfUrl.add("");
			} else { //else if search result is not empty
				Elements searchResult = document.getElementsByClass("search_results module").first().getElementsByClass("product_title basic_stat");
				boolean gameFound = false; //to keep track whether if game is found
				for (Element a : searchResult) { //loop through the search result
					String developerStudio = "";
					String gameTitle = a.child(0).text(); //get the game title
					String specificMetaCriticGameUrl = metaCriticUrl + a.child(0).attr("href"); //get the url for the game

					if (gameTitle.equals(s.getGameTitle())) { //if game title equals to steam game title then add to the list
						listOfUrl.add(specificMetaCriticGameUrl + "/user-reviews");
						gameFound = true;
						break;
					} else { //else if game title not equal to steam game title, access the page to check for more info (accuracy of correct games)
						Document newDoc = loadDocument(specificMetaCriticGameUrl);

						Elements findDeveloper = newDoc.getElementsByClass("summary_detail developer"); //get the developer studio
						developerStudio = findDeveloper.first().child(1).child(0).text();

						//if developer studio contains steam developer studio and vice versa, add URL to the list
						if ((s.getDeveloperStudio().contains(developerStudio)) || (developerStudio.contains(s.getDeveloperStudio()))) {
							listOfUrl.add(specificMetaCriticGameUrl + "/user-reviews");
							gameFound = true;
							break; //break the search result once game is found
						}
					}
				}
				//if game is not found add "" to the list
				if (gameFound == false) {
					listOfUrl.add("");
				}
			}
		}
		return listOfUrl; //return the list of user reviews URL
	}

	//this method will be use to retrieve user reviews and inserting it into database
	public void getGameInfo(String url, String gameTitle, MongoDatabase db) throws IOException {
		
		//creating database table, auto create if it doesn't exist
		MongoCollection<org.bson.Document> reviewCollection = db.getCollection("meta review");
		MongoCollection<org.bson.Document> metaStatsCollection = db.getCollection("meta stats");
		
		//if url is "", which means no result from the search results on metacritic
		if (url.isEmpty()) { 
			org.bson.Document metaStats = new org.bson.Document();
			metaStats.append("gametitle", gameTitle);
			metaStats.append("scoreresult", "tbd");
			metaStatsCollection.insertOne(metaStats);
		} else { //else if url is not empty
			
			Document userReviewDoc = loadDocument(url); //get the document of the user review page

			// retrieving game overall score
			String scoreResult = null;
			if ((userReviewDoc.getElementsByClass("metascore_w user large game negative").first()) != null) {
				Elements score = userReviewDoc.getElementsByClass("metascore_w user large game negative");
				scoreResult = score.first().text();
			} else if ((userReviewDoc.getElementsByClass("metascore_w user large game positive").first()) != null) {
				Elements score = userReviewDoc.getElementsByClass("metascore_w user large game positive");
				scoreResult = score.first().text();
			} else if ((userReviewDoc.getElementsByClass("metascore_w user large game mixed").first()) != null) {
				Elements score = userReviewDoc.getElementsByClass("metascore_w user large game mixed");
				scoreResult = score.first().text();
			} else {
				scoreResult = "tbd";
			}
			//end of retrieving game overall score
			
			if (!scoreResult.equals("tbd")) { // if scoreResult != "tbd" proceed to retrieve review
				// get each review (all comments for first load)
				Elements review = userReviewDoc.getElementsByClass("body product_reviews").select("div.review_content");
				int counter = 0; //for tracking purposes, to ensure it only crawls 10 reviews
				for (Element e : review) { //loop to get each review
					String reviewBody = null;
					String reviewScore = null;
					String reviewDate = null;
					org.bson.Document metaReview = new org.bson.Document();
					// if there is a expand on the review body text, get the expanded child
					if (e.select("div.review_body").first().child(0).hasClass("inline_expand_collapse inline_collapsed")) {
						reviewBody = e.select("div.review_body").first().child(0).getElementsByClass("blurb blurb_expanded").text();
					} else { //else just get the review
						reviewBody = e.select("div.review_body").first().child(0).text();
					}
					// get user review score
					reviewScore = e.select("div.review_grade").first().child(0).text();

					// get user review date
					reviewDate = e.select("div.review_stats").first().children().select("div.date").text();

					if (reviewBody != "") { //if the review is not empty add into database
						metaReview.append("gametitle", gameTitle);
						metaReview.append("userreview", reviewBody);
						metaReview.append("userscorescore", reviewScore);
						metaReview.append("reviewdate", reviewDate);
						metaReview.append("reviewcategory", "most helpful");
						reviewCollection.insertOne(metaReview);
						counter++;
					} else {
						continue; //get next review
					}
					if (counter == 10) { //break the loop, since 10 review had been crawled
						break;
					}
				}
				
				//getting the statistic for the game
				Elements categoryNumber = userReviewDoc.getElementsByClass("score_counts hover_none").first().children();
				String positiveUrl = null;
				String neutralUrl = null;
				String negativeUrl = null;
				String positiveNumber = null;
				String neutralNumber = null;
				String negativeNumber = null;

				// get positive reviews number
				// if review number is 0,unable to retrieve href attribute therefore get the text that is "0"
				if (categoryNumber.first().child(0).child(1).child(0).childrenSize() == 2) { 
					positiveNumber = categoryNumber.get(1).child(0).child(1).child(0).child(0).text();
				} else { // else get all positive review from the href attribute
					positiveNumber = categoryNumber.first().child(0).child(1).child(0).child(0).child(0).text();
					positiveUrl = metaCriticUrl + categoryNumber.first().child(0).child(1).child(0).attr("href");
					getCategoryReview(positiveUrl, "positive", gameTitle, reviewCollection);
				}
				//end of getting postive review number
				
				// get neutral reviews number
				// if review number is 0,unable to retrieve href attribute therefore get the text that is "0"
				if (categoryNumber.get(1).child(0).child(1).child(0).childrenSize() == 2) { 
					neutralNumber = categoryNumber.get(1).child(0).child(1).child(0).child(0).text();
				} else { // get all neutral review from the href attribute
					neutralNumber = categoryNumber.get(1).child(0).child(1).child(0).child(0).child(0).text();
					neutralUrl = metaCriticUrl + categoryNumber.get(1).child(0).child(1).child(0).attr("href");
					getCategoryReview(neutralUrl, "neutral", gameTitle, reviewCollection);
				}
				//end of getting neutral review number

				// get negative reviews number
				// if review number is 0,unable to retrieve href attribute therefore get the text that is "0"
				if (categoryNumber.get(2).child(0).child(1).child(0).childrenSize() == 2) { 
					negativeNumber = categoryNumber.get(2).child(0).child(1).child(0).child(0).text();
				} else { // get all the negative review from the href attribute
					negativeNumber = categoryNumber.get(2).child(0).child(1).child(0).child(0).child(0).text();
					negativeUrl = metaCriticUrl + categoryNumber.get(2).child(0).child(1).child(0).attr("href");
					getCategoryReview(negativeUrl, "negative", gameTitle, reviewCollection);
				}
				//end of getting negative review number
				
				//inserting the statistic into database
				org.bson.Document metaStats = new org.bson.Document();
				metaStats.append("gametitle", gameTitle);
				metaStats.append("scoreresult", scoreResult);
				metaStats.append("positive", positiveNumber);
				metaStats.append("neutral", neutralNumber);
				metaStats.append("negative", negativeNumber);
				metaStatsCollection.insertOne(metaStats);

			} else { // if score result equal to "tbd"
				org.bson.Document metaStats = new org.bson.Document();
				metaStats.append("gametitle", gameTitle);
				metaStats.append("scoreresult", "tbd");
				metaStatsCollection.insertOne(metaStats);
			}
		}
	}

	public void getCategoryReview(String url, String reviewCategory, String gameTitle,MongoCollection<org.bson.Document> collection) throws IOException {
		Document userReviewDoc = loadDocument(url); //load the document 
		
		// get each review
		Elements review = userReviewDoc.getElementsByClass("body product_reviews").select("div.review_content");
		int counter = 0; //for tracking purposes, to ensure it only crawls 10 reviews
		for (Element e : review) { //loop to get each review
			String reviewBody = null;
			String reviewScore = null;
			String reviewDate = null;
			org.bson.Document metaReview = new org.bson.Document();
			// if there is a expand on the review body text, get the expanded child
			if (e.select("div.review_body").first().child(0).hasClass("inline_expand_collapse inline_collapsed")) {
				reviewBody = e.select("div.review_body").first().child(0).getElementsByClass("blurb blurb_expanded").text();
			} else { //else just get the review
				reviewBody = e.select("div.review_body").first().child(0).text();
			}
			// get each review score
			reviewScore = e.select("div.review_grade").first().child(0).text();

			// get each review date
			reviewDate = e.select("div.review_stats").first().children().select("div.date").text();

			if (reviewBody != "") { //if the review is not empty add into database
				metaReview.append("gametitle", gameTitle);
				metaReview.append("userreview", reviewBody);
				metaReview.append("userscorescore", reviewScore);
				metaReview.append("reviewdate", reviewDate);
				metaReview.append("reviewcategory", reviewCategory);
				collection.insertOne(metaReview);
				counter++;
			} else {
				continue; //get next review
			}
			if (counter == 10) { //break the loop, since 10 review had been crawled
				break;
			}
		}
	}
	
	//this method is to load document, if exception caught retry and fetch again
	public Document loadDocument(String url) throws IOException {
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
