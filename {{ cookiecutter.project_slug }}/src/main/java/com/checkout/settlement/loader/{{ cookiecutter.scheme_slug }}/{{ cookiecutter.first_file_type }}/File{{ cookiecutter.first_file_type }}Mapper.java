package com.checkout.settlement.loader.{{ cookiecutter.scheme_slug }}.{{ cookiecutter.first_file_type }};

import com.checkout.settlement.loader.reader.FileLineRecord;
import com.checkout.settlement.loader.reader.UnknownRecord;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.checkout.settlement.loader.{{ cookiecutter.scheme_slug }}.{{ cookiecutter.first_file_type }}.model.Header;
import com.checkout.settlement.loader.{{ cookiecutter.scheme_slug }}.{{ cookiecutter.first_file_type }}.model.Item;

@StepScope
@Component
public class File{{ cookiecutter.first_file_type }}Mapper implements LineMapper<FileLineRecord> {

    static final String HEADER = "01";
    static final String TRAILER = "02";

    @Value("${jobParameters.file_name:'dummy.txt'}")
    private String fileName;

    /**
     * Identify the record type from the line String.
     * Call the correct record mapper and return the line mapped to fields.
     * NB! Final mapping step (enums, amount and number parsing) should happen later in Processor. 
     * This mapper should only parse the line to String values (eg substring for positional files)
     * This allows the raw event to be emitted even if parsing errors are encountered later.
     * @param line to be mapped
     * @param lineNumber of the current line
     * @return Deserialized line as the correct Record
     */
    @Override
    public FileLineRecord mapLine(String line, int lineNumber) {
        var recordType = line.substring(0, 2);
        return switch (recordType) {
            case HEADER -> mapHeader(lineNumber, line);
            case TRAILER -> mapItem(lineNumber, line);
            default -> new UnknownRecord(fileName, lineNumber, line);
        };
    }

    private Header mapHeader(int lineNumber, String line) {
        return Header.builder()
                .fileName(fileName)
                .lineNumber(lineNumber)
                .field1(line.substring(2, 18))
                .field2(line.substring(18, 20))
                .build();
    }

    private Item mapItem(int lineNumber, String line) {
        return Item.builder()
                .fileName(fileName)
                .lineNumber(lineNumber)
                .field1(line.substring(2, 16))
                .field2(line.substring(16, 27))
                .build();
    }
}
