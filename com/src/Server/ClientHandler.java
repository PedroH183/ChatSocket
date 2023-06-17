package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    // essa classe é responsável por permitir mais de um user utilizando o server Socket

    // manter o tráfego dos usuarios
    // broadcast message to multiple clients
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    // socket que será passado para o nosso Sever
    private Socket socket;
    // read data sent from the client, sen
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientHandler (Socket socket) throws IOException{
        try{
            this.socket = socket;
            // input == read data , output == can you use send data
            // in java exist two OutputStream == byte stream and char stream
            this.bufferedWriter = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()) );
            this.bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            broadCastMessage("# <Server> " + clientUsername + " entrou no chat #");

        } catch ( IOException err){
            closeEveryThing( socket, bufferedReader, bufferedWriter );
        }
    }


    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                if(messageFromClient == null) throw new IOException();
                broadCastMessage(messageFromClient);
            } catch (IOException err ){
                closeEveryThing( socket, bufferedReader, bufferedWriter );
                break;
            }
        }
    }

    public void broadCastMessage( String messageToSend ){
        for( ClientHandler clientHandler : clientHandlers ){
            try {
                if( !clientHandler.clientUsername.equals(clientUsername) ){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine(); // enter key
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException err){
                closeEveryThing(socket, bufferedReader, bufferedWriter );
            }
        }
    }

    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadCastMessage( "### Server " + clientUsername + " foi de base ###");
    }

    public void closeEveryThing(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();
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


}
