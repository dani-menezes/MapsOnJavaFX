package map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *  GoogleMap - Inicializa o {@link Stage} com os componentes de iteração com o mapa.
 * @author Daniel Menezes
 */
public class GoogleMap extends Application {
	
	/** Captura os frames ao se digitar na label de Localização. */
    private Timeline inputTextLocation;

    /**
     * Método sobrescrito da Classe {@link Application}.<br>
     * @see Application#start(Stage).
     * {@inheritDoc}.
     */
    @Override 
    public void start(Stage stage) {
    	
        // Instância as classes WebView e WebEngine.
        final WebView webView = new WebView();
        final WebEngine webEngine = webView.getEngine();
        
        // Carrega o HTML onde foi criado o mapa do google
        webEngine.load(this.getClass().getResource("googleMap.html").toString());
        
        // Cria os botôes para alternar as baselayers
        final ToggleGroup baselayerGroup = new ToggleGroup();
        final ToggleButton road = new ToggleButton("Road");
        road.setSelected(true);
        road.setToggleGroup(baselayerGroup);
        final ToggleButton satellite = new ToggleButton("Satellite");
        satellite.setToggleGroup(baselayerGroup);
        final ToggleButton hybrid = new ToggleButton("Hybrid");
        hybrid.setToggleGroup(baselayerGroup);
        final ToggleButton terrain = new ToggleButton("Terrain");
        terrain.setToggleGroup(baselayerGroup);
        baselayerGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle toggle1) {
                if (road.isSelected()) {
                    webEngine.executeScript("document.setMapTypeRoad()");
                } else if (satellite.isSelected()) {
                    webEngine.executeScript("document.setMapTypeSatellite()");
                } else if (hybrid.isSelected()) {
                    webEngine.executeScript("document.setMapTypeHybrid()");
                } else if (terrain.isSelected()) {
                    webEngine.executeScript("document.setMapTypeTerrain()");
                }
            }
        });
        
        // Barra de botões para alternar os baselayers
        HBox buttonBar = new HBox();
        buttonBar.getChildren().addAll(road, satellite, hybrid, terrain);
        
        // Campo para inserção da localização
        final TextField searchField = new TextField();
        searchField.setPromptText("Entre com o endereço");
        searchField.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observableValue, String s, String s1) {
                // delay location updates to we don't go too fast file typing
                if (inputTextLocation != null) {
                	inputTextLocation.stop();
                }
                inputTextLocation = new Timeline();
                inputTextLocation.getKeyFrames().add (
                    new KeyFrame(new Duration(550), new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent actionEvent) {
                            webEngine.executeScript("document.goToLocation(\""+searchField.getText()+"\")");
                        }
                    })
                );
                inputTextLocation.play();
            }
        });
        
        // Botão de ZoomIn, aproximando o mapa
        Button zoomIn = new Button("Zoom +");
        zoomIn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) { webEngine.executeScript("document.zoomIn()"); }
        });
        
        // Botão de ZoomOut, afastando o mapa
        Button zoomOut = new Button("Zoom -");
        zoomOut.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) { webEngine.executeScript("document.zoomOut()"); }
        });
        
        // Espaço horizontal para ajuste no layout
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Cria a barra de ferramentas com os controles da view
        ToolBar toolBar = new ToolBar();
        toolBar.getStyleClass().add("map-toolbar");
        toolBar.getItems().addAll(new Label("Localização: "), searchField, zoomIn, zoomOut, spacer, buttonBar);

        // Cria um layout do tipo BorderPane
        BorderPane root = new BorderPane();
        root.getStyleClass().add("map");
        root.setCenter(webView);
        root.setTop(toolBar);
        
        // Atribui o título da aplicação
        stage.setTitle("Google Maps");
        // Cria a scene, definindo o tamanho da janela e a cor de background
        Scene scene = new Scene(root, 1050, 590, Color.web("#666970"));
        stage.setScene(scene);
        // Atribui o arquivo de propriedades CSS.
        scene.getStylesheets().add("/map/googleMap.css");
        // Apresenta o stage, nodo raíz.
        stage.show();
    }

    /**
     * Método de inicialização.
     * @param args
     */
    public static void main(String ... args){
    	// Define as mesmas configurações de proxy do sistema
    	System.setProperty("java.net.useSystemProxies", "true");
        Application.launch(args);
    }
}