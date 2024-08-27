import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

class HuffmanNode {
    int frequency;
    char character;
    HuffmanNode left, right;

    HuffmanNode(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
        left = right = null;
    }
}

class HuffmanComparator implements Comparator<HuffmanNode> {
    public int compare(HuffmanNode x, HuffmanNode y) {
        return x.frequency - y.frequency;
    }
}

class HuffmanCoding {

    private static Map<Character, String> huffmanCodeMap;
    private static HuffmanNode root;

    private static void buildHuffmanTree(String data) {
        Map<Character, Integer> frequencyMap = buildFrequencyMap(data);
        PriorityQueue<HuffmanNode> priorityQueue = buildPriorityQueue(frequencyMap);

        root = buildHuffmanTree(priorityQueue);
        huffmanCodeMap = new HashMap<>();
        generateHuffmanCodes(root, "", huffmanCodeMap);
    }

    private static Map<Character, Integer> buildFrequencyMap(String data) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : data.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }
        return frequencyMap;
    }

    private static PriorityQueue<HuffmanNode> buildPriorityQueue(Map<Character, Integer> frequencyMap) {
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>(new HuffmanComparator());
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            priorityQueue.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }
        return priorityQueue;
    }

    private static HuffmanNode buildHuffmanTree(PriorityQueue<HuffmanNode> priorityQueue) {
        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll();
            HuffmanNode right = priorityQueue.poll();
            HuffmanNode parent = new HuffmanNode('\0', left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;
            priorityQueue.add(parent);
        }
        return priorityQueue.poll();
    }

    private static void generateHuffmanCodes(HuffmanNode root, String code, Map<Character, String> huffmanCodeMap) {
        if (root == null) return;
        if (root.left == null && root.right == null) {
            huffmanCodeMap.put(root.character, code);
        }
        generateHuffmanCodes(root.left, code + '0', huffmanCodeMap);
        generateHuffmanCodes(root.right, code + '1', huffmanCodeMap);
    }

    private static String encodeData(String data, Map<Character, String> huffmanCodeMap) {
        StringBuilder encodedData = new StringBuilder();
        for (char c : data.toCharArray()) {
            encodedData.append(huffmanCodeMap.get(c));
        }
        return encodedData.toString();
    }

    private static String decodeData(String encodedData, HuffmanNode root) {
        StringBuilder decodedData = new StringBuilder();
        HuffmanNode current = root;
        for (char c : encodedData.toCharArray()) {
            if (c == '0') {
                current = current.left;
            } else {
                current = current.right;
            }
            if (current.left == null && current.right == null) {
                decodedData.append(current.character);
                current = root;
            }
        }
        return decodedData.toString();
    }

    public static String encode(String data) {
        buildHuffmanTree(data);
        return encodeData(data, huffmanCodeMap);
    }

    public static String decode(String encodedData) {
        return decodeData(encodedData, root);
    }
}

 class HuffmanCodingGUI {

    private static JTextArea inputTextArea;
    private static JTextArea encodedTextArea;
    private static JTextArea decodedTextArea;
    private static JLabel statusLabel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Huffman Coding");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.add(panel);

        // Text area panel
        JPanel textAreaPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        textAreaPanel.setBorder(BorderFactory.createTitledBorder("Text Areas"));

        inputTextArea = new JTextArea();
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        textAreaPanel.add(createLabeledPanel("Input Text", inputScrollPane));

        encodedTextArea = new JTextArea();
        encodedTextArea.setLineWrap(true);
        encodedTextArea.setWrapStyleWord(true);
        encodedTextArea.setEditable(false);
        JScrollPane encodedScrollPane = new JScrollPane(encodedTextArea);
        textAreaPanel.add(createLabeledPanel("Encoded Text", encodedScrollPane));

        decodedTextArea = new JTextArea();
        decodedTextArea.setLineWrap(true);
        decodedTextArea.setWrapStyleWord(true);
        decodedTextArea.setEditable(false);
        JScrollPane decodedScrollPane = new JScrollPane(decodedTextArea);
        textAreaPanel.add(createLabeledPanel("Decoded Text", decodedScrollPane));

        panel.add(textAreaPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton encodeButton = new JButton("Encode");
        JButton decodeButton = new JButton("Decode");
        JButton loadFileButton = new JButton("Load File");
        JButton saveFileButton = new JButton("Save File");
        buttonPanel.add(loadFileButton);
        buttonPanel.add(saveFileButton);
        buttonPanel.add(encodeButton);
        buttonPanel.add(decodeButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Status label
        statusLabel = new JLabel("Status: Ready");
        panel.add(statusLabel, BorderLayout.NORTH);

        // Action listeners
        encodeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String inputData = inputTextArea.getText();
                    if (inputData.isEmpty()) {
                        statusLabel.setText("Status: Input text is empty.");
                        return;
                    }
                    String encodedData = HuffmanCoding.encode(inputData);
                    encodedTextArea.setText(encodedData);
                    statusLabel.setText("Status: Encoding completed.");
                } catch (Exception ex) {
                    statusLabel.setText("Status: Error during encoding.");
                }
            }
        });

        decodeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String encodedData = encodedTextArea.getText();
                    if (encodedData.isEmpty()) {
                        statusLabel.setText("Status: Encoded text is empty.");
                        return;
                    }
                    String decodedData = HuffmanCoding.decode(encodedData);
                    decodedTextArea.setText(decodedData);
                    statusLabel.setText("Status: Decoding completed.");
                } catch (Exception ex) {
                    statusLabel.setText("Status: Error during decoding.");
                }
            }
        });

        loadFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        inputTextArea.setText("");
                        String line;
                        while ((line = reader.readLine()) != null) {
                            inputTextArea.append(line + "\n");
                        }
                        statusLabel.setText("Status: File loaded.");
                    } catch (IOException ex) {
                        statusLabel.setText("Status: Error loading file.");
                    }
                }
            }
        });

        saveFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showSaveDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        writer.write(encodedTextArea.getText());
                        statusLabel.setText("Status: File saved.");
                    } catch (IOException ex) {
                        statusLabel.setText("Status: Error saving file.");
                    }
                }
            }
        });

        frame.setVisible(true);
    }

    private static JPanel createLabeledPanel(String labelText, JScrollPane scrollPane) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        panel.add(label, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
}
