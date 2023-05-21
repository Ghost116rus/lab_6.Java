import java.util.*;
import java.util.LinkedList;
import java.awt.*;
import java.awt.event.*;
public class Main {
    static int count;
    public static void main(String[] args){
        count = 0;
        DemonstrationWindow balls = new DemonstrationWindow();
    }
}
class DemonstrationWindow extends Frame implements Observer, ActionListener, ItemListener {
    private LinkedList LL = new LinkedList();
    private Frame ControlPanel;
    private Button StartBtn;

    private Choice colorChoice;
    private Choice speedChoice;
    private Choice figureChoice;

    private TextField inputField;
    private static int[] speedArray = new int[] {8, 12, 15, 18, 22, 25};

    private void InitControlPanel()
    {
        ControlPanel = new Frame();
        ControlPanel.setSize(new Dimension(400,200));
        ControlPanel.setResizable(false);
        ControlPanel.setTitle("Контроль");
        ControlPanel.setLayout(null);
        ControlPanel.addWindowListener(new WindowAdapter2());

        StartBtn = new Button("Пуск");
        StartBtn.setSize(new Dimension(20,40));
        StartBtn.setActionCommand("OK");
        StartBtn.addActionListener(this);
        StartBtn.setBounds(200,150, 150,20);
        ControlPanel.add(StartBtn, new Point(20,20));

        inputField = new TextField();
        inputField.setBounds(80,150, 150,20);
        ControlPanel.add(inputField);


        figureChoice = new Choice();
        figureChoice.addItemListener(this);
        figureChoice.setBounds(100,35, 150,20);
        figureChoice.add("Создание фигуры");
        //figureChoice.select(0);
        ControlPanel.add(figureChoice);

        colorChoice = new Choice();
        colorChoice.addItem("Синий");
        colorChoice.addItem("Зелёный");
        colorChoice.addItem("Красный");
        colorChoice.addItem("Чёрный");
        colorChoice.addItem("Жёлтый");
        colorChoice.addItemListener(this);
        colorChoice.setBounds(100,60, 150,20);
        ControlPanel.add(colorChoice);

        speedChoice = new Choice();
        speedChoice.addItem("125%");
        speedChoice.addItem("110%");
        speedChoice.addItem("100%");
        speedChoice.addItem("90%");
        speedChoice.addItem("75%");
        speedChoice.addItem("60%");
        speedChoice.addItemListener(this);
        speedChoice.setBounds(100,85, 150,20);
        ControlPanel.add(speedChoice);


        speedChoice.select(2);

        ControlPanel.setVisible(true);
    }

    DemonstrationWindow(){
        this.addWindowListener(new WindowAdapter2());

        InitControlPanel();

        this.setSize(500,200);
        this.setVisible(true);
        this.setLocation(100, 150);
    }
    public void update(Observable o, Object arg) {
        //MyFigure figure = (MyFigure)arg;
        //System.out.println ("x= " + figure.thr.getName() + figure.x);
        repaint();
    }
    public void paint (Graphics g) {
        if (figureChoice.getSelectedIndex() != 0)
        {
            inputField.setEditable(false);
            colorChoice.setEnabled(false);
            StartBtn.setLabel("Изменить скорость");
            StartBtn.setActionCommand("Change");
        }
        else
        {
            inputField.setEditable(true);
            colorChoice.setEnabled(true);
            StartBtn.setLabel("Пуск");
            StartBtn.setActionCommand("OK");
        }

        if (!LL.isEmpty()){
            for (Object item : LL) {
                MyFigure figure = (MyFigure) item;
                g.setColor(figure.col);
                switch (figure.type)
                {
                    case 1:
                        g.fillOval(figure.x, figure.y, 30, 30); break;
                    case 2:
                        g.fillOval(figure.x, figure.y, 30, 20); break;
                    case 3:
                        g.drawRect(figure.x, figure.y, 30, 30); break;
                    case 4:
                        g.drawRect(figure.x, figure.y, 30, 25); break;

                    case 5:
                        int[] xPoints = {figure.x,figure.x-10,figure.x+30};
                        int[] yPoints = {figure.y,figure.y+20,figure.y + 30};
                        g.fillPolygon(xPoints,yPoints,3);
                        break;
                }
                g.drawString(Integer.toString(figure.Id),figure.x + 35, figure.y + 35);
            }
        }
    }
    public void itemStateChanged (ItemEvent iE) {}

    private int GetFigureType(String textFromControlPanel){
        switch (textFromControlPanel)
        {
            case "круг": return 1;
            case "овал": return 2;
            case "квадрат": return 3;
            case "прямоугольник": return 4;
            case "треугольник": return 5;
            default: return -1;
        }
    }

    public void actionPerformed (ActionEvent aE) {
        String str = aE.getActionCommand();
        if (str.equals ("OK")){
            Color col = null;
            switch (colorChoice.getSelectedIndex()) {
                case 0: col= Color.blue; break;
                case 1: col= Color.green; break;
                case 2: col= Color.red; break;
                case 3: col= Color.black; break;
                case 4: col= Color.yellow; break;
            }
            int figureType = GetFigureType(this.inputField.getText());

            if (figureType != -1) {
                MyFigure figure = new MyFigure(col, figureType, speedArray[speedChoice.getSelectedIndex()], this);
                LL.add(figure);
                figure.addObserver(this);
                figureChoice.add(Integer.toString(figure.Id));
                figureChoice.select(0);
                inputField.setText("");
            }
        }
        else if (str.equals("Change")){
            System.out.println(figureChoice.getSelectedIndex());

            for (Object item : LL) {
                MyFigure figure = (MyFigure) item;
                if (figure.Id == figureChoice.getSelectedIndex()-1)
                {
                    figure.speed = speedArray[speedChoice.getSelectedIndex()];
                }
            }
        }
        repaint();
    }
}
class MyFigure extends Observable implements Runnable {
    private static Random random = new Random();
    Thread thr;

    private boolean xplus = false;
    private boolean yplus = false;

    public int speed;
    private int incrementX;
    private int incrementY;

    public int Id;
    public int type;
    int x; int y;
    Color col;

    DemonstrationWindow mainFrame = null;

    public MyFigure (Color col, int figureType, int speed, DemonstrationWindow f ){
        mainFrame = f;

        xplus = true; yplus = true;

        x = 20; y = 30;
        incrementX = random.nextInt(3);
        incrementY = random.nextInt(3);
        if (incrementX == 0) incrementY = 2;
        if (incrementY == 0) incrementX = 2;


        this.type = figureType;
        this.col = col;
        Id = Main.count++;
        thr = new Thread(this,"Id: " + Id);
        thr.start();
        this.speed = speed;
    }
    public void run(){
        while (true){

            if(x>= mainFrame.getSize().width - 30) xplus = false;
            if(x<=-1) xplus = true;

            if(y>=mainFrame.getSize().height - 30) yplus = false;
            if(y<=29) yplus = true;

            if(xplus) x+=incrementX; else x-=incrementX;
            if(yplus) y+=incrementY; else y-=incrementY;

            setChanged();notifyObservers (this);
            try{Thread.sleep(speed);}
            catch (InterruptedException e){}
        }
    }
}
class WindowAdapter2 extends WindowAdapter {
    public void windowClosing (WindowEvent wE) {System.exit (0);}
}
