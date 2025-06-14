package dev.phoenixhaven.customac.utils.block;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@AllArgsConstructor
@Getter
public class CollideEntry {
    private final Material block;
    private final BoundingBox boundingBox;
    private final BoundingBox blockBox;
}