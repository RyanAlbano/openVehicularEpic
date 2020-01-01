package ve.ui.converter;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;

class FileDialogUtility {

 private final JFileChooser dialog;

 FileDialogUtility(JFileChooser dialog) {
  this.dialog = dialog;
 }

 FileDialogUtility setWorkingDirectory() {
  dialog.setCurrentDirectory(new File(MainFrame.convertedFileFolder));
  return this;
 }

 FileDialogUtility setTitle() {
  dialog.setDialogTitle("Select an .OBJ file");
  return this;
 }

 FileDialogUtility setMultipleSelection() {
  dialog.setMultiSelectionEnabled(true);
  return this;
 }

 FileDialogUtility setAllowAllFiles() {
  dialog.setAcceptAllFileFilterUsed(true);
  return this;
 }

 FileDialogUtility setActionListener(java.awt.event.ActionListener listener) {
  dialog.addActionListener(listener);
  return this;
 }

 void showDialog(Component component) {
  dialog.showOpenDialog(component);
 }
}
