package com.yuriykoziy.issueTracker.exceptions;

public class UserAlreadyExistException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public UserAlreadyExistException(String arg0) {
      super(arg0);
   }
}
