import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class PermissionHandler {
    Map<String, EnumSet<Operation>> deniedOperations;

    public PermissionHandler() {
        clearPermissions();
    }

    public void handleCommand(String command){

    }

    public void allowPermission(String resource, Operation operation){
        deniedOperations.get(resource).add(operation);
    }

    public void denyPermission(String resource, Operation operation){
        if (deniedOperations.containsKey(resource)) {
            deniedOperations.get(resource).add(operation);
            return;
        }
        deniedOperations.put(resource, EnumSet.of(operation));
    }

    public void clearPermissions() {
        this.deniedOperations = new HashMap<>();
        deniedOperations.put("*", EnumSet.noneOf(Operation.class));
    }

    public boolean isAllowed(String resource, Operation operation){

    }

    public String allowedPermissions(String resource){

    }
}
