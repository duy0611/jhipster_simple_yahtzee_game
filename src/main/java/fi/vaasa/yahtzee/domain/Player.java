package fi.vaasa.yahtzee.domain;

public class Player {
	
	private String playerName; //name of player
	
	public Player() {}
	
	public Player(String name) {
		this.playerName = name;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	@Override
    public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Player)) {
            return false;
        }
		
		Player player = (Player) o;
		return player.playerName.equals(this.playerName);
	}
	
	@Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + playerName.hashCode();
        return result;
    }
	
	@Override
	public String toString() {
		return "Player name: " + this.playerName;
	}
}
