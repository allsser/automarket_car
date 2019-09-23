package application;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;

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

public class VCSController {
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
	@FXML
	private Button Conn;
	
	// 사용할 COM 포트를 지정하기 위해서 필요.
	private CommPortIdentifier portIdentifier;
	// 만약 COM 포트를 사용할 수 있고 해당 포트를 open하면 COM 포트 객체를 획득
	private CommPort commPort;
	// COM 포트는 종류가 2가지이다. (Serial, Parallel 두가지 종류)
	// CAN 통신은 Serial 통신을 한다. 따라서 COM 포트의 타입을 알아내서 type casting 한다.
	private SerialPort serialPort;
	// Port객체로부터 Stream을 얻어내서 입출력 할 수 있다.
	private BufferedInputStream bis;
	private OutputStream out;
	
	private void printMsg(String msg) {
		State.appendText(msg + "\n");
	}
	
	private String getCheckSum(String send_data) {
		char[] chars = send_data.toCharArray();
		Integer result = 0;
		Integer sum = 0;
		char[] var8 = chars;
		int var7 = chars.length;
		
		for(int var6=0; var6 < var7; ++var6) {
			char i = var8[var6];
			sum = sum + i;
		}
		
		result = sum & 255;
		return Integer.toHexString(result);
	}
	
	// inner class형식으로 event처리 listener class를 작성
	class MyPortListener implements SerialPortEventListener{

		@Override
		public void serialEvent(SerialPortEvent event) {
			// Serial Port에서 EVENT가 발생하면 호출
			if(event.getEventType() == 
					SerialPortEvent.DATA_AVAILABLE) {
				// port를 통해서 데이터가 들어왔다는 의미
				byte[] writeBuffer = new byte[128];
				try {
					while(bis.available() > 0 ) {
						bis.read(writeBuffer);
					}
					String result = new String(writeBuffer);
					printMsg("보낸 메시지는 : " + result);
				} catch (Exception e) {
					System.out.println(e);
				}
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
					String msg = "G11A9\r";
					
					try {
						byte[] inputData = msg.getBytes();
						out.write(inputData);
						printMsg(portName + "가 송신을 시작합니다.");
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
	
	
	public void clickHandler() {
		// 시동
		EngineOn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Engine.setText("ON");
				printMsg("시동ON");
				String msg = ":W2800000001000000000000000042\r";
				byte[] inputData = msg.getBytes();
				try {
					out.write(inputData);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		EngineOFF.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Engine.setText("OFF");
				printMsg("시동OFF");
				String msg = ":W2800000001000000000000000143\r";
				byte[] inputData = msg.getBytes();
				try {
					out.write(inputData);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		// 고장
		Trouble1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FixState.setText("고장1");
				printMsg("고장1");
				String msg = ":W2800000003000000000000000044\r";
				byte[] inputData = msg.getBytes();
				try {
					out.write(inputData);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		Trouble2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FixState.setText("고장2");
				printMsg("고장2");
				String msg = ":W2800000003000000000000000145\r";
				byte[] inputData = msg.getBytes();
				try {
					out.write(inputData);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		Fix.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FixState.setText("정상");
				printMsg("정상");
				String msg = ":W2800000003000000000000000246\r";
				byte[] inputData = msg.getBytes();
				try {
					out.write(inputData);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		// 도착
		Arrival.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ArrivalState.setText("도착");
				printMsg("도착");
				String msg = ":W280000000900000000000000004A\r";
				byte[] inputData = msg.getBytes();
				try {
					out.write(inputData);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		Delivery.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ArrivalState.setText("배달중");
				printMsg("배달중");
				String msg = ":W280000000900000000000000014B\r";
				byte[] inputData = msg.getBytes();
				try {
					out.write(inputData);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		// 온도
		Temp.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				String start = ":";
				String msg = "";
				String checksum = "";
				String end = "\r";

				msg = "W280000000500000000000000";
				String temp = TempState.getText();
				
				if(temp.length() == 3) {
					msg = "W28000000050000000000000100";
				} else if(temp.length() == 2) {
					msg = msg + temp;					
				} else if (temp.length() == 1) {
					temp = "0" + temp;
					msg = msg + temp;
				}
				checksum = getCheckSum(msg).toUpperCase();
				msg = start + msg + checksum + end;

				printMsg("온도 :" + temp + "도");

				byte[] inputData = msg.getBytes();
				try {
					out.write(inputData);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		
		// 배터리
		Battery.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				String start = ":";
				String msg = "";
				String checksum = "";
				String end = "\r";

				msg = "W280000000700000000000000";
				String battery = BatteryState.getText();

				if(battery.length() == 3) {
					msg = "W28000000070000000000000100";
				} else if(battery.length() == 2) {
					msg = msg + battery;					
				} else if (battery.length() == 1) {
					battery = "0" + battery;
					msg = msg + battery;
				}
				
				checksum = getCheckSum(msg).toUpperCase();
				msg = start + msg + checksum + end;

				printMsg("배터리 :" + battery + "%");

				byte[] inputData = msg.getBytes();
				try {
					out.write(inputData);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		// 연결
		Conn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String portName = "COM6";
				connectPort(portName);
				
			}
		});

	}

}
