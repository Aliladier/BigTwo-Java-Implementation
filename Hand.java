/**
 * The Hand class is a subclass of the CardList class and is used to model a hand of cards. It has
 * a private instance variable for storing the player who plays this hand. It also has methods for
 * getting the player of this hand, checking if it is a valid hand, getting the type of this hand,
 * getting the top card of this hand, and checking if it beats a specified hand.
 *
 * @author ZHU Lingxiao
 */

public abstract class Hand extends CardList{
    private CardGamePlayer player; // a private instance variable for storing the player who plays this hand

    /**
     * a constructor for building a hand with the specified player and list of cards
     *
     * @param player who plays this hand
     * @param cards the hand the player is going to play
     */
    public Hand(CardGamePlayer player, CardList cards){
        this.player = player;

        if (cards != null){
            for (int i = 0; i < cards.size(); i++){
                this.addCard(cards.getCard(i));
            }
        }
    }

    /**
     * a method for retrieving the player of this hand.
     *
     * @return the player of this hand.
     */
    public CardGamePlayer getPlayer(){
        return this.player;
    }

    /**
     * a method for retrieving the top card of this hand.
     *
     * @return the top card of this hand.
     */
    public Card getTopCard(){
        Card topCard = this.getCard(0);
        for (int i = 1; i < this.size(); i++){
            if (this.getCard(i).compareTo(topCard) > 0){
                topCard = this.getCard(i);
            }
        }
        //single, pair, triple, quad, straight, flush, straight flush
        //fullhouse, flush to be over
        return topCard;
    }

    /**
     * a method for checking if this hand beats a specified hand. Would be overridden by each subclass case-by-case
     *
     * @param hand the hand the player is going to play
     * @return whether this hand beats a specified hand or not
     */
    public boolean beats(Hand hand){
        return false;
    }

    /**
     * a method for checking if this is a valid hand. Would be implemented by each subclass
     *
     * @return the type of the subclass
     */
    public abstract String getType();

    /**
     * a method for returning a string specifying the type of this hand. would be implemented by each subclass
     *
     * @return whether the hand is a valid hand or not
     */
    public abstract boolean isValid();


}
