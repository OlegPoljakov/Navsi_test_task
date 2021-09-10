package com.example.Navsi_test_task;

import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    static Logger logger = Logger.getLogger(Server.class);
    private ServerSocket serverSocket = null;

    public static void main(String args[])
    {
        Server server = new Server(8080);
    }

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);

            while (true) {
                Socket socket = null;
                try{
                    logger.info("Connection is set. Server waits for an answer");
                    socket = serverSocket.accept();

                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream());

                    System.out.println("Create new thread for new client");
                    logger.info("A new thread for a new client is created");
                    Thread t = new ClientHandler(socket, input, output);
                    t.start();
                }
                catch(IOException e)
                {
                    try {
                        socket.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    System.out.println(e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    final DataInputStream input;
    final DataOutputStream output;
    final Socket socket;
    private Database database;

    // Constructor
    public ClientHandler(Socket socket, DataInputStream input, DataOutputStream output)
    {
        this.socket = socket;
        this.input = input;
        this.output = output;
        this.database = Database.getInstance();
    }

    @Override
    public void run()
    {
        while(true){
            try {
                String receivedData = input.readUTF();
                String answer;
                if (receivedData.equals("exit")) {
                    this.socket.close();
                    System.out.println("Connection is closed");
                    break;
                }
                else{
                    System.out.println("Received data > " + receivedData);
                    String checksum = checksum(receivedData);
                    if (checksum.equals("000")){
                        answer = "correct data";
                        CoordinatePoint point = new CoordinatePoint(receivedData);
                        database.addPointToDatabase(point);
                    }
                    else {
                        answer = "corrupted data";
                    }
                    sendMessage(receivedData + " is " + answer, output);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try
        {
            // Сlosing resources
            //Add here all the data that was added to database
            System.out.println(database.printNumOfPoints());
            this.input.close();
            this.output.close();

        }catch(IOException e){
            e.printStackTrace();
        }

    }

    //CRC calculation
    //See https://asecuritysite.com/comms/crc_div?a=101110&b=1001
    //https://asecuritysite.com/comms/crc_div
    private String checksum(String str){
        int[] a = new int[str.length()];
        int n = str.length();
        for(int i = 0;i < n;i++){  //converting string message into integer ARRAY
            a[i] = Integer.valueOf(str.substring(i, i+1));
        }
        for(int i = 0;i < n-3;i++){                      //xor operation with 1011
            if(a[i] == 1){
                a[i] = (a[i] == 1) ? 0 : 1;
                a[i+1] = (a[i+1] == 1) ? 1 : 0;
                a[i+2] = (a[i+2] == 1) ? 0 : 1;
                a[i+3] = (a[i+3] == 1) ? 0 : 1;
            }
        }
        str=String.valueOf(a[n-3]);
        str=str.concat(String.valueOf(a[n-2]));
        str=str.concat(String.valueOf(a[n-1]));
        return str;
    }

    private static void sendMessage(String message, DataOutputStream output)  {
        try{
            output.writeUTF(message);
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }
}
