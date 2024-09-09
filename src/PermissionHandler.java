import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class PermissionHandler {
    private Map<String, EnumSet<Operation>> deniedOperations;

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

    private void allowPermission(String resource, String operation){
        if (resource.equals("*")){
            if (operation.equals("*")) {
                clearPermissions();
                return;
            }
            Operation op = Operation.valueOf(operation.toUpperCase());
            deniedOperations.forEach((key, set) -> set.remove(op));
            return;
        }

        if (operation.equals("*")){
            deniedOperations.get(resource).clear();
            return;
        }

        Operation op = Operation.valueOf(operation.toUpperCase());
        deniedOperations.get(resource).remove(op);
    }

    private void denyPermission(String resource, String operation){
        EnumSet<Operation> toAdd = EnumSet.noneOf(Operation.class);
        if (operation.equals("*")){
            toAdd.addAll(EnumSet.allOf(Operation.class));
        } else {
            toAdd.add(Operation.valueOf(operation.toUpperCase()));
        }

        if (deniedOperations.containsKey(resource)) {
            deniedOperations.get(resource).addAll(toAdd);
            return;
        }

        deniedOperations.put(resource, toAdd);
    }

    private void clearPermissions() {
        this.deniedOperations = new HashMap<>();
        deniedOperations.put("*", EnumSet.noneOf(Operation.class));
    }

    public boolean isAllowed(String resource, String operation){
        Operation op = Operation.valueOf(operation.toUpperCase());
        boolean isResourceAllowed = !deniedOperations.containsKey(resource) || !deniedOperations.get(resource).contains(op);
        boolean areAllResourcesAllowed = !deniedOperations.get("*").contains(op);
        return isResourceAllowed && areAllResourcesAllowed;
    }

    public String allowedPermissions(String resource){
        EnumSet<Operation> allowed = EnumSet.allOf(Operation.class);
        EnumSet<Operation> denied = EnumSet.noneOf(Operation.class);

        denied.addAll(deniedOperations.get("*"));
        if (deniedOperations.containsKey(resource)){
            denied.addAll(deniedOperations.get(resource));
        }

        allowed.removeAll(denied);
        return String.format("%s resource is allowed to do the following operations: %s",resource,allowed);
    }
}
