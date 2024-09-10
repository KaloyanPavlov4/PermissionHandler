import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
            PermissionHandler permissionHandler = new PermissionHandler();
            List<String> commands = List.of("*:*", "Secret:read", "Secret:read:deny");
            permissionHandler.handleCommands(commands);

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("1.See allowed operations\n2.Check permission on operation");
                String input = scanner.nextLine();
                if (input.equals("1")){
                    System.out.print("Resource: ");
                    String resource = scanner.nextLine();
                    System.out.println(permissionHandler.allowedPermissions(resource));
                }else if (input.equals("2")){
                    System.out.print("Resource: ");
                    String resource = scanner.nextLine();
                    System.out.print("Operation: ");
                    String operation = scanner.nextLine();
                    System.out.println(permissionHandler.isAllowed(resource,operation));
                }
            }
        }
    }
