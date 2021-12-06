package ru.itis.kpfu.simplesystem;

import ru.itis.kpfu.simplesystem.dto.BlockInfo;
import ru.itis.kpfu.simplesystem.dto.ChainData;
import ru.itis.kpfu.simplesystem.utils.SignUtils;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SingedBlockChainProvider implements BlockChainProvider<BlockInfo>{

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
            blockInfo.setHashSign(SignUtils.getSignature(keyPair.getPrivate(), prevHash));
            blockInfo.setDataSign(SignUtils.getSignature(keyPair.getPrivate(), data.toString().getBytes()));

            blockChain.add(blockInfo);
        }

        return blockChain;
    }

}
