package ve.converter;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JOptionPane;

public class MainFrame extends javax.swing.JFrame {

 public MainFrame() {
  initComponents();
  setLocationRelativeTo(null);
 }

 // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
 private void initComponents() {
  File saveFolder = new File("ConvertedFiles");
  if (!saveFolder.exists()) {
   saveFolder.mkdir();
  }
  /*try {
   javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
  } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException e) {
   java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
  }*/
  fileDialog = new javax.swing.JFileChooser();
  txtSelectedFile = new javax.swing.JTextField();
  // Variables declaration - do not modify//GEN-BEGIN:variables
  javax.swing.JButton btnBrowse = new javax.swing.JButton();
  javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
  javax.swing.JButton btnConvertVE = new javax.swing.JButton();
  invertX = new javax.swing.JCheckBox();
  invertY = new javax.swing.JCheckBox();
  invertZ = new javax.swing.JCheckBox();
  javax.swing.JButton btnStXYZ = new javax.swing.JButton();
  javax.swing.JButton btnStXZY = new javax.swing.JButton();
  setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
  setTitle(".OBJ-to-V.E. Converter");
  setResizable(false);
  txtSelectedFile.setEnabled(false);
  btnBrowse.setText("Browse...");
  btnBrowse.addActionListener(event -> buttonBrowseActionPerformed());
  jLabel1.setText("Selected file:");
  btnConvertVE.setText("Convert .OBJ to V.E.");
  btnConvertVE.addActionListener(event -> buttonConvertVEActionPerformed());
  invertX.setText("Invert X-axis");
  invertY.setText("Invert Y-axis");
  invertZ.setText("Invert Z-axis");
  btnStXYZ.setText("set Conversion order (X,Y,Z)");
  btnStXYZ.addActionListener(event -> buttonSetXYZActionPerformed());
  btnStXZY.setText("set Conversion order (X,Z,Y)");
  btnStXZY.addActionListener(event -> buttonSetXZYActionPerformed());
  javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
  getContentPane().setLayout(layout);
  layout.setHorizontalGroup(
  layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
  .addGroup(layout.createSequentialGroup()
  .addContainerGap()
  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
  .addComponent(btnConvertVE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
  .addGroup(layout.createSequentialGroup()
  .addComponent(btnStXYZ, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
  .addComponent(btnStXZY, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
  .addGroup(layout.createSequentialGroup()
  .addComponent(jLabel1)
  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
  .addComponent(txtSelectedFile, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
  .addComponent(btnBrowse, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
  .addGroup(layout.createSequentialGroup()
  .addGap(8, 8, 8)
  .addComponent(invertX, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
  .addGap(28, 28, 28)
  .addComponent(invertY, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
  .addGap(65, 65, 65)
  .addComponent(invertZ, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
  .addGap(0, 0, Short.MAX_VALUE)))
  .addContainerGap())
  );
  layout.setVerticalGroup(
  layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
  .addGroup(layout.createSequentialGroup()
  .addContainerGap()
  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
  .addComponent(txtSelectedFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
  .addComponent(jLabel1)
  .addComponent(btnBrowse))
  .addGap(18, 18, 18)
  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
  .addComponent(invertX)
  .addComponent(invertY)
  .addComponent(invertZ))
  .addGap(18, 18, 18)
  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
  .addComponent(btnStXYZ, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
  .addComponent(btnStXZY, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
  .addComponent(btnConvertVE, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
  .addGap(18, 18, 18))
  );
  pack();
 }// </editor-fold>//GEN-END:initComponents

 private void buttonBrowseActionPerformed() {//GEN-FIRST:event_btnBrowseActionPerformed
  new FileDialogUtility(fileDialog).setTitle().setActionListener((ActionEvent e) -> {
   if (fileDialog.getSelectedFile() != null) {
    txtSelectedFile.setText(fileDialog.getSelectedFile().getAbsolutePath());
   }
  }).setMultipleSelection().setAllowAllFiles().setWorkingDirectory().showDialog(this);
 }//GEN-LAST:event_btnBrowseActionPerformed

 private void buttonConvertVEActionPerformed() {//GEN-FIRST:event_btnConvertVEActionPerformed
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
 }//GEN-LAST:event_btnConvertVEActionPerformed

 private void buttonSetXYZActionPerformed() {//GEN-FIRST:event_btnStXYZActionPerformed
  Converter.axisSwap = false;
 }//GEN-LAST:event_btnStXYZActionPerformed

 private void buttonSetXZYActionPerformed() {//GEN-FIRST:event_btnStXZYActionPerformed
  Converter.axisSwap = true;
 }//GEN-LAST:event_btnStXZYActionPerformed

 public static void main(String[] s) {
  java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
 }

 private javax.swing.JFileChooser fileDialog;
 private javax.swing.JCheckBox invertX;
 private javax.swing.JCheckBox invertY;
 private javax.swing.JCheckBox invertZ;
 private javax.swing.JTextField txtSelectedFile;
 // End of variables declaration//GEN-END:variables
}
