package ru.itis.kpfu.simplesystem;

import ru.itis.kpfu.simplesystem.dto.BlockInfo;
import ru.itis.kpfu.simplesystem.dto.ChainData;
import ru.itis.kpfu.simplesystem.utils.SignUtils;

import java.io.*;
import java.util.Collections;
import java.util.List;

/**
 * @author Zagir Dingizbaev
 */

public class SignedDataMain {

    public static void main(String[] args) {
        var keyPair = SignUtils.generateKeyPair();
        BlockChainProvider<BlockInfo> provider = new SingedBlockChainProvider();
        List<BlockInfo> blocks = provider.provide(10, keyPair);
        SignUtils.verified(blocks, keyPair.getPublic());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("result.txt"));) {
            for (BlockInfo result : blocks)
                writer.write(result + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
