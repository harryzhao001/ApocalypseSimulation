/*
Abirami Sivakanathan, Janet Cheng, Harry Zhao
ICS4U1 Summative Project
January 26, 2018
 */

// IMPORTANT: BEFORE RUNNING PROGRAM, GO TO LINE 431 (SECOND LINE OF THE COLONY CLASS)
// IN ORDER TO CHANGE THE FILE PATHS FOR THE IMAGES
// To do this, change String path = "C:/Harry/ICS12/ICS4U1Summative/src/"; to the appropriate path

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;  // Needed for ActionListener
import javax.swing.event.*;  // Needed for ActionListener
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.*;
import java.util.Random;
import javax.imageio.ImageIO;

// Main class
class ICS4U1Summative extends JFrame implements ActionListener, ChangeListener
{
    static Colony colony; // declare colony
    static JSlider speedSldr = new JSlider (); // declare slider
    static Timer t; // declare timer
    static Scanner scnr; // declare scanner
    static JButton simulateBtn;
    static JButton knifeBtn;
    static JButton loadGunBtn;
    static JButton shootBtn;
    static JButton shieldBtn;
    static JButton smallBombBtn;
    static JButton bigBombBtn; // declare JButtons

    //======================================================== constructor
    public ICS4U1Summative ()
    {
        // 1... Create/initialize components
        DrawArea board = new DrawArea (850, 900);
        simulateBtn = new JButton ("Start");
        simulateBtn.addActionListener (this);
        knifeBtn = new JButton ("Knife");
        knifeBtn.addActionListener (this);
        loadGunBtn = new JButton ("Load Gun");
        loadGunBtn.addActionListener (this);
        shootBtn = new JButton ("Shoot");
        shootBtn.addActionListener (this);
        shieldBtn = new JButton ("Shield");
        shieldBtn.addActionListener (this);
        smallBombBtn = new JButton ("Small Bomb");
        smallBombBtn.addActionListener (this);
        bigBombBtn = new JButton ("Big Bomb");
        bigBombBtn.addActionListener (this); // add action listeners to all buttons
        speedSldr.addChangeListener (this); // add change listener to slider

        // Add a key listener to receive user input in order to control their character using WASD controls
        this.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed (KeyEvent e){
                for (int x = 0; x < colony.getLength(); x++){
                    for (int y = 0; y < colony.getLength0(); y++){
                        if (colony.getGrid()[x][y].getType() == 1){ // loop through the grid in order to find the player
                            if (e.getKeyChar() == 'w')
                                colony.setDirection(x, y, 'u'); // if user presses 'w', set direction to up
                            else if (e.getKeyChar() == 's')
                                colony.setDirection(x, y, 'd'); // if user presses 's', set direction to down
                            else if (e.getKeyChar() == 'a')
                                colony.setDirection(x, y, 'l'); // if user presses 'a', set direction to left
                            else if (e.getKeyChar() == 'd')
                                colony.setDirection(x, y, 'r'); // if user presses 'd', set direction to right
                        }
                    }
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                 // overwrite method from abstract class
            }

            @Override
            public void keyReleased(KeyEvent e) {
                 // overwrite method from abstract class
            }
        });

        // 2... Create content pane, set layout
        JPanel content = new JPanel ();        // Create a content pane
        content.setLayout (new FlowLayout ()); // Use FlowLayout for panel
        JPanel north1 = new JPanel ();
        north1.setLayout (new FlowLayout ()); // Use FlowLayout for input area
        JPanel north2 = new JPanel ();
        north2.setLayout (new FlowLayout ()); // Use FlowLayout for input area

        // 3... Add the components to the input area.
        north1.add (simulateBtn);
        north1.add (knifeBtn);
        north1.add (loadGunBtn);
        north1.add (shootBtn);
        north1.add (shieldBtn);
        north1.add (smallBombBtn);
        north1.add (bigBombBtn);
        north1.add (speedSldr); // add all GUI components to the JPanels

        content.add (north1, "North"); // Input area
        content.add (north2, "Center");
        content.add (board, "South"); // Output area

        shootBtn.setEnabled(false); // Disable shootBtn, cannot be pressed yet
        
        // 4... Set this window's attributes.
        setContentPane (content);
        pack ();
        setTitle ("Apocalpytic Life Simulation");
        setSize (890, 1030);
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        int x = 0;
        int y = 0;
        this.setLocation(x, y); // sets this JFrame to the top left corner
        this.setVisible(true); // set it to be visible
        
        // Add mouse listener in order to receive user input for bombArea buttons
        addMouseListener(new MouseAdapter(){        
            public void mousePressed (MouseEvent e){
                int x = (e.getX()-2)/42;
                int y = (e.getY()-2)/42 - 2; // find the coordinates of the click
                if (!smallBombBtn.isEnabled()){ 
                    colony.bombArea(y, x, 1); // if small bomb is selected, eradicate an area of radius 2
                } else if (!bigBombBtn.isEnabled()){
                    colony.bombArea(y, x, 2); // if big bomb is select, eradicate an area of radius 4
                }
                repaint(); // repaint afterwards
            }

            // overwrite methods from abstract class
            public void mouseExited (MouseEvent e){

            }
            public void mouseEntered (MouseEvent e){

            }
            public void mouseReleased (MouseEvent e){

            }
            public void mouseClicked (MouseEvent e){

            }
        });
    }

    // checks if the user has died
    public boolean death (){
        boolean death = true; // first assume user is dead
        for (int x = 0; x < colony.getLength(); x++){
            for (int y = 0; y < colony.getLength0(); y++){ // loop through the entire array to find the user
                if (colony.getGrid()[x][y].getType() == 1){
                    death = false; // if the user is detected in the grid, they are alive
                }
            }
        }
        return death; // return whether dead or alive
    }
    
    // checks if the user has won
    public boolean win (){
        boolean win = true;
        for (int x = 0; x < colony.getLength(); x++){
            for (int y = 0; y < colony.getLength0(); y++){ // loop through the entire array to find any enemies
                if (colony.getGrid()[x][y].getType() == 2){
                    win = false; // if an enemy is detected, the user has not won yet
                }
            }
        }
        return win; // return whether won or not won
    }
    
    
    // checks whether the user has any powerups at the current moment
    public boolean neutralState (){
        boolean neutral = false;
        for (int x = 0; x < colony.getLength(); x++){
            for (int y = 0; y < colony.getLength0(); y++){ // loops through the grid to find the play
                if (colony.getGrid()[x][y].getType() == 1 && colony.getGrid()[x][y].getPowerup() == ' '){
                    neutral = true; // returns true if the user has no powerup
                }
            }
        }
        return neutral; // return boolean
    }
    
    // method to set a delay for each new iteration based on the slider
    public void stateChanged (ChangeEvent e)
    {
        if (t != null)
            t.setDelay (400 - 4 * speedSldr.getValue ()); // 0 to 400 ms
        this.setFocusable(true);
        this.requestFocus(); // allows user to keep controlling the player while the grid changes
    }

    public void actionPerformed (ActionEvent e)
    {
        // when the user presses start
        if (e.getActionCommand ().equals ("Start"))
        {
            Movement moveColony = new Movement (colony); // ActionListener
            t = new Timer (200, moveColony); // set up timer
            t.start (); // start simulation
            colony.getInstructions().costList();
            colony.getInstructions().tutorial();
        }
        
        // equips user with a knife by finding the player in the grid, and setting powerup to knife
        if (e.getActionCommand ().equals ("Knife")){
            for (int a = 0; a < colony.getLength(); a++){ // loops through entire array
                for (int b = 0; b < colony.getLength0(); b++){
                    if (colony.getGrid()[a][b].getType() == 1){
                        colony.setPowerup(a, b, 'k'); // when the user is found, set powerup to knife
                    }
                }
            }
            colony.getInstructions().equip("knife");// print text on instructions frame
            knifeBtn.setEnabled(false); // disable other buttons
            loadGunBtn.setEnabled(false);
            shootBtn.setEnabled(false);
            shieldBtn.setEnabled(false);
        }
        
        // equips user with a gun by finding the player in the grid, and setting powerup to gun
        if (e.getActionCommand ().equals ("Load Gun")){
            for (int a = 0; a < colony.getLength(); a++){ // loops through entire array
                for (int b = 0; b < colony.getLength0(); b++){
                    if (colony.getGrid()[a][b].getType() == 1){
                        colony.setPowerup(a, b, 'g'); // when the user is found, set powerup to gun
                    }
                }
            }
            colony.getInstructions().equip("gun");// print text on instructions frame
            shootBtn.setEnabled(true); // disable other buttons
            knifeBtn.setEnabled(false);
            loadGunBtn.setEnabled(false);
            shieldBtn.setEnabled(false);
        }
        
        // shoots the gun ahead of the user
        if (e.getActionCommand ().equals ("Shoot")){
            for (int a = 0; a < colony.getLength(); a++){
                for (int b = 0; b < colony.getLength0(); b++){
                    if (colony.getGrid()[a][b].getType() == 1){ // loops through the array to find the user
                        colony.shoot(a, b, colony.getGrid()[a][b].getDirection()); // shoot in the direction the user is facing
                        colony.setPowerup(a, b, ' '); // set powerup to nothing since gun has been fired
                        colony.setDirection(a, b, colony.getGrid()[a][b].getDirection());
                    }
                }
            }
            shootBtn.setEnabled(false); // disable all other buttons
            knifeBtn.setEnabled(true);
            loadGunBtn.setEnabled(true);
            shieldBtn.setEnabled(true);
            repaint();
        }
        
        // equip user with a shield by finding the player in the grid, and setting powerup to shield
        if (e.getActionCommand ().equals ("Shield")){
            for (int a = 0; a < colony.getLength(); a++){
                for (int b = 0; b < colony.getLength0(); b++){ // loops through entire array
                    if (colony.getGrid()[a][b].getType() == 1){
                        colony.setPowerup(a, b, 's'); // when user is found, set powerup to shield
                    }
                }
            }
            colony.getInstructions().equip("shield"); // print text on instructions frame
            shootBtn.setEnabled(true); // disable other buttons
            knifeBtn.setEnabled(false);
            loadGunBtn.setEnabled(false);
            shieldBtn.setEnabled(false);
        }
        
        // equips the user with a small bomb, they need to tap the field to activate it
        if (e.getActionCommand ().equals ("Small Bomb"))
        {
            smallBombBtn.setEnabled(false);
            bigBombBtn.setEnabled(true); // set small bomb button to disabled to show that it has been pressed
            colony.getInstructions().equip("small bomb");
        }
        
        // equips the user with a big bomb, they need to tap the field to activate it
        if (e.getActionCommand ().equals ("Big Bomb"))
        {
            bigBombBtn.setEnabled(false);
            smallBombBtn.setEnabled(true); // set small bomb button to disabled to show that it has been pressed
            colony.getInstructions().equip("big bomb");
        }
        
        this.setFocusable(true);
        this.requestFocus(); // ensures that player is able to keep controlling the character while after pressing buttons
        repaint ();            // refresh display of deck
    }


    // create a JPanel, which is the main gameplay panel where the actions occur
    class DrawArea extends JPanel
    {
        public DrawArea (int width, int height)
        {
            this.setPreferredSize (new Dimension (width, height)); // set size
        }

        public void paintComponent (Graphics g)
        {
            colony.show (g); // use show method in order to display a visual representation of the grid
        }
    }

    // create a Movement class in order to keep the simulation running
    class Movement implements ActionListener
    {
        private Colony colony;

        // constructor
        public Movement (Colony col)
        {
            colony = col;
        }

        // performs the following action after each iteration
        public void actionPerformed (ActionEvent event)
        {
            colony.movement (); // moves the Units
            repaint ();
            colony.change (); // makes the necessary changes when certain Units come into contact with each other
            repaint ();
            if (neutralState()){
                shootBtn.setEnabled(false);
                knifeBtn.setEnabled(true);
                loadGunBtn.setEnabled(true);
                shieldBtn.setEnabled(true); // enables buttons
            }
            if (death()){
                t.stop(); // when the user has died, stop the simulation
            } else if (win()){
                t.stop(); // when the user has won, stop the simulation
                colony.getInstructions().win(); // print winning message
            }
                
        }
    }

    //======================================================== method main
    public static void main (String[] args)
    {
        scnr = new Scanner(System.in); // declare scanner
        char choice; // declare variable
        System.out.println ("Welcome to the apocalpyse. Select from one of the following difficulty levels: ");
        System.out.println ("--------------");
        System.out.println ("1. Tutorial");
        System.out.println ("2. Easy");
        System.out.println ("3. Medium");
        System.out.println ("4. Hard");
        System.out.println ("5. Impossible"); 
        System.out.println ("6. Demo"); // output the menu, prompt user to select an option
        choice = scnr.nextLine ().charAt(0); // receives user input

        if (choice == '1')
            colony = new Colony (1, 0, 0, 0, 30); // if user inputs 1, create a simulation with 1 zombies and 30 trees
        else if (choice == '2')
            colony = new Colony (1, 1, 1, 1, 20); // if user inputs 2, create a simulation with 1 zombie, 1 vampire, 1 werewolf, 1 alien, and 20 trees
        else if (choice == '3')
            colony = new Colony (2, 2, 2, 2, 15); // if user inputs 3, create a simulation with 2 zombies, 2 vampires, 2 werewolves, 2 aliens, and 15 trees
        else if (choice == '4')
            colony = new Colony (3, 3, 3, 3, 8); // if user inputs 4, create a simulation with 3 zombies, 3 vampires, 3 werewolves, 3 aliens, and 8 trees
        else if (choice == '5')
            colony = new Colony (5, 5, 5, 5, 0); // if user inputs 5, create a simulation with 5 zombies, 5 vampires, 5 werewolves, 5 aliens, and 0 trees
        else if (choice == '6')
            colony= new Colony(); // if user inputs 6, create demo simulation

        ICS4U1Summative window = new ICS4U1Summative ();
        window.setVisible (true);
        
    }
}

// an object of this class represents each of the organism on the grid 
class Units {
    
    // 3 private variables
    private int type; // the type of Unit (either human, enemy, obstacle, or terrain)
    private char direction; // direction that the Unit is travelling
    private char powerup; // for the player: the powerup that the Unit currently has
        // for the enemies: the type of enemy (zombie, vampire, werewolf, or alien)
        // for the obstacle: the type of obstacle (tree, mountain, boulder)
        // for the terrain: the type of terrain (grass, rocks, dirt)
    
    public Units (int t, char d, char p){
        type = t; 
        direction = d; 
        powerup = p; 
    }
    
    // getter methods
    public int getType (){
        return type;
    }
    
    public char getDirection (){
        return direction;
    }
    
    public char getPowerup (){
        return powerup;
    }
    
    // setter methods
    public void setType(int x){
        type = x;
    }
    
    public void setDirection(char x){
        direction = x;
    }
    
    public void setPowerup (char x){
        powerup = x;
    }
}

class Colony
{
    private Units grid[][]; // declare private Units array
    String path = "C:/Users/yzhao/OneDrive/Harry/WINDOWS C FILES/ICS12/ICS4U1Summative/src/"; // declare path that accesses a file
    private Instructions i; // declare private instructions object
    private static Image player, knife, gun, shield, zom, vamp, ww, ali, grass, treesquare, rock, mountain, hill, dirt, boulder; // declare images

    // main constructor
    public Colony (int zombies, int vampires, int werewolves, int aliens, int trees) // parameters for the # of objects in the simulation
    {
        i = new Instructions(); // create instructions object
        Random r = new Random(); // create random object
        grid = new Units [20][20]; // set size of grid
        int[][] zombie = new int[2][zombies];
        int[][] vampire = new int [2][vampires];
        int[][] werewolf = new int[2][werewolves];
        int[][] alien = new int [2][aliens];
        int[][] tree = new int [2][trees]; // create arrays to store random coordinate points
        for (int x = 0; x < zombie[0].length; x++){
            do {
                zombie[0][x] = r.nextInt(grid.length);
                zombie[1][x] = r.nextInt(grid.length); // store two values that will be used as coordinates for zombies
            } while (zombie[0][x] > grid.length/5 && zombie[0][x] < 4*grid.length/5 && zombie[1][x] > grid.length/5 && zombie[1][x] < 4*grid.length/5); // ensures that no zombies will spawn too close to the player at the start
        }
        for (int x = 0; x < vampire[0].length; x++){
            do {
                vampire[0][x] = r.nextInt(grid.length);
                vampire[1][x] = r.nextInt(grid.length); // store two values that will be used as coordinates for vampires
            } while (vampire[0][x] > grid.length/5 && vampire[0][x] < 4*grid.length/5 && vampire[1][x] > grid.length/5 && vampire[1][x] < 4*grid.length/5); // ensures that no vampires will spawn too close to the player at the start
        }
        for (int x = 0; x < werewolf[0].length; x++){
            do {
                werewolf[0][x] = r.nextInt(grid.length);
                werewolf[1][x] = r.nextInt(grid.length); // store two values that will be used as coordinates for werewolves
            } while (werewolf[0][x] > grid.length/5 && werewolf[0][x] < 4*grid.length/5 && werewolf[1][x] > grid.length/5 && werewolf[1][x] < 4*grid.length/5); // ensures that no werewolves will spawn too close to the player at the start
        }
        for (int x = 0; x < alien[0].length; x++){
            do {
                alien[0][x] = r.nextInt(grid.length);
                alien[1][x] = r.nextInt(grid.length); // store two values that will be used as coordinates for aliens
            } while (alien[0][x] > grid.length/5 && alien[0][x] < 4*grid.length/5 && alien[1][x] > grid.length/5 && alien[1][x] < 4*grid.length/5); // ensures that no aliens will spawn too close to the player at the start
        }
        for (int x = 0; x < tree[0].length; x++){
                tree[0][x] = r.nextInt(grid.length);
                tree[1][x] = r.nextInt(grid.length); // store two values that will be used as coordinate for trees
        }
        for (int row = 0 ; row < grid.length ; row++){
            for (int col = 0 ; col < grid [0].length ; col++){ // loop through entire array
                boolean zom = false;
                boolean vam = false;
                boolean wer = false;
                boolean al = false;
                boolean tr = false; // set boolean variables to false
                for (int x = 0; x < zombie[0].length; x++){
                    if (zombie[0][x] == row && zombie[1][x] == col){
                        zom = true; // if the current coordinate is the coordinate of a zombie, set zom to true
                    }
                }
                for (int x = 0; x < vampire[0].length; x++){
                    if (vampire[0][x] == row && vampire[1][x] == col){
                        vam = true; // if the current coordinate is the coordinate of a vampire, set vam to true
                    }
                }
                for (int x = 0; x < werewolf[0].length; x++){
                    if (werewolf[0][x] == row && werewolf[1][x] == col){
                        wer = true; // if the current coordinate is the coordinate of a werewolf, set wer to true
                    }
                }
                for (int x = 0; x < alien[0].length; x++){
                    if (alien[0][x] == row && alien[1][x] == col){
                        al = true; // if the current coordinate is the coordinate of a alien, set al to true
                    }
                }
                for (int x = 0; x < tree[0].length; x++){
                    if (tree[0][x] == row && tree[1][x] == col){
                        tr = true; // if the current coordinate is the coordinate of a tree, set tr to true
                    }
                }
                if (zom){ // if there is a zombie here
                    int d1 = r.nextInt(4);
                    char d2;
                    if (d1 == 0)
                        d2 = 'u';
                    else if (d1 == 1)
                        d2 = 'd';
                    else if (d1 == 2)
                        d2 = 'l';
                    else 
                        d2 = 'r'; // give the zombie a random direction intially
                    grid [row][col] = new Units (2, d2, 'z'); // create a new Units object for the zombie
                } else if (vam){ // if there is a vampire here
                    int d1 = r.nextInt(4);
                    char d2;
                    if (d1 == 0)
                        d2 = 'u';
                    else if (d1 == 1)
                        d2 = 'd';
                    else if (d1 == 2)
                        d2 = 'l';
                    else 
                        d2 = 'r'; // give the vampire a random direction intially
                    grid [row][col] = new Units (2, d2, 'v'); // create a new Units object for the vampire
                } else if (wer){ // if there is a werewolf here
                    int d1 = r.nextInt(4);
                    char d2;
                    if (d1 == 0)
                        d2 = 'u';
                    else if (d1 == 1)
                        d2 = 'd';
                    else if (d1 == 2)
                        d2 = 'l';
                    else 
                        d2 = 'r'; // give the werewolf a random direction intially
                    grid [row][col] = new Units (2, d2, 'w'); // create a new Units object for the werewolf
                } else if (al){ // if there is an alien here
                    int d1 = r.nextInt(4);
                    char d2;
                    if (d1 == 0)
                        d2 = 'u';
                    else if (d1 == 1)
                        d2 = 'd';
                    else if (d1 == 2)
                        d2 = 'l';
                    else 
                        d2 = 'r'; // give the alien a random direction intially
                    grid [row][col] = new Units (2, d2, 'a'); // create a new Units object for the alien
                } else if (tr){ // if there is a tree here
                    if (row >= 13 + col*0.5)
                        grid[row][col] = new Units (4, ' ', 's'); // if located in the rock terrain, turn tree into mountain
                    else if (row <= 8 - (20 - col) * 0.5)
                        grid[row][col] = new Units (4, ' ', 'd'); // if located in the dirt terrain, turn tree into boulder
                    else
                        grid[row][col] = new Units (4, ' ', 'g'); // if located in the grass terrain, stay as tree
                } 
                else { // if empty space
                    if (row >= 13 + col*0.5) 
                        grid[row][col] = new Units (0, ' ', 's'); // fill with rock terrain
                    else if (row <= 8 - (20 - col) * 0.5)
                        grid[row][col] = new Units (0, ' ', 'd'); // fill with dirt terrain
                    else 
                        grid[row][col] = new Units (0, ' ', 'g'); // fill with grass terrain
                }
            }
        }
        grid[grid.length/2][grid[0].length/2] = new Units (1, ' ', ' '); // place the player at the centre of the grid
        try
        {
            player = ImageIO.read (new File (path + "player.gif")); // load file into Image object
            knife = ImageIO.read (new File (path + "knife.gif"));// load file into Image object
            gun = ImageIO.read (new File (path + "gun.gif"));// load file into Image object
            shield = ImageIO.read (new File (path + "shield.gif")); // load file into Image object
            zom = ImageIO.read (new File (path + "zom.gif"));// load file into Image object
            vamp = ImageIO.read (new File (path + "vamp.gif"));// load file into Image object
            treesquare = ImageIO.read (new File (path + "tree.gif"));// load file into Image object
            ww = ImageIO.read (new File (path + "werewolf.gif"));// load file into Image object;
            ali = ImageIO.read (new File (path + "alien.gif"));// load file into Image object;
            grass = ImageIO.read (new File (path + "grass.gif"));// load file into Image object;
            rock = ImageIO.read (new File (path + "rock.gif"));// load file into Image object;
            mountain = ImageIO.read (new File (path + "mountain.gif"));// load file into Image object;
            hill = ImageIO.read (new File (path + "hill.gif"));// load file into Image object;
            dirt = ImageIO.read (new File (path + "dirt.gif"));// load file into Image object;
            boulder = ImageIO.read (new File (path + "boulder.gif"));// load file into Image object;
        }
        catch (IOException e)
        {
            System.out.println ("File not found"); // catch exception if file is not found
        }
    }
    
    public Colony () { // another constructor that simply loads a pre-made pattern to create the boolean array
        grid = new Units [20][20];
        i = new Instructions();
        Random r = new Random();
        int temp[][] = { // demo that contains pre-set values
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,4,4,4,4,0,0,0,0,0,0,0,0},
            {0,0,0,0,2,0,0,0,4,1,0,4,0,0,0,0,0,0,0,0},
            //
            {0,0,0,0,0,0,0,0,4,0,0,4,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,4,4,4,4,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        };
        for (int x = 0; x < grid.length; x++){
            for (int y = 0; y < grid.length; y++){ // loop through the array
                int cell = temp[x][y]; // check each int of the array
                if (cell == 1){
                    grid[x][y] = new Units (1, ' ', ' '); // if the int is 1, there is a player there
                } else if (cell == 2){
                    grid [x][y] = new Units (2, 'l', 'v'); // if the int is 2, there is a vampire there
                } else if (cell == 4){
                    grid[x][y] = new Units (4, ' ', terrain(x,y)); // if the int is 4, there is an obstacle there
                } else if (cell == 0){
                    grid [x][y] = new Units (0, ' ', terrain(x,y)); // if the int is 0, there is terrain there
                }
            }
        }
        try
        {
            player = ImageIO.read (new File (path + "player.gif")); // load file into Image object
            knife = ImageIO.read (new File (path + "knife.gif"));// load file into Image object
            gun = ImageIO.read (new File (path + "gun.gif"));// load file into Image object
            shield = ImageIO.read (new File (path + "shield.gif")); // load file into Image object
            zom = ImageIO.read (new File (path + "zom.gif"));// load file into Image object
            vamp = ImageIO.read (new File (path + "vamp.gif"));// load file into Image object
            treesquare = ImageIO.read (new File (path + "tree.gif"));// load file into Image object
            ww = ImageIO.read (new File (path + "werewolf.gif"));// load file into Image object;
            ali = ImageIO.read (new File (path + "alien.gif"));// load file into Image object;
            grass = ImageIO.read (new File (path + "grass.gif"));// load file into Image object;
            rock = ImageIO.read (new File (path + "rock.gif"));// load file into Image object;
            mountain = ImageIO.read (new File (path + "mountain.gif"));// load file into Image object;
            hill = ImageIO.read (new File (path + "hill.gif"));// load file into Image object;
            dirt = ImageIO.read (new File (path + "dirt.gif"));// load file into Image object;
            boulder = ImageIO.read (new File (path + "boulder.gif"));// load file into Image object;
        }
        catch (IOException e)
        {
            System.out.println ("File not found"); // catch exception if file is not found
        }
    }
    
    // getter for grid
    public Units [][] getGrid (){
        return grid;
    }

    // getter for grid.length
    public int getLength (){
        return grid.length;
    }
    
    // getter for grid[0].length
    public int getLength0 (){
        return grid[0].length;
    }

    // show method that visually represents the elements in the grid
    public void show (Graphics g)
    {
        Random r = new Random();
        for (int row = 0 ; row < grid.length ; row++)
            for (int col = 0 ; col < grid [0].length ; col++) // loop through entire array
            {
                if (grid [row] [col].getType() == 1){ // if the Unit is the player
                    if (grid [row] [col].getPowerup() == ' '){ // if the Unit doesn't currently have a powerup
                        g.setColor(Color.blue); 
                        g.fillRect (col * 42 + 2, row * 42 + 2, 42, 42); // fill the spot with blue
                        g.drawImage(player, col * 42 + 2, row * 42 + 2, 42, 42, null); // draw smiley face over it
                    }
                    else if (grid [row] [col].getPowerup() == 'k'){ // if the Unit currently has a knife
                        g.setColor(Color.orange);
                        g.fillRect (col * 42 + 2, row * 42 + 2, 42, 42); // fill the spot with orange
                        g.drawImage(knife, col * 42 + 2, row * 42 + 2, 42, 42, null); // draw knife over it
                    }
                    else if (grid [row] [col].getPowerup() == 'g'){ // if the Unit currently has a gun
                        g.setColor(Color.cyan);
                        g.fillRect (col * 42 + 2, row * 42 + 2, 42, 42); // fill the spot with cyan
                        g.drawImage(gun, col * 42 + 2, row * 42 + 2, 42, 42, null); // draw gun over it
                    }
                    else if (grid [row] [col].getPowerup() == 's'){ // if the Unit currently has a shield
                        g.setColor(Color.green);
                        g.fillRect (col * 42 + 2, row * 42 + 2, 42, 42); // fill the spot with green
                        g.drawImage(shield, col * 42 + 2, row * 42 + 2, 42, 42, null); // draw shield over it
                    }
                } else if (grid [row][col].getType() == 2){ // if the Unit is a zombie
                    if (grid[row][col].getPowerup() == 'z'){
                        g.setColor(Color.black);
                        g.fillRect (col * 42 + 2, row * 42 + 2, 42, 42); // fill the spot with black
                        g.drawImage(zom, col * 42 + 2, row * 42 + 2, 42, 42, null); // draw zombie over it
                    } else if (grid[row][col].getPowerup() == 'v'){
                        g.setColor(Color.black);
                        g.fillRect (col * 42 + 2, row * 42 + 2, 42, 42); // fill the spot with black
                        g.drawImage(vamp, col * 42 + 2, row * 42 + 2, 42, 42, null); // draw vampire over it
                    } else if (grid[row][col].getPowerup() == 'w'){
                        g.setColor(Color.black);
                        g.fillRect (col * 42 + 2, row * 42 + 2, 42, 42); // fill the spot with black
                        g.drawImage(ww, col * 42 + 2, row * 42 + 2, 42, 42, null); // draw werewolf over it
                    } else if (grid[row][col].getPowerup() == 'a'){
                        g.setColor(Color.black);
                        g.fillRect (col * 42 + 2, row * 42 + 2, 42, 42); // fill the spot with black
                        g.drawImage(ali, col * 42 + 2, row * 42 + 2, 42, 42, null); // draw alien over it
                    } 
                }
                else if (grid[row][col].getType() == 3){ // if the Unit is a unit about to be killed
                    g.setColor (Color.red);
                    g.fillRect (col * 42 + 2, row * 42 + 2, 42, 42); // fill the spot with red
                }
                else if (grid[row][col].getType() == 4){ // if the Unit is an obstacle
                    if (grid[row][col].getPowerup() == 'g'){ // if the unit is a tree
                        g.drawImage(grass, col * 42 + 2, row * 42 + 2, 42, 42, null); // draw grass terrain
                        g.drawImage(treesquare, col * 42 + 2, row * 42 + 2, 42, 42, null); // draw a tree over it
                    }
                    else if (grid[row][col].getPowerup() == 'd'){ // if the unit is a boulder
                        g.drawImage(dirt, col * 42 + 2, row * 42 + 2, 42, 42, null); // draw dirt terrain
                        g.drawImage(boulder, col * 42 + 2, row * 42 + 2, 42, 42, null); // draw a boulder over it
                    }
                    else if (grid[row][col].getPowerup() == 's'){ // if the unit is a mountain
                        g.drawImage(rock, col * 42 + 2, row * 42 + 2, 42, 42, null); // draw rock terrain
                        g.drawImage(mountain, col * 42 + 2, row * 42 + 2, 42, 42, null); // draw a mountain over it
                    }
                }
                else { // if the Unit is empty
                    if (grid[row][col].getPowerup() == 'g') 
                        g.drawImage(grass, col * 42 + 2, row * 42 + 2, 42, 42, null); // draw grass terrain
                    else if (grid[row][col].getPowerup() == 'd')
                        g.drawImage(dirt, col * 42 + 2, row * 42 + 2, 42, 42, null); // draw dirt terrain
                    else if (grid[row][col].getPowerup() == 's')
                        g.drawImage(rock, col * 42 + 2, row * 42 + 2, 42, 42, null); // draw rock terrain
                }
        }
        
    }
    
    // setter for direction of a specific Unit
    public void setDirection (int x, int y, char d){
        grid[x][y].setDirection(d);
    }
    
    // setter for powerup of a specific Unit
    public void setPowerup (int x, int y, char p){
        grid[x][y].setPowerup(p); 
    }
    
    // getter for Instructions object
    public Instructions getInstructions(){
        return i;
    }
    
    // method to count the number of a specific Unit around a specific square on the grid
    public int counter (int r, int c, int x)
    {
        int count = 0; // initialize count
        for (int a = r-1; a <= r+1; a++){
            for (int b = c-1; b <= c+1; b++){ // loops for the 8 spaces around the square
                if (a >= 0 && a < grid.length && b >= 0 && b < grid[0].length){ // makes sure the square is in range of the grid
                    if (grid[a][b].getType() == x){
                        count++; // if the square is of the specific type, increment count
                    }
                }
            }
        }
        return count; 
    }
    
    // method to determine what type of terrain a certain coordinate is on
    public char terrain (int row, int col){
        if (row >= 13 + col*0.5)
            return 's'; // return rock terrain
        else if (row <= 8 - (20 - col) * 0.5)
            return 'd'; // return dirt terrain
        else 
            return 'g'; // return grass terrain
    }
    
    // method that makes all the Units move, according to their current direction
    public void movement (){
        Random rand = new Random(); // declare random object
        Units nextGen[] [] = new Units [grid.length] [grid [0].length]; // create temporary next generation of life forms
        for (int x = 0; x < nextGen.length; x++){
            for (int y = 0; y < nextGen.length; y++){
                nextGen[x][y] = new Units (0, ' ', terrain(x,y)); // fill temp array with empty space for now
            }
        }
        for (int r = 0 ; r < grid.length ; r++){
            for (int c = 0 ; c < grid [0].length ; c++){ // loop for the entire grid
                if (grid[r][c].getType() == 1){ // if the Unit is the player
                    if (grid[r][c].getDirection() == 'u'){ // if the unit is travelling up
                        if (r > 0 && grid[r-1][c].getType() == 0 && nextGen[r-1][c].getType() == 0){ // makes sure unit is not running into anything
                            nextGen[r-1][c] = new Units(1, 'u', grid[r][c].getPowerup());
                            nextGen[r][c] = new Units(0, ' ', terrain(r,c)); // move unit a square up
                        } else {
                            nextGen[r][c] = new Units(1, 'd', grid[r][c].getPowerup()); // if unit is about to bump into something, stop and switch its direction
                        }
                    } else if (grid[r][c].getDirection() == 'd'){// if the unit is travelling down
                        if (r < grid.length - 1 && grid[r+1][c].getType() == 0 && nextGen[r+1][c].getType() == 0){// makes sure unit is not running into anything
                            nextGen[r+1][c] = new Units(1, 'd', grid[r][c].getPowerup());
                            nextGen[r][c] = new Units(0, ' ', terrain(r,c));// move unit a square down
                        } else {
                            nextGen[r][c] = new Units(1, 'u', grid[r][c].getPowerup()); // if unit is about to bump into something, stop and switch its direction
                        }
                    } else if (grid[r][c].getDirection() == 'l'){ // if the unit is travelling left
                        if (c > 0 && grid[r][c-1].getType() == 0 && nextGen[r][c-1].getType() == 0){// makes sure unit is not running into anything
                            nextGen[r][c-1] = new Units(1, 'l', grid[r][c].getPowerup());
                            nextGen[r][c] = new Units(0, ' ', terrain(r,c));// move unit a square left
                        } else {
                            nextGen[r][c] = new Units(1, 'r', grid[r][c].getPowerup());// if unit is about to bump into something, stop and switch its direction
                        }
                    } else if (grid[r][c].getDirection() == 'r'){// if the unit is travelling right
                        if (c < grid.length - 1 && grid[r][c+1].getType() == 0 && nextGen[r][c+1].getType() == 0){ // makes sure unit is not running into anything
                            nextGen[r][c+1] = new Units(1, 'r', grid[r][c].getPowerup());
                            nextGen[r][c] = new Units(0, ' ', terrain(r,c));// move unit a square right
                        } else {
                            nextGen[r][c] = new Units(1, 'l', grid[r][c].getPowerup());// if unit is about to bump into something, stop and switch its direction
                        }
                    } else if (grid[r][c].getDirection() == ' '){ // if unit is not travelling
                        nextGen[r][c] = new Units (1, ' ', grid[r][c].getPowerup()); // make it stay still
                    }
                }
                if (grid[r][c].getType() == 2){ // if the Unit is an enemy
                    if (grid[r][c].getPowerup() == 'z'){ // if unit is a zombie
                        if (grid[r][c].getDirection() == 'u'){ // if it is travelling up
                            if (r > 0 && grid[r-1][c].getType() == 0 && nextGen[r-1][c].getType() == 0){ // make sure it is not bumping into anything
                                int d1 = rand.nextInt(10);
                                char d2;
                                if (d1 == 0)
                                    d2 = 'l';
                                else if (d1 == 1)
                                    d2 = 'r';
                                else
                                    d2 = 'u'; // possibly change direction, 80% of staying straight, 20% chance of turning
                                nextGen[r-1][c] = new Units(2, d2, 'z'); // move zombie up 1 square
                                nextGen[r][c] = new Units(0, ' ', terrain(r,c));
                            } else {
                                nextGen[r][c] = new Units(2, 'd', 'z'); // if about to bump into something, change direction
                            }
                        } else if (grid[r][c].getDirection() == 'd'){// if it is travelling down
                            if (r < grid.length - 1 && grid[r+1][c].getType() == 0 && nextGen[r+1][c].getType() == 0){// make sure it is not bumping into anything
                                int d1 = rand.nextInt(10);
                                char d2;
                                if (d1 == 0)
                                    d2 = 'l';
                                else if (d1 == 1)
                                    d2 = 'r';
                                else
                                    d2 = 'd';// possibly change direction, 80% of staying straight, 20% chance of turning
                                nextGen[r+1][c] = new Units(2, d2, 'z');// move zombie down 1 square
                                nextGen[r][c] = new Units(0, ' ', terrain(r,c));
                            } else {
                                nextGen[r][c] = new Units(2, 'u', 'z');// if about to bump into something, change direction
                            }
                        } else if (grid[r][c].getDirection() == 'l'){// if it is travelling down
                            if (c > 0 && grid[r][c-1].getType() == 0 && nextGen[r][c-1].getType() == 0){// make sure it is not bumping into anything
                                int d1 = rand.nextInt(10);
                                char d2;
                                if (d1 == 0)
                                    d2 = 'u';
                                else if (d1 == 1)
                                    d2 = 'd';
                                else
                                    d2 = 'l';// possibly change direction, 80% of staying straight, 20% chance of turning
                                nextGen[r][c-1] = new Units(2, d2, 'z');// move zombie left 1 square
                                nextGen[r][c] = new Units(0, ' ', terrain(r,c));
                            } else {
                                nextGen[r][c] = new Units(2, 'r', 'z');// if about to bump into something, change direction
                            }
                        } else if (grid[r][c].getDirection() == 'r'){// if it is travelling down
                            if (c < grid.length - 1 && grid[r][c+1].getType() == 0 && nextGen[r][c+1].getType() == 0){// make sure it is not bumping into anything
                                int d1 = rand.nextInt(10);
                                char d2;
                                if (d1 == 0)
                                    d2 = 'u';
                                else if (d1 == 1)
                                    d2 = 'd';
                                else
                                    d2 = 'r';// possibly change direction, 80% of staying straight, 20% chance of turning
                                nextGen[r][c+1] = new Units(2, d2, 'z');// move zombie right 1 square
                                nextGen[r][c] = new Units(0, ' ', terrain(r,c));
                            } else {
                                nextGen[r][c] = new Units(2, 'l', 'z');// if about to bump into something, change direction
                            }
                        }
                    } else if (grid[r][c].getPowerup() == 'v'){ // if the enemy is a vampire
                        if (grid[r][c].getDirection() == 'u'){ // if going up
                            if (r > 0 && c < grid.length - 1 && grid[r-1][c+1].getType() == 0 && nextGen[r-1][c+1].getType() == 0){ // make sure it is not bumping into anything
                                nextGen[r-1][c+1] = new Units(2, 'u', 'v');
                                nextGen[r][c] = new Units (0, ' ', terrain(r,c)); // move up-right
                            } else {
                                nextGen[r][c] = new Units (2, 'r', 'v'); // if about to bump into something, change direction
                            }
                        } else if (grid[r][c].getDirection() == 'd'){ // if going down
                            if (r < grid.length - 1 && c > 0 && grid[r+1][c-1].getType() == 0 && nextGen[r+1][c-1].getType() == 0){// make sure it is not bumping into anything
                                nextGen[r+1][c-1] = new Units(2, 'd', 'v');
                                nextGen[r][c] = new Units (0, ' ', terrain(r,c)); // move down-left
                            } else {
                                nextGen[r][c] = new Units (2, 'l', 'v');// if about to bump into something, change direction
                            }
                        } else if (grid[r][c].getDirection() == 'l'){ // if going left
                            if (r > 0 && c > 0 && grid[r-1][c-1].getType() == 0 && nextGen[r-1][c-1].getType() == 0){// make sure it is not bumping into anything
                                nextGen[r-1][c-1] = new Units(2, 'l', 'v');
                                nextGen[r][c] = new Units (0, ' ', terrain(r,c)); // move left-up
                            } else {
                                nextGen[r][c] = new Units (2, 'u', 'v');// // if about to bump into something, change direction
                            }
                        } else if (grid[r][c].getDirection() == 'r'){ // if going right
                            if (r < grid.length - 1 && c < grid.length - 1 && grid[r+1][c+1].getType() == 0 && nextGen[r+1][c+1].getType() == 0){// make sure it is not bumping into anything
                                nextGen[r+1][c+1] = new Units(2, 'r', 'v');
                                nextGen[r][c] = new Units (0, ' ', terrain(r,c)); // move right-down
                            } else {
                                nextGen[r][c] = new Units (2, 'd', 'v'); // if about to bump into something, change direction
                            }
                        }
                    } else if (grid[r][c].getPowerup() == 'w'){ // if enemy is a werewolf
                        if (grid[r][c].getDirection() == 'u'){// if moving up
                            if (r > 0 && grid[r-1][c].getType() == 0 && nextGen[r-1][c].getType() == 0 && r%5 == 0){// make sure it is not bumping into anything
                                nextGen[r-1][c] = new Units(2, 'l', 'w');
                                nextGen[r][c] = new Units (0, ' ', terrain(r,c)); // if the row is divisible by 5, turn
                            } else if (r > 0 && grid[r-1][c].getType() == 0 && nextGen[r-1][c].getType() == 0 && r%5 != 0){
                                nextGen[r-1][c] = new Units(2, 'u', 'w');
                                nextGen[r][c] = new Units (0, ' ', terrain(r,c)); // if the row is not divisible by 5, keep going straight
                            } else {
                                nextGen[r][c] = new Units (2, 'd', 'w'); // if about to bump into something, change direction
                            }
                        } else if (grid[r][c].getDirection() == 'd'){// if going down
                            if (r < grid.length - 1 && grid[r+1][c].getType() == 0 && nextGen[r+1][c].getType() == 0 && r%5 == 0){// make sure it is not bumping into anything
                                nextGen[r+1][c] = new Units(2, 'r', 'w');
                                nextGen[r][c] = new Units (0, ' ', terrain(r,c));// if the row is divisible by 5, turn
                            } else if (r < grid.length - 1 && grid[r+1][c].getType() == 0 && nextGen[r+1][c].getType() == 0 && r%5 != 0){
                                nextGen[r+1][c] = new Units(2, 'd', 'w');
                                nextGen[r][c] = new Units (0, ' ', terrain(r,c));// if the row is not divisible by 5, keep going straight
                            } else {
                                nextGen[r][c] = new Units (2, 'u', 'w');// if about to bump into something, change direction
                            }
                        } else if (grid[r][c].getDirection() == 'l'){// if going left
                            if (c > 0 && grid[r][c-1].getType() == 0 && nextGen[r][c-1].getType() == 0 && c%5 == 0){// make sure it is not bumping into anything
                                nextGen[r][c-1] = new Units(2, 'u', 'w');
                                nextGen[r][c] = new Units (0, ' ', terrain(r,c));// if the col is divisible by 5, turn
                            } else if (c > 0 && grid[r][c-1].getType() == 0 && nextGen[r][c-1].getType() == 0 && c%5 != 0){
                                nextGen[r][c-1] = new Units(2, 'l', 'w');
                                nextGen[r][c] = new Units (0, ' ', terrain(r,c));// if the row is not divisible by 5, keep going straight
                            } else {
                                nextGen[r][c] = new Units (2, 'r', 'w');// if about to bump into something, change direction
                            }
                        } else if (grid[r][c].getDirection() == 'r'){ // if going right
                            if (c < grid.length - 1 && grid[r][c+1].getType() == 0 && nextGen[r][c+1].getType() == 0 && c%5 == 0){// make sure it is not bumping into anything
                                nextGen[r][c+1] = new Units(2, 'd', 'w');
                                nextGen[r][c] = new Units (0, ' ', terrain(r,c));// if the col is divisible by 5, turn
                            } else if (c < grid.length - 1 && grid[r][c+1].getType() == 0 && nextGen[r][c+1].getType() == 0 && c%5 != 0){
                                nextGen[r][c+1] = new Units(2, 'r', 'w');
                                nextGen[r][c] = new Units (0, ' ', terrain(r,c));// if the row is not divisible by 5, keep going straight
                            } else {
                                nextGen[r][c] = new Units (2, 'l', 'w');// if about to bump into something, change direction
                            }
                        }
                    } else if (grid[r][c].getPowerup() == 'a'){ // if enemy is an alien
                        if (grid[r][c].getDirection() == 'u'){ // if it is travelling up
                            if (r > 0 && grid[r-1][c].getType() == 0 && nextGen[r-1][c].getType() == 0){ // make sure it is not bumping into anything
                                int d1 = rand.nextInt(4);
                                char d2;
                                if (d1 == 0)
                                    d2 = 'l';
                                else if (d1 == 1)
                                    d2 = 'r';
                                else if (d1 == 2)
                                    d2 = 'd';
                                else
                                    d2 = 'u'; // change to random direction
                                nextGen[r-1][c] = new Units(2, d2, 'a'); // move zombie up 1 square
                                nextGen[r][c] = new Units(0, ' ', terrain(r,c));
                            } else {
                                nextGen[r][c] = new Units(2, 'd', 'a'); // if about to bump into something, change direction
                            }
                        } else if (grid[r][c].getDirection() == 'd'){// if it is travelling down
                            if (r < grid.length - 1 && grid[r+1][c].getType() == 0 && nextGen[r+1][c].getType() == 0){// make sure it is not bumping into anything
                                int d1 = rand.nextInt(4);
                                char d2;
                                if (d1 == 0)
                                    d2 = 'l';
                                else if (d1 == 1)
                                    d2 = 'r';
                                else if (d1 == 2)
                                    d2 = 'd';
                                else
                                    d2 = 'u'; // change to random direction
                                nextGen[r+1][c] = new Units(2, d2, 'a');// move zombie down 1 square
                                nextGen[r][c] = new Units(0, ' ', terrain(r,c));
                            } else {
                                nextGen[r][c] = new Units(2, 'u', 'a');// if about to bump into something, change direction
                            }
                        } else if (grid[r][c].getDirection() == 'l'){// if it is travelling down
                            if (c > 0 && grid[r][c-1].getType() == 0 && nextGen[r][c-1].getType() == 0){// make sure it is not bumping into anything
                                int d1 = rand.nextInt(4);
                                char d2;
                                if (d1 == 0)
                                    d2 = 'l';
                                else if (d1 == 1)
                                    d2 = 'r';
                                else if (d1 == 2)
                                    d2 = 'd';
                                else
                                    d2 = 'u'; // change to random direction
                                nextGen[r][c-1] = new Units(2, d2, 'a');// move zombie left 1 square
                                nextGen[r][c] = new Units(0, ' ', terrain(r,c));
                            } else {
                                nextGen[r][c] = new Units(2, 'r', 'a');// if about to bump into something, change direction
                            }
                        } else if (grid[r][c].getDirection() == 'r'){// if it is travelling down
                            if (c < grid.length - 1 && grid[r][c+1].getType() == 0 && nextGen[r][c+1].getType() == 0){// make sure it is not bumping into anything
                                int d1 = rand.nextInt(4);
                                char d2;
                                if (d1 == 0)
                                    d2 = 'l';
                                else if (d1 == 1)
                                    d2 = 'r';
                                else if (d1 == 2)
                                    d2 = 'd';
                                else
                                    d2 = 'u'; // change to random direction
                                nextGen[r][c+1] = new Units(2, d2, 'a');// move zombie right 1 square
                                nextGen[r][c] = new Units(0, ' ', terrain(r,c));
                            } else {
                                nextGen[r][c] = new Units(2, 'l', 'a');// if about to bump into something, change direction
                            }
                        }
                    }
                }
                if (grid[r][c].getType() == 3){ // if unit is about to die
                    nextGen[r][c] = new Units (0, ' ', terrain(r, c)); // unit becomes empty
                }
                if (grid[r][c].getType() == 4){ // if unit is an obstacle
                    nextGen[r][c] = new Units (4, ' ', terrain(r, c)); // stay as an obstacle
                }
            }
        }
        grid = nextGen; // overwrite grid with the temporary array
    }

    public void change ()
    {
        for (int x = 0; x < grid.length; x++){
            for (int y = 0; y < grid.length; y++){ // loop for the entire array
                if (grid[x][y].getType() == 2){ // if the current element is a zombie
                    for (int a = x-1; a <= x+1; a++){
                        for (int b = y-1; b <= y+1; b++){ // loop for all neighbouring squares
                            if (a >= 0 && a < grid.length && b >= 0 && b < grid[0].length){ // check that neighbouring squares in the range of the array
                                if (grid[a][b].getType() == 1){ // if the neighbouring square is the player
                                    if (grid[a][b].getPowerup() == ' ' || grid[a][b].getPowerup() == 'g'){ // if player has no powerup or gun
                                        grid[a][b].setType(3); // player dies and loses the game
                                        if (grid[x][y].getPowerup() == 'z')
                                            i.lose("zombie"); 
                                        else if (grid[x][y].getPowerup() == 'v')
                                            i.lose("vampire");
                                        else if (grid[x][y].getPowerup() == 'w')
                                            i.lose("werewolf");
                                        else if (grid[x][y].getPowerup() == 'a')
                                            i.lose("alien"); // print lose text, depending on what killed the player
                                    } else if (grid[a][b].getPowerup() == 'k'){ // if player has knife powerup
                                        grid[a][b].setPowerup(' ');
                                        grid[x][y].setType(3); // enemy dies, player loses powerup
                                        if (grid[x][y].getPowerup() == 'z')
                                            i.kill("knife", "a zombie");
                                        else if (grid[x][y].getPowerup() == 'v')
                                            i.kill("knife", "a vampire");
                                        else if (grid[x][y].getPowerup() == 'w')
                                            i.kill("knife", "a werewolf");
                                        else if (grid[x][y].getPowerup() == 'a')
                                            i.kill("knife", "an alien"); // print text instructions, depending on what was killed
                                    } else if (grid[a][b].getPowerup() == 's'){ // if player has shield powerup
                                        grid[a][b].setPowerup(' '); // player loses powerup
                                        if (grid[x][y].getPowerup() == 'z')
                                            i.shieldBreak("a zombie");
                                        else if (grid[x][y].getPowerup() == 'v')
                                            i.shieldBreak("a vampire");
                                        else if (grid[x][y].getPowerup() == 'w')
                                            i.shieldBreak("a werewolf");
                                        else if (grid[x][y].getPowerup() == 'a')
                                            i.shieldBreak("an alien"); // print text instructions, depending on what broke the shield
                                    } 
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // eradicates a circle, with specific parameters for the center of the circle and its radius
    public void bombArea (int xCenter, int yCenter, int radius) 
    {
        for (int x = 0; x < grid.length; x++){
            for (int y = 0; y < grid[0].length; y++){ // loops through entire array
                int deltaX = Math.abs(xCenter - x); // finds distance between x-value of current cell and x-value of the centre coordinate
                int deltaY = Math.abs(yCenter - y); // finds distance between y-value of current cell and y-value of the centre coordinate
                double distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)); // uses pythagorean theorem to find the distance between current cell and centre coordinate
                if (distance <= radius){ 
                    grid[x][y] = new Units (3, ' ', ' ');  // if the element is within the radius, it will die, no matter what it is
                }
            }
        }
    }
    
    // shoots a line ahead of the user
    public void shoot (int x, int y, char direction){
        if (direction == 'u'){ // if player is going upwards
            for (int a = x - 1; a >= 0; a--){
                grid[a][y] = new Units (3, ' ', ' '); // eradicate everything in the column above of the player
            }
        } else if (direction == 'd'){ // if player is going downwards
            for (int a = x + 1; a < grid.length; a++){
                grid[a][y] = new Units (3, ' ', ' '); // eradicate everything in the column below the player
            }
        } else if (direction == 'l'){ // if player is going left
            for (int a = y - 1; a >= 0; a--){
                grid[x][a] = new Units (3, ' ', ' ');// eradicate everything in the row to the left of the player
            }
        } else if (direction == 'r'){ // if player is going right 
            for (int a = y + 1; a < grid.length; a++){
                grid[x][a] = new Units (3, ' ', ' '); // eradicate everything in the row to the right the player
            }
        }
    }

}

// a separate JFrame that helps the user play the game by offering text suggestions and tips
class Instructions extends JFrame {
    
    // declare Jlabels
    static JLabel text1 = new JLabel();
    static JLabel text2 = new JLabel();
    static JLabel text3 = new JLabel();
    static JLabel text4 = new JLabel();
    static JLabel text5 = new JLabel();
    static JLabel text6 = new JLabel();
    static JLabel text7 = new JLabel();
    static JLabel text8 = new JLabel();
    static JLabel text9 = new JLabel();
    static JLabel text10 = new JLabel();
    static JLabel resource = new JLabel();
    static JLabel space = new JLabel();
    static JLabel list1 = new JLabel();
    static JLabel list2 = new JLabel();
    static JLabel list3 = new JLabel();
    static JLabel list4 = new JLabel();
    static JLabel list5 = new JLabel();
    
    // constructor
    public Instructions (){
        
        // initialize JPanels
        JPanel content = new JPanel();
        JPanel west = new JPanel();
        JPanel east = new JPanel();
        
        // set empty texts for now
        text1.setText("");
        text2.setText("");
        text3.setText("");
        text4.setText("");
        text5.setText("");
        text6.setText("");
        text7.setText("");
        text8.setText("");
        text9.setText("");
        text10.setText("");
        
        // set font
        text1.setFont(new Font("Comic Sans", Font.PLAIN, 22));
        text2.setFont(new Font("Comic Sans", Font.PLAIN, 22));
        text3.setFont(new Font("Comic Sans", Font.PLAIN, 22));
        text4.setFont(new Font("Comic Sans", Font.PLAIN, 22));
        text5.setFont(new Font("Comic Sans", Font.PLAIN, 22));
        text6.setFont(new Font("Comic Sans", Font.PLAIN, 22));
        text7.setFont(new Font("Comic Sans", Font.PLAIN, 22));
        text8.setFont(new Font("Comic Sans", Font.PLAIN, 22));
        text9.setFont(new Font("Comic Sans", Font.PLAIN, 22));
        text10.setFont(new Font("Comic Sans", Font.PLAIN, 22));
        
        // add JLabels to JPanels
        west.add(text1);
        west.add(text2);
        west.add(text3);
        west.add(text4);
        west.add(text5);
        west.add(text6);
        west.add(text7);
        west.add(text8);
        west.add(text9);
        west.add(text10);
        east.add(space);
        east.add(resource);
        east.add(list1);
        east.add(list2);
        east.add(list3);
        east.add(list4);
        east.add(list5);

        // set layouts and add JPanel to main Jpanel
        content.setLayout (new FlowLayout ()); 
        west.setLayout(new BoxLayout(west, BoxLayout.Y_AXIS));
        east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
        content.add(west, "West");
        content.add(east, "East");
        
        // set attributes
        setContentPane (content);
        pack ();
        setTitle ("Instructions");
        setSize (1100, 700);
        setVisible(true);
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE); 
        
        // puts the JFrame in the upper right corner of the screen, so that it does not overlap with the main game
            int x = 890;
            int y = 0;
            this.setLocation(x, y);
            this.setVisible(true);
            
        // print the starting message
        start();
    }
    
    // print at the start of the game
    public void start(){
        replaceText("It is the year 2128. After a risky lab experiment gone wrong, horrible creatures have taken over the world.");
        replaceText("You have stayed hidden for a long time, but they have finally found you.");
        replaceText("No one else is coming to help you. You must survive on your own.");
        replaceText("Use WASD keyboard controls in order to move your character (the smiley face).");
        replaceText("W is up, S is down, A is left, and D is right.");
        replaceText("Try to avoid your enemies, since they will kill you if you get too close to them without a powerup.");
        replaceText("You can also adjust the speed of movement using the slider.");
        replaceText("When you are ready, press start to begin the challenge.");
    }
    
    // print after the user presses start, to teach them how to play
    public void tutorial(){
        replaceText("The simulation has begun.");
        replaceText("The enemies look hungry, you can't survive just by walking around!");
        replaceText("Press one of the buttons to select a powerup.");
    }
    
    // print when the user selects a weapon to equip
    public void equip(String weapon){
        replaceText("You have equipped yourself with a " + weapon + "! Check the powerup list to learn how to use your powerup.");
    }
    
    // print when the user successfully defeats an enemy using a weapon
    public void kill(String weapon, String enemy){
        replaceText("You have killed " + enemy + " using a " + weapon + "! To equip yourself again, select another weapon.");
    }
    
    // print when the user's shield is broken by an enemy attack
    public void shieldBreak(String enemy){
        replaceText("Your shield was broken by " + enemy + " attack. To equip yourself again, select another weapon.");
    }
    
    // print when the user defeats all the enemies and wins
    public void win(){
        replaceText("Congratulations! You've defeated all the enemies!");
    }
    
    // print when the user is defeated by an enemy
    public void lose(String enemy){
        if (enemy.equals("zombie"))
            replaceText("Your brains were eaten by a zombie... Better luck next time!");
        else if (enemy.equals("vampire"))
            replaceText("A vampire sucked the blood out of your neck... Better luck next time!");
        else if (enemy.equals("werewolf"))
            replaceText("You were mauled by a werewolf... Better luck next time!");
        else if (enemy.equals("alien"))
            replaceText("You were abducted by an alien... Better luck next time!");
    }
    
    // a list of powerups, in order to give info about each specific powerup
    public void costList (){
        space.setFont(new Font("Verdana", Font.PLAIN, 22));
        resource.setFont(new Font("Verdana", Font.PLAIN, 15));
        list1.setFont(new Font("Verdana", Font.PLAIN, 15));
        list2.setFont(new Font("Verdana", Font.PLAIN, 15));
        list3.setFont(new Font("Verdana", Font.PLAIN, 15));
        list4.setFont(new Font("Verdana", Font.PLAIN, 15));
        list5.setFont(new Font("Verdana", Font.PLAIN, 15));
        space.setText(" ");
        resource.setText("List of Powerups");
        list1.setText("Knife - arm yourself with a close range weapon, if you come within 1 square of an enemy, you will kill it");
        list2.setText("Gun - load a gun in order to kill any enemies straight ahead of you, press 'Shoot' to fire");
        list3.setText("Shield - protect yourself with a temporary shield that gives immunity from one enemy attack");
        list4.setText("Small Bomb - drop a bomb with a 2 square diameter in order to kill nearby enemies, click on the field to drop the bomb");
        list5.setText("Big Bomb - drop a bomb with a 4 square diameter in order to kill nearby enemies, click on the field to drop the bomb");
    }
    
    // a general method to give out commands and print text
    private void replaceText(String s){
        text1.setText(text2.getText());
        text2.setText(text3.getText());
        text3.setText(text4.getText());
        text4.setText(text5.getText());
        text5.setText(text6.getText());
        text6.setText(text7.getText());
        text7.setText(text8.getText());
        text8.setText(text9.getText());
        text9.setText(text10.getText());
        text10.setText(s); // most recent output appears at the bottom, shifting all other messages up until it disappears
    }
}