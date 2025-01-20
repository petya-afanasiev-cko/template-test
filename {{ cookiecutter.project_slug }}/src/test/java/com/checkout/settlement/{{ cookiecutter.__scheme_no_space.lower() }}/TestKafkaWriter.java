package com.checkout.settlement.{{ cookiecutter.scheme_slug }};

import com.checkout.settlement.loader.writer.OutgoingEvent;
import java.util.ArrayList;
import java.util.List;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class TestKafkaWriter implements ItemWriter<OutgoingEvent> {
  private List<String> output = new ArrayList<>();

  public List<String> getOutput() {
    return output;
  }

  @Override
  public void write(Chunk<? extends OutgoingEvent> chunk) {
    for (var event : chunk) {
      output.add(event.settlementEvent().toString());
    }
  }
}
