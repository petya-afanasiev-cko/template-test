package com.checkout.settlement.loader.reader;

public interface FileLineRecord {
  String fileName();
  int lineNumber();

  String lineContents();
  default String getType() {
    return getClass().getSimpleName();
  }
}
