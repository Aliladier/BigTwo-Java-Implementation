import java.util.*;

/**
 * The Flush class is a subclass of the Hand class and is used to model a hand of straight flush.
 *
 * @author ZHU Lingxiao
 */
public class StraightFlush extends Hand{

    /**
     * a constructor for building a hand of full house with the specified player and list of cards
     *
     * @param player who plays this hand
     * @param cards the hand the player is going to play
     */
    public StraightFlush(CardGamePlayer player, CardList cards){
        super(player, cards);
    }

    /**
     * a method for checking if this is a valid hand.
     *
     * @return the type of the subclass
     */
    public String getType(){
        return "StraightFlush";
    }

    /**
     * a method for checking if this is a valid hand.
     *
     * @return whether this is a valid hand or not
     */
    public boolean isValid(){
        if(this.size() < 5 || this.size() > 5){
            return false;
        }

        int firstSuit = this.getCard(0).getSuit();
        for (int i = 1; i < this.size(); i++){
            if(this.getCard(i).getSuit() != firstSuit){
                return false;
            }
        }

        ArrayList<Integer> ranks = new ArrayList<>();
        for (int i = 0; i < this.size(); i++){
            int rank = this.getCard(i).getRank();

            if(rank < 2){
                rank += 13;
            }

            ranks.add(rank);
        }

        Collections.sort(ranks);

        int firstRank = ranks.get(0);
        for (int i = 0; i < this.size(); i++){
            int properRank = firstRank + i;
            int actualRank = ranks.get(i);
            if(actualRank != properRank){
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
            return true;
        }

        if(this.getTopCard().compareTo(hand.getTopCard()) > 0){
            return true;
        }
        else{
            return false;
        }
    }
}
