import java.util.Random;

public class Game {

    private final char[][] terrain;
    private final int width;
    private final int height;
    private Snake snake;
    private Square food;
    private int foodExp;
    private boolean gameEnded = false;

    Game(char[][] terrain, int width, int height) {
        this.terrain = terrain;
        this.width = width;
        this.height = height;
        placeSnake();
        placeFood();
    }

    public String[] getTerrain() {
        String[] res = new String[height];
        for (int y = 0; y < height; y++) {
            String line = new String(terrain[y]);
            res[y] = line;
        }
        return res;
    }

    public Snake getSnake() {
        return snake;
    }

    public boolean hasGameEnded() {
        return gameEnded;
    }

    public void placeSnake() {
        boolean hasFreeSpaceForSnake = false;
        while (!hasFreeSpaceForSnake) {
            Random rand = new Random();
            int posSnakeX = rand.nextInt(width - 2) + 1;
            int posSnakeY = rand.nextInt(height - 2) + 1;
            if (terrain[posSnakeY][posSnakeX] == ' ' &&
                    terrain[posSnakeY][posSnakeX - 1] == ' ' &&
                    terrain[posSnakeY][posSnakeX - 2] == ' ') {
                terrain[posSnakeY][posSnakeX] = Main.objDesigns[Main.OGO ? 1 : 0][1];
                terrain[posSnakeY][posSnakeX - 1] = Main.objDesigns[Main.OGO ? 1 : 0][2];
                terrain[posSnakeY][posSnakeX - 2] = Main.objDesigns[Main.OGO ? 1 : 0][2];
                snake = new Snake(posSnakeX, posSnakeY);
                hasFreeSpaceForSnake = true;
            }
        }
    }

    public void placeFood() {
        foodExp = 10;
        boolean hasFreeSpaceForFood = false;
        while (!hasFreeSpaceForFood) {
            Random rand = new Random();
            int posSnakeX = rand.nextInt(width - 2) + 1;
            int posSnakeY = rand.nextInt(height - 2) + 1;
            if (terrain[posSnakeY][posSnakeX] == ' ') {
                if (rand.nextInt(6) < 5) // Probability 5 to 1
                    terrain[posSnakeY][posSnakeX] = Main.objDesigns[Main.OGO ? 1 : 0][3];
                else
                    terrain[posSnakeY][posSnakeX] = Main.objDesigns[Main.OGO ? 1 : 0][4];
                hasFreeSpaceForFood = true;
                food = new Square(posSnakeX, posSnakeY);
            }
        }
    }

    public void update() {
        foodExp--;
        if (foodExp == 0) {
            terrain[food.getY()][food.getX()] = ' ';
            placeFood();
        }
        Square newSquare = new Square();
        switch (snake.getDirection()) {
            case ArrowUp:
                newSquare.setX(snake.getBody().getFirst().getX());
                newSquare.setY(snake.getBody().getFirst().getY() - 1);
                break;
            case ArrowRight:
                newSquare.setX(snake.getBody().getFirst().getX() + 1);
                newSquare.setY(snake.getBody().getFirst().getY());
                break;
            case ArrowDown:
                newSquare.setX(snake.getBody().getFirst().getX());
                newSquare.setY(snake.getBody().getFirst().getY() + 1);
                break;
            case ArrowLeft:
                newSquare.setX(snake.getBody().getFirst().getX() - 1);
                newSquare.setY(snake.getBody().getFirst().getY());
                break;
        }
        if (terrain[newSquare.getY()][newSquare.getX()] == Main.objDesigns[Main.OGO ? 1 : 0][0] ||
                terrain[newSquare.getY()][newSquare.getX()] == Main.objDesigns[Main.OGO ? 1 : 0][2]) {
            gameEnded = true;
        } else if (terrain[newSquare.getY()][newSquare.getX()] == ' ') {
            terrain[newSquare.getY()][newSquare.getX()] = Main.objDesigns[Main.OGO ? 1 : 0][1];
            terrain[snake.getBody().getFirst().getY()][snake.getBody().getFirst().getX()] = Main.objDesigns[Main.OGO ? 1 : 0][2];
            terrain[snake.getBody().getLast().getY()][snake.getBody().getLast().getX()] = ' ';
            snake.getBody().addFirst(newSquare);
            snake.getBody().removeLast();
        } else if (terrain[newSquare.getY()][newSquare.getX()] == Main.objDesigns[Main.OGO ? 1 : 0][3]) {
            terrain[newSquare.getY()][newSquare.getX()] = Main.objDesigns[Main.OGO ? 1 : 0][1];
            snake.getBody().addFirst(newSquare);
            terrain[snake.getBody().get(1).getY()][snake.getBody().get(1).getX()] = Main.objDesigns[Main.OGO ? 1 : 0][2];
            placeFood();
        } else if (terrain[newSquare.getY()][newSquare.getX()] == Main.objDesigns[Main.OGO ? 1 : 0][4]) {
            terrain[newSquare.getY()][newSquare.getX()] = Main.objDesigns[Main.OGO ? 1 : 0][2];
            snake.getBody().addFirst(newSquare);
            terrain[snake.getBody().get(1).getY()][snake.getBody().get(1).getX()] = Main.objDesigns[Main.OGO ? 1 : 0][2];
            snake.reverseSnake();
            terrain[snake.getBody().getFirst().getY()][snake.getBody().getFirst().getX()] = Main.objDesigns[Main.OGO ? 1 : 0][1];
            placeFood();
        }
    }
}
