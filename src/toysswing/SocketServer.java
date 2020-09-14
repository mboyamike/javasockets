package toysswing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketServer {
    
    Consumer<String> onReceive;
    Consumer<String> onConnect;
    Consumer<String> onSend;

    Connection connection = new Connection();
    

    public void setOnReceive(Consumer<String> onReceive) {
        this.onReceive = onReceive;
    }

    public void setOnConnect(Consumer<String> onConnect) {
        this.onConnect = onConnect;
    }
    
    public void setOnSend(Consumer<String> onSend) {
        this.onSend = onSend;
    }
    
    public void startConnection() {
        connection.start();
    }
    
    public void send(String message) {
        try {
            connection.out.writeObject(message);
            onSend.accept(message);
        } catch (IOException ex) {
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void stopConnection() {
        try {
            connection.out.close();
        } catch (IOException ex) {
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public class Connection extends Thread {
        ObjectOutputStream out;
        
        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(0);
                String message = "Server is running on port " + serverSocket.getLocalPort();
                onConnect.accept(message);
                
                Socket socket = serverSocket.accept();
                message = "Successfully connected to client";
                onConnect.accept(message);
                
                out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                
                message = "Please send toy information detail";
                send(message);
                                        
                while(true) {
                    String received = in.readObject().toString();
                    onReceive.accept(received);
          
                }
                
            } catch (IOException ex) {
                Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }
}
