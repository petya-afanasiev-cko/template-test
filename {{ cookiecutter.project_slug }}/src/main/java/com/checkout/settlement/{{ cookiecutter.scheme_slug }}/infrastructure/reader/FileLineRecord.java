package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.infrastructure.reader;

public interface FileLineRecord {
  String fileName();
  int lineNumber();
  default String getType() {
    return getClass().getSimpleName();
  }
}
