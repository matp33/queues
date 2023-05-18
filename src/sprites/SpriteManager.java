package sprites;

import java.io.IOException;

public class SpriteManager {

    private Sprite clientSprite;
    private Sprite doorSprite;
    private Sprite queueSprite;
    private Sprite backgroundSprite;

    public SpriteManager() throws IOException {
        doorSprite = new Sprite (43, 69, "/door2.png");
        backgroundSprite=new Sprite(620,395,"/supermarket-kolejka.jpg");
        clientSprite = new Sprite (30, 45, "/sprite.png");
        queueSprite = new Sprite (106, 58, "/kasa.png");
    }

    public Sprite getSprite(SpriteType spriteType){
        switch (spriteType){
            case DOOR:
                return doorSprite;
            case CLIENT:
                return clientSprite;
            case QUEUE:
                return queueSprite;
            case BACKGROUND:
                return backgroundSprite;
            default:
                throw new IllegalArgumentException("Unknown sprite type: "+spriteType);
        }
    }

}
