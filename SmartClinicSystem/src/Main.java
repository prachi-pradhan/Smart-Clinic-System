import database.DatabaseManager;
import ui.LoginFrame;

public class Main {

    public static void main(String[] args) {

        DatabaseManager.createTables();

        new LoginFrame();
    }
}