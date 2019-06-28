package socket;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.*;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class Client{

        private boolean isConnected = false;
        private Socket socket;
        
        //读写接口
        private PrintWriter writer;
        private BufferedReader reader;
        
        // 负责接收消息的线程
        private MessageThread messageThread;

        //按钮定义
        private JButton btn_start;
        private JButton btn_stop;
        private JButton btn_send;
        private JButton btn_sendone;
        
        //窗体定义
        private JFrame frame;
        private JTextArea textArea;
        private JTextField textField;
        private JTextField txt_port;
        private JTextField txt_hostIp;
        private JTextField txt_name;
        private JPanel nPanel;
        private JPanel sPanel;
        private JScrollPane rScroll;
        private JScrollPane lScroll;
        private JSplitPane centerSplit;

        //用户列表信息
        private JList userList;
        private DefaultListModel listModel;
        
        private String user;

        private Map<String, User> onLineUsers = new HashMap<String, User>();// 所有在线用户

        // 主方法,程序入口
        
        public static void main(String[] args) {
                new Client();
        }
        
        // 执行发送(公聊)
        
        public void send() {
        		String users = "所有人";
                if (!isConnected) {
                        JOptionPane.showMessageDialog(frame, "还没有连接服务器，无法发送消息！", "错误",
                                        JOptionPane.ERROR_MESSAGE);
                        return;
                }
                String message = textField.getText().trim();
                if (message == null || message.equals("")) {
                        JOptionPane.showMessageDialog(frame, "消息不能为空！", "错误",
                                        JOptionPane.ERROR_MESSAGE);
                        return;
                }
                
                sendMessage(frame.getTitle() + "@" + "ALL" + "@" + users + "@" + message);
                textField.setText(null);
        }

        //执行私聊

        public void sendone(String user) {
                if (!isConnected) {
                        JOptionPane.showMessageDialog(frame, "还没有连接服务器，无法发送消息！", "错误",
                                        JOptionPane.ERROR_MESSAGE);
                        return;
                }
                String message = textField.getText().trim();
                if (message == null || message.equals("")) {
                        JOptionPane.showMessageDialog(frame, "消息不能为空！", "错误",
                                        JOptionPane.ERROR_MESSAGE);
                        return;
                }
                if(user == null) {
                	JOptionPane.showMessageDialog(frame, "未选中用户！", "错误",
                            JOptionPane.ERROR_MESSAGE);
                	return;
                }
                sendMessage(frame.getTitle() + "@" + "ALLP" + "@" + user + "@" + message);
                textField.setText(null);
        }

        // 构造方法
        
        public Client() {
                textArea = new JTextArea();
                textArea.setEditable(false);
                textArea.setForeground(Color.blue);
                textField = new JTextField();
                txt_port = new JTextField("10010");
                txt_hostIp = new JTextField("127.0.0.1");
                txt_name = new JTextField("james");
                btn_start = new JButton("连接");
                btn_stop = new JButton("断开");
                btn_send = new JButton("群聊");
                btn_sendone = new JButton("私聊");
                listModel = new DefaultListModel();
                userList = new JList(listModel);

                nPanel = new JPanel();
                nPanel.setLayout(new GridLayout(1, 7));
                nPanel.add(new JLabel("端口号"));
                nPanel.add(txt_port);
                nPanel.add(new JLabel("服务器IP地址"));
                nPanel.add(txt_hostIp);
                nPanel.add(new JLabel("昵称"));
                nPanel.add(txt_name);
                nPanel.add(btn_start);
                nPanel.add(btn_stop);
                nPanel.setBorder(new TitledBorder("连接配置信息"));

                rScroll = new JScrollPane(userList);
                rScroll.setBorder(new TitledBorder("在线用户"));
                lScroll = new JScrollPane(textArea);
                lScroll.setBorder(new TitledBorder("消息记录"));
                sPanel = new JPanel(new BorderLayout());
                sPanel.add(textField, "Center");
                sPanel.add(btn_send, "East");
                sPanel.add(btn_sendone, "South");
                sPanel.setBorder(new TitledBorder("写消息"));

                centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, lScroll,
                                rScroll);
                centerSplit.setDividerLocation(450);

                frame = new JFrame("客户端");
                frame.setLayout(new BorderLayout());
                frame.add(nPanel, "North");
                frame.add(centerSplit, "Center");
                frame.add(sPanel, "South");
                frame.setSize(600, 400);
                int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
                int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
                frame.setLocation((screen_width - frame.getWidth()) / 2,
                                (screen_height - frame.getHeight()) / 2);
                frame.setVisible(true);

                // 写消息的文本框中按回车键时事件
                textField.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent arg0) {
                                send();
                        }
                });
                
                //建立用户私聊监听事件
                userList.addMouseListener(new MouseAdapter() {  
                    @Override  
                    public void mouseClicked(MouseEvent e) {  
                        if(e.getClickCount() == 1){   
                            JList userList = (JList) e.getSource();  
                            int index = userList.getSelectedIndex();    //已选项的下标  
                            Object obj = userList.getModel().getElementAt(index);  //取出数据  
                            //System.out.println(obj.toString()); 
                            user = obj.toString();
                        }  
                    }  
                });  
                
                // 单击发送按钮时事件
                btn_send.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                                send();
                        }
                });

                // 单击私聊按钮时事件
                btn_sendone.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                                sendone(user);
                        }
                });

                // 单击连接按钮时事件
                btn_start.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                                int port;
                                if (isConnected) {
                                        JOptionPane.showMessageDialog(frame, "账号已登陆!",
                                                        "错误", JOptionPane.ERROR_MESSAGE);
                                        return;
                                }
                                try {
                                        try {
                                                port = Integer.parseInt(txt_port.getText().trim());
                                        } catch (NumberFormatException e2) {
                                                throw new Exception("端口号不符合要求!端口为整数!");
                                        }
                                        String hostIp = txt_hostIp.getText().trim();
                                        String name = txt_name.getText().trim();
                                        if (name.equals("") || hostIp.equals("")) {
                                                throw new Exception("姓名、服务器IP不能为空!");
                                        }
                                        boolean flag = connectServer(port, hostIp, name);
                                        if (flag == false) {
                                                throw new Exception("进入聊天室失败!");
                                        }
                                        frame.setTitle(name);
                                        JOptionPane.showMessageDialog(frame, "进入聊天室成功!");
                                } catch (Exception exc) {
                                        JOptionPane.showMessageDialog(frame, exc.getMessage(),
                                                        "错误", JOptionPane.ERROR_MESSAGE);
                                }
                        }
                });

                // 单击断开按钮时事件
                btn_stop.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                                if (!isConnected) {
                                        JOptionPane.showMessageDialog(frame, "已处于断开状态，不要重复断开!",
                                                        "错误", JOptionPane.ERROR_MESSAGE);
                                        return;
                                }
                                try {
                                        boolean flag = closeConnection();// 断开连接
                                        if (flag == false) {
                                                throw new Exception("断开连接发生异常！");
                                        }
                                        JOptionPane.showMessageDialog(frame, "成功断开!");
                                } catch (Exception exc) {
                                        JOptionPane.showMessageDialog(frame, exc.getMessage(),
                                                        "错误", JOptionPane.ERROR_MESSAGE);
                                }
                        }
                });

                // 关闭窗口时事件
                frame.addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent e) {
                                if (isConnected) {
                                        closeConnection();// 关闭连接
                                }
                                System.exit(0);// 退出程序
                        }
                });
        }

        /**
         * 连接服务器
         * 
         */
        public boolean connectServer(int port, String hostIp, String name) {
                // 连接服务器
                try {
                        socket = new Socket(hostIp, port);// 根据端口号和服务器ip建立连接
                        writer = new PrintWriter(socket.getOutputStream());
                        reader = new BufferedReader(new InputStreamReader(socket
                                        .getInputStream()));
                        // 发送客户端用户基本信息(用户名和ip地址)
                        sendMessage(name + "@" + socket.getLocalAddress().toString());
                        // 开启接收消息的线程
                        messageThread = new MessageThread(reader, textArea);
                        messageThread.start();
                        isConnected = true;// 已经连接上了
                        return true;
                } catch (Exception e) {
                        textArea.append("与端口号为：" + port + "    IP地址为：" + hostIp
                                        + "   的服务器连接失败!" + "\r\n");
                        isConnected = false;// 未连接上
                        return false;
                }
        }

        /**
         * 发送消息
         * 
         */
        public void sendMessage(String message) {
                writer.println(message);
                writer.flush();
        }

        /**
         * 客户端主动关闭连接
         */
        @SuppressWarnings("deprecation")
        public synchronized boolean closeConnection() {
                try {
                        sendMessage("CLOSE");// 发送断开连接命令给服务器
                        messageThread.stop();// 停止接受消息线程
                        // 释放资源
                        if (reader != null) {
                                reader.close();
                        }
                        if (writer != null) {
                                writer.close();
                        }
                        if (socket != null) {
                                socket.close();
                        }
                        isConnected = false;
                        return true;
                } catch (IOException e1) {
                        e1.printStackTrace();
                        isConnected = true;
                        return false;
                }
        }

        // 不断接收消息的线程
        class MessageThread extends Thread {
                private BufferedReader reader;
                private JTextArea textArea;

                // 接收消息线程的构造方法
                public MessageThread(BufferedReader reader, JTextArea textArea) {
                        this.reader = reader;
                        this.textArea = textArea;
                }

                // 被动的关闭连接
                public synchronized void closeCon() throws Exception {
                        // 清空用户列表
                        listModel.removeAllElements();
                        // 被动的关闭连接释放资源
                        if (reader != null) {
                                reader.close();
                        }
                        if (writer != null) {
                                writer.close();
                        }
                        if (socket != null) {
                                socket.close();
                        }
                        isConnected = false;// 修改状态为断开
                }

                public void run() {
                        String message = "";
                        while (true) {
                                try {
                                        message = reader.readLine();
                                        StringTokenizer stringTokenizer = new StringTokenizer(
                                                        message, "/@");
                                        String command = stringTokenizer.nextToken();// 命令
                                        if (command.equals("CLOSE"))// 服务器已关闭命令
                                        {
                                                textArea.append("服务器已关闭!\r\n");
                                                closeCon();// 被动的关闭连接
                                                return;// 结束线程
                                        } else if (command.equals("ADD")) {// 有用户上线更新在线列表
                                                String username = "";
                                                String userIp = "";
                                                if ((username = stringTokenizer.nextToken()) != null
                                                                && (userIp = stringTokenizer.nextToken()) != null) {
                                                        User user = new User(username, userIp);
                                                        onLineUsers.put(username, user);
                                                        listModel.addElement(username);
                                                }
                                        } else if (command.equals("DELETE")) {// 有用户下线更新在线列表
                                                String username = stringTokenizer.nextToken();
                                                User user = (User) onLineUsers.get(username);
                                                onLineUsers.remove(user);
                                                listModel.removeElement(username);
                                        } else if (command.equals("USERLIST")) {// 加载在线用户列表
                                                int size = Integer.parseInt(stringTokenizer.nextToken());
                                                String username = null;
                                                String userIp = null;
                                                for (int i = 0; i < size; i++) {
                                                        username = stringTokenizer.nextToken();
                                                        userIp = stringTokenizer.nextToken();
                                                        User user = new User(username, userIp);
                                                        onLineUsers.put(username, user);
                                                        listModel.addElement(username);
                                                }
                                        } else if (command.equals("MAX")) {// 人数已达上限
                                                textArea.append(stringTokenizer.nextToken()
                                                                + stringTokenizer.nextToken() + "\r\n");
                                                closeCon();// 被动的关闭连接
                                                JOptionPane.showMessageDialog(frame, "服务器缓冲区已满！", "错误",
                                                                JOptionPane.ERROR_MESSAGE);
                                                return;// 结束线程
                                        } else {// 普通消息
                                                textArea.append(message + "\r\n");
                                        }
                                } catch (IOException e) {
                                        e.printStackTrace();
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }
                }
        }
}