/**
 * The Flush class is a subclass of the Hand class and is used to model a hand of pair.
 *
 * @author ZHU Lingxiao
 */

public class Pair extends Hand{

    /**
     * a constructor for building a hand of full house with the specified player and list of cards
     *
     * @param player who plays this hand
     * @param cards the hand the player is going to play
     */
    public Pair(CardGamePlayer player, CardList cards){
        super(player, cards);
    }

    /**
     * a method for checking if this is a valid hand.
     *
     * @return the type of the subclass
     */
    public String getType(){
        return "Pair";
    }

    /**
     * a method for checking if this is a valid hand.
     *
     * @return whether this is a valid hand or not
     */
    public boolean isValid(){
        if(this.size() != 2){
            return false;
        }

        int firstRank = this.getCard(0).getRank();
        int secondRank = this.getCard(1).getRank();

        if(firstRank == secondRank){
            return true;
        }
        else {
            return false;
        }
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
        if(!this.getType().equals(hand.getType())){
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
