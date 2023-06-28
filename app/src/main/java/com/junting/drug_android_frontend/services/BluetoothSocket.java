package com.junting.drug_android_frontend.services;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;

public class BluetoothSocket extends Activity {

    private BluetoothSocket mmSocket;
    private OutputStream mmOutputStream;


    // calling this function to send the message to the device
    public void openPillbox(String cellPosition) {
        /**
         * cellPosition -> String
         *
         */

        String message = "";
        switch(cellPosition){
            case "1":
                message = "1";
            case "2":
                message = "3";
            case "3":
                message = "5";
            case "4":
                message = "7";
            case "5":
                message = "9";
            case "6":
                message = "11";
            case "7":
                message = "13";
            case "8":
                message = "15";
            case "9":
                message = "17";
        }

        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice("78:21:84:8C:A6:92"); //MAC address of the device

            SerialSocket ss = new SerialSocket(device);
            ss.run();

            byte[] messageBytes = message.getBytes();
            ss.write(messageBytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closePillbox(String cellPosition) {
        /**
         * cellPosition -> String
         *
         */

        String message = "";
        switch(cellPosition){
            case "1":
                message = "2";
            case "2":
                message = "4";
            case "3":
                message = "6";
            case "4":
                message = "8";
            case "5":
                message = "10";
            case "6":
                message = "12";
            case "7":
                message = "14";
            case "8":
                message = "16";
            case "9":
                message = "18";
        }

        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice("78:21:84:8C:A6:92"); //MAC address of the device

            SerialSocket ss = new SerialSocket(device);
            ss.run();

            byte[] messageBytes = message.getBytes();
            ss.write(messageBytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}