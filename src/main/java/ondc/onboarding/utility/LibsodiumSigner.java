package ondc.onboarding.utility;
import org.abstractj.kalium.keys.SigningKey;
import java.util.Base64;

public class LibsodiumSigner {
    public static String sign(String requestId, String base64PrivateKey) {
        byte[] privateKey = Base64.getDecoder().decode(base64PrivateKey);
        SigningKey signingKey = new SigningKey(privateKey);  // Libsodium style 32/64 byte key
        byte[] signature = signingKey.sign(requestId.getBytes());
        
        System.out.println("first signature : "+signature);
        System.out.println("Second Signature : "+Base64.getEncoder().encode(privateKey, signature));
        
        return Base64.getEncoder().encodeToString(signature);
    }
}


//import com.sun.jna.Memory;
//import com.sun.jna.Pointer;
//import com.sun.jna.ptr.ByteByReference;
//import org.libsodium.jni.crypto.Signature;
//
//import java.util.Base64;
//
//public class LibsodiumSigner {
//    public static String sign(String requestId, String base64PrivateKey) {
//        // Decode the base64 private key to byte array
//        byte[] privateKey = Base64.getDecoder().decode(base64PrivateKey);
//        
//        // Check if the private key is 32 bytes (required for Ed25519)
//        if (privateKey.length != 32) {
//            throw new IllegalArgumentException("Private key must be 32 bytes.");
//        }
//
//        // Initialize Libsodium's Ed25519 signer (libsodium-jna)
//        Signature signer = new Signature();
//        
//        // Create a memory buffer to hold the signature output
//        Memory signatureOutput = new Memory(Signature.BYTES);
//
//        // Sign the requestId (which should be a byte array)
//        byte[] requestIdBytes = requestId.getBytes();
//        signer.sign(privateKey, requestIdBytes, requestIdBytes.length, signatureOutput);
//
//        // Convert signature to base64 and return
//        byte[] signature = signatureOutput.getByteArray(0, Signature.BYTES);
//        return Base64.getEncoder().encodeToString(signature);
//    }
//}

