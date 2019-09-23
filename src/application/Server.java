package application;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server {
	public static void main(String[] args) {
		// ������ �����׷��� Ŭ���̾�Ʈ�� ���� ������ ��ٷ��� �Ѵ�.
		// ServerSocket class�� �̿��ؼ� ����� ����
		ServerSocket server = null;
		// Ŭ���̾�Ʈ�� ���ӵ� �� Socket ��ü�� �־���� Ŭ���̾�Ʈ��
		// ������ ����� ����
		Socket socket = null;
		try {
			// port��ȣ�� ������  ServerSocket��ü�� ����
			// port��ȣ�� 0~65535 ��밡��. 0~1023������ ����Ǿ� �ִ�.
			server = new ServerSocket(5554);
			System.out.println("Ŭ���̾�Ʈ ���� ���");
			socket = server.accept(); // Ŭ���̾�Ʈ ������ ��ٸ���.(block)
			// ���࿡ ���ӿ� �����ϸ� socket��ü�� �ϳ� ȹ��
			PrintWriter out =
					new PrintWriter(socket.getOutputStream());
			SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
			out.println(format.format(new Date()));
			// �Ϲ������� Reader�� Write�� ���� buffer�� ������ �ִ�.
			out.flush(); // ��������� ���� buffer�� ���� �����͸� ���޸��.
			out.close();
			socket.close();
			server.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
