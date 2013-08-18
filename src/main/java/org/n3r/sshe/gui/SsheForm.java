package org.n3r.sshe.gui;

import org.n3r.sshe.SsheMain;
import org.n3r.sshe.SsheOutput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
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
                textAreaResult.setText("");
            }
        });
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

        JFrame frame = new JFrame("SSH-E GUI v0.2");
        frame.setContentPane(new SsheForm(executorService).panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        //frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);  // centered on screen
        frame.setExtendedState(Frame.MAXIMIZED_BOTH); // set maximized

        frame.setVisible(true);
    }
}
