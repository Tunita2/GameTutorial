package levels;

import static utilz.Constants.EnemyConstants.CRABBY;
import static utilz.Constants.EnemyConstants.PINKSTAR;
import static utilz.Constants.EnemyConstants.SHARK;
import static utilz.Constants.ObjectConstants.BARREL;
import static utilz.Constants.ObjectConstants.BLUE_POTION;
import static utilz.Constants.ObjectConstants.BOX;
import static utilz.Constants.ObjectConstants.CANNON_LEFT;
import static utilz.Constants.ObjectConstants.CANNON_RIGHT;
import static utilz.Constants.ObjectConstants.RED_POTION;
import static utilz.Constants.ObjectConstants.SPIKE;
import static utilz.Constants.ObjectConstants.TREE_ONE;
import static utilz.Constants.ObjectConstants.TREE_THREE;
import static utilz.Constants.ObjectConstants.TREE_TWO;
import static utilz.HelpMethods.GetCrabs;
import static utilz.HelpMethods.GetLevelData;
import static utilz.HelpMethods.GetPlayerSpawn;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Crabby;
import entities.Pinkstar;
import entities.Shark;
import main.Game;
import objects.BackgroundTree;
import objects.Cannon;
import objects.GameContainer;
import objects.Grass;
import objects.Potion;
import objects.Spike;
import utilz.HelpMethods;

public class Level {
	private BufferedImage img;
	private int[][] lvlData;
//	private ArrayList<Crabby> crabs;
//	private ArrayList<Potion> potions;
//	private ArrayList<Spike> spikes;
//	private ArrayList<GameContainer> containers;
//	private ArrayList<Cannon> cannons;
//	Add
	private ArrayList<Crabby> crabs = new ArrayList<>();
	private ArrayList<Pinkstar> pinkstars = new ArrayList<>();
	private ArrayList<Shark> sharks = new ArrayList<>();
	private ArrayList<Potion> potions = new ArrayList<>();
	private ArrayList<Spike> spikes = new ArrayList<>();
	private ArrayList<GameContainer> containers = new ArrayList<>();
	private ArrayList<Cannon> cannons = new ArrayList<>();
	private ArrayList<BackgroundTree> trees = new ArrayList<>();
	private ArrayList<Grass> grass = new ArrayList<>();
	
	private int lvlTilesWide;
	private int maxTilesOffset;
	private int maxLvlOffsetX;
	private Point playerSpawn;

	public Level(BufferedImage img) {
		this.img = img;
//		Remove
//		createLevelData();
//		createEnemies();
//		createPotions();
//		createContainers();
//		createSpikes();
//		createCannons();
//		calcLvlOffsets();
//		calcPlayerSpawn();
//		Add
		lvlData = new int[img.getHeight()][img.getWidth()];
		loadLevel();
		calcLvlOffsets();
		
	}
//	Add 5 methods
	private void loadLevel() {

		for (int y = 0; y < img.getHeight(); y++)
			for (int x = 0; x < img.getWidth(); x++) {
				Color c = new Color(img.getRGB(x, y));
				int red = c.getRed();
				int green = c.getGreen();
				int blue = c.getBlue();

				loadLevelData(red, x, y);
				loadEntities(green, x, y);
				loadObjects(blue, x, y);
			}
	}
	
	private void loadLevelData(int redValue, int x, int y) {
		if (redValue >= 50)
			lvlData[y][x] = 0;
		else
			lvlData[y][x] = redValue;
		switch (redValue) {
		case 0, 1, 2, 3, 30, 31, 33, 34, 35, 36, 37, 38, 39 -> 
		grass.add(new Grass((int) (x * Game.TILES_SIZE), (int) (y * Game.TILES_SIZE) - Game.TILES_SIZE, getRndGrassType(x)));
		}
	}
	
	private void loadEntities(int greenValue, int x, int y) {
		switch (greenValue) {
		case CRABBY -> crabs.add(new Crabby(x * Game.TILES_SIZE, y * Game.TILES_SIZE));
		case PINKSTAR -> pinkstars.add(new Pinkstar(x * Game.TILES_SIZE, y * Game.TILES_SIZE));
		case SHARK -> sharks.add(new Shark(x * Game.TILES_SIZE, y * Game.TILES_SIZE));
		case 100 -> playerSpawn = new Point(x * Game.TILES_SIZE, y * Game.TILES_SIZE);
		}
	}
	
	private void loadObjects(int blueValue, int x, int y) {
		switch (blueValue) {
		case RED_POTION, BLUE_POTION -> potions.add(new Potion(x * Game.TILES_SIZE, y * Game.TILES_SIZE, blueValue));
		case BOX, BARREL -> containers.add(new GameContainer(x * Game.TILES_SIZE, y * Game.TILES_SIZE, blueValue));
		case SPIKE -> spikes.add(new Spike(x * Game.TILES_SIZE, y * Game.TILES_SIZE, SPIKE));
		case CANNON_LEFT, CANNON_RIGHT -> cannons.add(new Cannon(x * Game.TILES_SIZE, y * Game.TILES_SIZE, blueValue));
		case TREE_ONE, TREE_TWO, TREE_THREE -> trees.add(new BackgroundTree(x * Game.TILES_SIZE, y * Game.TILES_SIZE, blueValue));
		}
	}
	
	private int getRndGrassType(int xPos) {
		return xPos % 2;
	}
//  Remove all creative methods
//	private void createCannons() {
//		cannons = HelpMethods.GetCannons(img);
//	}
//
//	private void createSpikes() {
//		spikes = HelpMethods.GetSpikes(img);
//	}
//
//	private void createContainers() {
//		containers = HelpMethods.GetContainers(img);
//	}
//
//	private void createPotions() {
//		potions = HelpMethods.GetPotions(img);
//	}
//
//	private void calcPlayerSpawn() {
//		playerSpawn = GetPlayerSpawn(img);
//	}

	private void calcLvlOffsets() {
		lvlTilesWide = img.getWidth();
		maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
		maxLvlOffsetX = Game.TILES_SIZE * maxTilesOffset;
	}

//	private void createEnemies() {
//		crabs = GetCrabs(img);
//	}
//
//	private void createLevelData() {
//		lvlData = GetLevelData(img);
//	}

	public int getSpriteIndex(int x, int y) {
		return lvlData[y][x];
	}

	public int[][] getLevelData() {
		return lvlData;
	}

	public int getLvlOffset() {
		return maxLvlOffsetX;
	}

	public ArrayList<Crabby> getCrabs() {
		return crabs;
	}
	
//	Add 4 methods
	public ArrayList<Shark> getSharks() {
		return sharks;
	}
	public ArrayList<Pinkstar> getPinkstars() {
		return pinkstars;
	}

	public ArrayList<BackgroundTree> getTrees() {
		return trees;
	}

	public ArrayList<Grass> getGrass() {
		return grass;
	}

	public Point getPlayerSpawn() {
		return playerSpawn;
	}

	public ArrayList<Potion> getPotions() {
		return potions;
	}

	public ArrayList<GameContainer> getContainers() {
		return containers;
	}

	public ArrayList<Spike> getSpikes() {
		return spikes;
	}
	
	public ArrayList<Cannon> getCannons(){
		return cannons;
	}
}
