import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;

public class Notepad extends JFrame {
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private String currentFile = null;
    private boolean changed = false;
    private UndoManager undoManager;

    public Notepad() {
        // Set up the frame
        super("Ellay pad");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Initialize components
        textArea = new JTextArea();
        undoManager = new UndoManager();
        fileChooser = new JFileChooser();
        
        // Set up text area with scroll pane
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { changed = true; }
            public void removeUpdate(DocumentEvent e) { changed = true; }
            public void changedUpdate(DocumentEvent e) { changed = true; }
        });
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create menu bar
        setJMenuBar(createMenuBar());
        
        // Display the window
        setVisible(true);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem newMenuItem = new JMenuItem("New", KeyEvent.VK_N);
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newMenuItem.addActionListener(e -> newFile());
        
        JMenuItem openMenuItem = new JMenuItem("Open...", KeyEvent.VK_O);
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openMenuItem.addActionListener(e -> openFile());
        
        JMenuItem saveMenuItem = new JMenuItem("Save", KeyEvent.VK_S);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveMenuItem.addActionListener(e -> saveFile(false));
        
        JMenuItem saveAsMenuItem = new JMenuItem("Save As...", KeyEvent.VK_A);
        saveAsMenuItem.addActionListener(e -> saveFile(true));
        
        JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitMenuItem.addActionListener(e -> exit());
        
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        
        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        
        JMenuItem undoMenuItem = new JMenuItem("Undo", KeyEvent.VK_U);
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        undoMenuItem.addActionListener(e -> {
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        });
        
        JMenuItem redoMenuItem = new JMenuItem("Redo", KeyEvent.VK_R);
        redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        redoMenuItem.addActionListener(e -> {
            if (undoManager.canRedo()) {
                undoManager.redo();
            }
        });
        
        JMenuItem cutMenuItem = new JMenuItem("Cut", KeyEvent.VK_T);
        cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        cutMenuItem.addActionListener(e -> textArea.cut());
        
        JMenuItem copyMenuItem = new JMenuItem("Copy", KeyEvent.VK_C);
        copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        copyMenuItem.addActionListener(e -> textArea.copy());
        
        JMenuItem pasteMenuItem = new JMenuItem("Paste", KeyEvent.VK_P);
        pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        pasteMenuItem.addActionListener(e -> textArea.paste());
        
        JMenuItem selectAllMenuItem = new JMenuItem("Select All", KeyEvent.VK_A);
        selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        selectAllMenuItem.addActionListener(e -> textArea.selectAll());
        
        editMenu.add(undoMenuItem);
        editMenu.add(redoMenuItem);
        editMenu.addSeparator();
        editMenu.add(cutMenuItem);
        editMenu.add(copyMenuItem);
        editMenu.add(pasteMenuItem);
        editMenu.addSeparator();
        editMenu.add(selectAllMenuItem);
        
        // Format menu
        JMenu formatMenu = new JMenu("Format");
        formatMenu.setMnemonic(KeyEvent.VK_O);
        
        JMenuItem wordWrapMenuItem = new JMenuItem("Word Wrap", KeyEvent.VK_W);
        wordWrapMenuItem.addActionListener(e -> {
            boolean current = textArea.getLineWrap();
            textArea.setLineWrap(!current);
            textArea.setWrapStyleWord(!current);
        });
        
        JMenuItem fontMenuItem = new JMenuItem("Font...", KeyEvent.VK_F);
        fontMenuItem.addActionListener(e -> {
            Font font = JFontChooser.showDialog(this, "Choose Font", textArea.getFont());
            if (font != null) {
                textArea.setFont(font);
            }
        });
        
        formatMenu.add(wordWrapMenuItem);
        formatMenu.add(fontMenuItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);
        
        return menuBar;
    }
    
    private void newFile() {
        if (confirmSave()) {
            textArea.setText("");
            currentFile = null;
            setTitle("Notepad");
            changed = false;
        }
    }
    
    private void openFile() {
        if (confirmSave()) {
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    textArea.setText("");
                    String line;
                    while ((line = reader.readLine()) != null) {
                        textArea.append(line + "\n");
                    }
                    currentFile = file.getPath();
                    setTitle("Notepad - " + file.getName());
                    changed = false;
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void saveFile(boolean saveAs) {
        if (currentFile == null || saveAs) {
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().contains(".")) {
                    file = new File(file.getPath() + ".txt");
                }
                currentFile = file.getPath();
                setTitle("Notepad - " + file.getName());
            } else {
                return;
            }
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
            writer.write(textArea.getText());
            changed = false;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean confirmSave() {
        if (changed) {
            int option = JOptionPane.showConfirmDialog(this,
                    "The text has been changed. Save changes?",
                    "Save", JOptionPane.YES_NO_CANCEL_OPTION);
            
            if (option == JOptionPane.YES_OPTION) {
                saveFile(false);
                return !changed;
            } else if (option == JOptionPane.CANCEL_OPTION) {
                return false;
            }
        }
        return true;
    }
    
    private void exit() {
        if (confirmSave()) {
            System.exit(0);
        }
    }
    
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new Notepad());
    }
}
