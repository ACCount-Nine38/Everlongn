package com.everlongn.states;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.everlongn.assets.Tiles;
import com.everlongn.entities.EntityManager;
import com.everlongn.entities.Player;
import com.everlongn.game.ControlCenter;
import com.everlongn.tiles.EarthTile;
import com.everlongn.tiles.Tile;
import com.everlongn.utils.Chunk;
import com.everlongn.world.BackgroundManager;

public class WorldLoadingState extends State {
    private String currentStage = "Loading...";
    public GlyphLayout layout = new GlyphLayout();
    public static BitmapFont loadingFont = new BitmapFont(Gdx.files.internal("fonts/chalk22.fnt"));

    public FileHandle file;

    private boolean generated;
    private int currentStep, numSteps;
    private float count;

    public WorldLoadingState(StateManager stateManager, FileHandle file) {
        super(stateManager);

        this.file = file;
        currentStep = 0;
        numSteps = 4;
    }

    public void generate(int step, float delta) {
        count += delta;
        if(count < 0.5) {
            return;
        } else {
            count = 0;
        }

        if(step == 0) {
            currentStage = "Initializing Realm...";
            initializing();
        } else if (step == 1) {
            currentStage = "Loading Realm...";
            loadWorld(file);
        } else if(step == 2) {
            currentStage = "Loading Chunks...";

            GameState.chunks = new Chunk[GameState.worldWidth/GameState.chunkSize][GameState.worldHeight/GameState.chunkSize];

            for(int i = 0; i < GameState.worldWidth/GameState.chunkSize; i++) {
                for(int j = 0; j < GameState.worldHeight/GameState.chunkSize; j++) {
                    GameState.chunks[i][j] = new Chunk(false);
                }
            }
        } else if(step == 3) {
            currentStage = "Finalizing...";
            createPlayer();
        }

        currentStep++;
    }

    public void createPlayer() {
        GameState.entityManager = new EntityManager(c, new Player(c, GameState.spawnX*Tile.TILESIZE, GameState.spawnY*Tile.TILESIZE, 18, 60, 2.5f));

        Vector3 position = ControlCenter.camera.position;
        position.x = GameState.spawnX*Tile.TILESIZE;
        position.y = GameState.spawnY*Tile.TILESIZE;
        GameState.parallexBackground.position.set(position);

        for(int i = 0; i < BackgroundManager.layers.length; i++) {
            BackgroundManager.layers[i] = new Vector2();
            BackgroundManager.layers[i].x = 1280/2 - 25*Tile.TILESIZE;
            BackgroundManager.layers[i].y = 800/2 - 25*Tile.TILESIZE + 120;
        }

        ControlCenter.camera.position.x = GameState.spawnX*Tile.TILESIZE; //getting back to scale by *PPM
        ControlCenter.camera.position.y = GameState.spawnY*Tile.TILESIZE + 200;
        ControlCenter.camera.update();//397 × 581
    }

    public void loadWorld(FileHandle file) {
        Pixmap pixmap = new Pixmap(file);

        GameState.worldWidth = pixmap.getWidth();
        GameState.worldHeight = pixmap.getHeight();

        GameState.tiles = new Tile[GameState.worldWidth][GameState.worldHeight];

        for(int y=0; y < GameState.worldHeight; y++){
            for(int x=0; x < GameState.worldWidth; x++){
                int color = pixmap.getPixel(x, y);
                String hexColor = Integer.toHexString(color);
                int red = color >>> 24;
                int green = (color & 0xFF0000) >>> 16;
                int blue = (color & 0xFF00) >>> 8;
                int alpha = color & 0xFF;

                if(red == 1 && green == 1 && blue == 1) {
                    GameState.tiles[x][GameState.worldHeight - 1 - y] = new EarthTile(x, GameState.worldHeight - 1 - y);
                }
                else if(red == 255 && green == 255 && blue == 255) {
                    GameState.spawnX = x;
                    GameState.spawnY = GameState.worldHeight - 1 - y;
                }
            }
        }
    }

    public void initializing() {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        GameState.hud = new OrthographicCamera();
        GameState.hud.setToOrtho(false, width/ControlCenter.SCALE, height/ControlCenter.SCALE);
        GameState.parallexBackground = new OrthographicCamera();
        GameState.parallexBackground.setToOrtho(false, width/ControlCenter.SCALE, height/ControlCenter.SCALE);

        // vector 2 takes a x and y gravity value
        GameState.world = new World(new Vector2(0, -20f), false);
        GameState.rayHandler = new RayHandler(GameState.world);
        GameState.debug = new Box2DDebugRenderer();
    }

    @Override
    public void tick(float delta) {
        updateLayers(delta);

        if(!generated) {
            generate(currentStep, delta);

            if(currentStep == numSteps) {
                generated = true;
                count = 0;
                currentStage = "Complete...";
            }
        } else {
            count += delta;
            if(count >= 1f) { // 1 second timer
                stateManager.setState(StateManager.CurrentState.GAME_STATE);
            }
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        layout.setText(loadingFont, currentStage);
        loadingFont.draw(batch, currentStage,
                ControlCenter.width/2 - layout.width/2, ControlCenter.height/2 - layout.height/2);
        batch.end();
    }

    @Override
    public void dispose() {

    }
}
