package com.checkout.settlement.loader.{{ cookiecutter.scheme_slug }}.{{ cookiecutter.first_file_type }}.model;

import com.checkout.settlement.loader.reader.FileLineRecord;

public sealed interface File{{ cookiecutter.first_file_type }}Record extends FileLineRecord permits Header, Item {

}
