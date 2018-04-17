package com.dmsoft.firefly.gui.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * gobbler extend thread
 */
public class StreamGobbler extends Thread {

    private InputStream is;
    private String type;

    /**
     * constructor
     *
     * @param is   input stream
     * @param type type
     */
    public StreamGobbler(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    @Override
    public void run() {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        try {
            String line = null;
            while ((line = br.readLine()) != null) {
                if (type.equals("Error")) {
                    System.out.println("Error   :" + line);
                } else {
                    System.out.println("Debug:" + line);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
