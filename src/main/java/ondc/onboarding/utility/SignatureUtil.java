package ondc.onboarding.utility;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class SignatureUtil {

    // Sign the request ID using the signing private key
    public static String signRequestId(String requestId, byte[] signingPrivateKeyBytes) throws Exception {
        // Create PrivateKey object
        KeyFactory keyFactory = KeyFactory.getInstance("Ed25519");
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(signingPrivateKeyBytes));

        // Sign the request_id
        Signature signature = Signature.getInstance("Ed25519");
        signature.initSign(privateKey);
        signature.update(requestId.getBytes());

        byte[] signatureBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }
}
