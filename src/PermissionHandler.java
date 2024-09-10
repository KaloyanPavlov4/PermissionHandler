import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PermissionHandler {
    private Map<String, EnumSet<Operation>> deniedOperations;
    private Map<String, EnumSet<Operation>> allowedOperations;

    public PermissionHandler() {
        clearPermissions();
    }

    public void handleCommand(String command){
        String[] commandParts = command.split(":");

        if (commandParts.length > 3 || commandParts.length < 2){
            throw new IllegalArgumentException("Invalid command length!");
        }

        //Deny
        if (commandParts.length == 3){
            if (!commandParts[2].equalsIgnoreCase("deny")) throw new IllegalArgumentException("Invalid third part of command!");
            denyPermission(commandParts[0],commandParts[1]);
        }

        //Allow
        else {
            allowPermission(commandParts[0], commandParts[1]);
        }
    }

    public void handleCommands(List<String> commands){
        for(String command : commands){
            handleCommand(command);
        }
    }

    private void allowPermission(String resource, String operation){
        EnumSet<Operation> toAdd = EnumSet.noneOf(Operation.class);
        if (operation.equals("*")){
            toAdd.addAll(EnumSet.allOf(Operation.class));
        } else {
            toAdd.add(Operation.valueOf(operation.toUpperCase()));
        }

        if(!allowedOperations.containsKey(resource)){
            allowedOperations.put(resource, toAdd);

        }else {
            allowedOperations.get(resource).addAll(toAdd);
        }
    }

    private void denyPermission(String resource, String operation){
        EnumSet<Operation> toAdd = EnumSet.noneOf(Operation.class);
        if (operation.equals("*")){
            toAdd.addAll(EnumSet.allOf(Operation.class));
        } else {
            toAdd.add(Operation.valueOf(operation.toUpperCase()));
        }

        if(!deniedOperations.containsKey(resource)){
            deniedOperations.put(resource, toAdd);

        }else {
            deniedOperations.get(resource).addAll(toAdd);
        }
    }

    private void clearPermissions() {
        this.deniedOperations = new HashMap<>();
        deniedOperations.put("*", EnumSet.noneOf(Operation.class));

        this.allowedOperations = new HashMap<>();
        allowedOperations.put("*", EnumSet.noneOf(Operation.class));
    }

    public boolean isAllowed(String resource, String operation){
        Operation op = Operation.valueOf(operation.toUpperCase());
        boolean isOperationAllowed = allowedOperations.get("*").contains(op);
        boolean isOperationDenied = deniedOperations.get("*").contains(op);
        if (allowedOperations.containsKey(resource)){
            isOperationAllowed = isOperationAllowed || allowedOperations.get(resource).contains(op);
        }

        if (deniedOperations.containsKey(resource)){
            isOperationDenied = isOperationDenied || deniedOperations.get("*").contains(op);
        }
        return isOperationAllowed && !isOperationDenied;
    }

    public String allowedPermissions(String resource){
        EnumSet<Operation> allowed = allowedOperations.get("*").clone();
        EnumSet<Operation> denied = deniedOperations.get("*").clone();

        if (deniedOperations.containsKey(resource)){
            denied.addAll(deniedOperations.get(resource));
        }

        if (allowedOperations.containsKey(resource)){
            allowed.addAll(allowedOperations.get(resource));
        }

        allowed.removeAll(denied);
        return String.format("%s resource is allowed to do the following operations: %s",resource,allowed);
    }
}
