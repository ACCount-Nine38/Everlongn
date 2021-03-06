package com.everlongn.entities.projectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.everlongn.assets.Items;
import com.everlongn.assets.Sounds;
import com.everlongn.assets.ThrowWeapons;
import com.everlongn.entities.*;
import com.everlongn.items.Item;
import com.everlongn.items.Throwing;
import com.everlongn.states.GameState;
import com.everlongn.utils.Constants;
import com.everlongn.utils.Tool;

public class Shuriken extends Throw {
    public ParticleEffect explosion;

    public Shuriken(float x, float y, int direction, float angle, float damage, Entity source) {
        super(x, y, 30, 30, 1);
        this.direction = direction;
        this.angle = angle;
        this.damage = damage;
        this.source = source;

        body = Tool.createEntity((int)(x), (int)(y), width, height, false, 1, false,
                (short) Constants.BIT_PROJECTILE, (short)(Constants.BIT_TILE), (short)0, this);

        float forceX = (float)(Math.abs(Math.sin(angle)*320));
        float forceY = (float)(Math.cos(angle)*250);

        if(direction == 0) {
            moveByForce(new Vector2(-forceX, -forceY));
        } else {
            moveByForce(new Vector2(forceX, -forceY));
        }

        explosion = new ParticleEffect();
        explosion.load(Gdx.files.internal("particles/throwExplosion"), Gdx.files.internal(""));
        explosion.getEmitters().first().setPosition(body.getPosition().x * Constants.PPM, body.getPosition().y * Constants.PPM);

        throwBound = new Rectangle(0, 0, Throwing.shuriken.width, Throwing.shuriken.height);
    }

    @Override
    public void tick() {
        if(!lifeOut) {
            for(Entity e: EntityManager.entities) {
                if(e.getBound().overlaps(throwBound) && !damaged.contains(e) && e.team != EntityManager.player.team) {
                    damaged.add(e);
                    e.stunned = true;

                    float force = 500 + (float)Math.random()*100;
                    float angle = (float)(Math.random()*(Math.PI/4));
                    if(direction == 0) {
                        e.body.applyForceToCenter(
                                -(float)Math.cos(angle)*force, (float)Math.sin(angle)*force, false);
                    } else {
                        e.body.applyForceToCenter(
                                (float)Math.cos(angle)*force, (float)Math.sin(angle)*force, false);
                    }

                    e.hurt(damage, source);
                    if(e instanceof Creature) {
                        Creature c = (Creature)e;
                        c.target = source;
                    }
                }
            }
            if(direction == 0)
                rotation += 15;
            else
                rotation -= 15;
        } else {
            if(!collected) {
                if(locked != null && locked.body != null)
                    body.setTransform(locked.body.getPosition().x + lockX, locked.body.getPosition().y + lockY, 0);
                else
                    lifeOut = false;
            }
            checkPickedUp();
        }

        if ((lifeOut && deactivate) || (despawn && explosion.isComplete())) {
            GameState.world.destroyBody(body);
            explosion.dispose();
            active = false;
        }

        if(lifeOut && despawn) {
            explosion.getEmitters().first().setPosition(body.getPosition().x * Constants.PPM, body.getPosition().y * Constants.PPM);
            explosion.update(Gdx.graphics.getDeltaTime());
        }
        throwBound.setPosition(body.getPosition().x*Constants.PPM - Throwing.shuriken.width/2 + width/2, body.getPosition().y*Constants.PPM - Throwing.shuriken.height/2 + height/2);
    }

    public void checkPickedUp() {
        if(Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT) && throwBound.contains(Player.mouseWorldPos().x, Player.mouseWorldPos().y) && throwBound.overlaps(Player.itemPickBound) && Item.canPick) {
            collected = true;
            Item.canPick = false;
        }

        if(!Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            Item.canPick = true;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && throwBound.overlaps(Player.itemPickBound)) {
            collected = true;
        }

        if(collected) {
            float sx = Player.itemCollectBound.x/Constants.PPM;
            float sy = Player.itemCollectBound.y/Constants.PPM;

            if(Math.abs(sx - body.getPosition().x) > 75/Constants.PPM) {
                collected = false;
                return;
            }

            double angle = Math.atan2(sx - body.getPosition().x,
                    sy - body.getPosition().y);

            body.setLinearVelocity((float)Math.sin(angle) * (10f), (float) Math.cos(angle) * (10f));

            if(throwBound.overlaps(Player.itemCollectBound)) {
                GameState.inventory.addItem(Throwing.shuriken.createNew(1));
                pickedUp = true;
                deactivate = true;
            }
        } else {
            body.setLinearVelocity(body.getLinearVelocity().x/1.04f, body.getLinearVelocity().y);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        //batch.draw(Tiles.blackTile, throwBound.x, throwBound.y, throwBound.width, throwBound.height);
        if(body != null) {
            if(lifeOut && despawn) {
                alpha -= 0.05;
                batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, alpha);
            }
            batch.draw(ThrowWeapons.shuriken, body.getPosition().x * Constants.PPM - Throwing.shuriken.width / 2 + width / 2, body.getPosition().y * Constants.PPM - Throwing.shuriken.height / 2 + height / 2,
                    Throwing.shuriken.width / 2, Throwing.shuriken.height / 2,
                    Throwing.shuriken.width, Throwing.shuriken.height, 1f, 1f, rotation);
            if(lifeOut && despawn) {
                batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1);
            }
        }
        if(lifeOut && despawn) {
            explosion.draw(batch);
        }
        batch.end();
    }

    @Override
    public void finish() {
        if(direction == 0) {
            ParticleEffect land = new ParticleEffect();
            land.load(Gdx.files.internal("particles/shredderLandLeft"), Gdx.files.internal(""));
            land.getEmitters().first().setPosition(body.getPosition().x * Constants.PPM, body.getPosition().y * Constants.PPM - height/2 + 10);
            land.start();
            EntityManager.particles.add(land);
        } else {
            ParticleEffect land = new ParticleEffect();
            land.load(Gdx.files.internal("particles/shredderLandRight"), Gdx.files.internal(""));
            land.getEmitters().first().setPosition(body.getPosition().x * Constants.PPM + width, body.getPosition().y * Constants.PPM - height/2 + 10);
            land.start();
            EntityManager.particles.add(land);
        }
        lifeOut = true;
        despawn = false;
        body.setLinearVelocity(0,0);
        Sounds.playSound(Sounds.shurikenLand);
    }
}
