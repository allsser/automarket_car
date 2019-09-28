package application;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.ObjectMapper;

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
	@FXML
	private TextField LatiState;
	@FXML
	private TextField LongiState;
	@FXML
	private TextField CarName;
	@FXML
	private TextField CompleteState;
	
	
	private CommPortIdentifier portIdentifier;
	private CommPort commPort;
	private SerialPort serialPort;
	private BufferedInputStream bis;
	private OutputStream out;
	
	Socket socket;
	BufferedReader br;
	PrintWriter out1;			
	
	ExecutorService executorService = Executors.newCachedThreadPool();
	
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
		String Delivery = ":U2800000009000000000000000149";
		String Temp = ":U2800000005";
		String Battery = ":U2800000007";
		String Latitude = ":U280000001100000000";
		String Longitude = ":U28000000110000000";
		String Recall = ":U2800000013000000000000000043";
		String Complete = ":U2800000013000000000000000144";
		String GoodState = ":U2800000013000000000000000245";
		
		String Engine = null;
		String test1 = null;
		String test2 = null;
		String test3 = null;
		
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
						Engine = "1";
						printMsg("ON");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(EngineOFF)){
						EngineState.setText("OFF");
						Engine = "0";
						printMsg("OFF");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Trouble1)){
						FixState.setText("����1");
						test1 = "Trouble1";
						printMsg("����1");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Trouble2)){
						FixState.setText("����2");
						test1 = "Trouble2";
						printMsg("����2");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Fix)){
						FixState.setText("����");
						test1 = "Fix";
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
						String temp = result.substring(26, 28);
						TempState.setText(temp);
						printMsg("�µ� : "+ temp + "��");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Battery)) {
						String battery = result.substring(26, 28);
						BatteryState.setText(battery);
						printMsg("���͸� : "+ battery + "%");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Latitude)) {
						String latitude;
						String left = result.substring(20, 22);
						String right = result.substring(22, 28);
						latitude = left + "." + right;
						LatiState.setText(latitude);
						printMsg("�浵 :"+latitude);
						printMsg("���� �޼����� : " + result);
					} else if(result.contains(Longitude)) {
						String longitude;
						String left = result.substring(19, 22);
						String right = result.substring(22, 28);
						longitude = left + "." + right;
						LongiState.setText(longitude);
						printMsg("���� :"+longitude);
						printMsg("���� �޼����� : " + result);
					} else if(result.contains(Recall)){
						CompleteState.setText("ȸ����");
						printMsg("ȸ����");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Complete)){
						CompleteState.setText("ȸ���Ϸ�");
						printMsg("ȸ���Ϸ�");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(GoodState)){
						CompleteState.setText("����");
						printMsg("����");
						printMsg("���� �޽����� : " + result);
					} 
//					if((test != null) && (test1 != null)) {
//						
//						//PrintWriter out1 = new PrintWriter(socket.getOutputStream());	
//						String send = "/10000202/1";
//						out1.println(send);
//						out1.flush();
//						printMsg("�����͸� ���½��ϴ�.");
//					}
				} catch (Exception e) {
					System.out.println(e);
				}
			} 
			if((test != null) && (test1 != null)) {
				
				//PrintWriter out1 = new PrintWriter(socket.getOutputStream());	
				String send = "/10000202/1";
				out1.println(send);
				out1.flush();
				printMsg("�����͸� ���½��ϴ�.");
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
	
	class ReceiveRunnable implements Runnable {
		//������ ���� ������ �޽����� �޾Ƶ��̴� ������ ����
		//���Ͽ� ���� �Է½�Ʈ���� ������ ��
		private BufferedReader br;
		
		public ReceiveRunnable(BufferedReader br) {
			super();
			this.br = br;
		}

		@Override
		public void run() {
			String line = "";
			try {
				while((line=br.readLine())!=null) {
					printMsg("test" + line);
					if(line.contains("/10000001/")) {
						CompleteState.setText("ȸ����");
						printMsg("ȸ����");
						String msg = ":W2800000013000000000000000045\r";
						byte[] inputData = msg.getBytes();
						try {
							out.write(inputData);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} 
				}
			}catch(Exception e) {
				
			}
		}
		
	}
		
	public void clickHandler() {
		// ����
		Conn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {			
				String portName = "COM7";
				connectPort(portName);
						
			}
		});
		
		
		
		Server.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {			
				try {
					// Ŭ���̾�Ʈ�� ��ư�� ������ �����ʿ� Socket������ �õ�.
					socket = new Socket("127.0.0.1",7848);
					// ���࿡ ���ӿ� �����ϸ� socket��ü�� �ϳ� ȹ��.
					
					br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out1 = new PrintWriter(socket.getOutputStream());				
					printMsg("���� ���� ����");
					
					String msg = CarName.getText();
					out1.println(msg);
					out1.flush();
					printMsg("�� : "+msg);
									
					
					
					ReceiveRunnable runnable = new ReceiveRunnable(br);
					executorService.execute(runnable);
				} catch (Exception e) {
					System.out.println(e);
				}				
			}
		});
	}
	
}


