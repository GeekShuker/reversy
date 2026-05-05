/**
 * The Disc interface defines the characteristics of a game in a chess-like game.
 * Implementing classes should provide information about the player who owns the Disc.
 */
public abstract class Disc {

protected Player player;

public Disc (Player player){
    this.player=player;
}
    /**
     * Get the player who owns the Disc.
     *
     * @return The player who is the owner of this game disc.
     */
     public Player getOwner(){
        return player;
     }


    /**
     * Set the player who owns the Disc.
     *
     */
    void setOwner(Player player){
        this.player=player;
    }

    /**
     * Get the type of the disc.
     * use the:
     *          "⬤",         "⭕"                "💣"
     *      Simple Disc | Unflippedable Disc | Bomb Disc |
     * respectively.
     */
   public abstract String getType();

}