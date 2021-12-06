package ru.itis.kpfu.simplesystem;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONObject;
import ru.itis.kpfu.simplesystem.dto.BlockInfo;
import ru.itis.kpfu.simplesystem.dto.ChainData;
import ru.itis.kpfu.simplesystem.utils.SignUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArbiterBlockChainProvider implements BlockChainProvider<BlockInfo> {

    @Getter
    @Setter
    private final String arbiterUrl = "http://188.93.211.195/ts";

    @SneakyThrows
    @Override
    public List<BlockInfo> provide(int size, KeyPair keyPair) {
        List<BlockInfo> blockChain = new ArrayList<>();
        byte[] prevHash = null;

        for (int i = 0; i < size; i++) {
            var data = new ChainData();
            BlockInfo blockInfo = BlockInfo.builder()
                    .blockNum(i)
                    .data(Collections.singletonList(data))
                    .prevHash(prevHash)
                    .build();

            prevHash = SignUtils.getHash(blockInfo);
            String response = sendDigest(new String(Hex.encode(prevHash)));
            JSONObject jsonObject = new JSONObject(response).getJSONObject("timeStampToken");
            byte[] sign = jsonObject.get("signature").toString().getBytes();
            String timeStamp = jsonObject.get("ts").toString();

            byte[] signData = new byte[timeStamp.getBytes().length + prevHash.length];
            System.arraycopy(timeStamp.getBytes(), 0, signData, 0, timeStamp.getBytes().length);
            System.arraycopy(prevHash, 0, signData, timeStamp.getBytes().length, prevHash.length);

            blockInfo.setHashSign(sign);
            blockInfo.setDataSign(signData);

            blockChain.add(blockInfo);
        }

        return blockChain;
    }

    @SneakyThrows
    public String sendDigest(String digest) {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(arbiterUrl + "?digest=" + digest))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }
}
