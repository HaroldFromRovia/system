package ru.itis.kpfu.simplesystem;

import java.security.KeyPair;
import java.util.List;

public interface BlockChainProvider<T> {
    List<T> provide(int size, KeyPair keyPair);
}
