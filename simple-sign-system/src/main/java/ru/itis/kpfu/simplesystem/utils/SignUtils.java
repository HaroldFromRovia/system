package ru.itis.kpfu.simplesystem.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import ru.itis.kpfu.simplesystem.dto.BlockInfo;
import ru.itis.kpfu.simplesystem.dto.ChainData;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

@UtilityClass
public class SignUtils {

    public static final String DIGEST_ALGORITHM = "SHA-256";
    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGN_ALGORITHM = "SHA256withRSAandMGF1";

    @SneakyThrows
    public KeyPair generateKeyPair() {
        Security.addProvider(new BouncyCastleProvider());
        var generator = KeyPairGenerator.getInstance(KEY_ALGORITHM, "BC");
        generator.initialize(1024, new SecureRandom());

        return generator.genKeyPair();
    }

    @SneakyThrows
    public byte[] getSignature(PrivateKey privateKey, byte[] input) {
        Signature signature = Signature.getInstance(SIGN_ALGORITHM, "BC");
        signature.initSign(privateKey);
        signature.update(input);

        return signature.sign();
    }

    @SneakyThrows
    public boolean verifySignature(PublicKey publicKey, byte[] input, byte[] encSignature) {
        Signature signature = Signature.getInstance(SIGN_ALGORITHM, "BC");
        signature.initVerify(publicKey);
        signature.update(input);

        return signature.verify(encSignature);
    }

    @SneakyThrows
    public byte[] getHash(BlockInfo blockInfo) {
        StringBuilder info = new StringBuilder();
        MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM, "BC");

        for (ChainData data : blockInfo.getData()) {
            info.append(data.toString());
        }

        return digest.digest(
                Arrays.concatenate(blockInfo.getPrevHash(), info.toString().getBytes(StandardCharsets.UTF_8)));
    }

    @SneakyThrows
    public static boolean verified(List<BlockInfo> blocks, PublicKey publicKey) {
        byte[] prevHash = SignUtils.getHash(blocks.get(0));
        for (int i = 1; i < blocks.size(); i++) {

            if (!java.util.Arrays.equals(prevHash, blocks.get(i).getPrevHash())) {
                System.err.println("Hashes doesn't match. Block num = " + i);
                return false;
            }
            prevHash = SignUtils.getHash(blocks.get(i));
            if (!SignUtils.verifySignature(publicKey, prevHash, blocks.get(i).getHashSign())) {
                System.err.println("Hash signatures doesn't match. Block num = " + i);
                return false;
            }

            if (!SignUtils.verifySignature(publicKey, blocks.get(i).getData().get(0).toString().getBytes(), blocks.get(i).getDataSign())) {
                System.err.println("Data signatures doesn't match. Block num = " + i);
                return false;
            }
        }

        System.out.println("Chain verified");
        return true;
    }

    @SneakyThrows
    public boolean verified(List<BlockInfo> blocks, String publicKey) {
        Security.addProvider(new BouncyCastleProvider());
        Signature signature = Signature.getInstance(SIGN_ALGORITHM, "BC");

        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Hex.decode(publicKey));
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
        signature.initVerify(pubKey);

        byte[] prevHash = SignUtils.getHash(blocks.get(0));

        for (int i = 1; i < blocks.size(); i++) {
            if (!java.util.Arrays.equals(prevHash, blocks.get(i).getPrevHash())) {
                System.err.println("Hashes doesn't match. Block num = " + i);
                return false;
            }

            if (!SignUtils.verifySignature(pubKey, blocks.get(i).getData().get(0).toString().getBytes(), blocks.get(i).getDataSign())) {
                System.err.println("Data signatures doesn't match. Block num = " + i);
                return false;
            }
        }
        System.out.println("Chain verified");

        return true;
    }
}

