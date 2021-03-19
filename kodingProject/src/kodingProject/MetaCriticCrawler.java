import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MetaCriticCrawler {

	final String metaCriticUrl = "https://www.metacritic.com";

	public MetaCriticCrawler() {

	}

	// get a list of search results base on game title
	public ArrayList<String> getGameInfo(ArrayList<SteamGames> listOfSteam) throws IOException {
		ArrayList<String> listOfUrl = new ArrayList<String>();

		for (SteamGames s : listOfSteam) {
			String searchMetaCriticUrl = "https://www.metacritic.com/search/game/" + s.getGameTitle() + "/results?plats[3]=1&search_type=advanced";
			Document document = Jsoup.connect(searchMetaCriticUrl).get();
			if (document.getElementsByClass("search_results module").isEmpty()) {
				System.out.println("Not found in metacritic");
				listOfUrl.add("");
			} else {
				Elements searchResult = document.getElementsByClass("search_results module").first().getElementsByClass("product_title basic_stat");
				boolean gameFound = false;
				for (Element a : searchResult) {
					String userReviewUrl = "";
					String developerStudio = "";
					String releaseDate = "";

					String specificMetaCriticGameUrl = metaCriticUrl + a.child(0).attr("href");
					System.out.println(specificMetaCriticGameUrl);
					Document newDoc = Jsoup.connect(specificMetaCriticGameUrl).get();

					Elements findDeveloper = newDoc.getElementsByClass("summary_detail developer");
					developerStudio = findDeveloper.first().child(1).child(0).text();

					Elements findDate = newDoc.getElementsByClass("summary_detail release_data");
					releaseDate = findDate.first().child(1).text();

//					 if developer and release date match set the link
					if ((s.getDeveloper().contains(developerStudio)) && (releaseDate.equals(s.getReleaseDate()))) {
						Elements userReviewLink = newDoc.getElementsByClass("userscore_wrap feature_userscore");
						userReviewUrl = metaCriticUrl + userReviewLink.first().child(1).attr("href"); // set the new URL
						listOfUrl.add(userReviewUrl);
						gameFound = true;
						// System.out.println(userReviewUrl);
						break;
					}
				}
				if (gameFound == false) {
					listOfUrl.add("");
				}
			}
		}
		return listOfUrl;
	}

	public void getReview(String url, String gameTitle, MongoDatabase db) throws IOException {
		MongoCollection<org.bson.Document> reviewCollection = db.getCollection("meta review");

		MongoCollection<org.bson.Document> metaStatsCollection = db.getCollection("meta stats");

		Document userReviewDoc = Jsoup.connect(url).get();

		// get user overall score
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

		if (!scoreResult.equals("tbd")) { // if scoreResult != "tbd" proceed to retrieve review
			// get each review (all comments for first load)
			Elements review = userReviewDoc.getElementsByClass("body product_reviews").first().child(1).children();
			int counter = 0;
			for (Element e : review) {
				String reviewBody = null;
				String reviewScore = null;
				String reviewDate = null;
				org.bson.Document metaReview = new org.bson.Document();
				// if there is a expand on the review, get the expanded child
				if (e.child(0).child(0).child(0).child(0).child(0).child(0).child(1).child(0).childrenSize() == 4) {
					reviewBody = e.child(0).child(0).child(0).child(0).child(0).child(0).child(1).child(0).child(1)
							.text();
				} else {
					reviewBody = e.child(0).child(0).child(0).child(0).child(0).child(0).child(1).child(0).text();
				}
				// get each review score
				reviewScore = e.child(0).child(0).child(0).child(0).child(0).child(0).child(0).child(1).child(0).text();

				// get each review date
				reviewDate = e.child(0).child(0).child(0).child(0).child(0).child(0).child(0).child(0).child(1).text();

				if (reviewBody != "") {
					metaReview.append("gametitle", gameTitle);
					metaReview.append("userreview", reviewBody);
					metaReview.append("userscorescore", reviewScore);
					metaReview.append("reviewdate", reviewDate);
					metaReview.append("reviewcategory", "firstload");
					reviewCollection.insertOne(metaReview);
				} else {
					continue;
				}
				counter++;
				if (counter == 10) {
					break;
				}
			}

			Elements categoryNumber = userReviewDoc.getElementsByClass("score_counts hover_none").first().children();
			String positiveUrl = null;
			String neutralUrl = null;
			String negativeUrl = null;
			String positiveNumber = null;
			String neutralNumber = null;
			String negativeNumber = null;

			// get positive reviews number
			if (categoryNumber.first().child(0).child(1).child(0).childrenSize() == 2) { // if review number is 0,unable
																							// to retrieve href
																							// attribute
				positiveNumber = categoryNumber.get(1).child(0).child(1).child(0).child(0).text();
			} else { // get all positive review
				positiveNumber = categoryNumber.first().child(0).child(1).child(0).child(0).child(0).text();
				positiveUrl = metaCriticUrl + categoryNumber.first().child(0).child(1).child(0).attr("href");
				getCategoryReview(positiveUrl, "positive", gameTitle, reviewCollection);
			}

			// get neutral reviews number
			if (categoryNumber.get(1).child(0).child(1).child(0).childrenSize() == 2) { // if review number is 0, unable
																						// to retrieve href attribute
				neutralNumber = categoryNumber.get(1).child(0).child(1).child(0).child(0).text();
			} else { // get all neutral review
				neutralNumber = categoryNumber.get(1).child(0).child(1).child(0).child(0).child(0).text();
				neutralUrl = metaCriticUrl + categoryNumber.get(1).child(0).child(1).child(0).attr("href");
				getCategoryReview(neutralUrl, "neutral", gameTitle, reviewCollection);
			}

			// get negative reviews number
			if (categoryNumber.get(2).child(0).child(1).child(0).childrenSize() == 2) { // if review number is 0, unable
																						// to retrieve href attribute
				negativeNumber = categoryNumber.get(2).child(0).child(1).child(0).child(0).text();
			} else { // get all the negative review
				negativeNumber = categoryNumber.get(2).child(0).child(1).child(0).child(0).child(0).text();
				negativeUrl = metaCriticUrl + categoryNumber.get(2).child(0).child(1).child(0).attr("href");
				getCategoryReview(negativeUrl, "negative", gameTitle, reviewCollection);
			}

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
			metaStats.append("scoreResult", "tbd");
			metaStatsCollection.insertOne(metaStats);
		}
	}

	// return List<MetaCriticReview>
	public void getCategoryReview(String url, String reviewCategory, String gameTitle, MongoCollection<org.bson.Document> collection) throws IOException {

		Document userReviewDoc = Jsoup.connect(url).get();

		// get each review
		Elements review = userReviewDoc.getElementsByClass("body product_reviews").first().child(1).children();
		int counter = 0;
		for (Element e : review) {
			String reviewBody = null;
			String reviewScore = null;
			String reviewDate = null;
			org.bson.Document metaReview = new org.bson.Document();
			// if there is a expand on the review, get the expanded child
			if (e.child(0).child(0).child(0).child(0).child(0).child(0).child(1).child(0).childrenSize() == 4) {
				reviewBody = e.child(0).child(0).child(0).child(0).child(0).child(0).child(1).child(0).child(1).text();
			} else {
				reviewBody = e.child(0).child(0).child(0).child(0).child(0).child(0).child(1).child(0).text();
			}
			// get each review score
			reviewScore = e.child(0).child(0).child(0).child(0).child(0).child(0).child(0).child(1).child(0).text();

			// get each review date
			reviewDate = e.child(0).child(0).child(0).child(0).child(0).child(0).child(0).child(0).child(1).text();

			if (reviewBody != "") {
				metaReview.append("gametitle", gameTitle);
				metaReview.append("userreview", reviewBody);
				metaReview.append("userscorescore", reviewScore);
				metaReview.append("reviewdate", reviewDate);
				metaReview.append("reviewcategory", reviewCategory);
				collection.insertOne(metaReview);
			} else {
				continue;
			}
			counter++;
			if (counter == 10) {
				break;
			}
		}
	}
}
