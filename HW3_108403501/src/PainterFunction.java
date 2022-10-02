//�B�X�� 108403501 ��ޤTA
import java.awt.*;	//�ϥ�BorderLayout�������Jawt�M��
import java.awt.event.*;	//�ϥΨƥ󥲶����Jevent�M��
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;	//���Jswing�M��
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
	
	private final String[] brushes = {"����", "���u", "����", "�x��"};
	private final JComboBox<String> brushComboBox;	//�U�Ԧ����
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
    int drag = 1; //����ø�Ϥu��w��
	
	public PainterFunction() {
		super("�p�e�a");
		
		//�u�@��toolPanel
		toolPanel = new JPanel();
		toolPanel.setLayout(new FlowLayout());
		setLayout(new BorderLayout());
		
		//ø�Ϥu��Label1 ComboBox
		PanelComboBox = new JPanel();
		PanelComboBox.setLayout(new BorderLayout());
		label1 = new JLabel("ø�Ϥu��");
		PanelComboBox.add(label1, BorderLayout.NORTH);
		
		brushComboBox = new JComboBox<String>(brushes);
		PanelComboBox.add(brushComboBox, BorderLayout.SOUTH);
		
		brushComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				// TODO Auto-generated method stub
				if(event.getStateChange() == ItemEvent.SELECTED) {
					System.out.println("��� " + brushes[brushComboBox.getSelectedIndex()]);
					
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
		
		toolPanel.add(PanelComboBox);	//�[�J�u���
		
		//����j�pLabel2 RadioButton
		PanelRadioButton = new JPanel();
		PanelRadioButton.setLayout(new BorderLayout());
		label2 = new JLabel("����j�p");
		PanelRadioButton.add(label2, BorderLayout.NORTH);
		
		sButton = new JRadioButton("�p", false);
		mButton = new JRadioButton("��", false);
		lButton = new JRadioButton("�j", false);
		bGroup = new ButtonGroup();
		
		bGroup.add(sButton);
		bGroup.add(mButton);
		bGroup.add(lButton);
		PanelRadioButton.add(sButton, BorderLayout.WEST);
		PanelRadioButton.add(mButton, BorderLayout.CENTER);
		PanelRadioButton.add(lButton, BorderLayout.EAST);
		
		sButton.addItemListener(new RadioButtonHandler("�p"));
		mButton.addItemListener(new RadioButtonHandler("��"));
		lButton.addItemListener(new RadioButtonHandler("�j"));
		
		toolPanel.add(PanelRadioButton);	//�[�J�u���
		
		//��label3 CheckBox
		PanelCheckBox = new JPanel();
		PanelCheckBox.setLayout(new BorderLayout());
		label3 = new JLabel("��");
		fillCheckBox = new JCheckBox();
		fillCheckBox.addItemListener(new CheckBoxHandler("��"));
		PanelCheckBox.add(label3, BorderLayout.NORTH);
		PanelCheckBox.add(fillCheckBox, BorderLayout.SOUTH);
		fillCheckBox.setEnabled(false);
		
		toolPanel.add(PanelCheckBox);	//�[�J�u���
		
		//�����C��B�M���e�� Button
		PanelButton = new JPanel();
		PanelButton.setLayout(new FlowLayout());
		strokeColorButton = new JButton("�u�ئ�");//
		brushColorButton = new JButton("�����C��");
		cleanButton = new JButton("�M���e��");
		eraserButton = new JButton("�����");
		backButton = new JButton("�W�@�B");
		PanelButton.add(strokeColorButton);//
		PanelButton.add(brushColorButton);
		PanelButton.add(cleanButton);
		PanelButton.add(eraserButton);
		PanelButton.add(backButton);
		
		strokeColorButton.setEnabled(false); //�w�]�L�k�ϥ�//
		strokeColorButton.setVisible(false); //�w�]�L�k�ݨ�//
		
		strokeColorButton.addActionListener(new ButtonHandler());//
		brushColorButton.addActionListener(new ButtonHandler());
		cleanButton.addActionListener(new ButtonHandler());
		eraserButton.addActionListener(new ButtonHandler());
		backButton.addActionListener(new ButtonHandler());
		
		toolPanel.add(PanelButton);	//�[�J�u���
		
		//���A�CstatusBar
		statusBar = new JLabel("���Ц�m:(0,0)");
		statusBar.setBackground(Color.BLACK);
		statusBar.setForeground(Color.WHITE);
		statusBar.setOpaque(true);	//let label background painted
		
		//�e����mousePanel
		mousePanel.setBackground(Color.WHITE);
		
		//�[�iFrame
		add(toolPanel, BorderLayout.NORTH);
		add(statusBar, BorderLayout.SOUTH);
		add(mousePanel, BorderLayout.CENTER);
	}
	
	//�e����
	private class MousePanel extends JPanel{
		public MousePanel() {
			arrBuffered.add(new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB_PRE));
			
			addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent event) {
					statusBar.setText(String.format("���Ц�m:(%d,%d)", event.getX(), event.getY()));
					mouseDraggedX = event.getX();
					mouseDraggedY = event.getY();
					
					switch(brushComboBox.getSelectedIndex()) {
					case 0://����
						arrPoints.add(event.getPoint());
						arrColors.add(brushColor);
						arrSize.add(brushSize);
						repaint();
						break;
					case 1://���u
						repaint();
						break;
					case 2://����
						repaint();
						break;
					case 3://�x��
						repaint();
						break;
					}
				}
				
				public void mouseMoved(MouseEvent event) {
					statusBar.setText(String.format("�ƹ���m�G [ %d, %d ]", event.getX(), event.getY()));
					mouseMovedX = event.getX();
					mouseMovedY = event.getY();
				}
			});
			
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent event) {
					statusBar.setText(String.format("�ƹ���m�G [ %d, %d ]", event.getX(), event.getY()));
					mousePressedX = event.getX();
					mousePressedY = event.getY();
				}
				
				public void mouseReleased(MouseEvent event) {
					statusBar.setText(String.format("�ƹ���m�G [ %d, %d ]", event.getX(), event.getY()));
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
					statusBar.setText(String.format("�ƹ����}�e��"));	
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
				Graphics2D g2d = (Graphics2D) gg; //g2d�����ø�ϧ@�ΰϰ�
				Graphics2D g2dp = (Graphics2D) g; //g2dp���w��ø�ϰϰ�
				g2d.setPaint(brushColor);
				g2dp.setPaint(brushColor);
				float[] dash = {40};
				
				if(fillCheckBox.isSelected()) {	//��
					g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER));
					g2dp.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER));
					switch(brushComboBox.getSelectedIndex()) {
					case 0:	//����		
						for(int i=0; i<arrPoints.size(); i++) {
							g2d.setColor(arrColors.get(i));
							g2d.fillOval(arrPoints.get(i).x, arrPoints.get(i).y, arrSize.get(i), arrSize.get(i));
						}
						break;
					case 1:	//���u
						if(drag == 1) {	//drag����w���ܼ�
							g2dp.draw(new Line2D.Double(mousePressedX, mousePressedY, mouseDraggedX, mouseDraggedY));
						}
						else if(drag == 0) {
							g2d.draw(new Line2D.Double(mousePressedX, mousePressedY, mouseReleasedX, mouseReleasedY));
						}
						break;
					case 2:	//����
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
					case 3:	//�x��
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
				}else {	//�D��
					g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER));
					g2dp.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER));
					switch(brushComboBox.getSelectedIndex()) {
					case 0:	//����		
						for(int i=0; i<arrPoints.size(); i++) {
							g2d.setColor(arrColors.get(i));
							g2d.fill(new Ellipse2D.Double(arrPoints.get(i).x, arrPoints.get(i).y, arrSize.get(i), arrSize.get(i)));
						}
						break;
					case 1:	//���u
						g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER, 10, dash, 0));
						g2dp.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER, 10, dash, 0));
						if(drag == 1) {
							g2dp.drawLine(mousePressedX, mousePressedY, mouseDraggedX, mouseDraggedY);
						}
						else if(drag == 0) {
							g2d.draw(new Line2D.Double(mousePressedX, mousePressedY, mouseReleasedX, mouseReleasedY));
						}
						break;
					case 2:	//����
						if(drag == 1) {
							g2dp.drawOval(mousePressedX, mousePressedY, mouseDraggedX-mousePressedX, mouseDraggedY-mousePressedY);
						}
						else if(drag == 0) {
							g2d.draw(new Ellipse2D.Double(mousePressedX, mousePressedY, mouseReleasedX-mousePressedX, mouseReleasedY-mousePressedY));
						}
						break;
					case 3:	//�x��
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
				System.out.println("��� " + size + " ����");
				
				switch(size) {
				case "�p":
					brushSize = 5;
					break;
				case "��":
					brushSize = 15;
					break;
				case "�j":
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
				System.out.println("��� ��");
				strokeColorButton.setEnabled(true);
				strokeColorButton.setVisible(true);
				if(brushComboBox.getSelectedIndex()==1) {
					strokeColorButton.setEnabled(false);
					strokeColorButton.setVisible(false);
				}//
			}
			else {
				System.out.println("���� ��");
				strokeColorButton.setEnabled(false);//
				strokeColorButton.setVisible(false);//
			}
		}
	}
	
	private class ButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			// TODO Auto-generated method stub
			System.out.println("�I�� " + event.getActionCommand());
			
			switch(event.getActionCommand()) {
			case "�u�ئ�"://
				strokeColor = JColorChooser.showDialog(PainterFunction.this, "�u�ئ�", getForeground());
				break;
			case "�����C��":
				brushColor = JColorChooser.showDialog(PainterFunction.this, "�����C��", getForeground());
				break;
			case "�M���e��":	
				clear=1;
				arrPoints.clear();
				mousePanel.repaint();
				break;
			case "�����":
				brushColor = Color.WHITE;
				break;
			case "�W�@�B":
				
				break;
			}
		}
	}
}
