
import java.io.*;
import java.util.ArrayList;



public class Test {

    public Test() throws Exception{
        File file=new File("D:\\A_Documents\\2_sophmore\\Java\\lab\\vocabulary.txt");
        FileReader fr=new FileReader(file);
        BufferedReader br_txt=new BufferedReader(fr);
        ArrayList<String>words=new ArrayList<String>();
        while(true){
            String str=br_txt.readLine();
            if(str==null){
                break;
            }
            //System.out.println(str);
            words.add(str);
        }
        fr.close();
        br_txt.close();

        //将arrlist类型转化为string数组
        String[] strwords=(String[]) words.toArray(new String[0]);

        //产生随机数，选择任意一个单词
        int random_index=(int)(Math.random()*strwords.length);
        String word_meaning=strwords[random_index];

        word_meaning=strwords[random_index].trim(); //剔除字符串首尾的空格
        word_meaning=word_meaning.replaceAll("\\s+"," ");    //将多个空格替换成一个空格

        String[] w_m=word_meaning.split(" ");
        String c_word=w_m[0];
        String c_mean=w_m[1];
        System.out.println(c_word);
        System.out.println(c_mean);
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



    public static void main(String[] args) throws Exception{
        Test t1=new Test();
//        String str1="apple";
//        StringBuffer str2=new StringBuffer(str1);
//        str2.setCharAt(0,'A');
//        System.out.println(str1);
//        System.out.println(str2);

//        boolean is_true=2==3;
//        String str="abc";
//        str+=is_true;
//        System.out.println(str);
        //t1.writeF("abc","D:\\test.txt");
        t1.writeF("ert","D:\\test.txt");
    }
}
