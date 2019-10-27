package ve.converter;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.*;

public class MainFrame extends JFrame {

 public MainFrame() {
  initComponents();
  setLocationRelativeTo(null);
 }

 private void initComponents() {
  File saveFolder = new File("ConvertedFiles");
  if (!saveFolder.exists()) {
   saveFolder.mkdir();
  }
  /*try {
   UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
  } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
   java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
  }*/
  fileDialog = new JFileChooser();
  selectedFile = new JTextField();
  JButton browse = new JButton();
  JLabel jLabel1 = new JLabel();
  JButton convertVE = new JButton();
  invertX = new JCheckBox();
  invertY = new JCheckBox();
  invertZ = new JCheckBox();
  JButton setXYZ = new JButton();
  JButton setXZY = new JButton();
  setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  setTitle(".OBJ-to-V.E. Converter");
  setResizable(false);
  selectedFile.setEnabled(false);
  browse.setText("Browse...");
  browse.addActionListener(event -> browseActionPerformed());
  jLabel1.setText("Selected file:");
  convertVE.setText("Convert .OBJ to V.E.");
  convertVE.addActionListener(event -> convertVEActionPerformed());
  invertX.setText("Invert X-axis");
  invertY.setText("Invert Y-axis");
  invertZ.setText("Invert Z-axis");
  setXYZ.setText("set Conversion order (X,Y,Z)");
  setXYZ.addActionListener(event -> setXYZActionPerformed());
  setXZY.setText("set Conversion order (X,Z,Y)");
  setXZY.addActionListener(event -> setXZYActionPerformed());
  GroupLayout layout = new GroupLayout(getContentPane());
  getContentPane().setLayout(layout);
  layout.setHorizontalGroup(
  layout.createParallelGroup(GroupLayout.Alignment.LEADING)
  .addGroup(layout.createSequentialGroup()
  .addContainerGap()
  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
  .addComponent(convertVE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
  .addGroup(layout.createSequentialGroup()
  .addComponent(setXYZ, GroupLayout.PREFERRED_SIZE, 260, GroupLayout.PREFERRED_SIZE)
  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
  .addComponent(setXZY, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
  .addGroup(layout.createSequentialGroup()
  .addComponent(jLabel1)
  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
  .addComponent(selectedFile, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
  .addComponent(browse, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
  .addGroup(layout.createSequentialGroup()
  .addGap(8, 8, 8)
  .addComponent(invertX, GroupLayout.PREFERRED_SIZE, 176, GroupLayout.PREFERRED_SIZE)
  .addGap(28, 28, 28)
  .addComponent(invertY, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
  .addGap(65, 65, 65)
  .addComponent(invertZ, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
  .addGap(0, 0, Short.MAX_VALUE)))
  .addContainerGap())
  );
  layout.setVerticalGroup(
  layout.createParallelGroup(GroupLayout.Alignment.LEADING)
  .addGroup(layout.createSequentialGroup()
  .addContainerGap()
  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
  .addComponent(selectedFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
  .addComponent(jLabel1)
  .addComponent(browse))
  .addGap(18, 18, 18)
  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
  .addComponent(invertX)
  .addComponent(invertY)
  .addComponent(invertZ))
  .addGap(18, 18, 18)
  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
  .addComponent(setXYZ, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
  .addComponent(setXZY, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
  .addComponent(convertVE, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
  .addGap(18, 18, 18))
  );
  pack();
 }

 private void browseActionPerformed() {
  new FileDialogUtility(fileDialog).setTitle().setActionListener((ActionEvent e) -> {
   if (fileDialog.getSelectedFile() != null) {
    selectedFile.setText(fileDialog.getSelectedFile().getAbsolutePath());
   }
  }).setMultipleSelection().setAllowAllFiles().setWorkingDirectory().showDialog(this);
 }

 private void convertVEActionPerformed() {
  if (fileDialog.getSelectedFile().getName().endsWith(".obj")) {
   try {
    Converter.saveFile(fileDialog.getSelectedFile(), new Converter().convert(fileDialog.getSelectedFile(), invertX.isSelected(), invertY.isSelected(), invertZ.isSelected()));
    JOptionPane.showMessageDialog(this, "Conversion Successful", "Completed", JOptionPane.INFORMATION_MESSAGE);
   } catch (IllegalStateException e) {
    JOptionPane.showMessageDialog(this, ".OBJ must be fully Triangulated", "Error", JOptionPane.ERROR_MESSAGE);
   }
  } else {
   JOptionPane.showMessageDialog(this, "File selected is not an .OBJ", "Error", JOptionPane.ERROR_MESSAGE);
  }
 }

 private static void setXYZActionPerformed() {
  Converter.axisSwap = false;
 }

 private static void setXZYActionPerformed() {
  Converter.axisSwap = true;
 }

 public static void main(String[] s) {
  java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
 }

 private JFileChooser fileDialog;
 private JCheckBox invertX, invertY, invertZ;
 private JTextField selectedFile;
}
