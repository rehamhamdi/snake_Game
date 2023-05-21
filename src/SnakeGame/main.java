package SnakeGame;

import java.io.File;
import java.util.Random;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class main extends Application {

    private static final int WIDTH = 400; // The width of the window
    private static final int HEIGHT = 400; // The height of the window
    private static final int RADIUS = 8;   //The raduis of food
    private static final int TIME = 100;
    private int score = 0;
    private Pane root;
    private Random random;
    private Circle food;
    private Snake snake;
    private Button startButton;  //button for start playing
    private Button retryButton; // button for play again after lose
    private Button exitButton; //button for end the game
    private Button pauseButton; //button for stop and continue the game 
    private Button backButton; //button for back the previous page
    private Button classicButton; // Classic mode button
    private Button modernButton;  // Modern mode button
    private Timeline timeline;
    private Text scoreText;
    private MediaPlayer mediaPlayer1;
    private MediaPlayer mediaPlayer2;
    private MediaPlayer mediaPlayer3;
    boolean isClassicMode = true;
    private boolean gameOver = false;

    private void handleStartButton() { // in case of clicking on start button
        root.getChildren().remove(startButton);
        ImageView Image = new ImageView(new Image("ModeGame.gif"));
        Image.setFitWidth(WIDTH);
        Image.setFitHeight(HEIGHT);
        root.getChildren().add(Image);
        root.getChildren().add(classicButton);
        root.getChildren().add(modernButton);
    }

    private void handleClassicButton() {// in case of clicking on classic button
        isClassicMode = true;
        startGame();
        move();
        mediaPlayer1.stop();
    }

    private void handleModernButton() { // in case of clicking on modern button
        isClassicMode = false;
        startGame();
        move();
        mediaPlayer1.stop();
    }

    private void createFood() {
        food = new Circle(random.nextInt(30, WIDTH), random.nextInt(50, HEIGHT), RADIUS);
        if (isClassicMode) {
            food.setFill(Color.BROWN);
        } else {
            food.setFill(Color.DARKORANGE);
        }
        root.getChildren().add(food);
    }

    private void createSnake() {
        snake = new Snake(WIDTH / 2, HEIGHT / 2, RADIUS + 2);
        root.getChildren().add(snake);
        if (isClassicMode) {
            snake.setFill(Color.RED);
        } else {
            snake.setFill(Color.DARKGOLDENROD);
        }
    }

    private void addChessBoardBackground() { //adding background to the window
        int SQUARE_SIZE = 20;
        Color color;
        for (int row = 0; row < HEIGHT / SQUARE_SIZE; row++) {
            for (int col = 0; col < WIDTH / SQUARE_SIZE; col++) {
                color = ((row + col) % 2 == 0) ? Color.DARKGREEN : Color.GREEN;
                Rectangle square = new Rectangle(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                square.setFill(color);
                root.getChildren().add(square);
            }
        }
    }

    private void startGame() {
        if (isClassicMode) {
            addChessBoardBackground();
        } else {
            ImageView modemodern = new ImageView(new Image("modernmode_image.jpg"));
            modemodern.setFitWidth(WIDTH);
            modemodern.setFitHeight(HEIGHT);
            root.getChildren().add(modemodern);

        }
        root.getChildren().add(backButton);
        root.getChildren().add(pauseButton);

        createFood();
        createSnake();
        score = 0;
        scoreText = new Text(10, 20, "Score: 0");
        scoreText.setFill(Color.RED);
        scoreText.setStyle("-fx-font-size: 24px;-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        root.getChildren().add(scoreText);
        root.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.UP) {
                if (snake.getLength() < 2 || snake.getCurrentDirection() != Direction.DOWN) {
                    snake.setCurrentDirection(Direction.UP);
                }
            }
            if (e.getCode() == KeyCode.DOWN) {
                if (snake.getLength() < 2 || snake.getCurrentDirection() != Direction.UP) {
                    snake.setCurrentDirection(Direction.DOWN);
                }
            }
            if (e.getCode() == KeyCode.LEFT) {
                if (snake.getLength() < 2 || snake.getCurrentDirection() != Direction.RIGHT) {
                    snake.setCurrentDirection(Direction.LEFT);
                }
            }
            if (e.getCode() == KeyCode.RIGHT) {
                if (snake.getLength() < 2 || snake.getCurrentDirection() != Direction.LEFT) {
                    snake.setCurrentDirection(Direction.RIGHT);
                }
            }
        });
        timeline = new Timeline(new KeyFrame(Duration.millis(TIME), event -> move()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        root.requestFocus();
    }

    private void move() { // for moving the snake , check collision between food and snake and check game over
        if (timeline.getStatus() == Animation.Status.PAUSED) {
            return;
        }
        snake.step();
        if (isClassicMode) {
            adjustLocation();
            if (checkGameOver()) {
                gamaOver();
            }
        } else {
            adjustLocation2();
            if (checkGameOver2()) {
                gamaOver();
            }
        }
        if (checkCollision()) {
            snake.eat(food);
            String eat = "EatSound.mp3";
            Media eatsound = new Media(new File(eat).toURI().toString());
            mediaPlayer2 = new MediaPlayer(eatsound);
            mediaPlayer2.play();
            createFood();
            score++;
            scoreText.setText("Score: " + score);
            timeline.setRate(timeline.getCurrentRate() + .1);
        }
    }

    private void adjustLocation() {

        if (snake.getCenterX() < 0) {
            snake.setCenterX(WIDTH);
        } else if (snake.getCenterX() > WIDTH) {
            snake.setCenterX(0);
        }
        if (snake.getCenterY() < 0) {
            snake.setCenterY(HEIGHT);
        } else if (snake.getCenterY() > HEIGHT) {
            snake.setCenterY(0);
        }
    }

    private void adjustLocation2() {
        if (snake.getCenterX() < 0 || snake.getCenterX() >= WIDTH || snake.getCenterY() < 0 || snake.getCenterY() >= HEIGHT) {
            gameOver = true;
        }
    }

    private boolean checkCollision() {
        return food.intersects(snake.getBoundsInLocal());
    }

    private boolean checkGameOver() {

        return snake.eatSelf();

    }

    private boolean checkGameOver2() {

        return snake.eatSelf() || gameOver;
    }

    private void gamaOver() {
        String endgame = "endGame.mp3";
        Media endsound = new Media(new File(endgame).toURI().toString());
        mediaPlayer3 = new MediaPlayer(endsound);
        mediaPlayer3.play();
        root.getChildren().clear();
        ImageView endImage = new ImageView(new Image("imageEnd.jpg"));
        endImage.setFitWidth(WIDTH);
        endImage.setFitHeight(HEIGHT);
        root.getChildren().add(endImage);
        Text gameOverText = new Text(WIDTH / 2 - 25, HEIGHT / 2 - 45, "Game Over \n Score: " + score);
        gameOverText.setFill(Color.RED);
        gameOverText.setStyle("-fx-font-size: 24px;-fx-text-fill: white; -fx-font-size: 35px; -fx-font-weight: bold;");
        root.getChildren().add(gameOverText);
        root.getChildren().add(retryButton); // Add retry button
        root.getChildren().add(exitButton);// Add exit button
        timeline.stop();
    }

    private void handleRetryButton() {
        root.getChildren().remove(retryButton);
        gameOver = false;
        mediaPlayer3.stop();
        ImageView Image = new ImageView(new Image("ModeGame.gif"));
        Image.setFitWidth(WIDTH);
        Image.setFitHeight(HEIGHT);
        root.getChildren().add(Image);
        root.getChildren().add(classicButton);
        root.getChildren().add(modernButton);
        mediaPlayer1.play();
    }

    private void handleexitButton() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    private void handlepauseButton() {
        //pauseButton = new Button("<|");
        if (timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.pause();
             pauseButton.setText("◀");

        } else {
             pauseButton.setText("||");
            timeline.play();
            root.requestFocus();
        }
       
    }

    private void handleBackButton() {
        root.getChildren().clear();
        ImageView Image = new ImageView(new Image("ModeGame.gif"));
        Image.setFitWidth(WIDTH);
        Image.setFitHeight(HEIGHT);
        root.getChildren().add(Image);
        root.getChildren().add(classicButton);
        root.getChildren().add(modernButton);
        mediaPlayer1.play();
        timeline.stop();
    }

    @Override
    public void start(Stage primaryStage) {

        root = new Pane();
        root.setPrefSize(WIDTH, HEIGHT);
        random = new Random();
        ImageView startImage = new ImageView(new Image("startphoto.gif"));
        startImage.setFitWidth(WIDTH);
        startImage.setFitHeight(HEIGHT);
        root.getChildren().add(startImage);
        String startGame = "startGame.mp3";
        Media startsound = new Media(new File(startGame).toURI().toString());
        mediaPlayer1 = new MediaPlayer(startsound);
        mediaPlayer1.play();
        startButton = new Button("Start");
        startButton.setLayoutX(WIDTH / 2 - 50);
        startButton.setLayoutY(HEIGHT / 2 + 120);
        startButton.setPrefWidth(100);
        startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        startButton.setOnAction(e -> handleStartButton());
        root.getChildren().add(startButton);
        retryButton = new Button("Retry");
        retryButton.setLayoutX(WIDTH / 2 - 20);
        retryButton.setLayoutY(HEIGHT / 2 + 20);
        retryButton.setPrefWidth(70);
        retryButton.setStyle("-fx-background-color: CHARTREUSE; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        retryButton.setOnAction(e -> handleRetryButton());
        exitButton = new Button("Exit");
        exitButton.setLayoutX(WIDTH / 2 + 70);
        exitButton.setLayoutY(HEIGHT / 2 + 20);
        exitButton.setPrefWidth(60);
        exitButton.setStyle("-fx-background-color: RED; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        exitButton.setOnAction(e -> handleexitButton());
        pauseButton = new Button("||");
        pauseButton.setLayoutX(WIDTH - 105);
        pauseButton.setLayoutY(5);
        pauseButton.setPrefWidth(30);
        pauseButton.setOnAction(e -> handlepauseButton());
        pauseButton.setStyle("-fx-background-color:RED; -fx-text-fill: BLACK; -fx-font-size: 16px; -fx-font-weight: bold;");
        backButton = new Button("Back");
        backButton.setLayoutX(WIDTH -70 );
        backButton.setLayoutY(5);
        backButton.setPrefWidth(70);
        backButton.setOnAction(e -> handleBackButton());
        backButton.setStyle("-fx-background-color:RED; -fx-text-fill: BLACK; -fx-font-size: 16px; -fx-font-weight: bold;");

        classicButton = new Button("Classic");
        classicButton.setLayoutX(WIDTH / 2 - 50);
        classicButton.setLayoutY(HEIGHT / 2 + 110);
        classicButton.setPrefWidth(100);
        classicButton.setStyle("-fx-background-color: CADETBLUE; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        classicButton.setOnAction(e -> handleClassicButton());
        modernButton = new Button("Modern");
        modernButton.setLayoutX(WIDTH / 2 - 50);
        modernButton.setLayoutY(HEIGHT / 2 + 150);
        modernButton.setPrefWidth(100);
        modernButton.setStyle("-fx-background-color:CADETBLUE; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        modernButton.setOnAction(e -> handleModernButton());
        Scene scene = new Scene(root);
        primaryStage.setTitle("Snake");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
