package com.github.shyykoserhiy.gfm.toolwindow.browser;

import com.github.shyykoserhiy.gfm.browser.IsBrowser;
import com.github.shyykoserhiy.gfm.toolwindow.browser.resources.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ToolBar extends JPanel {
    private static final String RUN_JAVASCRIPT = "Open JavaScript Console";
    private static final String CLOSE_JAVASCRIPT = "Close JavaScript Console";
    private static final String DEFAULT_URL = "about:blank";

    private JButton backwardButton;
    private JButton forwardButton;
    private JButton refreshButton;
    private JButton stopButton;
    private JMenuItem consoleMenuItem;

    private final JTextField addressBar;
    private final IsBrowser browser;

    public ToolBar(IsBrowser browser) {
        this.browser = browser;
        addressBar = createAddressBar();
        setLayout(new GridBagLayout());
        add(createActionsPane(), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(addressBar, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(4, 0, 4, 5), 0, 0));
        add(createMenuButton(), new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.LINE_END, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
    }

    public void didJSConsoleClose() {
        consoleMenuItem.setText(RUN_JAVASCRIPT);
    }

    private JPanel createActionsPane() {
        backwardButton = createBackwardButton(browser);
        forwardButton = createForwardButton(browser);
        refreshButton = createRefreshButton(browser);
        stopButton = createStopButton(browser);

        JPanel actionsPanel = new JPanel();
        actionsPanel.add(backwardButton);
        actionsPanel.add(forwardButton);
        actionsPanel.add(refreshButton);
        actionsPanel.add(stopButton);
        return actionsPanel;
    }

    private JTextField createAddressBar() {
        final JTextField result = new JTextField(DEFAULT_URL);
        result.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browser.loadUrl(result.getText());
            }
        });


        browser.addLoadListener(new IsBrowser.LoadListener() {
            @Override
            public void onStartLoadingFrame() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        refreshButton.setEnabled(false);
                        stopButton.setEnabled(true);
                    }
                });
            }

            @Override
            public void onProvisionalLoadingFrame() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        result.setText(browser.getUrl());
                        result.setCaretPosition(result.getText().length());
                    }
                });

            }

            @Override
            public void onFinishLoadingFrame() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        result.setText(browser.getUrl());
                        result.setCaretPosition(result.getText().length());
                        refreshButton.setEnabled(true);
                        stopButton.setEnabled(false);

                        final boolean canGoForward = browser.canGoForward();
                        final boolean canGoBack = browser.canGoBack();
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                forwardButton.setEnabled(canGoForward);
                                backwardButton.setEnabled(canGoBack);
                            }
                        });
                    }
                });
            }
        });
        return result;
    }

    private static JButton createBackwardButton(final IsBrowser browser) {
        return createButton("Back", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                browser.goBack();
            }
        });
    }

    private static JButton createForwardButton(final IsBrowser browser) {
        return createButton("Forward", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                browser.goForward();
            }
        });
    }

    private static JButton createRefreshButton(final IsBrowser browser) {
        return createButton("Refresh", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                browser.reload();
            }
        });
    }

    private static JButton createStopButton(final IsBrowser browser) {
        return createButton("Stop", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                browser.stop();
            }
        });
    }

    private static JButton createButton(String caption, Action action) {
        ActionButton button = new ActionButton(caption, action);
        String imageName = caption.toLowerCase();
        button.setIcon(Resources.getIcon(imageName + ".png"));
        button.setRolloverIcon(Resources.getIcon(imageName + "-selected.png"));
        return button;
    }

    private JComponent createMenuButton() {
        final JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(createConsoleMenuItem());

        final ActionButton button = new ActionButton("Preferences", null);
        button.setIcon(Resources.getIcon("gear.png"));
        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
                    popupMenu.show(e.getComponent(), 0, button.getHeight());
                } else {
                    popupMenu.setVisible(false);
                }
            }
        });
        return button;
    }

    private JMenuItem createConsoleMenuItem() {
        consoleMenuItem = new JMenuItem(RUN_JAVASCRIPT);
        consoleMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (RUN_JAVASCRIPT.equals(consoleMenuItem.getText())) {
                    consoleMenuItem.setText(CLOSE_JAVASCRIPT);
                    firePropertyChange("JSConsoleDisplayed", false, true);
                } else {
                    consoleMenuItem.setText(RUN_JAVASCRIPT);
                    firePropertyChange("JSConsoleClosed", false, true);
                }
            }
        });
        return consoleMenuItem;
    }

    private boolean isFocusRequired() {
        String url = addressBar.getText();
        return url.isEmpty() || url.equals(DEFAULT_URL);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (isFocusRequired()) {
                    addressBar.requestFocus();
                    addressBar.selectAll();
                }
            }
        });
    }

    private static class ActionButton extends JButton {
        private ActionButton(String hint, Action action) {
            super(action);
            setContentAreaFilled(false);
            setBorder(BorderFactory.createEmptyBorder());
            setBorderPainted(false);
            setRolloverEnabled(true);
            setToolTipText(hint);
            setText(null);
            setFocusable(false);
            setDefaultCapable(false);
        }
    }

}
