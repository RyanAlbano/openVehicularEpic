package ve.converter;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;

class FileDialogUtility {

 private final JFileChooser dialog;

 FileDialogUtility(JFileChooser dialog) {
  this.dialog = dialog;
 }

 FileDialogUtility setWorkingDirectory() {
  this.dialog.setCurrentDirectory(new File("ConvertedFiles"));
  return this;
 }

 FileDialogUtility setTitle() {
  this.dialog.setDialogTitle("Select an .OBJ file");
  return this;
 }

 FileDialogUtility setMultipleSelection() {
  this.dialog.setMultiSelectionEnabled(true);
  return this;
 }

 FileDialogUtility setAllowAllFiles() {
  this.dialog.setAcceptAllFileFilterUsed(true);
  return this;
 }

 FileDialogUtility setActionListener(java.awt.event.ActionListener listener) {
  this.dialog.addActionListener(listener);
  return this;
 }

 void showDialog(Component component) {
  this.dialog.showOpenDialog(component);
 }
}
