package dev.phoenixhaven.customac.utils.vector;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Vec5D {
    private double x, y, z, ya, pi;

    public Vec5D() {
        this(0, 0, 0, 0, 0);
    }

    public Vec5D(double x, double y, double z, double ya, double pi) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.ya = ya;
        this.pi = pi;
    }

    public Vec5D copy() {
        return new Vec5D(this.x, this.y, this.z, this.ya, this.pi);
    }
}