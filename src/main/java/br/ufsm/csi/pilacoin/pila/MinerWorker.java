package br.ufsm.csi.pilacoin.pila;

import br.ufsm.csi.pilacoin.key.KeyPairGenerator;
import br.ufsm.csi.pilacoin.mock.dto.Dificuldade;
import br.ufsm.csi.pilacoin.mock.dto.PilaCoin;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@RequiredArgsConstructor
public class MinerWorker implements Runnable {
    private Thread thread;
    private final String threadName;
    private final SecureRandom random = new SecureRandom();
    private AtomicReference<Dificuldade> difficulty;
    private PilaCoin pilaCoin;
    private final String algorithm = "SHA-256";
    private final int radix = 16;
    @Value("${pilacoin.username}")
    private String username;
    private final KeyPairGenerator keyPairGenerator;
    private final PilaService pilaService;

    public MinerWorker(String name, AtomicReference<Dificuldade> difficulty, KeyPairGenerator keyPairGenerator, PilaService pilaService) {
        threadName = name;
        this.difficulty = difficulty;
        this.keyPairGenerator = keyPairGenerator;
        this.pilaService = pilaService;
        log.info("Creating " + threadName);
    }

    private boolean hashMeetsDifficulty(String hash, Dificuldade difficulty) {
        BigInteger hashNum = new BigInteger(hash, radix);
        BigInteger difficultyNum = new BigInteger(difficulty.getDificuldade(), radix);
        return hashNum.compareTo(difficultyNum) < 0;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String generateNonce() {
        byte[] nonce = new byte[32];
        random.nextBytes(nonce);
        return bytesToHex(nonce);
    }

    public void run() {
        log.info("Running " + threadName);
        try {
            while (true) {
                String nonce = generateNonce();
                if (hashMeetsDifficulty(nonce, difficulty.get())) {
                    pilaCoin = createPilaCoin(nonce, username);
                    pilaService.publishMinedPila(pilaCoin);
                    log.info("Thread {} mined a coin.", threadName);
                    break;
                }
            }

        } catch (NoSuchAlgorithmException e) {
            log.error("Error while processing", e);
        }
    }

    private PilaCoin createPilaCoin(String nonce, String username) throws NoSuchAlgorithmException {
        return PilaCoin.builder()
                .nomeCriador(username)
                .chaveCriador(keyPairGenerator.getPublicKey().toString().getBytes(StandardCharsets.UTF_8))
                .dataCriacao(new Date().getTime())
                .nonce(new BigInteger(MessageDigest.getInstance(algorithm).digest(nonce.getBytes(StandardCharsets.UTF_8))).abs().toString())
                .build();
    }

    public void start() {
        log.info("Starting " + threadName);
        if (thread == null) {
            thread = new Thread(this, threadName);
            thread.start();
        }
    }

}
