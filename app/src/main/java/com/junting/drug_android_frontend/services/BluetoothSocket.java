package com.junting.drug_android_frontend.services


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;

public class BluetoothSocket extends Activity {

    private BluetoothSocket mmSocket;
    private OutputStream mmOutputStream;


    // calling this function to send the message to the device
    public static void sendStringOverBluetooth(String message) {
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

    public void openPillbox(int position){
        sendStringOverBluetooth(String.format(position));
    }

    public void closePillbox(int position){
        sendStringOverBluetooth(String.format(position));
    }

}