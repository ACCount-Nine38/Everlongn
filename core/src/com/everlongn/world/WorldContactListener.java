package com.everlongn.world;

import com.badlogic.gdx.physics.box2d.*;
import com.everlongn.entities.Creature;
import com.everlongn.entities.Entity;
import com.everlongn.entities.EntityManager;
import com.everlongn.entities.Projectile;
import com.everlongn.entities.projectiles.ArcaneEruption;
import com.everlongn.entities.projectiles.ArcaneTrail;
import com.everlongn.tiles.Tile;
import com.everlongn.utils.Constants;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        if(a == null || b == null || a.getUserData() == null || b.getUserData() == null)
            return;

//        if(a.getUserData() instanceof ArcaneEruption) {
//            ArcaneEruption temp = (ArcaneEruption)(a.getUserData());
//            if(!temp.lifeOut) {
//                EntityManager.player.body.applyForceToCenter(
//                        -1500, 550, false);
//                temp.finish();
//            }
//        }
//        if(b.getUserData() instanceof ArcaneEruption) {
//            ArcaneEruption temp = (ArcaneEruption)(b.getUserData());
//            if(!temp.lifeOut) {
//                EntityManager.player.body.applyForceToCenter(
//                        -1500, 550, false);
//                temp.finish();
//            }
//        }

        short cDef = (short)(a.getFilterData().categoryBits | b.getFilterData().categoryBits);

        if(cDef == (short)(Constants.BIT_ENEMY | Constants.BIT_TILE)) {
            if (a.getFilterData().categoryBits == Constants.BIT_ENEMY) {
                Creature temp = (Creature) a.getUserData();
                temp.stunned = false;
            }
            else {
                Creature temp = (Creature) b.getUserData();
                temp.stunned = false;
            }
        } else if(cDef == (short)(Constants.BIT_PROJECTILE | Constants.BIT_TILE) || cDef == (short)(Constants.BIT_PROJECTILE | Constants.BIT_ENEMY)) {
            if (a.getFilterData().categoryBits == Constants.BIT_PROJECTILE) {
                Projectile temp = (Projectile) a.getUserData();
                if(!temp.lifeOut) {
                    temp.finish();
                }
            }
            else {
                Projectile temp = (Projectile) b.getUserData();
                if(!temp.lifeOut) {
                    temp.finish();
                }
            }
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
