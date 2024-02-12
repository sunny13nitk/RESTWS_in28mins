package in28mins.restws.exceptions.advice;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import in28mins.restws.exceptions.ErrorDetails;
import in28mins.restws.exceptions.PostsNotFoundException4User;
import in28mins.restws.exceptions.UserNotFoundException;

@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler
{

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorDetails> handleAllExceptions(Exception ex, WebRequest request) throws Exception
    {
        ErrorDetails errorDetails = new ErrorDetails(new Timestamp(System.currentTimeMillis()),
                ex.getLocalizedMessage(), request.getDescription(false), Arrays.asList(ex.getStackTrace()).toString());

        return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<ErrorDetails> handleUserNotFoundException(Exception ex, WebRequest request)
            throws Exception
    {
        ErrorDetails errorDetails = new ErrorDetails(new Timestamp(System.currentTimeMillis()),
                ex.getLocalizedMessage(), request.getDescription(false), Arrays.asList(ex.getStackTrace()).toString());

        return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PostsNotFoundException4User.class)
    public final ResponseEntity<ErrorDetails> handlePostNotFound4UserException(Exception ex, WebRequest request)
            throws Exception
    {
        ErrorDetails errorDetails = new ErrorDetails(new Timestamp(System.currentTimeMillis()),
                ex.getLocalizedMessage(), request.getDescription(false), Arrays.asList(ex.getStackTrace()).toString());

        return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request)
    {
        String msgs = null;
        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        if (!CollectionUtils.isEmpty(allErrors))
        {
            msgs = new String();
            int i = 1;
            for (ObjectError objectError : allErrors)
            {
                msgs += i + " - " + objectError.getCode() + objectError.getDefaultMessage() + ", ";
                i++;
            }
        }

        ErrorDetails errorDetails = new ErrorDetails(new Timestamp(System.currentTimeMillis()), msgs,
                request.getDescription(false), Arrays.asList(ex.getStackTrace()).toString());
        return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
    }

}
