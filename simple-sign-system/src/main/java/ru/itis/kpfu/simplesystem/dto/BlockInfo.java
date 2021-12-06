package ru.itis.kpfu.simplesystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bouncycastle.util.encoders.Hex;
import ru.itis.kpfu.simplesystem.utils.SignUtils;

import java.util.List;

/**
 * @author Zagir Dingizbaev
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlockInfo {

    private int blockNum;
    private List<ChainData> data;
    private byte[] prevHash;
    private byte[] hashSign;
    private byte[] dataSign;

    @Override
    public String toString() {
        return "BlockInfo{" +
                "blockNum=" + blockNum +
                ", data=" + data +
                ", prevHash=" + new String(Hex.encode(SignUtils.getHash(this))) +
                ", hashSign=" + new String(Hex.encode(hashSign)) +
                ", dataSign=" + new String(Hex.encode(dataSign)) +
                '}';
    }
}
