package com.openodm.impl;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class Transform {

	public static void main(String[] args) throws Exception {
		TransformerFactory tf = TransformerFactory.newInstance();
		Source source = new StreamSource(Transform.class.getClassLoader().getResourceAsStream("define2-0-0.xsl"));
		Templates template = tf.newTemplates(source);
		Result outputTarget = new StreamResult(System.out);
		template.newTransformer().transform(new StreamSource(Transform.class.getClassLoader().getResourceAsStream("define2-0-0-example-adam.xml")),
				outputTarget);
	}
}
