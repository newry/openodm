package com.openodm.impl;

import java.io.InputStream;
import java.util.List;

import com.epam.parso.Column;
import com.epam.parso.SasFileProperties;
import com.epam.parso.SasFileReader;
import com.epam.parso.impl.SasFileReaderImpl;

public class SasFileApp {

	public static void main(String[] args) throws Exception {
		try (InputStream is = SasFileApp.class.getClassLoader().getResourceAsStream("ae_coded.sas7bdat")) {
			SasFileReader sasFileReader = new SasFileReaderImpl(is);
			SasFileProperties sasFileProperties = sasFileReader.getSasFileProperties();
			System.out.println(sasFileProperties.getName());
			System.out.println(sasFileProperties.getFileType());
			List<Column> columns = sasFileReader.getColumns();
			for (Column column : columns) {
				System.out.println(column.getName());
			}
			Object[] next;
			while ((next = sasFileReader.readNext()) != null) {
				for (Object o : next) {
					System.out.println(o == null ? null : o + "@" + o.getClass());
				}
			}
		}

	}
}
