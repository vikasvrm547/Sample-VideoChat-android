package com.quickblox.AndroidVideoChat.stun;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.*;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Dmitrenko
 * Date: 6/6/13
 * Time: 11:45 AM
 */
public class TURNClient {


    public static final String SERVERIP = "turnserver.quickblox.com"; //your computer IP address
    public static final int SERVERPORT = 3478;
    private OnMessageReceived messageListener = null;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TURNClient(OnMessageReceived listener) {
        messageListener = listener;
        new TURNClientAsyncTask().execute();
    }


    private class TURNClientAsyncTask extends AsyncTask<Void, Void, Void> {

        String fullAddress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            byte[] responseData = sendBindingRequest();
            if (responseData != null) {
                processBindingResponse(responseData);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            messageListener.messageReceived(fullAddress);
        }

        private byte[] sendBindingRequest() {
            Socket clientSocket = new Socket();
            SocketAddress serverAddress = new InetSocketAddress(SERVERIP, SERVERPORT);
            try {
                clientSocket.connect(serverAddress, SERVERPORT);
                if (clientSocket.isConnected()) {
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    DataInputStream dis = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));

                    byte[] headerData = getHeaderData();

                    outToServer.flush();
                    outToServer.write(headerData);

                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    DataOutputStream dos = new DataOutputStream(outStream);
                    byte buffer[] = new byte[8192];
                    int read;
                    while ((read = dis.read(buffer)) != -1) {
                        dos.write(buffer, 0, read);
                        dos.flush();
                    }
                    dis.close();
                    dos.close();
                    outStream.close();

                    outToServer.close();
                    clientSocket.close();
                    return outStream.toByteArray();
                }
            } catch (Exception e) {
                Log.v("Connection Thread", e.getMessage());
            }
            return null;
        }

        private byte[] getHeaderData() {

            byte[] headerData = new byte[20];
            headerData[0] = 0x00;
            headerData[1] = 0x01;

            headerData[2] = 0x00;
            headerData[3] = 0x00;

            headerData[4] = 0x21;
            headerData[5] = 0x12;
            headerData[6] = (byte) 0xA4;
            headerData[7] = 0x42;

            headerData[8] = 0x00;
            headerData[9] = 0x01;
            headerData[10] = 0x00;
            headerData[11] = 0x01;
            headerData[12] = 0x00;
            headerData[13] = 0x01;
            headerData[14] = 0x00;
            headerData[15] = 0x01;
            headerData[16] = 0x00;
            headerData[17] = 0x01;
            headerData[18] = 0x00;
            headerData[19] = 0x01;
            return headerData;
        }

        private void processBindingResponse(byte[] responseArray) {
            for (int i = 0; i < responseArray.length; i++) {
                Log.v("Response", i + " " + responseArray[i] + "");
            }
            byte[] ip = new byte[4];
            System.arraycopy(responseArray, 40, ip, 0, 4);
            try {
                InetAddress inet4Address = Inet4Address.getByAddress(ip);
                byte[] portArray = new byte[2];
                System.arraycopy(responseArray, 38, portArray, 0, 2);
                int port = twoBytesToInteger(portArray);
                Log.v("Response", "INETADDR: " + inet4Address + ":" + port);
                fullAddress = inet4Address + ":" + port;
            } catch (UnknownHostException e) {
                fullAddress = "";
                e.printStackTrace();
            }

        }

        private int twoBytesToInteger(byte[] value) {
            int temp0 = value[0] & 0xFF;
            int temp1 = value[1] & 0xFF;
            return ((temp0 << 8) + temp1);
        }

    }


    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}