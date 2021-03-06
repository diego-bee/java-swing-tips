// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.stream.Stream;
import javax.swing.*;
import javax.swing.plaf.basic.BasicToolBarUI;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super();
    String disabledAreNavigable = "MenuItem.disabledAreNavigable";

    Boolean b = UIManager.getBoolean(disabledAreNavigable);
    System.out.println(disabledAreNavigable + ": " + b);
    JCheckBox disabledAreNavigableCheck = new JCheckBox(disabledAreNavigable, b) {
      @Override public void updateUI() {
        super.updateUI();
        setSelected(UIManager.getLookAndFeelDefaults().getBoolean(disabledAreNavigable));
        UIManager.put(disabledAreNavigable, isSelected());
      }
    };
    disabledAreNavigableCheck.addActionListener(e -> {
      UIManager.put(disabledAreNavigable, ((JCheckBox) e.getSource()).isSelected());
    });

    // EventQueue.invokeLater(new Runnable() {
    //   @Override public void run() {
    //     ActionListener al = new ActionListener() {
    //       @Override public void actionPerformed(ActionEvent e) {
    //         EventQueue.invokeLater(new Runnable() {
    //           @Override public void run() {
    //             Object o = e.getSource();
    //             if (o instanceof JRadioButtonMenuItem) {
    //               JRadioButtonMenuItem rbmi = (JRadioButtonMenuItem) o;
    //               if (rbmi.isSelected()) {
    //                 Boolean b = UIManager.getBoolean(disabledAreNavigable);
    //                 System.out.println(rbmi.getText() + ": " + b);
    //                 disabledAreNavigableCheck.setSelected(b);
    //               }
    //             }
    //           }
    //         });
    //       }
    //     };
    //     List<JRadioButtonMenuItem> list = new ArrayList<>();
    //     ManuBarUtil.searchAllMenuElements(getRootPane().getJMenuBar(), list);
    //     for (JRadioButtonMenuItem mi: list) {
    //       mi.addActionListener(al);
    //     }
    //   }
    // });

    JPopupMenu popup = new JPopupMenu();
    ManuBarUtil.initMenu(popup);
    setComponentPopupMenu(popup);
    add(disabledAreNavigableCheck);
    setPreferredSize(new Dimension(320, 240));
  }

  public static void main(String... args) {
    EventQueue.invokeLater(new Runnable() {
      @Override public void run() {
        createAndShowGui();
      }
    });
  }

  public static void createAndShowGui() {
    // try {
    //   UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    // } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
    //   ex.printStackTrace();
    // }
    JMenuBar menuBar = ManuBarUtil.createMenuBar();

    // Stream.of(menuBar)
    //   .flatMap(new Function<MenuElement, Stream<MenuElement>>() {
    //     @Override public Stream<MenuElement> apply(MenuElement me) {
    //       return Stream.concat(Stream.of(me), Stream.of(me.getSubElements()).flatMap(this::apply));
    //     }
    //   })
    //   .filter(mi -> mi instanceof JRadioButtonMenuItem)
    //   .forEach(mi -> System.out.println("----\n" + mi.getClass()));

    JFrame frame = new JFrame("@title@");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().add(new MainPanel());
    frame.setJMenuBar(menuBar);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}

class ExitAction extends AbstractAction {
  protected ExitAction() {
    super("Exit");
  }

  @Override public void actionPerformed(ActionEvent e) {
    Component root = null;
    Container parent = SwingUtilities.getUnwrappedParent((Component) e.getSource());
    if (parent instanceof JPopupMenu) {
      JPopupMenu popup = (JPopupMenu) parent;
      root = SwingUtilities.getRoot(popup.getInvoker());
    } else if (parent instanceof JToolBar) {
      JToolBar toolbar = (JToolBar) parent;
      if (((BasicToolBarUI) toolbar.getUI()).isFloating()) {
        root = SwingUtilities.getWindowAncestor(toolbar).getOwner();
      } else {
        root = SwingUtilities.getRoot(toolbar);
      }
    } else {
      root = SwingUtilities.getRoot(parent);
    }
    if (root instanceof Window) {
      Window window = (Window) root;
      window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
    }
  }
}

final class ManuBarUtil {
  private ManuBarUtil() {
    /* Singleton */
  }

  public static JMenuBar createMenuBar() {
    JMenuBar mb = new JMenuBar();
    JMenu m = new JMenu("File");
    initMenu(m);
    mb.add(m);
    m = createMenu("Edit");
    mb.add(m);
    m = LookAndFeelUtil.createLookAndFeelMenu();
    mb.add(m);
    mb.add(Box.createGlue());
    m = new JMenu("Help");
    m.add("About");
    mb.add(m);
    return mb;
  }

  private static JMenu createMenu(String key) {
    JMenu menu = new JMenu(key);
    Stream.of("Cut", "Copy", "Paste", "Delete").map(menu::add).forEach(mi -> mi.setEnabled(false));
    return menu;
  }

  public static void initMenu(Container p) {
    JMenuItem item = new JMenuItem("Open(disabled)");
    item.setEnabled(false);
    p.add(item);
    item = new JMenuItem("Save(disabled)");
    item.setEnabled(false);
    p.add(item);
    p.add(new JSeparator());
    p.add(new JMenuItem(new ExitAction()));
  }
  // public static void searchAllMenuElements(MenuElement me, List<JRadioButtonMenuItem> list) {
  //   if (me instanceof JRadioButtonMenuItem) {
  //     list.add((JRadioButtonMenuItem) me);
  //   }
  //   MenuElement[] sub = me.getSubElements();
  //   if (sub.length != 0) {
  //     for (MenuElement e: sub) {
  //       searchAllMenuElements(e, list);
  //     }
  //   }
  // }
  // public static Stream<MenuElement> stream(MenuElement me) {
  //   return Stream.of(me.getSubElements())
  //     .flatMap(m -> Stream.concat(Stream.of(m), stream(m)));
  // }
}

// @see https://java.net/projects/swingset3/sources/svn/content/trunk/SwingSet3/src/com/sun/swingset3/SwingSet3.java
final class LookAndFeelUtil {
  private static String lookAndFeel = UIManager.getLookAndFeel().getClass().getName();

  private LookAndFeelUtil() {
    /* Singleton */
  }

  public static JMenu createLookAndFeelMenu() {
    JMenu menu = new JMenu("LookAndFeel");
    ButtonGroup lafGroup = new ButtonGroup();
    for (UIManager.LookAndFeelInfo lafInfo: UIManager.getInstalledLookAndFeels()) {
      menu.add(createLookAndFeelItem(lafInfo.getName(), lafInfo.getClassName(), lafGroup));
    }
    return menu;
  }

  private static JMenuItem createLookAndFeelItem(String lafName, String lafClassName, ButtonGroup lafGroup) {
    JRadioButtonMenuItem lafItem = new JRadioButtonMenuItem(lafName, lafClassName.equals(lookAndFeel));
    lafItem.setActionCommand(lafClassName);
    lafItem.setHideActionText(true);
    lafItem.addActionListener(e -> {
      ButtonModel m = lafGroup.getSelection();
      try {
        setLookAndFeel(m.getActionCommand());
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        ex.printStackTrace();
        Toolkit.getDefaultToolkit().beep();
      }
    });
    lafGroup.add(lafItem);
    return lafItem;
  }

  private static void setLookAndFeel(String lookAndFeel) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
    String oldLookAndFeel = LookAndFeelUtil.lookAndFeel;
    if (!oldLookAndFeel.equals(lookAndFeel)) {
      UIManager.setLookAndFeel(lookAndFeel);
      LookAndFeelUtil.lookAndFeel = lookAndFeel;
      updateLookAndFeel();
      // firePropertyChange("lookAndFeel", oldLookAndFeel, lookAndFeel);
    }
  }

  private static void updateLookAndFeel() {
    for (Window window: Frame.getWindows()) {
      SwingUtilities.updateComponentTreeUI(window);
    }
  }
}
