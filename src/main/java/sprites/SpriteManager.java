package sprites;

import spring2.Bean;
import visualComponents.Background;
import visualComponents.Client;
import visualComponents.Door;
import visualComponents.StoreCheckout;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SpriteManager {

    private static Sprite clientSprite;
    private static Sprite doorSprite;
    private static Sprite storeCheckoutSprite;
    private static Sprite backgroundSprite;

    private static Map<Class<?>, Sprite> sprites = new HashMap<>();

    public static final int CLIENT_WIDTH = 30;

    public static final int CLIENT_HEIGHT = 45;
    public static final int CHECKOUT_WIDTH = 106;
    public static final int CHECKOUT_HEIGHT = 58;

    static {
        try {
            doorSprite = new Sprite (43, 69, "/door2.png");
            backgroundSprite=new Sprite(620,395,"/supermarket-kolejka.jpg");
            clientSprite = new Sprite (CLIENT_WIDTH, CLIENT_HEIGHT, "/sprite.png", true);
            storeCheckoutSprite = new Sprite (CHECKOUT_WIDTH, CHECKOUT_HEIGHT, "/kasa.png");

            sprites.put(Door.class, doorSprite);
            sprites.put(Client.class, clientSprite);
            sprites.put(StoreCheckout.class, storeCheckoutSprite);
            sprites.put(Background.class, backgroundSprite);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static Sprite getSprite(Class<?> classType){
        return sprites.get(classType);
    }

}
