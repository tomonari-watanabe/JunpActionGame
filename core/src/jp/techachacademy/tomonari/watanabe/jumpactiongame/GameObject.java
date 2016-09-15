package jp.techachacademy.tomonari.watanabe.jumpactiongame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by moggmogg on 2016/09/06.
 */
public class GameObject extends Sprite {
    public final Vector2 velocity;//x,y方向の速度を保持

    public GameObject(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight){
        super(texture, srcX, srcY, srcWidth, srcHeight);
        velocity = new Vector2();
    }


}
