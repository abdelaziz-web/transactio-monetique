package com.example;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOServer;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.ISO87APackager;
import org.jpos.util.Logger;
import org.jpos.util.SimpleLogListener;



import java.io.IOException;

public class Server implements Runnable {
    public static void main(String[] args) {
        new Server().run();
    }

    @Override
    public void run() {
        Logger logger = new Logger();
        logger.addListener(new SimpleLogListener(System.out));

        try {
            ISOServer server = new ISOServer(7000, new ASCIIChannel(new ISO87APackager()), null);
            server.setLogger(logger, "server");
            server.addISORequestListener((source, m) -> {
                try {
                    System.out.println("Received request: " + m.getMTI());

                    // Create response
                    ISOMsg response = (ISOMsg) m.clone();
                    response.setMTI("0110"); // Authorization response
                    
                    // Set fields as per the sample authorization response
                    response.set(2, "4321123443211234"); // Primary Account Number
                    response.set(3, "000000"); // Processing Code
                    response.set(4, "000000012300"); // Amount transaction
                    response.set(7, "0304054133"); // Transmission data/time
                    response.set(11, "001205"); // System trace audit number
                    response.set(14, "0205"); // Expiration date
                    response.set(18, "5399"); // Merchant Type
                    response.set(22, "022"); // POS Entry Mode
                    response.set(25, "00"); // POS Condition Code
                    response.set(35, "4321123443211234=0205.."); // Track 2
                    response.set(37, "206305000014"); // Retrieval Reference Number
                    response.set(38, "010305"); // Authorization number
                    response.set(39, "00"); // Response code (Approved)
                    response.set(41, "29110001"); // Terminal ID
                    response.set(42, "1001001"); // Merchant ID
                    response.set(49, "840"); // Currency (US Dollars)
                    response.set(25, "00"); // Approval number

                
                    System.out.println("Sending response: " + response.getMTI());
                    source.send(response);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            });

            new Thread(server).start();
            System.out.println("ISO8583 Server is running on port 7000...");

        

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}