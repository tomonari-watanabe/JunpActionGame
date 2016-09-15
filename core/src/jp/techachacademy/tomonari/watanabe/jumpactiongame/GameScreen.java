package jp.techachacademy.tomonari.watanabe.jumpactiongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * Created by moggmogg on 2016/09/06.
 */
public class GameScreen extends ScreenAdapter {

    Sound msound = Gdx.audio.newSound(Gdx.files.internal("se_maoudamashii_retro12.mp3"));
    Music music = Gdx.audio.newMusic(Gdx.files.internal("BGM109-110124-letschallenge-mp3.mp3"));
    Sound msound2 = Gdx.audio.newSound(Gdx.files.internal("se_maoudamashii_system05.mp3"));
    Sound msound3 = Gdx.audio.newSound(Gdx.files.internal("se_maoudamashii_retro08.mp3"));

    static final float CAMERA_WIDTH = 10;
    static final float CAMERA_HEIGHT = 15;
    static final float WORLD_WIDTH = 10;
    static final float WORLD_HEIGHT = 15*10; // 20画面分登れば終了
    static final float GUI_WIDTH = 320;
    static final float GUI_HEIGHT = 480;


    static final int GAME_STATE_READY = 0;
    static final int GAME_STATE_PLAYING = 1;
    static final int GAME_STATE_GAMEOVER = 2;

    // 重力
    static final float GRAVITY = -12;

    private JumpActionGame mGame;

    Sprite mBg;
    OrthographicCamera mCamera;
    OrthographicCamera mGuiCamera;

    FitViewport mViewPort;
    FitViewport mGuiViewport;


    Random mRandom;
    List<Step> mSteps;
    List<Star> mStars;
    List<Enemy> mEnemy;
    Ufo mUfo;
    Player mPlayer;

    float mHeightSoFar;
    int mGameState;
    Vector3 mTouchPoint;

    BitmapFont mFont;
    int mScore;
    int mHifhScore;

    Preferences mPrefs;

    boolean isPlying;

    public GameScreen(JumpActionGame game, boolean play){
        mGame = game;

        isPlying = play;

        if(isPlying != true) {
            music.setLooping(true);
            music.play();
            isPlying = music.isPlaying();
        } else {}




        //背景
        Texture bgTexture = new Texture("back.png");
        //TextureRegionで切り出すときの原稿は左上
        mBg = new Sprite( new TextureRegion(bgTexture, 0,0,540,810));
        mBg.setSize(CAMERA_WIDTH,CAMERA_HEIGHT);
        mBg.setPosition(0,0);

        //カメラ、ViewPortを生成、設定する
        mCamera = new OrthographicCamera();
        mCamera.setToOrtho(false,CAMERA_WIDTH,CAMERA_HEIGHT);
        mViewPort = new FitViewport(CAMERA_WIDTH,CAMERA_HEIGHT,mCamera);

        //GUI用のカメラ、VIewportを生成、認定する。
        mGuiCamera = new OrthographicCamera();
        mGuiCamera.setToOrtho(false, GUI_WIDTH,GUI_HEIGHT);
        mGuiViewport = new FitViewport(GUI_WIDTH, GUI_HEIGHT,mGuiCamera);

        //メンバ変数の初期化

        mRandom = new Random();
        mSteps = new ArrayList<Step>();
        mStars = new ArrayList<Star>();
        mEnemy = new ArrayList<Enemy>();
        mGameState = GAME_STATE_READY;
        mTouchPoint = new Vector3();

        mFont = new BitmapFont(Gdx.files.internal("font.fnt"), Gdx.files.internal("font.png"), false);
        mFont.getData().setScale(0.8f);
        mScore = 0;
        mHifhScore = 0;


        mPrefs = Gdx.app.getPreferences("jp.techacademy.tomonari.watanabe.jampactiongame");
        mHifhScore = mPrefs.getInteger("HIGHSCORE", 0);


        createStage();

    }

    @Override
    public void render (float delta){

        // アップデートを実行(1フレームごと)
        update(delta);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //カメラ移動。カメラの中心を超えたらカメラを上に移動。
        if (mPlayer.getY() > mCamera.position.y){
            mCamera.position.y = mPlayer.getY();
        }



        //カメラの座標をアップデートしてスプライトの表示に反映
        mCamera.update();
        mGame.batch.setProjectionMatrix(mCamera.combined);

        mGame.batch.begin();


        //原点は左下
        mBg.setPosition(mCamera.position.x-CAMERA_WIDTH/2, mCamera.position.y-CAMERA_HEIGHT/2);
        mBg.draw(mGame.batch);

        //Step
        for (int i=0; i<mSteps.size();i++){
            mSteps.get(i).draw(mGame.batch);
        }

        //Star
        for(int i = 0; i<mStars.size();i++){
            mStars.get(i).draw(mGame.batch);
        }

        //Enemy
        for(int i = 0; i<mEnemy.size();i++){
            mEnemy.get(i).draw(mGame.batch);
        }

        //UFO
        mUfo.draw(mGame.batch);

        //Player
        mPlayer.draw(mGame.batch);

        mGame.batch.end();

        //スコア表示
        mGuiCamera.update();
        mGame.batch.setProjectionMatrix(mGuiCamera.combined);
        mGame.batch.begin();
        mFont.draw(mGame.batch, "HighScore" + mHifhScore, 16, GUI_HEIGHT - 15);
        mFont.draw(mGame.batch, "Score" + mScore, 16, GUI_HEIGHT-35);
        mGame.batch.end();

    }

    @Override
    public void resize(int width, int height){
        mViewPort.update(width,height);
        mGuiViewport.update(width,height);
    }

    //ステージ作成
    private void createStage(){

        //テクスチャの準備
        Texture stepTexture = new Texture("step.png");
        Texture starTexture = new Texture("star.png");
        Texture playerTexture = new Texture("uma.png");
        Texture ufoTexture = new Texture("ufo.png");
        Texture enemyTexture = new Texture("uma2.png");

        //stepとstarをゴールまで配置していく

        float y = 0;

        float maxJumpHeight = Player.PLAYER_JUMP_VELOCITY * Player.PLAYER_JUMP_VELOCITY/(2*-GRAVITY);
        while (y<WORLD_HEIGHT-5){

            //三項条件演算子
            int type = mRandom.nextFloat() > 0.8f ? Step.STEP_TYPE_MOVING : Step.STEP_TYPE_STATIC;
            float x = mRandom.nextFloat() * (WORLD_WIDTH - Step.STEP_WIDTH);

            Step step = new Step(type, stepTexture, 0, 0, 144, 36);
            step.setPosition(x, y);
            mSteps.add(step);

            //スター
            if (mRandom.nextFloat() > 0.6f) {
                Star star = new Star(starTexture, 0, 0, 72, 72);
                star.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Star.STAR_HEIGHT + mRandom.nextFloat() * 3);
                mStars.add(star);
            }

            //敵
            if (mRandom.nextFloat() > 0.8f) {
                Enemy enemy = new Enemy(1,enemyTexture, 0, 0, 72, 72);
                enemy.setPosition(step.getX() + mRandom.nextFloat(), step.getY() + Enemy.ENEMY_HEIGHT + mRandom.nextFloat() * 3);
                mEnemy.add(enemy);
            }

            y += (maxJumpHeight - 0.5f);//ジャンプ最大値--0.5fに次のstepの判定を行う
            y -= mRandom.nextFloat() * (maxJumpHeight / 3);//少し上下させる
        }

        //Playerの配置
        mPlayer = new Player(playerTexture,0,0,72,72);
        mPlayer.setPosition(WORLD_WIDTH/2-mPlayer.getWidth()/2, Step.STEP_HEIGHT);

        //ゴールのUFOを配置
        mUfo = new Ufo(ufoTexture,0,0,120,74);
        mUfo.setPosition(WORLD_WIDTH/2-Ufo.UFO_WIDTH/2, y);


    }


    //アップデートする

    private void update (float delta){
        switch (mGameState){
            case GAME_STATE_READY:
                updateReady();
                break;
            case GAME_STATE_PLAYING:
                updatePlaying(delta);
                break;
            case GAME_STATE_GAMEOVER:
                updateGameOver();
                break;

        }
    }

    private void updateReady(){
        if (Gdx.input.justTouched()){
            mGameState = GAME_STATE_PLAYING;
        }
    }

    private void updatePlaying(float delta){
        float accel =0;
        if(Gdx.input.isTouched()){
            mGuiViewport.unproject(mTouchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            Rectangle left = new Rectangle(0,0,GUI_WIDTH/2, GUI_HEIGHT);
            Rectangle right = new Rectangle(GUI_WIDTH/2, 0, GUI_WIDTH/2, GUI_HEIGHT);

            if(left.contains(mTouchPoint.x, mTouchPoint.y)){
                accel = 5.0f;
            }
            if(right.contains(mTouchPoint.x, mTouchPoint.y)){
                accel = -5.0f;
            }

        }

        //Step
        for (int i =0 ;  i < mSteps.size(); i++){
            mSteps.get(i).update(delta);
        }

        //Enemy
        for (int i =0 ;  i < mEnemy.size(); i++){
            mEnemy.get(i).update(delta);
        }


        //Player
        if(mPlayer.getY()<=0.5f){
            mPlayer.hitStep();
        }

        mPlayer.update(delta,accel);
        mHeightSoFar = Math.max(mPlayer.getY(), mHeightSoFar);

        //当たり判定
        checkCollision();

        //ゲームオーバー判定
        checkGameOver();

    }

    private void updateGameOver(){
        if (Gdx.input.justTouched()){
            mGame.setScreen((new ResultScreen(mGame,mScore,isPlying)));
        }

    }

    private void checkCollision(){
        //UFOとの当たり判定
        if (mPlayer.getBoundingRectangle().overlaps(mUfo.getBoundingRectangle())){
            msound3.play();
            mGameState = GAME_STATE_GAMEOVER;
            return;
        }

        //Starとの当たり判定
        for (int i =0; i< mStars.size();i++){
            Star star = mStars.get(i);

            if(star.mState == Star.STAR_NONE){
                continue;
            }

            if (mPlayer.getBoundingRectangle().overlaps(star.getBoundingRectangle())){

                msound3.play();
                star.get();
                mScore++;
                if (mScore > mHifhScore){
                    mHifhScore = mScore;

                    //ハイスコア処理
                    mPrefs.putInteger("HIGHSCORE", mHifhScore);
                    mPrefs.flush();
                }
                break;
            }

        }

        //stepとの当たり判定。
        //上昇中は当たり判定を確認しない
        if (mPlayer.velocity.y>0){
            return;
        }

        for(int i =0;i<mSteps.size();i++){
            Step step = mSteps.get(i);

            if(step.mState == Step.STEP_STATE_VANISH){
                continue;
            }

            if(mPlayer.getY()> step.getY()){
                if (mPlayer.getBoundingRectangle().overlaps(step.getBoundingRectangle())) {


                    mPlayer.hitStep();
                    msound2.play();

                    if(mRandom.nextFloat()>0.5f){
                        step.vanish();

                    break;}
                }
            }

        }

        //Enemyとの当たり判定
        for(int i =0;i<mEnemy.size();i++) {
            Enemy enemy = mEnemy.get(i);

            if (mPlayer.getBoundingRectangle().overlaps(enemy.getBoundingRectangle())) {
                msound.play();
                mGameState = GAME_STATE_GAMEOVER;
                return;
            }
        }


    }

    private void checkGameOver(){
        if(mHeightSoFar - CAMERA_HEIGHT/2 > mPlayer.getY()){
            Gdx.app.log("JampActionGame", "GAMEOVER");
            mGameState = GAME_STATE_GAMEOVER;
        }
    }

}
