/**
 * The Flush class is a subclass of the Hand class and is used to model a hand of full house.
 *
 * @author ZHU Lingxiao
 */

public class FullHouse extends Hand{

    /**
     * a constructor for building a hand of full house with the specified player and list of cards
     *
     * @param player who plays this hand
     * @param cards the hand the player is going to play
     */
    public FullHouse(CardGamePlayer player, CardList cards){
        super(player, cards);
    }

    /**
     * a method for checking if this is a valid hand.
     *
     * @return the type of the subclass
     */
    public String getType(){
        return "FullHouse";
    }

    /**
     * a method for checking if this is a valid hand.
     *
     * @return whether this is a valid hand or not
     */
    public boolean isValid(){
        if (this.size() != 5){
            return false;
        }

        int[] counts = new int[13];
        for (int i = 0; i < this.size(); i++){
            int index = this.getCard(i).getRank();
            counts[index]++;
        }

        int numOf3 = 0;
        int numOf2 = 0;
        int numOf0 = 0;
        for (int i = 0; i < 13; i++){
            if(counts[i] == 3){
                numOf3++;
            }
            if (counts[i] == 2){
                numOf2++;
            }
            if(counts[i] == 0){
                numOf0++;
            }
        }

        if(numOf2 == 1 && numOf3 == 1 && numOf0 == 11){
            return true;
        }
        else{
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
        if(hand.size() < 5){
            return false;
        }
        if(hand.getType().equals("Straight") || hand.getType().equals("Flush")){
            return true;
        }
        if(!hand.getType().equals(this.getType())){
            return false;
        }

        int[] thisCounts = new int[13];
        int thisHighestRank = 0;
        for (int i = 0; i < this.size(); i++){
            int index = this.getCard(i).getRank();
            thisCounts[index]++;
        }

        for (int i = 0; i < 13; i++){
            if(thisCounts[i] == 3){
                thisHighestRank = i;
            }
        }

        if(thisHighestRank < 2){
            thisHighestRank += 13;
        }

        int[] handCounts = new int[13];
        for (int i = 0; i < hand.size(); i++){
            int index = hand.getCard(i).getRank();
            handCounts[index]++;
        }

        int handHighestRank = 0;
        for (int i = 0; i < 13; i++){
            if(handCounts[i] == 3){
                handHighestRank = i;
            }
        }

        if(handHighestRank < 2){
            handHighestRank += 13;
        }

        if(thisHighestRank > handHighestRank){
            return true;
        }
        else{
            return false;
        }

    }
}
