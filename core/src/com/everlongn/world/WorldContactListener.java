package com.everlongn.world;

import com.badlogic.gdx.physics.box2d.*;
import com.everlongn.entities.*;
import com.everlongn.entities.creatures.Hydra;
import com.everlongn.entities.projectiles.ArcaneEruption;
import com.everlongn.entities.projectiles.ArcaneRebound;
import com.everlongn.entities.projectiles.ArcaneReflection;
import com.everlongn.entities.projectiles.ArcaneTrail;
import com.everlongn.items.Item;
import com.everlongn.tiles.Tile;
import com.everlongn.utils.Constants;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        if(a == null || b == null || a.getUserData() == null || b.getUserData() == null)
            return;

        if(a.getUserData() instanceof ArcaneRebound && b.getUserData() instanceof Creature && !(b.getUserData() instanceof Player)) {
            ArcaneRebound temp = (ArcaneRebound)(a.getUserData());
            if(!temp.lifeOut) {
                temp.destroy();
            }
            return;
        }
        if(b.getUserData() instanceof ArcaneRebound && a.getUserData() instanceof Creature && !(a.getUserData() instanceof Player)) {
            ArcaneRebound temp = (ArcaneRebound)(b.getUserData());
            if(!temp.lifeOut) {
                temp.destroy();
            }
            return;
        }

        short cDef = (short)(a.getFilterData().categoryBits | b.getFilterData().categoryBits);

        if(cDef == (short)(Constants.BIT_ENEMY | Constants.BIT_TILE)) {
            if (a.getFilterData().categoryBits == Constants.BIT_ENEMY) {
                if(!(a.getUserData() instanceof Creature)) {
                    return;
                }
                Creature temp = (Creature) a.getUserData();

                // special creature tests
                if(temp instanceof Player) {
                    if (temp.xThrust != 0) {
                        temp.xThrust /= 1.1;
                        if (Math.abs(temp.xThrust) < 0.1) {
                            temp.xThrust = 0;
                        }
                    }
                    if (temp.yThrust != 0) {
                        temp.yThrust /= 1.1;
                        if (Math.abs(temp.yThrust) < 0.1) {
                            temp.yThrust = 0;
                        }
                    }
                } else if(temp instanceof Hydra && temp.status.equals("natural")) {
                    Tile tile = (Tile)(b.getUserData());
                    Hydra hydra = (Hydra)(temp);
                    if(tile.numAdjacent == 2) {
                        if (tile.left && tile.right && !tile.up && !tile.down) {
                            hydra.currentVelY *= -1;
                        }
                        if (!tile.left && !tile.right && tile.up && tile.down) {
                            hydra.currentVelX *= -1;
                        } else {
                            if (tile.down) {
                                hydra.currentVelX *= -1;
                                hydra.currentVelY *= Math.abs(hydra.currentVelY);
                            } else {
                                hydra.currentVelX *= -1;
                                hydra.currentVelY *= -Math.abs(hydra.currentVelY);
                            }
                        }
                    } else {
                        if(tile.left && tile.right) {
                            hydra.currentVelY *= -1;
                        } else if(tile.up && tile.down) {
                            hydra.currentVelX *= -1;
                        }
                    }
                } else {
                    temp.stunned = false;
                }
            }
            else {
                if(!(b.getUserData() instanceof Creature)) {
                    return;
                }
                Creature temp = (Creature) b.getUserData();

                // special creature tests
                if(temp instanceof Player) {
                    if (temp.xThrust != 0) {
                        temp.xThrust /= 1.03;
                        if (Math.abs(temp.xThrust) < 0.1) {
                            temp.xThrust = 0;
                        }
                    }
                    if (temp.yThrust != 0) {
                        temp.yThrust /= 1.03;
                        if (Math.abs(temp.yThrust) < 0.1) {
                            temp.yThrust = 0;
                        }
                    }
                }
                else if(temp instanceof Hydra && temp.status.equals("natural")) {
                    Tile tile = (Tile)(a.getUserData());
                    Hydra hydra = (Hydra)(temp);
                    if(tile.numAdjacent == 2) {
                        if (tile.left && tile.right && !tile.up && !tile.down) {
                            hydra.currentVelY *= -1;
                        }
                        if (!tile.left && !tile.right && tile.up && tile.down) {
                            hydra.currentVelX *= -1;
                        } else {
                            if (tile.down) {
                                hydra.currentVelX *= -1;
                                hydra.currentVelY *= Math.abs(hydra.currentVelY);
                            } else {
                                hydra.currentVelX *= -1;
                                hydra.currentVelY *= -Math.abs(hydra.currentVelY);
                            }
                        }
                    } else {
                        if(tile.left && tile.right) {
                            hydra.currentVelY *= -1;
                        } else if(tile.up && tile.down) {
                            hydra.currentVelX *= -1;
                        }
                    }
                } else {
                    temp.stunned = false;
                }
            }
        } else if(cDef == (short)(Constants.BIT_PROJECTILE | Constants.BIT_TILE) || cDef == (short)(Constants.BIT_PROJECTILE | Constants.BIT_ENEMY)) {
            if (a.getFilterData().categoryBits == Constants.BIT_PROJECTILE) {
                if(a.getUserData() instanceof Item) {
                    return;
                }
                else if(a.getUserData() instanceof Throw) {
                    Throw temp = (Throw) a.getUserData();
                    if(b.getUserData() instanceof Tile && !temp.lifeOut) {
                        temp.finish();
                        temp.lockTile((Tile)b.getUserData());
                    } else if(b.getUserData() instanceof Entity && !temp.lifeOut) {
                        temp.finish();
                        temp.lockEntity((Entity)b.getUserData());
                    }
                }
                else {
                    Projectile temp = (Projectile) a.getUserData();
                    if (!temp.lifeOut) {
                        if (temp instanceof ArcaneReflection) {
                            ArcaneReflection ar = (ArcaneReflection) a.getUserData();
                            if (b.getUserData() instanceof Entity) {
                                ar.numReflection = ar.maxReflection;
                                ar.finish2(null, true);
                            } else {
                                Tile tile = (Tile) b.getUserData();
                                ar.finish2(tile, false);
                            }
                        } else {
                            if (b.getUserData() instanceof Entity) {
                                Entity entity = (Entity) (b.getUserData());
                                if (entity.active) {
                                    temp.finish();
                                }
                            } else {
                                temp.finish();
                            }
                        }
                    }
                }
            }
            else {
                if(b.getUserData() instanceof Item) {
                    return;
                }
                else if(b.getUserData() instanceof Throw) {
                    Throw temp = (Throw) b.getUserData();
                    if(a.getUserData() instanceof Tile && !temp.lifeOut) {
                        temp.finish();
                        temp.lockTile((Tile)a.getUserData());
                    } else if(a.getUserData() instanceof Entity && !temp.lifeOut) {
                        temp.finish();
                        temp.lockEntity((Entity)a.getUserData());
                    }
                }
                else {
                    Projectile temp = (Projectile) b.getUserData();
                    if (!temp.lifeOut) {
                        if (temp instanceof ArcaneReflection) {
                            ArcaneReflection ar = (ArcaneReflection) b.getUserData();
                            if (a.getUserData() instanceof Entity && !temp.lifeOut) {
                                ar.numReflection = ar.maxReflection;
                                ar.finish2(null, false);
                            } else if(a.getUserData() instanceof Tile && !temp.lifeOut){
                                Tile tile = (Tile) a.getUserData();
                                ar.finish2(tile, false);
                            }
                        } else {
                            if (a.getUserData() instanceof Entity) {
                                Entity entity = (Entity) (a.getUserData());
                                if (entity.active) {
                                    temp.finish();
                                }
                            } else {
                                temp.finish();
                            }
                        }
                    }
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
