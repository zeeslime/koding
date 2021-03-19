package kodingProject;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;

public class MetaCriticCrawler {

	final String metaCriticUrl = "https://www.metacritic.com";

	public MetaCriticCrawler() {

	}

	// get a list of search results base on game title
	public Elements searchGame(String gameTitle) throws IOException {
		String metaCriticUrl = "https://www.metacritic.com/search/game/" + gameTitle
				+ "/results?plats[3]=1&search_type=advanced";
		Document document = Jsoup.connect(metaCriticUrl).get();
		if (document.getElementsByClass("search_results module").isEmpty()) {
			System.out.println("Not found in metacritic");
			return null;
		} else {
			Elements searchResult = document.getElementsByClass("search_results module").first()
					.getElementsByClass("product_title basic_stat");
			return searchResult;
		}
	}

	// search for the exact game base on the game title by comparing release date
	// and the developer
	// return the URL for the specific game
	public String getMetaGame(Elements searchResult, String steamDeveloper, String steamDate) throws IOException {

		String userReviewUrl = null;
		String developerStudio = null;
		String releaseDate = null;
		for (Element a : searchResult) {
			String specificMetaCriticGameUrl = metaCriticUrl + a.child(0).attr("href");
			Document newDoc = Jsoup.connect(specificMetaCriticGameUrl).get();

			Elements findDeveloper = newDoc.getElementsByClass("summary_detail developer");
			developerStudio = findDeveloper.first().child(1).child(0).text();

			Elements findDate = newDoc.getElementsByClass("summary_detail release_data");
			releaseDate = findDate.first().child(1).text();

			// if developer and release date match set the link
			if ((steamDeveloper.contains(developerStudio)) && (releaseDate.equals(steamDate))) {
				Elements userReviewLink = newDoc.getElementsByClass("userscore_wrap feature_userscore");
				userReviewUrl = metaCriticUrl + userReviewLink.first().child(1).attr("href"); // set the new URL
				System.out.println(userReviewUrl);
				break;
			}
		}
		return userReviewUrl; // return null if no specific game found
	}

	public void getReview(String url, String gameTitle, DB db) throws IOException {
		DBCollection reviewCollection;
		DBCollection metaStatsCollection;
		if (db.collectionExists("meta review")) {
			reviewCollection = db.getCollection("meta review");
		} else {
			reviewCollection = db.createCollection("meta review", null);
		}

		if (db.collectionExists("meta stats")) {
			metaStatsCollection = db.getCollection("meta stats");
		} else {
			metaStatsCollection = db.createCollection("meta stats", null);
		}
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
				BasicDBObject metaReview = new BasicDBObject();
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
					metaReview.put("gametitle", gameTitle);
					metaReview.put("userreview", reviewBody);
					metaReview.put("userscorescore", reviewScore);
					metaReview.put("reviewdate", reviewDate);
					metaReview.put("reviewcategory", "firstload");
					reviewCollection.insert(WriteConcern.SAFE, metaReview);
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

			BasicDBObject metaStats = new BasicDBObject();
			metaStats.put("gameTitle", gameTitle);
			metaStats.put("scoreresult", scoreResult);
			metaStats.put("positive", positiveNumber);
			metaStats.put("neutral", neutralNumber);
			metaStats.put("negative", negativeNumber);
			metaStatsCollection.insert(WriteConcern.SAFE, metaStats);

		} else { // if score result equal to "tbd"
			BasicDBObject metaStats = new BasicDBObject();
			metaStats.put("gameTitle", gameTitle);
			metaStats.put("scoreResult", "tbd");
			metaStatsCollection.insert(WriteConcern.SAFE, metaStats);
		}
	}

	// return List<MetaCriticReview>
	public void getCategoryReview(String url, String reviewCategory, String gameTitle, DBCollection collection)
			throws IOException {

		Document userReviewDoc = Jsoup.connect(url).get();

		// get each review
		Elements review = userReviewDoc.getElementsByClass("body product_reviews").first().child(1).children();
		int counter = 0;
		for (Element e : review) {
			String reviewBody = null;
			String reviewScore = null;
			String reviewDate = null;
			BasicDBObject metaReview = new BasicDBObject();
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
				metaReview.put("gametitle", gameTitle);
				metaReview.put("userreview", reviewBody);
				metaReview.put("userscorescore", reviewScore);
				metaReview.put("reviewdate", reviewDate);
				metaReview.put("reviewcategory", reviewCategory);
				collection.insert(WriteConcern.SAFE, metaReview);
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
