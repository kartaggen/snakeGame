public class Square {

    private int x;
    private int y;

    Square() {
    }

    Square(Square square) {
        this.x = square.x;
        this.y = square.y;
    }

    Square(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}