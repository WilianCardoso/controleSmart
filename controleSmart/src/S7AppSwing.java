import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class S7AppSwing extends JFrame {

    public S7AppSwing() {
        setTitle("Leitura e Escrita de TAGs no CLP - Protocolo S7");
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel labelIp = new JLabel("Ip Host");
        labelIp.setBounds(50, 10, 100, 30);
        add(labelIp);

        JTextField textIp = new JTextField("10.74.241.10");
        textIp.setBounds(150, 10, 200, 30);
        add(textIp);

        JLabel labelDb = new JLabel("DB: ");
        labelDb.setBounds(50, 50, 100, 30);
        add(labelDb);

        JTextField textDb = new JTextField("6");
        textDb.setBounds(150, 50, 200, 30);
        add(textDb);

        JLabel labelType = new JLabel("Tipo: ");
        labelType.setBounds(50, 100, 100, 30);

        JComboBox<String> comboType = new JComboBox<>(
                new String[] { "String", "Block", "Integer", "Float", "Byte", "Boolean" });
        comboType.setBounds(150, 100, 200, 30);
        add(comboType);

        JLabel labelOffSet = new JLabel("Offset: ");
        labelOffSet.setBounds(50, 150, 100, 30);
        add(labelOffSet);

        JTextField textOffSet = new JTextField("16");
        textOffSet.setBounds(150, 150, 200, 30);
        add(textOffSet);

        JLabel labelBitNumber = new JLabel("Bit Number");
        labelBitNumber.setBounds(50, 200, 100, 30);
        add(labelBitNumber);

        JTextField textBitNumber = new JTextField("0");
        textBitNumber.setBounds(150, 200, 200, 30);

        JLabel labelSize = new JLabel("Tamanho: ");
        labelSize.setBounds(50, 250, 100, 30);
        add(labelSize);

        JTextField textSize = new JTextField("14");
        textSize.setBounds(150, 250, 200, 30);
        add(textSize);

        comboType.addActionListener((ActionEvent e) -> {

            String selectedItem = (String) comboType.getSelectedItem();
            switch (selectedItem) {
                case "Boolean" -> textSize.setText("1");
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
                case "String" -> textBitNumber.setText("0");
            }
        });

        JButton buttonRead = new JButton("Ler TAG");
        buttonRead.setBounds(150, 300, 200, 30);
        add(buttonRead);

        JLabel labelValueRead = new JLabel("Valor Lido:");
        labelValueRead.setBounds(50, 350, 200, 30);
        add(labelValueRead);

        JTextField textValue = new JTextField();
        textValue.setBounds(150, 350, 200, 30);
        textValue.setEditable(false);
        add(textValue);

        JButton buttonWrite = new JButton("Escrever TAG");
        buttonWrite.setBounds(150, 400, 200, 30);
        add(buttonWrite);

        JLabel labelValue = new JLabel("Valor Escrito");
        labelValue.setBounds(40, 450, 100, 30);

        JTextField textValueWrite = new JTextField();
        textValueWrite.setBounds(150, 450, 200, 30);
        add(textValueWrite);

        JButton buttonLeituras = new JButton("Leitura Cíclica");
        buttonLeituras.setBounds(150, 500, 200, 30);
        add(buttonLeituras);

        buttonLeituras.addActionListener((ActionEvent e) -> {

        });

        buttonRead.addActionListener((ActionEvent e) -> {
            try {
                int db = Integer.parseInt(textDb.getText());
                int offSet = Integer.parseInt(textOffSet.getText());
                int bitNumber = !textBitNumber.getText().equals("") ? Integer.parseInt(textBitNumber.getText()) : -1;
                int size = Integer.parseInt(textSize.getText());
                String type = (String) comboType.getSelectedItem();

                PlcConnector plc = new PlcConnector(textIp.getText().trim(), 102);
                plc.connect();

                switch (type.toLowerCase()) {
                    case "string" -> {
                        String str = plc.readString(db, offSet, size);
                        textValue.setText(str);
                    }
                    case "block" -> {
                        String blk = bytesToHex(plc.readBlock(db, offSet, size), size);
                        textValue.setText(blk);
                    }
                    case "float" -> {
                        float flt = plc.readFloat(db, offSet);
                        textValue.setText(String.valueOf(flt));
                    }
                    case "integer" -> {
                        int val = plc.readInt(db, offSet);
                        textValue.setText(String.valueOf(val));
                    }
                    case "byte" -> {
                        byte byt = plc.readByte(db, offSet);
                        textValue.setText(String.valueOf(byt));
                    }
                    case "boolean" -> {
                        boolean bit = plc.readBit(db, offSet, bitNumber);
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
                int db = Integer.parseInt(textDb.getText());
                int offSet = Integer.parseInt(textOffSet.getText());
                int bitNumber = !textBitNumber.getText().equals("") ? Integer.parseInt(textBitNumber.getText()) : -1;
                int size = Integer.parseInt(textSize.getText());
                String type = (String) comboType.getSelectedItem();

                PlcConnector plc = new PlcConnector(textIp.getText().trim(), 102);
                plc.connect();

                switch (type.toLowerCase()) {
                    case "string" -> {
                        if (plc.writeString(db, offSet, size, textValueWrite.getText().trim())) {
                            System.out.println("Escrita no CLP realizada com Suceso");
                        } else {
                            System.out.println("Erro da escrita do CLP");
                        }
                    }
                    case "block" -> {
                        if (plc.writeBlock(db, offSet, size,
                                PlcConnector.hexStringToByteArray(textValueWrite.getText().trim()))) {
                            System.out.println("Escrita no CLP realizada com Suceso");
                        } else {
                            System.out.println("Erro da escrita do CLP");
                        }
                    }
                    case "float" -> {
                        if (plc.writeFloat(db, offSet, Float.parseFloat(textValueWrite.getText().trim()))) {
                            System.out.println("Escrita no CLP realizada com Suceso");
                        } else {
                            System.out.println("Erro da escrita do CLP");
                        }
                    }
                    case "integer" -> {
                        if(plc.writeInt(db, offSet, Integer.parseInt(textValueWrite.getText().trim()))) {
                            System.out.println("Escrita no CLP realizada com Suceso");
                        } else {
                            System.out.println("Erro da escrita do CLP");
                        }
                    }
                    case "byte" -> {
                        if(plc.writeByte(db, offSet, Byte.parseByte(textValueWrite.getText().trim()))){
                            System.out.println("Escrita no CLP realizada com Suceso");
                        } else {
                            System.out.println("Erro da escrita do CLP");
                        }
                    }
                    case "boolean" -> {
                        if(plc.writeBit(db, offSet, bitNumber, Boolean.parseBoolean(textValueWrite.getText().trim()))){
                            System.out.println("Escrita no CLP realizada com Suceso");
                        } else {
                            System.out.println("Erro da escrita do CLP");
                        }
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static String bytesToHex(byte[] bytes, int lenght){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < lenght; i++){
            sb.append(String.format("%02X", bytes[i]));
        }
        return sb.toString().trim();
    }

    public static void updateTextField(int id, byte[] bytes) {}

    public static void extracted(int id, byte[] bytes) {}

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            S7AppSwing app = new S7AppSwing();
            app.setVisible(true);
        });
    }
}
