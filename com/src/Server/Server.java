package Server;

import ConsoleColors.ConsoleColors;

import java.io.Console;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void startServer(){

        try{
            while( !serverSocket.isClosed() )
            {
                // critical point method accept, explain !!
                Socket socket = serverSocket.accept();
                System.out.println(ConsoleColors.MessageWColor(ConsoleColors.YELLOW, "user", " entrou no chat"));
                // implementing interface runnable, instancia uma nova conexão
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch ( IOException err ){
            // em caso de execção encerrammos nosso server
            closeServerSocket();
        }
    }

    public void closeServerSocket(){
        try{
            if( serverSocket != null ){
                serverSocket.close();
            }
        } catch ( IOException err ){
            err.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // server waiting for client in port 1234
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
