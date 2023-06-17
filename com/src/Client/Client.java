package Client;

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
            this.bufferedWriter = new BufferedWriter( new OutputStreamWriter( socket.getOutputStream() ));
            this.bufferedReader = new BufferedReader( new InputStreamReader( socket.getInputStream() ));
            this.userName = userName;
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
                bufferedWriter.write(userName + ": " + messageToSend );
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException err){
            closeEveryThing( socket, bufferedReader, bufferedWriter );
        }
    }

    // critical point for explain because it's
    public void listenForMessage(){
        new Thread(() -> {
            String messageFromGroup;

            while ( socket.isConnected() ){
                try {
                    messageFromGroup = bufferedReader.readLine();
                    System.out.println(messageFromGroup);
                } catch ( IOException err){
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
        System.out.println("Digite seu username for the chat ");
        String username = scanner.nextLine();

        // can take for the user
        Socket socket = new Socket( "localhost", 1234);
        Client client = new Client(socket, username);
        // separate threads !! has while infinite loops
        client.listenForMessage();
        client.sendMessage();

    }
}
