package org.n3r.sshe.gui;

import javax.swing.*;
import java.io.*;

public class CharOutputStream extends OutputStream {

    private PipedOutputStream out = new PipedOutputStream();
    private Reader reader;
    private JTextArea textAreaResult;

    public CharOutputStream(JTextArea textAreaResult, String charset) throws IOException {
        this.textAreaResult = textAreaResult;
        PipedInputStream in = new PipedInputStream(out);
        reader = new InputStreamReader(in, charset);
    }

    public void write(int i) throws IOException {
        out.write(i);
    }

    public void write(byte[] bytes, int i, int i1) throws IOException {
        out.write(bytes, i, i1);
    }

    public void flush() throws IOException {
        if (!reader.ready()) return;

        char[] chars = new char[1024];
        int n = reader.read(chars);

        String txt = new String(chars, 0, n);
        updateTextArea(txt);
    }

    public void updateTextArea(String str) {
        textAreaResult.append(str);
        // scrolls the text area to the end of data
        int length = textAreaResult.getDocument().getLength();
        textAreaResult.setCaretPosition(length);
    }
}
