package br.ufsm.csi.pilacoin.pila.miner;

import br.ufsm.csi.pilacoin.key.KeyPairGenerator;
import br.ufsm.csi.pilacoin.dificuldade.model.Dificuldade;
import br.ufsm.csi.pilacoin.pila.model.PilaCoin;
import br.ufsm.csi.pilacoin.pila.service.PilaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@RequiredArgsConstructor
public class PilaMiner implements Runnable {
    private Thread thread;
    private final String threadName;
    private final SecureRandom random = new SecureRandom();
    private AtomicReference<Dificuldade> difficulty;
    private PilaCoin pilaCoin;
    private final String algorithm = "SHA-256";
    private static final int radix = 16;
    private String username = "carara-schmitzhaus";
    private final KeyPairGenerator keyPairGenerator;
    private final PilaService pilaService;
    private static ObjectMapper mapper = new ObjectMapper();

    public PilaMiner(String name, AtomicReference<Dificuldade> difficulty, KeyPairGenerator keyPairGenerator, PilaService pilaService) {
        threadName = name;
        this.difficulty = difficulty;
        this.keyPairGenerator = keyPairGenerator;
        this.pilaService = pilaService;
        log.info("Creating " + threadName);
    }

    @SneakyThrows
    public static boolean hashMeetsDifficulty(PilaCoin pilaCoin, Dificuldade difficulty) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String pilaJson = mapper.writeValueAsString(pilaCoin);
        byte[] digest = md.digest(pilaJson.getBytes(StandardCharsets.UTF_8));

        BigInteger hashNum = new BigInteger(digest).abs();
        BigInteger difficultyNum = new BigInteger(difficulty.getDificuldade(), radix).abs();

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

    @SneakyThrows
    private BigInteger generateNonce() {
        byte[] nonce = new byte[32];
        random.nextBytes(nonce);
        String nonceHex = bytesToHex(nonce);
        return new BigInteger(MessageDigest.getInstance(algorithm).digest(nonceHex.getBytes(StandardCharsets.UTF_8))).abs();
    }

    @SneakyThrows
    public void run() {
        log.info("Running " + threadName);
        while (true) {
            pilaCoin = createPilaCoin();
            if (isPilaValid(pilaCoin)) {
                pilaService.publishMinedPila(pilaCoin);
                log.info("Thread {} mined a coin.", threadName);
            }
        }
    }

    private boolean isPilaValid(PilaCoin pilaCoin) {
        return hashMeetsDifficulty(pilaCoin, difficulty.get());
    }

    private PilaCoin createPilaCoin() {
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