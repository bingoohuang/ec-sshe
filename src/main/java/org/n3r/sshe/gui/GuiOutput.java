package org.n3r.sshe.gui;

import org.apache.commons.lang3.StringUtils;
import org.n3r.sshe.SsheOutput;
import org.n3r.sshe.util.Util;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class GuiOutput implements SsheOutput {
    private final JButton btnContinue;
    private final JTextArea textAreaResult;

    public GuiOutput(JButton btnContinue, JTextArea textAreaResult) {
        this.btnContinue = btnContinue;
        this.textAreaResult = textAreaResult;
    }

    @Override
    public void print(String x) {
        updateTextArea(x);
    }

    @Override
    public void println(String x) {
        updateTextArea(x + "\r\n");
    }

    @Override
    public void println() {
        updateTextArea("\r\n");
    }

    @Override
    public void waitConfirm(int maxWaitMillis) {
        String str = "[Please press continue button to continue]";
        updateTextArea(str);

        btnContinue.setEnabled(true);
        btnContinue.setText("Continue(0s)");
        btnContinue.setVisible(true);
        btnContinue.grabFocus();
        long start = System.currentTimeMillis();

        while (btnContinue.isEnabled()) {
            long cost = System.currentTimeMillis() - start;
            if (maxWaitMillis > 0 && cost > maxWaitMillis) break;

            btnContinue.setText("Continue(" + cost / 1000 + "s)");
            Util.sleepMillis(1000);
        }

        updateTextArea(StringUtils.repeat('\b', str.length()));

        btnContinue.setVisible(false);
    }


    public void updateTextArea(String str) {
        Document document = textAreaResult.getDocument();

        try {
            for (int i = 0, ii = str.length(); i < ii; ++i) {
                char ch = str.charAt(i);
                if (ch == '\b') {
                    document.remove(document.getLength() - 1, 1);
                } else {
                    document.insertString(document.getLength(), "" + ch, null);
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        // textAreaResult.append(str);
        // scrolls the text area to the end of data
        textAreaResult.setCaretPosition(document.getLength());
    }
}
