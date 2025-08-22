//package com.lsp.web.controller;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import ondc.onboarding.utility.*;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import ondc.onboarding.utility.CryptoFunctions;
//
//import org.springframework.web.bind.annotation.*;
//
//import java.nio.charset.StandardCharsets;
//import java.security.MessageDigest;
//import java.time.Instant;
//import java.util.Base64;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.logging.Logger;
//
//@RestController
//@RequestMapping("/api")
//public class SignatureController {
//
//  private static final String BASE64_PRIVATE_KEY = "DwIj0AObeY+06UHJoYrOYgoP9jz/9PGZilw4tqW9Jeg=";
//  private static final String SUBSCRIBER_ID = "uat.credithaat.in";
//  private static final String UNIQUE_KEY_ID = "642129cd-7856-4975-8496-d62cecd912b0";
//
//  private static final String CREATED_ISO = "2025-06-19T11:12:50.788Z";
//  private static final String EXPIRES_ISO = "2026-10-20T18:00:15.071Z";
//
//  @PostMapping("/sign-ondc")
//  public String signRequest(@RequestBody String requestBody) throws Exception {
//        
//          long created = System.currentTimeMillis() / 1000L;
//          long expires = created + 300000;
////          logger.info(toBase64(generateBlakeHash(req.get("value").toString())));
////          logger.info(req.get("value").toString());
//          String hashedReq = Utils.hashMassage(requestBody.toString(),created,expires);
//          String signature = Utils.sign(Base64.getDecoder().decode(BASE64_PRIVATE_KEY),hashedReq.getBytes());
//          String subscriberId = SUBSCRIBER_ID;
//          String uniqueKeyId = UNIQUE_KEY_ID;
//
//          return "Signature keyId=\"" + subscriberId + "|" + uniqueKeyId + "|" + "ed25519\"" + ",algorithm=\"ed25519\"," + "created=\"" + created + "\",expires=\"" + expires + "\",headers=\"(created) (expires)" + " digest\",signature=\"" + signature + "\"";
//      
//          
//          
////          return "Signature keyId=\"" + SUBSCRIBER_ID + "|" + UNIQUE_KEY_ID + "|" + "ed25519\"" + ",algorithm=\"ed25519\"," + "created=\"" + created + "\",expires=\"" + expires + "\",headers=\"(created) (expires)" + " digest\",signature=\"" + signature + "\"";
//          
//          
//
////          response.put("Authorization", authHeader);
////          response.put("created", String.valueOf(created));
////          response.put("expires", String.valueOf(expires));
////          response.put("signature", signatureBase64);
////          response.put("digest", digestBase64);
////          response.put("normalized_body", normalizedJson);
//
////      } catch (Exception e) {
////          response.put("error", "Failed to sign payload: " + e.getMessage());
////      }
////return null;
////      return response;
//  }
//
//  @PostMapping("/verify-ondc")
//  public Map<String, Object> verifySignature(
//          @RequestBody String requestBody,
//          @RequestHeader("Authorization") String authorizationHeader,
//          @RequestParam("publicKey") String base64PublicKey
//  ) {
//      Map<String, Object> response = new HashMap<>();
//      try {
//          ObjectMapper mapper = new ObjectMapper();
//          String normalizedJson = mapper.writeValueAsString(mapper.readTree(requestBody));
//
//          // Digest using BLAKE2B-512 (match signing step)
//          byte[] hash = CryptoFunctions.generateBlakeHash(normalizedJson);
//          String digestBase64 = Base64.getEncoder().encodeToString(hash);
//
//          Map<String, String> headerParts = new HashMap<>();
//          for (String part : authorizationHeader.replace("Signature ", "").split(",")) {
//              String[] kv = part.trim().split("=", 2);
//              String key = kv[0];
//              String value = kv[1].replaceAll("^\"|\"$", "");
//              headerParts.put(key, value);
//          }
//
//          long created = Long.parseLong(headerParts.get("created"));
//          long expires = Long.parseLong(headerParts.get("expires"));
//          String signatureBase64 = headerParts.get("signature");
//
//          String signingString = "(created): " + created + "\n" +
//                                 "(expires): " + expires + "\n" +
//                                 "digest: BLAKE2B-512=" + digestBase64;
//
//          byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
//          byte[] publicKeyBytes = Base64.getDecoder().decode(base64PublicKey);
//
//          boolean verified = CryptoFunctions.verify(
//                  signatureBytes,
//                  signingString.getBytes(StandardCharsets.UTF_8),
//                  publicKeyBytes
//          );
//
//          response.put("verified", verified);
//          response.put("digest", digestBase64);
//          response.put("signing_string", signingString);
//
//      } catch (Exception e) {
//          response.put("error", "Verification failed: " + e.getMessage());
//      }
//
//      return response;
//  }
//  
//  @PostMapping("/")
//  public void onSearchCallback(@RequestBody String json) {
//  	System.out.println("The callback received is : "+json);
//  }
//  
//}

package com.lsp.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import ondc.onboarding.utility.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import ondc.onboarding.utility.CryptoFunctions;

import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class SignatureController {

  private static final String BASE64_PRIVATE_KEY = "MnP2sPvCnMlFVR3k/PfORhIATn+V9bVNd3EgNHVRA18=";
  private static final String SUBSCRIBER_ID = "uat.getrysa.com";
  private static final String UNIQUE_KEY_ID = "d744ee65-0831-463f-b489-7add070f96f4";

//  private static final String CREATED_ISO = "2025-06-19T11:12:50.788Z";
//  private static final String EXPIRES_ISO = "2026-10-20T18:00:15.071Z";

  @PostMapping("/sign-ondc")
  public String signRequest(@RequestBody String requestBody) throws Exception {
        
          long created = System.currentTimeMillis() / 1000L;
          long expires = created + 300000;
//          logger.info(toBase64(generateBlakeHash(req.get("value").toString())));
//          logger.info(req.get("value").toString());
          String hashedReq = Utils.hashMassage(requestBody.toString(),created,expires);
          String signature = Utils.sign(Base64.getDecoder().decode(BASE64_PRIVATE_KEY),hashedReq.getBytes());
          String subscriberId = SUBSCRIBER_ID;
          String uniqueKeyId = UNIQUE_KEY_ID;

          return "Signature keyId=\"" + subscriberId + "|" + uniqueKeyId + "|" + "ed25519\"" + ",algorithm=\"ed25519\"," + "created=\"" + created + "\",expires=\"" + expires + "\",headers=\"(created) (expires)" + " digest\",signature=\"" + signature + "\"";
      
          
          
//          return "Signature keyId=\"" + SUBSCRIBER_ID + "|" + UNIQUE_KEY_ID + "|" + "ed25519\"" + ",algorithm=\"ed25519\"," + "created=\"" + created + "\",expires=\"" + expires + "\",headers=\"(created) (expires)" + " digest\",signature=\"" + signature + "\"";
          
          

//          response.put("Authorization", authHeader);
//          response.put("created", String.valueOf(created));
//          response.put("expires", String.valueOf(expires));
//          response.put("signature", signatureBase64);
//          response.put("digest", digestBase64);
//          response.put("normalized_body", normalizedJson);

//      } catch (Exception e) {
//          response.put("error", "Failed to sign payload: " + e.getMessage());
//      }
//return null;
//      return response;
  }

  @PostMapping("/verify-ondc")
  public Map<String, Object> verifySignature(
          @RequestBody String requestBody,
          @RequestHeader("Authorization") String authorizationHeader,
          @RequestParam("publicKey") String base64PublicKey
  ) {
      Map<String, Object> response = new HashMap<>();
      try {
          ObjectMapper mapper = new ObjectMapper();
          String normalizedJson = mapper.writeValueAsString(mapper.readTree(requestBody));

          // Digest using BLAKE2B-512 (match signing step)
          byte[] hash = CryptoFunctions.generateBlakeHash(normalizedJson);
          String digestBase64 = Base64.getEncoder().encodeToString(hash);

          Map<String, String> headerParts = new HashMap<>();
          for (String part : authorizationHeader.replace("Signature ", "").split(",")) {
              String[] kv = part.trim().split("=", 2);
              String key = kv[0];
              String value = kv[1].replaceAll("^\"|\"$", "");
              headerParts.put(key, value);
          }

          long created = Long.parseLong(headerParts.get("created"));
          long expires = Long.parseLong(headerParts.get("expires"));
          String signatureBase64 = headerParts.get("signature");

          String signingString = "(created): " + created + "\n" +
                                 "(expires): " + expires + "\n" +
                                 "digest: BLAKE2B-512=" + digestBase64;

          byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
          byte[] publicKeyBytes = Base64.getDecoder().decode(base64PublicKey);

          boolean verified = CryptoFunctions.verify(
                  signatureBytes,
                  signingString.getBytes(StandardCharsets.UTF_8),
                  publicKeyBytes
          );

          response.put("verified", verified);
          response.put("digest", digestBase64);
          response.put("signing_string", signingString);

      } catch (Exception e) {
          response.put("error", "Verification failed: " + e.getMessage());
      }

      return response;
  }
  
  @PostMapping("/")
  public void onSearchCallback(@RequestBody String json) {
  	System.out.println("The callback received is : "+json);
  }
  
}
