import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Main extends JFrame {

    private static final String URL = "jdbc:mysql://localhost:3306/grug";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private JTable table;
    private DefaultTableModel model;

    private JTextField tytulField;
    private JTextField autorField;
    private JTextField rokField;

    public Main() {
        setTitle("Księgozbiór – CRUD JDBC + Swing");
        setSize(750, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(new String[]{"ID", "Tytuł", "Autor", "Rok"}, 0);
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel inputPanel = new JPanel(new GridLayout(3, 1));

        JPanel labels = new JPanel(new GridLayout(1, 3));
        labels.add(new JLabel("Tytuł", SwingConstants.CENTER));
        labels.add(new JLabel("Autor", SwingConstants.CENTER));
        labels.add(new JLabel("Rok wydania", SwingConstants.CENTER));
        inputPanel.add(labels);

        JPanel fields = new JPanel(new GridLayout(1, 3));
        tytulField = new JTextField();
        autorField = new JTextField();
        rokField = new JTextField();
        fields.add(tytulField);
        fields.add(autorField);
        fields.add(rokField);
        inputPanel.add(fields);

        JPanel buttons = new JPanel(new GridLayout(1, 3));
        JButton addBtn = new JButton("Dodaj");
        JButton updateBtn = new JButton("Aktualizuj");
        JButton deleteBtn = new JButton("Usuń");
        buttons.add(addBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);
        inputPanel.add(buttons);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        refreshTable();

        addBtn.addActionListener(e -> addBook());
        deleteBtn.addActionListener(e -> deleteBook());
        updateBtn.addActionListener(e -> updateBook());

        table.getSelectionModel().addListSelectionListener(e -> loadSelectedRow());
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private void refreshTable() {
        model.setRowCount(0);
        String sql = "SELECT * FROM ksiazki";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("tytul"),
                        rs.getString("autor"),
                        rs.getInt("rok_wydania")
                });
            }
        } catch (SQLException ignored) {}
    }

    private boolean validateInputs() {
        if (tytulField.getText().isEmpty() || autorField.getText().isEmpty() || rokField.getText().isEmpty())
            return false;
        try {
            Integer.parseInt(rokField.getText());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private void clearFields() {
        tytulField.setText("");
        autorField.setText("");
        rokField.setText("");
    }

    private void loadSelectedRow() {
        int row = table.getSelectedRow();
        if (row != -1) {
            tytulField.setText(model.getValueAt(row, 1).toString());
            autorField.setText(model.getValueAt(row, 2).toString());
            rokField.setText(model.getValueAt(row, 3).toString());
        }
    }

    private void addBook() {
        if (!validateInputs()) return;
        String tytul = tytulField.getText();
        String autor = autorField.getText();
        int rok = Integer.parseInt(rokField.getText());
        String sql = "INSERT INTO ksiazki (tytul, autor, rok_wydania) VALUES ('" + tytul + "','" + autor + "'," + rok + ")";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            refreshTable();
            clearFields();
        } catch (SQLException ignored) {}
    }

    private void deleteBook() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int id = (int) model.getValueAt(row, 0);
        String sql = "DELETE FROM ksiazki WHERE id=" + id;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            refreshTable();
            clearFields();
        } catch (SQLException ignored) {}
    }

    private void updateBook() {
        int row = table.getSelectedRow();
        if (row == -1 || !validateInputs()) return;
        int id = (int) model.getValueAt(row, 0);
        String tytul = tytulField.getText();
        String autor = autorField.getText();
        int rok = Integer.parseInt(rokField.getText());
        String sql = "UPDATE ksiazki SET tytul='" + tytul + "', autor='" + autor + "', rok_wydania=" + rok + " WHERE id=" + id;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            refreshTable();
            clearFields();
        } catch (SQLException ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
