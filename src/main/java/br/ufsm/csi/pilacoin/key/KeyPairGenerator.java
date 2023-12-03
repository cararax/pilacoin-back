package br.ufsm.csi.pilacoin.key;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Data
@Component
public class KeyPairGenerator {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @Value("${keypair.algorithm:RSA}")
    private String keyPairAlgorithm;

    @Value("${keypair.size:1024}")
    private int keyPairSize;

    @Value("${keypair.privateKeyPath:private_key.pem}")
    private String privateKeyPath;

    @Value("${keypair.publicKeyPath:public_key.pem}")
    private String publicKeyPath;

    @SneakyThrows
    @PostConstruct
    private void loadKeypair() {
        if (Files.exists(Paths.get(privateKeyPath)) && Files.exists(Paths.get(publicKeyPath))) {
            this.privateKey = readPrivateKey(privateKeyPath);
            this.publicKey = readPublicKey(publicKeyPath);
        } else {
            java.security.KeyPairGenerator keyPairGenerator = java.security.KeyPairGenerator.getInstance(keyPairAlgorithm);
            keyPairGenerator.initialize(keyPairSize);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
            savePrivateKey(privateKey, privateKeyPath);
            savePublicKey(publicKey, publicKeyPath);
        }
    }

    @SneakyThrows
    private PrivateKey readPrivateKey(String path) {
        byte[] keyBytes = Files.readAllBytes(Paths.get(path));
        KeyFactory keyFactory = KeyFactory.getInstance(keyPairAlgorithm);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        return keyFactory.generatePrivate(keySpec);
    }

    @SneakyThrows
    private PublicKey readPublicKey(String path) {
        byte[] keyBytes = Files.readAllBytes(Paths.get(path));
        KeyFactory keyFactory = KeyFactory.getInstance(keyPairAlgorithm);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        return keyFactory.generatePublic(keySpec);
    }

    @SneakyThrows
    private void savePrivateKey(PrivateKey privateKey, String path) {
        byte[] keyBytes = privateKey.getEncoded();
        Files.write(Paths.get(path), keyBytes);
    }

    @SneakyThrows
    private void savePublicKey(PublicKey publicKey, String path) {
        byte[] keyBytes = publicKey.getEncoded();
        Files.write(Paths.get(path), keyBytes);
    }
}
