import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.application.Platform;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Random;


public class Anaconda extends Application {
    public static void main(String[] args) { 
        launch(args); 
    }
    
    GridPane GameGrid = new GridPane();
    GridPane TextGrid = new GridPane();

    int GridSizeSquared = 15;

    int highestScore = 0;

    Label Score = new Label("Score: 0");
  
    Label Pause = new Label("Tap to Start");

    ArrayList<Snake> SnakeP = new ArrayList<>(0);

    Timeline Loop;

    double LoopSpeed = 1/6.0;

    int mX = 0, mY = 0;

    int posX = new Random().nextInt(GridSizeSquared), posY =new Random().nextInt(GridSizeSquared);

    Rectangle Food = new Rectangle(30,30,Color.RED);

    int foodN = 0;

    int FoodPosX = new Random().nextInt(GridSizeSquared);
    int FoodPosY = new Random().nextInt(GridSizeSquared);

    boolean start = false;
    boolean dead = false;

    public void start(Stage PrimaryStage) {

        FillGrid(GameGrid);

        //Constructing Snake's Head
        SnakeP.add(new Snake(posX, posY));

        //Seperates Grids to make it look better
        GameGrid.setVgap(1.5);
        GameGrid.setHgap(1.5);
        TextGrid.setVgap(1.5);
        TextGrid.setHgap(2);

        //Setting Grid Positions
        GameGrid.setAlignment(Pos.CENTER);
        TextGrid.setAlignment(Pos.BOTTOM_CENTER);

        //Adding Food and Snake Head in their initial positions
        GameGrid.add(Food, FoodPosX,FoodPosY);
        GameGrid.add(SnakeP.get(0).body, posX,posY);
        // Display highest score along with current score
        Label HighScore = new Label("High Score:" + highestScore);

        //Adding Text to Grid
        TextGrid.add(Score, 1, 0,3,1);
        TextGrid.add(HighScore, 1, 1, 3, 1);
        TextGrid.add(Pause, 1, 3,3,1);

        //Allows us to use both grids in the same screen
        FlowPane Screen = new FlowPane(Orientation.HORIZONTAL,GameGrid, TextGrid);

        // Creating Game Scene with a black background
        Scene Game = new Scene(Screen);
        Game.setFill(Color.BLACK);

        //Detects a Key Being Pressed
        Game.setOnKeyPressed(this::KeyPressedProcess);

        //Generates Window
        PrimaryStage.setTitle("Snake Game");
        PrimaryStage.setScene(Game);
        PrimaryStage.show();

        //Initializing Loop as timeline.
        Loop = new Timeline(new KeyFrame(Duration.seconds(LoopSpeed),
                new EventHandler<ActionEvent>()
                {

            @Override
            public void handle(ActionEvent event) {

                //Moves Snake
                MoveChar();
            }
               }));
        Loop.setCycleCount(Timeline.INDEFINITE);
        //^ Loop will run endlessly
    }

    public void MoveChar() {
        // Wrap around to the opposite side if the snake hits a wall
        posX = (posX + mX + GridSizeSquared) % GridSizeSquared;
        posY = (posY + mY + GridSizeSquared) % GridSizeSquared;

        // Updates head position
        GameGrid.getChildren().remove(SnakeP.get(0).body);
        GameGrid.add(SnakeP.get(0).body, posX, posY);
        SnakeP.get(0).setPos(posX, posY);

        // Update for the rest of the body
        if (SnakeP.size() > 1) {
            for (int x = 1; x < SnakeP.size(); x++) {
                GameGrid.getChildren().remove(SnakeP.get(x).body);
                GameGrid.add(SnakeP.get(x).body, SnakeP.get(x - 1).getOldXpos(), SnakeP.get(x - 1).getOldYpos());
                SnakeP.get(x).setPos(SnakeP.get(x - 1).getOldXpos(), SnakeP.get(x - 1).getOldYpos());
            }
        }

        // If you find food, the snake will grow
        if (posX == FoodPosX && posY == FoodPosY) {
            // Grows Snake
            Grow();
        }

        // If you crash into any part of your body, then die
        for (int x = 1; x < SnakeP.size(); x++) {
            if (posX == SnakeP.get(x).getXpos() && posY == SnakeP.get(x).getYpos()) {
                Die();
            }
        }
    }

    //Detects Key Presses
    public void KeyPressedProcess(KeyEvent event) {
        //If you GameOver and Restart
        if(start == false && dead && event.getCode()==KeyCode.ENTER)
        {
            Pause.setText("\"Enter\" to Pause");
            Score.setText("Score: 0");
            Loop.play();
            start = true;
            dead = false;
        }
        //If Paused and Resumed
        else if(start == false && dead == false)
        {
            Pause.setText("\"Enter\" to Pause");
            Loop.play();
            start = true;
        }

        //If Enter is pressed, game will pause
        if (event.getCode() == KeyCode.ENTER)
        {
            Pause.setText("Press Any Key to Resume");
            Loop.stop();
            start = false;
        }

        //Changes direction to UP when up/W is pressed
        if(mY ==0 && (event.getCode() == KeyCode.W || event.getCode() == KeyCode.UP))
        {
            mX = 0;
            mY = -1;
        }
        //Changes direction to DOWN when down/S is pressed
        else if(mY == 0 && (event.getCode() == KeyCode.S || event.getCode() == KeyCode.DOWN))
        {
            mX = 0;
            mY = 1;
        }
        //Changes direction to Left when left/A is pressed
        else if(mX ==0 && (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT))
        {
            mX = -1;
            mY = 0;
        }
        //Changes direction to Right when right/D is pressed
        else if(mX == 0 && (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT))
        {
            mX = 1;
            mY = 0;
        }

        //Closes program when escape is pressed
        if(event.getCode() == KeyCode.ESCAPE)
            System.exit(0);
    }

    //Fills Grid with rectangles
    public void FillGrid(GridPane Grid) {
        for(int x =0;x<GridSizeSquared;x++) {
            GameGrid.addColumn(x,new Rectangle(30,30, Color.rgb(179, 201, 161)));

           for(int y = 1; y < GridSizeSquared;y++)
            GameGrid.addRow(y,new Rectangle(30,30, Color.rgb(179, 201, 161)));
        }
    }

    //Changes randomly Food's position
    public void PlaceFood() {
        Random rPos = new Random();

        int newPosX =  rPos.nextInt(GridSizeSquared);
        int newPosY =  rPos.nextInt(GridSizeSquared);

        FoodPosX = newPosX;
        FoodPosY = newPosY;

        GameGrid.getChildren().remove(Food);
        GameGrid.add(Food, newPosX,newPosY);
    }

    //Grows Snake's Body
    public void Grow() {
        //Adds new Tail where last Tail's position was
        SnakeP.add(new Snake(SnakeP.get(SnakeP.size()-1).getOldXpos(),
                SnakeP.get(SnakeP.size()-1).getOldYpos()));

        GameGrid.add(SnakeP.get(SnakeP.size()-1).body,
                SnakeP.get(SnakeP.size()-1).getOldXpos(),
                SnakeP.get(SnakeP.size()-1).getOldYpos());

        foodN ++;
        Score.setText("Score: " + foodN);

        //Randomly Places new Food
        PlaceFood();

        // Update highest score if the current score surpasses it
        if (foodN > highestScore) {
            highestScore = foodN;
            TextGrid.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().startsWith("High Score:"));
            Label HighScore = new Label("High Score:" + highestScore);
            TextGrid.add(HighScore, 1, 1, 3, 1);
        }
    }

    //Makes you Game Over
    public void Die() {
    int size = SnakeP.size();

    // First Removes all but the head from the grid
    for (int x = size - 1; x > 0; x--)
        GameGrid.getChildren().remove(SnakeP.get(x).body);

    // Now Removes all but the head from the arrayList
    for (int x = size - 1; x > 0; x--)
        SnakeP.remove(x);

    // Pauses Game and lets the game know that you lost
    start = false;
    dead = true;
    Loop.stop();

    Platform.runLater(() -> {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Game Over :(");
        alert.setContentText("Your Score: " + foodN + "\nHighest Score: " + highestScore + "\nDo you want to try again?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                restartGame();
            } else {
                System.exit(0);
            }
        });
    });
}

    private void restartGame() {
        // Reset game state
        GameGrid.getChildren().remove(SnakeP.get(0).body);
        posX = new Random().nextInt(GridSizeSquared);
        posY = new Random().nextInt(GridSizeSquared);
        GameGrid.add(SnakeP.get(0).body, posX, posY);
        SnakeP.get(0).setPos(posX, posY);

        // Clear the game grid and ArrayList
        for (int x = SnakeP.size() - 1; x > 0; x--)
            SnakeP.remove(x);

        // Reset score
        foodN = 0;
        Score.setText("Score: " + foodN);
        // Display highest score along with current score
        TextGrid.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().startsWith("High Score:"));
        Label HighScore = new Label("High Score:" + highestScore);
        TextGrid.add(HighScore, 1, 1, 3, 1);

        // Restart the game loop
        start = true;
        dead = false;
        Loop.play();
    }
}

class Snake {
    public Rectangle body = new Rectangle(30, 30, Color.GREEN);
    private int Xpos;
    private int Ypos;
    private int oldXpos;
    private int oldYpos;

    Snake(int X, int Y) {
        oldXpos = Xpos = X;
        oldYpos = Ypos = Y;
    }

    public void setPos(int X, int Y) {
        oldXpos =Xpos;
        oldYpos =Ypos;

        Xpos =X;
        Ypos =Y;
    }

    public int getOldXpos() { 
        return oldXpos; 
    }

    public int getOldYpos() {
        return oldYpos;
    }

    public int getXpos() {
        return Xpos; 
    }

    public int getYpos() {
        return Ypos; 
    }
}