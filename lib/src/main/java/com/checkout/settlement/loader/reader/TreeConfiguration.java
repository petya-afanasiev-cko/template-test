package com.checkout.settlement.loader.reader;

import java.util.List;
import java.util.Map;

public interface TreeConfiguration {
  Map<String, List<String>> getHierarchy();
}
