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
	
	// ����� COM ��Ʈ�� �����ϱ� ���ؼ� �ʿ�.
	private CommPortIdentifier portIdentifier;
	// ���� COM ��Ʈ�� ����� �� �ְ� �ش� ��Ʈ�� open�ϸ� COM ��Ʈ ��ü�� ȹ��
	private CommPort commPort;
	// COM ��Ʈ�� ������ 2�����̴�. (Serial, Parallel �ΰ��� ����)
	// CAN ����� Serial ����� �Ѵ�. ���� COM ��Ʈ�� Ÿ���� �˾Ƴ��� type casting �Ѵ�.
	private SerialPort serialPort;
	// Port��ü�κ��� Stream�� ���� ����� �� �� �ִ�.
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
	
	// inner class�������� eventó�� listener class�� �ۼ�
	class MyPortListener implements SerialPortEventListener{

		@Override
		public void serialEvent(SerialPortEvent event) {
			// Serial Port���� EVENT�� �߻��ϸ� ȣ��
			if(event.getEventType() == 
					SerialPortEvent.DATA_AVAILABLE) {
				// port�� ���ؼ� �����Ͱ� ���Դٴ� �ǹ�
				byte[] writeBuffer = new byte[128];
				try {
					while(bis.available() > 0 ) {
						bis.read(writeBuffer);
					}
					String result = new String(writeBuffer);
					printMsg("���� �޽����� : " + result);
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
					String msg = "G11A9\r";
					
					try {
						byte[] inputData = msg.getBytes();
						out.write(inputData);
						printMsg(portName + "�� �۽��� �����մϴ�.");
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
		// �õ�
		EngineOn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Engine.setText("ON");
				printMsg("�õ�ON");
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
				printMsg("�õ�OFF");
				String msg = ":W2800000001000000000000000143\r";
				byte[] inputData = msg.getBytes();
				try {
					out.write(inputData);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		// ����
		Trouble1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FixState.setText("����1");
				printMsg("����1");
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
				FixState.setText("����2");
				printMsg("����2");
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
				FixState.setText("����");
				printMsg("����");
				String msg = ":W2800000003000000000000000246\r";
				byte[] inputData = msg.getBytes();
				try {
					out.write(inputData);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		// ����
		Arrival.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ArrivalState.setText("����");
				printMsg("����");
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
				ArrivalState.setText("�����");
				printMsg("�����");
				String msg = ":W280000000900000000000000014B\r";
				byte[] inputData = msg.getBytes();
				try {
					out.write(inputData);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		// �µ�
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

				printMsg("�µ� :" + temp + "��");

				byte[] inputData = msg.getBytes();
				try {
					out.write(inputData);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		
		// ���͸�
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

				printMsg("���͸� :" + battery + "%");

				byte[] inputData = msg.getBytes();
				try {
					out.write(inputData);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		// ����
		Conn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String portName = "COM6";
				connectPort(portName);
				
			}
		});

	}

}
