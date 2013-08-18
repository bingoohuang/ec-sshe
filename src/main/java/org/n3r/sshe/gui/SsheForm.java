package org.n3r.sshe.gui;

import static org.apache.commons.lang3.StringUtils.*;

import org.n3r.sshe.SsheConf;
import org.n3r.sshe.SsheMain;
import org.n3r.sshe.SsheOutput;
import org.n3r.sshe.security.AESEncrypter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class SsheForm {
    private final ExecutorService executorService;
    private JPanel panel1;
    private JButton btnRun;
    private JTabbedPane tabbedPane1;
    private JTextArea textAreaConfig;
    private JTextArea textAreaResult;
    private JButton btnCleanResult;
    private JTextField textFieldKey;
    private JTextField textFieldDest;
    private JTextField textFieldSource;

    public SsheForm(final ExecutorService executorService) {
        this.executorService = executorService;
        btnRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane1.setSelectedIndex(1);
                final SsheOutput ssheOutput = new SsheOutput() {
                    @Override
                    public void print(String x) {
                        updateTextArea(x);
                    }

                    @Override
                    public void println(String x) {
                        updateTextArea(x + "\r\n" );
                    }

                    @Override
                    public void println() {
                        updateTextArea("\r\n" );
                    }
                };

                FutureTask<Void> task = new FutureTask<Void>(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        try {
                            SsheMain.runGUI(textAreaConfig.getText(), ssheOutput);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        return null;
                    }
                });

                executorService.submit(task);
            }
        });

        btnCleanResult.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textAreaResult.setText("" );
            }
        });

        textFieldKey.setText(SsheConf.key);
        // Listen for changes in the text
        textFieldSource.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                onTextFieldSourceChange();
            }

            public void removeUpdate(DocumentEvent e) {
                onTextFieldSourceChange();
            }

            public void insertUpdate(DocumentEvent e) {
                onTextFieldSourceChange();
            }
        });

        textFieldDest.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                onTextFieldDestChange();
            }

        });
    }

    public void onTextFieldDestChange() {
        if (isEmpty(textFieldKey.getText())) return;
        if (isEmpty(textFieldDest.getText())) return;
        String text = trim(textFieldDest.getText());
        if (startsWith(text, "{AES}" )) text = trim(substring(text, 5));

        try {
            textFieldSource.setText(new AESEncrypter(textFieldKey.getText()).decrypt(text));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Fail to Decrypt", "Error Massage", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void onTextFieldSourceChange() {
        if (isEmpty(textFieldKey.getText())) textFieldKey.setText(AESEncrypter.createKey());

        textFieldDest.setText("{AES}" + new AESEncrypter(textFieldKey.getText()).encrypt(textFieldSource.getText()));
    }

    public void updateTextArea(String str) {
        // redirects data to the text area
        textAreaResult.append(str);
        // scrolls the text area to the end of data
        textAreaResult.setCaretPosition(textAreaResult.getDocument().getLength());
    }


    public static void main(String[] args) {
        runGUI();
    }

    public static void runGUI() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        JFrame frame = new JFrame("SSH-E GUI v0.2.1" );
        frame.setContentPane(new SsheForm(executorService).panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        //frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);  // centered on screen
        frame.setExtendedState(Frame.MAXIMIZED_BOTH); // set maximized

        frame.setVisible(true);
    }

}

