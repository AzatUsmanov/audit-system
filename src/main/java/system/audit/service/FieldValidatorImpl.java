package system.audit.service;

import jakarta.validation.Validator;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/**
 *Класс, реализующий функционал по валидации клиентских данных,
 * полученных из HTML форм
 */
@Service
@AllArgsConstructor
public class FieldValidatorImpl implements FieldValidator {

    private final Validator validator;

    @Override
    public <T> Map<String, String> validate(T obj) {
        final var errors = validator.validate(obj);
        final var res = new HashMap<String, String>();
        errors.forEach(x ->
                res.put(x.getPropertyPath().toString(), x.getMessage()));
        return res;
    }
}
