//�B�X�� 108403501 ��ޤTA
import java.awt.*;
import javax.swing.*;

public class Main {
	public static void main(String[] args) {
		PainterFunction f = new PainterFunction();
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(1000, 500);
		JOptionPane.showMessageDialog(f, "Wellcome", "�T��", JOptionPane.PLAIN_MESSAGE);
		
		f.setVisible(true);
	}

}
