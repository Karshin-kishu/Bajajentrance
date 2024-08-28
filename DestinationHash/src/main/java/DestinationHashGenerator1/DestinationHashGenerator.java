package DestinationHashGenerator1;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

public class DestinationHashGenerator {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar 240341220084 C:\\Users\\karsh\\Desktop\\DestinationHash\\DestinationHash\\src\\main\\resources\\exampl.json");
            return;
        }

        String prnNumber = args[0];
        String jsonFilePath = args[1];

        try {
            // Read JSON file
            FileReader fileReader = new FileReader(new File(jsonFilePath));
            StringBuilder jsonContent = new StringBuilder();
            Scanner scanner = new Scanner(fileReader);
            while (scanner.hasNext()) {
                jsonContent.append(scanner.nextLine());
            }
            scanner.close();
            JSONObject jsonObject = new JSONObject(jsonContent.toString());

            // Traverse JSON to find "destination"
            String destinationValue = findDestinationValue(jsonObject);
            if (destinationValue == null) {
                System.out.println("Key 'destination' not found.");
                return;
            }

            // Generate random string
            String randomString = generateRandomString(8);

            // Create hash
            String hashInput = prnNumber + destinationValue + randomString;
            String md5Hash = generateMD5Hash(hashInput);

            // Output result
            System.out.println(md5Hash + ";" + randomString);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static String findDestinationValue(JSONObject jsonObject) {
        return findDestinationValueRecursively(jsonObject);
    }

    private static String findDestinationValueRecursively(Object obj) {
        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            if (jsonObject.has("destination")) {
                return jsonObject.getString("destination");
            }
            for (String key : jsonObject.keySet()) {
                String result = findDestinationValueRecursively(jsonObject.get(key));
                if (result != null) {
                    return result;
                }
            }
        } else if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;
            for (int i = 0; i < jsonArray.length(); i++) {
                String result = findDestinationValueRecursively(jsonArray.get(i));
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
