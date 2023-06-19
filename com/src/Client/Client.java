package Client;

import ConsoleColors.ConsoleColors;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String userName;

    public Client( Socket socket, String userName ){
        try{
            this.socket = socket;
            this.userName = userName;
            this.bufferedWriter = new BufferedWriter( new OutputStreamWriter( socket.getOutputStream() ));
            this.bufferedReader = new BufferedReader( new InputStreamReader( socket.getInputStream() ));
        } catch (IOException err){
            closeEveryThing( socket , bufferedReader, bufferedWriter );
        }
    }

    public void sendMessage(){
        try{
            bufferedWriter.write(userName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);

            while( socket.isConnected() ){
                String messageToSend = scanner.nextLine();
                messageToSend = ConsoleColors.MessageWColor(ConsoleColors.YELLOW, userName + ": ", messageToSend);
                bufferedWriter.write( messageToSend );
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException err){
            closeEveryThing( socket, bufferedReader, bufferedWriter );
        }
    }

    // listen message from group
    public void listenForMessage(){
        new Thread( () -> {
            String messageFromGroup;

            while ( socket.isConnected() ){
                try {
                    messageFromGroup = bufferedReader.readLine();
                    messageFromGroup = ConsoleColors.MessageWColor(ConsoleColors.YELLOW, ">> ", messageFromGroup);
                    System.out.println(messageFromGroup);
                } catch ( Exception err){
                    closeEveryThing( socket, bufferedReader, bufferedWriter );
                }
            }
        }).start();
    }

    public void closeEveryThing(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try {
            if( bufferedReader != null ){
                bufferedReader.close();
            }
            if ( bufferedWriter != null ){
                bufferedWriter.close();
            }
            if ( socket != null ){
                socket.close();
            }
        } catch ( IOException err ){
            err.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite seu Username para o chat ");
        String username = scanner.nextLine();

        // can take for the user
        Socket socket = new Socket( "localhost", 1234);
        Client client = new Client(socket, username);
        // separate threads !! has while infinite loops
        client.listenForMessage();
        client.sendMessage();
    }
}
