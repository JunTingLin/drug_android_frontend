package com.junting.drug_android_frontend.services


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;

public class bluetoothSocket extends Activity {

    private BluetoothSocket mmSocket;
    private OutputStream mmOutputStream;


    // calling this function to send the message to the device
    //1 -> the first pillbox, 2 -> the second pillbox, so on and so forth
    public void sendStringOverBluetooth(String message) {
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