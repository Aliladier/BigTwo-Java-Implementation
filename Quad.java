/**
 * The Flush class is a subclass of the Hand class and is used to model a hand of quad.
 *
 * @author ZHU Lingxiao
 */

public class Quad extends Hand{

    /**
     * a constructor for building a hand of full house with the specified player and list of cards
     *
     * @param player who plays this hand
     * @param cards the hand the player is going to play
     */
    public Quad(CardGamePlayer player, CardList cards){
        super(player, cards);
    }

    /**
     * a method for checking if this is a valid hand.
     *
     * @return the type of the subclass
     */
    public String getType(){
        return "Quad";
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

        this.sort();

        int firstRank;
        if(this.getCard(0).getRank() == this.getCard(1).getRank()){
            firstRank = this.getCard(0).getRank();
            for (int i = 0; i < 4; i++){
                if(this.getCard(i).getRank() != firstRank){
                    return false;
                }
            }
        }
        else{
            firstRank = this.getCard(1).getRank();
            for (int i = 1; i < 5; i++){
                if(this.getCard(i).getRank() != firstRank){
                    return false;
                }
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
    public boolean beats(Hand hand) {
        if(hand == null){
            return false;
        }
        if (hand.size() < 5) {
            return false;
        }
        if (hand.getType().equals("Straight") || hand.getType().equals("Flush") || hand.getType().equals("FullHouse")){
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
            if(thisCounts[i] == 4){
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
            if(handCounts[i] == 4){
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
