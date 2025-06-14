package dev.phoenixhaven.customac.utils.player;

import cc.funkemunky.api.utils.MathHelper;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

@Getter
@Setter
public class CustomLocation {
    private double x, y, z;
    private float pitch, yaw;
    private boolean ground;

    public CustomLocation(double x, double y, double z, float pitch, float yaw, boolean ground) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.ground = ground;
    }

    public CustomLocation(Location location, boolean ground) {
        this(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw(), ground);
    }

    public CustomLocation clone() {
        return new CustomLocation(this.x, this.y, this.z, this.pitch, this.yaw, this.ground);
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public double distanceToHorizontal(CustomLocation customLocation) {
        double f = this.x - customLocation.getX();
        double f2 = this.z - customLocation.getZ();

        return MathHelper.sqrt_double(f * f + f2 * f2);
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }
}
