package com.example.Navsi_test_task;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Client {

    static Logger logger = Logger.getLogger(Client.class);

    public static void main(String args[])
    {
        Client client = new Client("localhost", 8080);
    }

    public Client(String address, int port){
        try {
            Socket socket = new Socket(address, port);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            logger.info("Connection is set!");
            //Read txt file with data to be send
            try(Scanner data = new Scanner (new File("src/main/resources/test.txt"))) {
                //Read strings one by one
                logger.info("File is read");
                while(data.hasNextLine()){
                    String stringData = data.nextLine();
                    //If string is "exit" - send "exit" command to server, close connection and quit
                    if(stringData.equals("exit")){
                        logger.info("The end of the file");
                        output.writeUTF("exit");
                        socket.close();
                        System.out.println("Connection closed");
                        break;
                    }
                    else {
                        //Convert Int data to binary String representation
                        String binaryData = convertSrtingIntToBinary(stringData);
                        System.out.println("Data to be sent > " + binaryData);
                        //CRC
                        System.out.println("checksum > "+checksum(binaryData.concat("000")));
                        //Data + CRC = package
                        binaryData = binaryData.concat(checksum(binaryData.concat("000")));
                        System.out.println("Sent data > " + binaryData);
                        //Send package
                        logger.info("Binary data is sent");
                        sendMessage(binaryData, output);
                        //1 second delay
                        TimeUnit.SECONDS.sleep(1);
                        //Receive server answer
                        logger.info("Server answer is read");
                        String message = input.readUTF();
                        System.out.println("server answer > " + message);
                    }
                }
            }
            catch (IOException e) {
                System.out.println(e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Method converts String of int to single string of bits
    private static String convertSrtingIntToBinary(String input){
        String[] words = input.split(" ");
        int[] nums = new int[words.length];
        String out = "";
        for (int i = 0; i < words.length; i++) {
            nums[i] = Integer.parseInt(words[i]);
        }
        for (int i = 0; i < nums.length; i++) {
            String tmp = String.format("%8s", Integer.toBinaryString(nums[i] & 0xFF)).replace(' ', '0');
            out = out + tmp;
        }
        return out;
    }

    //CRC calculation
    //See https://asecuritysite.com/comms/crc_div?a=101110&b=1001
    //https://asecuritysite.com/comms/crc_div
    private static String checksum(String str){
        int n=str.length();
        int[] a=new int[n];
        for(int i=0;i<n;i++){
            a[i]=Integer.valueOf(str.substring(i,i+1));
        }
        for(int i=0;i<n-3;i++){
            if(a[i]==1){
                a[i]=(a[i]==1)?0:1;
                a[i+1]=(a[i+1]==1)?1:0;
                a[i+2]=(a[i+2]==1)?0:1;
                a[i+3]=(a[i+3]==1)?0:1;
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
