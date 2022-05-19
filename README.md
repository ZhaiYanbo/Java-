# Java-期末大作业：六级单词pk小游戏

## 1.实验要求

**题**：用Java编程开发“六级单词强化记忆”游戏

（1）在网上下载英语六级词汇表，中英文对应。保存在服务器端，服务器可以让两个客户端连入对战。两人初始分数为10分。

（1）功能1：两个客户端界面打开，开始游戏。服务器随机选出一个英文单词的中文单词描述，发给两个客户端显示。该中文单词描述，从界面顶端落下。界面底端出现该英文单词中的1-2个提示字母，客户端补齐其他字母，提交。规则：在中文单词描述掉到底端之前，先提交正确的，该客户端增加1分，对方分数不变，进入下一局；先提交但是错误的，该客户端扣2分，对方分数不变，进入下一局；两人在掉到底端前都不回答，两人都扣1分，进入下一局。进入下一局之前，回答正确的客户端显示“恭喜回答正确”；回答错误的客户端显示“回答错误，答案是XXX”；没回答的客户端显示“您没有回答，正确答案是XXX”；此界面持续10秒钟。某个用户分数扣到0分，则游戏输掉，退出。

（2）功能2：“成绩记忆”。如果一个单词，在功能1中，被用户答对，将其保存在“已掌握单词.txt”中；如果一个单词，在功能1中，答错或没有答，则保存在“未掌握单词.txt”中（标注是答错还是没答），用户可以打开复习。对于客户端，“已掌握单词.txt”和“未掌握单词.txt”，保存在本地。

## 2.实验报告

/*实验报告：
    1.目的与要求：copy
    2.硬软件环境
    3.实验内容：填表
    4.实验结果
    5.（源代码打包）
    6.自己总结总结
*/

## 3.类的设计

//1.服务器类


//2.取单词(从客户端向服务器取)

//3.写文件

//4.界面类

//考虑添加功能：1.用户可以暂停

//知识点：多线程、界面、字符串、文件……
public class Readme {
}

## 4.客户端与服务器的通信

![image](https://github.com/ZhaiYanbo/Java-/blob/main/picture/image1.png))

## 5. 通信控制逻辑

![image](https://github.com/ZhaiYanbo/Java-/blob/main/picture/image2.png))

## 6.项目组成

![image](https://github.com/ZhaiYanbo/Java-/blob/main/picture/image.png))

其中Server表示服务器类

Client表示客户端类

Test是我在测试某一子功能的时候用的类，正常运行是用不到的，只需要先运行一个Server类，再运行两个Client类即可。

## 6.附加功能

1.当有客户端上线时，服务器会显示

2.客户端可以显示对方的分数，以便知道自己与对方的答题情况
