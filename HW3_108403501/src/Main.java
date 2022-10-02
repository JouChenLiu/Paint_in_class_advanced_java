//劉柔辰 108403501 資管三A
import java.awt.*;
import javax.swing.*;

public class Main {
	public static void main(String[] args) {
		PainterFunction f = new PainterFunction();
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(1000, 500);
		JOptionPane.showMessageDialog(f, "Wellcome", "訊息", JOptionPane.PLAIN_MESSAGE);
		
		f.setVisible(true);
	}

}
