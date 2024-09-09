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

        //Deny
        if (commandParts.length == 3){
            denyPermission(commandParts[0],commandParts[1].toUpperCase());
        }

        //Allow
        else {
            if (commandParts[0].equals("*") && commandParts[1].equals("*")) clearPermissions();
            else allowPermission(commandParts[0], commandParts[1].toUpperCase());
        }
    }

    private void allowPermission(String resource, String operation){
        if (operation.equals("*")){
            deniedOperations.get(resource).clear();
            return;
        }

        Operation op = Operation.valueOf(operation);
        deniedOperations.get(resource).remove(op);
    }

    private void denyPermission(String resource, String operation){
        EnumSet<Operation> toAdd = EnumSet.noneOf(Operation.class);
        if (operation.equals("*")){
            toAdd.addAll(EnumSet.allOf(Operation.class));
        } else {
            toAdd.add(Operation.valueOf(operation));
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
        Operation op = Operation.valueOf(operation);
        boolean isResourceAllowed = !deniedOperations.get(resource).contains(op);
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
