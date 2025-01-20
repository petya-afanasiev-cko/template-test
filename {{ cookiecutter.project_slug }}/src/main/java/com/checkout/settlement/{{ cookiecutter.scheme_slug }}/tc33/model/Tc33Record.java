package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33.model;


import com.checkout.settlement.loader.reader.FileLineRecord;

public sealed interface Tc33Record extends FileLineRecord permits FileHeader, FileTrailer, CAS,
    PartiallyParsedLine {

}
