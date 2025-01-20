package com.checkout.settlement.integration.reference.oneline.model;

import com.checkout.settlement.loader.reader.FileLineRecord;

public sealed interface OnelineRecord extends FileLineRecord permits Header, Item {

}
