package application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MainController {
	@FXML
	private TextField Engine;
	@FXML
	private TextField FixState;
	@FXML
	private TextField TempState;
	@FXML
	private TextField ArrivalState;
	@FXML
	private TextField BatteryState;
	@FXML
	private TextArea State;
	@FXML
	private Button Temp;
	@FXML
	private Button EngineOn;
	@FXML
	private Button EngineOFF;
	@FXML
	private Button Trouble1;
	@FXML
	private Button Trouble2;
	@FXML
	private Button Fix;
	@FXML
	private Button Arrival;
	@FXML
	private Button Delivery;
	@FXML
	private Button Battery;
	
	
	public void clickHandler() {
		// 시동
		EngineOn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Engine.setText("ON");
				State.setText(":U28000000010000000000000000->시동ON");
			}
		});
		EngineOFF.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Engine.setText("OFF");
				State.setText(":U28000000010000000000000001->시동OFF");
			}
		});
		
		// 고장
		Trouble1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FixState.setText("고장1");
			}
		});
		Trouble2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FixState.setText("고장2");
			}
		});
		Fix.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FixState.setText("정상");
			}
		});
		
		// 도착
		Arrival.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ArrivalState.setText("도착");
			}
		});
		Delivery.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ArrivalState.setText("배달중");
			}
		});
		
		// 온도
		Temp.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		
		// 배터리
		Battery.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
			}
		});

	}
}
