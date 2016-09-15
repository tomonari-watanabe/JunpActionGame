package jp.techachacademy.tomonari.watanabe.jumpactiongame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by moggmogg on 2016/09/08.
 */
public class ResultScreen extends ScreenAdapter {
    static final float GUI_WIDTH = 320;
    static final float GUI_HEIGHT = 480;

    private JumpActionGame mGame;
    Sprite mBg;
    OrthographicCamera mGuiCamera;
    FitViewport mGuiViewCamera;
    BitmapFont mFont;

    int mScore;

    boolean isPlying;

    public ResultScreen(JumpActionGame game, int score, boolean play){

        mGame = game;
        isPlying = play;

        if(mGame.mRequestHandler != null){
            mGame.mRequestHandler.showAds(true);
        }


        mScore = score;

        //背景の準備
        Texture bgTexture = new Texture("resultback.png");
        mBg = new Sprite(new TextureRegion(bgTexture,0,0,540,810));
        mBg.setSize(GUI_WIDTH, GUI_HEIGHT);
        mBg.setPosition(0,0);

        //GUI用のカメラを設定
        mGuiCamera = new OrthographicCamera();
        mGuiCamera.setToOrtho(false,GUI_WIDTH,GUI_HEIGHT);
        mGuiViewCamera = new FitViewport(GUI_WIDTH,GUI_HEIGHT,mGuiCamera);

        //フォント
        mFont = new BitmapFont(Gdx.files.internal("font.fnt"), Gdx.files.internal("font.png"), false );

    }

    @Override
    public void render(float delta) {
        //描画する
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //カメラの座標をアップデートしてスプライトの表示

        mGuiCamera.update();
        mGame.batch.setProjectionMatrix(mGuiCamera.combined);

        mGame.batch.begin();
        mBg.draw(mGame.batch);
        mFont.draw(mGame.batch,"SCORE:" + mScore, 0,GUI_HEIGHT/2 + 40, GUI_WIDTH, Align.center,false);
        mFont.draw((mGame.batch), "Retry?",0,GUI_HEIGHT/2-40, GUI_WIDTH, Align.center,false);
        mGame.batch.end();

        if (Gdx.input.justTouched()){

            if(mGame.mRequestHandler != null){
                mGame.mRequestHandler.showAds(false);
            }


            mGame.setScreen(new GameScreen(mGame, isPlying));
        }

    }


}
