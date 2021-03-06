package com.everlongn.entities.projectiles;

import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.graphics.ParticleEmitterBox2D;
import com.everlongn.entities.*;
import com.everlongn.game.ControlCenter;
import com.everlongn.states.GameState;
import com.everlongn.utils.Constants;
import com.everlongn.utils.Tool;

public class ArcaneDevastation extends Projectile {
    public ParticleEffect movingParticle;
    public int direction;
    public static float maxLife = 0.5f;

    public float life, angle;
    public static Color color = new Color(0.02f, 0.02f, 0.00f, 1f);

    public ArcaneDevastation(float x, float y, float density, int direction, float angle, float xSize, float damage, Entity source) {
        super(x, y, 5, 5, density);
        this.direction = direction;
        this.angle = angle;
        this.damage = damage;
        this.source = source;

        body = Tool.createEntity((int)(x), (int)(y), width, height, false, 1, false,
                (short) Constants.BIT_PROJECTILE, (short)(Constants.BIT_TILE | Constants.BIT_ENEMY), (short)0, this);

        float additionalSpeed = (float)(Math.random()*25);
        float xMove = Math.abs((float)Math.sin(angle)*(40f + additionalSpeed));
        if(direction == 0) {
            speedX = -xMove;
        } else {
            speedX = xMove;
        }
        speedY = -(float)Math.cos(angle)*(40f + additionalSpeed);

        movingParticle = new ParticleEffect();
        movingParticle.load(Gdx.files.internal("particles/oblivionTrail"), Gdx.files.internal(""));
        movingParticle.getEmitters().first().setPosition(body.getPosition().x * Constants.PPM, body.getPosition().y * Constants.PPM);
        movingParticle.getEmitters().add(new ParticleEmitterBox2D(GameState.world,movingParticle.getEmitters().first()));
        movingParticle.getEmitters().removeIndex(0);
        movingParticle.getEmitters().first().scaleSize(xSize);
        movingParticle.start();

        maxMovingRadius = 350;
        light = new PointLight(GameState.rayHandler, 150, color, 0,
                body.getPosition().x * Constants.PPM,
                body.getPosition().y * Constants.PPM);
        light.setSoft(true);
    }

    @Override
    public void tick() {
        if(!lifeOut) {
            moveByVelocityX();
            moveByVelocityY();
            currentRadius+=15;
            if(currentRadius > maxMovingRadius)
                currentRadius = maxMovingRadius;
            light.setDistance(currentRadius);
            light.setPosition(body.getPosition().x * Constants.PPM,
                    body.getPosition().y * Constants.PPM);
        } else {
            body.setLinearVelocity(0, 0);
            if(maxReached) {
                currentRadius -= 25;
                if (currentRadius <= 0)
                    currentRadius = 0;
            } else {
                currentRadius += 25;
                if (currentRadius > maxExplodingRadius) {
                    currentRadius = maxMovingRadius;
                    maxReached = true;
                }
            }
            light.setDistance(currentRadius);
            light.setPosition(body.getPosition().x * Constants.PPM,
                    body.getPosition().y * Constants.PPM);
            if(!exploded) {
                explosionTimer += Gdx.graphics.getDeltaTime();
                if(explosionTimer > 0.01) {
                    explode();
                    exploded = true;
                }
            }
        }

        life += Gdx.graphics.getDeltaTime();
        if(life > maxLife && !lifeOut) {
            finish();
        }
        if(lifeOut && movingParticle.isComplete() && currentRadius <= 0) {
            GameState.world.destroyBody(body);
            movingParticle.dispose();
            light.remove();
            active = false;
        }
        movingParticle.getEmitters().first().setPosition(body.getPosition().x * Constants.PPM, body.getPosition().y * Constants.PPM);
        movingParticle.update(Gdx.graphics.getDeltaTime());
    }

    public void explode() {
        Rectangle explosionRectangle = new Rectangle(body.getPosition().x*Constants.PPM+2 - 10, body.getPosition().y*Constants.PPM+2 - 10,
                20, 20);
        for(int i = 0; i < EntityManager.entities.size(); i++) {
            if(EntityManager.entities.get(i).getBound().overlaps(explosionRectangle) && EntityManager.entities.get(i) != this) {
                if(EntityManager.entities.get(i) instanceof Creature && !(EntityManager.entities.get(i) instanceof Player)) {
                    Creature c = (Creature)EntityManager.entities.get(i);
                    c.trueDamage(damage, GameState.difficulty);
                    c.target = source;
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        movingParticle.draw(batch);
        batch.end();
    }

    @Override
    public void finish() {
        lifeOut = true;
        movingParticle.getEmitters().get(0).setContinuous(false);
    }
}
