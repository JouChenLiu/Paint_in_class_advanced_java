//劉柔辰 108403501 資管三A
import java.awt.*;	//使用BorderLayout必須載入awt套件
import java.awt.event.*;	//使用事件必須載入event套件
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;	//載入swing套件
import java.util.ArrayList;

public class PainterFunction extends JFrame {
	private final JPanel toolPanel;
	private final JLabel statusBar;
	private final JLabel label1;
	private final JLabel label2;
	private final JLabel label3;
	private final JPanel PanelComboBox;
	private final JPanel PanelRadioButton;
	private final JPanel PanelCheckBox;
	private final JPanel PanelButton;
	
	private final String[] brushes = {"筆刷", "直線", "橢圓形", "矩形"};
	private final JComboBox<String> brushComboBox;	//下拉式選單
	private final JRadioButton sButton;
	private final JRadioButton mButton;
	private final JRadioButton lButton;
	private final ButtonGroup bGroup;
	private final JCheckBox fillCheckBox;
	private final JButton strokeColorButton;//
	private final JButton brushColorButton;
	private final JButton cleanButton;
	private final JButton eraserButton;//
	private final JButton backButton;//
	
	private final ArrayList<Point> arrPoints = new ArrayList<>();
	private final ArrayList<Color> arrColors = new ArrayList<>();
	private final ArrayList<Integer> arrSize = new ArrayList<>();
	private final ArrayList<Graphics2D> arrGraphics2D = new ArrayList<>();
	private final ArrayList<BufferedImage> arrBuffered = new ArrayList<>();
	private Color brushColor = Color.BLACK;//
	private Color strokeColor = brushColor;//
	private MousePanel mousePanel = new MousePanel();
	int brushSize = 10;
	int mouseDraggedX;
    int mouseDraggedY;
    int mousePressedX;
    int mousePressedY;
    int mouseReleasedX;
    int mouseReleasedY;
    int mouseClickedX;
    int mouseClickedY;
    int mouseMovedX;
    int mouseMovedY;
    int clear;
    int colorCount=0;
    int sizeCount=0;
    int gc=0;
    int drag = 1; //控制繪圖工具預覽
	
	public PainterFunction() {
		super("小畫家");
		
		//工作區toolPanel
		toolPanel = new JPanel();
		toolPanel.setLayout(new FlowLayout());
		setLayout(new BorderLayout());
		
		//繪圖工具Label1 ComboBox
		PanelComboBox = new JPanel();
		PanelComboBox.setLayout(new BorderLayout());
		label1 = new JLabel("繪圖工具");
		PanelComboBox.add(label1, BorderLayout.NORTH);
		
		brushComboBox = new JComboBox<String>(brushes);
		PanelComboBox.add(brushComboBox, BorderLayout.SOUTH);
		
		brushComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				// TODO Auto-generated method stub
				if(event.getStateChange() == ItemEvent.SELECTED) {
					System.out.println("選擇 " + brushes[brushComboBox.getSelectedIndex()]);
					
					if(brushComboBox.getSelectedIndex() != 0) {
						fillCheckBox.setEnabled(true);
						strokeColorButton.setEnabled(true);
						if(brushComboBox.getSelectedIndex() == 1) {
							strokeColorButton.setEnabled(false);
							strokeColorButton.setVisible(false);
						}//
					}else {
						fillCheckBox.setEnabled(false);
						strokeColorButton.setEnabled(false);
						strokeColorButton.setVisible(false);
					}//
				}
			}
		});
		
		toolPanel.add(PanelComboBox);	//加入工具區
		
		//筆刷大小Label2 RadioButton
		PanelRadioButton = new JPanel();
		PanelRadioButton.setLayout(new BorderLayout());
		label2 = new JLabel("筆刷大小");
		PanelRadioButton.add(label2, BorderLayout.NORTH);
		
		sButton = new JRadioButton("小", false);
		mButton = new JRadioButton("中", false);
		lButton = new JRadioButton("大", false);
		bGroup = new ButtonGroup();
		
		bGroup.add(sButton);
		bGroup.add(mButton);
		bGroup.add(lButton);
		PanelRadioButton.add(sButton, BorderLayout.WEST);
		PanelRadioButton.add(mButton, BorderLayout.CENTER);
		PanelRadioButton.add(lButton, BorderLayout.EAST);
		
		sButton.addItemListener(new RadioButtonHandler("小"));
		mButton.addItemListener(new RadioButtonHandler("中"));
		lButton.addItemListener(new RadioButtonHandler("大"));
		
		toolPanel.add(PanelRadioButton);	//加入工具區
		
		//填滿label3 CheckBox
		PanelCheckBox = new JPanel();
		PanelCheckBox.setLayout(new BorderLayout());
		label3 = new JLabel("填滿");
		fillCheckBox = new JCheckBox();
		fillCheckBox.addItemListener(new CheckBoxHandler("填滿"));
		PanelCheckBox.add(label3, BorderLayout.NORTH);
		PanelCheckBox.add(fillCheckBox, BorderLayout.SOUTH);
		fillCheckBox.setEnabled(false);
		
		toolPanel.add(PanelCheckBox);	//加入工具區
		
		//筆刷顏色、清除畫面 Button
		PanelButton = new JPanel();
		PanelButton.setLayout(new FlowLayout());
		strokeColorButton = new JButton("線框色");//
		brushColorButton = new JButton("筆刷顏色");
		cleanButton = new JButton("清除畫面");
		eraserButton = new JButton("橡皮擦");
		backButton = new JButton("上一步");
		PanelButton.add(strokeColorButton);//
		PanelButton.add(brushColorButton);
		PanelButton.add(cleanButton);
		PanelButton.add(eraserButton);
		PanelButton.add(backButton);
		
		strokeColorButton.setEnabled(false); //預設無法使用//
		strokeColorButton.setVisible(false); //預設無法看見//
		
		strokeColorButton.addActionListener(new ButtonHandler());//
		brushColorButton.addActionListener(new ButtonHandler());
		cleanButton.addActionListener(new ButtonHandler());
		eraserButton.addActionListener(new ButtonHandler());
		backButton.addActionListener(new ButtonHandler());
		
		toolPanel.add(PanelButton);	//加入工具區
		
		//狀態列statusBar
		statusBar = new JLabel("指標位置:(0,0)");
		statusBar.setBackground(Color.BLACK);
		statusBar.setForeground(Color.WHITE);
		statusBar.setOpaque(true);	//let label background painted
		
		//畫布區mousePanel
		mousePanel.setBackground(Color.WHITE);
		
		//加進Frame
		add(toolPanel, BorderLayout.NORTH);
		add(statusBar, BorderLayout.SOUTH);
		add(mousePanel, BorderLayout.CENTER);
	}
	
	//畫布區
	private class MousePanel extends JPanel{
		public MousePanel() {
			arrBuffered.add(new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB_PRE));
			
			addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent event) {
					statusBar.setText(String.format("指標位置:(%d,%d)", event.getX(), event.getY()));
					mouseDraggedX = event.getX();
					mouseDraggedY = event.getY();
					
					switch(brushComboBox.getSelectedIndex()) {
					case 0://筆刷
						arrPoints.add(event.getPoint());
						arrColors.add(brushColor);
						arrSize.add(brushSize);
						repaint();
						break;
					case 1://直線
						repaint();
						break;
					case 2://橢圓形
						repaint();
						break;
					case 3://矩形
						repaint();
						break;
					}
				}
				
				public void mouseMoved(MouseEvent event) {
					statusBar.setText(String.format("滑鼠位置： [ %d, %d ]", event.getX(), event.getY()));
					mouseMovedX = event.getX();
					mouseMovedY = event.getY();
				}
			});
			
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent event) {
					statusBar.setText(String.format("滑鼠位置： [ %d, %d ]", event.getX(), event.getY()));
					mousePressedX = event.getX();
					mousePressedY = event.getY();
				}
				
				public void mouseReleased(MouseEvent event) {
					statusBar.setText(String.format("滑鼠位置： [ %d, %d ]", event.getX(), event.getY()));
					mouseReleasedX = event.getX();
					mouseReleasedY = event.getY();
					drag = 0;
					repaint();
				}
				
				public void mouseClicked(MouseEvent event) {
					mouseClickedX = event.getX();
					mouseClickedY = event.getY();
				}
				
				public void mouseExited(MouseEvent event)
				{
					statusBar.setText(String.format("滑鼠離開畫布"));	
				}
			});
		}
		
		public void paintComponent(Graphics g) {
			Graphics gg = arrBuffered.get(arrBuffered.size()-1).createGraphics();
			arrGraphics2D.add(arrBuffered.get(arrBuffered.size()-1).createGraphics());
			if(clear == 1) {
				super.paintComponent(g);
				arrBuffered.add(new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB_PRE));
				arrSize.clear();
				arrColors.clear();
				clear = 0;
			}else {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) gg; //g2d為實際繪圖作用區域
				Graphics2D g2dp = (Graphics2D) g; //g2dp為預覽繪圖區域
				g2d.setPaint(brushColor);
				g2dp.setPaint(brushColor);
				float[] dash = {40};
				
				if(fillCheckBox.isSelected()) {	//填滿
					g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER));
					g2dp.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER));
					switch(brushComboBox.getSelectedIndex()) {
					case 0:	//筆刷		
						for(int i=0; i<arrPoints.size(); i++) {
							g2d.setColor(arrColors.get(i));
							g2d.fillOval(arrPoints.get(i).x, arrPoints.get(i).y, arrSize.get(i), arrSize.get(i));
						}
						break;
					case 1:	//直線
						if(drag == 1) {	//drag控制預覽變數
							g2dp.draw(new Line2D.Double(mousePressedX, mousePressedY, mouseDraggedX, mouseDraggedY));
						}
						else if(drag == 0) {
							g2d.draw(new Line2D.Double(mousePressedX, mousePressedY, mouseReleasedX, mouseReleasedY));
						}
						break;
					case 2:	//橢圓形
						if(drag == 1) {
							g2dp.fill(new Ellipse2D.Double(mousePressedX, mousePressedY, mouseDraggedX-mousePressedX, mouseDraggedY-mousePressedY));
							g2dp.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER));
							g2dp.setPaint(strokeColor);//
							g2dp.draw(new Ellipse2D.Double(mousePressedX, mousePressedY, mouseDraggedX-mousePressedX, mouseDraggedY-mousePressedY));
						}
						else if(drag == 0) {
							g2d.fill(new Ellipse2D.Double(mousePressedX, mousePressedY, mouseReleasedX-mousePressedX, mouseReleasedY-mousePressedY));
							g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER));
							g2d.setPaint(strokeColor);//
							g2d.draw(new Ellipse2D.Double(mousePressedX, mousePressedY, mouseReleasedX-mousePressedX, mouseReleasedY-mousePressedY));
						}
						break;
					case 3:	//矩形
						if(drag == 1) {
							g2dp.fillRect(mousePressedX, mousePressedY, mouseDraggedX-mousePressedX, mouseDraggedY-mousePressedY);
							g2dp.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER));
							g2dp.setPaint(strokeColor);//
							g2dp.drawRect(mousePressedX, mousePressedY, mouseDraggedX-mousePressedX, mouseDraggedY-mousePressedY);
						}
						else if(drag == 0) {
							g2d.fill(new Rectangle2D.Double(mousePressedX, mousePressedY, mouseReleasedX-mousePressedX, mouseReleasedY-mousePressedY));
							g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER));
							g2d.setPaint(strokeColor);//
							g2d.draw(new Rectangle2D.Double(mousePressedX, mousePressedY, mouseReleasedX-mousePressedX, mouseReleasedY-mousePressedY));
						}	
						break;
					}	//end switch
				}else {	//非填滿
					g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER));
					g2dp.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER));
					switch(brushComboBox.getSelectedIndex()) {
					case 0:	//筆刷		
						for(int i=0; i<arrPoints.size(); i++) {
							g2d.setColor(arrColors.get(i));
							g2d.fill(new Ellipse2D.Double(arrPoints.get(i).x, arrPoints.get(i).y, arrSize.get(i), arrSize.get(i)));
						}
						break;
					case 1:	//直線
						g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER, 10, dash, 0));
						g2dp.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER, 10, dash, 0));
						if(drag == 1) {
							g2dp.drawLine(mousePressedX, mousePressedY, mouseDraggedX, mouseDraggedY);
						}
						else if(drag == 0) {
							g2d.draw(new Line2D.Double(mousePressedX, mousePressedY, mouseReleasedX, mouseReleasedY));
						}
						break;
					case 2:	//橢圓形
						if(drag == 1) {
							g2dp.drawOval(mousePressedX, mousePressedY, mouseDraggedX-mousePressedX, mouseDraggedY-mousePressedY);
						}
						else if(drag == 0) {
							g2d.draw(new Ellipse2D.Double(mousePressedX, mousePressedY, mouseReleasedX-mousePressedX, mouseReleasedY-mousePressedY));
						}
						break;
					case 3:	//矩形
						if(drag == 1) {
							g2dp.drawRect(mousePressedX, mousePressedY, mouseDraggedX-mousePressedX, mouseDraggedY-mousePressedY);
						}
						else if(drag == 0) {
							g2d.draw(new Rectangle2D.Double(mousePressedX, mousePressedY, mouseReleasedX-mousePressedX, mouseReleasedY-mousePressedY));
						}						
						break;
					}	//end switch
				}	//end else
				drag = 1;
			}	//end not clear else
			g.drawImage(arrBuffered.get(arrBuffered.size()-1), 0, 0, this);
		}
	}
	
	private class RadioButtonHandler implements ItemListener {
		private String size;
		
		public RadioButtonHandler(String s) {
			size = s;
		}
		@Override
		public void itemStateChanged(ItemEvent event) {
			// TODO Auto-generated method stub
			if(event.getStateChange() == ItemEvent.SELECTED) {
				System.out.println("選擇 " + size + " 筆刷");
				
				switch(size) {
				case "小":
					brushSize = 5;
					break;
				case "中":
					brushSize = 15;
					break;
				case "大":
					brushSize = 25;
					break;
				}
			}
		}
	}
	
	private class CheckBoxHandler implements ItemListener {
		private String fill;
		
		public CheckBoxHandler(String f) {
			fill = f;
		}
		
		@Override
		public void itemStateChanged(ItemEvent event) {
			// TODO Auto-generated method stub
			if(fillCheckBox.isSelected()) {
				System.out.println("選擇 填滿");
				strokeColorButton.setEnabled(true);
				strokeColorButton.setVisible(true);
				if(brushComboBox.getSelectedIndex()==1) {
					strokeColorButton.setEnabled(false);
					strokeColorButton.setVisible(false);
				}//
			}
			else {
				System.out.println("取消 填滿");
				strokeColorButton.setEnabled(false);//
				strokeColorButton.setVisible(false);//
			}
		}
	}
	
	private class ButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			// TODO Auto-generated method stub
			System.out.println("點選 " + event.getActionCommand());
			
			switch(event.getActionCommand()) {
			case "線框色"://
				strokeColor = JColorChooser.showDialog(PainterFunction.this, "線框色", getForeground());
				break;
			case "筆刷顏色":
				brushColor = JColorChooser.showDialog(PainterFunction.this, "筆刷顏色", getForeground());
				break;
			case "清除畫面":	
				clear=1;
				arrPoints.clear();
				mousePanel.repaint();
				break;
			case "橡皮擦":
				brushColor = Color.WHITE;
				break;
			case "上一步":
				
				break;
			}
		}
	}
}
