package security;

import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.KeyAgreement;

public class DHAbstraction {
    private KeyPair IntermediaryKey;
    private KeyAgreement FinalKey;

    public DHAbstraction() throws Exception {
    // Essa vai ser a poarte que vai ser mandada pella internet
        KeyPairGenerator tipoChave = KeyPairGenerator.getInstance("X25519");
        this.IntermediaryKey = tipoChave.generateKeyPair();
        
        // Essa é a parte que não pode sair
        this.FinalKey = KeyAgreement.getInstance("X25519");
        this.FinalKey.init(IntermediaryKey.getPrivate());
    }

    public byte[] senndIntermediaryKey() throws Exception {
        return IntermediaryKey.getPublic().getEncoded();
    }

    public void reciveIntermediaryKey(byte[] chaveResposta) throws Exception{
        KeyFactory keyFactory = KeyFactory.getInstance("X25519");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(chaveResposta);
        PublicKey partnerPublicKey = keyFactory.generatePublic(keySpec);

        this.FinalKey.doPhase(partnerPublicKey, true);
    } 

    public KeyAgreement getFinalKey() {
        return FinalKey;
    }
}
