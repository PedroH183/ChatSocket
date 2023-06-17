package Server;

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
            // espera pela conexão de algum client
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("Um novo user entrou no chat");
                // implementing a interface runnable
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
