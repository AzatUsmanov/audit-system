package system.audit.service;

import java.util.Map;

public interface FieldValidator {

    <T> Map<String, String> validate(T obj);
}
