package ru.itis.kpfu.simplesystem.neuroClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

public class TestClient {

    /* хеш для подписи */
    public static final String digest = "1F4A121121123123189002BC";
    /* 16-ричное представление публичного ключа сервиса */
    public static String publicKey16 = "30819f300d06092a864886f70d010101050003818d0030818902818100a811365d2f3642952751029edf87c8fa2aeb6e0feafcf800190a7dd2cf750c63262f6abd8ef52b251c0e10291d5e2f7e6682de1aae1d64d4f9b242050f898744ca300a44c4d8fc8af0e7a1c7fd9b606d7bde304b29bec01fbef554df6ba1b7b1ec355e1ff68bd37f3d40fb27d1aa233fe3dd6b63f7241e734739851ce8c590f70203010001";
    public static String publicKey64 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCoETZdLzZClSdRAp7fh8j6KutuD+r8+AAZCn3Sz3UMYyYvar2O9SslHA4QKR1eL35mgt4arh1k1PmyQgUPiYdEyjAKRMTY/Irw56HH/ZtgbXveMEspvsAfvvVU32uht7HsNV4f9ovTfz1A+yfRqiM/491rY/ckHnNHOYUc6MWQ9wIDAQAB";
    public static String privateKey16 = "30820277020100300d06092a864886f70d0101010500048202613082025d02010002818100a811365d2f3642952751029edf87c8fa2aeb6e0feafcf800190a7dd2cf750c63262f6abd8ef52b251c0e10291d5e2f7e6682de1aae1d64d4f9b242050f898744ca300a44c4d8fc8af0e7a1c7fd9b606d7bde304b29bec01fbef554df6ba1b7b1ec355e1ff68bd37f3d40fb27d1aa233fe3dd6b63f7241e734739851ce8c590f7020301000102818019a116533b81e0c1720f6e80dfc18b2a6b251fd1999a1e0f95c1fdd20a04e570b14005dd0651e8d46de4cf1e15f668f3b39a3d588038c7499b124df30b9d835d007173897de36ed60b741be71d9c0c92539bc5d8d8c81c217e35768afa4f5deb208adc074646747b64bddf401d24fc27d989073b811145e3f26453e50dff9391024100e8ba466033c207db1c886a385e0933bb592bd279f436dcdd809516e6c9ff2a3bce740668c9ec306f536694e79f47aac1854e59eed67ae9b04a4387ceff2fd8cb024100b8dfa8debdbd3b732a3b108b0669985f8c064bdedf219b98ddbaf3d43c57cd13caea5d247e3099bdf6d553e9182d76fc8f002a7ee7ac27277ee0010f5cc95f0502405c90f2bb02fb330ab8cf9d882c06f6f796a1328765568904e546e7a4064622b3450bab1cf4eba7dc2fcd067f1640782f9ee92a263053152e146bc5d1d478607f024100a5cd310e52ec9b9cb58b3352fbf5480aa8f21ca94d443edfe23c00e19f1cbf77b8cc67ac8adfdb825b1507a44d62b90bd8d435488b4e01ef920223f13b2dfe61024100cf1d242fb3c7bf46f6c222756122db17326894108af890d645cc787f38798b4c0e8500625bf66b548855768583ef53c21be5f8d46d9076552fe32f75c3461e53";
    public static String privateKey64 = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKgRNl0vNkKVJ1ECnt+HyPoq624P6vz4ABkKfdLPdQxjJi9qvY71KyUcDhApHV4vfmaC3hquHWTU+bJCBQ+Jh0TKMApExNj8ivDnocf9m2Bte94wSym+wB++9VTfa6G3sew1Xh/2i9N/PUD7J9GqIz/j3Wtj9yQec0c5hRzoxZD3AgMBAAECgYAZoRZTO4HgwXIPboDfwYsqayUf0ZmaHg+Vwf3SCgTlcLFABd0GUejUbeTPHhX2aPOzmj1YgDjHSZsSTfMLnYNdAHFziX3jbtYLdBvnHZwMklObxdjYyBwhfjV2ivpPXesgitwHRkZ0e2S930AdJPwn2YkHO4ERRePyZFPlDf+TkQJBAOi6RmAzwgfbHIhqOF4JM7tZK9J59Dbc3YCVFubJ/yo7znQGaMnsMG9TZpTnn0eqwYVOWe7WeumwSkOHzv8v2MsCQQC436jevb07cyo7EIsGaZhfjAZL3t8hm5jduvPUPFfNE8rqXSR+MJm99tVT6RgtdvyPACp+56wnJ37gAQ9cyV8FAkBckPK7AvszCrjPnYgsBvb3lqEyh2VWiQTlRuekBkYis0ULqxz066fcL80GfxZAeC+e6SomMFMVLhRrxdHUeGB/AkEApc0xDlLsm5y1izNS+/VICqjyHKlNRD7f4jwA4Z8cv3e4zGesit/bglsVB6RNYrkL2NQ1SItOAe+SAiPxOy3+YQJBAM8dJC+zx79G9sIidWEi2xcyaJQQiviQ1kXMeH84eYtMDoUAYlv2a1SIVXaFg+9Twhvl+NRtkHZVL+MvdcNGHlM=";

    /* алгоритм ключа сервиса */
    public static final String KEY_ALGORITHM = "RSA";
    /* алгоритм подписи, формируемой сервисом */
    public static final String SIGN_ALGORITHM = "SHA256withRSA";

    public static String weights = "";

    public static void main(String[] args) {
        getChain(null);
        sendBlock();
    }

    public static void sendBlock() {

        SignService service = new SignService();

        String prevHash = null;
        if (BlockChain.chain.size() > 0) {
            try {
                prevHash = new String(Hex.encode(service.getHash(BlockChain.chain.get(BlockChain.chain.size() - 1))));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        String dataStr = "{\"w11\":\"0.40450895\",\"w12\":\"0.6409668\",\"w21\":\"-0.0528092\",\"w22\":\"0.69250953\",\n" +
                "\"v11\":\"-0.748313\",\"v12\":\"0.56010175\",\"v13\":\"0.891308\",\"v21\":\"0.93825704\",\"v22\":\"-0.34686327\",\"v23\":\"-0.15610261\",\n" +
                "\"w1\":\"0.20637804\",\"w2\":\"-0.78078914\",\"w3\":\"1.3415401 \"\n" +
                ",\"e\":\"0.24078458186488258\" ,\"publickey\":\"" + publicKey16 + "\"}";

        try {

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Hex.decode(privateKey16.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey priKey = keyFactory.generatePrivate(keySpec);

            byte[] sign = service.generateRSAPSSSignature(priKey, dataStr.getBytes());
            String signature = new String(Hex.encode(sign));
            System.out.println(signature);

            String block = "{\"prevhash\":\"" + prevHash + "\",\"data\":" + dataStr + ",\"signature\":\"" + signature + "\",\"info\":\"Dingizbaev Zagir, 11-804\"}";

            URL url = new URL("http://188.93.211.195/newblock?block=" + URLEncoder.encode(block, "UTF-8"));

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");

            int rcode = con.getResponseCode();

            if (rcode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String response = reader.readLine();

                ObjectMapper mapper = new ObjectMapper();
                NewBlockResponse resp = mapper.readValue(response, NewBlockResponse.class);
                if (resp.getStatus() == 0) {
                    BlockModel newBlock = resp.getBlock();
                    System.out.println(newBlock);
                } else {
                    System.out.println(resp.getStatusString());
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    // Запрос блокчейна
    public static void getChain(String hash) {
        try {
//            URL url = new URL("http://188.93.211.195/newblock?block=");
            URL url = new URL("http://188.93.211.195/chain" + (hash != null ? "?hash=" + hash : ""));

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");

            int rcode = con.getResponseCode();

            if (rcode == 200) {
                ObjectMapper mapper = new ObjectMapper();
                List<BlockModel> blockChain = mapper.readValue(con.getInputStream(), new TypeReference<>() {
                });

                if (blockChain != null) {
                    BlockChain.chain = blockChain;
                    BlockChain.chain.forEach(blockModel -> {
                        System.out.println(blockModel.toString());
                    });
                }

                System.out.println(publicKey16);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean verify(String publicKeyHexStr, byte[] data, String signHexStr) {
        Security.addProvider(new BouncyCastleProvider());

        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHM, "BC");

            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Hex.decode(publicKeyHexStr));
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
            signature.initVerify(pubKey);

            signature.update(data);

            return signature.verify(Hex.decode(signHexStr));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}