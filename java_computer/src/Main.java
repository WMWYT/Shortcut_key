package src;

import javax.swing.JFrame;
import src.swing.Interface;

public class Main{
    public static void main(String[] args){
        //实例化一个界面的类
        Interface interface1 = new Interface();

        //当窗口关闭时退出程序
        interface1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //设置界面的大小        
        interface1.setSize(450, 700);

        //设置显示窗口
        interface1.setVisible(true);
	}
}