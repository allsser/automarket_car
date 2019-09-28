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
	
	// inner class형식으로 event처리 listener class를 작성
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
			
			// Serial Port에서 EVENT가 발생하면 호출
			if(event.getEventType() == 
					SerialPortEvent.DATA_AVAILABLE) {
				// port를 통해서 데이터가 들어왔다는 의미
				
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
						printMsg("받은 메시지는 : " + result);
					} else if(result.contains(EngineOFF)){
						EngineState.setText("OFF");
						Engine = "0";
						printMsg("OFF");
						printMsg("받은 메시지는 : " + result);
					} else if(result.contains(Trouble1)){
						FixState.setText("고장1");
						test1 = "Trouble1";
						printMsg("고장1");
						printMsg("받은 메시지는 : " + result);
					} else if(result.contains(Trouble2)){
						FixState.setText("고장2");
						test1 = "Trouble2";
						printMsg("고장2");
						printMsg("받은 메시지는 : " + result);
					} else if(result.contains(Fix)){
						FixState.setText("정상");
						test1 = "Fix";
						printMsg("정상");
						printMsg("받은 메시지는 : " + result);
					} else if(result.contains(Arrival)){
						ArrivalState.setText("도착");
						printMsg("도착");
						printMsg("받은 메시지는 : " + result);
					} else if(result.contains(Delivery)){
						ArrivalState.setText("배달중");
						printMsg("배달중");
						printMsg("받은 메시지는 : " + result);
					} else if(result.contains(Temp)) {
						String temp = result.substring(26, 28);
						TempState.setText(temp);
						printMsg("온도 : "+ temp + "도");
						printMsg("받은 메시지는 : " + result);
					} else if(result.contains(Battery)) {
						String battery = result.substring(26, 28);
						BatteryState.setText(battery);
						printMsg("배터리 : "+ battery + "%");
						printMsg("받은 메시지는 : " + result);
					} else if(result.contains(Latitude)) {
						String latitude;
						String left = result.substring(20, 22);
						String right = result.substring(22, 28);
						latitude = left + "." + right;
						LatiState.setText(latitude);
						printMsg("경도 :"+latitude);
						printMsg("받은 메세지는 : " + result);
					} else if(result.contains(Longitude)) {
						String longitude;
						String left = result.substring(19, 22);
						String right = result.substring(22, 28);
						longitude = left + "." + right;
						LongiState.setText(longitude);
						printMsg("위도 :"+longitude);
						printMsg("받은 메세지는 : " + result);
					} else if(result.contains(Recall)){
						CompleteState.setText("회수중");
						printMsg("회수중");
						printMsg("받은 메시지는 : " + result);
					} else if(result.contains(Complete)){
						CompleteState.setText("회수완료");
						printMsg("회수완료");
						printMsg("받은 메시지는 : " + result);
					} else if(result.contains(GoodState)){
						CompleteState.setText("정상");
						printMsg("정상");
						printMsg("받은 메시지는 : " + result);
					} 
//					if((test != null) && (test1 != null)) {
//						
//						//PrintWriter out1 = new PrintWriter(socket.getOutputStream());	
//						String send = "/10000202/1";
//						out1.println(send);
//						out1.flush();
//						printMsg("데이터를 보냈습니다.");
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
				printMsg("데이터를 보냈습니다.");
			}
		}
	}
		
	private void connectPort(String portName) {
		// portName을 이용해 Port에 접근해서 객체를 생성한다.
		try {
			portIdentifier =
					CommPortIdentifier.getPortIdentifier(portName);
			printMsg(portName + "에 연결을 시도합니다.");
			
			if(portIdentifier.isCurrentlyOwned()) { // 현재 다른 놈에 의해 쓰이고 있는지 확인
				printMsg(portName + "가 다른 프로그램에 의해서 사용되고 있어요.");
			} else {
				// 포트가 존재하고 내가 사용할 수 있다.
				// 포트를 열고 포트 객체를 획득
				// 첫번째 인자는 포트를 여는 프로그램의 이름(문자열)
				// 두번째 인자는 포트를 열때 기다릴 수 있는 시간(밀리세컨드)
				commPort = portIdentifier.open("MyApp",5000);
				// 포트 객체를 얻은 후 이 포트객체가 Serial인지 Parallel인지를
				// 확인한 후 적절하게 type casting
				if( commPort instanceof SerialPort) {
					// Serial Port 객체를 얻어낼 수 있다.
					serialPort = (SerialPort)commPort;
					// Serial Port에 대한 설정을 해야 한다.
					serialPort.setSerialPortParams(
							38400, // Serial Port 통신 속도
							SerialPort.DATABITS_8, // 데이터 비트
							SerialPort.STOPBITS_1, // stop bit 설정
							SerialPort.PARITY_NONE); // Parity bit는 안쓴다.				
					// Serial Port를 Open하고 설정까지 잡아놓은 상태이다.
					// 나에게 들어오는 Data Frame을 받아들일 수 있는 상태이다.
					// Data Frame이 전달되는 것을 감지하기 위해서 Event처리 기법을 이용
					// 데이터가 들어오는걸 감지하고 처리하는 Listener 객체가 있어야 한다.
					// 이런 Listener객체를 만들어서 Port에 리스너로 등록해주면 된다.
					// 당연히 Listener객체를 만들기 위한 class가 있어야 한다.
					serialPort.addEventListener(new MyPortListener());
					serialPort.notifyOnDataAvailable(true);
					printMsg(portName + "에 리스너가 등록되었습니다");
					// 입출력을 하기 위해서 Stream을 열면 된다.
					bis = new BufferedInputStream(
							serialPort.getInputStream());
					out = serialPort.getOutputStream();
					// CAN 데이터 수신 허용 설정
					// 이 작업은 어떻게 해야 하나요?
					// 프로토콜을 이용해서 정해진 형식대로 문자열을 만들어서
					// out stream을 통해서 출력
					String msg = ":G11A9\r";
					// 시작문자 :, 명령코드 G, 수신여부 명령 코드 11, Check Sum A9, 끝 문자 \r
					try {
						byte[] inputData = msg.getBytes();
						out.write(inputData);
						printMsg(portName + "가 수신을 시작합니다.");
					}catch (Exception e) {
						System.out.println(e);
					}
				}
			}	
		}catch (Exception e) {
			// 발생한 Exception을 처리하는 코드가 들어와야 한다.
			System.out.println(e);
		}
	}
	
	class ReceiveRunnable implements Runnable {
		//서버로 부터 들어오는 메시지를 받아들이는 역할을 수행
		//소켓에 대한 입력스트림만 있으면 됨
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
						CompleteState.setText("회수중");
						printMsg("회수중");
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
		// 연결
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
					// 클라이언트는 버튼을 누르면 서버쪽에 Socket접속을 시도.
					socket = new Socket("127.0.0.1",7848);
					// 만약에 접속에 성공하면 socket객체를 하나 획득.
					
					br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out1 = new PrintWriter(socket.getOutputStream());				
					printMsg("서버 접속 성공");
					
					String msg = CarName.getText();
					out1.println(msg);
					out1.flush();
					printMsg("차 : "+msg);
									
					
					
					ReceiveRunnable runnable = new ReceiveRunnable(br);
					executorService.execute(runnable);
				} catch (Exception e) {
					System.out.println(e);
				}				
			}
		});
	}
	
}


