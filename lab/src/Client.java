
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.io.PrintStream;
import java.net.Socket;

public class Client extends JFrame implements Runnable, ActionListener {
    private Socket s=null;
    private String nickName=null;
    private BufferedReader br=null;
    private PrintStream ps=null;
    private String c_word=null,c_mean=null;
    private int myMark,yourMark;

    private JLabel jlb=new JLabel();
    private JLabel hp_value1=null;
    private JLabel hp_value2=null;
    private JTextArea jta =null;
    private JLabel word_tip=null;
    private JTextField word_input=null;

    private MoveThread mt=null;
    private int X=200,Y=0;
    //构造函数
    public Client() throws Exception{
        nickName=JOptionPane.showInputDialog("请输入昵称：");
        this.setTitle("六级单词PK游戏——"+"(客户端:"+nickName+")");
        jlb.setFont(new Font("黑体",Font.BOLD,20));
        jlb.setForeground(Color.yellow);
        jlb.setSize(200,50);
        jlb.setLocation(X,Y);


        mt=new MoveThread();
        mt.RUN=false;

        this.add(createPN(),BorderLayout.NORTH);
        this.add(createPC(),BorderLayout.CENTER);
        this.add(createPS(),BorderLayout.SOUTH);

        hp_value1.setFont(new Font("黑体",Font.BOLD,20));
        hp_value2.setFont(new Font("黑体",Font.BOLD,20));
        hp_value2.setForeground(Color.red);
        word_tip.setFont(new Font("黑体",Font.BOLD,20));
        word_tip.setForeground(Color.blue);
        word_input.setFont(new Font("黑体",Font.BOLD,20));
        word_tip.setForeground(Color.green);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(800,400);
        this.setVisible(true);
        word_input.addActionListener(this);

        s=new Socket("127.0.0.1",9999);
        br=new BufferedReader(new InputStreamReader(s.getInputStream()));
        ps=new PrintStream(s.getOutputStream());
        ps.println("$LOGIN$:"+nickName);    //登录信息
        new Thread(this).start();

    }

    //北边：显示生命值
    public JPanel createPN(){
        JPanel jpn=new JPanel();
        jpn.setLayout(new GridLayout(1,4));
        JLabel hp_msg1=new JLabel("对方生命值:");
        JLabel hp_msg2=new JLabel("您的生命值:");
        hp_value1=new JLabel(" ");
        hp_value2=new JLabel(" ");
        jpn.add(hp_msg1);
        jpn.add(hp_value1);
        jpn.add(hp_msg2);
        jpn.add(hp_value2);
        return jpn;
    }

    //中间：一个文本框
    public JPanel createPC(){
        JPanel jp=new JPanel();
        jta=new JTextArea();
        jp.add(jta,BorderLayout.CENTER);
        jp.add(jlb);
        return jp;
    }

    //南边：一个单词提示一个单词输入
    public JPanel createPS(){
        JPanel jp=new JPanel();
        jp.setLayout(new GridLayout(2,2));
        JLabel w_tip=new JLabel("单词提示");
        word_tip=new JLabel();
        JLabel w_input=new JLabel("单词输入");
        word_input=new JTextField();
        jp.add(w_tip);
        jp.add(word_tip);
        jp.add(w_input);
        jp.add(word_input);
        return jp;
    }

    //写文件
    public void writeF(String str,String path){
        try {
            //如果路径文件不存在，则创建文件
            File f=new File(path);
            if(!f.exists()){
                boolean res = f.createNewFile();
                if (!res) System.out.println("文件创建失败");
            }
            FileWriter fw=new FileWriter(f,true);
            BufferedWriter br=new BufferedWriter(fw,1024);
//            PrintStream ps=new PrintStream(f);
            br.write(str+'\n');
            br.flush();
            br.close();
        }catch (Exception ex){}
    }

    @Override
    public void run() {
        while(true){
            try{
                String msg=br.readLine();
                //对传来的消息进行处理
                //1.等待另一位玩家上线
                if(msg.equals("#WAIT#")){
                    jta.append("正在等待另一位玩家上线……");
                }
                //2.两位玩家均已上线
                else if(msg.equals("#READY#")){
                    jta.setText("");
                    jta.append("两位玩家均已上线，游戏即将开始……\n");
                    jta.append("游戏规则如下：\n" +
                            "1.游戏开始后，会有一个六级单词的中文单词描述从页面顶端落下\n"+
                            "2.页面底端会出现该英文单词的1-2个提示字母\n"+
                            "3.您需要在页面底端的文本框中根据中文单词描述与字幕提示输入该单词并回车确定\n"+
                            "4.在中文单词描述掉到底端之前，您与对手先提交正确的增加1分，另一方分数不变，进入下一局;先提交但是错误的扣2分，另一方不变，\n进入下一局;两人在掉到底端前都不回答，两人都扣1分，进入下一局\n"+
                            "5.进入下一局之前，回答正确的客户端显示“恭喜回答正确”；回答错误的客户端显示“回答错误，答案是XXX”；没回答的客户端显示“您没有\n回答，正确答案是XXX”；此界面持续10秒钟\n"+
                            "6.玩家初始分数为10分，待一位玩家的分数扣到0分，该玩家输掉，另一位玩家胜利\n"+
                            "7.您在对战过程中遇到的单词会根据您的回答情况存到本地的文件中，方便您后续复习。");
                }
                //拿到单词
                else if(msg.startsWith("#WORD#")){
                    System.out.println("我拿到了单词");
                    // mean拿出来放到掉落的jlb中
                    //word替换下划线后放到底部jlb中
                    String[] msgs=msg.split(":");
                    c_word=msgs[1];
                    c_mean=msgs[2];
                    StringBuffer c_tip=new StringBuffer(c_word);
                    int index1=Integer.parseInt(msgs[4]);
                    int index2=Integer.parseInt(msgs[5]);
                    for(int i=0;i<c_tip.length();i++) {
                        //除了index1和index2不替换成下划线，其余都替换
                        if (i == index1 ||i==index2) continue;
                        c_tip.setCharAt(i,'_');
                    }

                    jta.setText("");
                    word_tip.setText(c_tip.toString());
                    word_input.setText("");
                    jlb.setText(c_mean);
                    Y=0;
                    jlb.setLocation(X,Y);
                    mt.RUN=true;
                    //新开辟一个线程
                    mt=new MoveThread();
                    mt.start();
                    }
                //拿到分数
                else if(msg.startsWith("#MARK#")){
                    String[] msgs=msg.split(":");
                    String username1=msgs[1];
                    String username2=msgs[3];
                    if(username1.equals(nickName)){
                        myMark=Integer.parseInt(msgs[2]);
                        yourMark=Integer.parseInt(msgs[4]);
                    }else{
                        myMark=Integer.parseInt(msgs[4]);
                        yourMark=Integer.parseInt(msgs[2]);
                    }
                    //将生命值放入对应的JLabel中
                    hp_value1.setText(Integer.toString(yourMark));
                    hp_value2.setText(Integer.toString(myMark));
                }
                //拿到本次答题结果
                else if(msg.startsWith("#RES#")){
                    mt.RUN=false;
                    String[] msgs=msg.split(":");
                    if(msgs[1].equals("true")){
                        jta.setText("恭喜回答正确!\n(十秒后进行下一轮回答，请您耐心等待……)");
                        writeF(c_mean+"  "+c_word,"D:\\A_Documents\\2_sophmore\\Java\\lab\\"+nickName+"已掌握单词.txt");
                    }else if(msgs[1].equals("false")){
                        jta.setText("回答错误，答案是——"+c_word+"  "+c_mean+"\n(十秒后进行下一轮回答，请您耐心等待……)");
                        writeF(c_mean+"  "+c_word+"(回答错误)","D:\\A_Documents\\2_sophmore\\Java\\lab\\"+nickName+"未掌握单词.txt");
                    }else{
                        jta.setText("您没有回答，正确答案是——"+c_word+"  "+c_mean+"\n(十秒后进行下一轮回答，请您耐心等待……)");
                        writeF(c_mean+"  "+c_word+"(未回答)","D:\\A_Documents\\2_sophmore\\Java\\lab\\"+nickName+"未掌握单词.txt");
                    }
                    Thread.sleep(10000);

                }
                //拿到游戏结束信息
                else if(msg.startsWith("#END#")){
                    mt.RUN=false;
                    String[] msgs=msg.split(":");
                    if(msgs[1].equals("winner")){
                        JOptionPane.showMessageDialog(null,"对方已没有生命值，恭喜您赢得比赛！");
                    }else{
                        JOptionPane.showMessageDialog(null,"您没有生命值，很遗憾输掉比赛！");
                    }
                    Thread.sleep(10000);
                    System.exit(0);
                }

            }catch (Exception ex){}
        }
    }

    @Override
    //向服务器提交单词
    public void actionPerformed(ActionEvent e) {
        mt.RUN=false;
        String user_submit=word_input.getText();
        boolean is_true=user_submit.equals(c_word);
        ps.println("$SUBMIT$:"+nickName+":"+is_true);
        //测试用
        System.out.println(nickName+"submit_ok"+user_submit);

    }

    //控制单词掉落的类
    class MoveThread extends Thread {
        boolean RUN = true;
        public void run() {
            while(true) {
                while (RUN) {
                    try {
                        Thread.sleep(150);
                        Y+=3;
                        jlb.setLocation(X, Y);
                        if (Y >= 350) {
                            ps.println("$BOTTOM$:" + nickName);
//                            Y = 0;
//                            jlb.setLocation(X, Y);
                            System.out.println(nickName + "bottom_ok");
                            mt.RUN=false;
                        }
                    } catch (Exception ex) {
                    }
                }
            }
        }

    }

    public static void main(String[] args) throws Exception{
        new Client();
    }

}
