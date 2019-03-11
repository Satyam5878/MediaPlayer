package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NewInstance {

	/**
	 * Field Variables
	 */
	
//DataStore 
	DataStores dsObj;
	private final String dsString = "datastore";
	
	private Stage stage;
	private Scene scene;
	private VBox root;
	private BorderPane bpane;
	
	private ObservableSet<File> mediaPlayListFile;
	
	//top border pane variable
	private HBox hbox1,hbox2,hbox3;
	/*private Button closeBtn,minimizeBtn,fullScreenBtn;*/
	private ImageView closeBtn,minimizeBtn,fullScreenBtn,maximizeBtn;
	private boolean maximized=false;
	private BooleanProperty fullScreen = new SimpleBooleanProperty(false);
	private Slider slider;
	private MenuBar menubar;
	private Menu menu;
	private MenuItem newInstance;
	
	//bottom border pane
	private HBox hbox4,hbox5,hbox6,hbox7,hbox8,hbox9,hbox10;
	private VBox vbox1;
	private Label currentTimeLbl,totalTimeLbl;
	private Slider slider2;
	private IntegerProperty slider2MinProperty = new SimpleIntegerProperty(0),
							slider2MaxProperty = new SimpleIntegerProperty(0),
							slider2ValueProperty = new SimpleIntegerProperty(0);
	private StringProperty currentTimeLblProperty = new SimpleStringProperty("00:00:00");
	private StringProperty totalTimeLblProperty = new SimpleStringProperty("00:00:00");
	private /*StringProperty*/BooleanProperty playPauseProperty = new SimpleBooleanProperty(/*"play*/true);
	private ImageView playPauseImgView;
	private ImageView stopImageView;
	private StringProperty stopProperty = new SimpleStringProperty("stop_not_active");
	private ImageView fwdImgView,bckwdImgView;
	private StringProperty fwdProperty = new SimpleStringProperty("f_not_active");
	private StringProperty bckwdProperty = new SimpleStringProperty("b_not_active");
	
	private ImageView volumeImageView;
	private StringProperty volumeImageViewProperty = new SimpleStringProperty("volume_active");
	private Slider volumeSlider;
	private DoubleProperty volumeValueProperty = new SimpleDoubleProperty(0.3);
	private BooleanProperty volumeMuteProperty = new SimpleBooleanProperty(false);
	
	private ImageView screenshotImageView;
	private StringProperty ssImageViewProperty = new SimpleStringProperty("screenshot_not_active");
	
	private ImageView openDirectoryImageView;
	private ImageView openPlayListImageView;
	
	private TextField generalDisplayTxtFld;
	private StringProperty gdTxtFldProperty = new SimpleStringProperty("Some Thing To Display.");
	
	//center borderPane
//	private VBox vbox2;
	private GridPane vbox2;
	private MediaView mediaView;
	private boolean BulkAdditionOfFile = false;
	//for stage moving 
	private double startX,startY,draggOffsetX,draggOffsetY;
	
	//Media Player Property for Bounding
	private DoubleProperty rateProperty = new SimpleDoubleProperty(1.0);
	
	private DoubleProperty volumeProperty = new SimpleDoubleProperty(0.5);
	private BooleanProperty muteProperty = new SimpleBooleanProperty(false);
	private DoubleProperty balanceProperty = new SimpleDoubleProperty(0.0);
	
	private ObjectProperty<Duration> currentTimeProperty = new SimpleObjectProperty<Duration>();
	private ObjectProperty<Duration> totalTimeProperty = new SimpleObjectProperty<Duration>();
	private ObjectProperty<Duration> startTimeProperty = new SimpleObjectProperty<Duration>();
	private ObjectProperty<Duration> stopTimeProperty = new SimpleObjectProperty<Duration>();
	
	private StringProperty titleProperty =  new SimpleStringProperty("");
	//private BooleanProperty play_pauseProperty = new SimpleBooleanProperty(t);
	
	public NewInstance(Stage stage) throws InterruptedException, ClassNotFoundException, IOException{
		this.stage = stage;
		loadDataStores();
		createDirectories();
		
		createMediaListFile();
		decorateStage();
		addDnDHandlerToScene();
		
		bindMediaPlayerProperty();
		
		System.out.println("Stage Showning");
		this.stage.show();
		this.stage.getIcons().add(new Image(this.getClass().getClassLoader().getResource("Logo2.jpg").toString()));
		
	}
	private void bindMediaPlayerProperty(){
		//binding PlayerRate
		this.rateProperty.addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub
				gdTxtFldProperty.set("Playing Rate : "+String.format("%.1f",newValue.doubleValue()));
			}
			
		});
		this.volumeProperty.addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub
				gdTxtFldProperty.set("Volume : "+String.format("%.2f",newValue.doubleValue()));
			}
		});
		this.muteProperty.addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// TODO Auto-generated method stub
				gdTxtFldProperty.set("Mute : "+newValue.toString());
			}
		});
		this.balanceProperty.addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub
				gdTxtFldProperty.set("Speaker Balance :: "+((newValue.doubleValue()<0)?"Left More":(newValue.doubleValue() == 0.0?"Balanced":"Right More"))+newValue.doubleValue());
		
				gdTxtFldProperty.set("Speaker Balance :: "+
				((newValue.doubleValue()<0)?"Left More ":
						((newValue.doubleValue() == 0.0)?"Both Balanced ":"Right More "))+String.format("%.2f",newValue.doubleValue()));
			
			}
		});
		
		this.currentTimeProperty.addListener(new ChangeListener<Duration>(){
			@Override
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
				// TODO Auto-generated method stub
				//currentTimeProperty.set(newValue);
				//System.out.println("currentTimeProperty : "+newValue.toString());
				currentTimeLbl.textProperty().set(String.format("%02d:%02d:%02d",
						TimeUnit.MILLISECONDS.toHours((long) newValue.toMillis()),
						TimeUnit.MILLISECONDS.toMinutes((long) newValue.toMillis())
								-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours((long)newValue.toMillis())),
						TimeUnit.MILLISECONDS.toSeconds((long) newValue.toMillis())
							-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) newValue.toMillis()))
						));
				slider2.setValue(newValue.toSeconds());
				//slider2ValueProperty.set((int) newValue.toSeconds());
			}
		});
		
		this.totalTimeProperty.addListener(new ChangeListener<Duration>(){
			@Override
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
				
				totalTimeLbl.textProperty().set(String.format("%02d:%02d:%02d",//.textProperty()
						TimeUnit.MILLISECONDS.toHours((long) newValue.toMillis()),
						TimeUnit.MILLISECONDS.toMinutes((long) newValue.toMillis())
								-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours((long)newValue.toMillis())),
						TimeUnit.MILLISECONDS.toSeconds((long) newValue.toMillis())
							-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) newValue.toMillis()))
						));
				
			}
		});
		this.titleProperty.addListener(new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				stage.setTitle(newValue);
			}			
		});
	
		
		
	}
	/*
	 * Decoration of Stage
	 */
		private void decorateStage(){
		//	this.stage.initStyle(StageStyle.UNDECORATED);
			root = new VBox();
				root.setPadding(new Insets(0,5,0,0));
				root.setStyle("-fx-background-color:black;"
							 +"-fx-border-width:2;"
							 + "-fx-border:solid inside;"
							 +"-fx-border-color:blue;");
				
			scene = new Scene(root,500,500);
			scene.setFill(Color.WHITE);
			setBorderPane();
				
				root.minWidthProperty().bind(this.scene.widthProperty());
				this.stage.setMinWidth(600);
				this.stage.setMinHeight(600);
			this.stage.setScene(scene);
		}
		private void setBorderPane(){
			bpane = new BorderPane();
				bpane.minWidthProperty().bind(root.widthProperty());
				setCenterBorderPane();
				setTopBorderPane();
				setRightBorderPane();
				setBottomBorderPane();
				setLeftBorderPane();
				
			root.getChildren().add(bpane);
		}
		private void setTopBorderPane(){
			hbox1 = new HBox();
				hbox1.minWidthProperty().bind(bpane.widthProperty()/*.multiply(.99)*/);
				addHandllersforMovingStage();
				//hbox1.setPadding(new Insets(1,-1,1,1));
				//hbox1.setStyle("-fx-border-insets:5;");
				hbox2 = new HBox();
					hbox2.minWidthProperty().bind(hbox1.minWidthProperty().multiply(0.5));
					hbox2.setStyle("-fx-background-color:gray;");
					hbox2.setSpacing(2);
					//hbox2.setPadding(new Insets(2));
					this.menubar = new MenuBar();
						this.menu = new Menu("",new ImageView(new Image(this.getClass().getClassLoader().getResource("setting_not_active.jpg").toString())));
						this.menu.setStyle("-fx-background-color:gray");
							this.newInstance = new MenuItem("_New Instance");
								KeyCodeCombination kcc = new KeyCodeCombination(KeyCode.N,KeyCombination.CONTROL_DOWN);
								this.newInstance.setAccelerator(kcc);
								this.newInstance.setOnAction(e->{
									
									Stage s = new Stage();
									Screen screen = Screen.getPrimary();
									s.initModality(Modality.NONE);
									Rectangle2D r2d = screen.getVisualBounds();
									s.setX((this.stage.getX()+50)>r2d.getMaxX()?100:(this.stage.getX()+50));
									s.setY((this.stage.getY()+50)>r2d.getMaxY()?100:(this.stage.getY()+50));
									try {
										new NewInstance(s);
									} catch (Exception e2) {
										// TODO Auto-generated catch block
										e2.printStackTrace();
									}
									
									
								});
							this.menu.getItems().addAll(this.newInstance);
						this.menubar.getMenus().add(menu);
					hbox2.getChildren().add(this.menubar);
				hbox3 = new HBox();
					hbox3.setAlignment(Pos.CENTER_RIGHT);
					hbox3.setStyle("-fx-background-color:gray;");
					hbox3.setSpacing(2);
					hbox3.minWidthProperty().bind(hbox1.minWidthProperty().multiply(0.5));
					
					this.slider = new Slider(0.2,1,1);
					this.slider.valueProperty().addListener(
							(ObservableValue<? extends Number> prop, Number oldVal, Number newVal)->{
								this.stage.setOpacity(newVal.doubleValue());
						
					});
					this.slider.setStyle("-fx-background-color: gray;");
					
					this.slider.setMaxWidth(5);
					//this.slider.setMaxHeight(0);
					
					this.minimizeBtn = new ImageView(new Image(this.getClass().getClassLoader().getResource("minimize_not_active.jpg").toString()));
					
					this.minimizeBtn.setOnMouseEntered(e->{
						this.minimizeBtn.setImage(new Image(this.getClass().getClassLoader().getResource("minimize_active.jpg").toString()));
					});
					this.minimizeBtn.setOnMouseExited(e->{
						this.minimizeBtn.setImage(new Image(this.getClass().getClassLoader().getResource("minimize_not_active.jpg").toString()));
					});
					this.minimizeBtn.setOnMouseClicked(e->{
						
						this.stage.setIconified(true);
					});
					
					this.fullScreenBtn = new ImageView(new Image(this.getClass().getClassLoader().getResource("fullscreen_not_active.jpg").toString()));
					this.fullScreenBtn.setOnMouseEntered(e->{
						this.fullScreenBtn.setImage(new Image(this.getClass().getClassLoader().getResource("fullscreen_active.jpg").toString()));
					});
					this.fullScreenBtn.setOnMouseExited(e->{
						this.fullScreenBtn.setImage(new Image(this.getClass().getClassLoader().getResource("fullscreen_not_active.jpg").toString()));
					});
					this.fullScreenBtn.setOnMouseClicked(e->{
						this.fullScreen.set(!this.fullScreen.get());
						//this.stage.setMaximized(this.fullScreen);
						this.stage.setFullScreen(this.fullScreen.get());
					});
					
					//adding key pressed event for escape key 
					
					this.scene.setOnKeyPressed(e->{
						System.out.println(e.getCode());
						if(e.getCode() == KeyCode.ESCAPE){
							this.fullScreen.set(!this.fullScreen.get());
						}
						
						else if(e.isControlDown()){
							if(e.getCode()==KeyCode.R){
								reloadMedia();
							}
							else if(e.getCode() == KeyCode.T){
								this.stage.setAlwaysOnTop(true);
							}
							else if(e.getCode() == KeyCode.B){
								this.stage.setAlwaysOnTop(false);
							}
							else if(e.getCode() == KeyCode.F){
								if(this.rateProperty.get()<7.9){
									this.rateProperty.set(this.rateProperty.get()+.1);
								}
							}
							else if(e.getCode() == KeyCode.S){
								if(this.rateProperty.get()>0.1){
									this.rateProperty.set(this.rateProperty.get()-.1);
								}
							}
							else if((e.getCode() == KeyCode.ADD) || (e.getCode()== KeyCode.EQUALS)){
								System.out.println("Generating ctrl+ADD");
								if(this.volumeProperty.get() <= .96){
									System.out.println("inside changer");
									this.volumeProperty.set(this.volumeProperty.get()+0.05);
								}
							}
							else if((e.getCode() == KeyCode.SUBTRACT) || (e.getCode() == KeyCode.MINUS)){
								if(this.volumeProperty.get() >= 0.04){
									this.volumeProperty.set(this.volumeProperty.get()-0.05);
								}
							}
							else if(e.getCode() == KeyCode.Q){
								if(this.balanceProperty.get() >= -.9){
									this.balanceProperty.set(this.balanceProperty.get()-0.1);
								}
							}
							else if(e.getCode() == KeyCode.W){
								if(this.balanceProperty.get() <= .9){
									this.balanceProperty.set(this.balanceProperty.get()+0.1);
								}
							}
							
						}
					});
					this.maximizeBtn = new ImageView(new Image(this.getClass().getClassLoader().getResource("restore_not_active.jpg").toString()));
					this.maximizeBtn.setOnMouseEntered(e->{
						if(this.maximized){
							this.maximizeBtn.setImage(new Image(this.getClass().getClassLoader().getResource("maximize_active.jpg").toString()));
						}else{
							this.maximizeBtn.setImage(new Image(this.getClass().getClassLoader().getResource("restore_active.jpg").toString()));
						}
						});
					this.maximizeBtn.setOnMouseExited(e->{
						if(this.maximized){
							this.maximizeBtn.setImage(new Image(this.getClass().getClassLoader().getResource("maximize_not_active.jpg").toString()));
						}else{
							this.maximizeBtn.setImage(new Image(this.getClass().getClassLoader().getResource("restore_not_active.jpg").toString()));
						}
						});
					this.maximizeBtn.setOnMouseClicked(e->{
						this.maximized = !this.maximized;
						this.stage.setMaximized(maximized);
					});
					
					this.closeBtn = new ImageView(new Image(this.getClass().getClassLoader().getResource("close_not_active.jpg").toString()));
					this.closeBtn.setOnMouseEntered(e->{
						this.closeBtn.setImage(new Image(this.getClass().getClassLoader().getResource("close_active.jpg").toString()));
					});
					this.closeBtn.setOnMouseExited(e->{
						this.closeBtn.setImage(new Image(this.getClass().getClassLoader().getResource("close_not_active.jpg").toString()));
					});
					this.closeBtn.setOnMouseClicked(e->{
						this.stage.close();
					});
					
					hbox3.getChildren().addAll(this.slider,this.fullScreenBtn,this.minimizeBtn,this.maximizeBtn,this.closeBtn);
				hbox1.getChildren().addAll(hbox2,hbox3);
			bpane.setTop(hbox1);
		}
		private void setRightBorderPane(){
			bpane.setRight(null);
		}
		private void setCenterBorderPane(){
			vbox2 = new /*VBox()*/ GridPane();;
				vbox2.alignmentProperty().set(Pos.CENTER);
				Rectangle2D r2d = Screen.getPrimary().getVisualBounds();
				DoubleProperty fractionW = new SimpleDoubleProperty();
				fractionW.bind(this.stage.heightProperty().divide(r2d.getHeight()*8.3));
				vbox2.setStyle("-fx-background-color:#13213D;"
						+ "-fx-border-insets:3;"
						+ "-fx-border-style:solid inside;"
						+ "-fx-border-color:blue;"
						);
				vbox2.minWidthProperty().bind(scene.widthProperty().multiply(.95));
				
				this.mediaView = new MediaView();
				this.mediaView.setOnError((MediaErrorEvent)->{
					System.out.println("MediaViewError");
				});
				this.mediaView.setOnMouseClicked(new EventHandler<MouseEvent>(){

					@Override
					public void handle(MouseEvent e) {
						// TODO Auto-generated method stub
						if(e.getClickCount() == 2){
							playPauseProperty.set(!playPauseProperty.get());
						}
					}
					
				});
				vbox2.minHeightProperty().bind(scene.heightProperty().multiply(fractionW.add(.76)));
				vbox2.getChildren().add(mediaView);
			bpane.setCenter(vbox2);
		}
		private void setBottomBorderPane(){
	
			Rectangle2D r2d = Screen.getPrimary().getVisualBounds();
			DoubleProperty fractionW = new SimpleDoubleProperty();
			fractionW.bind(this.stage.widthProperty().divide(r2d.getWidth()*8));
			hbox4 = new HBox();
			//Button btn = new Button("Help");
				hbox4.minWidthProperty().bind(bpane.widthProperty());
				//hbox4.setStyle("-fx-background-color:#3B3C36;");
				vbox1 = new VBox();
					vbox1.minWidthProperty().bind(hbox4.widthProperty());
					vbox1.setSpacing(2);
						hbox5 = new HBox();
							hbox5.minWidthProperty().bind(vbox1.widthProperty());
							hbox5.setSpacing(2);
								
							hbox7 = new HBox();
								hbox7.setStyle("-fx-background-color:#3B3C36;");
								hbox7.setSpacing(3);
								
								
								hbox7.minWidthProperty().bind(hbox5.widthProperty().multiply(fractionW.add(.7)));
								hbox7.setPrefHeight(10);
								this.currentTimeLbl = new Label(this.currentTimeLblProperty.get());
								this.currentTimeLbl.setTextFill(Color.web("#C46210"));
								this.currentTimeLbl.minWidthProperty().bind(hbox7.widthProperty().multiply(new SimpleDoubleProperty(.15).subtract(fractionW)));
							
								this.slider2 = new Slider();
								this.slider2.minWidthProperty().bind(hbox7.widthProperty().multiply(fractionW.multiply(1.5).add(.69)));
								this.slider2.setStyle("-fx-track-color:#C46210;");
								
								this.totalTimeLbl = new Label(this.totalTimeLblProperty.get());
								this.totalTimeLbl.minWidthProperty().bind(hbox7.widthProperty().multiply(new SimpleDoubleProperty(.15).subtract(fractionW)));
								this.totalTimeLbl.setTextFill(Color.web("#C46210"));
							hbox7.getChildren().addAll(this.currentTimeLbl,this.slider2,this.totalTimeLbl);
							hbox8 = new HBox();
								hbox8.setStyle("-fx-background-color:#3B3C36;");
								hbox8.setSpacing(10);
								hbox8.setPadding(new Insets(3));
								hbox8.setAlignment(Pos.CENTER_LEFT);
								hbox8.minWidthProperty().bind(hbox5.widthProperty().multiply(new SimpleDoubleProperty(.3).subtract(fractionW)));
								this.volumeImageView = new ImageView(new Image(this.getClass().getClassLoader().getResource("volume_active.jpg").toString()));
								this.volumeImageView.setOnMouseClicked(e->{
									
									this.muteProperty.set(!this.muteProperty.get());
									System.out.println("muteProperty for backend : "+this.muteProperty.get());
									this.volumeMuteProperty.set(!this.volumeMuteProperty.get());
									if(this.volumeImageViewProperty.get() == "volume_active"){
										this.volumeImageViewProperty.set("volume_not_active");
										 this.volumeImageView.setImage((new Image(this.getClass().getClassLoader().getResource(this.volumeImageViewProperty.get()+".jpg").toString())));
										 
									}
									else{
										this.volumeImageViewProperty.set("volume_active");
										 this.volumeImageView.setImage((new Image(this.getClass().getClassLoader().getResource(this.volumeImageViewProperty.get()+".jpg").toString())));
									}
								});
						//TESTING CODE
								this.volumeMuteProperty.addListener(new ChangeListener<Boolean>(){
									@Override
									public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
											Boolean newValue) {
										// TODO Auto-generated method stub
										System.out.println("muteProperty : "+newValue);
									}
									
								});
								this.volumeSlider = new Slider(0,1,0.5);
								this.volumeSlider.valueProperty().bindBidirectional(volumeProperty);
								//this.volumeSlider.setShowTickLabels(true);
								//this.volumeSlider.setShowTickMarks(true);
							//	this.volumeSlider.showTickMarksProperty();
							//	this.volumeSlider.majorTickUnitProperty().set(0.05);
								this.volumeSlider.setMajorTickUnit(4);
								this.volumeSlider.setMinorTickCount(4);
								this.volumeSlider.majorTickUnitProperty().set(0.25);
								this.volumeSlider.snapToTicksProperty();
								this.volumeSlider.minWidthProperty().bind(hbox8.widthProperty().multiply(.70));
								//this.volumeSlider.setPrefWidth(hbox5.widthProperty().multiply(new SimpleDoubleProperty(.3).subtract(fractionW)).get());
								hbox8.getChildren().addAll(this.volumeImageView,this.volumeSlider);
							hbox5.getChildren().addAll(hbox7,hbox8);
						hbox6 = new HBox();
							hbox6.minWidthProperty().bind(root.widthProperty().multiply(.7));
							hbox6.setSpacing(2);
							
							hbox9 = new HBox();
								hbox9.setStyle("-fx-background-color:#3B3C36;");
								hbox9.setPadding(new Insets(2));
								hbox9.setSpacing(3);
								hbox9.minWidthProperty().bind(hbox6.widthProperty().multiply(fractionW.add(.7)));
								this.playPauseImgView = new ImageView(new Image(this.getClass().getClassLoader().getResource("play.jpg").toString()));
								this.playPauseImgView.setOnMouseClicked(e->{
									//this.playPauseProperty.set("pause");
									if(this.playPauseProperty.get()/*"play"*/){
										this.playPauseProperty.set(/*"pause"*/false);
										this.playPauseImgView.setImage(new Image(this.getClass().getClassLoader().getResource(/*this.playPauseProperty.get()*/"pause.jpg").toString()));
									}
									else{
										this.playPauseProperty.set(/*"play"*/true);
										this.playPauseImgView.setImage(new Image(this.getClass().getClassLoader().getResource(/*this.playPauseProperty.get()*/"play.jpg").toString()));
									}
								});
								this.playPauseProperty.addListener(new ChangeListener<Boolean>(){

									@Override
									public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
											Boolean newValue) {
										// TODO Auto-generated method stub
										System.out.println("OldValue : "+oldValue+"New Value : "+newValue);
										if(newValue./*equals("play")*/booleanValue()){
											if(mediaView.getMediaPlayer() != null){
												mediaView.getMediaPlayer().play();
											}
											playPauseImgView.setImage(new Image(this.getClass().getClassLoader().getResource(/*this.playPauseProperty.get()*/"play.jpg").toString()));
										}
										else /*if(newValue.equals("pause"))*/{
											if(mediaView.getMediaPlayer() != null){
												mediaView.getMediaPlayer().pause();
											}
											playPauseImgView.setImage(new Image(this.getClass().getClassLoader().getResource(/*this.playPauseProperty.get()*/"pause.jpg").toString()));

											
										}
									}
									
								});
								this.stopImageView = new ImageView(new Image(this.getClass().getClassLoader().getResource("stop_not_active.jpg").toString()));
								this.stopImageView.setOnMouseEntered(e->{
									this.stopProperty.set("stop_active");
									this.stopImageView.setImage(new Image(this.getClass().getClassLoader().getResource(this.stopProperty.get()+".jpg").toString()));
								});
								this.stopImageView.setOnMouseExited(e->{
									this.stopProperty.set("stop_not_active");
									this.stopImageView.setImage(new Image(this.getClass().getClassLoader().getResource(this.stopProperty.get()+".jpg").toString()));
								});
								
								this.stopImageView.setOnMouseClicked(e->{
									if(this.mediaView.getMediaPlayer() != null){
										this.mediaView.getMediaPlayer().stop();
									}
									//And
									this.playPauseProperty.set(false);
								});
								this.stopProperty.addListener(new ChangeListener<String>(){
									@Override
									public void changed(ObservableValue<? extends String> observable, String oldValue,String newValue) {
											System.out.println("OldValue : "+oldValue+"New Value : "+newValue);
									}
								});
								
								this.bckwdImgView = new ImageView(new Image(this.getClass().getClassLoader().getResource("b_not_active.jpg").toString()));
								this.bckwdImgView.setOnMousePressed(e->{
									if(this.bckwdProperty.get() == "b_not_active"){
										this.bckwdProperty.set("b_active");
										this.bckwdImgView.setImage(new Image(this.getClass().getClassLoader().getResource(this.bckwdProperty.get()+".jpg").toString()));
									}
									else{
										this.bckwdProperty.set("b_not_active");
										this.bckwdImgView.setImage(new Image(this.getClass().getClassLoader().getResource(this.bckwdProperty.get()+".jpg").toString()));

									}
								});
								
								this.bckwdImgView.setOnMouseReleased(e->{
									if(this.bckwdProperty.get() == "b_not_active"){
									}
									else{
										this.bckwdProperty.set("b_not_active");
										this.bckwdImgView.setImage(new Image(this.getClass().getClassLoader().getResource(this.bckwdProperty.get()+".jpg").toString()));
									}
								});
								this.bckwdProperty.addListener(new ChangeListener<String>(){

									@Override
									public void changed(ObservableValue<? extends String> observable, String oldValue,
											String newValue) {
										// TODO Auto-generated method stub
										System.out.println("Old : "+oldValue+" New : "+newValue);
									}	
								});
								this.bckwdImgView.setOnMouseClicked(e->{
									if(this.mediaView.getMediaPlayer() != null){
										this.mediaView.getMediaPlayer().seek(Duration.seconds((this.slider2.getValue()<15)?0:(this.slider2.getValue()-15)));
									}
								});
								this.fwdImgView = new ImageView(new Image(this.getClass().getClassLoader().getResource("f_not_active.jpg").toString()));
								this.fwdImgView.setOnMousePressed(e->{
									//this.gdTxtFldProperty.set("Changed");
									if(this.fwdProperty.get() == "f_not_active"){
										this.fwdProperty.set("f_active");
										this.fwdImgView.setImage(new Image(this.getClass().getClassLoader().getResource(this.fwdProperty.get()+".jpg").toString()));
									}
									else{
										this.fwdProperty.set("f_not_active");
										this.fwdImgView.setImage(new Image(this.getClass().getClassLoader().getResource(this.fwdProperty.get()+".jpg").toString()));

									}
								});
								this.fwdImgView.setOnMouseReleased(e->{
									if(this.fwdProperty.get() == "f_not_active"){
										//this.fwdProperty.set("f_active");
										//this.fwdImgView.setImage(new Image(this.getClass().getClassLoader().getResource(this.fwdProperty.get()+".jpg").toString()));
									}
									else{
										this.fwdProperty.set("f_not_active");
										this.fwdImgView.setImage(new Image(this.getClass().getClassLoader().getResource(this.fwdProperty.get()+".jpg").toString()));
									}
								});
								this.fwdImgView.setOnMouseClicked(e->{
									if(this.mediaView.getMediaPlayer() != null){
										System.out.println("Max Value : " +this.slider2.getMax()+" curr Value : "+this.slider2.getValue());
										//this.mediaView.getMediaPlayer().seek(Duration.seconds((this.slider2.getValue()<=(this.slider2.getMax()-15000))?((this.slider2.getValue()+15000)/1000):this.slider2.getMax()/1000));
										System.out.println(Duration.seconds((this.slider2.getValue()<=(this.slider2.getMax()-15))?((this.slider2.getValue()+15)):this.slider2.getMax()));
										this.mediaView.getMediaPlayer().seek(Duration.seconds((this.slider2.getValue()<=(this.slider2.getMax()-15))?((this.slider2.getValue()+15)):this.slider2.getMax()));
									}
								});
								this.generalDisplayTxtFld = new TextField(this.gdTxtFldProperty.get());
								this.generalDisplayTxtFld.setAlignment(Pos.CENTER);
								this.generalDisplayTxtFld.textProperty().bind(this.gdTxtFldProperty);
								this.generalDisplayTxtFld.setStyle("-fx-background-color:black;"
																 + "-fx-text-fill:#C46210;"
																 + "");
								
								this.generalDisplayTxtFld.minWidthProperty().bind(hbox9.widthProperty().multiply(fractionW.multiply(2).add(.64)));
								hbox9.getChildren().addAll(this.playPauseImgView,this.stopImageView,this.bckwdImgView,this.fwdImgView,this.generalDisplayTxtFld);
								
							hbox10 = new HBox();
								hbox10.setStyle("-fx-background-color:#3B3C36;");
								hbox10.setPadding(new Insets(2));
								hbox10.setSpacing(3);
								hbox10.minWidthProperty().bind(hbox6.widthProperty().multiply(new SimpleDoubleProperty(.3).subtract(fractionW)));
							
								this.screenshotImageView = new ImageView(new Image(this.getClass().getClassLoader().getResource("screenshot_not_active.jpg").toString()));
								this.screenshotImageView.setOnMouseEntered(e->{
									this.screenshotImageView.setImage(new Image(this.getClass().getClassLoader().getResource("screenshot_active.jpg").toString()));
								});
								this.screenshotImageView.setOnMouseExited(e->{
									this.screenshotImageView.setImage(new Image(this.getClass().getClassLoader().getResource("screenshot_not_active.jpg").toString()));
								});
								this.screenshotImageView.setOnMouseClicked(e->{
									WritableImage wimg = this.mediaView.snapshot(null,null);
									Stage ssdStage = new Stage();
									ssdStage.setWidth(wimg.getWidth());
									ssdStage.setHeight(wimg.getHeight());
									ssdStage.setResizable(false);
									ssdStage.setScene(new Scene(new VBox(new ImageView(wimg))));
							
									ssdStage.show();
									BufferedImage bimg = SwingFXUtils.fromFXImage(wimg, null);
									try {
										ImageIO.write(bimg, "png", new File(this.dsObj.getPathRoot()+"/ScreenShots/"+new Date().toString().replaceAll(":","_")+".jpg"));
									} catch (Exception e1) {
										// TODO Auto-generated catch block
										System.out.println("Error while Saving Screenshot");
										e1.printStackTrace();
									}
									this.gdTxtFldProperty.set("Screen Shot Saved at : "+this.dsObj.getPathRoot()+"/ScreenShots/"+new Date().toString().replaceAll(":","_")+".jpg");
								});
								
								
								//adding Tooltip to screenshot imageView
								Label l = new Label();
								l.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
								l.setGraphic(this.screenshotImageView);
								l.setTooltip(new Tooltip("Save the Current Frame"));
								
								this.openDirectoryImageView = new ImageView(new Image(this.getClass().getClassLoader().getResource("fileopen_not_active.jpg").toString()));
								this.openDirectoryImageView.setOnMouseEntered(e->{
									this.openDirectoryImageView.setImage(new Image(this.getClass().getClassLoader().getResource("fileopen_active.jpg").toString()));
								});
								this.openDirectoryImageView.setOnMouseExited(e->{
									this.openDirectoryImageView.setImage(new Image(this.getClass().getClassLoader().getResource("fileopen_not_active.jpg").toString()));
								});
								
								this.openDirectoryImageView.setOnMouseClicked(e->{
									loadSomeMedia();   //For Loading Some Media By openFor Selection
								});
								
								
								this.openPlayListImageView = new ImageView(new Image(this.getClass().getClassLoader().getResource("playlist_not_active.jpg").toString()));
								this.openPlayListImageView.setOnMouseEntered(e->{
									this.openPlayListImageView.setImage(new Image(this.getClass().getClassLoader().getResource("playlist_active.jpg").toString()));
								});
								this.openPlayListImageView.setOnMouseExited(e->{
									this.openPlayListImageView.setImage(new Image(this.getClass().getClassLoader().getResource("playlist_not_active.jpg").toString()));
								});
								this.openPlayListImageView.setOnMouseClicked(e->{
									
								Stage playListStage = new Stage();
									
									VBox rootVbox = new VBox();
										HBox hbox = new HBox();
											hbox.setPadding(new Insets(3));
											hbox.setAlignment(Pos.CENTER_RIGHT);
											hbox.setSpacing(10);
										ScrollPane scrollPane = new ScrollPane();
											scrollPane.setMinHeight(400);
											scrollPane.setMinWidth(400);
											scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
											scrollPane.setHbarPolicy(ScrollBarPolicy.ALWAYS);
											VBox vbox = new VBox();
												vbox.setSpacing(5);
												vbox.setPadding(new Insets(5));
												//vbox.setMinHeight(400);
												//vbox.setMinWidth(400);
												vbox.minWidthProperty().bind(scrollPane.minWidthProperty().subtract(7));
												vbox.minHeightProperty().bind(scrollPane.minHeightProperty().subtract(7));
												vbox.setStyle("-fx-background-color:#13213D;"
														+ "-fx-border-insets:3;"
														+ "-fx-border-style:solid inside;"
														+ "-fx-border-color:blue;"
														);
												scrollPane.setContent(vbox);
											ImageView playlistReloadImgView = new ImageView(new Image(this.getClass().getClassLoader().getResource("reload_not_active.jpg").toString()));
											playlistReloadImgView.setOnMouseClicked(event->{
												System.out.println("Reloding Current PlayList");
												vbox.getChildren().remove(0,vbox.getChildren().size());
												decoratePlayListStage(vbox);
											});
											
											playlistReloadImgView.setOnMouseEntered(event->{
												playlistReloadImgView.setImage(new Image(this.getClass().getClassLoader().getResource("reload_active.jpg").toString()));
											});
											
											playlistReloadImgView.setOnMouseExited(event->{
												playlistReloadImgView.setImage(new Image(this.getClass().getClassLoader().getResource("reload_not_active.jpg").toString()));
											});
											
											ImageView createNewPlayList = new ImageView(new Image(this.getClass().getClassLoader().getResource("addPlayList_not_active.jpg").toString()));
											createNewPlayList.setOnMouseClicked(event->{
												System.out.println("PlayCreated");
											});
											createNewPlayList.setOnMouseEntered(event->{
												createNewPlayList.setImage(new Image(this.getClass().getClassLoader().getResource("addPlayList_active.jpg").toString()));
											});
											createNewPlayList.setOnMouseExited(event->{
												createNewPlayList.setImage(new Image(this.getClass().getClassLoader().getResource("addPlayList_not_active.jpg").toString()));
											});
											hbox.getChildren().addAll(playlistReloadImgView,createNewPlayList);
											rootVbox.getChildren().addAll(hbox,scrollPane);
										
									
									Scene pScene = new Scene(rootVbox);
									rootVbox.minWidthProperty().bind(pScene.widthProperty());
									rootVbox.setMaxWidth(600);
									rootVbox.setMaxHeight(600);
									decoratePlayListStage(vbox);			// For updating playlist stage as well
									
									playListStage.setScene(pScene);
									
									playListStage.sizeToScene();
									playListStage.initOwner(this.stage);
									playListStage.setResizable(false);
									playListStage.showAndWait();
									

								});
								hbox10.getChildren().addAll(/*l,*/this.screenshotImageView,this.openDirectoryImageView,this.openPlayListImageView);
								hbox6.getChildren().addAll(hbox9,hbox10);
						vbox1.getChildren().addAll(hbox5,hbox6);
				hbox4.getChildren().addAll(vbox1);
			bpane.setBottom(hbox4);
			
		}
		private void setLeftBorderPane(){
			bpane.setLeft(null);
		}
		
		private void addHandllersforMovingStage(){
			hbox1.setOnMousePressed(e->{
				this.startX = e.getScreenX();
				this.startY = e.getScreenY();
				System.out.println("startX : "+this.startX+" startY : "+this.startY);
			});
			
			hbox1.setOnMouseDragged(e->{
				this.draggOffsetX = this.startX-e.getScreenX();
				this.draggOffsetY = this.startY-e.getScreenY();
				System.out.println("draggOffsetX : "+this.draggOffsetX+" draggOffsetY : "+this.draggOffsetY);
				
				this.startX = e.getScreenX();
				this.startY = e.getScreenY();
				
				double currX = this.stage.getX();
				double currY = this.stage.getY();
				
				this.stage.setX(currX-this.draggOffsetX);
				this.stage.setY(currY-this.draggOffsetY);
			});
		}
	
	private void decoratePlayListStage(VBox vbox){
		Iterator<File> iterator = this.mediaPlayListFile.iterator();
		while(iterator.hasNext()){
			Label label = new Label(iterator.next().toString()/*.getName()*/);
			label.setFocusTraversable(true);
			label.setTextFill(Color.web("F7FF00"));
			label.setWrapText(true);
			label.setOnMouseClicked(new EventHandler<MouseEvent>(){

				@Override
				public void handle(MouseEvent event) {
					playMedia(new File(label.getText()));
					
				}
			});
			vbox.getChildren().addAll(label);
		}
	}
	private void loadSomeMedia(){
		FileChooser fc = new FileChooser();
		fc.setTitle("Choose Some Media");
		fc.setInitialDirectory(new File(this.dsObj.getPathForSelection()));
		fc.getExtensionFilters().addAll(new ExtensionFilter("AIFF,FXM,FLV,HLS,MP3,MP4,WAV","*.aiff","*.aif","*.fxm","*.flv","*.m3u8","*.mp3","*.mp4","*.m4a","*.m4v","*.wav"));
		List<File> files = fc.showOpenMultipleDialog(this.stage);
		
		if(files != null){
			//Test
			if(files.size()>1){
				this.BulkAdditionOfFile = true;
				this.mediaPlayListFile.addAll(files.subList(1, files.size()-1));
				this.BulkAdditionOfFile = false;
				this.mediaPlayListFile.add(files.get(0));
			}
			else{
				this.mediaPlayListFile.addAll(files);
			}
			
			
			//this.mediaPlayListFile.addAll(files);
			System.out.println("Some Files added to list");
		}
		else{
			System.out.println("Selected Null");
		}
	}
	private void createMediaListFile(){
		this.mediaPlayListFile = FXCollections.observableSet();
		this.mediaPlayListFile.addListener(new SetChangeListener<File>(){
			@Override
			public void onChanged(javafx.collections.SetChangeListener.Change<? extends File> change) {
					System.out.println("Inside onChange Method");
				
					if (change.wasAdded()) {
						System.out.println("From onChanged Method : "+change.getElementAdded().getName());
						
						if(mediaPlayListFile.size()>1){
							if(mediaView.getMediaPlayer() != null){
								mediaView.getMediaPlayer().dispose();
								System.out.println("Disposing MediaPlayer");
							}
							
							
							else{
								System.out.println("Null");
							}
						}
						
						System.out.println("From onChanged Method Calling For new Media to load");
						
						if(BulkAdditionOfFile == true){
							//Do Nothing
							System.out.println(" Bullk Addition was true");
						}
						else{
							System.out.println(" Bullk Addition was false");
							playMedia(change.getElementAdded());
						}
						
							
					}
					if (change.wasRemoved()) {
						if (change.getSet().size() > 0) {
							//TODO - write logic for loading first media from this.mediaPlayListFile
						}
					} 
					
				}
			
		});
	}
	private void addDnDHandlerToScene(){
		this.scene.setOnDragOver((DragEvent e)->{
			Dragboard db = e.getDragboard();
			if(db.hasFiles()){
				List<File> files = db.getFiles();
				for(File file : files){
					if(file.isFile()){
						String mimeType;
						try {
							mimeType = Files.probeContentType(file.toPath());
							System.out.println("MimeType : "+mimeType );
							if(mimeType != null &&(mimeType.startsWith("audio/")||
												   mimeType.startsWith("video/")||
												   mimeType.startsWith("application/"))){
								e.acceptTransferModes(TransferMode.ANY);
							}
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}	
				}
			}
			e.consume();
		});
		this.scene.setOnDragDropped((DragEvent e)->{
			
			Dragboard db = e.getDragboard();
			if(db.hasFiles()){
				List<File> files = db.getFiles();
				List<File> finalFile = new ArrayList<File>();
				for(File file : files){
					if(file.isFile()){
						//finalFile.add(file);
						String mimeType;
						try {
							mimeType = Files.probeContentType(file.toPath());
							if(mimeType != null &&(mimeType.startsWith("audio/")||
												   mimeType.startsWith("video/")||
												   mimeType.startsWith("application/"))){
								String extension = file.getName().substring(file.getName().lastIndexOf("."));
								System.out.println("Extension : " +extension);
								if(extension != null &&
											   (extension.equals(".aiff")||
												extension.equals(".aif")||
												extension.equals(".fxm")||
												extension.equals(".flv")||
												extension.equals(".m3u8")||
												extension.equals(".mp3")||
												extension.equals(".mp4")||
												extension.equals(".m4a")||
												extension.equals(".m4v")||
												extension.equals(".wav"))){
									
									finalFile.add(file);
									System.out.println("Dropp Successfull");
									
								}else{
									if(extension != null)
										this.gdTxtFldProperty.set(extension+" Format Do not Supported.");
								}
										
								
							}
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}	
				}
				
				if(finalFile.size()>0){
					if(finalFile.size()>1){
						this.BulkAdditionOfFile = true;
						this.mediaPlayListFile.addAll(finalFile.subList(1, finalFile.size()-1));
						this.BulkAdditionOfFile = false;
						this.mediaPlayListFile.add(finalFile.get(0));
					}
					else{
						this.mediaPlayListFile.add(finalFile.get(0));
					}
					
				}
				else{
					System.out.println("final File is Null");
				}
			}
			
			e.setDropCompleted(true);
			e.consume();
		});
	}
	
	private void playMedia(File file){
		//this.mediaView = new MediaView();
		try{
			System.out.println("Loading Media File : "+file.getName());
			Media media = new Media(file.toURI().toString());
			System.out.println("Loading Completed");
			media.setOnError(()->{
				System.out.println("Media Error");
			});
			
			
		
			try{
				this.mediaView.getMediaPlayer().dispose();
				this.mediaView.setMediaPlayer(null);
			}
			catch(Exception e){
				System.out.println("hurray1 Exception");
				e.printStackTrace();
				//playMedia(file);
			}
		
			System.out.println("after Testing");
			
			MediaPlayer mediaPlayer = new MediaPlayer(media);
			
			System.out.println("after testing2");
			mediaPlayer.setOnError(()->{
				System.out.println("Media Player Error");
			});
			mediaPlayer.setOnReady(()->{
				this.mediaView.setMediaPlayer(mediaPlayer);
				mediaPlayer.setAutoPlay(true);
				System.out.println("After AutoPlay");
				Rectangle2D r2d = Screen.getPrimary().getVisualBounds();
				if(media.getWidth()>r2d.getWidth()-50){
					//this.mediaView;
				}
				this.mediaView.setSmooth(true);
				this.mediaView.fitHeightProperty().bind(this.scene.heightProperty().multiply(.86));
				this.mediaView.fitWidthProperty().bind(this.scene.widthProperty().multiply(.99));
				
				
				//Binding of Property starts
				mediaPlayer.rateProperty().bindBidirectional(rateProperty);
				mediaPlayer.volumeProperty().bindBidirectional(volumeProperty);
				mediaPlayer.muteProperty().bind(muteProperty);
				mediaPlayer.balanceProperty().bindBidirectional(balanceProperty);
			
				this.currentTimeProperty.set(mediaPlayer.currentTimeProperty().get());
				mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>(){
					@Override
					public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
						// TODO Auto-generated method stub
						currentTimeProperty.set(newValue);
					}
				});
				
				this.totalTimeProperty.set(mediaPlayer.totalDurationProperty().get());
				this.slider2.setMin(0);
				this.slider2.setOnMouseClicked(e->{
					System.out.println("Slider value : "+slider2.getValue());
				});
				
				this.slider2.setMax(mediaPlayer.getTotalDuration().toSeconds());
				mediaPlayer.totalDurationProperty().addListener(new ChangeListener<Duration>(){
					@Override
					public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
						// TODO Auto-generated method stub
						totalTimeProperty.set(newValue);
					}
				});
					
				this.playPauseProperty.set(true);
				int lastIndexOf = file.toString().lastIndexOf("\\\\");
				this.titleProperty.set(file.getName());
				System.out.println("Title : "+file.getName());
				//this.mediaView.setFitWidth(media.getWidth());
				//this.mediaView.setFitHeight(media.getHeight());
		 
				
			});
			mediaPlayer.setOnPlaying(()->{
				System.out.println("setOnPlaying");
			});
			mediaPlayer.setOnRepeat(()->{
				System.out.println("setOnRepeat");
			});
			mediaPlayer.setOnStalled(()->{
				System.out.println("setOnStalled");
			});
			mediaPlayer.setOnPaused(()->{
				System.out.println("setOnPaused");
			});
			mediaPlayer.setOnStopped(()->{
				System.out.println("setOnStopped");
			});
			mediaPlayer.setOnHalted(()->{
				System.out.println("setOnHalted");
			});
			mediaPlayer.setOnEndOfMedia(()->{
			
				reloadMedia();
				System.out.println("reloaded");
			});
		}
		catch (MediaException me){
			System.out.println("MediaException");
			me.printStackTrace();
			
		}
	}
	private void reloadMedia(){
		System.out.println("setOnEndOfMedia : ");
		//mediaPlayer.dispose();
		try{
			this.mediaView.getMediaPlayer().dispose();
			this.mediaView.setMediaPlayer(null);
			//this.mediaView.getMediaPlayer().getClass();
		}
		catch(Exception e){
			System.out.println("hurray1 Exception");
			e.printStackTrace();
			//playMedia(file);
		}
		
		System.out.println("Disposed Successfully");
		
		Object[] tmpFile = this.mediaPlayListFile.toArray();
		if(!this.mediaPlayListFile.isEmpty()){
			System.out.println("length : "+tmpFile.length);
			this.playMedia((File)tmpFile[(int)(Math.random()*tmpFile.length)]);
		}
		else{
			System.out.println("this.mediaPlayListFile is empty");
		}
		

	}
	private void disposePreviousMediaPlayer(){
		if(this.mediaPlayListFile.size()>0){
			//getting reference to mediaPlayer object and checking if not null
			
			if(this.mediaView.getMediaPlayer() != null){
				//starting unbinding the listeners of mediaPlayer
				MediaPlayer mediaPlayer = this.mediaView.getMediaPlayer();
				mediaPlayer.rateProperty().unbindBidirectional(rateProperty);
				
			}
			
		}
	}
/*
 * 
 * Methods for Loading Data Stores if exists else create new Data Store
 */
	public void loadDataStores() throws IOException, ClassNotFoundException{
		File file = new File(dsString);
		// run only first time FileOutputStream fout = new FileOutputStream(file);
		if(file.exists()){
			System.out.println(file.toString()+" Exists");
			FileInputStream fin = new FileInputStream(dsString);
			ObjectInputStream oin = new ObjectInputStream(fin);
			this.dsObj = (DataStores) oin.readObject();
			//System.out.println("Here comes Data From data store :" +this.dsObj.getS());
			System.out.println("Here Comes Data Store Object : \n"+this.dsObj.toString());
			oin.close();
		}
		else{
			System.out.println(file.toString()+" Does Not Exists");
			createDataStores();
			
		}
		
	}	
	public void createDataStores() throws IOException{
		FileOutputStream fout = new FileOutputStream(this.dsString);
		ObjectOutputStream oout = new ObjectOutputStream(fout);
		
		DataStores ds = DataStoreHelper.populateDataStore(this);
		this.dsObj = ds;
		oout.writeObject(ds);
		oout.close();
		System.out.println("Created DataStore");
	}
	

	
/*
 * Method For Creating Directories
 */
	public void createDirectories() throws IOException{
		if(Files.isDirectory(Paths.get(this.dsObj.getPathRoot()))){
			System.out.println("Directory :: "+this.dsObj.getPathRoot()+" already exist.");
			//test for others
			if(Files.isDirectory(Paths.get(this.dsObj.getPathRoot()+"/History"))){
				System.out.println("Directory History :: "+this.dsObj.getPathRoot()+"/History "+" already exist.");
			}else{
				createHistoryDirectory();
				//System.out.println("Created History Directory : "+this.dsObj.getPath()+"/History");
			}
			if(Files.isDirectory(Paths.get(this.dsObj.getPathRoot()+"/PlayList"))){
				System.out.println("Directory PlayList :: "+this.dsObj.getPathRoot()+"/PlayList "+" already exist.");
			}else{
				createPlaylistDirectory();
				//System.out.println("Created PlayList Directory : "+this.dsObj.getPath()+"/PlayList");
			}
			if(Files.isDirectory(Paths.get(this.dsObj.getPathRoot()+"/ScreenShots"))){
				System.out.println("Directory PlayList :: "+this.dsObj.getPathRoot()+"/ScreenShots "+" already exist.");
			}else{
				createScreenshotDirectory();
				//System.out.println("Created PlayList Directory : "+this.dsObj.getPath()+"/PlayList");
			}
		}
		else{
			//create all directory and sub-directories
			createPath();
			createPlaylistDirectory();
			createHistoryDirectory();
			createScreenshotDirectory();
		}
		
	}
	public void createPath() throws IOException{
		Files.createDirectories(Paths.get(this.dsObj.getPathRoot()));
		System.out.println("Created Path Directory : "+this.dsObj.getPathRoot());
	}
	public void createPlaylistDirectory() throws IOException{
		Files.createDirectories(Paths.get(this.dsObj.getPathRoot()+"/PlayList"));
		System.out.println("Created Playlist Directory : "+this.dsObj.getPathRoot()+"/PlayList");
	}
	public void createHistoryDirectory() throws IOException{
		Files.createDirectories(Paths.get(this.dsObj.getPathRoot()+"/History"));
		System.out.println("Created Playlist Directory : "+this.dsObj.getPathRoot()+"/History");
	}
	public void createScreenshotDirectory() throws IOException{
		Files.createDirectories(Paths.get(this.dsObj.getPathRoot()+"/ScreenShots"));
		System.out.println("Created ScreenShot Directory : "+this.dsObj.getPathRoot()+"/ScreenShots");
	}
	public void callFunc(){
		this.stage.setWidth(100);
		this.stage.setHeight(200);
	}
	public void createNewInstance() throws ClassNotFoundException, InterruptedException, IOException{
		Stage newstage = new Stage();
		newstage.initOwner(null);
		new NewInstance(newstage);
	}
	public StringProperty getGDTxtProperty(){
		return this.gdTxtFldProperty;
	}
}
