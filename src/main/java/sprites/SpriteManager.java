package sprites;

import spring2.Bean;

import java.io.IOException;
@Bean
public class SpriteManager {

    private Sprite clientSprite;
    private Sprite doorSprite;
    private Sprite storeCheckoutSprite;
    private Sprite backgroundSprite;

    public void loadSprites () throws IOException {
        doorSprite = new Sprite (43, 69, "/door2.png");
        backgroundSprite=new Sprite(620,395,"/supermarket-kolejka.jpg");
        clientSprite = new Sprite (30, 45, "/sprite.png");
        storeCheckoutSprite = new Sprite (106, 58, "/kasa.png");
    }

    public Sprite getSprite(SpriteType spriteType){
        switch (spriteType){
            case DOOR:
                return doorSprite;
            case CLIENT:
                return clientSprite;
            case STORE_CHECKOUT:
                return storeCheckoutSprite;
            case BACKGROUND:
                return backgroundSprite;
            default:
                throw new IllegalArgumentException("Unknown sprite type: "+spriteType);
        }
    }

}
