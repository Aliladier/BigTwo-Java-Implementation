import javax.swing.*;
import java.util.*;

/**
 * The BigTwo class implements the CardGame interface and is used to model a Big Two card
 * game. It has private instance variables for storing the number of players, a deck of cards, a
 * list of players, a list of hands played on the table, an index of the current player, and a user
 * interface.
 *
 * @author ZHU Lingxiao
 */

public class BigTwo implements CardGame{
    private int numOfPlayers; // private instance variable representing the number of players in the game;
    private Deck deck; // private instance variable representing the deck of cards used in the game;
    private ArrayList<CardGamePlayer> playerList; // private instance variable for storing the players in the game;
    private ArrayList<Hand> handsOnTable; // private instance variable for storing the hands played by the players;
    private int currentPlayerIdx; // private instance variable representing the player who can play the current turn;
    private BigTwoGUI ui; // private instance variable representing the user interface;
    private BigTwoClient client; // a BigTwoClient object for the client;

    /**
     * a constructor for creating a Big Two card game.
     *
     */
    public BigTwo(){
        numOfPlayers = 0;
        deck = new BigTwoDeck();
        playerList = new ArrayList<>();
        handsOnTable = new ArrayList<>();
        currentPlayerIdx = 0;
        for (int i = 0; i < 4; i++){
            playerList.add(new CardGamePlayer());
            numOfPlayers++;
        }

        this.ui = new BigTwoGUI(this);
        this.client = new BigTwoClient(this,this.ui);
        this.ui.setClient(this.client);
    }

    /**
     * a method for getting the number of players.
     *
     * @return the int number of players.
     */
    public int getNumOfPlayers(){
        return this.numOfPlayers;
    }

    /**
     * a method for retrieving the deck of cards being used.
     *
     * @return the deck of cards being used.
     */
    public Deck getDeck(){
        return this.deck;
    }

    /**
     * a method for retrieving the list of players.
     *
     * @return the arraylist of players.
     */
    public ArrayList<CardGamePlayer> getPlayerList(){
        return this.playerList;
    }

    /**
     * a method for retrieving the index of the current player.
     *
     * @return the int index of the current player.
     */

    public int getCurrentPlayerIdx() {
        return currentPlayerIdx;
    }

    /**
     * a method for retrieving the list of hands played on the table.
     *
     * @return the arraylist of hands played on the table.
     */
    public ArrayList<Hand> getHandsOnTable(){
        return this.handsOnTable;
    }

    /**
     * a method for starting/restarting the game with a given shuffled deck of cards.
     *
     * @param deck the deck of (shuffled) cards to be used in this game
     */
    public void start(Deck deck){
        this.deck = deck;
        handsOnTable.clear();
        for (int i = 0; i < numOfPlayers; i++){
            playerList.get(i).removeAllCards();
        }

        for (int i = 0; i < 4; i++){
            for (int j = i * 13; j < i * 13 + 13; j++){
                playerList.get(i).addCard(deck.getCard(j));
                if (deck.getCard(j).getRank() == 2 && deck.getCard(j).getSuit() == 0){
                    currentPlayerIdx = i;
                    this.ui.setActivePlayer(i);
                }
            }
            playerList.get(i).sortCardsInHand();
        }

        this.ui.repaint();

        this.ui.promptActivePlayer();
    }

    /**
     * a method for returning a valid hand from the specified list of cards of the player. Returns null if no
     * valid hand can be composed from the specified list of cards.
     *
     * @param player the card game player who is going to play a hand
     * @param cards the cards the player is going to play
     * @return
     */
    public static Hand composeHand(CardGamePlayer player, CardList cards){
        if (cards.size() > 5){
            return null;
        }
        if (cards.size() == 1){
            return new Single(player, cards);
        }
        if (cards.size() == 2){
            Pair pair = new Pair(player,cards);
            if(pair.isValid()){
                return pair;
            }
            else{
                return null;
            }
        }
        if (cards.size() == 3){
            Triple triple =  new Triple(player,cards);
            if(triple.isValid()){
                return triple;
            }
            else{
                return null;
            }
        }

        StraightFlush straightFlush = new StraightFlush(player, cards);
        Quad quad = new Quad(player,cards);
        FullHouse fullHouse = new FullHouse(player, cards);
        Flush flush = new Flush(player,cards);
        Straight straight = new Straight(player, cards);

        if(straightFlush.isValid()){
            return straightFlush;
        }
        if(quad.isValid()){
            return quad;
        }
        if(fullHouse.isValid()){
            return fullHouse;
        }
        if(flush.isValid()){
            return flush;
        }
        if(straight.isValid()){
            return straight;
        }

        return null;
    }

    /**
     * a method for making a move by a player with the specified index using the cards
     * specified by the list of indices.
     *
     * @param playerIdx the index of the player who makes the move
     * @param cardIdx   the list of the indices of the cards selected by the player
     */
    public void makeMove(int playerIdx, int[] cardIdx){
        checkMove(playerIdx, cardIdx);
    }

    /**
     * a method for checking whether a move made by a player is valid or not.
     *
     * @param playerIdx the index of the player who makes the move
     * @param cardIdx   the list of the indices of the cards selected by the player
     */
    public void checkMove(int playerIdx, int[] cardIdx){
        CardGamePlayer cur_player = playerList.get(playerIdx);
        if(cardIdx == null){
            if (handsOnTable.isEmpty()){
                ui.printMsg("Not a legal move!!!\n");
                ui.promptActivePlayer();
                ui.repaint();
                return;
            }
            else{
                if (handsOnTable.get(handsOnTable.size() - 1).getPlayer().getName().equals(cur_player.getName())){
                    ui.printMsg("Not a legal move!!!\n");
                    ui.promptActivePlayer();
                    ui.repaint();
                    return;
                }
            }

            ui.printMsg("{Pass}\n");
            ui.printMsg("\n");
            currentPlayerIdx = (playerIdx + 1) % 4;
            ui.setActivePlayer(currentPlayerIdx);
            ui.repaint();
            ui.promptActivePlayer();
            return;
        }

        CardList cards = cur_player.play(cardIdx);
        Hand handToPlay = composeHand(cur_player,cards);

        if(handToPlay == null){
            ui.printMsg("Not a legal move!!!\n");
            ui.promptActivePlayer();
            ui.repaint();
            return;
        }

        if (handsOnTable.isEmpty()){
            handsOnTable.add(handToPlay);
            cur_player.removeCards(cards);
            ui.printMsg("{" + handToPlay.getType() + "} " + cards + "\n");

            int next_player = (playerIdx + 1) % 4;
            currentPlayerIdx = next_player;
            ui.setActivePlayer(currentPlayerIdx);
        }
        else{
            Hand pre_hand = handsOnTable.get(handsOnTable.size() - 1);
            if(pre_hand.getPlayer().getName().equals(cur_player.getName())){
                handsOnTable.add(handToPlay);
                cur_player.removeCards(cards);
                ui.printMsg("{" + handToPlay.getType() + "} " + cards + "\n");
                int next_player = (playerIdx + 1) % 4;
                currentPlayerIdx = next_player;
                ui.setActivePlayer(currentPlayerIdx);
            }
            else if (handToPlay.beats(pre_hand)){
                handsOnTable.add(handToPlay);
                cur_player.removeCards(cards);
                ui.printMsg("{" + handToPlay.getType() + "} " + cards + "\n");
                int next_player = (playerIdx + 1) % 4;
                currentPlayerIdx = next_player;
                ui.setActivePlayer(currentPlayerIdx);
            }
            else{
                ui.printMsg("Not a legal move!!!\n");
                ui.promptActivePlayer();
                ui.repaint();
                return;
            }
        }
        ui.repaint();

        if (endOfGame()){
            ui.printMsg("Game ends\n");
            for (int i = 0; i < 4; i++){
                CardGamePlayer player = playerList.get(i);
                if(player.getNumOfCards() != 0){
                    ui.printMsg(player.getName() + " has " + player.getNumOfCards() + " cards in hand.\n");
                }
                else{
                    ui.printMsg(player.getName() + " wins the game.\n");
                }
            }
            ui.setActivePlayer(-1);
            ui.disable();
            ui.promptActivePlayer();
            JOptionPane.showMessageDialog(null,"Game ends.","Game ends.",JOptionPane.INFORMATION_MESSAGE);
            this.client.sendMessage(new CardGameMessage(CardGameMessage.READY,-1, null));
            return;
        }

        ui.promptActivePlayer();
    }

    /**
     * a method for checking if the game ends.
     *
     * @return a boolean value representing whether the game ends or not
     */
    public boolean endOfGame(){
        for (int i = 0; i < 4; i++){
            if(playerList.get(i).getNumOfCards() == 0){
                return true;
            }
        }
        return false;
    }

    /**
     * a method for starting a Big Two card game.
     *
     * @param args
     */
    public static void main(String[] args){
        BigTwo game = new BigTwo();
    }
}

