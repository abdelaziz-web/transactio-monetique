package com.example;

import org.jpos.iso.ISOBinaryField;
import org.jpos.iso.ISOBitMap;
import org.jpos.iso.ISOComponent;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.iso.packager.ISO87APackager;

// import com.example.db.H2DatabaseConnection;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.Formatter;

public class Client {
    public static void main(String[] args) {
  
        try {
            GenericPackager packager = new GenericPackager("C:\\Users\\Abdelaziz\\Desktop\\my-app\\src\\main\\resources\\fields.xml");
            ISOMsg message = new ISOMsg();
            message.setPackager(packager);

       

            // Set other fields
            message.set(new ISOField(0, "0100"));
            message.set(new ISOField(2, "4111111111111111"));
            message.set(new ISOField(3, "000000"));
            message.set(new ISOField(4, "000000012300"));
            message.set(new ISOField(7, "0804120000"));
            message.set(new ISOField(11, "123456"));
            message.set(new ISOField(12, "120000"));
            message.set(new ISOField(13, "0804"));
            message.set(new ISOField(14, "2512"));
            message.set(new ISOField(18, "5999"));
            message.set(new ISOField(22, "021"));
            message.set(new ISOField(25, "00"));

            byte[] packedMessage = message.pack();


          
      //      String binaryDump = getBinaryDump(packedMessage);
    //        System.out.println(binaryDump);



// Display packed message length
            System.out.println("Packed message length: " + packedMessage.length);

// Display packed message in hexdump format
            System.out.println("Packed message (Hexdump):");
            System.out.println(getHexDump(packedMessage));
            

            System.out.println("Packed message (ASCII): " + new String(packedMessage, StandardCharsets.US_ASCII));            /* 
            // Send the packed message
            System.out.println("Sending message to server...");
            channel.send(message);
            
            // Receive and unpack the response
            System.out.println("Waiting for server response...");
            ISOMsg response = channel.receive();
            System.out.println("Response received:");
            System.out.println("Response MTI: " + response.getMTI());
            System.out.println("Response Code: " + response.getString(39));

            System.out.println("Response insertion "); */

        

            // Display message as a string
        //    String messageString = getMessageAsString(message);
         //   System.out.println("Message as string: " + messageString);
            
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
          /*   if (channel != null) {
                try {
                    channel.disconnect();
                    System.out.println("Disconnected from server.");
                } catch (Exception e) {
                    System.out.println("Error while disconnecting: " + e.getMessage());
                }
            } */
        }
    }
    
    private static String getHexDump(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        StringBuilder ascii = new StringBuilder();
        Formatter formatter = new Formatter(result);
        
        for (int i = 0; i < bytes.length; i++) {
            if (i % 16 == 0) {
                if (i > 0) {
                    result.append("  ").append(ascii).append("\n");
                    ascii.setLength(0);
                }
                formatter.format("%04X: ", i);
            }
            formatter.format("%02X ", bytes[i]);
            ascii.append(Character.isISOControl(bytes[i]) ? '.' : (char) bytes[i]);
        }
        
        // Pad the last line if necessary
        int remaining = bytes.length % 16;
        if (remaining > 0) {
            for (int i = 0; i < (16 - remaining); i++) {
                result.append("   ");
            }
        }
        result.append("  ").append(ascii).append("\n");
        
        formatter.close();
        return result.toString();
    }


    private static String getMessageAsString(ISOMsg message) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    message.pack(baos);
    byte[] packedMsg = baos.toByteArray();
    return new String(packedMsg, StandardCharsets.ISO_8859_1);
}

private static String getBinaryDump(byte[] bytes) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < bytes.length; i++) {
        if (i % 4 == 0 && i > 0) {
            result.append("\n"); // New line for every 4 bytes for better readability
        }
        result.append(byteToBinary(bytes[i])).append(" ");
    }
    return result.toString();
}

private static String byteToBinary(byte b) {
    return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
}


}


   //  H2DatabaseConnection.getConnection() ;

   //  H2DatabaseConnection.insertISOMessage(message) ;

   //   H2DatabaseConnection.closeConnection() ;