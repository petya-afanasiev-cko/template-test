package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.t112.model;

import com.checkout.settlement.loader.reader.FileLineRecord;

public sealed interface T112Record extends FileLineRecord permits FileHeader, FileTrailer, T112 {
}
