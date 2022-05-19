
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.StubNotFoundException;
import java.util.HashMap;
import java.util.ArrayList;

public class Server extends JFrame implements Runnable{
    private JTextArea jta=new JTextArea();
    private JTextField info=new JTextField();
    private ServerSocket ss=null;
    private Socket s=null;
    private ArrayList<String> users = new ArrayList<String>();
    private HashMap<String,ChatThread> cts = new HashMap<String,ChatThread>();
    private HashMap<String,Integer>hps =new HashMap<String,Integer>();
    private int random_index;   //选择单词的索引
    private int tip_index1,tip_index2;  //提示单词字母的索引
    String[] strwords=null;  //单词全部存放在该数组中
    String c_word=null,c_mean=null;   //选中的单词与意思
    Server() throws Exception{
        //从文件读单词
        readWords();
        this.setTitle("服务器");
        jta.setBackground(Color.YELLOW);
        this.add(jta,BorderLayout.CENTER);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(300,400);
        this.setVisible(true);
        //等待客户端的连接
        ss=new ServerSocket(9999);
        new Thread(this).start();
    }

    public void readWords() throws Exception{
        File file=new File("D:\\A_Documents\\2_sophmore\\Java\\lab\\vocabulary.txt");
        FileReader fr=new FileReader(file);
        BufferedReader br_txt=new BufferedReader(fr);
        ArrayList<String>words=new ArrayList<String>();
        while(true){
            String str=br_txt.readLine();
            if(str==null){
                break;
            }

            words.add(str);
        }
        fr.close();
        br_txt.close();

        //将arrlist类型转化为string数组
        strwords=(String[]) words.toArray(new String[0]);
    }

    @Override
    public void run() {
        while(true) {
            try {
                s = ss.accept();
                ChatThread ct=new ChatThread(s);
                ct.start();
            }catch (Exception ex){
                ex.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(this,"游戏异常退出！");
                System.exit(0);
            }
        }

    }

    //服务器自身的线程
    class ServerThread extends Thread{
        @Override
        public void run() {
            //两位玩家上线，可以开始游戏
            sendReady();
            //等待10s后开始发放单词,并初始化生命值
            try{Thread.sleep(10000);}catch (Exception e){}
            sendMark();
            try {
                while (true) {
                    sendWord();
                    //测试用
                    System.out.println("flag1");
                    //阻塞等待
                    String msg=null;
                    while (true){
                        //每50ms检查一次
                        Thread.sleep(50);
                        msg=info.getText();
                        if(!msg.isEmpty())
                            break;
                    }
                    info.setText("");
                    String[] msgs=msg.split(":");
                    //测试用
                    System.out.println(msgs);
                    //得到消息进行处理
                    if (msgs[0].equals("$SUBMIT$")) {
                        System.out.println("flag2");
                        if (msgs[2].equals("true")) {
                            for (String nk : users) {
                                //回答正确，加一分并发送正确消息
                                if (msgs[1].equals(nk)) {
                                    hps.replace(nk, hps.get(nk) + 1);
                                    cts.get(nk).ps.println("#RES#:true");
                                } else {
                                    //没有回答
                                    cts.get(nk).ps.println("#RES#:ntd");
                                }
                            }
                        } else {
                            for (String nk : users) {
                                //回答错误，减两分并发送错误消息
                                if (msgs[1].equals(nk)) {
                                    hps.replace(nk, hps.get(nk) - 2);
                                    cts.get(nk).ps.println("#RES#:false");
                                } else {
                                    //没有回答
                                    cts.get(nk).ps.println("#RES#:ntd");
                                }
                            }
                        }
                        sendMark();
                    } else if (msgs[0].equals("$BOTTOM$")) {
                        //测试用
                        System.out.println("flag3");
                        String first_user = users.get(0);
                        //只处理一个玩家的bottom消息，避免重复处理
                        if (msgs[1].equals(first_user)) {
                            //每位玩家都扣一分
                            for (String nk : users) {
                                hps.replace(nk, hps.get(nk) - 1);
                                cts.get(nk).ps.println("#RES#:ntd");
                                //测试用
                                System.out.println("send end ok");
                            }
                            sendMark();
                        }
                    }
                    //每一次回答或者掉落到底部后判断是否游戏结束
                    if (checkEnd()) {
                        sendEnd();
                        break;
                    }

                }
            }catch (Exception ex){}
        }

        //发送就绪
        public void sendReady(){
            for(String nk : users){
                ChatThread ct = cts.get(nk);
                //两位玩家已经上线，PK即将开始……
                ct.ps.println("#READY#");
            }
        }

        //发送分数
        public void sendMark(){
            String mark_str="#MARK#";
            for(String nk : users){
                mark_str+=":"+nk+":"+hps.get(nk);
            }
            for(String nk : users){
                ChatThread ct = cts.get(nk);
                ct.ps.println(mark_str);
            }
        }

        //发送单词
        public void sendWord(){
            //产生随机数，选择任意一个单词
            random_index=(int)(Math.random()*strwords.length);
            String word_meaning=strwords[random_index];
            word_meaning=strwords[random_index].trim(); //剔除字符串首尾的空格
            word_meaning=word_meaning.replaceAll("\\s+"," ");    //将多个空格替换成一个空格
            String[] w_m=word_meaning.split(" ");
            c_word=w_m[0];
            c_mean=w_m[1];
            int word_len=c_word.length();
            //单词长度大于5，提示两个字母
            if(word_len>5){
                tip_index1=(int)(word_len*Math.random());
                do {
                    tip_index2 = (int) (word_len * Math.random());
                }while(tip_index2==tip_index1);
                for(String nk : users){
                    ChatThread ct = cts.get(nk);
                    ct.ps.println("#WORD#:"+c_word+":"+c_mean+":"+2+":"+tip_index1+":"+tip_index2);
                }
            }else{
                tip_index1=(int)(word_len*Math.random());
                for(String nk : users){
                    ChatThread ct = cts.get(nk);
                    ct.ps.println("#WORD#:"+c_word+":"+c_mean+":"+1+":"+tip_index1+":"+-1);
                }
            }
            System.out.println("发送了一个单词");
        }

        //判断游戏是否结束
        public boolean checkEnd(){
            for(String nk:users){
                if(hps.get(nk)<=0)  return true;
            }
            return false;
        }

        //向客户端发送结束信息
        public void sendEnd(){
            for(String nk:users){
                if(hps.get(nk)<=0)  cts.get(nk).ps.println("#END#:winner");
                else                cts.get(nk).ps.println("#END#:looser");
            }
        }
    }

    //为每个客户端建立一个线程
    class ChatThread extends Thread{
        private Socket s=null;
        private PrintStream ps=null;
        private BufferedReader br=null;
        private String nickName=null;

        public ChatThread(Socket s) throws Exception{
            this.s=s;
            ps=new PrintStream(s.getOutputStream());
            br=new BufferedReader(new InputStreamReader(s.getInputStream()));
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String msg = br.readLine();
                    String[] msgs = msg.split(":");
                    //得到消息进行处理
                    //有玩家登录
                    if (msgs[0].equals("$LOGIN$")) {
                        nickName = msgs[1];
                        users.add(nickName);
                        cts.put(nickName, this);
                        hps.put(nickName, 10);
                        jta.append("客户端" + nickName + "上线了\n");
                        if (users.size() == 1) {
                            //只有一个玩家上线，向他发送等待消息
                            sendWait();
                        } else if (users.size() == 2) {

                            new ServerThread().start();
                        }
                    }
                    else{
                        //测试用
                        System.out.println("向info中填入："+info);
                        info.setText(msg);
                    }

                }catch(Exception ex) {}
            }
        }
        //发送等待
        public void sendWait(){
            for(String nk : users){
                ChatThread ct = cts.get(nk);
                //正在等待另一位玩家上线……
                ct.ps.println("#WAIT#");
            }
        }

    }


    public static void main(String[] args) throws Exception{
        new Server();
    }
}
