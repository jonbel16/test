/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamesfromscratch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 *
 * @author Jonathan-PC
 */
public class Assets implements Disposable, AssetErrorListener{

    public static final String TAG = Assets.class.getName();
    public static final Assets instance = new Assets();
    private AssetManager assetManager;
    public AssetTyre tyre;
    
    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private Assets () {}

    public void init(AssetManager assetManager){
        this.assetManager = assetManager;
        // set asset manager error handler
        assetManager.setErrorListener(this);
        // load texture atlas
        assetManager.load("spritesheets/f1 car.atlas",
        TextureAtlas.class);
        // start loading assets and wait until finished
        assetManager.finishLoading();
        Gdx.app.debug(TAG, "# of assets loaded: "
        + assetManager.getAssetNames().size);
        for (String a : assetManager.getAssetNames())
            Gdx.app.debug(TAG, "asset: " + a);
        
        TextureAtlas atlas = assetManager.get("spritesheets/f1 car.atlas");
        
        //enabling texture filtering for pixel smoothing
        for (Texture t : atlas.getTextures())t.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        
        //creating game resources
        tyre = new AssetTyre(atlas);

    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
     /**
     * Inner classes
     */
    public class AssetTyre{
        public final TextureAtlas.AtlasRegion tyre;
        public AssetTyre (TextureAtlas atlas){
            tyre = atlas.findRegion("Wheel");
            
        }
    }
    
}
