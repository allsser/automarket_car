package application;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.util.JSONPObject;

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
	
	String CarStatus = null;
	
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
		
		String CarStart = null;
		String CarError = null;
//		String CarStatus = null;
		String Battery1 = null;
		String Temperature = null;
		String DestLati = null;
		String DestLong = null;
		
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
					
					String CarNum = CarName.getText();
	
					if(result.contains(EngineON)) {
						EngineState.setText("ON");
						CarStart = "1";
						printMsg("ON");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(EngineOFF)){
						EngineState.setText("OFF");
						CarStart = "0";
						printMsg("OFF");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Trouble1)){
						FixState.setText("����1");
						CarError = "01";
						printMsg("����1");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Trouble2)){
						FixState.setText("����2");
						CarError = "02";
						printMsg("����2");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Fix)){
						FixState.setText("����");
						CarError = "00";
						printMsg("����");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Temp)) {
						String temp = result.substring(26, 28);
						TempState.setText(temp);
						Temperature = temp;
						printMsg("�µ� : "+ temp + "��");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Battery)) {
						String battery = result.substring(26, 28);
						BatteryState.setText(battery);
						Battery1 = battery;
						printMsg("���͸� : "+ battery + "%");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Latitude)) {
						String latitude;
						String left = result.substring(20, 22);
						String right = result.substring(22, 28);
						latitude = left + "." + right;
						LatiState.setText(latitude);
						DestLati = latitude;
						printMsg("�浵 :"+latitude);
						printMsg("���� �޼����� : " + result);
					} else if(result.contains(Longitude)) {
						String longitude;
						String left = result.substring(19, 22);
						String right = result.substring(22, 28);
						longitude = left + "." + right;
						DestLong = longitude;
						LongiState.setText(longitude);
						printMsg("���� :"+longitude);
						printMsg("���� �޼����� : " + result);
					} else if(result.contains(GoodState)){
						CompleteState.setText("����");
						CarStatus = "00";
						printMsg("����");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Delivery)){
						CompleteState.setText("�����");
						CarStatus = "01";
						printMsg("�����");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Arrival)){
						CompleteState.setText("����");
						CarStatus = "02";
						printMsg("����");
						printMsg("���� �޽����� : " + result);
						String send = "/10000103/"+CarNum;
						out1.println(send);
						out1.flush();
					} else if(result.contains(Recall)){
						CompleteState.setText("ȸ����");
						CarStatus = "03";
						printMsg("ȸ����");
						printMsg("���� �޽����� : " + result);
					} else if(result.contains(Complete)){
						CompleteState.setText("ȸ���Ϸ�");
						CarStatus = "04";
						printMsg("ȸ���Ϸ�");
						printMsg("���� �޽����� : " + result);
						String send = "/10000002/"+CarNum;
						out1.println(send);
						out1.flush();
					} 
				} catch (Exception e) {
					System.out.println(e);
				}
			} 
			if((CarStatus != null) && (DestLati != null) && (DestLong != null) 
					&& (Temperature != null) && (Battery1 != null) && (CarStart != null) && (CarError != null)) {
			

				JSONObject obj = new JSONObject();
				
				Double DestLa = Double.parseDouble(DestLati);
				Double DestLo = Double.parseDouble(DestLong);
				int Temper =Integer.parseInt(Temperature);
				int Batt =Integer.parseInt(Battery1);
				
				obj.put("carstatus", CarStatus);
				obj.put("latitude", DestLa);
				obj.put("longitude", DestLo);
				obj.put("temp", Temper);
				obj.put("battery", Batt);
				obj.put("startflag", CarStart);
				obj.put("error", CarError);

				String json = obj.toJSONString();
				
				printMsg(json);
				
				String CarNum = CarName.getText();
				printMsg(CarNum);
				String send = "C/10000202/"+CarNum+"/"+json;
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
					line = "C"+line;
					printMsg(line +"-> ȸ�� ��û");
					String CarNum = CarName.getText();
					if(line.contains("C/10000001/"+CarNum)) {
						CompleteState.setText("ȸ����");
						CarStatus = "03";
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
				String portName = "COM6";
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
					
					String Conn = "C/10000000/";
					
					String CarNum = CarName.getText();
					
					out1.println(Conn+CarNum);
					out1.flush();
					printMsg("�� : "+CarNum);
					
													
					ReceiveRunnable runnable = new ReceiveRunnable(br);
					executorService.execute(runnable);
				} catch (Exception e) {
					System.out.println(e);
				}				
			}
		});
	}
	
}


