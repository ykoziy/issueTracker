package com.yuriykoziy.issueTracker.exceptions;

public class ValidationException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public ValidationException(String arg0) {
      super(arg0);
   }
}
