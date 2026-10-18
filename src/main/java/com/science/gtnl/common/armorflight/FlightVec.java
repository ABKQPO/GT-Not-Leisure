package com.science.gtnl.common.armorflight;

public record FlightVec(double x, double y, double z) {

    public static final FlightVec ZERO = new FlightVec(0, 0, 0);

    public FlightVec add(FlightVec o) {
        return new FlightVec(x + o.x, y + o.y, z + o.z);
    }

    public FlightVec add(double dx, double dy, double dz) {
        return new FlightVec(x + dx, y + dy, z + dz);
    }

    public FlightVec subtract(FlightVec o) {
        return new FlightVec(x - o.x, y - o.y, z - o.z);
    }

    public FlightVec scale(double k) {
        return new FlightVec(x * k, y * k, z * k);
    }

    public FlightVec multiply(double kx, double ky, double kz) {
        return new FlightVec(x * kx, y * ky, z * kz);
    }

    public double lengthSqr() {
        return x * x + y * y + z * z;
    }

    public double length() {
        return Math.sqrt(lengthSqr());
    }

    public double horizontalDistanceSqr() {
        return x * x + z * z;
    }

    public double distanceTo(FlightVec o) {
        return subtract(o).length();
    }

    public FlightVec normalize() {
        double len = length();
        return len < 1.0E-8 ? ZERO : scale(1 / len);
    }

    public FlightVec flat() {
        return new FlightVec(x, 0, z);
    }
}
