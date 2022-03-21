import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

/**
 * The BigTwoGUI class implements the CardGameUI interface. It is used to build a GUI for
 * the Big Two card game and handle all user actions.
 *
 * @author ZHU Lingxiao
 */

public class BigTwoGUI implements CardGameUI{
    private BigTwo game; // a BigTwo object;
    private boolean[][] selected; // a 2-D array to store whether a player's card is selected;
    private int activePlayer = 0; // the player who should play at the current round;
    private JFrame frame; // the JFrame for holding the panel of the game;
    private JPanel bigTwoPanel; // the panel for holding the game;
    private JButton playButton; // the button for playing the hand;
    private JButton passButton; // the button for pass;
    private JTextArea msgArea; // the JTextArea for holding the game status;
    private JTextArea chatArea; // the JTextArea for holding the chat messages;
    private JTextField chatInput; // the JTextField for getting the input chat messages;
    private BigTwoClient client;// a BigTwoClient object representing the client;
    private int clientIdx; // an integer representing the client user's player index;
    private int playerNumber; // an integer representing the number of players in the current game;

    private Image[][] cardImages; // the 2-D array for holding the card images
    private Image cardBack; // the back of the card;
    private Image[] avatar; // the array for holding the avatars；

    /**
     * a constructor for creating a BigTwoGUI.
     *
     * @param game a reference to a Big Two card game associates with this GUI
     */
    public BigTwoGUI(BigTwo game){
        //initialize the instance variables
        this.game = game;

        this.selected = new boolean[4][13];

        this.msgArea = new JTextArea(15,50);
        this.chatArea = new JTextArea(15,50);
        this.chatInput = new JTextField(19);
        this.bigTwoPanel = new BigTwoPanel();

        //set the frame
        frame = new JFrame("BigTwo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800,830));
        frame.setBackground(new Color(0,153,76));
        frame.setLayout(null);
        frame.add(bigTwoPanel);

        //get the images of the cards and avatars
        this.cardImages = new Image[4][13];
        this.getCardImages();
        this.avatar = new Image[4];
        this.getAvatar();

        //set the buttons
        passButton = new JButton("Pass");
        passButton.addActionListener(new PassButtonListener());

        playButton = new JButton("Play");
        playButton.addActionListener(new PlayButtonListener());
        bigTwoPanel.setBackground(new Color(0,153,76));

        bigTwoPanel.setSize(800,830);
        bigTwoPanel.setLayout(null);

        bigTwoPanel.add(playButton);
        bigTwoPanel.add(passButton);

        playButton.setLocation(150,755);
        playButton.setSize(new Dimension(60,25));
        playButton.setBackground(new Color(0,153,76));

        passButton.setLocation(200,755);
        passButton.setSize(new Dimension(60,25));
        passButton.setBackground(new Color(0,153,76));

        //set the text areas
        msgArea.setWrapStyleWord(true);
        msgArea.setLineWrap(true);
        msgArea.setEditable(false);
        msgArea.append("Welcome to BigTwo!\n");
        JScrollPane msgPane = new JScrollPane(msgArea);
        msgPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        msgPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        chatArea.setWrapStyleWord(true);
        chatArea.setLineWrap(true);
        chatArea.setEditable(false);
        chatArea.setForeground(Color.BLUE);
        //chatArea.setPreferredSize(new Dimension(440,8000));
        //chatArea.setMaximumSize(new Dimension(440,375));
        chatArea.append("Chat with your opponents!\n");
        JScrollPane chatPane = new JScrollPane(chatArea);
        chatPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        chatPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel textPane = new JPanel();
        bigTwoPanel.add(textPane);
        textPane.setSize(360,749);
        textPane.setLocation(440,0);

        textPane.add(msgPane);
        textPane.add(chatPane);

        msgPane.setLocation(5,0);
        msgPane.setPreferredSize(new Dimension(320,375));
        chatPane.setLocation(5,400);
        chatPane.setPreferredSize(new Dimension(320,360));


        ChatInputPanel chatInputPanel = new ChatInputPanel();
        JLabel msgLabel = new JLabel("Msg here: ");
        msgLabel.setLocation(0,650);
        chatInputPanel.setBackground(new Color(0,153,76));
        bigTwoPanel.add(chatInputPanel);
        chatInputPanel.setSize(new Dimension(400,30));
        chatInputPanel.setLocation(400,755);
        chatInput.setSize(new Dimension(300,20));
        chatInput.setLocation(455,650);
        chatInput.setEditable(true);
        chatInput.setEnabled(true);
        chatInput.addActionListener(new ChatInputListener());
        chatInputPanel.add(msgLabel);
        chatInputPanel.add(chatInput);

        //set the menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setSize(800,50);
        JMenu control = new JMenu("Game");
        JMenuItem restart = new JMenuItem("Connect");
        restart.addActionListener(new RestartListener());
        JMenuItem quit = new JMenuItem("Quit");
        quit.addActionListener(new QuitListener());
        control.add(restart);
        control.add(quit);
        menuBar.add(control);
        menuBar.setLocation(0,0);
        frame.setJMenuBar(menuBar);
        this.enable();

        frame.setVisible(true);
    }

    /**
     * a method for setting the client of the game;
     *
     * @param client the client for the game;
     */
    public void setClient(BigTwoClient client){
        this.client = client;
    }

    /**
     * a method for setting the client index;
     *
     * @param clientIdx the index of the client;
     */
    public void setClientIdx(int clientIdx){
        this.clientIdx = clientIdx;
    }

    /**
     * a method for setting the number of players in the game;
     *
     * @param number number of players in the game.
     */
    public void setPlayerNumber(int number){
        this.playerNumber = number;
    }

    /**
     * The method for getting the images of the avatars;
     */
    public void getAvatar(){
        String avatarPath = "avatar";
        for (int i = 0; i < game.getPlayerList().size(); i++){
            String avatarName = avatarPath + i + ".jpeg";
            Image img = new ImageIcon(avatarName).getImage();
            avatar[i] = img;
        }
    }

    /**
     * The method for getting the images of the avatars;
     */
    public void getCardImages(){
        char[] suits = {'d','c','h','s'};
        char[] ranks = {'a', '2', '3', '4', '5', '6', '7', '8', '9', 't', 'j', 'q', 'k' };
        String cardPath = "cards/";

        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 13; j++){
                String cardName = cardPath + ranks[j] + suits[i] + ".gif";
                Image card = new ImageIcon(cardName).getImage();
                cardImages[i][j] = card;
            }
        }

        cardBack = new ImageIcon(cardPath + "b.gif").getImage();
    }

    /**
     * Sets the index of the active player.
     *
     * @param activePlayer the index of the active player (i.e., the player who can
     *                     make a move)
     */
    public void setActivePlayer(int activePlayer){
        if (activePlayer < 0 || activePlayer >= game.getPlayerList().size()) {
            this.activePlayer = -1;
        } else {
            this.activePlayer = activePlayer;
        }
    }

    /**
     * Prints the specified string to the GUI.
     *
     * @param msg the string to be printed to the UI
     */
    public void printMsg(String msg){
        this.msgArea.append(msg);
        this.msgArea.append("\n");
        msgArea.setCaretPosition(msgArea.getDocument().getLength());
    }

    /**
     * Prints the chat message to the GUI.
     *
     * @param msg the string to be printed.
     */
    public void printChatMsg(String msg){
        this.chatArea.append(msg + '\n');
        this.chatArea.append("\n");
    }

    /**
     * Resets the list of selected cards to an empty list.
     */
    public void resetSelected(){
        for (int i = 0; i < selected.length;i++){
            for (int j = 0; j < selected[i].length; j++){
                selected[i][j] = false;
            }
        }
    }

    /**
     * Clears the message area of the card game user interface.
     */
    public void clearMsgArea(){
        this.msgArea.setText("Welcome to BigTwo!\n");
    }

    /**
     * Resets the card game user interface.
     */
    public void reset(){
        this.clearMsgArea();
        this.chatArea.setText(null);
        this.resetSelected();
    }

    /**
     * Repaints the user interface.
     */
    public void repaint(){
        frame.repaint();
        //below is for debugging uses
        /*
        if (bigTwoPanel != null){
            frame.remove(bigTwoPanel);
        }

        passButton = new JButton("Pass");
        passButton.addActionListener(new PassButtonListener());

        playButton = new JButton("Play");
        playButton.addActionListener(new PlayButtonListener());
        bigTwoPanel.setBackground(new Color(0,153,76));

        frame.setLayout(null);

        frame.add(bigTwoPanel);

        bigTwoPanel.setSize(800,800);
        bigTwoPanel.setLayout(null);
        bigTwoPanel.add(playButton);
        bigTwoPanel.add(passButton);

         playButton.setLocation(150,755);
         playButton.setSize(new Dimension(60,25));
         playButton.setBackground(new Color(0,153,76));
         passButton.setLocation(200,755);
         passButton.setSize(new Dimension(60,25));
         passButton.setBackground(new Color(0,153,76));
        bigTwoPanel = new BigTwoPanel();
        frame.add(bigTwoPanel);
         */
    }

    /**
     * Enables user interactions.
     */
    public void enable() {
        this.playButton.setEnabled(true);
        this.passButton.setEnabled(true);
        this.chatInput.setEnabled(true);
        this.chatInput.setEditable(true);
    }


    /**
     * Disables user interactions.
     */
    public void disable() {
        this.playButton.setEnabled(false);
        this.passButton.setEnabled(false);
        this.chatInput.setEnabled(false);
        this.chatInput.setEditable(false);
    }

    /**
     * Prompts active player to select cards and make his/her move.
     */
    public void promptActivePlayer(){
        if (client.getPlayerID() == game.getCurrentPlayerIdx()){
            this.enable();
        }
        else{
            this.disable();
        }
        printMsg(game.getPlayerList().get(game.getCurrentPlayerIdx()).getName() + "'s turn: \n");
        resetSelected();
    }

    /**
     * Returns an array of indices of the cards selected through the GUI.
     *
     * @return an array of indices of the cards selected, or null if no valid cards
     *         have been selected
     */
    public int[] getSelected(){
        int count = 0;
        int[] cardIdx = null;

        for (int i = 0; i < selected[activePlayer].length; i++){
            if(selected[activePlayer][i]) {
                count++;
            }
        }

        //System.out.println(count);
        if(count != 0){
            cardIdx = new int[count];
            count = 0;
            for (int i = 0; i < selected[activePlayer].length; i++){
                if(selected[activePlayer][i]) {
                    cardIdx[count] = i;
                    count++;
                }
            }
        }
        //below is for debugging uses
        /*
        if(cardIdx != null){
            for (int i = 0; i < cardIdx.length; i++){
                if(i == count - 1){
                    System.out.println(cardIdx[i]);
                }
                else {
                    System.out.print(cardIdx[i] + " ");
                }
            }
        }
         */
        return cardIdx;
    }

    /*
     * Inner class for the panel handling click events;
     */
    private class BigTwoPanel extends JPanel implements MouseListener {
        /**
         * constructs the panel;
         */
        private BigTwoPanel(){
            this.addMouseListener(this);
        }

        /**
         * Paints the components in the panel;
         *
         * @param g the Graphics object to protect
         */
        public void paintComponent(Graphics g) {
            if (activePlayer == -1){
                for (int i = 1; i < 6; i++) {
                    g.drawLine(0, 150 * i, 800, 150 * i);
                }

                for (int i = 0; i < game.getPlayerList().size(); i++){
                    CardGamePlayer player = game.getPlayerList().get(i);

                    if(i != client.getPlayerID()) {
                        g.drawString(player.getName(), 20, 20 + 150 * i);
                    }
                    else{
                        g.drawString("You",20, 20 + 150 * i);
                    }

                    g.drawImage(avatar[i],20, 47 + 150 * i, this);

                    for (int j = 0; j < player.getNumOfCards(); j++){
                        Card card = player.getCardsInHand().getCard(j);
                        Image img = cardImages[card.getSuit()][card.getRank()];
                        g.drawImage(img,115 + 17 * j,40 + 150 * i,this);

                    }
                }

                ArrayList<Hand> handsOnTable = game.getHandsOnTable();
                if(handsOnTable.size() != 0) {
                    Hand preHand = handsOnTable.get(handsOnTable.size() - 1);
                    g.drawString("Played by " + preHand.getPlayer().getName() + ": " + preHand.getType() + " " + preHand,20,620);
                    for (int i = 0; i < preHand.size(); i++){
                        preHand.sort();
                        Image img = cardImages[preHand.getCard(i).getSuit()][preHand.getCard(i).getRank()];
                        g.drawImage(img, 10 + i * 77,647,this);
                    }
                }
            }
            else {
                for (int i = 1; i < 6; i++) {
                    g.drawLine(0, 150 * i, 800, 150 * i);
                }

                for (int i = 0; i < playerNumber; i++){
                    g.drawImage(avatar[i], 20, 47 + 150 * i, this);

                    CardGamePlayer player = game.getPlayerList().get(i);
                    if (player.getName() != null && !player.getName().equals("")){
                        if (i != clientIdx) {
                            if (i == activePlayer) {
                                g.drawString(player.getName() + " (current player)", 20, 20 + 150 * i);
                            }
                            else{
                                g.drawString(player.getName(), 20, 20 + 150 * i);
                            }
                        }
                        else {
                            if(i == activePlayer) {
                                g.drawString("You (current player)", 20, 20 + 150 * i);
                            }
                            else{
                                g.drawString("You", 20, 20 + 150 * i);
                            }
                        }
                    }
                }

                for (int i = 0; i < game.getPlayerList().size(); i++) {
                    CardGamePlayer player = game.getPlayerList().get(i);
                    if (player.getName() != null && !player.getName().equals("")) {
                        for (int j = 0; j < player.getNumOfCards(); j++) {
                            if (i != clientIdx) {
                                g.drawImage(cardBack, 115 + 17 * j, 47 + 150 * i, this);
                            } else if (i == clientIdx && !selected[i][j]) {
                                Card card = player.getCardsInHand().getCard(j);
                                Image img = cardImages[card.getSuit()][card.getRank()];
                                g.drawImage(img, 115 + 17 * j, 47 + 150 * i, this);
                            } else if (i == clientIdx && selected[i][j]) {
                                Card card = player.getCardsInHand().getCard(j);
                                Image img = cardImages[card.getSuit()][card.getRank()];
                                g.drawImage(img, 115 + 17 * j, 40 + 150 * i, this);
                            }
                        }
                    }
                }

                ArrayList<Hand> handsOnTable = game.getHandsOnTable();
                if (handsOnTable.size() != 0) {
                    Hand preHand = handsOnTable.get(handsOnTable.size() - 1);
                    g.drawString("Played by " + preHand.getPlayer().getName() + ": " + preHand.getType() + " " + preHand, 20, 620);
                    for (int i = 0; i < preHand.size(); i++) {
                        preHand.sort();
                        Image img = cardImages[preHand.getCard(i).getSuit()][preHand.getCard(i).getRank()];
                        g.drawImage(img, 10 + i * 77, 647, this);
                    }
                } else {
                    g.drawString("[Empty]", 20, 620);
                }
            }


        }

        /**
         * Invoked when a mouse button has been released on a component.
         *
         * @param e the event to be processed
         */
        public void mouseReleased(MouseEvent e){
            if(!game.endOfGame()) {

                int x = e.getX();
                int y = e.getY();

                //version 3
                if (x >= 115 && x <= 173 + 15 + 17 * (game.getPlayerList().get(activePlayer).getNumOfCards() - 1)){
                    int cardIdx = (x - 115) / 17;
                    if (cardIdx > game.getPlayerList().get(activePlayer).getNumOfCards() - 1) {
                        cardIdx = game.getPlayerList().get(activePlayer).getNumOfCards() - 1;
                    }

                    if(!selected[activePlayer][cardIdx]){
                        if (y >= activePlayer * 150 + 40 && y < activePlayer * 150 + 47){
                            cardIdx -= 1;
                            if (cardIdx >= 0) {
                                if (selected[activePlayer][cardIdx]) {
                                    selected[activePlayer][cardIdx] = !selected[activePlayer][cardIdx];
                                }else{
                                    cardIdx -= 1;
                                    if(cardIdx >= 0){
                                        if (selected[activePlayer][cardIdx]) {
                                            selected[activePlayer][cardIdx] = !selected[activePlayer][cardIdx];
                                        }else{
                                            cardIdx -= 1;
                                            if(cardIdx >= 0) {
                                                if (selected[activePlayer][cardIdx]) {
                                                    selected[activePlayer][cardIdx] = !selected[activePlayer][cardIdx];
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else if (y >= activePlayer * 150 + 47 && y < activePlayer * 150 + 144){
                            selected[activePlayer][cardIdx] = !selected[activePlayer][cardIdx];
                        }
                    }
                    else{
                        if (y >= activePlayer * 150 + 40 && y < activePlayer * 150 + 137){
                            selected[activePlayer][cardIdx] = !selected[activePlayer][cardIdx];
                        }
                        else if (y >= activePlayer * 150 + 137 && y <= activePlayer * 150 + 144) {
                            cardIdx -= 1;
                            if (cardIdx >= 0){
                                if (!selected[activePlayer][cardIdx]) {
                                    selected[activePlayer][cardIdx] = !selected[activePlayer][cardIdx];
                                } else {
                                    cardIdx -= 1;
                                    if (cardIdx >= 0) {
                                        if (!selected[activePlayer][cardIdx]) {
                                            selected[activePlayer][cardIdx] = !selected[activePlayer][cardIdx];
                                        } else {
                                            cardIdx -= 1;
                                            if (cardIdx >= 0) {
                                                if (!selected[activePlayer][cardIdx]) {
                                                    selected[activePlayer][cardIdx] = !selected[activePlayer][cardIdx];
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    repaint();
                }

                //version 2 big fault
                /*
                if (x >= 115 && x <= 173 + 15 + 17 * (game.getPlayerList().get(activePlayer).getNumOfCards() - 1)){
                    int cardIdx = (x - 115) / 17;
                    if (cardIdx > game.getPlayerList().get(activePlayer).getNumOfCards() - 1) {
                        cardIdx = game.getPlayerList().get(activePlayer).getNumOfCards() - 1;
                    }

                    if(!selected[activePlayer][cardIdx]){
                        if(y > activePlayer * 150 + 47 && y < (activePlayer + 1) * 150 - 6){
                            selected[activePlayer][cardIdx] = !selected[activePlayer][cardIdx];
                        }
                    }
                    else{
                        if(y >= 150 * activePlayer + 40 && y < 150 * activePlayer + 47){
                            selected[activePlayer][cardIdx] = !selected[activePlayer][cardIdx];
                        }
                        else if (y >= 150 * activePlayer + 47 && y < 150 * activePlayer + 120){
                            selected[activePlayer][cardIdx] = !selected[activePlayer][cardIdx];
                        }
                        else if(y >= 150 * activePlayer + 120 && y <= 150 * (activePlayer + 1) - 6){
                            cardIdx -= 1;
                            selected[activePlayer][cardIdx] = !selected[activePlayer][cardIdx];
                        }
                    }
                    repaint();
                }

                 */

                //version1 small fault
                /*
                if (x >= 100 && x <= 173 + 15 + 17 * (game.getPlayerList().get(activePlayer).getNumOfCards() - 1) && y > activePlayer * 150 + 47 && y < (activePlayer + 1) * 150 - 6) {
                    int cardIdx = (x - 115) / 17;
                    if (cardIdx > game.getPlayerList().get(activePlayer).getNumOfCards() - 1) {
                        cardIdx = game.getPlayerList().get(activePlayer).getNumOfCards() - 1;
                    }
                    selected[activePlayer][cardIdx] = !selected[activePlayer][cardIdx];
                    repaint();
                }

                 */


            }
        }

        public void mouseClicked(MouseEvent e){ }

        public void mousePressed(MouseEvent e){ }

        public void mouseEntered(MouseEvent e){ }

        public void mouseExited(MouseEvent e){ }
    }

    /*
     * Inner class for handling the events on the play button
     */
    private class PlayButtonListener implements ActionListener {
        /**
         * Invoked when an action occurs.
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            int[] cardIdx = getSelected();
            if( cardIdx != null ){
                client.sendMessage(new CardGameMessage(CardGameMessage.MOVE,-1,cardIdx));
            } else {
                printMsg("Please select cards to play.\n");
            }

        }
    }

    /*
     * Inner class for the handling the events on the pass button;
     */
    private class PassButtonListener implements ActionListener {
        /**
         * Invoked when an action occurs.
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            client.sendMessage(new CardGameMessage(CardGameMessage.MOVE, -1, null));
        }
    }

    /*
     * Inner class for handling the chat input;
     */
    private class ChatInputListener implements ActionListener{
        /**
         * Invoked when an action occurs.
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e){
            if(activePlayer == game.getCurrentPlayerIdx()){
                /*
                String msg = game.getPlayerList().get(activePlayer).getName() + ": " + chatInput.getText() + "\n";
                chatArea.append(msg);
                 */
                String msg = chatInput.getText();
                client.sendMessage(new CardGameMessage(CardGameMessage.MSG,-1,msg));
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
                chatInput.setText("");
            }
        }
    }

    /*
     * Inner class for handling the events on the panel holding the chat input;
     */
    private class ChatInputPanel extends JPanel implements MouseListener{
        /**
         * Invoked when a mouse button has been released on a component.
         *
         * @param e the event to be processed
         */
        public void mouseReleased(MouseEvent e){
            int x = e.getX();
            int y = e.getY();

        }

        public void mouseClicked(MouseEvent e){ }

        public void mousePressed(MouseEvent e){ }

        public void mouseEntered(MouseEvent e){ }

        public void mouseExited(MouseEvent e){ }
    }

    /*
     * Inner class for handling the events on the restart item;
     */
    private class RestartListener implements ActionListener{
        /**
         * Invoked when an action occurs.
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e){
            if(!client.getConnectStatus()) {
                reset();
                client.connect();
            }
        }
    }

    /*
     * Inner class for handling the events on the quit item;
     */
    private class QuitListener implements ActionListener{
        /**
         * Invoked when an action occurs.
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e){
            System.exit(0);
        }
    }
}
