import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JFontChooser extends JDialog {
    private Font selectedFont;
    private JList<String> fontNameList;
    private JList<String> fontStyleList;
    private JList<String> fontSizeList;
    private JTextField sampleText;
    
    private static final String[] FONT_STYLES = {"Regular", "Bold", "Italic", "Bold Italic"};
    private static final String[] FONT_SIZES = {"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36", "48", "72"};
    
    public JFontChooser(Frame parent, Font initialFont) {
        super(parent, "Font Chooser", true);
        selectedFont = initialFont;
        
        // Create components
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Font selection panel
        JPanel selectionPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        
        // Font name list
        JPanel fontNamePanel = new JPanel(new BorderLayout());
        fontNamePanel.add(new JLabel("Font:"), BorderLayout.NORTH);
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();
        fontNameList = new JList<>(fontNames);
        fontNameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontNameList.setSelectedValue(initialFont.getFamily(), true);
        fontNameList.addListSelectionListener(e -> updateSample());
        
        JScrollPane fontNameScroll = new JScrollPane(fontNameList);
        fontNamePanel.add(fontNameScroll, BorderLayout.CENTER);
        
        // Font style list
        JPanel fontStylePanel = new JPanel(new BorderLayout());
        fontStylePanel.add(new JLabel("Style:"), BorderLayout.NORTH);
        
        fontStyleList = new JList<>(FONT_STYLES);
        fontStyleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontStyleList.setSelectedIndex(initialFont.getStyle());
        fontStyleList.addListSelectionListener(e -> updateSample());
        
        JScrollPane fontStyleScroll = new JScrollPane(fontStyleList);
        fontStylePanel.add(fontStyleScroll, BorderLayout.CENTER);
        
        // Font size list
        JPanel fontSizePanel = new JPanel(new BorderLayout());
        fontSizePanel.add(new JLabel("Size:"), BorderLayout.NORTH);
        
        fontSizeList = new JList<>(FONT_SIZES);
        fontSizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontSizeList.setSelectedValue(String.valueOf(initialFont.getSize()), true);
        fontSizeList.addListSelectionListener(e -> updateSample());
        
        JScrollPane fontSizeScroll = new JScrollPane(fontSizeList);
        fontSizePanel.add(fontSizeScroll, BorderLayout.CENTER);
        
        // Add to selection panel
        selectionPanel.add(fontNamePanel);
        selectionPanel.add(fontStylePanel);
        selectionPanel.add(fontSizePanel);
        
        // Sample text panel
        JPanel samplePanel = new JPanel(new BorderLayout());
        samplePanel.setBorder(BorderFactory.createTitledBorder("Sample"));
        
        sampleText = new JTextField("AaBbYyZz");
        sampleText.setEditable(false);
        sampleText.setHorizontalAlignment(JTextField.CENTER);
        sampleText.setFont(initialFont);
        sampleText.setPreferredSize(new Dimension(300, 80));
        
        samplePanel.add(sampleText, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            updateSelectedFont();
            dispose();
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        // Add to main panel
        mainPanel.add(selectionPanel, BorderLayout.CENTER);
        mainPanel.add(samplePanel, BorderLayout.SOUTH);
        
        // Add to dialog
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        // Set dialog properties
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void updateSample() {
        if (fontNameList.getSelectedValue() != null && 
            fontStyleList.getSelectedValue() != null && 
            fontSizeList.getSelectedValue() != null) {
            
            Font font = createFont();
            sampleText.setFont(font);
        }
    }
    
    private void updateSelectedFont() {
        selectedFont = createFont();
    }
    
    private Font createFont() {
        String fontName = fontNameList.getSelectedValue();
        int fontStyle = getFontStyle(fontStyleList.getSelectedValue());
        int fontSize = Integer.parseInt(fontSizeList.getSelectedValue());
        
        return new Font(fontName, fontStyle, fontSize);
    }
    
    private int getFontStyle(String styleName) {
        switch (styleName) {
            case "Bold": return Font.BOLD;
            case "Italic": return Font.ITALIC;
            case "Bold Italic": return Font.BOLD | Font.ITALIC;
            default: return Font.PLAIN;
        }
    }
    
    public Font getSelectedFont() {
        return selectedFont;
    }
    
    public static Font showDialog(Component parent, String title, Font initialFont) {
        Frame frame = (Frame) SwingUtilities.getWindowAncestor(parent);
        if (frame == null) {
            frame = new Frame(title);
        }
        
        JFontChooser chooser = new JFontChooser(frame, initialFont);
        chooser.setVisible(true);
        
        return chooser.getSelectedFont();
    }
}
