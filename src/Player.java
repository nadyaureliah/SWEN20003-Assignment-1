import java.text.DecimalFormat;
// class to store and update player's x coordinate, y coordinate, and energy level
public class Player {
    double x;
    double y;
    int energy;
    public Player() { }
    public Player(double x, double y, int energy) {
        setX(x);
        setY(y);
        setEnergy(energy);
    }
    public void updateEnergy(int change) {
        energy += change;
    }
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void setEnergy(int energy) {
        this.energy = energy;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public int getEnergy() {
        return energy;
    }
}
