package com.yuriykoziy.issueTracker.util;

import org.springframework.security.core.Authentication;

public class CommonUtil {
  private static final String ADMIN = "ADMIN";

  public static boolean isAdmin(Authentication auth) {
    if (auth == null) {
      return false;
    }
    return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(ADMIN));
  }
}
