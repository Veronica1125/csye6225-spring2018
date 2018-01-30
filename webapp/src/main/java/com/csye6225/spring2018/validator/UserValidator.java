package com.csye6225.spring2018.validator;

import com.csye6225.spring2018.pojo.User;
import com.csye6225.spring2018.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserValidator implements Validator {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(User.class);
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "error.email","email is required!");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "error.password", "password is required!");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "error.password", "confirmed password is required!");
        if(userRepository.findUserByEmail(user.getEmail()) != null){
            errors.rejectValue("email", "error.emailExists", "email already exists!");
        }
        if(user.getEmail()!= null && !validateEmail(user.getEmail())){
            errors.rejectValue("email", "invalidEmail", "please provide valid email address!");
        }
//        System.out.println("ps: "+ user.getPassword()+" c ps: "+user.getConfirmPassword());
//        System.out.println("equal? "+user.getPassword().equals(user.getConfirmPassword()));
        if(user.getConfirmPassword()!= null && !user.getConfirmPassword().equals(user.getPassword())){
            errors.rejectValue("confirmPassword", "error.passwordMismatch","confirmed password does not match!");
        }

    }
}
