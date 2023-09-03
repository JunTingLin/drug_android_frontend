//package com.junting.drug_android_frontend.services.BTServices;
//
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothSocket;
//
//import java.io.IOException;
//import java.util.UUID;
//
//public class SerialSocket{
//
//    private static final UUID BLUETOOTH_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//    public BluetoothSocket socket;
//
//    public SerialSocket() {
//        run();
//    }
//    void write(byte[] data) throws IOException {
//        if (socket != null){
//            socket.getOutputStream().write(data);
//        }
//    }
//    public void close(){
//        try {
//            socket.close();
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//    public void run() {
//        try {
//            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//            BluetoothDevice device = bluetoothAdapter.getRemoteDevice("78:21:84:8C:A6:92"); //MAC address of the device(old)
////            BluetoothDevice device = bluetoothAdapter.getRemoteDevice("78:21:84:8E:6D:C6"); //MAC address of the device
//            socket = device.createRfcommSocketToServiceRecord(BLUETOOTH_SPP);
//            socket.connect();
//        }
//        catch (Exception e) {
//            socket = null;
//            e.printStackTrace();
//        }
//    }
//
//}
