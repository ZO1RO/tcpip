
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.util.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;

import javax.swing.*;


public class Server {
    //用于保存Socket的集合，也可以说是把个C端与S端的一个连接通道保存起来
    //作用：服务器将接收到的信息发给集合里的所以socket，也就是发给每个C端
    public static List<Socket> list = new ArrayList<Socket>();

    public static void main(String[] args) {
        new ServerFrame("服务器端");
    }
}

/*
 * 继承Jframe类表示该类可以创建一个窗口程序了，
 * 实现ActionListener：动作监听，在S端点击“启动服务器”是要执行的代码
 * 实现Runnable：实现多线程，该窗口是个客户端窗口，要开启一个线程接收显示服务器发过来的信息
 */
class ServerFrame extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private ServerSocket serverSocket = null;

    private Socket socket;

    String uname = "";
    String pname = "";
    JPanel login;
    JTextField username;
    JButton ok;
    private JTextArea message;

    public ServerFrame(String name) {
        super(name);
        
        //用一个死循环一直让S端开启接收C端的连接，将C端的IP和端口显示在面板上
        //如果用循环的话就只能接收一次
        
        login = new JPanel();
        username = new JTextField(20);
        ok = new JButton("确定");
        ok.addActionListener(this);
        JLabel label = new JLabel("对方ip地址\n");
        login.add(label);
        login.add(username);
        login.add(ok);
        message = new JTextArea();
        message.setEditable(false);
        login.add(message, "Center");

        this.add(login);
        this.setResizable(false);
        this.setBounds(400, 100, 400, 300);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        while(true){
            String address = null;
            int pot = 0;
            try {
                serverSocket = new ServerSocket(6666);
                socket = serverSocket.accept();
                Server.list.add(socket);
                address = socket.getInetAddress().getHostAddress();
                pot = socket.getPort();
                //message.append("\r\nip/" + address + ":" + pot + "\t上线了");

                new sendThread(socket).start();
            } catch (IOException e1) {
                //e1.printStackTrace();
            }    
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ok) {
            uname = username.getText();
            // 如果点击的按钮是开始按钮。则启动服务器。
            new ClientFrame(uname);
        }
    }

}

/*
 * 接收每个C端的信息并向每个C端发送接收到的信息
 */
class sendThread extends Thread {
    private Socket socket;

    public sendThread(Socket socket) {
        super();
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream is = null;
        BufferedReader br = null;
        String str = null;
        OutputStream os = null;
        BufferedWriter bw = null;
        while (true) {
            try {
                is = socket.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                str = br.readLine();
                for (Socket s : Server.list) {
                    os = s.getOutputStream();
                    bw = new BufferedWriter(new OutputStreamWriter(os));
                    bw.write(str);
                    bw.newLine();
                    bw.flush();
                }
            } catch (IOException e) {
                //如果断开连接则移除对于的socket
                Server.list.remove(socket);
            }
        }
    }
}

class ClientFrame extends JFrame implements ActionListener, Runnable {

    private static final long serialVersionUID = 1L;


    private Socket socket;
    OutputStream os = null;
    BufferedWriter bw = null;
    
    JTextArea message;
    JScrollPane scroll;
    JTextField input;
    JButton submit;
    JPanel panel;

    String uname = "";
    // 聊天页面
    JPanel chat;

    public ClientFrame(String name) {
        super(name);
        JLabel label = new JLabel("正在和"+name+"聊天\n");
        message = new JTextArea();
        message.setEditable(false);
        scroll = new JScrollPane(message);
        input = new JTextField(20);
        submit = new JButton("发送");
        panel = new JPanel();

        panel.add(input);
        panel.add(submit);
        chat = new JPanel(new BorderLayout());
        chat.add(label);
        chat.add(scroll, "Center");
        chat.add(panel, "South");
              
        /*    在创建C端窗体的时候也应该开启一个线程接收显示来自服务器的信息
         *     为什么要开启一个线程呢？因为在这个窗体里既要实现消息的发送，
         *     也要接收信息，而且两个不能按顺序进行，也互不干扰，所以开启一个线程时时刻刻等着S端发来的信息
         */
        //接收信息
        try {
            //127.0.0.1表示本机地址，地址好端口都可以改，这要看服务器那边是什么地址和端口
            socket = new Socket("127.0.0.1", 6666);
            os = socket.getOutputStream();
            bw = new BufferedWriter(new OutputStreamWriter(os));
            
        } catch (IOException e1) {
            //e1.printStackTrace();
        }

        new Thread(this).start();
        
        //提交按钮工作监听器，点击提交时触发
        submit.addActionListener(this);

        this.add(chat);

        this.setResizable(false);
        this.setBounds(400, 100, 400, 300);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submit) {
            // 如果点击提交按钮，则表示需要将信息发送出去。
            String str = null;
            //以下三行是创建发送时间的代码
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            String dateStr = format.format(date);
            
            str = uname+" "+dateStr+"  "+input.getText();
            
            try {
                bw.write(str);
                bw.newLine();
                bw.flush();
            } catch (IOException e1) {
                System.out.println("无法发送，服务器连接异常！");
            }            
        } else {
                chat.setVisible(true);
                this.setTitle(uname + " 的客户端");
                this.add(chat);
            }
        }
    
    //run方法里面的是接受S端信息和将信息显示的代码
    @Override
    public void run() {
        BufferedReader br = null;
        InputStream is = null;
        String str = null;
        try {
            is = socket.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            while (true) {
                str = br.readLine()+"\r\n";
                message.append(str);
            }
        } catch (IOException e) {
            System.out.println("无法发送，服务器连接异常！");
        }
}
}
