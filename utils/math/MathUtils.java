package dev.phoenixhaven.customac.utils.math;

import dev.phoenixhaven.customac.utils.player.CustomLocation;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.List;

import static cc.funkemunky.api.utils.MathUtils.yawTo180D;

@UtilityClass
public class MathUtils {
    public double hypot(double... value) {
        double total = 0;

        for (double val : value) {
            total += (val * val);
        }

        return sqrt(total);
    }

    public double sqrt(double number) {
        if (number == 0) return 0;

        double t;
        double squareRoot = number / 2;

        do {
            t = squareRoot;
            squareRoot = (t + (number / t)) / 2;
        } while ((t - squareRoot) != 0);

        return squareRoot;
    }

    public static List<Entity> getEntitiesWithinRadius(Location location, double radius) {
        double x = location.getX();
        double z = location.getZ();

        World world = location.getWorld();
        List<Entity> entities = new LinkedList<>();

        for (int locX = (int) Math.floor((x - radius) / 16.0D);
             locX <= (int) Math.floor((x + radius) / 16.0D); locX++) {
            for (int locZ = (int) Math.floor((z - radius) / 16.0D);
                 locZ <= (int) Math.floor((z + radius) / 16.0D); locZ++) {
                if (!world.isChunkLoaded(locX, locZ)) continue;

                for (Entity entity : world.getChunkAt(locX, locZ).getEntities()) {
                    if (entity == null || entity.getLocation()
                            .distanceSquared(location) > radius * radius) continue;
                    entities.add(entity);
                }
            }
        }

        return entities;
    }

    public static double getAngleRotation(CustomLocation loc1, CustomLocation loc2) {
        if (loc1 == null || loc2 == null) return -1;

        Vector playerRotation = new Vector(loc1.getYaw(), loc1.getPitch(), 0.0f);
        loc1.setY(0);
        loc2.setY(0);

        float[] rot = getRotations(loc1, loc2);
        Vector expectedRotation = new Vector(rot[0], rot[1], 0);
        return yawTo180D(playerRotation.getX() - expectedRotation.getX());
    }

    public double angleDivisor(double angle) {
        return angleDivisor(angle, 45);
    }

    public double angleDivisor(double angle, double value) {
        while (angle > 0) {
            angle -= value;
        }
        return angle;
    }

    public static float[] getRotations(CustomLocation one, CustomLocation two) {
        double diffX = two.getX() - one.getX();
        double diffZ = two.getZ() - one.getZ();
        double diffY = two.getY() + 2.0 - 0.4 - (one.getY() + 2.0);
        double dist = sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-Math.atan2(diffY, dist) * 180.0 / Math.PI);
        return new float[]{yaw, pitch};
    }

    public boolean isInBounds(double value, double min, double max) {
        return max > value && value > min;
    }

    public boolean isInBoundsEqual(double value, double min, double max) {
        return max >= value && value >= min;
    }
}
