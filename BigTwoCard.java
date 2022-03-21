/**
 *The BigTwoCard class is a subclass of the Card class and is used to model a card used in a
 * Big Two card game.
 *
 * @author ZHU Lingxiao
 */

public class BigTwoCard extends Card{

    /**
     * a constructor for building a card with the specified
     * suit and rank.
     *
     * @param suit is an integer between 0 and 3 representing the suit of the card
     * @param rank is an integer between 0 and 12 representing the rank of the card
     */
    public BigTwoCard(int suit, int rank){
        super(suit, rank);
    }

    /**
     * a method for comparing the order of this card with the
     * specified card.
     *
     * @param card the card to be compared
     * @return a negative integer, zero, or a positive integer when this card is
     * less than, equal to, or greater than the specified card.
     */
    public int compareTo(Card card){
        int thisRank = this.rank;
        int cardRank = card.rank;

        if (thisRank < 2){
            thisRank += 13;
        }

        if (cardRank < 2){
            cardRank += 13;
        }

        if (thisRank > cardRank){
            return 1;
        }
        else if (thisRank < cardRank){
            return -1;
        }
        else{
            if(this.getSuit() > card.getSuit()){
                return 1;
            }
            else if (this.getSuit() < card.getSuit()){
                return -1;
            }
            else {
                return 0;
            }
        }

    }

}
