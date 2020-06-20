package com.everlongn.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.graphics.ParticleEmitterBox2D;
import com.everlongn.game.ControlCenter;
import com.everlongn.items.Inventory;
import com.everlongn.states.GameState;
import com.everlongn.utils.Constants;

import java.util.ArrayList;

public abstract class Creature extends Entity {
    public float speed, currentSpeed, sightRadius, knockbackResistance, yChangeTimer, previousVelY, fadeAlpha = 1f;
    public int direction, damage;
    public boolean canJump, jump, fall, airborn, alive = true;

    public Entity target;
    public ArrayList<String> enemyList = new ArrayList<String>();

    public Animation[] chase, attack;
    public ParticleEffect destroyed;

    public Creature(ControlCenter c, float x, float y, int width, int height, float density, float speed) {
        super(c, x, y, width, height, density);

        // default values
        this.speed = speed;
        currentSpeed = speed;
        type.add("creature");
        damage = 50;
        direction = 0; // 0-Left, 1-Right

        destroyed = new ParticleEffect();
        destroyed.load(Gdx.files.internal("particles/destroyed"), Gdx.files.internal(""));
    }

    public void move() {
        if(!stunned) {
            if (direction == 0)
                body.setLinearVelocity(-speed, body.getLinearVelocity().y);
            else {
                body.setLinearVelocity(speed, body.getLinearVelocity().y);
            }
        } else {
            body.setLinearVelocity(body.getLinearVelocity().x/knockbackResistance, body.getLinearVelocity().y);
            if(body.getLinearVelocity().x == 0 && body.getLinearVelocity().y == 0) {
                stunned = false;
            }
        }
    }

    public void findTarget() {
        Entity possibleTarget = null;

        for(int i = 0; i < EntityManager.entities.size(); i++) {
            Entity e = EntityManager.entities.get(i);
            if(e instanceof Creature && e != this) {
                Creature c = (Creature)e;
                for(int j = 0; j < c.type.size(); j++) {
                    if(enemyList.contains(c.type.get(j))) {
                        if(possibleTarget == null) {
                            possibleTarget = c;
                        } else {
                            if(Math.abs(x - possibleTarget.x) > Math.abs(x - c.x)) {
                                possibleTarget = c;
                            }
                        }
                        break;
                    }
                }

            }
        }
        target = possibleTarget;
    }

    public void chase() {
        if(target == null)
            return;
        if(!target.active) {
            target = null;
        }
        if(target.body.getPosition().x*Constants.PPM + target.width/2 < body.getPosition().x*Constants.PPM + width/2) {
            direction = 0;
        } else {
            direction = 1;
        }
        move();
    }

    public abstract void die();
}
