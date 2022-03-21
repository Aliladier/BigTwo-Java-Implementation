import javax.swing.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

/**
 * The BigTwoClient class implements the NetworkGame interface. It is used to model a Big
 * Two game client that is responsible for establishing a connection and communicating with
 * the Big Two game server.
 *
 * @author ZHU Lingxiao
 */

public class BigTwoClient implements NetworkGame{
    private BigTwo game; //a BigTwo object for the Big Two card game
    private BigTwoGUI gui; //a BigTwoGUI object for the Big Two card game.
    private Socket sock; //a socket connection to the game server.
    private ObjectOutputStream oos; //an ObjectOutputStream for sending messages to the server.
    private int playerID; //an integer specifying the playerID (i.e., index) of the local player.
    private String playerName; //a string specifying the name of the local player.
    private String serverIP; //a string specifying the IP address of the game server.
    private int serverPort; //an integer specifying the TCP port of the game server.
    private int activePlayerNumber = 0;// an integer specifying the number of active players in the current game;
    private boolean connectStatus = false; // a boolean value specifying the connection status of the client;

    /**
     * a constructor for creating a Big Two client.
     *
     * @param game is a reference to a BigTwo object associated with this client
     * @param gui is a reference to a BigTwoGUI object associated the BigTwo object.
     */
    public BigTwoClient(BigTwo game, BigTwoGUI gui){
        this.game = game;
        this.gui = gui;

        //get user name input
        String name = (String) JOptionPane.showInputDialog(null,"Please enter your name: ", "Welcome To BigTwo", JOptionPane.INFORMATION_MESSAGE);

        //assign a value if the user didn't input a name
        if (name == null || name.equals("")){
            int rIdx = (int) (Math.random() * 100);
            name = "Anonymous" + rIdx;
        }
        this.playerName = name;

        //get the server port input
        String serverPort = (String) JOptionPane.showInputDialog(null,"TCP Port", "Welcome To BigTwo", JOptionPane.INFORMATION_MESSAGE);

        //assign a value if the user didn't input the server port
        if (serverPort == null || serverPort.equals("")){
            serverPort = "2396";
        }

        this.setServerPort(Integer.parseInt(serverPort));
        this.setServerIP("127.0.0.1");

        this.connect();
    }

    /**
     * a method for getting the number of active players in the current game;
     *
     * @return the number of active players in the current game;
     */
    public int getActivePlayerNumber(){
        return activePlayerNumber;
    }

    /**
     * a method for getting the playerID (i.e., index) of the local player.
     *
     * @return the playerID (i.e., index) of the local player.
     */
    public int getPlayerID(){
        return this.playerID;
    }

    /**
     * a method for getting the connection status of the client;
     *
     * @return the connection status of the client;
     */
    public boolean getConnectStatus(){
        return this.connectStatus;
    }

    /**
     * a method for setting the playerID (i.e., index) of
     * the local player.
     *
     * @param playerID the playerID (i.e., index) of
     * the local player.
     */
    public void setPlayerID(int playerID){
        this.playerID = playerID;
    }

    /**
     * a method for getting the name of the local player.
     *
     * @return the name of the local player.
     */
    public String getPlayerName(){
        return this.playerName;
    }

    /**
     * a method for setting the name of the local
     * player.
     *
     * @param playerName the name of the local
     * player.
     */
    public void setPlayerName(String playerName){
        this.playerName = playerName;
    }

    /**
     * a method for getting the IP address of the game server.
     *
     * @return the IP address of the game server.
     */
    public String getServerIP(){
        return this.serverIP;
    }

    /**
     * a method for setting the IP address of the game
     * server.
     *
     * @param serverIP the IP address of the game
     * server.
     */
    public void setServerIP(String serverIP){
        this.serverIP = serverIP;
    }

    /**
     * a method for getting the TCP port of the game server.
     *
     * @return the TCP port of the game server.
     */
    public int getServerPort(){
        return this.serverPort;
    }

    /**
     * a method for setting the TCP port of the game
     * server.
     *
     * @param serverPort the TCP port of the game
     * server.
     */
    public void setServerPort(int serverPort){
        if(serverPort <= 1024){
            serverPort = 8888;
        }
        this.serverPort = serverPort;
    }

    /**
     * a method for making a socket connection with the game server.
     */
    public void connect(){
        try{
            this.sock = new Socket(serverIP,serverPort);
            this.oos = new ObjectOutputStream(sock.getOutputStream());

            Thread serverHandler = new Thread(new ServerHandler());
            serverHandler.start();

            System.out.println("Networking established.");

            sendMessage(new CardGameMessage(CardGameMessage.JOIN,-1, this.getPlayerName()));

        }catch (Exception e){
            System.out.println("Failed to connect.");
            int choice = JOptionPane.showConfirmDialog(null, "Server is not responding, please reconnect.", "Error", JOptionPane.OK_OPTION);
            if (choice == JOptionPane.OK_OPTION){
                connect();
            }
        }
    }

    /**
     * a method for parsing the messages received from the game server;
     *
     * @param message the message received from the game server
     */
    public synchronized void parseMessage(GameMessage message){
        int messagePlayerID = message.getPlayerID();
        int messageType = message.getType();

        switch (messageType){
            case CardGameMessage.PLAYER_LIST:
                this.setPlayerID(messagePlayerID);
                this.gui.setClientIdx(messagePlayerID);
                String[] playerNames = (String[]) message.getData();
                int count = 0;
                for (int i = 0; i < playerNames.length; i++){
                    if (i == this.getPlayerID()){
                        this.game.getPlayerList().get(i).setName(this.playerName);
                    }
                    if (playerNames[i] != null){
                        this.game.getPlayerList().get(i).setName(playerNames[i]);
                        count++;
                    }
                }
                this.activePlayerNumber = count;
                this.gui.setPlayerNumber(count);
                this.gui.repaint();
                break;

            case CardGameMessage.JOIN:
                if (messagePlayerID == this.playerID){
                    this.connectStatus = true;
                    this.gui.printMsg(this.playerName + " joined the game.\n");
                    sendMessage(new CardGameMessage(CardGameMessage.READY,-1,null));
                }
                else{
                    String newPlayerName = (String) message.getData();
                    this.game.getPlayerList().get(messagePlayerID).setName(newPlayerName);
                    this.gui.printMsg(newPlayerName + " joined the game.\n");
                }
                this.gui.repaint();
                break;

            case CardGameMessage.FULL:
                this.gui.printMsg("The server is full and cannot join the game.\n");
                try {
                    sock.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                this.gui.repaint();
                break;

            case CardGameMessage.QUIT:
                activePlayerNumber--;
                if(activePlayerNumber < 0){
                    activePlayerNumber = 0;
                }
                this.gui.setPlayerNumber(activePlayerNumber);

                String quitName = this.game.getPlayerList().get(messagePlayerID).getName();
                this.gui.printMsg(quitName + " quited the game.\n");
                this.game.getPlayerList().get(messagePlayerID).setName("");
                if (!this.game.endOfGame()){
                    for (CardGamePlayer player : this.game.getPlayerList()){
                        player.removeAllCards();
                    }
                    sendMessage(new CardGameMessage(CardGameMessage.READY,-1,null));
                    this.gui.printMsg("Game stops.\n");
                    this.gui.printMsg("Waiting for others to join.\n");
                }
                this.gui.repaint();
                break;

            case CardGameMessage.READY:
                activePlayerNumber++;
                if (activePlayerNumber > 4){
                    activePlayerNumber = 4;
                }
                this.gui.setPlayerNumber(activePlayerNumber);

                String messageName = this.game.getPlayerList().get(messagePlayerID).getName();
                this.gui.printMsg(messageName + " is ready.\n");
                this.gui.repaint();
                break;

            case CardGameMessage.START:
                BigTwoDeck deck = (BigTwoDeck) message.getData();
                this.gui.printMsg("Starting the game.\n");
                this.game.start(deck);
                this.gui.repaint();
                break;

            case CardGameMessage.MOVE:
                this.game.checkMove(messagePlayerID,(int[]) message.getData());
                this.gui.repaint();
                break;

            case CardGameMessage.MSG:
                String chatMessage = (String) message.getData();
                this.gui.printChatMsg(chatMessage);
                break;

            default:
                this.gui.printMsg("Received message can't be parsed.\n");
        }

    }

    /**
     * a method for sending the specified message to the game server.
     *
     * @param message the specified message to be sent to the game server.
     */
    public synchronized void sendMessage(GameMessage message){
        try{
            oos.writeObject(message);
            oos.flush();
            System.out.println("Message Sent.");
        }catch(Exception e){
            System.out.println("Failed to send message.");
            e.printStackTrace();
        }
    }

    // an inner class that implements the Runnable interface, for receiving the message from the server.
    private class ServerHandler implements Runnable{
        private ObjectInputStream ois;

        /**
         * a method from the runnable interface to handle messages from the server.
         */
        public void run(){
            try {
                this.ois = new ObjectInputStream(sock.getInputStream());
                while(!sock.isClosed()){
                    CardGameMessage msg = (CardGameMessage) ois.readObject();
                    if(msg != null){
                        parseMessage(msg);
                    }
                }
                ois.close();
            }catch (Exception e){
                ois = null;
                e.printStackTrace();
            }
        }
    }
}
