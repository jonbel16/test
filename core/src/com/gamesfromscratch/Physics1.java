package com.gamesfromscratch;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;


public class Physics1 extends ApplicationAdapter implements InputProcessor {
    SpriteBatch batch;
    Sprite sprite;
    Texture img;
    World world;
    Body body;
    Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;
    OrthographicCamera camera;
    
    
    float torque = 0.0f;
    boolean drawSprite = true;
    
    final float PIXELS_TO_METERS = 100f;
    
    final float WORLD_WIDTH =100;
    final float WORLD_HEIGHT=50;


    @Override
    public void create() {

        batch = new SpriteBatch();
        
        img = new Texture("badlogic.jpg");
        sprite = new Sprite();
        
        //sprite.setRegion();
        sprite.setSize(25,12);


        //sprite dimensions - 256 /256 
        sprite.setPosition(50-sprite.getWidth()/2,25);
        
        Gdx.app.log("Physics1", "Sprite positions"+ -sprite.getWidth()/2+ " ,"+ -sprite.getHeight()/2);

        world = new World(new Vector2(0, 0f),true);
        
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        
        bodyDef.position.set((sprite.getX() + sprite.getWidth()/2) / 
                             PIXELS_TO_METERS, 
                (sprite.getY() + sprite.getHeight()/2) / PIXELS_TO_METERS);

        //calculation should equal 2.56
        Gdx.app.log("Physics1", "Body positions calculations"+ sprite.getX() +" "+ sprite.getWidth()/2);
        
        Gdx.app.log("Physics1", "Body positions"+ bodyDef.position);
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sprite.getWidth()/2 / PIXELS_TO_METERS, sprite.getHeight()
                       /2 / PIXELS_TO_METERS);

        //Gdx.app.log("Physics1", "Sprite positions"+ bodyDef.position);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;

        body.createFixture(fixtureDef);
        shape.dispose();
        
        Gdx.input.setInputProcessor(this);
        
        // Create a Box2DDebugRenderer, this allows us to see the physics 
        //simulation controlling the scene
        debugRenderer = new Box2DDebugRenderer();

        camera = new OrthographicCamera(WORLD_WIDTH,WORLD_HEIGHT);
        Gdx.app.log("Physics1", "camera "+ camera.viewportWidth+" "+camera.viewportHeight);

        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);//320 240
        Gdx.app.log("Physics1", "camera "+ camera.viewportWidth+" "+camera.viewportHeight);
        //camera.setToOrtho(true);
    }
    
      @Override
   public void resize(int width, int height){
      camera.update();
      camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
      Gdx.app.log("Physics1", "camera "+ camera.viewportWidth+" "+camera.viewportHeight);
   }

    private float elapsed = 0;
    @Override
    public void render() {
        camera.update();
        // Step the physics simulation forward at a rate of 60hz
        world.step(1f/60f, 6, 2);
        
        // Apply torque to the physics body.  At start this is 0 and will do 
//        nothing.  Controlled with [] keys
        // Torque is applied per frame instead of just once
        body.applyTorque(torque,true);
        
        // Set the sprite's position from the updated physics body location
        sprite.setPosition((body.getPosition().x * PIXELS_TO_METERS) - sprite.
                           getWidth()/2 , 
                (body.getPosition().y * PIXELS_TO_METERS) -sprite.getHeight()/2 )
                 ;
        Gdx.app.log("Physics1", "Body.GetPosition :" +body.getPosition().x +" "+body.getPosition().y);
        Gdx.app.log("Physics1", "Sprite positions "+ sprite.getX()+ sprite.getY());
        // Ditto for rotation
        sprite.setRotation((float)Math.toDegrees(body.getAngle()));

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        batch.setProjectionMatrix(camera.combined);
        
        // Scale down the sprite batches projection matrix to box2D size
        debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS, 
                      PIXELS_TO_METERS, 0);
        
        batch.begin();
        
        if(drawSprite)
            batch.draw(sprite, sprite.getX(), sprite.getY(),sprite.getOriginX(),
                       sprite.getOriginY(),
                sprite.getWidth(),sprite.getHeight(),sprite.getScaleX(),sprite.
                                getScaleY(),sprite.getRotation());
                        
        batch.end();
        
        // Now render the physics world using our scaled down matrix
        // Note, this is strictly optional and is, as the name suggests, just 
//        for debugging purposes
        debugRenderer.render(world, debugMatrix);
    }

    @Override
    public void dispose() {
        img.dispose();
        world.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        
        // On right or left arrow set the velocity at a fixed rate in that 
//        direction
        if(keycode == Input.Keys.RIGHT) 
            body.setLinearVelocity(1f, 0f);
        if(keycode == Input.Keys.LEFT)
            body.setLinearVelocity(-1f,0f);

        if(keycode == Input.Keys.UP)
            body.applyForceToCenter(0f,.1f,true);
        if(keycode == Input.Keys.DOWN)
            body.applyForceToCenter(0f, -.1f, true);
        
        // On brackets ( [ ] ) apply torque, either clock or counterclockwise
        if(keycode == Input.Keys.RIGHT_BRACKET)
            torque += 0.1f;
        if(keycode == Input.Keys.LEFT_BRACKET)
            torque -= 0.1f;
        
        // Remove the torque using backslash /
        if(keycode == Input.Keys.BACKSLASH)
            torque = 0.0f;
        
        // If user hits spacebar, reset everything back to normal
        if(keycode == Input.Keys.SPACE) {
            body.setLinearVelocity(0f, 0f);
            body.setAngularVelocity(0f);
            torque = 0f;
            sprite.setPosition(0f,0f);
            body.setTransform(0.125f,0.06f,0f);
        }
        
        // The ESC key toggles the visibility of the sprite allow user to see 
//        physics debug info
        if(keycode == Input.Keys.ESCAPE)
            drawSprite = !drawSprite;

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    
    // On touch we apply force from the direction of the users touch.
    // This could result in the object "spinning"
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        body.applyForce(1f,1f,screenX,screenY,true);
        //body.applyTorque(0.4f,true);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}