package com.everlongn.entities.projectiles;

import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.graphics.ParticleEmitterBox2D;
import com.everlongn.assets.Items;
import com.everlongn.entities.Creature;
import com.everlongn.entities.EntityManager;
import com.everlongn.entities.Player;
import com.everlongn.entities.Projectile;
import com.everlongn.items.Throwing;
import com.everlongn.states.GameState;
import com.everlongn.utils.Constants;
import com.everlongn.utils.Tool;

public class TriStar extends Projectile {
    //public ParticleEffect explosion;
    public int direction;

    public float life, angle, rotation;

    public TriStar(float x, float y, float density, int direction, float angle, float damage) {
        super(x, y, 5, 5, density);
        this.direction = direction;
        this.angle = angle;
        this.damage = damage;

        body = Tool.createEntity((int)(x), (int)(y), width, height, false, 1, false,
                (short) Constants.BIT_PROJECTILE, (short)(Constants.BIT_TILE | Constants.BIT_ENEMY), (short)0, this);

        float forceX = (float)(Math.abs(Math.sin(angle)*6));
        float forceY = (float)(Math.cos(angle)*6);

        if(direction == 0) {
            moveByForce(new Vector2(-forceX, -forceY));
        } else {
            moveByForce(new Vector2(forceX, -forceY));
        }

//        explosion = new ParticleEffect();
//        explosion.load(Gdx.files.internal("particles/trailExplosion"), Gdx.files.internal(""));
//        explosion.getEmitters().first().setPosition(body.getPosition().x * Constants.PPM, body.getPosition().y * Constants.PPM);

    }

    @Override
    public void tick() {
        if(!lifeOut) {
            if(direction == 0)
                rotation += 15;
            else
                rotation -= 15;
        } else {
            body.setLinearVelocity(0, 0);
            if(!exploded) {
                explosionTimer += Gdx.graphics.getDeltaTime();
                if(explosionTimer > 0.01) {
                    explode();
                    exploded = true;
                }
            }
        }

        if(lifeOut) {
            GameState.world.destroyBody(body);
            active = false;
        }

//        if(lifeOut && explosion.isComplete() && currentRadius <= 0) {
//            GameState.world.destroyBody(body);
//            explosion.dispose();
//            light.remove();
//            active = false;
//        }
//
//        if(lifeOut) {
//            explosion.getEmitters().first().setPosition(body.getPosition().x * Constants.PPM, body.getPosition().y * Constants.PPM);
//            explosion.update(Gdx.graphics.getDeltaTime());
//        }
    }

    public void explode() {
        Rectangle explosionRectangle = new Rectangle(body.getPosition().x*Constants.PPM+2 - Throwing.triStar.width, body.getPosition().y*Constants.PPM+2 - Throwing.triStar.height/2,
                Throwing.triStar.width, Throwing.triStar.height);
        for(int i = 0; i < EntityManager.entities.size(); i++) {
            if(EntityManager.entities.get(i).getBound().overlaps(explosionRectangle) && EntityManager.entities.get(i) != this) {
                if(EntityManager.entities.get(i) instanceof Creature && !(EntityManager.entities.get(i) instanceof Player)) {
                    Creature c = (Creature)EntityManager.entities.get(i);

                    c.stunned = true;

                    float force = 500 + (float)Math.random()*100;
                    float angle = (float)(Math.random()*(Math.PI/4));
                    if(direction == 0) {
                        c.body.applyForceToCenter(
                                -(float)Math.cos(angle)*force, (float)Math.sin(angle)*force, false);
                    } else {
                        c.body.applyForceToCenter(
                                (float)Math.cos(angle)*force, (float)Math.sin(angle)*force, false);
                    }

                    c.hurt(damage, GameState.difficulty);
                    break;
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        if(body != null)
            batch.draw(Items.tristar, body.getPosition().x*Constants.PPM - Throwing.stone.width/2, body.getPosition().y*Constants.PPM -Throwing.stone.height/2,
                    Throwing.stone.width/2, Throwing.stone.height/2,
                    Throwing.triStar.width, Throwing.triStar.height, 1f, 1f, rotation);
//        if(lifeOut) {
//            explosion.draw(batch);
//        }
        batch.end();
    }

    @Override
    public void finish() {
        lifeOut = true;
        //explosion.start();
    }
}