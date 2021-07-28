package abs.ixi.server;

import java.util.HashMap;
import java.util.Map;

/**
 * An entity state is a set of parameters and values which represent the state
 * of an entity at a given point of time.
 * 
 * @author Yogi
 *
 */
public class EntityState {
    /**
     * Name property;
     */
    public static final String NAME = "name";

    /**
     * Entity class name
     */
    public static final String CLASS = "class";

    private Map<String, Object> stateParams;

    public EntityState() {
	this.stateParams = new HashMap<>();
    }

    public EntityState(Map<String, Object> stateParams) {
	this.stateParams = stateParams;
    }

    /**
     * Get name of the entity; this is the name of the entity instance
     * 
     * @return
     */
    public String getName() {
	return this.stateParams.containsKey(NAME) ? this.stateParams.get(NAME).toString() : getClassName();
    }

    /**
     * Get the name of the class of the entity
     * 
     * @return
     */
    public String getClassName() {
	return this.stateParams.containsKey(CLASS) ? this.stateParams.get(CLASS).toString() : "Not Available";
    }

    /**
     * Store value for an attribute. If there is already a value for this
     * attribute, it will be overwritten; and previous value will be returned.
     * 
     * @param attribute
     * @param value
     * @return
     */
    public Object put(String attribute, Object value) {
	return this.stateParams.put(attribute, value);
    }

    /**
     * Get the value object for an attribute
     * 
     * @param attribute
     * @return
     */
    public Object get(String attribute) {
	return this.stateParams.get(attribute);
    }

}
