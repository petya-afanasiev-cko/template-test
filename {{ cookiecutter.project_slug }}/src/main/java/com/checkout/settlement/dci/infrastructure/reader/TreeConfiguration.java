package com.checkout.settlement.dci.infrastructure.reader;

import java.util.List;
import java.util.Map;

public interface TreeConfiguration {
  Map<String, List<String>> getHierarchy();
}
