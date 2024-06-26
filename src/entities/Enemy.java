package entities;

import static utilz.Constants.ANI_SPEED;
import static utilz.Constants.GRAVITY;
import static utilz.Constants.Directions.LEFT;
import static utilz.Constants.Directions.RIGHT;
import static utilz.Constants.Directions.UP;
import static utilz.Constants.EnemyConstants.ATTACK;
import static utilz.Constants.EnemyConstants.CRABBY;
import static utilz.Constants.EnemyConstants.DEAD;
import static utilz.Constants.EnemyConstants.GetEnemyDmg;
import static utilz.Constants.EnemyConstants.GetMaxHealth;
import static utilz.Constants.EnemyConstants.GetSpriteAmount;
import static utilz.Constants.EnemyConstants.HIT;
import static utilz.Constants.EnemyConstants.IDLE;
import static utilz.Constants.EnemyConstants.PINKSTAR;
import static utilz.Constants.EnemyConstants.SHARK;
import static utilz.HelpMethods.CanMoveHere;
import static utilz.HelpMethods.GetEntityYPosUnderRoofOrAboveFloor;
import static utilz.HelpMethods.IsEntityInWater;
import static utilz.HelpMethods.IsEntityOnFloor;
import static utilz.HelpMethods.IsFloor;
import static utilz.HelpMethods.IsSightClear;

import java.awt.geom.Rectangle2D;

import gamestates.Playing;
import main.Game;

public class Enemy extends Entity{
	protected int enemyType;
	protected boolean firstUpdate = true;
	protected int walkDir = LEFT;
	protected int tileY;
	protected float attackDistance = Game.TILES_SIZE;
	protected boolean active = true;
	protected boolean attackChecked;
//	From Crabby
	protected int attackBoxOffsetX;

	public Enemy(float x, float y, int width, int height, int enemyType) {
		super(x, y, width, height);
		this.enemyType = enemyType;

		maxHealth = GetMaxHealth(enemyType);
		currentHealth = maxHealth;
		walkSpeed = Game.SCALE * 0.35f;
	}
//	From Crabby
	protected void updateAttackBox() {
		attackBox.x = hitbox.x - attackBoxOffsetX;
		attackBox.y = hitbox.y;
	}
//	Add
	protected void updateAttackBoxFlip() {
		if (walkDir == RIGHT)
			attackBox.x = hitbox.x + hitbox.width;
		else
			attackBox.x = hitbox.x - attackBoxOffsetX;

		attackBox.y = hitbox.y;
	}
//	Add
	protected void initAttackBox(int w, int h, int attackBoxOffsetX) {
		attackBox = new Rectangle2D.Float(x, y, (int) (w * Game.SCALE), (int) (h * Game.SCALE));
		this.attackBoxOffsetX = (int) (Game.SCALE * attackBoxOffsetX);
	}

	protected void firstUpdateCheck(int[][] lvlData) {
		if (!IsEntityOnFloor(hitbox, lvlData))
			inAir = true;
		firstUpdate = false;
	}
//	Add
	protected void inAirChecks(int[][] lvlData, Playing playing) {
		if (state != HIT && state != DEAD) {
			updateInAir(lvlData);
			playing.getObjectManager().checkSpikesTouched(this);
			if (IsEntityInWater(hitbox, lvlData))
				hurt(maxHealth);
		}
	}
	
	protected void updateInAir(int[][] lvlData) {
		if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
			hitbox.y += airSpeed;
			airSpeed += GRAVITY;
		} else {
			inAir = false;
			hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
			tileY = (int) (hitbox.y / Game.TILES_SIZE);
		}
	}

	protected void move(int[][] lvlData) {
		float xSpeed = 0;

		if (walkDir == LEFT)
			xSpeed = -walkSpeed;
		else
			xSpeed = walkSpeed;

		if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData))
			if (IsFloor(hitbox, xSpeed, lvlData)) {
				hitbox.x += xSpeed;
				return;
			}

		changeWalkDir();
	}

	protected void turnTowardsPlayer(Player player) {
		if (player.hitbox.x > hitbox.x)
			walkDir = RIGHT;
		else
			walkDir = LEFT;
	}

	protected boolean canSeePlayer(int[][] lvlData, Player player) {
		int playerTileY = (int) (player.getHitbox().y / Game.TILES_SIZE);
		if (playerTileY == tileY)
			if (isPlayerInRange(player)) {
				if (IsSightClear(lvlData, hitbox, player.hitbox, tileY))
					return true;
			}

		return false;
	}

	protected boolean isPlayerInRange(Player player) {
		int absValue = (int) Math.abs(player.hitbox.x - hitbox.x);
		return absValue <= attackDistance * 5;
	}
// Add component
	protected boolean isPlayerCloseForAttack(Player player) {
		int absValue = (int) Math.abs(player.hitbox.x - hitbox.x);
		switch (enemyType) {
		case CRABBY -> {
			return absValue <= attackDistance;
		}
		case SHARK -> {
			return absValue <= attackDistance * 2;
		}
		}
		return false;
	}
//	To Entity
//	protected void newState(int enemyState) {
//		this.state = enemyState;
//		aniTick = 0;
//		aniIndex = 0;
//	}

	public void hurt(int amount) {
//		Remove
//		currentHealth -= amount;
//		if (currentHealth <= 0)
//			newState(DEAD);
//		else
//			newState(HIT);

//		Add
		currentHealth -= amount;
		if (currentHealth <= 0)
			newState(DEAD);
		else {
			newState(HIT);
			if (walkDir == LEFT)
				pushBackDir = RIGHT;
			else
				pushBackDir = LEFT;
			pushBackOffsetDir = UP;
			pushDrawOffset = 0;
		}
	}

	protected void checkPlayerHit(Rectangle2D.Float attackBox, Player player) {
//Remove
//		if (attackBox.intersects(player.hitbox))
//			player.changeHealth(-GetEnemyDmg(enemyType));
//		attackChecked = true;
	
//		Add
		if (attackBox.intersects(player.hitbox))
			player.changeHealth(-GetEnemyDmg(enemyType), this);
		else {
			if (enemyType == SHARK)
				return;
		}
		attackChecked = true;
	}
//Add component
	protected void updateAnimationTick() {
		aniTick++;
		if (aniTick >= ANI_SPEED) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= GetSpriteAmount(enemyType, state)) {
				if (enemyType == CRABBY || enemyType == SHARK) {
					aniIndex = 0;

					switch (state) {
					case ATTACK, HIT -> state = IDLE;
					case DEAD -> active = false;
					}
				} else if (enemyType == PINKSTAR) {
					if (state == ATTACK)
						aniIndex = 3;
					else {
						aniIndex = 0;
						if (state == HIT) {
							state = IDLE;

						} else if (state == DEAD)
							active = false;
					}
				}
			}
		}
	}

	protected void changeWalkDir() {
		if (walkDir == LEFT)
			walkDir = RIGHT;
		else
			walkDir = LEFT;
	}

	public void resetEnemy() {
		hitbox.x = x;
		hitbox.y = y;
		firstUpdate = true;
		currentHealth = maxHealth;
		newState(IDLE);
		active = true;
		airSpeed = 0;
//		Add
		pushDrawOffset = 0;
	}
	
//	From Crabby
	public int flipX() {
		if (walkDir == RIGHT)
			return width;
		else
			return 0;
	}

	public int flipW() {
		if (walkDir == RIGHT)
			return -1;
		else
			return 1;
	}

	public boolean isActive() {
		return active;
	}
//	Add
	public float getPushDrawOffset() {
		return pushDrawOffset;
	}
}
