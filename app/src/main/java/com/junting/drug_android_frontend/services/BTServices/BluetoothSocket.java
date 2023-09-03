//package com.junting.drug_android_frontend.services.BTServices;
//
//
//
//
//import java.io.IOException;
//
//public class BluetoothSocket{
//
//    public SerialSocket ss = new SerialSocket();
//
//    // calling this function to send the message to the device
//    public void openPillbox(String cellPosition) {
//        /**
//         * cellPosition -> String
//         *
//         */
//
//        String message = "";
//        switch(cellPosition){
//            case "7":
//            message = "b";break;
//            case "8":
//            message = "d";break;
//            case "9":
//            message = "f";break;
//            case "4":
//            message = "h";break;
//            case "5":
//            message = "j";break;
//            case "6":
//            message = "l";break;
//            case "1":
//            message = "n";break;
//            case "2":
//            message = "p";break;
//            case "3":
//            message = "r";break;
//        }
//        try {
//            byte[] messageBytes = message.getBytes();
//            ss.write(messageBytes);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void closePillbox(String cellPosition) {
//        /**
//         * cellPosition -> String
//         *
//         */
//
//        String message = "";
//        switch(cellPosition){
//            case "7":
//            message = "a";break;
//            case "8":
//            message = "c";break;
//            case "9":
//            message = "e";break;
//            case "4":
//            message = "g";break;
//            case "5":
//            message = "i";break;
//            case "6":
//            message = "k";break;
//            case "1":
//            message = "m";break;
//            case "2":
//            message = "o";break;
//            case "3":
//            message = "q";break;
//        }
//        try {
//            byte[] messageBytes = message.getBytes();
//            ss.write(messageBytes);
//            ss.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void closeMutiplePillbox(int[] arr){
//        for (int i=0; i< arr.length; i++){
//            String position = String.valueOf(arr[i]);
//            String message = "";
//            switch(position){
//                case "7":
//                    message = "a";break;
//                case "8":
//                    message = "c";break;
//                case "9":
//                    message = "e";break;
//                case "4":
//                    message = "g";break;
//                case "5":
//                    message = "i";break;
//                case "6":
//                    message = "k";break;
//                case "1":
//                    message = "m";break;
//                case "2":
//                    message = "o";break;
//                case "3":
//                    message = "q";break;
//            }
//            try {
//                byte[] messageBytes = message.getBytes();
//                ss.write(messageBytes);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        ss.close();
//    }
//
//}