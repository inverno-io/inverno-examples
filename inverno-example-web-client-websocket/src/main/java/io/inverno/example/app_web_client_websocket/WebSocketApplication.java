/*
 * Copyright 2023 Jeremy KUHN
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.inverno.example.app_web_client_websocket;

import io.inverno.core.annotation.Bean;
import io.inverno.core.annotation.Destroy;
import io.inverno.core.annotation.Init;
import io.inverno.example.app_web_client_websocket.dto.Message;
import io.inverno.mod.http.base.ExchangeContext;
import io.inverno.mod.web.client.WebClient;
import io.inverno.mod.web.client.ws.Web2SocketExchange;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

import java.net.URI;
import java.util.Random;

/**
 * <p>
 * Inverno WebSocket client JavaFX application using a programmatic Web client.
 * </p>
 *
 * @author <a href="jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean
public class WebSocketApplication extends Application {

	private final AppConfiguration configuration;
	private final WebClient<? extends ExchangeContext> webClient;

	private TextField nicknameTextField;
	private TextField wsURLTextField;
	private Button connectButton; 
	private TextFlow chatTextFlow;
	private TextField messageTextField;
	private Button sendButton;

	private String color;
	private Web2SocketExchange<? extends ExchangeContext> wsExchange;
	private Many<Message> wsOutbound;
	
	public WebSocketApplication(AppConfiguration configuration, WebClient<? extends ExchangeContext> webClient) {
		this.configuration = configuration;
		this.webClient = webClient;
	}
	
	/**
	 * <p>
	 * Builds the JavaFX scene. 
	 * </p>
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox vbox1 = new VBox();
		vbox1.setStyle(
			"-fx-spacing: 16;" +
			"-fx-padding: 16;"
		);
		
		HBox hbox11 = new HBox();
		hbox11.setStyle(
			"-fx-spacing: 16;"
		);
		hbox11.setSpacing(16);

		TextField textField111 = new TextField("Guest" + new Random().nextInt(1000));
		textField111.setPromptText("Nickname...");

		TextField textField112 = new TextField("ws://127.0.0.1:8080/ws");
		textField112.setPromptText("ws://... wss://... ");
		HBox.setHgrow(textField112, Priority.ALWAYS);
		
		Button button113 = new Button("Connect");
		hbox11.getChildren().addAll(textField111, textField112, button113);
		
		VBox vbox12 = new VBox();
		vbox12.setStyle(
			"-fx-spacing: 16;" +
			"-fx-padding: 16;" +
			"-fx-border-color: -fx-box-border;" +
			"-fx-border-width: 1;" +
			"-fx-border-radius: 3, 2;"
		);
		VBox.setVgrow(vbox12, Priority.ALWAYS);

		ScrollPane scrollPane121 = new ScrollPane();
		scrollPane121.setStyle(
			"-fx-padding: 5 16;" +
			"-fx-border-color: -fx-box-border;" +
			"-fx-border-width: 1;" +
			"-fx-border-radius: 3, 2;" +
			"-fx-background-color: transparent;"
		);
		VBox.setVgrow(scrollPane121, Priority.ALWAYS);
		
		TextFlow textflow1211 = new TextFlow();
		textflow1211.setLineSpacing(16);
		
		scrollPane121.setContent(textflow1211);
		
		HBox hbox122 = new HBox();
		hbox122.setStyle(
			"-fx-spacing: 16;"
		);
		
		TextField textField1221 = new TextField();
		HBox.setHgrow(textField1221, Priority.ALWAYS);
		
		Button button1222 = new Button("Send");
		
		hbox122.getChildren().addAll(textField1221, button1222);
		vbox12.getChildren().addAll(scrollPane121, hbox122);
		vbox1.getChildren().addAll(hbox11, vbox12);

		this.nicknameTextField = textField111;
		this.wsURLTextField = textField112;
		this.connectButton = button113;
		this.connectButton.addEventHandler(ActionEvent.ACTION, this::onConnectDisconnect);
		this.chatTextFlow = textflow1211;
		this.messageTextField = textField1221;
		this.messageTextField.addEventHandler(ActionEvent.ACTION, this::onSend);
		this.sendButton = button1222;
		this.sendButton.addEventHandler(ActionEvent.ACTION, this::onSend);
		
		Scene scene = new Scene(vbox1, 800, 600);
		primaryStage.setTitle("Inverno Web Client WebSocket Example");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * <p>
	 * Determines whether the client is connected.
	 * </p>
	 * 
	 * @return true if the client is connected, false otherwise
	 */
	public boolean isConnected() {
		return this.wsExchange != null;
	}
	
	/**
	 * <p>
	 * Establishes a WebSocket connection.
	 * </p>
	 */
	private void connect() {
		if(!this.isConnected() && !this.isConnecting()) {
			this.nicknameTextField.setDisable(true);
			this.wsURLTextField.setDisable(true);
			
			URI wsURI = URI.create(this.wsURLTextField.getText());
			this.webClient.exchange(wsURI)
				.flatMap(exchange -> exchange.webSocket("json"))                            // Make sure to subscribe to the WebSocket exchange publisher to establish the WebSocket connection
				.flatMapMany(webSocketExchange -> {
					this.wsExchange = webSocketExchange;
					this.onConnected();                                                        // The WebSocket connection is established

					this.wsOutbound = Sinks.many().unicast().onBackpressureBuffer();           // Set the WebSocket outbound messages publisher
					webSocketExchange.outbound().encodeTextMessages(this.wsOutbound.asFlux());
					return webSocketExchange.inbound().decodeTextMessages(Message.class);      // Prepare to consume inbound messages publisher
				})
				.doOnTerminate(this::onDisconnected)                                           // Finalize disconnect when the inbound publisher terminates
				.doOnNext(this::onMessage)                                                     // Handle inbound messages
				.subscribe();                                                                  // Subscribe to start the whole process
		}
	}
	
	/**
	 * <p>
	 * Determines whether a connection is currently being established.
	 * <p>
	 * 
	 * @return true if there's an active connection process, false otherwise
	 */
	private boolean isConnecting() {
		return this.wsURLTextField.isDisabled();
	}
	
	/**
	 * <p>
	 * Disconnects the client.
	 * </p>
	 */
	private void disconnect() {
		if(this.isConnected()) {
			this.wsExchange.close();
		}
	}
	
	/**
	 * <p>
	 * Callback invoked when a Connect/Disconnect action is triggered from the UI.
	 * </p>
	 * 
	 * @param event an action event
	 */
	private synchronized void onConnectDisconnect(ActionEvent event) {
		if(this.isConnected()) {
			this.disconnect();
		}
		else {
			this.connect();
		}
	}
	
	/**
	 * <p>
	 * Callback invoked when a Send action is triggered from the UI.
	 * </p>
	 * 
	 * @param event an action event
	 */
	private void onSend(ActionEvent event) {
		if(this.wsOutbound != null) {
			this.wsOutbound.tryEmitNext(new Message(this.nicknameTextField.getText(), this.messageTextField.getText(), this.color));
		}
		this.messageTextField.setText("");
	}
	
	/**
	 * <p>
	 * Callback invoked on inbound messages.
	 * </p>
	 * 
	 * @param message an inbound message
	 */
	private void onMessage(Message message) {
		Platform.runLater(() -> {
			Text nicknameText = new Text("[" + message.getNickname() + "] ");
			nicknameText.setStyle("-fx-font-weight: bold;-fx-fill: " + message.getColor() + ";");

			Text messageText = new Text(message.getMessage() + "\n");
			messageText.setStyle("-fx-font-weight: bold;");
			this.chatTextFlow.getChildren().addAll(nicknameText, messageText);
		});
	}
	
	/**
	 * <p>
	 * Callback invoked when a WebSocket connection is established.
	 * </p>
	 */
	private void onConnected() {
		Random rgbRandom = new Random();
		this.color = String.format("#%02x%02x%02x", rgbRandom.nextInt(256), rgbRandom.nextInt(256), rgbRandom.nextInt(256));
		Platform.runLater(() -> {
			this.connectButton.setText("Disconnect");
			Text text = new Text("[ Connected to " + this.wsURLTextField.getText() + " ]\n");
			text.setStyle("-fx-font-style: italic;-fx-fill: rgba(33, 37, 41, 0.75);");
			this.chatTextFlow.getChildren().add(text);
		});
	}
	
	/**
	 * <p>
	 * Callback invoked when a WebSocket connection is closed.
	 * </p>
	 */
	private void onDisconnected() {
		if(this.wsOutbound != null) {
			this.wsOutbound.tryEmitComplete();
		}
		this.wsOutbound = null;
		this.wsExchange = null;
		Platform.runLater(() -> {
			this.connectButton.setText("Connect");
			this.nicknameTextField.setDisable(false);
			this.wsURLTextField.setDisable(false);
			Text text = new Text("[ Disconnected ]\n");
			text.setStyle("-fx-font-style: italic;-fx-fill: rgba(33, 37, 41, 0.75);");
			this.chatTextFlow.getChildren().add(text);
		});
	}
	
	/**
	 * <p>
	 * Starts the JavaFX runtime and launches the JavaFX application.
	 * </p>
	 */
	@Init
	public void launch() {
		if(this.configuration.mode() == AppConfiguration.WebClientMode.PROGRAMMATIC) {
			Platform.startup(() -> {
				try {
					Stage stage = new Stage();
					stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
						System.exit(0);
					});

					this.init();
					this.start(stage);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			});
		}
	}
	
	/**
	 * <p>
	 * Exits the application.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Destroy
	public void exit() throws Exception {
		if(this.configuration.mode() == AppConfiguration.WebClientMode.PROGRAMMATIC) {
			this.disconnect();
			this.stop();
		}
	}
}
