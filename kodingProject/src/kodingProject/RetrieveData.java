package kodingProject;

import java.util.ArrayList;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
/**
 * This is the RetrieveData Class for retrieving data from database for Steam/MetaCritic
 * @author Wei Xiang, Chang Hua, Zhan An,Jing Wei
 */
public class RetrieveData {
	
	/**
	 * Get the Database Tables for MetaCritic
	 * @return the listOfMeta Array list in String 
	 */
	public static ArrayList<SteamGames> retrieveSteam(MongoDatabase db) {

		//retrieve database table
		MongoCollection<org.bson.Document> gameInfoCollection = db.getCollection("game info"); 
		MongoCollection<org.bson.Document> steamReviewCollection = db.getCollection("steam review");
		MongoCursor<org.bson.Document> gameCursor = gameInfoCollection.find().iterator();
		ArrayList<SteamGames> gameList = new ArrayList<>();
		
		// loop through the game info table 
		try {
			while (gameCursor.hasNext()) {
				SteamGames game = new SteamGames();
				org.bson.Document gameDoc = gameCursor.next();
				// store game info from database into SteamGames obj
				game.setGameTitle(gameDoc.get("gameTitle").toString());
				game.setProductID(Integer.parseInt(gameDoc.get("productID").toString()));
				game.setDesc(gameDoc.get("gameDesc").toString());
				game.setDeveloperStudio(gameDoc.get("developer").toString());
				game.setImg(gameDoc.get("gameImage").toString());
				game.setPrice(gameDoc.get("gamePrice").toString());
				game.setReleaseDate(gameDoc.get("gameReleaseDate").toString());
				game.setSumReview(gameDoc.get("gameSumReview").toString());
				
                // retrieve list of reviews from database based on game title
                ArrayList<org.bson.Document> steamReviewDoc = new ArrayList<org.bson.Document>(); 
                // retrieve the data that has the same game title
                steamReviewCollection.find(Filters.eq("gameTitle", gameDoc.get("gameTitle"))).into(steamReviewDoc);
        		ArrayList<SteamGameReviews> listOfSteamReview = new ArrayList<SteamGameReviews>();
                for (org.bson.Document doc : steamReviewDoc) {
                    SteamGameReviews steamReview = new SteamGameReviews();
	                steamReview.setFoundHelpful(doc.get("foundHelpful").toString());
	                steamReview.setGamehours(doc.get("gamehours").toString());
	                steamReview.setGameReviewsDate(doc.get("gameReviewsDate").toString());
	                steamReview.setGameTitle(doc.get("gameTitle").toString());
	                steamReview.setRecomendation(doc.get("recomendation").toString());
	                steamReview.setReview(doc.get("gameReview").toString());
	                // add the steamreviews into an arraylist of SteamGameReviews
	                listOfSteamReview.add(steamReview);
                }
                // Add the arraylist of SteamGameReviews to the Steam Games
                game.setListOfSteamReviews(listOfSteamReview);
                // Add the games info into the game arraylist
				gameList.add(game);
			}
		} finally {
			gameCursor.close();
		}
		return gameList;
	}
	/**
	 * Get the Database Tables for MetaCritic
	 * @return the listOfMeta Array list in String 
	 */
	public static ArrayList<MetaCritic> retrieveMeta(MongoDatabase db, ArrayList<SteamGames> gameList) {
		
		//retrieve database table
		MongoCollection<org.bson.Document> statsCollection = db.getCollection("meta stats");
		MongoCollection<org.bson.Document> reviewCollection = db.getCollection("meta review");
		
		ArrayList<MetaCritic> listOfMeta = new ArrayList<MetaCritic>(); //store a list of MetaCritic
		
		//loop base on list of steam games
		for (SteamGames s : gameList) {
			MetaCritic mc = new MetaCritic();
			//retrieve the data that has the same game title
			org.bson.Document doc = statsCollection.find(Filters.eq("gametitle", s.getGameTitle())).first();
			//if doccument is not empty start assigning it into the MetaCritic Object
			if (!doc.equals(null)) {
				String gameTitle = doc.get("gametitle").toString();
				mc.setGameTitle(gameTitle);

				mc.setReleaseDate(s.getReleaseDate());
				mc.setDeveloperStudio(s.getDeveloperStudio());
				
				//if overall score is "tbd" mean either game not found or no reviews set all to 0
				String overallScoreString = doc.get("scoreresult").toString();
				if (overallScoreString.equals("tbd")) {
					mc.setUserGameScore(0);
					mc.setPositiveReview(0);
					mc.setNeutralReview(0);
					mc.setNegativeReview(0);
				} else { // if overall game score is not "tbd"
					//converting the values into the correct data type and set to the object
					double overallScore = Double.parseDouble(overallScoreString);
					mc.setUserGameScore(overallScore);

					String positiveString = doc.get("positive").toString().replace(",", "");
					int positive = Integer.parseInt(positiveString);
					mc.setPositiveReview(positive);

					String neutralString = doc.get("neutral").toString().replace(",", "");
					int neutral = Integer.parseInt(neutralString);
					mc.setNeutralReview(neutral);

					String negativeString = doc.get("negative").toString().replace(",", "");
					int negative = Integer.parseInt(negativeString);
					mc.setNegativeReview(negative);
					//end of converting
					
					//start of retrieving metacritic review
					ArrayList<org.bson.Document> reviewDoc = new ArrayList<org.bson.Document>();
					//get all document that equal to the game title
					reviewCollection.find(Filters.eq("gametitle", s.getGameTitle())).into(reviewDoc);
					ArrayList<MetaCriticReview> listOfReview = new ArrayList<MetaCriticReview>(); //store all the reviews
					//loop base on how many document are there
					for (org.bson.Document d : reviewDoc) { //for each review store into listOfReview
						MetaCriticReview mcr = new MetaCriticReview();

						String review = d.get("userreview").toString();
						mcr.setReviewText(review);

						String reviewDate = d.get("reviewdate").toString();
						mcr.setReviewDate(reviewDate);

						String reviewCategory = d.get("reviewcategory").toString();
						mcr.setReviewCategory(reviewCategory);

						String reviewScoreString = d.get("userscorescore").toString();
						int reviewScore = Integer.parseInt(reviewScoreString);
						mcr.setReviewScore(reviewScore);
						listOfReview.add(mcr); //add into listOfReview after setting
					}
					mc.setListOfReviews(listOfReview); //set the listOfReview into the mc object
				}
			}
			listOfMeta.add(mc); //add to the list that contain all metacritic
		}
		return listOfMeta; //return list of metacritic
	}
}
