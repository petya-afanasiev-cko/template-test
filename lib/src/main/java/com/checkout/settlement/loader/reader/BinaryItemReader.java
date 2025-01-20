package com.checkout.settlement.loader.reader;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Arrays;
import lombok.Setter;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.LineMapper;

public class BinaryItemReader<T> implements ItemStreamReader<FileLineRecord> {

  private InputStream inputStream;
  private int chunkSize; // Size of each binary chunk to read
  @Setter
  private LineMapper<FileLineRecord> lineMapper;
  private boolean readHeader = true;  // todo remove once actual implementation is used

  public BinaryItemReader(InputStream is, int chunkSize) {
    this.inputStream = new BufferedInputStream(is);
    this.chunkSize = chunkSize;
  }

  @Override
  public FileLineRecord read() throws Exception {
    if (inputStream == null) {
      return null; // End of file
    }

    byte[] buffer = new byte[chunkSize];
    int bytesRead = inputStream.read(buffer);

    if (bytesRead == -1) {
      inputStream.close();
      inputStream = null;
      return null;
    } else if (bytesRead < chunkSize) {
      // Handle the case where the last chunk is smaller
      var outputA = Arrays.copyOfRange(buffer, 0, bytesRead);
      // ToDo: WIP. Actual parsing ISO8583 would require some more time and effort. Currently return dummy object.ˇ
      return lineMapper.mapLine("92xxxxx", 1);
    } else {
      var outputB = buffer;
    }

    // ToDo: WIP. Actual parsing ISO8583 would require some more time and effort. Currently return dummy object.
    if (readHeader) {
      readHeader = false;
      return lineMapper.mapLine("90xxxxx", 1);
    }

    // ToDo: WIP. Actual parsing ISO8583 would require some more time and effort. Currently return dummy object.
    return lineMapper.mapLine("3300xxx", 1);
  }
}