package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.infrastructure.reader;

import java.util.List;
import java.util.Map;

public interface TreeConfiguration {
  Map<String, List<String>> getHierarchy();
}
