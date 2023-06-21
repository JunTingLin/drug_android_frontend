package com.junting.drug_android_frontend.services

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

class SerialSocket implements Runnable {

    private static final UUID BLUETOOTH_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BluetoothDevice device;
    private BluetoothSocket socket;

    public SerialSocket( BluetoothDevice device) {

        this.device = device;  // setter
    }

    public String getName() {
        return device.getName() != null ? device.getName() : device.getAddress();
    }

    void write(byte[] data) throws IOException {
        socket.getOutputStream().write(data);
    }

    @Override
    public void run() {
        try {
            socket = device.createRfcommSocketToServiceRecord(BLUETOOTH_SPP);
            socket.connect();
        }
        catch (Exception e) {}
    }

}
