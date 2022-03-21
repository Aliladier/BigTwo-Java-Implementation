/**
 * The Flush class is a subclass of the Hand class and is used to model a hand of straight flush.
 *
 * @author ZHU Lingxiao
 */

public class Triple extends Hand{

    /**
     * a constructor for building a hand of full house with the specified player and list of cards
     *
     * @param player who plays this hand
     * @param cards the hand the player is going to play
     */
    public Triple(CardGamePlayer player, CardList cards){
        super(player,cards);
    }

    /**
     * a method for checking if this is a valid hand.
     *
     * @return the type of the subclass
     */
    public String getType(){
        return "Triple";
    }

    /**
     * a method for checking if this is a valid hand.
     *
     * @return whether this is a valid hand or not
     */
    public boolean isValid(){
        if(this.size() != 3){
            return false;
        }

        int firstRank = this.getCard(0).getRank();
        for (int i = 1; i < this.size(); i++){
            int rank = this.getCard(i).getRank();
            if(firstRank != rank){
                return false;
            }
        }
        return true;
    }

    /**
     * a method for checking if this hand beats a specified hand.
     *
     * @param hand the hand the player is going to play
     * @return whether this hand beats a specified hand or not
     */
    public boolean beats(Hand hand){
        if(hand == null){
            return false;
        }
        if(!hand.getType().equals(this.getType())){
            return false;
        }

        if(this.isValid() && hand.isValid() && this.getTopCard().compareTo(hand.getTopCard()) > 0){
            return true;
        }
        else{
            return false;
        }
    }
}
