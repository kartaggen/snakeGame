import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    public static boolean OGO = true; // Original Game Objects
    public static final char[][] objDesigns = new char[][]{{'▧', '●', '•', 'ѽ', 'б'}, {'#', '*', '*', 'o', 'd'}};

    private static Terminal terminal = null;
    private static Game game;
    private static boolean hasEnded;

    public static void main(String[] args) {
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        defaultTerminalFactory.setTerminalEmulatorTitle("Snake");
        try {
            terminal = defaultTerminalFactory.createTerminal();
            loadGame("");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (terminal != null) {
                try {
                    terminal.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void loadGame(String error) throws IOException {
        TextGraphics fieldText = terminal.newTextGraphics();

        TextGraphics objDesignText = terminal.newTextGraphics();
        objDesignText.setForegroundColor(TextColor.ANSI.GREEN);

        StringBuilder filePath = new StringBuilder();
        KeyStroke keyStroke = null;
        terminal.setCursorVisible(true);

        do {
            terminal.clearScreen();
            if (keyStroke != null)
                if (keyStroke.getKeyType() == KeyType.Character)
                    filePath.append(keyStroke.getCharacter());
                else if (keyStroke.getKeyType() == KeyType.Backspace && filePath.length() > 0)
                    filePath.deleteCharAt(filePath.length() - 1);
                else if (keyStroke.getKeyType() == KeyType.Tab)
                    OGO ^= true;

            if (!error.isEmpty()) {
                TextGraphics errorText = terminal.newTextGraphics();
                errorText.setForegroundColor(TextColor.ANSI.RED);
                errorText.putString(0, 1, error, SGR.BOLD);
            }
            fieldText.putString(0, 0, "Enter field file path: " + filePath.toString(), SGR.BOLD);
            objDesignText.putString(0, 3, "You can press Tab to change the game design.");
            objDesignText.putString(0, 4, "Brick - " + objDesigns[OGO ? 1 : 0][0]);
            objDesignText.putString(0, 5, "Snake - " + objDesigns[OGO ? 1 : 0][1]);
            objDesignText.putString(0, 6, "Apple - " + objDesigns[OGO ? 1 : 0][3]);
            objDesignText.putString(0, 7, "Pear - " + objDesigns[OGO ? 1 : 0][4]);
            terminal.setCursorPosition(new TerminalPosition(23 + filePath.toString().length(), 0));
            terminal.flush();
            keyStroke = terminal.readInput();
        } while (keyStroke.getKeyType() != KeyType.Enter);

        //Reading File and initializing Game
        File myObj = new File(filePath.toString());
        if (myObj.isFile()) {
            boolean isValidFile = true;
            Scanner reader = new Scanner(myObj);
            String widthString = reader.nextLine();
            String heightString = reader.nextLine();
            int width = 0;
            int height = 0;
            try {
                width = Integer.parseInt(widthString);
                height = Integer.parseInt(heightString);
            } catch (Exception e) {
                isValidFile = false;
            }
            char[][] newTerrain = new char[height][width];
            for (int y = 0; y < height && isValidFile; y++) {
                String line = reader.nextLine();
                if (line.length() != width || line.matches(".*[^#\\s].*")) isValidFile = false;
                if (!OGO) line = line.replace("#", "▧");
                for (int x = 0; x < width && isValidFile; x++) {
                    newTerrain[y][x] = line.charAt(x);
                }
            }
            if (reader.hasNextLine()) isValidFile = false;
            reader.close();

            if (isValidFile) {
                game = new Game(newTerrain, width, height);
                startGame();
            } else loadGame("File has invalid field.");
        } else loadGame("Wrong field file: " + filePath.toString());
    }

    private static void startGame() throws IOException {
        hasEnded = false;
        terminal.setCursorVisible(false);
        displayGame();

        //Automatic snake movement
        Timer timer = new Timer();
        timer.schedule(new autoMove(), 500, 500);

        //Arrow button snake movement
        KeyStroke keyPress = terminal.readInput();
        while (keyPress.getKeyType() != KeyType.Escape && !hasEnded) {
            if (keyPress.getKeyType() == KeyType.ArrowUp &&
                    (game.getSnake().getDirection() == KeyType.ArrowLeft ||
                            game.getSnake().getDirection() == KeyType.ArrowRight) ||
                    keyPress.getKeyType() == KeyType.ArrowRight &&
                            (game.getSnake().getDirection() == KeyType.ArrowUp ||
                                    game.getSnake().getDirection() == KeyType.ArrowDown) ||
                    keyPress.getKeyType() == KeyType.ArrowDown &&
                            (game.getSnake().getDirection() == KeyType.ArrowLeft ||
                                    game.getSnake().getDirection() == KeyType.ArrowRight) ||
                    keyPress.getKeyType() == KeyType.ArrowLeft &&
                            (game.getSnake().getDirection() == KeyType.ArrowUp ||
                                    game.getSnake().getDirection() == KeyType.ArrowDown)) {
                game.getSnake().setDirection(keyPress.getKeyType());
                game.update();
                hasEnded = game.hasGameEnded();
                displayGame();

                timer.cancel();
                timer = new Timer();
                timer.schedule(new autoMove(), 500, 500);
            }
            if (!hasEnded) keyPress = terminal.readInput();
        }

        //End of game
        timer.cancel();
        terminal.clearScreen();

        TextGraphics endText = terminal.newTextGraphics();
        endText.setForegroundColor(TextColor.ANSI.RED);
        endText.putString(1, 1, "You have died! Your snake was " + game.getSnake().getBody().size() + " squares long.", SGR.BOLD);

        TextGraphics againText = terminal.newTextGraphics();
        againText.setForegroundColor(TextColor.ANSI.DEFAULT);
        againText.putString(0, 3, "Press Enter to play again.");
        againText.putString(0, 4, "Press Esc to exit.");
        terminal.flush();

        do {
            keyPress = terminal.readInput();
            if (keyPress.getKeyType() == KeyType.Enter)
                loadGame("");
        } while (keyPress.getKeyType() != KeyType.Escape &&
                keyPress.getKeyType() != KeyType.Enter);
    }

    private static void displayGame() throws IOException {
        terminal.clearScreen();
        TextGraphics gameGraphics = terminal.newTextGraphics();
        gameGraphics.setForegroundColor(TextColor.ANSI.GREEN);
        int lineInd = 0;
        for (String gameLine : game.getTerrain()) {
            gameGraphics.putString(0, lineInd, gameLine);
            lineInd++;
        }
        gameGraphics.putString(0, lineInd + 2, "Pressing Esc will exit the game.");
        terminal.flush();
    }

    private static class autoMove extends TimerTask {
        public void run() {
            try {
                game.update();
                hasEnded = game.hasGameEnded();
                if (!hasEnded) {
                    displayGame();
                } else {
                    Robot r = new Robot();
                    r.keyPress(KeyEvent.VK_ENTER);
                    r.keyRelease(KeyEvent.VK_ENTER);
                }
            } catch (IOException | AWTException e) {
                e.printStackTrace();
            }
        }
    }
}