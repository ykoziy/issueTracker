package com.yuriykoziy.issueTracker.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.yuriykoziy.issueTracker.exceptions.*;

@ControllerAdvice
public class ExceptionHandling {

   @ExceptionHandler(UserAlreadyExistException.class)
   @ResponseStatus(HttpStatus.CONFLICT)
   @ResponseBody
   public ErrorResponse userAlreadyExistsException(UserAlreadyExistException ex) {
      return new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
   }

   @ExceptionHandler(UserNotFoundException.class)
   @ResponseStatus(HttpStatus.NOT_FOUND)
   @ResponseBody
   public ErrorResponse userNotFoundException(UserNotFoundException ex) {
      return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
   }

   @ExceptionHandler(IssueException.class)
   @ResponseStatus(HttpStatus.NOT_FOUND)
   @ResponseBody
   public ErrorResponse issueException(IssueException ex) {
      return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
   }

   @ExceptionHandler(CommentException.class)
   @ResponseStatus(HttpStatus.NOT_FOUND)
   @ResponseBody
   public ErrorResponse commentException(CommentException ex) {
      return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
   }

   @ExceptionHandler(ValidationException.class)
   @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
   @ResponseBody
   public ErrorResponse validationException(ValidationException ex) {
      return new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage());
   }
}
