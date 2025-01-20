package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.confirmation.model;


import com.checkout.settlement.loader.reader.FileLineRecord;

public sealed interface ConfirmationRecord extends FileLineRecord permits Header, Charge, ChargeTotal {

}
