package com.everlongn.entities;

import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.everlongn.assets.Entities;
import com.everlongn.game.ControlCenter;
import com.everlongn.states.GameState;
import com.everlongn.tiles.Tile;
import com.everlongn.world.BackgroundManager;

import static com.everlongn.utils.Constants.PPM;

public class Player extends Creature {

    public static boolean jump, fall, canJump = true, movingHorizontal, airborn;

    private PointLight light;
    private PointLight sun;
    private int direction = 1;
    private boolean cameraXStopped, fallUpdate;
    private float elapsedTime, airbornTime, fallUpdateTimer;
    /*
      0 - left
      1 - right
    */

    private Animation<TextureRegion>[] legsRun = new Animation[2];
    private Animation<TextureRegion>[] legsJump = new Animation[2];
    private Animation<TextureRegion>[] armsRun = new Animation[2];
    private Animation<TextureRegion>[] headRun = new Animation[2];
    private Animation<TextureRegion>[] chestRun = new Animation[2];

    public static int currentChunkX, currentChunkY;

    public Player(ControlCenter c, float x, float y, int width, int height, float density) {
        super(c, x, y, width, height, density);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        speed = 5;

        type.add("player");
        light = new PointLight(GameState.rayHandler, 100, Color.WHITE, 250/PPM,
                body.getPosition().x,
                body.getPosition().y);

        legsRun[0] = new Animation<>(1f/50f, Entities.legRun[0]);
        legsRun[1] = new Animation<>(1f/50f, Entities.legRun[1]);

        legsJump[0] = new Animation<>(1f/50f, Entities.legJump[0]);
        legsJump[1] = new Animation<>(1f/50f, Entities.legJump[1]);

        armsRun[0] = new Animation<>(1f/50f, Entities.armRun[0]);
        armsRun[1] = new Animation<>(1f/50f, Entities.armRun[1]);

        chestRun[0] = new Animation<>(1f/50f, Entities.chestRun[0]);
        chestRun[1] = new Animation<>(1f/50f, Entities.chestRun[1]);

        headRun[0] = new Animation<>(1f/50f, Entities.headRun[0]);
        headRun[1] = new Animation<>(1f/50f, Entities.headRun[1]);
    }

    @Override
    public void tick() {
        currentChunkX = (int)(body.getPosition().x/GameState.chunkSize);
        currentChunkY = (int)(body.getPosition().y/GameState.chunkSize);

        light.setPosition(body.getPosition().x,
                body.getPosition().y);
        cameraUpdate();
        inputUpdate();

        elapsedTime += Gdx.graphics.getDeltaTime();

        if(body.getLinearVelocity().y <= 0 && airbornTime > 0.01 && !fall && jump) {
            fall = true;
            jump = false;
            fallUpdateTimer = 0;
            fallUpdate = true;
        }

        if(fallUpdate) {
            fallUpdateTimer += Gdx.graphics.getDeltaTime();
        }

        if(fall && body.getLinearVelocity().y > -0.5f && fallUpdateTimer > 0.3f) {
            fall = false;
            fallUpdate = false;
        }

        if(jump && !Gdx.input.isKeyPressed(Input.Keys.W)) {
            body.setLinearVelocity(body.getLinearVelocity().x, body.getLinearVelocity().y/1.15f);
        }

        if(!canJump) {
            airbornTime += Gdx.graphics.getDeltaTime();
            if(airbornTime >= 0.5) {
                canJump = true;
            }
        }
    }

    public void inputUpdate() {
        int horizontalForce = 0;
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            horizontalForce = -1;
            direction = 0;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            horizontalForce = 1;
            direction = 1;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.W) && canJump && (Math.abs(body.getLinearVelocity().y) < 0.334 ||
                (body.getLinearVelocity().y > speed - 0.44 && body.getLinearVelocity().y < speed - 0.3))) {
            body.applyForceToCenter(0, 300/(body.getLinearVelocity().y/8 + 1), false);
            jump = true;
            canJump = false;
            airbornTime = 0;
            airborn = true;

            legsJump[0] = new Animation<>(1f/50f, Entities.legJump[0]);
            legsJump[1] = new Animation<>(1f/50f, Entities.legJump[1]);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            System.exit(1);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.F4)) {
            ControlCenter.DEBUG = !ControlCenter.DEBUG;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            ControlCenter.DEBUG_RENDER = !ControlCenter.DEBUG_RENDER;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.F6)) {
            speed += 5;
        }

        if(body.getLinearVelocity().x != 0 && !cameraXStopped) {
            movingHorizontal = true;
            BackgroundManager.layers[0].x -= body.getLinearVelocity().x/4f;
            BackgroundManager.layers[1].x -= body.getLinearVelocity().x/5.5f;
            BackgroundManager.layers[2].x -= body.getLinearVelocity().x/7f;
        } else {
            movingHorizontal = false;
        }

        body.setLinearVelocity(horizontalForce*speed, body.getLinearVelocity().y);
    }

    public void cameraUpdate() {
        Vector3 position = ControlCenter.camera.position;
        position.x = ControlCenter.camera.position.x + (body.getPosition().x * PPM - ControlCenter.camera.position.x)*.1f;
        position.y = ControlCenter.camera.position.y + (body.getPosition().y * PPM - ControlCenter.camera.position.y + Tile.TILESIZE/2*3)*.1f;

        if(position.x - ControlCenter.width/2 < 0) {
            position.x = ControlCenter.width/2;
            cameraXStopped = true;
        } else if(position.x + ControlCenter.width/2 > Tile.TILESIZE*GameState.worldWidth - width/2 - 20) {
            position.x = Tile.TILESIZE*GameState.worldWidth - ControlCenter.width/2 - width/2 - 20;
            cameraXStopped = true;
        } else {
            cameraXStopped = false;
        }

        if(position.y - ControlCenter.height/2 < 0) {
            position.y = ControlCenter.height/2;
        } else if(position.y + ControlCenter.height/2 > Tile.TILESIZE*GameState.worldHeight - height/2 - 10) {
            position.y = Tile.TILESIZE*GameState.worldHeight - ControlCenter.height/2 - height/2 - 10;
        }

        ControlCenter.camera.position.set(position);

        ControlCenter.camera.update();
    }

    public void render(SpriteBatch batch) {
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.begin();

        if(body.getLinearVelocity().x != 0f) {
            if(jump || fall) {
                batch.draw(legsJump[direction].getKeyFrame(elapsedTime, true), body.getPosition().x * PPM + width / 2 - 57,
                        body.getPosition().y * PPM - 5, 114, 114);
            } else {
                batch.draw(legsRun[direction].getKeyFrame(elapsedTime, true), body.getPosition().x * PPM + width / 2 - 57,
                        body.getPosition().y * PPM - 5, 114, 114);
            }
            batch.draw(armsRun[direction].getKeyFrame(elapsedTime, true),
                    body.getPosition().x * PPM + width / 2 - 57,
                    body.getPosition().y * PPM - 5, 114, 114);
            batch.draw(chestRun[direction].getKeyFrame(elapsedTime, true),
                    body.getPosition().x * PPM + width / 2 - 57,
                    body.getPosition().y * PPM - 5, 114, 114);

            batch.draw(headRun[direction].getKeyFrame(elapsedTime, true),
                    body.getPosition().x * PPM + width / 2 - 57,
                    body.getPosition().y * PPM - 5, 114, 114);

        } else {
            batch.draw(Entities.headRun[direction][0], body.getPosition().x * PPM + width / 2 - 57,
                    body.getPosition().y * PPM - 5, 114, 114);
            batch.draw(Entities.chestRun[direction][0], body.getPosition().x * PPM + width / 2 - 57,
                    body.getPosition().y * PPM - 5, 114, 114);
            batch.draw(Entities.armRun[direction][0], body.getPosition().x * PPM + width / 2 - 57,
                    body.getPosition().y * PPM - 5, 114, 114);
            if(jump || fall) {
                batch.draw(legsJump[direction].getKeyFrame(elapsedTime, true), body.getPosition().x * PPM + width / 2 - 57,
                        body.getPosition().y * PPM - 5, 114, 114);
            } else {
                batch.draw(Entities.legRun[direction][0], body.getPosition().x * PPM + width / 2 - 57,
                        body.getPosition().y * PPM - 5, 114, 114);
            }
        }
        batch.end();
    }
}
