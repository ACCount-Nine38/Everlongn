package com.everlongn.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.everlongn.game.ControlCenter;
import com.everlongn.states.GameState;
import com.everlongn.utils.Constants;
import com.everlongn.utils.Tool;

import java.util.ArrayList;

public abstract class Entity {
    public float health, maxHealth, bonusHealth, resistance, maxResistance, bonusResistance, baseRegenAmount, bonusRegenAmount, alpha = 1;
    public float x, y, density; //protected allow extended class to have access to them
    public int width, height, team, boundWidth, boundHeight;
    public String name, form;
    public ArrayList<String> type = new ArrayList<String>();
    public boolean active = true, canRegen = true, stunned, knockbackResistant, vulnerableToArcane, exploded, destroyed;

    // timers
    public float regenTimer;

    public Body body;

    public Entity(float x, float y, int width, int height, float density) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.density = density;

        // default values
        maxHealth = 100;
        health = 100;
        maxResistance = 5;
        resistance = maxResistance;
        name = "UNNAMED";
        team = -1;
    }

    public abstract void tick();
    public abstract void render(SpriteBatch batch);
    public abstract void finish();

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void setMaxHealth(int health) {
        maxHealth = health;
        this.health = health;
    }

    public void setMaxResistance(int resistance) {
        maxResistance = resistance;
        this.resistance = resistance;
    }

    public Rectangle getBound() {
        return new Rectangle(body.getPosition().x*Constants.PPM, body.getPosition().y*Constants.PPM, boundWidth, boundHeight);
    }

    public void resetHealth(float amount) {
        maxHealth = amount;
        health = maxHealth;
    }

    public void regenerate() {
        if(canRegen && health != maxHealth && active) {
            regenTimer += Gdx.graphics.getDeltaTime();
            if(regenTimer > 0.1) {
                regenTimer = 0;
                health += baseRegenAmount + bonusRegenAmount;
                if(health > maxHealth)
                    health = maxHealth;
            }
        }
    }

    public float getHealthPercentage() {
        return health/(maxHealth+bonusHealth);
    }

    // method that calculates damage to player and enemies by difficulty
    public void hurt(float damage, Entity source) {
        if(name.equals("player")) {
            if (GameState.difficulty == 2) { // insane difficulty
                if (damage*1.5 - resistance > 0) {
                    health -= damage*1.5 - resistance;
                } else {
                    health -= 1;
                }
            } else {
                if (damage - resistance > 0) {
                    health -= damage - resistance;
                } else {
                    health -= 1;
                }
            }
        } else {
            if (GameState.difficulty == 0) { // standard difficulty
                if (damage - resistance * 0.5 > 0) {
                    health -= damage - resistance * 0.5;
                } else {
                    health -= 1;
                }
            } else {
                if (damage - resistance > 0) {
                    health -= damage - resistance;
                } else {
                    health -= 1;
                }
            }
        }
    }

    // method that calculates damage to player and enemies by difficulty
    public void trueDamage(float damage, int difficulty) {
        if(name.equals("player")) {
            if(EntityManager.player.godMode)
                return;
            if (difficulty == 2) { // insane difficulty
                if (damage*1.5 > 0) {
                    health -= damage*1.5;
                }
            } else {
                if (damage > 0) {
                    health -= damage;
                }
            }
        } else {
            if (difficulty == 0) { // standard difficulty
                if (damage * 0.5 > 0) {
                    health -= damage * 0.5;
                }
            } else {
                if (damage > 0) {
                    health -= damage;
                }
            }
        }
    }
}
