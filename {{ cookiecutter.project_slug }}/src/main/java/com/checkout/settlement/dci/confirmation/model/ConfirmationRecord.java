package com.checkout.settlement.dci.confirmation.model;

import com.checkout.settlement.dci.infrastructure.reader.FileLineRecord;

public sealed interface ConfirmationRecord extends FileLineRecord permits Header, Charge  {

}
