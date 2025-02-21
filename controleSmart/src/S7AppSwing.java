
import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class S7AppSwing extends JFrame {

    public static boolean leitura = true;

    public static byte[] indxColor = new byte[28];

    public JTextField textIp;
    public JPanel pnLBlkEst;
    public PlcConnector plcEstoque;

    @SuppressWarnings("unused")
    public S7AppSwing() {
        setTitle("Leitura e Escrita de TAGs no CLP - Protocolo S7");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        // Criar painel onde os quadrados serão desenhados
        pnLBlkEst = new JPanel();
        pnLBlkEst.setBounds(380, 10, 275, 245);
        Border borderPanel = BorderFactory.createLineBorder(Color.BLACK, 1);
        pnLBlkEst.setBorder(borderPanel);
        pnLBlkEst.setLayout(null);
        add(pnLBlkEst);
        updatePnlBlocks();

        JLabel labelIp = new JLabel("Ip Host:");
        labelIp.setBounds(50, 10, 100, 30);
        add(labelIp);

        textIp = new JTextField("10.74.241.10");
        textIp.setBounds(150, 10, 200, 30);
        add(textIp);

        JLabel labelDB = new JLabel("DB:");
        labelDB.setBounds(50, 50, 100, 30);
        add(labelDB);

        JTextField textDB = new JTextField("6");
        textDB.setBounds(150, 50, 200, 30);
        add(textDB);

        JLabel labelType = new JLabel("Tipo:");
        labelType.setBounds(50, 100, 100, 30);
        add(labelType);

        JComboBox<String> comboType = new JComboBox<>(
                new String[]{"String", "Block", "Integer", "Float", "Byte", "Boolean"});
        comboType.setBounds(150, 100, 200, 30);
        add(comboType);

        JLabel labelOffset = new JLabel("Offset:");
        labelOffset.setBounds(50, 150, 100, 30);
        add(labelOffset);

        JTextField textOffset = new JTextField("16");
        textOffset.setBounds(150, 150, 200, 30);
        add(textOffset);

        JLabel labelBitNumber = new JLabel("Bit Number:");
        labelBitNumber.setBounds(50, 200, 100, 30);
        add(labelBitNumber);

        JTextField textBitNumber = new JTextField("0");
        textBitNumber.setBounds(150, 200, 200, 30);
        add(textBitNumber);

        JLabel labelSize = new JLabel("Tamanho");
        labelSize.setBounds(50, 250, 100, 30);
        add(labelSize);

        JTextField textSize = new JTextField("14");
        textSize.setBounds(150, 250, 200, 30);
        add(textSize);

        comboType.addActionListener((ActionEvent e) -> {
            String selectedItem = (String) comboType.getSelectedItem();
            switch (selectedItem) {
                case "Boolean" ->
                    textSize.setText("1");
                case "Byte" -> {
                    textSize.setText("1");
                    textBitNumber.setText("0");
                }
                case "Integer" -> {
                    textSize.setText("2");
                    textBitNumber.setText("0");
                }
                case "Float" -> {
                    textSize.setText("4");
                    textBitNumber.setText("0");
                }
                case "String" ->
                    textBitNumber.setText("0");
            }
        });

        JButton buttonRead = new JButton("Ler TAG");
        buttonRead.setBounds(150, 300, 200, 30);
        add(buttonRead);

        JLabel labelValueRead = new JLabel("Valor lido:");
        labelValueRead.setBounds(50, 350, 200, 30);
        add(labelValueRead);

        JTextField textValue = new JTextField();
        textValue.setBounds(150, 350, 200, 30);
        textValue.setEditable(false);
        add(textValue);

        JButton buttonWrite = new JButton("Escrever TAG");
        buttonWrite.setBounds(150, 400, 200, 30);
        add(buttonWrite);

        JLabel labelValue = new JLabel("Valor Escrito: ");
        labelValue.setBounds(50, 450, 100, 30);
        add(labelValue);

        JTextField textValueWrite = new JTextField();
        textValueWrite.setBounds(150, 450, 200, 30);
        add(textValueWrite);

        JButton buttonLeituras = new JButton("Leitura Ciclica");
        buttonLeituras.setBounds(150, 500, 200, 30);
        add(buttonLeituras);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(380, 260, 275, 30);
        add(btnUpdate);

        btnUpdate.addActionListener((ActionEvent e) -> {
            updatePnlBlocks();
        });
        buttonLeituras.addActionListener((ActionEvent e) -> {

        });

        buttonRead.addActionListener((ActionEvent e) -> {
            try {
                int db = Integer.parseInt(textDB.getText());
                int offset = Integer.parseInt(textOffset.getText());
                int bitNumber = !textBitNumber.getText().equals("") ? Integer.parseInt(textBitNumber.getText()) : -1;
                int size = Integer.parseInt(textSize.getText());
                String type = (String) comboType.getSelectedItem();

                PlcConnector plc = new PlcConnector(textIp.getText().trim(), 102);
                plc.connect();

                switch (type.toLowerCase()) {
                    case "string" -> {
                        String str = plc.readString(db, offset, size);
                        textValue.setText(str);
                    }
                    case "block" -> {
                        String blk = bytesToHex(plc.readBlock(db, offset, size), size);
                        textValue.setText(blk);
                    }
                    case "float" -> {
                        float flt = plc.readFloat(db, offset);
                        textValue.setText(String.valueOf(flt));
                    }
                    case "integer" -> {
                        int val = plc.readInt(db, offset);
                        textValue.setText(String.valueOf(val));
                    }
                    case "byte" -> {
                        byte byt = plc.readByte(db, offset);
                        textValue.setText(String.valueOf(byt));
                    }
                    case "boolean" -> {
                        boolean bit = plc.readBit(db, offset, bitNumber);
                        textValue.setText(String.valueOf(bit));
                    }
                    default -> {

                    }
                }
                plc.disconnect();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonWrite.addActionListener((ActionEvent e) -> {
            try {
                int db = Integer.parseInt(textDB.getText());
                int offset = Integer.parseInt(textOffset.getText());
                int bitNumber = !textBitNumber.getText().equals("") ? Integer.parseInt(textBitNumber.getText()) : -1;
                int size = Integer.parseInt(textSize.getText());
                String type = (String) comboType.getSelectedItem();

                PlcConnector plc = new PlcConnector(textIp.getText().trim(), 102);
                plc.connect();

                switch (type.toLowerCase()) {
                    case "string" -> {
                        if (plc.writeString(db, offset, size, textValueWrite.getText().trim())) {
                            System.out.println("Escrita no CLP realizada com sucesso!");
                        } else {
                            System.err.println("Erro de escrita no CLP!");
                        }
                    }
                    case "block" -> {
                        if (plc.writeBlock(db, offset, size,
                                PlcConnector.hexStringToByteArray(textValueWrite.getText().trim()))) {
                            System.out.println("Escrita no CLP realizada com sucesso!");
                        } else {
                            System.err.println("Erro de escrita no CLP!");
                        }
                    }
                    case "float" -> {
                        if (plc.writeFloat(db, offset, Float.parseFloat(textValueWrite.getText().trim()))) {
                            System.out.println("Escrita no CLP realizada com sucesso!");
                        } else {
                            System.err.println("Erro de escrita no CLP!");
                        }
                    }
                    case "integer" -> {
                        if (plc.writeInt(db, offset, Integer.parseInt(textValueWrite.getText().trim()))) {
                            System.out.println("Escrita no CLP realizada com sucesso!");
                        } else {
                            System.err.println("Erro de escrita no CLP!");
                        }
                    }
                    case "byte" -> {
                        if (plc.writeByte(db, offset, Byte.parseByte(textValueWrite.getText().trim()))) {
                            System.out.println("Escrita no CLP realizada com sucesso!");
                        } else {
                            System.err.println("Erro de escrita no CLP!");
                        }
                    }
                    case "boolean" -> {
                        if (plc.writeBit(db, offset, bitNumber,
                                Boolean.parseBoolean(textValueWrite.getText().trim()))) {
                            System.out.println("Escrita no CLP realizada com sucesso!");
                        } else {
                            System.err.println("Erro de escrita no CLP!");
                        }
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Atualiza os blocos após a criação da interface
        SwingUtilities.invokeLater(() -> updatePnlBlocks());
    }

    private static String bytesToHex(byte[] bytes, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%02X", bytes[i]));
        }
        return sb.toString().trim();
    }

    public static void updateTextField(int id, byte[] bytes) {

    }

    private static void extracted(int id, byte[] bytes) {

    }

    private void updatePnlBlocks() {

        // Verifica se textIp foi inicializado
        if (textIp == null) {
            System.out.println("Erro: textIp não foi inicializado!");
            return; // Sai do método se textIp for null
        }
        plcEstoque = new PlcConnector(textIp.getText().trim(), 102);
        try {
            plcEstoque.connect();
        } catch (Exception e) {

            e.printStackTrace();
        }

        try {
            indxColor = plcEstoque.readBlock(9, 68, 28);
        } catch (Exception e) {

            e.printStackTrace();
        }

        int largura = 35; // Largura do bloco
        int altura = 35; // Altura do bloco
        int espaco = 10;  // Espaço entre os blocos

        for (int i = 0; i < 28; i++) {
            JLabel label = new JLabel("P" + (i + 1), SwingConstants.CENTER);
            // Define o tamanho do JPanel
            label.setSize(largura, altura);
            //adiciona uma borda para visualização
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            // Calcula a posição do JPanel
            int x = (i % 6) * (largura + espaco);
            int y = (i / 6) * (altura + espaco);
            label.setLocation(x + 10, y + 10);
            // Adicionar o JLabel ao JPanel
            pnLBlkEst.add(label);
            pnLBlkEst.revalidate();
            pnLBlkEst.repaint();
        }
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            S7AppSwing app = new S7AppSwing();
            app.setVisible(true);
        });
    }
}
