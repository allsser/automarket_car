package application;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server {
	public static void main(String[] args) {
		// 서버쪽 프러그램은 클라이언트의 소켓 접속을 기다려야 한다.
		// ServerSocket class를 이용해서 기능을 구현
		ServerSocket server = null;
		// 클라이언트와 접속된 후 Socket 객체가 있어야지 클라이언트와
		// 데이터 통신이 가능
		Socket socket = null;
		try {
			// port번호를 가지고  ServerSocket객체를 생성
			// port번호는 0~65535 사용가능. 0~1023까지는 예약되어 있다.
			server = new ServerSocket(5554);
			System.out.println("클라이언트 접속 대기");
			socket = server.accept(); // 클라이언트 접속을 기다린다.(block)
			// 만약에 접속에 성공하면 socket객체를 하나 획득
			PrintWriter out =
					new PrintWriter(socket.getOutputStream());
			SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
			out.println(format.format(new Date()));
			// 일반적으로 Reader와 Write는 내보 buffer를 가지고 있다.
			out.flush(); // 명시적으로 내부 buffer를 비우고 데이터를 전달명령.
			out.close();
			socket.close();
			server.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
