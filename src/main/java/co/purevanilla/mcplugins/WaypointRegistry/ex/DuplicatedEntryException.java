package co.purevanilla.mcplugins.WaypointRegistry.ex;

public class DuplicatedEntryException extends Exception {
    public DuplicatedEntryException(String errorMessage) {
        super(errorMessage);
    }
}
