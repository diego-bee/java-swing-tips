// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Objects;
import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());

    JScrollPane s1 = new JScrollPane(new JTable(5, 3)) {
      @Override public Dimension getMinimumSize() {
        return new Dimension(0, 100);
      }
    };
    JScrollPane s2 = new JScrollPane(new JTree()) {
      @Override public Dimension getMinimumSize() {
        return new Dimension(0, 100);
      }
    };

    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    splitPane.setTopComponent(s1);
    splitPane.setBottomComponent(s2);
    splitPane.setOneTouchExpandable(true);

    Action minAction = new AbstractAction("Min:Action") {
      @Override public void actionPerformed(ActionEvent e) {
        splitPane.requestFocusInWindow();
        Action selectMin = splitPane.getActionMap().get("selectMin");
        EventQueue.invokeLater(() -> {
          selectMin.actionPerformed(new ActionEvent(splitPane, e.getID(), "selectMin"));
        });
      }
    };
    Action maxAction = new AbstractAction("Max:Action") {
      @Override public void actionPerformed(ActionEvent e) {
        splitPane.requestFocusInWindow();
        Action selectMax = splitPane.getActionMap().get("selectMax");
        EventQueue.invokeLater(() -> {
          selectMax.actionPerformed(new ActionEvent(splitPane, e.getID(), e.getActionCommand()));
        });
      }
    };

    JPanel north = new JPanel(new GridLayout(0, 2, 5, 5));
    north.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    north.add(new JButton(new AbstractAction("Min:DividerLocation") {
      @Override public void actionPerformed(ActionEvent e) {
        splitPane.setDividerLocation(0);
      }
    }));
    north.add(new JButton(new AbstractAction("Max:DividerLocation") {
      @Override public void actionPerformed(ActionEvent e) {
        Insets i = splitPane.getInsets();
        if (splitPane.getOrientation() == JSplitPane.VERTICAL_SPLIT) {
          int v = Objects.nonNull(i) ? i.bottom : 0;
          splitPane.setDividerLocation(splitPane.getHeight() - v);
        } else {
          int v = Objects.nonNull(i) ? i.right : 0;
          splitPane.setDividerLocation(splitPane.getWidth() - v);
        }
        // int lastLoc = splitPane.getLastDividerLocation();
        // int currentLoc = splitPane.getDividerLocation();
        // int newLoc;
        // BasicSplitPaneUI splitPaneUI = (BasicSplitPaneUI) splitPane.getUI();
        // Container divider = ((BasicSplitPaneUI) splitPane.getUI()).getDivider();
        //
        // if (currentLoc == insets.top) {
        //   int maxLoc = splitPane.getMaximumDividerLocation();
        //   newLoc = Math.min(lastLoc, maxLoc);
        //   // splitPaneUI.setKeepHidden(false);
        // } else {
        //   newLoc = splitPane.getHeight() - divider.getHeight() - insets.top;
        //   // splitPaneUI.setKeepHidden(true);
        // }
        // if (currentLoc != newLoc) {
        //   splitPane.setDividerLocation(newLoc);
        //   splitPane.setLastDividerLocation(currentLoc);
        // }
      }
    }));

    north.add(new JButton(minAction));
    north.add(new JButton(maxAction));

    JButton smin = new JButton("Min:keepHidden");
    JButton smax = new JButton("Max:keepHidden");
    Container divider = ((BasicSplitPaneUI) splitPane.getUI()).getDivider();
    initDividerButtonModel(divider, smin, smax);
    north.add(smin);
    north.add(smax);

    add(north, BorderLayout.NORTH);
    add(splitPane);
    setPreferredSize(new Dimension(320, 240));
  }

  private static void initDividerButtonModel(Container divider, JButton smin, JButton smax) {
    ButtonModel selectMinModel = null;
    ButtonModel selectMaxModel = null;
    for (Component c: divider.getComponents()) {
      if (c instanceof JButton) {
        ButtonModel m = ((JButton) c).getModel();
        if (Objects.isNull(selectMinModel) && Objects.isNull(selectMaxModel)) {
          selectMinModel = m;
        } else if (Objects.isNull(selectMaxModel)) {
          selectMaxModel = m;
        }
      }
    }
    smin.setModel(selectMinModel);
    smax.setModel(selectMaxModel);
  }

  public static void main(String... args) {
    EventQueue.invokeLater(new Runnable() {
      @Override public void run() {
        createAndShowGui();
      }
    });
  }

  public static void createAndShowGui() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
      ex.printStackTrace();
      Toolkit.getDefaultToolkit().beep();
    }
    JFrame frame = new JFrame("@title@");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().add(new MainPanel());
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
