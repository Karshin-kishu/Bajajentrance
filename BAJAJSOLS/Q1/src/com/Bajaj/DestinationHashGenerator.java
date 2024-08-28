package com.Bajaj;

import java.io.FileReader;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Random;
import org.json.JSONObject;
import org.json.JSONTokener;

public class DestinationHashGenerator {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN> <JSON file path>");
            return;
        }

        String prnNumber = args[0].toLowerCase().replaceAll("\\s+", "");
        String filePath = args[1];

        // Read and parse JSON
        JSONObject json = new JSONObject(new JSONTokener(new FileReader(filePath)));
        String destinationValue = findDestinationValue(json);

        if (destinationValue == null) {
            System.out.println("Key 'destination' not found in the JSON.");
            return;
        }

        // Generate random string
        String randomString = generateRandomString(8);

        // Concatenate and generate MD5 hash
        String combined = prnNumber + destinationValue + randomString;
        String md5Hash = generateMD5Hash(combined);

        // Output the result
        System.out.println(md5Hash + ";" + randomString);
    }

    // Function to find the first "destination" key in JSON
    private static String findDestinationValue(JSONObject json) {
        Iterator<String> keys = json.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            Object value = json.get(key);

            if (key.equals("destination")) {
                return value.toString();
            } else if (value instanceof JSONObject) {
                String found = findDestinationValue((JSONObject) value);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    // Function to generate a random alphanumeric string
    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    // Function to generate an MD5 hash
    private static String generateMD5Hash(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();

        for (byte b : messageDigest) {
            hexString.append(Integer.toHexString(0xFF & b));
        }
        return hexString.toString();
    }
}
