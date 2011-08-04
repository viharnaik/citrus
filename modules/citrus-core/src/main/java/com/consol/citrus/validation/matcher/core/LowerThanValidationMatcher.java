package com.consol.citrus.validation.matcher.core;

import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.validation.matcher.ValidationMatcher;

/**
 * ValidationMatcher based on on Double < Double.
 * 
 * @author Christian Wied
 */
public class LowerThanValidationMatcher implements ValidationMatcher {

    public void validate(String fieldName, String value, String control) throws ValidationException {
        
    	Double dValue;
    	Double dControl;
    	try {
    		dValue = Double.parseDouble(value);
    		dControl = Double.parseDouble(control);
    	} catch (Exception e) {
    		throw new ValidationException(this.getClass().getSimpleName()
                    + " failed for field '" + fieldName
                    + "'. Received value is '" + value
                    + "', control value is '" + control + "'.");
		}
        if (!(dValue < dControl)) {
            throw new ValidationException(this.getClass().getSimpleName()
                    + " failed for field '" + fieldName
                    + "'. Received value is '" + value
                    + "', control value is '" + control + "'.");
        }
    }
}
