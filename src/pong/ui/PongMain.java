package pong.ui;
	
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;


public class PongMain extends Application {
	
	
	private enum UserAction {
        NONE, UP, DOWN
    }

    private static final int APP_W = 800;
    private static final int APP_H = 600;

    private static final int BALL_RADIUS = 10;
    private static final int BAT_W = 20;
    private static final int BAT_H = 80;
    
    private int playerScore=0;
    private int enemyScore=0;
    
    private Label playerScoreText = new Label(""+playerScore);
    private Label enemyScoreText = new Label(""+enemyScore);
    
    private Circle ball = new Circle(BALL_RADIUS);
    private Rectangle paddle1 = new Rectangle(BAT_W, BAT_H);
    private Rectangle paddle2 = new Rectangle(BAT_W, BAT_H);
    
    private Rectangle wallTop = new Rectangle(APP_W,2);
    private Rectangle wallBottom = new Rectangle(APP_W,2);
    private Rectangle wallLeft = new Rectangle(2,APP_H);
    private Rectangle wallRight = new Rectangle(2,APP_H);
    
    private boolean ballLeft = true, ballUp = false;
    private UserAction action = UserAction.NONE;

    private Timeline timeline = new Timeline();
    private boolean running = true;

    private Parent createContent() {
        Pane pane = new Pane();
        pane.setPrefSize(APP_W, APP_H);
        
        playerScoreText.setTranslateY(0);
        playerScoreText.setTranslateX(APP_W/2-25);
        this.enemyScoreText.setTranslateX(APP_W/2+25);
        
        initPaddles();
        initWalls();
        KeyFrame frame = new KeyFrame(Duration.seconds(0.016), event -> {
            if (!running)
                return;
            

            switch (action) {
                case UP:
                    if (paddle1.getTranslateY() - 5 >= 0)
                    	paddle1.setTranslateY(paddle1.getTranslateY() - 5);
                    break;
                case DOWN:
                    if (paddle1.getTranslateY() + BAT_H + 5 <= APP_H)
                    	paddle1.setTranslateY(paddle1.getTranslateY() + 5);
                    break;
                case NONE:
                    break;
                    
            }
            
            initEnemyMovement();
            
            ball.setTranslateX(ball.getTranslateX() + (ballLeft ? -6.5 : 6.5));
            ball.setTranslateY(ball.getTranslateY() + (ballUp ? -6.5 : 6.5));
            
            checkColisions();
        });
        
        
        timeline.getKeyFrames().addAll(frame);
        
        
        timeline.setCycleCount(Timeline.INDEFINITE);
        pane.getChildren().addAll(paddle1,paddle2,wallTop,wallBottom,wallLeft,wallRight,ball,playerScoreText,enemyScoreText);
        
        return pane;
    }  
    
    private void initEnemyMovement(){
    	
    		if (paddle2.getTranslateY() - 5 >= 0 && ballUp&& !ballLeft)
    			
    			paddle2.setTranslateY(paddle2.getTranslateY() - 4.57);
    			
    		else if(paddle2.getTranslateY() + BAT_H  <= APP_H && !ballUp&& !ballLeft)
            		 
        		paddle2.setTranslateY(paddle2.getTranslateY() + 4.57);
    		
    		
    }
    
    private void checkColisions(){
    	Thread thread = new Thread();
    	thread.start();
    	if (ball.getBoundsInParent().intersects(wallLeft.getBoundsInParent())){
    		enemyScore++;
    		enemyScoreText.setText(Integer.toString(enemyScore));
        	restartGame();
    	}	
    	
    		
    	else if(ball.getBoundsInParent().intersects(wallRight.getBoundsInParent())){
    		playerScore++;
    		playerScoreText.setText(Integer.toString(playerScore));
     		restartGame();
    	 }
    		
    	
    	 if (ball.getBoundsInParent().intersects(wallTop.getBoundsInParent()))
            ballUp = false;
        
        else if (ball.getBoundsInParent().intersects(wallBottom.getBoundsInParent()))
            ballUp = true;
        
        else if (paddle1.getBoundsInParent().intersects(ball.getBoundsInParent()))
        	ballLeft = false;
        
        else if (paddle2.getBoundsInParent().intersects(ball.getBoundsInParent()))
        	ballLeft = true;
    }
    
    private void initPaddles(){
    	 paddle1.setTranslateX(0);
         paddle1.setTranslateY((APP_H-BAT_H)/2);
         paddle1.setFill(Color.BLACK);
         
         paddle2.setTranslateX(APP_W-BAT_W);
         paddle2.setTranslateY((APP_H-BAT_H)/2);
         paddle2.setFill(Color.BLACK);
         
    }
    
    private void initWalls(){
    	wallTop.setFill(Color.WHITE);
    	wallBottom.setFill(Color.WHITE);
    	wallLeft.setFill(Color.WHITE);
    	wallRight.setFill(Color.WHITE);
    	
    	wallTop.setTranslateY(0);
    	wallBottom.setTranslateY(APP_H-2);
    	wallLeft.setTranslateX(0);
    	wallRight.setTranslateX(APP_W-2);
    	
    	
    }
        
    private void restartGame(){
    	stopGame();
    	timeline.setDelay(Duration.millis(100));
    	
    	startGame();
    }
    
    private void stopGame(){
    	running = false;
    	timeline.stop();
    }
        
    private void startGame(){
    	ballLeft = true;
    	ballUp = false;
    	ball.setTranslateX(APP_W/2);
    	ball.setTranslateY(0+BALL_RADIUS);
    	
    	timeline.play();
    	running = true;
    }
    
	@Override
	public void start (Stage primaryStage) {
		try {
			
			Scene scene = new Scene(createContent());
			this.keyListener(scene);
			
			primaryStage.setTitle("Pong");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			startGame();
			
		} catch(Exception e) {
			
			e.printStackTrace();
		}
	}
	
	private void keyListener(Scene scene){
		
		scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
			
		      if(key.getCode()==KeyCode.W) {
		          action = UserAction.UP;
		      }
		      else if(key.getCode()==KeyCode.S){
		    	  action = UserAction.DOWN;
		    	  
		      }
		});
		
		scene.addEventHandler(KeyEvent.KEY_RELEASED, (key) -> {
			
			if(key.getCode()==KeyCode.W||key.getCode()==KeyCode.S){
				action = UserAction.NONE;
			}
			
		});
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}


}
