import com.googlecode.lanterna.input.KeyType;

import java.util.LinkedList;

public class Snake {

    private LinkedList<Square> body = new LinkedList<>();
    private KeyType direction = KeyType.ArrowRight;

    Snake(int x, int y) {
        body.add(new Square(x, y));
        body.add(new Square(x - 1, y));
        body.add(new Square(x - 2, y));
    }

    public LinkedList<Square> getBody() {
        return body;
    }

    public KeyType getDirection() {
        return direction;
    }

    public void setDirection(KeyType direction) {
        this.direction = direction;
    }

    public void reverseSnake() {
        LinkedList<Square> newBody = new LinkedList<>();
        while (body.size() != 0) {
            newBody.add(body.pollLast());
        }
        this.body = newBody;

        //Changing direction according to first new body segments
        int bodyFirX = body.get(0).getX();
        int bodyFirY = body.get(0).getY();
        int bodySecX = body.get(1).getX();
        int bodySecY = body.get(1).getY();

        if (bodyFirX - bodySecX == 0 && bodyFirY - bodySecY > 0)
            direction = KeyType.ArrowDown;
        else if (bodyFirX - bodySecX == 0 && bodyFirY - bodySecY < 0)
            direction = KeyType.ArrowUp;
        else if (bodyFirX - bodySecX > 0 && bodyFirY - bodySecY == 0)
            direction = KeyType.ArrowRight;
        else if (bodyFirX - bodySecX < 0 && bodyFirY - bodySecY == 0)
            direction = KeyType.ArrowLeft;
    }
}
