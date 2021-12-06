package ru.itis.kpfu.simplesystem.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChainData {

    private Instant instant = Instant.now();
    private UUID uuid = UUID.randomUUID();
}
