import java.util.*;

/**
 * The Flush class is a subclass of the Hand class and is used to model a hand of straight.
 *
 * @author ZHU Lingxiao
 */

public class Straight extends Hand{
    /**
     * a constructor for building a hand of full house with the specified player and list of cards
     *
     * @param player who plays this hand
     * @param cards the hand the player is going to play
     */
    public Straight(CardGamePlayer player, CardList cards){
        super(player, cards);
    }

    /**
     * a method for checking if this is a valid hand.
     *
     * @return the type of the subclass
     */
    public String getType(){
        return "Straight";
    }

    /**
     * a method for checking if this is a valid hand.
     *
     * @return whether this is a valid hand or not
     */
    public boolean isValid(){
        if (this.size() != 5) {
            return false;
        }

        ArrayList<Integer> ranks = new ArrayList<>();
        for (int i = 0; i < this.size(); i++){
            Card card = this.getCard(i);
            int rank = card.getRank();

            if(rank < 2){
                rank += 13;
            }
            ranks.add(rank);
        }

        Collections.sort(ranks);

        int firstRank = ranks.get(0);
        for (int i = 1; i < ranks.size(); i++){
            int properRank = firstRank + i;
            int actualRank = ranks.get(i);
            if(properRank != actualRank){
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
        if(hand.size() < 5){
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
