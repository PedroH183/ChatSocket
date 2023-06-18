package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    // essa classe é responsável por permitir mais de um user utilizando o server Socket
    // broadcast message to multiple clients
    private Socket socket;
    public  String clientUsername;
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
            // link do server com cada client é static
            connections.add(this);
            broadCastMessage("<Server> " + clientUsername + " entrou no chat");
            listUsersOn();
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
                removeClientHandler();
                return ;
            }
        }

    }

    // mensagens autenticadas
    public void broadCastMessage( String messageToSend ){
        // comunicando com todos os clients
        for( ClientHandler client : connections ){
            try {
                // só propaga a mensagem caso o username seja igual ao instanciado em this
                if( !client.clientUsername.equals(clientUsername) ){
                    client.bufferedWriter.write(messageToSend);
                    client.bufferedWriter.newLine(); // enter key
                    client.bufferedWriter.flush();
                }
            } catch (IOException err){
                removeClientHandler();
            }
        }
    }

    public void removeClientHandler(){
        broadCastMessage( "### Server " + clientUsername + " saiu do chat ###");
        connections.remove(this);
        closeEveryThing(this.socket, this.bufferedReader, this.bufferedWriter);
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

    // mensagens não autenticadas
    private void listUsersOn() throws IOException{
        // listando os users Online
        bufferedWriter.write("Listando Os Users Online !!");
        bufferedWriter.newLine(); // enter key
        bufferedWriter.flush();

        for ( ClientHandler client : ClientHandler.connections ){
            bufferedWriter.write(client.clientUsername + " IS ON !!" );
            bufferedWriter.newLine(); // enter key
            bufferedWriter.flush();
        }
        bufferedWriter.write("###########################!!");
        bufferedWriter.newLine(); // enter key
        bufferedWriter.flush();
    }

}
