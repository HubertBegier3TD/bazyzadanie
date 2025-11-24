import java.sql.*;

public class Main {

    private static final String URL = "jdbc:mysql://localhost:3306/greg";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            int nextId = getNextId(conn);

            String sqlInsert = "INSERT INTO ksiazki (id, tytul, autor, rok_wydania) VALUES (?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                pstmt.setInt(1, nextId);
                pstmt.setString(2, "Nowa Książka");
                pstmt.setString(3, "Nowy Autor");
                pstmt.setInt(4, 2003);

                int rowsAffected = pstmt.executeUpdate();
                System.out.println("Wstawiono " + rowsAffected + " książkę z ID: " + nextId);
            }

            czytajKsiazki(conn);

        } catch (SQLException e) {
            System.err.println("Błąd bazy danych: " + e.getMessage());
        }
    }


    private static int getNextId(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(MAX(id), 0) AS max_id FROM ksiazki";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("max_id") + 1;
            }
        }
        return 1;
    }

    private static void czytajKsiazki(Connection conn) throws SQLException {
        String sqlSelect = "SELECT id, tytul, autor, rok_wydania FROM ksiazki";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlSelect)) {

            System.out.println("\n--- Lista Książek ---");
            while (rs.next()) {
                int id = rs.getInt("id");
                String tytul = rs.getString("tytul");
                String autor = rs.getString("autor");
                int rok = rs.getInt("rok_wydania");

                System.out.printf("ID: %d | Tytuł: %s | Autor: %s | Rok wydania: %d%n",
                        id, tytul, autor, rok);
            }
            System.out.println("-----------------------");
        }
    }
}
