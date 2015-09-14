package com.github.shyykoserhiy.gfm.toolwindow.browser;

import com.github.shyykoserhiy.gfm.browser.IsBrowser;
import com.github.shyykoserhiy.gfm.toolwindow.browser.resources.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JSConsole extends JPanel {

    private static final String NEW_LINE = "\n";
    private static final String QUERY_LINE_START = ">> ";

    private JTextArea console;
    private final IsBrowser browser;
    private final ExecutorService executor;

    public JSConsole(IsBrowser browser) {
        this.browser = browser;
        this.executor = Executors.newCachedThreadPool();
        setLayout(new BorderLayout());
        add(createTitle(), BorderLayout.NORTH);
        add(createConsoleOutput(), BorderLayout.CENTER);
        add(createConsoleInput(), BorderLayout.SOUTH);
    }

    private JComponent createConsoleInput() {
        JPanel result = new JPanel(new BorderLayout());
        result.setBackground(Color.WHITE);

        JLabel label = new JLabel(QUERY_LINE_START);
        label.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 0));

        final JTextField consoleInput = new JTextField();
        consoleInput.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        consoleInput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executor.submit(new Runnable() {
                    public void run() {
                        final String script = consoleInput.getText();
                        browser.executeJavaScriptAndReturnValue(script, new IsBrowser.JsExecutedListener() {
                            @Override
                            public void onJsExecuted(final String result) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        updateConsoleOutput(script, result);
                                        consoleInput.setText("");
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
        result.add(label, BorderLayout.WEST);
        result.add(consoleInput, BorderLayout.CENTER);
        return result;
    }

    private JComponent createConsoleOutput() {
        console = new JTextArea();
        console.setFont(new Font("Consolas", Font.PLAIN, 12));
        console.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        console.setEditable(false);
        console.setWrapStyleWord(true);
        console.setLineWrap(true);
        console.setText("");
        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        return scrollPane;
    }

    private JComponent createTitle() {
        JPanel panel = new JPanel(new BorderLayout());
//        panel.setBackground(new Color(182, 191, 207));
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        panel.add(createTitleLabel(), BorderLayout.WEST);
        panel.add(createCloseButton(), BorderLayout.EAST);
        return panel;
    }

    private static JComponent createTitleLabel() {
        return new JLabel("JavaScript Console");
    }

    private JComponent createCloseButton() {
        JButton closeButton = new JButton();
        closeButton.setOpaque(false);
        closeButton.setToolTipText("Close JavaScript Console");
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        closeButton.setPressedIcon(Resources.getIcon("close-pressed.png"));
        closeButton.setIcon(Resources.getIcon("close.png"));
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusable(false);
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                firePropertyChange("JSConsoleClosed", false, true);
            }
        });
        return closeButton;
    }

    private void updateConsoleOutput(String script, String executionResult) {
        displayScript(script);
        displayExecutionResult(executionResult);
        console.setCaretPosition(console.getText().length());
    }

    private void displayExecutionResult(String result) {
        console.append(result);
        console.append(NEW_LINE);
    }

    private void displayScript(String script) {
        console.append(QUERY_LINE_START);
        console.append(script);
        console.append(NEW_LINE);
    }
}
