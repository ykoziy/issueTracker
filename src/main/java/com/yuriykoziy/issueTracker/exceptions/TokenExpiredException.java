package com.yuriykoziy.issueTracker.exceptions;

public class TokenExpiredException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public TokenExpiredException(String arg0) {
    super(arg0);
  }
}
