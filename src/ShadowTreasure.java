import bagel.*;
import bagel.Font;
import bagel.Image;
import bagel.DrawOptions;
import bagel.Window;
import bagel.util.Point;
import bagel.util.Colour;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * @author Nadya Aurelia Herryyanto (1185814)
 */
public class ShadowTreasure extends AbstractGame {
    private final Image background;
    private final Image player;
    private final Image sandwich;
    private final Image zombie;
    private final Font font = new Font("res/font/DejaVuSans-Bold.ttf", 20);
    private static final double STEP_SIZE = 10;
    private static final double MEET_DIST = 50;
    private static final Point TEXT_POINT = new Point(20,760);
    private static Point SANDW_POINT;
    private static Point ZOMBIE_POINT;
    private static double sandwichX, sandwichY, zombieX, zombieY;
    private double playerX, playerY;
    private double playerDirectionX = 0;
    private double playerDirectionY = 0;
    private int energyLevel;
    private int frame = 0;
    private boolean sandwichTouch = false;

    Player oPlayer = new Player();
    Entities oSandwich = new Entities();
    Entities oZombie = new Entities();

    DrawOptions black = new DrawOptions();

    // For rounding double number; use this to print the location of the player
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public static void printInfo(double x, double y, int e) {
        System.out.println(df.format(x) + "," + df.format(y) + "," + e);
    }
    
    public ShadowTreasure() throws IOException {
        // Initialize attributes and elements
        super(1024,768, "Shadow Treasure");
        this.loadEnvironment("res/IO/environment.csv");
        background = new Image("res/images/background.png");
        player = new Image("res/images/player.png");
        sandwich = new Image("res/images/sandwich.png");
        zombie = new Image("res/images/zombie.png");
    }

    /**
     * Load from input file
     */
    private void loadEnvironment(String filename){
        // Code here to read from the file and set up the environment

        String row;
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(filename));
            while((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                for (int i = 0; i < data.length; i++) {
                    data[i] = data[i].replaceAll("[^a-zA-Z0-9]", "");
                }
                if (data[0].equals("Player")) {
                    this.playerX = Double.parseDouble(data[1]);
                    this.playerY = Double.parseDouble(data[2]);
                    this.energyLevel = Integer.parseInt(data[3]);
                    // Store values in Player oPlayer class
                    oPlayer.setX(playerX);
                    oPlayer.setY(playerY);
                    oPlayer.setEnergy(energyLevel);
                } else if (data[0].equals("Zombie")) {
                    this.zombieX = Double.parseDouble(data[1]);
                    this.zombieY = Double.parseDouble(data[2]);
                    // Store values in Entities oZombie class
                    oZombie.setX(zombieX);
                    oZombie.setY(zombieY);
                    ZOMBIE_POINT = new Point((int)zombieX, (int)zombieY);
                } else if (data[0].equals("Sandwich")) {
                    this.sandwichX = Double.parseDouble(data[1]);
                    this.sandwichY = Double.parseDouble(data[2]);
                    // Store values in Entities oSandwich class
                    oSandwich.setX(sandwichX);
                    oSandwich.setY(sandwichY);
                    SANDW_POINT = new Point((int)sandwichX, (int)sandwichY);
                }
            }
        } catch (IOException e) { e.printStackTrace();} // If file not found, throw IOException error
    }

    /**
     * Performs a state update.
     */
    @Override
    public void update(Input input) {
        // Logic to update the game, as per specification must go here
        // Draw elements of game according to updated X and Y coordinates
        background.drawFromTopLeft(0,0);
        player.draw(oPlayer.getX(), oPlayer.getY());
        if (sandwichTouch == false) {sandwich.draw(oSandwich.getX(), oSandwich.getY());}
        zombie.draw(zombieX,zombieY);
        font.drawString("energy: "+oPlayer.getEnergy(), TEXT_POINT.x, TEXT_POINT.y, black.setBlendColour(0.0, 0.0, 0.0));
        frame++;
        if(frame%10 == 0) { // Simulate every tick = 10 frames
            double pX = oPlayer.getX();
            double pY = oPlayer.getY();
            double sX = oSandwich.getX();
            double sY = oSandwich.getY();
            double zX = oZombie.getX();
            double zY = oZombie.getY();
            font.drawString("energy: " + oPlayer.getEnergy(), TEXT_POINT.x, TEXT_POINT.y, black.setBlendColour(0.0, 0.0, 0.0));
            // Update energy by +5 if Player meets Sandwich
            if (new Point(pX, pY).distanceTo(SANDW_POINT) < MEET_DIST && !sandwichTouch) {
                oPlayer.updateEnergy(5);
                font.drawString("energy: " + oPlayer.getEnergy(), TEXT_POINT.x, TEXT_POINT.y, black.setBlendColour(0.0, 0.0, 0.0));
                sandwichTouch = true;
            }
            // Update energy by -3 if Player meets Zombie
            if (new Point(pX, pY).distanceTo(ZOMBIE_POINT) < MEET_DIST) {
                oPlayer.updateEnergy(-3);
                font.drawString("energy: " + oPlayer.getEnergy(), TEXT_POINT.x, TEXT_POINT.y, black.setBlendColour(0.0, 0.0, 0.0));
                Window.close();
            }
            // Print Player game updates per tick
            printInfo(oPlayer.getX(), oPlayer.getY(), oPlayer.getEnergy());
            // Set Player direction towards Zombie if energy level is >= 3
            if (oPlayer.getEnergy() >= 3) {
                setPlayerDirection(new Point(zX, zY));
                oPlayer.setX(pX + STEP_SIZE * playerDirectionX);
                oPlayer.setY(pY + STEP_SIZE * playerDirectionY);
            } else { // Set Player direction towards Sandwich if < 3
                setPlayerDirection(new Point(sX, sY));
                oPlayer.setX(pX + STEP_SIZE * playerDirectionX);
                oPlayer.setY(pY + STEP_SIZE * playerDirectionY);
            }
        }
    }

    /**
     * The entry point for the program.
     */
    // Method to set player's direction, derived from Workshop 5
    public void setPlayerDirection(Point dest) {
        double len = new Point(oPlayer.getX(), oPlayer.getY()).distanceTo(dest);
        playerDirectionX = (dest.x - oPlayer.getX()) / len;
        playerDirectionY = (dest.y - oPlayer.getY()) / len;
    }

    public static void main(String[] args) throws IOException {
        ShadowTreasure game = new ShadowTreasure();
        game.run();
    }
}
