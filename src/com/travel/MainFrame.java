package com.travel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;

public class MainFrame extends JFrame {

    private OfferService service = new OfferService();

    private JTable table;
    private DefaultTableModel model;

    private JComboBox<String> comboCountry;
    private JComboBox<String> comboSort;

    private JTabbedPane tabs;

    private JTextField txtUser;
    private JPasswordField txtPass;
    
    private double minTotal = 0;
    private double maxTotal = 0;


    public MainFrame() {
        setTitle("Ülkeye Göre Seyahat Fiyat Karşılaştırma");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        tabs = new JTabbedPane();

        tabs.addTab("Kullanıcı", createUserPanel());
        tabs.addTab("Admin Giriş", createLoginPanel());
        tabs.addTab("Admin Panel", createAdminPanel());

        // admin panel başta kilitli
        tabs.setEnabledAt(2, false);

        getContentPane().add(tabs);
    }

    /* ================= USER PANEL ================= */

    private JPanel createUserPanel() {

        BackgroundPanel panel =
                new BackgroundPanel("/images/bg.jpg");

        JPanel top = new JPanel();
        top.setOpaque(false);

        JLabel label = new JLabel("Ülke:");
        label.setForeground(new Color(255, 255, 255));
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        top.add(label);
        comboCountry = new JComboBox<>();
        refreshCountries(comboCountry);
        top.add(comboCountry);

        JLabel label_1 = new JLabel("Sırala:");
        label_1.setForeground(new Color(255, 255, 255));
        label_1.setFont(new Font("Arial", Font.PLAIN, 20));
        top.add(label_1);
        comboSort = new JComboBox<>(new String[]{
                "Ucuzdan Pahalıya", "Pahalıdan Ucuza"
        });
        comboSort.setFont(new Font("Arial", Font.PLAIN, 10));
        top.add(comboSort);

        JButton btnList = new JButton("Listele");
        btnList.setFont(new Font("Arial", Font.BOLD, 12));
        top.add(btnList);

        panel.add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Firma", "Uçak", "Otel", "Toplam", "Satın Al"}
        ) {
            public boolean isCellEditable(int r, int c) {
                return c == 4;
            }
        };

        table = new JTable(model);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                double total =
                        (double) table.getValueAt(row, 3); // Toplam sütunu

                if (total == minTotal) {
                    c.setBackground(new Color(200, 255, 200)); // EN UCUZ
                } else if (total == maxTotal) {
                    c.setBackground(new Color(255, 200, 200)); // EN PAHALI
                } else {
                    c.setBackground(Color.WHITE);
                }

                return c;
            }
        });

        table.setRowHeight(35);
        table.getColumn("Satın Al").setCellRenderer(new ButtonRenderer());
        table.getColumn("Satın Al").setCellEditor(new ButtonEditor());


        JScrollPane sp = new JScrollPane(table);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);

        panel.add(sp, BorderLayout.CENTER);
        
        

        btnList.addActionListener(e -> loadTable());

        return panel;
    }

    /* ================= LOGIN PANEL ================= */

    private JPanel createLoginPanel() {

        // DIŞ PANEL → her şeyi ortalar
        JPanel outer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 120));
        outer.setBackground(new Color(216, 191, 216));

        // İÇ PANEL → login formu
        JPanel panel = new JPanel();
        panel.setBackground(new Color(216, 191, 216));
        panel.setLayout(new GridLayout(3, 2, 15, 15));
        panel.setPreferredSize(new Dimension(400, 200));

        JLabel label_2 = new JLabel("Kullanıcı Adı:");
        label_2.setFont(new Font("Arial", Font.PLAIN, 25));
        panel.add(label_2);
        txtUser = new JTextField();
        txtUser.setBackground(new Color(255, 240, 245));
        panel.add(txtUser);

        JLabel label = new JLabel("Şifre:");
        label.setFont(new Font("Arial", Font.PLAIN, 25));
        label.setBackground(new Color(216, 191, 216));
        panel.add(label);
        txtPass = new JPasswordField();
        txtPass.setBackground(new Color(255, 240, 245));
        panel.add(txtPass);

        JLabel label_1 = new JLabel();
        label_1.setBackground(new Color(216, 191, 216));
        panel.add(label_1);
        JButton btnLogin = new JButton("Giriş Yap");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 25));
        btnLogin.setBackground(new Color(255, 250, 250));
        panel.add(btnLogin);

        btnLogin.addActionListener(e -> {
            if (txtUser.getText().equals("admin")
                    && new String(txtPass.getPassword()).equals("1234")) {

                JOptionPane.showMessageDialog(this, "Giriş başarılı");
                tabs.setEnabledAt(2, true);
                tabs.setSelectedIndex(2);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Hatalı giriş",
                        "Uyarı",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        outer.add(panel);
        return outer;
    }


    /* ================= ADMIN PANEL ================= */

    private JPanel createAdminPanel() {

        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 240, 245));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 200, 30, 200));

        // ÜLKE
        JLabel label = new JLabel("Ülke:");
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        panel.add(label);
        JComboBox<String> cbCountry = new JComboBox<>();
        refreshCountries(cbCountry);
        panel.add(cbCountry);

        panel.add(Box.createVerticalStrut(10));

        // FİRMA
        JLabel label_1 = new JLabel("Firma:");
        label_1.setFont(new Font("Arial", Font.PLAIN, 20));
        panel.add(label_1);
        JComboBox<String> cbCompany = new JComboBox<>();
        for (String c : service.getAllCompanies())
            cbCompany.addItem(c);
        panel.add(cbCompany);

        panel.add(Box.createVerticalStrut(10));

        // UÇAK
        JLabel label_2 = new JLabel("Uçak Fiyatı:");
        label_2.setFont(new Font("Arial", Font.PLAIN, 20));
        panel.add(label_2);
        JTextField txtFlight = new JTextField();
        panel.add(txtFlight);

        panel.add(Box.createVerticalStrut(10));

        // OTEL
        JLabel label_3 = new JLabel("Otel Fiyatı:");
        label_3.setFont(new Font("Arial", Font.PLAIN, 20));
        panel.add(label_3);
        JTextField txtHotel = new JTextField();
        panel.add(txtHotel);

        panel.add(Box.createVerticalStrut(15));

        // BUTONLAR (YAN YANA)
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnRow.setBackground(new Color(255, 240, 245));

        JButton btnAdd = new JButton("Ekle");
        btnAdd.setFont(new Font("Arial", Font.BOLD, 15));
        JButton btnUpdate = new JButton("Güncelle");
        btnUpdate.setFont(new Font("Arial", Font.BOLD, 15));
        JButton btnDelete = new JButton("Sil");
        btnDelete.setFont(new Font("Arial", Font.BOLD, 15));

        btnRow.add(btnAdd);
        btnRow.add(btnUpdate);
        btnRow.add(btnDelete);

        panel.add(btnRow);

        panel.add(Box.createVerticalStrut(25));

        // YENİ ÜLKE
        JLabel label_4 = new JLabel("Yeni Ülke:");
        label_4.setFont(new Font("Arial", Font.PLAIN, 20));
        panel.add(label_4);
        JTextField txtNewCountry = new JTextField();
        panel.add(txtNewCountry);

        panel.add(Box.createVerticalStrut(10));

        JButton btnAddCountry = new JButton("Ülke Ekle");
        btnAddCountry.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(btnAddCountry);

        /* === AKSİYONLAR === */

        btnAdd.addActionListener(e -> {
            boolean ok = service.addOfferByName(
                    cbCountry.getSelectedItem().toString(),
                    cbCompany.getSelectedItem().toString(),
                    Double.parseDouble(txtFlight.getText()),
                    Double.parseDouble(txtHotel.getText())
            );
            JOptionPane.showMessageDialog(this,
                    ok ? "Tur eklendi" : "Tur zaten var");
        });

        btnUpdate.addActionListener(e -> {
            service.updateOfferByName(
                    cbCountry.getSelectedItem().toString(),
                    cbCompany.getSelectedItem().toString(),
                    Double.parseDouble(txtFlight.getText()),
                    Double.parseDouble(txtHotel.getText())
            );
            JOptionPane.showMessageDialog(this, "Tur güncellendi");
        });

        btnDelete.addActionListener(e -> {
            service.deleteOfferByName(
                    cbCountry.getSelectedItem().toString(),
                    cbCompany.getSelectedItem().toString()
            );
            JOptionPane.showMessageDialog(this, "Tur silindi");
        });

        btnAddCountry.addActionListener(e -> {
            boolean ok = service.addCountry(txtNewCountry.getText());
            JOptionPane.showMessageDialog(this,
                    ok ? "Ülke eklendi" : "Ülke zaten var");
            refreshCountries(cbCountry);
            refreshCountries(comboCountry);
        });
        return panel;
    }

    /* ================= DATA ================= */

    private void refreshCountries(JComboBox<String> box) {
        box.removeAllItems();
        for (String c : service.getAllCountries())
            box.addItem(c);
    }

    private void loadTable() {
        model.setRowCount(0);

        String country = comboCountry.getSelectedItem().toString();
        boolean asc = comboSort.getSelectedIndex() == 0;

        List<Offer> list = service.getOffersByCountry(country, asc);

        if (list.isEmpty()) return;

        // 🔹 min & max hesapla
        minTotal = list.stream()
                .mapToDouble(Offer::getTotalPrice)
                .min().orElse(0);

        maxTotal = list.stream()
                .mapToDouble(Offer::getTotalPrice)
                .max().orElse(0);

        for (Offer o : list) {
            model.addRow(new Object[]{
                    o.getCompany(),
                    o.getFlightPrice(),
                    o.getHotelPrice(),
                    o.getTotalPrice(),
                    "Satın Al"
            });
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new MainFrame().setVisible(true));
    }
 // SATIN AL BUTON RENDERER
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setText("Satın Al");
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // SATIN AL BUTON EDITOR
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;

        public ButtonEditor() {
            super(new JCheckBox());
            button = new JButton("Satın Al");
            button.addActionListener(e ->
                    JOptionPane.showMessageDialog(
                            MainFrame.this,
                            "Siteye yönlendiriliyorsunuz ✈️"
                    )
            );
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected,
                int row, int column) {
            return button;
        }
    }

}
