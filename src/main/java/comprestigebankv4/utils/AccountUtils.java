package comprestigebankv4.utils;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.time.Year;

public class AccountUtils {


    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "Bu istifadəçinin artıq yaradılmış hesabı var!";

    public static final String ACCOUNT_CREATION_SUCCESS = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Hesab uğurla yaradıldı! Ətraflı məlumat üçün e-poçtunuzu yoxlayın.";

    public static final String PHONE_NUMBER_EXISTS_CODE = "003";
    public static final String PHONE_NUMBER_EXISTS_MESSAGE = "Zəhmət olmasa öz mobil nömrəsi yazın!";

    public static String generateAccountNumber() {

        /**
         * year + 6 random numbers
         */
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;
        // we take a random number between min and max
        //             m.random between 0 and 9
        int randNumber =(int)Math.floor(Math.random() * (max-min+1) + min);
        // convert randNumber to string and then concatenate
        String year = String.valueOf(currentYear);
        String  randomNumber = String.valueOf(randNumber);
        StringBuilder accountNumber = new StringBuilder();
        return accountNumber.append(year).append(randomNumber).toString();
    }

    public static String generateAccountIBAN(String accountNumber1) {
        Random random = new Random();
        int randNumberCGC1 =(int)Math.abs(random.nextInt()%10);
        int randNumberCGC2 =(int)Math.abs(random.nextInt()%10);
        int randNumberBC1 =(int)Math.abs(random.nextInt()%10);
        int randNumberBC2 =(int)Math.abs(random.nextInt()%10);
        int randNumberBC3 =(int)Math.abs(random.nextInt()%10);
        int randNumberBC4 =(int)Math.abs(random.nextInt()%10);
        int randNumberBC5 = (int)Math.abs(random.nextInt()%10);
        int randNumberBC6 = (int)Math.abs(random.nextInt()%10);
        String country = "AZ";
        String CGC1 = String.valueOf(randNumberCGC1);
        String CGC2 = String.valueOf(randNumberCGC2);
        String BIC = "APRE";
        String BC1 = String.valueOf(randNumberBC1);
        String BC2 = String.valueOf(randNumberBC2);
        String BC3 = String.valueOf(randNumberBC3);
        String BC4 = String.valueOf(randNumberBC4);
        String BC5 = String.valueOf(randNumberBC5);
        String BC6 = String.valueOf(randNumberBC6);
        String accountNumber = accountNumber1;
        StringBuilder accountIBAN = new StringBuilder();
        return accountIBAN.append(country).append(CGC1).append(CGC2).append(BIC).append(BC1).append(BC2).append(BC3).append(BC4).append(BC5).append(BC6).append(accountNumber).toString();
    }
    public static String generateCardNumber() {
        Random random = new Random();
        int randNumber1 =(int)Math.abs(random.nextInt()%10);
        int randNumber2 =(int)Math.abs(random.nextInt()%10);
        int randNumber3 =(int)Math.abs(random.nextInt()%10);
        int randNumber4 =(int)Math.abs(random.nextInt()%10);
        int randNumber5 =(int)Math.abs(random.nextInt()%10);
        int randNumber6 =(int)Math.abs(random.nextInt()%10);
        int randNumber7 =(int)Math.abs(random.nextInt()%10);
        int randNumber8 =(int)Math.abs(random.nextInt()%10);
        String firstNumbers = "4895 0333 ";
        String place = " ";
        String num1 = String.valueOf(randNumber1);
        String num2 = String.valueOf(randNumber2);
        String num3 = String.valueOf(randNumber3);
        String num4 = String.valueOf(randNumber4);
        String num5 = String.valueOf(randNumber5);
        String num6 = String.valueOf(randNumber6);
        String num7 = String.valueOf(randNumber7);
        String num8 = String.valueOf(randNumber8);
        StringBuilder cardNumber = new StringBuilder();
        return cardNumber.append(firstNumbers).append(num1).append(num2).append(num3).append(num4).append(place).append(num5).append(num6).append(num7).append(num8).toString();
    }
    public static String generateCVVCode() {
        Random random = new Random();
        int randNum =(int)Math.abs(random.nextInt()%1000);
        if (randNum < 10){
            String num;
            return num = "00" + String.valueOf(randNum);
        } else if (randNum < 100) {
            String num;
            return num = "0" + String.valueOf(randNum);
        }
        else{
            String num;
            return num = String.valueOf(randNum);
        }
    }
    public static String maskCardNumber(String cardNumber) {
        String[] parts = cardNumber.split(" ");
        String masked = "**** **** **** " + parts[3];

        return masked;
    }

    private static final String CHARACTERS = "0123456789";
    private static final int CODE_LENGTH = 6;
    private static SecureRandom random = new SecureRandom();

    public static String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

    public static String generateExpirationDate() {
        // Get current date
        LocalDate currentDate = LocalDate.now();

        // Add 3 years to the current date for expiration date
        LocalDate expirationDate = currentDate.plusYears(3);

        // Format date to string (MM/YY)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        return expirationDate.format(formatter);
    }
}
