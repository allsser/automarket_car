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
		// �õ�
		EngineOn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Engine.setText("ON");
				State.setText(":U28000000010000000000000000->�õ�ON");
			}
		});
		EngineOFF.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Engine.setText("OFF");
				State.setText(":U28000000010000000000000001->�õ�OFF");
			}
		});
		
		// ����
		Trouble1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FixState.setText("����1");
			}
		});
		Trouble2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FixState.setText("����2");
			}
		});
		Fix.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FixState.setText("����");
			}
		});
		
		// ����
		Arrival.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ArrivalState.setText("����");
			}
		});
		Delivery.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ArrivalState.setText("�����");
			}
		});
		
		// �µ�
		Temp.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		
		// ���͸�
		Battery.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
			}
		});

	}
}
