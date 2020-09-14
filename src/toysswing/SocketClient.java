package toysswing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketClient {
    Consumer<String> onReceive;
    Consumer<String> onConnect;
    Consumer<String> onSend;
    int port;
    
    int status = 1;

    Connection connection = new Connection();

    public void setPort(int port) {
        this.port = port;
    }
       
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
            try (Socket socket = new Socket("127.0.0.1", port);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream())){
                
                onConnect.accept("Successful");
                
                while(true) {
                    
                    String received = in.readObject().toString();
                    onReceive.accept(received);
                    status++;
                    
                }
                
            } catch (IOException ex) {
                Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
        
    }
}
