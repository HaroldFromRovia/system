package ru.itis.kpfu.simplesystem;

import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import ru.itis.kpfu.simplesystem.dto.BlockInfo;
import ru.itis.kpfu.simplesystem.dto.ChainData;
import ru.itis.kpfu.simplesystem.utils.SignUtils;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.Security;
import java.util.Collections;
import java.util.List;

public class ArbiterMain {

    @SneakyThrows
    public static void main(String[] args) {
        BlockChainProvider<BlockInfo> provider = new ArbiterBlockChainProvider();
        Security.addProvider(new BouncyCastleProvider());

        List<BlockInfo> blocks = provider.provide(8, null);
        blocks.get(1).setData(Collections.singletonList(new ChainData()));
        SignUtils.verified(blocks, getPublicKey());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("result.txt"));) {
            for (BlockInfo result : blocks) {
                writer.write(result + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    private static String getPublicKey() {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://188.93.211.195/public"))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }
}
