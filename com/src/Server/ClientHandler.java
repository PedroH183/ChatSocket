package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    // essa classe é responsável por permitir mais de um user utilizando o server Socket
    // broadcast message to multiple clients
    private Socket socket;
    private String clientUsername;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    public static ArrayList<ClientHandler> connections = new ArrayList<>();

    public ClientHandler (Socket socket) throws IOException {
        try{
            this.socket = socket;
            // convertendo de bytes para caracteres
            this.bufferedWriter = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()) );
            this.bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
            this.clientUsername = bufferedReader.readLine();
            connections.add(this);
            broadCastMessage("<Server> " + clientUsername + " entrou no chat");

        } catch ( IOException err){
            closeEveryThing( socket, bufferedReader, bufferedWriter );
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while ( socket.isConnected() ) {
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
        // comunicando com todos os clients
        for( ClientHandler clientHandler : connections ){
            try {
                // só propaga a mensagem caso o username seja igual ao instanciado em this
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
        connections.remove(this);
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
