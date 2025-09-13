package com.lsp.web.ONDCService;
import java.util.Base64;
import java.security.spec.KeySpec;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import javax.crypto.*;
import javax.crypto.spec.*;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class FinvuService {
	
	private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String UAT_PASSPHRASE = "_oAlw6AYXK%-&f%U";


    public static String encrypt(String strToEncrypt, String passPhrase, String salt) {
        try {
            byte[] ivBytes = new byte[16];
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(passPhrase.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            System.out.println("Error encrypting: " + e.getMessage());
            return null;
        }
    }
    public static String getSalt() {
        Instant now = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss")
                                                       .withZone(ZoneOffset.UTC);
        String timestamp = formatter.format(now);
        int millis = now.get(ChronoField.MILLI_OF_SECOND);
        return timestamp + String.format("%03d", millis);
    }

        // XOR Obfuscation using salt
    public static String xorObfuscate(String fiuId, String salt) {
        byte[] fiuBytes = fiuId.getBytes();
        byte[] keyBytes = salt.getBytes();
        byte[] xored = new byte[fiuBytes.length];

        for (int i = 0; i < fiuBytes.length; i++) {
            xored[i] = (byte) (fiuBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        return Base64.getEncoder().encodeToString(xored);
    }

     //requester type
     public static String requesterType(String requester, String salt) {
        byte[] fiuBytes = requester.getBytes();
        byte[] keyBytes = salt.getBytes();
        byte[] xored = new byte[fiuBytes.length];

        for (int i = 0; i < fiuBytes.length; i++) {
            xored[i] = (byte) (fiuBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        return Base64.getEncoder().encodeToString(xored);
    }

    public static String linkparam(String txnid,String srcref,String mobilenumber, String redirecturl){

        String sessionid = "DEV" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 15);
        String mobileNumber = mobilenumber; // You may get this from user input
        String userid = mobileNumber + "@finvu";
        String requestortype = "LSP";
        String fiuid = "VSPL-lsp-uat";

        String stringtoecr = String.format(
            "txnid=%s&sessionid=%s&srcref=%s&userid=%s&redirect=%s",
            txnid, sessionid, srcref, userid, redirecturl
        );

        String salt = getSalt();


        String ecreq = encrypt(stringtoecr,"_oAlw6AYXK%-&f%U",salt);
        String requesterType = requesterType(requestortype,salt);
        String xorObfuscate = xorObfuscate(fiuid,salt);

        String redirectionlinkforaa = "https://reactjssdk.finvu.in?ecreq="+ecreq+"&reqdate="+salt+"&fi="+xorObfuscate+"&requestorType="+requesterType;

        return redirectionlinkforaa;
    }

}