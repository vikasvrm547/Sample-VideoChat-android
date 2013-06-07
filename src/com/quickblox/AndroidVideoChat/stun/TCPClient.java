package com.quickblox.AndroidVideoChat.stun;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Dmitrenko
 * Date: 6/6/13
 * Time: 11:45 AM
 */
public class TCPClient {

    private String serverMessage;
    public static final String SERVERIP = "turnserver.quickblox.com"; //your computer IP address
    public static final int SERVERPORT = 3478;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;

    PrintWriter out;
    BufferedReader in;

    DatagramSocket initialSocket;

    int timeout = 300; //ms

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(OnMessageReceived listener) {
        mMessageListener = listener;
    }


    public void stopClient() {
        mRun = false;
    }

    public void run() {
        Socket clientSocket = new Socket();
        SocketAddress serverAddress = new InetSocketAddress(SERVERIP, SERVERPORT);
        Log.v("Connection Thread", serverAddress.toString());
        try {
            clientSocket.connect(serverAddress, 3478);

            if (clientSocket.isConnected()) {

                Log.v("Connection Thread", "Connection Successful");
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                byte[] data = new byte[20];
                data[0] = 0x00;
                data[1] = 0x01;

                data[2] = 0x00;
                data[3] = 0x00;

                data[4] = 0x21;
                data[5] = 0x12;
                data[6] = (byte) 0xA4;
                data[7] = 0x42;

                data[8] = 0x00;
                data[9] = 0x01;
                data[10] = 0x00;
                data[11] = 0x01;
                data[12] = 0x00;
                data[13] = 0x01;
                data[14] = 0x00;
                data[15] = 0x01;
                data[16] = 0x00;
                data[17] = 0x01;
                data[18] = 0x00;
                data[19] = 0x01;

                outToServer.flush();

                outToServer.write(data);
                Log.v("Connection Thread", "Sent Auth Message");
                String response = null;

                response = inFromServer.readLine();


                processResponse(response);

                Log.v("TCP Client", "Response: " + response);
                outToServer.close();
                inFromServer.close();
                clientSocket.close();
            } else {
                Log.v("Connection Thread", "Not Connected yet...");
            }
        } catch (Exception e)

        {
            Log.v("Connection Thread", e.getMessage());
        }

    }

    private void processResponse(String response) {
        byte[] responseArray = response.getBytes();
        String responseType = String.valueOf(responseArray[0]) + String.valueOf(responseArray[1]);
        Log.d("Response", responseType);
        String parameterLength = String.valueOf(responseArray[2]) + String.valueOf(responseArray[3]);
        Log.d("Response", parameterLength);
        String magicCookieAndTransactionID = "";
        for (int i = 4; i < 17; ++i) {
            magicCookieAndTransactionID += String.valueOf(responseArray[i]) + "+";
        }
        Log.d("Response", magicCookieAndTransactionID);
        String parameters = "";
        for (int i = 17; i < responseArray.length; ++i) {
            parameters += String.valueOf(responseArray[i]) + "+";
        }
        Log.d("Response", parameters);
    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
//class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}
