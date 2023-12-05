package org.wolflink.minecraft.plugin.siriuxa.task.regions;

import lombok.Data;
import lombok.Setter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Data
public class SpawnZone {
    private List<Location> spawnLocations = new ArrayList<>();
}
