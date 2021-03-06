package com.everlongn.utils.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.everlongn.assets.Sounds;
import com.everlongn.assets.Tiles;
import com.everlongn.assets.UI;
import com.everlongn.game.ControlCenter;
import com.everlongn.states.WorldSelectionState;
import com.everlongn.utils.TextManager;
import com.everlongn.utils.components.UIComponent;

public class WorldSelectButton extends UIComponent {
    public String worldName, difficulty, worldSize, date, mode;
    public int seed;
    public boolean selected, soundCanPlay;
    public float startY, endY;
    public FileHandle tilemap, wallmap, herbsMap, debrisMap;

    public WorldSelectButton(float x, float y, String text, boolean clickable, BitmapFont font,
                             String worldName, String difficulty, String worldSize, int seed, String mode, String date,
                             FileHandle tilemap, FileHandle wallmap, FileHandle herbsMap, FileHandle debrisMap) {
        super(x, y, text, clickable, font);

        this.worldName = worldName;
        this.difficulty = difficulty;
        this.worldSize = worldSize;
        this.seed = seed;
        this.mode = mode;
        this.date = date;
        this.tilemap = tilemap;
        this.wallmap = wallmap;
        this.herbsMap = herbsMap;
        this.debrisMap = debrisMap;

        startY = y;
        endY = startY - 100;

        width = 530;
        height = 100;
    }

    @Override
    public void tick() {
        startY = ControlCenter.height - y - WorldSelectionState.scrollY;
        endY = startY - 100;
        if(endY < ControlCenter.height/2 - 240) {
            endY = ControlCenter.height/2 - 240;
        }
        if(startY > ControlCenter.height/2 - 240 + 445) {
            startY = ControlCenter.height/2 - 240 + 445;
        }
        if(ControlCenter.height - ControlCenter.touchPos.y < startY && ControlCenter.height - ControlCenter.touchPos.y > endY &&
                ControlCenter.touchPos.x > x && ControlCenter.touchPos.x < x + 530) {
            hover = true;
            if(soundCanPlay) {
                Sounds.playSound(Sounds.buttonHover);
                soundCanPlay = false;
            }
        } else {
            hover = false;
            soundCanPlay = true;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if(!hover && !selected)
            batch.draw(UI.worldSelect, x, y + WorldSelectionState.scrollY, 530, 100);
        else
            batch.draw(UI.worldSelected, x, y + WorldSelectionState.scrollY, 530, 100);

        if(mode.equals("Hardcore"))
            batch.draw(UI.hardcore, x + 10, y + 5 + WorldSelectionState.scrollY, 90, 90);
        else
            batch.draw(UI.normal, x + 10, y + 5 + WorldSelectionState.scrollY, 90, 90);
        TextManager.bfont = WorldSelectionState.buttonFont;
        TextManager.draw(worldName, (int)x + 115, (int)(y + 92 + WorldSelectionState.scrollY), Color.BLACK, 1f, false);
        layout.setText(WorldSelectionState.buttonFont, worldName);
        float nameWidth = layout.width;
        float nameHeight = layout.height;
        batch.draw(Tiles.blackTile, (int)x + 115, (int)(y + 92 + WorldSelectionState.scrollY - nameHeight - 5), nameWidth, 2);

        TextManager.draw(difficulty + " Journey", (int)x + 115, (int)(y + 95 + WorldSelectionState.scrollY - nameHeight - 5 - 10), Color.BLACK, 1f, false);
        TextManager.draw(worldSize + " Realm", (int)x + 115, (int)(y + 95 + WorldSelectionState.scrollY - nameHeight - 5 - 30), Color.BLACK, 1f, false);

        TextManager.draw( mode, (int)x + 115, (int)(y + 95 + WorldSelectionState.scrollY - nameHeight - 5 - 50), Color.BLACK, 1f, false);

        layout.setText(WorldSelectionState.buttonFont, date);
        float dateWidth = layout.width;
        TextManager.draw( date, (int)(x + 520 - dateWidth), (int)(y + 95 + WorldSelectionState.scrollY - nameHeight - 5 - 50), Color.BLACK, 1f, false);

        layout.setText(WorldSelectionState.buttonFont, "seed: " + seed);
        float seedWidth = layout.width;
        TextManager.draw("seed: " + seed, (int)(x + 520 - seedWidth), (int)(y + 95 + WorldSelectionState.scrollY - nameHeight - 5 - 30), Color.BLACK, 1f, false);
    }

    @Override
    public void onClick() {

    }
}
