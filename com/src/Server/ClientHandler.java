package Server;

import ConsoleColors.ConsoleColors;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    // essa classe é responsável por permitir mais de um user utilizando o server Socket
    // broadcast is a message to multiple clients
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
            broadCastMessage( ConsoleColors.MessageWColor(ConsoleColors.CYAN, "<Server> ", ConsoleColors.RED + clientUsername + ConsoleColors.RESET + " entrou no chat") );
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
        broadCastMessage( ConsoleColors.MessageWColor(ConsoleColors.CYAN, "<Server> ", clientUsername + " saiu do chat") );
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

    // mensagem não autenticadas
    private void listUsersOn() throws IOException{
        // listando os users Online
        String mssg = ConsoleColors.MessageWColor(ConsoleColors.BLUE, "Listando Os Users Online", "");
        bufferedWriter.write(mssg);
        bufferedWriter.newLine(); // enter key
        bufferedWriter.flush();

        for ( ClientHandler client : ClientHandler.connections ){
            mssg = ConsoleColors.MessageWColor(ConsoleColors.RED, client.clientUsername, " Is On !!");
            bufferedWriter.write(mssg);
            bufferedWriter.newLine(); // enter key
            bufferedWriter.flush();
        }
        mssg = ConsoleColors.MessageWColor(ConsoleColors.BLUE, "####################", "");
        bufferedWriter.write(mssg);
        bufferedWriter.newLine(); // enter key
        bufferedWriter.flush();
    }

}
