package src.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import src.rxtx.port_operation;

class My_Shortcut_Key{
    public char id;
    public JTextField key_0 = new JTextField(5);
    public JTextField key_1 = new JTextField(5);
    public JTextField key_2 = new JTextField(5);
    public String str;

    public String getStr(){
        return str = String.valueOf(this.id) + ":" + this.key_0.getText() + ":" + this.key_1.getText() + ":" + this.key_2.getText() + ":";
    }

    public class Action_Listener_Self_upload implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent arg0) {
            try {
                port_operation.out.write(getStr().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public My_Shortcut_Key(char id, JPanel root, NodeList childNodes){
        this.id = id;

        JLabel userLabel = new JLabel(String.valueOf(id - 'a' + 1));
        userLabel.setBounds(10, 10 + (id - 'a') * 40, 50, 30);
        root.add(userLabel);

        key_0.setBounds(30, 10 + (id - 'a') * 40, 50, 30);
        if (childNodes.item(1).getFirstChild() != null )
            key_0.setText(childNodes.item(1).getFirstChild().getNodeValue());
        root.add(key_0);


        key_1.setBounds(120, 10 + (id - 'a') * 40, 50, 30);
        if (childNodes.item(3).getFirstChild() != null )
            key_1.setText(childNodes.item(3).getFirstChild().getNodeValue());
        root.add(key_1);
        
        key_2.setBounds(210, 10 + (id - 'a') * 40, 50, 30);
        if (childNodes.item(5).getFirstChild() != null )
            key_2.setText(childNodes.item(5).getFirstChild().getNodeValue());
        root.add(key_2);

        JButton self_upload = new JButton("↑");
        self_upload.setBounds(270, 10 + (id - 'a') * 40, 50, 30);
        self_upload.addActionListener(new Action_Listener_Self_upload());
        root.add(self_upload);
    }
}

public class Interface extends JFrame{
    My_Shortcut_Key[] SK = new My_Shortcut_Key[16];
    NodeList childNodes;
    DocumentBuilderFactory factory;
    DocumentBuilder builder;
    Document doc;
    NodeList bookList;
    Node book, attr;
    NamedNodeMap attrs;
    File file = new File("src/swing/Shortcut_key.xml");
    
    public class Key_Writer implements Runnable{
        public void run ()
        {
            try
            {
                for(int i = 0; i < 16; i++){
                    port_operation.out.write(SK[i].getStr().getBytes());
                    Thread.sleep(2000);
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("上传完成");
        }
    }

    public class Action_Listener_on implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent arg0) {
            (new Thread(new Key_Writer())).start();
        }
    }

    public class Action_Listener_save implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent arg0) {
            for(int i = 0; i < 16; i++){
                book = doc.getElementsByTagName("key_name").item(i);
                Element studentElement = (Element) book;

                Element key_0 = (Element) studentElement.getElementsByTagName("key_0").item(0);
                Element key_1 = (Element) studentElement.getElementsByTagName("key_1").item(0);
                Element key_2 = (Element) studentElement.getElementsByTagName("key_2").item(0);
            
                key_0.setTextContent(SK[i].key_0.getText());
                key_1.setTextContent(SK[i].key_1.getText());
                key_2.setTextContent(SK[i].key_2.getText());

                try {
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource domSource = new DOMSource(doc);
                    StreamResult reStreamResult = new StreamResult(file);
                    transformer.transform(domSource, reStreamResult);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class Action_Listener_off implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent arg0) {
            try {
                port_operation.out.write("clear\n".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class Action_Listener_btn_open implements ActionListener{
        JComboBox<String> port;
        
        public Action_Listener_btn_open(JComboBox<String> btnChoice){
            this.port = btnChoice;
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            port_operation.read_port((String) port.getSelectedItem());
        }
    }

    public Interface(){
        super("快捷键控制器");

        JPanel root = new JPanel();
        this.setContentPane(root); 
        root.setLayout(null);

        port_operation.port_scanner();

        JButton B_upload = new JButton("统一上传");
        B_upload.setBounds(330, 10, 100, 30);
        B_upload.addActionListener(new Action_Listener_on());

        JButton B_save = new JButton("保存");
        B_save.setBounds(330, 50, 100, 30);
        B_save.addActionListener(new Action_Listener_save());

        JButton B_clear = new JButton("清空快捷键");
        B_clear.setBounds(330, 90, 100, 30);
        B_clear.addActionListener(new Action_Listener_off());
        
        JComboBox<String> btnChoice = new JComboBox<>();
        for (String name : port_operation.array) { // 遍历返回的.java文件名
            btnChoice.addItem(name); // 把文件名添加到下拉列表中
        }
        
        btnChoice.setBounds(330, 130, 100, 30);

        JButton btnOpen = new JButton("打开串口");
        btnOpen.setBounds(330, 170, 100, 30);
        btnOpen.addActionListener(new Action_Listener_btn_open(btnChoice));

        try{
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            doc = builder.parse(file);

            bookList = doc.getElementsByTagName("key_name");

            for (int i = 0; i < bookList.getLength(); i++){
                book = bookList.item(i);
                attrs = book.getAttributes();
                attr = attrs.item(attrs.getLength() - 1);
                childNodes = book.getChildNodes();
                SK[i] = new My_Shortcut_Key(attr.getNodeValue().charAt(0), root, childNodes);
            }
        }catch (ParserConfigurationException e) {
            e.printStackTrace();
        }catch (SAXException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }


        root.add(B_upload);
        root.add(B_save);
        root.add(B_clear);
        root.add(btnChoice);
        root.add(btnOpen);
    }
}