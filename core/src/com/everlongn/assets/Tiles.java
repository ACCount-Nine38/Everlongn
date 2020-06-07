package com.everlongn.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tiles {
    public static int defaultImageSize = 500;

    // Earth Tile
    public static Texture earthTilesheet;
    public static TextureRegion earthTile, earthTileExpose4;
    public static TextureRegion earthTileExpose1[], earthTileExpose2[], earthTileExpose3[];
    public static Texture airTile, blackTile;

    public static Texture earthWallsheet;
    public static TextureRegion earthWall, earthWallExpose4;
    public static TextureRegion earthWallExpose1[], earthWallExpose2[], earthWallExpose3[];

    public static void init() {
        // temp tiles
        blackTile = new Texture(Gdx.files.internal("tiles/blackTile.png"));
        airTile = new Texture(Gdx.files.internal("tiles/airTile.png"));

        // Earth Tile
        earthTilesheet = new Texture(Gdx.files.internal("tiles/earthTileSpritesheet.png"),true);

        earthTile = new TextureRegion(earthTilesheet, 0, 0, defaultImageSize, defaultImageSize);
        earthTileExpose4 = new TextureRegion(earthTilesheet, 500, 0, defaultImageSize, defaultImageSize);

        earthTileExpose1 = new TextureRegion[2];
        earthTileExpose1[0] = new TextureRegion(earthTilesheet, 1000, 0, defaultImageSize, defaultImageSize);
        earthTileExpose1[1] = new TextureRegion(earthTilesheet, 1500, 0, defaultImageSize, defaultImageSize);

        earthTileExpose2 = new TextureRegion[9];
        earthTileExpose2[0] = new TextureRegion(earthTilesheet, 0, 1000, defaultImageSize, defaultImageSize);
        earthTileExpose2[1] = new TextureRegion(earthTilesheet, 500, 1000, defaultImageSize, defaultImageSize);
        earthTileExpose2[2] = new TextureRegion(earthTilesheet, 1000, 1000, defaultImageSize, defaultImageSize);
        earthTileExpose2[3] = new TextureRegion(earthTilesheet, 1500, 1000, defaultImageSize, defaultImageSize);
        earthTileExpose2[4] = new TextureRegion(earthTilesheet, 0, 1500, defaultImageSize, defaultImageSize);
        earthTileExpose2[5] = new TextureRegion(earthTilesheet, 500, 1500, defaultImageSize, defaultImageSize);
        earthTileExpose2[6] = new TextureRegion(earthTilesheet, 1000, 1500, defaultImageSize, defaultImageSize);
        earthTileExpose2[7] = new TextureRegion(earthTilesheet, 1500, 1500, defaultImageSize, defaultImageSize);
        // exposed to air on left and right
        earthTileExpose2[8] = new TextureRegion(earthTilesheet, 2000, 0, defaultImageSize, defaultImageSize);

        earthTileExpose3 = new TextureRegion[4];
        earthTileExpose3[0] = new TextureRegion(earthTilesheet, 0, 500, defaultImageSize, defaultImageSize);
        earthTileExpose3[1] = new TextureRegion(earthTilesheet, 500, 500, defaultImageSize, defaultImageSize);
        earthTileExpose3[2] = new TextureRegion(earthTilesheet, 1000, 500, defaultImageSize, defaultImageSize);
        earthTileExpose3[3] = new TextureRegion(earthTilesheet, 1500, 500, defaultImageSize, defaultImageSize);

        earthWallsheet = new Texture(Gdx.files.internal("tiles/earthWallSpritesheet.png"),true);

        earthWall = new TextureRegion(earthWallsheet, 0, 0, defaultImageSize, defaultImageSize);
        earthWallExpose4 = new TextureRegion(earthWallsheet, 500, 0, defaultImageSize, defaultImageSize);

        earthWallExpose1 = new TextureRegion[2];
        earthWallExpose1[0] = new TextureRegion(earthWallsheet, 1000, 0, defaultImageSize, defaultImageSize);
        earthWallExpose1[1] = new TextureRegion(earthWallsheet, 1500, 0, defaultImageSize, defaultImageSize);

        earthWallExpose2 = new TextureRegion[4];
        earthWallExpose2[0] = new TextureRegion(earthWallsheet, 0, 1000, defaultImageSize, defaultImageSize);
        earthWallExpose2[1] = new TextureRegion(earthWallsheet, 500, 1000, defaultImageSize, defaultImageSize);
        earthWallExpose2[2] = new TextureRegion(earthWallsheet, 1000, 1000, defaultImageSize, defaultImageSize);
        earthWallExpose2[3] = new TextureRegion(earthWallsheet, 1500, 1000, defaultImageSize, defaultImageSize);

        earthWallExpose3 = new TextureRegion[4];
        earthWallExpose3[0] = new TextureRegion(earthWallsheet, 0, 500, defaultImageSize, defaultImageSize);
        earthWallExpose3[1] = new TextureRegion(earthWallsheet, 500, 500, defaultImageSize, defaultImageSize);
        earthWallExpose3[2] = new TextureRegion(earthWallsheet, 1000, 500, defaultImageSize, defaultImageSize);
        earthWallExpose3[3] = new TextureRegion(earthWallsheet, 1500, 500, defaultImageSize, defaultImageSize);

    }
}
