package kodingProject;


/**
 * This is the Game Parent Class for retrieving "Game Title", "Release Date" & "Developer Studio"
 * @author Zhan An, Wei Xiang, Jing Wei, Chang Hua
 */

public class Game {
	private String gameTitle;
	private String releaseDate;
	private String developerStudio;
	
	/**
	 * Get the Title of the Game
	 * @return the gameTitle in String
	 */
	public String getGameTitle() {
		return gameTitle;
	}
	/**
	 * Set the Title of the Game
	 * @param gameTitle is the Title of the Game in String
	* @exception IllegalArgumentException if the game title is empty
	 */
	public void setGameTitle(String gameTitle) {
		if(gameTitle == null || gameTitle.isEmpty()) 
			throw new IllegalArgumentException("No Game Title");
		this.gameTitle = gameTitle;
	}
	/**
	 * Get the Release Date of the Game
	 * @return the releaseDate in String
	 */
	public String getReleaseDate() {
		return releaseDate;
	}
	/**
	 * Set the Release Date of the Game
	 * @param releaseDate is the Release Date in String
	* @exception IllegalArgumentException if the Release Date is empty
	 */
	public void setReleaseDate(String releaseDate) {
		if(releaseDate == null || releaseDate.isEmpty()) 
			throw new IllegalArgumentException("No Release Date");
		this.releaseDate = releaseDate;
	}
	/**
	 * Get the Developer Studio of the Game
	 * @return the developerStudio in String 
	 */
	public String getDeveloperStudio() {
		return developerStudio;
	}
	/**
	 * Set the Developer Studio of the Game
	 * @param developerStudio is the Developer Studio in String
	* @exception IllegalArgumentException if the Developer Studio is empty
	 */
	public void setDeveloperStudio(String developerStudio) {
		if(developerStudio == null || developerStudio.isEmpty()) 
			throw new IllegalArgumentException("No Developer Studio");
		this.developerStudio = developerStudio;
	}
}

