package library.util;

import library.models.Person;
import library.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PersonValidator implements Validator {
    private final PeopleService peopleService;
    @Autowired
    public PersonValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return new Person().equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
    Person person = (Person) target;
    if (peopleService.getPersonByFullName(person.getFullName()).isPresent())
        errors.rejectValue("fullName","","This person name is used");
    }
}
