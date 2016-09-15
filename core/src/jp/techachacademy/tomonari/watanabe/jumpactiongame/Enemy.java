package jp.techachacademy.tomonari.watanabe.jumpactiongame;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by moggmogg on 2016/09/14.
 */
public class Enemy extends GameObject {

    //横幅と高さ
    public static final float ENEMY_WIDTH = 1.0f;
    public static final float ENEMY_HEIGHT = 1.0f;

    //タイプ（通常とウゴクタイプ）
    public static final int ENEMY_TYPE_STATIC = 0;
    public static final int ENEMY_TYPE_MOVING = 1;

    //状態（通常と消えた状態）
    public static final int ENEMY_STATE_NORMAL = 0;
    public static final int ENEMY_STATE_VANISH = 1;

    //速度
    public static final float ENEMY_VELOCITY = 2.0f;

    int mType;
    int mState;

    float yHeight;


    public Enemy (int type, Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        super(texture, srcX, srcY, srcWidth, srcHeight);
        setSize(ENEMY_WIDTH, ENEMY_HEIGHT);
        mType = type;

        velocity.x = ENEMY_VELOCITY;
        velocity.y = ENEMY_VELOCITY;
        yHeight = 0;

    }

    //座標を更新
    public void update(float deltaTime) {
        if (mType == ENEMY_TYPE_MOVING) {
            setX(getX() + velocity.x * deltaTime);

            yHeight = yHeight+velocity.y * deltaTime;
            setY(getY() + velocity.y * deltaTime);


            if (getX() < ENEMY_WIDTH / 2) {
                velocity.x = -velocity.x;
                setX(ENEMY_WIDTH / 2);
            }

            if (getX() > GameScreen.WORLD_WIDTH - ENEMY_WIDTH / 2) {
                velocity.x = -velocity.x;
                setX(GameScreen.WORLD_WIDTH - ENEMY_WIDTH / 2);
            }

            if (yHeight < 2) {
                velocity.y = -velocity.y;
            }

            if (yHeight > -2) {
                velocity.y = -velocity.y;
            }

        }
    }

}