package com.checkout.settlement.dci.confirmation;

import com.checkout.settlement.dci.confirmation.model.Charge;
import com.checkout.settlement.dci.confirmation.model.ConfirmationRecord;
import com.checkout.settlement.dci.confirmation.model.Header;
import com.checkout.settlement.dci.infrastructure.reader.FileLineRecord;
import com.checkout.settlement.dci.infrastructure.reader.UnknownRecord;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

import static com.checkout.settlement.dci.utils.StringParser.optionalString;
import static com.checkout.settlement.dci.utils.StringParser.parseBigDecimal;
import static com.checkout.settlement.dci.utils.StringParser.parseIsoDate;
import static com.checkout.settlement.dci.utils.StringParser.parseIsoDateTime;
import static com.checkout.settlement.dci.utils.StringParser.parseOptionalIsoDate;

@StepScope
@Component
public class ConfirmationLineMapper implements LineMapper<FileLineRecord> {

  static final String HEADER = "01";
  static final String CHARGE = "06";

  @Value("#{jobParameters.file_name}")
  private String fileName;
  
  @Override
  public FileLineRecord mapLine(String line, int lineNumber) {
    var recordType = line.substring(0, 2);
    return switch (recordType) {
      case HEADER -> mapHeader(lineNumber, line);
      case CHARGE -> mapCharge(lineNumber, line);
      default -> new UnknownRecord(fileName, lineNumber);
    };
  }

  private Header mapHeader(int lineNumber, String line) {
    return new Header(
        fileName,
        lineNumber,
        line.substring(0, 2),
        line.substring(2, 5).strip(),
        line.substring(5, 16),
        line.substring(16, 21),
        parseOptionalIsoDate(line.substring(21, 29)),
        optionalString(line.substring(29, 32)).map(Integer::valueOf),
        parseIsoDateTime(line.substring(32, 46)).toInstant(ZoneOffset.UTC),
        line.substring(46, 47),
        line.substring(47, 800).strip()
    );
  }

  private ConfirmationRecord mapCharge(int lineNumber, String line) {
    return new Charge(
        fileName,
        lineNumber,
        line.substring(0, 2),
        line.substring(2, 5),
        "",
        line.substring(24, 32),
        line.substring(32, 35),
        line.substring(35, 37),
        line.substring(37, 45),
        line.substring(45, 51),
        line.substring(51, 57),
        parseBigDecimal(line.substring(57, 63)),
        parseBigDecimal(line.substring(63, 79)),
        parseBigDecimal(line.substring(79, 99)),
        parseBigDecimal(line.substring(99, 119)),
        parseBigDecimal(line.substring(119, 139)),
        parseBigDecimal(line.substring(139, 159)),
        parseBigDecimal(line.substring(159, 179)),
        parseBigDecimal(line.substring(179, 199)),
        parseBigDecimal(line.substring(199, 219)),
        parseBigDecimal(line.substring(219, 239)),
        parseBigDecimal(line.substring(239, 259)),
        parseBigDecimal(line.substring(259, 279)),
        parseBigDecimal(line.substring(279, 299)),
        parseBigDecimal(line.substring(299, 319)),
        line.substring(319, 355),
        line.substring(355, 361),
        line.substring(361, 397),
        parseIsoDate(line.substring(397, 405)).atStartOfDay().toInstant(ZoneOffset.UTC),
        line.substring(405, 406),
        line.substring(406, 410),
        line.substring(410, 425),
        parseBigDecimal(line.substring(425, 445)),
        parseBigDecimal(line.substring(445, 465)),
        parseBigDecimal(line.substring(465, 485)),
        parseBigDecimal(line.substring(485, 505)),
        parseBigDecimal(line.substring(505, 525)),
        parseBigDecimal(line.substring(525, 545)),
        parseBigDecimal(line.substring(545, 555)),
        line.substring(555, 558),
        line.substring(558, 559),
        line.substring(559, 563),
        line.substring(563, 567),
        line.substring(567, 582),
        line.substring(582, 583),
        line.substring(583, 584),
        line.substring(584, 585),
        line.substring(585, 588),
        line.substring(588, 591),
        line.substring(591, 610),
        line.substring(610, 611),
        line.substring(611, 800).strip()
    );
  }
}
