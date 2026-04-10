import java.sql.*;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();

        if (conn == null) {
            System.out.println("Connection Failed!");
            return;
        }

        while (true) {
            System.out.println("\n--- PHARMACY MENU ---");
            System.out.println("1. Add Medicine");
            System.out.println("2. Sell Medicine");
            System.out.println("3. Low Stock Alert");
            System.out.println("4. Expiry Report");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    addMedicine(conn);
                    break;
                case 2:
                    sellMedicine(conn);
                    break;
                case 3:
                    lowStock(conn);
                    break;
                case 4:
                    expiryReport(conn);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // ADD MEDICINE
    public static void addMedicine(Connection conn) {
        try {
            sc.nextLine(); // clear buffer

            System.out.print("Enter Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Quantity: ");
            int quantity = sc.nextInt();

            System.out.print("Enter Price: ");
            double price = sc.nextDouble();

            System.out.print("Enter Expiry Date (YYYY-MM-DD): ");
            String expiry = sc.next();

            String sql = "INSERT INTO medicines (name, quantity, price, expiry_date) VALUES (?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, quantity);
            ps.setDouble(3, price);
            ps.setString(4, expiry);

            ps.executeUpdate();

            System.out.println("Medicine Added Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // SELL MEDICINE
    public static void sellMedicine(Connection conn) {
        try {
            System.out.print("Enter Medicine ID: ");
            int id = sc.nextInt();

            System.out.print("Enter Quantity to Sell: ");
            int qty = sc.nextInt();

            String checkSql = "SELECT quantity, price FROM medicines WHERE med_id = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, id);

            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                int currentQty = rs.getInt("quantity");
                double price = rs.getDouble("price");

                if (currentQty >= qty) {
                    int newQty = currentQty - qty;

                    String updateSql = "UPDATE medicines SET quantity = ? WHERE med_id = ?";
                    PreparedStatement updatePs = conn.prepareStatement(updateSql);
                    updatePs.setInt(1, newQty);
                    updatePs.setInt(2, id);
                    updatePs.executeUpdate();

                    System.out.println("Sale Successful!");
                    System.out.println("Remaining Quantity: " + newQty);
                    System.out.println("Total Cost: " + (price * qty));
                } else {
                    System.out.println("Not enough stock!");
                }
            } else {
                System.out.println("Medicine not found!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // LOW STOCK ALERT
    public static void lowStock(Connection conn) {
        try {
            String sql = "SELECT * FROM medicines WHERE quantity < 10";
            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- LOW STOCK MEDICINES ---");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("med_id") + " | " +
                        rs.getString("name") + " | Qty: " +
                        rs.getInt("quantity"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // EXPIRY REPORT
    public static void expiryReport(Connection conn) {
        try {
            String sql = "SELECT * FROM medicines WHERE expiry_date <= CURDATE() + INTERVAL 30 DAY";
            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- EXPIRING SOON ---");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("med_id") + " | " +
                        rs.getString("name") + " | Expiry: " +
                        rs.getDate("expiry_date"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //DELETE MEDICINE

    public static void deleteMedicine(Connection conn) {
        try {
            System.out.print("Enter Medicine ID to delete: ");
            int id = sc.nextInt();

            String sql = "DELETE FROM medicines WHERE med_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Medicine Deleted Successfully!");
            } else {
                System.out.println("Medicine not found!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}