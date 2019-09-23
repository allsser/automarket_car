package application;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class VIController {
	@FXML
	private TextField EngineState;
	@FXML
	private TextField TempState; 
	@FXML
	private TextField BatteryState;
	@FXML
	private TextField FixState;
	@FXML
	private TextField ArrivalState;
	@FXML
	private TextArea State;
	@FXML
	private Button Conn;
	@FXML
	private Button Server;
	
	private CommPortIdentifier portIdentifier;
	private CommPort commPort;
	private SerialPort serialPort;
	private BufferedInputStream bis;
	private OutputStream out;
	
	private void printMsg(String msg) {
		State.appendText(msg + "\n");
	}
	
	// inner class�������� eventó�� listener class�� �ۼ�
	class MyPortListener implements SerialPortEventListener{

		String EngineON = ":U2800000001000000000000000040";
		String EngineOFF = ":U2800000001000000000000000141";
		String Trouble1 = ":U2800000003000000000000000042";
		String Trouble2 = ":U2800000003000000000000000143";
		String Fix = ":U2800000003000000000000000244";
		String Arrival = ":U2800000009000000000000000048";
		String Delivery = ":W2800000009000000000000000149";
		String Temp = ":W2800000005";
		String Battery = ":W2800000007";
		
		@Override
		public void serialEvent(SerialPortEvent event) {
			// Serial Port���� EVENT�� �߻��ϸ� ȣ��
			if(event.getEventType() == 
					SerialPortEvent.DATA_AVAILABLE) {
				// port�� ���ؼ� �����Ͱ� ���Դٴ� �ǹ�
				
				byte[] readBuffer = new byte[128];
				try {
					while(bis.available() > 0 ) {
						bis.read(readBuffer);
					}
					String result = new String(readBuffer);
	
					if(result.contains(EngineON)) {
						EngineState.setText("ON");
						printMsg("ON");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(EngineOFF)){
						EngineState.setText("OFF");
						printMsg("OFF");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Trouble1)){
						FixState.setText("����1");
						printMsg("����1");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Trouble2)){
						FixState.setText("����2");
						printMsg("����2");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Fix)){
						FixState.setText("����");
						printMsg("����");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Arrival)){
						ArrivalState.setText("����");
						printMsg("����");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Delivery)){
						ArrivalState.setText("�����");
						printMsg("�����");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Temp)) {
						String temp = result.substring(27, 29);
						TempState.setText(temp);
						printMsg("�µ� : "+ temp + "��");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Battery)) {
						String battery = result.substring(27, 29);
						BatteryState.setText(battery);
						printMsg("���͸� : "+ battery + "%");
						printMsg("���� �޽����� : " + result);
					}
				} catch (Exception e) {
					System.out.println(e);
				}
			}	
		}
	}
		
	private void connectPort(String portName) {
		// portName�� �̿��� Port�� �����ؼ� ��ü�� �����Ѵ�.
		try {
			portIdentifier =
					CommPortIdentifier.getPortIdentifier(portName);
			printMsg(portName + "�� ������ �õ��մϴ�.");
			
			if(portIdentifier.isCurrentlyOwned()) { // ���� �ٸ� �� ���� ���̰� �ִ��� Ȯ��
				printMsg(portName + "�� �ٸ� ���α׷��� ���ؼ� ���ǰ� �־��.");
			} else {
				// ��Ʈ�� �����ϰ� ���� ����� �� �ִ�.
				// ��Ʈ�� ���� ��Ʈ ��ü�� ȹ��
				// ù��° ���ڴ� ��Ʈ�� ���� ���α׷��� �̸�(���ڿ�)
				// �ι�° ���ڴ� ��Ʈ�� ���� ��ٸ� �� �ִ� �ð�(�и�������)
				commPort = portIdentifier.open("MyApp",5000);
				// ��Ʈ ��ü�� ���� �� �� ��Ʈ��ü�� Serial���� Parallel������
				// Ȯ���� �� �����ϰ� type casting
				if( commPort instanceof SerialPort) {
					// Serial Port ��ü�� �� �� �ִ�.
					serialPort = (SerialPort)commPort;
					// Serial Port�� ���� ������ �ؾ� �Ѵ�.
					serialPort.setSerialPortParams(
							38400, // Serial Port ��� �ӵ�
							SerialPort.DATABITS_8, // ������ ��Ʈ
							SerialPort.STOPBITS_1, // stop bit ����
							SerialPort.PARITY_NONE); // Parity bit�� �Ⱦ���.				
					// Serial Port�� Open�ϰ� �������� ��Ƴ��� �����̴�.
					// ������ ������ Data Frame�� �޾Ƶ��� �� �ִ� �����̴�.
					// Data Frame�� ���޵Ǵ� ���� �����ϱ� ���ؼ� Eventó�� ����� �̿�
					// �����Ͱ� �����°� �����ϰ� ó���ϴ� Listener ��ü�� �־�� �Ѵ�.
					// �̷� Listener��ü�� ���� Port�� �����ʷ� ������ָ� �ȴ�.
					// �翬�� Listener��ü�� ����� ���� class�� �־�� �Ѵ�.
					serialPort.addEventListener(new MyPortListener());
					serialPort.notifyOnDataAvailable(true);
					printMsg(portName + "�� �����ʰ� ��ϵǾ����ϴ�");
					// ������� �ϱ� ���ؼ� Stream�� ���� �ȴ�.
					bis = new BufferedInputStream(
							serialPort.getInputStream());
					out = serialPort.getOutputStream();
					// CAN ������ ���� ��� ����
					// �� �۾��� ��� �ؾ� �ϳ���?
					// ���������� �̿��ؼ� ������ ���Ĵ�� ���ڿ��� ����
					// out stream�� ���ؼ� ���
					String msg = ":G11A9\r";
					// ���۹��� :, ����ڵ� G, ���ſ��� ��� �ڵ� 11, Check Sum A9, �� ���� \r
					try {
						byte[] inputData = msg.getBytes();
						out.write(inputData);
						printMsg(portName + "�� ������ �����մϴ�.");
					}catch (Exception e) {
						System.out.println(e);
					}
				}
			}	
		}catch (Exception e) {
			// �߻��� Exception�� ó���ϴ� �ڵ尡 ���;� �Ѵ�.
			System.out.println(e);
		}
	}
	public void clickHandler() {
		// ����
		Conn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {			
				String portName = "COM6";
				connectPort(portName);
						
			}
		});
		
		Server.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {			
				try {
					// Ŭ���̾�Ʈ�� ��ư�� ������ �����ʿ� Socket������ �õ�.
					Socket socket = new Socket("127.0.0.1", 5554);
					// ���࿡ ���ӿ� �����ϸ� socket��ü�� �ϳ� ȹ��.
					InputStreamReader isr = new InputStreamReader(socket.getInputStream());
					BufferedReader br = new BufferedReader(isr);
					String msg = br.readLine();
					printMsg(msg);
					br.close();
					isr.close();
					socket.close();
				} catch (Exception e) {
					System.out.println(e);
				}				
			}
		});
	}
}

