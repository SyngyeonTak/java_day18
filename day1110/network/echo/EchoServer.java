package day1110.network.echo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class EchoServer extends JFrame {
	JTextField t_port;
	JButton bt;
	JPanel p_north;
	JTextArea area;
	JScrollPane scroll;

	ServerSocket server;
	int port = 7777;

	Thread thread;
	BufferedReader buffr;
	BufferedWriter buffw;
	
	public EchoServer() {
		t_port = new JTextField(Integer.toString(port), 10);
		bt = new JButton("서버가동");
		p_north = new JPanel();
		area = new JTextArea();
		scroll = new JScrollPane(area);

		// 조립
		p_north.add(t_port);
		p_north.add(bt);

		add(p_north, BorderLayout.NORTH);
		add(scroll);

		// 가동버튼과 리스너 연결
		bt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread = new Thread() {
					public void run() {
						startServer();
					}
				};
				thread.start();// Runnable 로 진입시키자!!!
			}
		});

		setDefaultCloseOperation(EXIT_ON_CLOSE);//윈도우 창 닫기 + 프로세스 종료
		setVisible(true);
		setBounds(600, 200, 300, 400);// x, y, width, height 순서

	}

	// 서버 가동
	public void startServer() {
		try {
			server = new ServerSocket(Integer.parseInt(t_port.getText()));// 서버 소켓 생성
			area.append("서버 준비\n");
			// (복습)
			// 서버 가동하기
			// 자바는 쓰레드 기반이므로, 지금까지 메인 실행부라 불렸던 실행주체도 사실은 시스템에 의해서 생성된 쓰레드 였다....
			// 하지만 main쓰레드는 개발자가 생성하는 일반 쓰레드와는 하는 역할 이 다르다.
			// main쓰레드는 프로그램을 운영해주는 역할, 특히 그래픽처리, 이벤트처리를 담당한다.
			// 주의사항
			// 1) main을 무한루프에 빠뜨리지 말 것
			// 2) main을 대기상태에 빠지게 하지 말 것!! (accept, read 등등)
			// 참고로 안드로이드에서는 1), 2) 시도자체를 에러로 본다!!
			// 결론) 별도의 쓰레드를 생성하여 처리하자!!
			Socket socket = server.accept();// main 실행부가 accept에 묶여 있다.
							// gui기반에서는 main쓰레드가 실행하기 때문에 조심해야 한다.
							// main실행부(쓰레드)는 무한루프, 대기상태에 빠뜨리면 안된다.
			area.append("접속자 발견\n");
			
			buffr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			listen();//듣기 시작
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//메시지 받기(청취)
	public void listen() {
		String msg = null;
		try {
			while (true) {
				msg = buffr.readLine();//현재로서는 한 번만 듣는다. client에서 입력을 해야 while문이 돌아간다.
													//따라서 메모리에 영향이 없다. (부하가 없다...)
													//read()는 입력내용이 있을 때만 동작한다.... 약간 event???
				area.append(msg+"\n");
				send(msg);
				//클라이언트에게 다시 보내야 한다.
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//클라이언트에게 메시지 보내기
	public void send(String msg) {
		try {
			buffw.write(msg+"\n");
			buffw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new EchoServer();
	}

}



























