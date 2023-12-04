package br.ufsm.csi.pilacoin.pila;

import br.ufsm.csi.pilacoin.key.KeyPairGenerator;
import br.ufsm.csi.pilacoin.mock.dto.Dificuldade;
import br.ufsm.csi.pilacoin.mock.dto.PilaCoin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
    @Value("${pilacoin.username:carara-schmitzhaus}")
    private String username;
    private final KeyPairGenerator keyPairGenerator;
    private final PilaService pilaService;
    private ObjectMapper mapper = new ObjectMapper();


    public MinerWorker(String name, AtomicReference<Dificuldade> difficulty, KeyPairGenerator keyPairGenerator, PilaService pilaService) {
        threadName = name;
        this.difficulty = difficulty;
        this.keyPairGenerator = keyPairGenerator;
        this.pilaService = pilaService;
        log.info("Creating " + threadName);
    }

//    private boolean hashMeetsDifficulty(PilaCoin pilaCoin, Dificuldade difficulty) {
//        BigInteger hashNum = new BigInteger(String.valueOf(pilaCoin), radix);
//        BigInteger difficultyNum = new BigInteger(difficulty.getDificuldade(), radix);
//        return hashNum.compareTo(difficultyNum) < 0;
//    }

    @SneakyThrows
    boolean hashMeetsDifficulty(PilaCoin pilaCoin, Dificuldade difficulty) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String pilaJson = mapper.writeValueAsString(pilaCoin);
        byte[] digest = md.digest(pilaJson.getBytes(StandardCharsets.UTF_8));

        BigInteger hashNum = new BigInteger(digest).abs();
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

    private BigInteger generateNonce() throws NoSuchAlgorithmException {
        byte[] nonce = new byte[32];
        random.nextBytes(nonce);
        String nonceHex = bytesToHex(nonce);
        return new BigInteger(MessageDigest.getInstance(algorithm).digest(nonceHex.getBytes(StandardCharsets.UTF_8))).abs();
    }

    public void run() {
        log.info("Running " + threadName);
        try {
            while (true) {
                pilaCoin = createPilaCoin();
                if (hashMeetsDifficulty(pilaCoin, difficulty.get())) {
                    pilaService.publishMinedPila(pilaCoin);
                    log.info("Thread {} mined a coin.", threadName);
                    break;
                }
            }
        } catch (NoSuchAlgorithmException e) {
            log.error("Error while processing", e);
        }
    }

    private PilaCoin createPilaCoin() throws NoSuchAlgorithmException {
        return PilaCoin.builder()
                .nomeCriador(username)
                .chaveCriador(keyPairGenerator.getPublicKey().toString().getBytes(StandardCharsets.UTF_8))
                .dataCriacao(new Date().getTime())
                .nonce(generateNonce().toString())
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
