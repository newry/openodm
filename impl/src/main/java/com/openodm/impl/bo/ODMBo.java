package com.openodm.impl.bo;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.openodm.impl.entity.ObjectStatus;
import com.openodm.impl.entity.ct.CodeList;
import com.openodm.impl.entity.ct.EnumeratedItem;
import com.openodm.impl.entity.ct.CTVersion;
import com.openodm.impl.repository.ct.CTVersionRepository;
import com.openodm.impl.repository.ct.CodeListRepository;
import com.openodm.impl.repository.ct.EnumeratedItemRepository;

@Component
@SuppressWarnings("rawtypes")
public class ODMBo {
	@Autowired
	private CTVersionRepository metaDataVersionRepository;
	@Autowired
	private CodeListRepository codeListRepository;
	@Autowired
	private EnumeratedItemRepository enumeratedItemRepository;

	private static class ODMNamespaceContext implements NamespaceContext {
		final private Map<String, String> namespaceMap = new HashMap<String, String>();

		public ODMNamespaceContext() {
			namespaceMap.put("odm", "http://www.cdisc.org/ns/odm/v1.3");
			namespaceMap.put("nciodm",
					"http://ncicb.nci.nih.gov/xml/odm/EVS/CDISC");
			namespaceMap.put("def", "http://www.cdisc.org/ns/def/v2.0");
		}

		@Override
		public String getNamespaceURI(String prefix) {
			return namespaceMap.get(prefix);
		}

		@Override
		public String getPrefix(String namespaceURI) {
			for (Map.Entry<String, String> entry : namespaceMap.entrySet()) {
				if (entry.getValue().equals(namespaceURI)) {
					return entry.getKey();
				}

			}
			return null;
		}

		@Override
		public Iterator getPrefixes(String namespaceURI) {
			for (Map.Entry<String, String> entry : namespaceMap.entrySet()) {
				if (entry.getValue().equals(namespaceURI)) {
					return Arrays.asList(entry.getKey()).iterator();
				}

			}
			return null;
		}

	}

	public void importMedatDataVersion(InputStream in) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(in);
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(new ODMNamespaceContext());
		NodeList nodes = (NodeList) xPath.evaluate("//odm:MetaDataVersion",
				doc.getDocumentElement(), XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node item = nodes.item(i);
			NamedNodeMap attributes = item.getAttributes();
			String oid = attributes.getNamedItem("OID").getNodeValue();
			List<CTVersion> mdvs = metaDataVersionRepository
					.findByOid(oid);
			CTVersion mdv;
			if (CollectionUtils.isEmpty(mdvs)) {
				mdv = new CTVersion();
				mdv.setCreator("admin");
				mdv.setUpdatedBy("admin");

				mdv.setOid(oid);
				mdv.setName(attributes.getNamedItem("Name").getNodeValue());
				mdv.setDescription(attributes.getNamedItem("Description")
						.getNodeValue());
				mdv = metaDataVersionRepository.save(mdv);
			} else {
				mdv = mdvs.get(0);
			}

			NodeList codeListNodes = (NodeList) xPath.evaluate("odm:CodeList",
					item, XPathConstants.NODESET);
			saveCodeList(xPath, mdv, codeListNodes);
		}
	}

	private void saveCodeList(XPath xPath, CTVersion mdv,
			NodeList codeListNodes) throws XPathExpressionException {
		for (int j = 0; j < codeListNodes.getLength(); j++) {
			Node codeListNode = codeListNodes.item(j);
			NamedNodeMap codeListAttributes = codeListNode.getAttributes();
			String oid = codeListAttributes.getNamedItem("OID").getNodeValue();
			List<CodeList> codeLists = codeListRepository
					.findByMetaDataVersionIdAndOid(mdv.getId(), oid);
			CodeList codeList;
			boolean needToSave = false;
			if (CollectionUtils.isEmpty(codeLists)) {
				codeList = new CodeList();
				codeList.setCreator("admin");
				needToSave = true;
			} else {
				codeList = codeLists.get(0);
				if (codeList.getStatus().equals(ObjectStatus.deleted)) {
					needToSave = true;
					codeList.setStatus(ObjectStatus.active);
					codeList.setDateLastModified(Calendar.getInstance(TimeZone
							.getTimeZone("UTC")));
				} else if (StringUtils.isEmpty(codeList.getSearchTerm())) {
					needToSave = true;
				}
			}
			if (needToSave) {
				codeList.setUpdatedBy("admin");
				codeList.setMetaDataVersion(mdv);
				codeList.setOid(oid);
				codeList.setName(codeListAttributes.getNamedItem("Name")
						.getNodeValue());
				codeList.setDataType(codeListAttributes
						.getNamedItem("DataType").getNodeValue());
				codeList.setExtCodeId(codeListAttributes.getNamedItem(
						"nciodm:ExtCodeID").getNodeValue());
				codeList.setCodeListExtensible(codeListAttributes.getNamedItem(
						"nciodm:CodeListExtensible").getNodeValue());

				Node descNode = (Node) xPath.evaluate(
						"odm:Description/TranslatedText", codeListNode,
						XPathConstants.NODE);
				if (descNode != null) {
					codeList.setDescription(descNode.getTextContent());
				}
				descNode = (Node) xPath.evaluate("nciodm:CDISCSubmissionValue",
						codeListNode, XPathConstants.NODE);
				if (descNode != null) {
					codeList.setCDISCSubmissionValue(descNode.getTextContent());
				}
				descNode = (Node) xPath.evaluate("nciodm:CDISCSynonym",
						codeListNode, XPathConstants.NODE);
				if (descNode != null) {
					codeList.setCDISCSynonym(descNode.getTextContent());
				}
				descNode = (Node) xPath.evaluate("nciodm:PreferredTerm",
						codeListNode, XPathConstants.NODE);
				if (descNode != null) {
					codeList.setPreferredTerm(descNode.getTextContent());
				}
				codeList.setSearchTerm(StringUtils.lowerCase(codeList.getName()
						+ " " + codeList.getCDISCSubmissionValue()));

				codeList = codeListRepository.save(codeList);
			}
			saveEnumeratedItem(xPath, codeListNode, codeList);
		}
	}

	private void saveEnumeratedItem(XPath xPath, Node codeListNode,
			CodeList codeList) throws XPathExpressionException {
		NodeList eiNodes = (NodeList) xPath.evaluate("odm:EnumeratedItem",
				codeListNode, XPathConstants.NODESET);
		for (int k = 0; k < eiNodes.getLength(); k++) {
			Node eiNode = eiNodes.item(k);

			NamedNodeMap eiAttributes = eiNode.getAttributes();
			String codedValue = eiAttributes.getNamedItem("CodedValue")
					.getNodeValue();
			List<EnumeratedItem> eis = enumeratedItemRepository
					.findByCodeListIdAndCodeValue(codeList.getId(), codedValue);
			boolean needToSave = false;
			EnumeratedItem ei;
			if (CollectionUtils.isEmpty(eis)) {
				ei = new EnumeratedItem();
				ei.setCreator("admin");
				needToSave = true;
			} else {
				ei = eis.get(0);
				if (ei.getStatus().equals(ObjectStatus.deleted)) {
					needToSave = true;
					ei.setStatus(ObjectStatus.active);
					ei.setDateLastModified(Calendar.getInstance(TimeZone
							.getTimeZone("UTC")));
				}
			}
			if (needToSave) {
				ei.setUpdatedBy("admin");
				ei.setCodeList(codeList);

				ei.setCodedValue(codedValue);
				ei.setExtCodeId(eiAttributes.getNamedItem("nciodm:ExtCodeID")
						.getNodeValue());
				NodeList subNodes = (NodeList) xPath.evaluate(
						"nciodm:CDISCSynonym", eiNode, XPathConstants.NODESET);
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < subNodes.getLength(); i++) {
					Node subNode = subNodes.item(i);
					if (i > 0) {
						sb.append(";");
					}
					sb.append(subNode.getTextContent());
				}
				ei.setCDISCSynonym(sb.toString());

				Node subNode = (Node) xPath.evaluate("nciodm:CDISCDefinition",
						eiNode, XPathConstants.NODE);
				if (subNode != null) {
					ei.setCDISCDefinition(subNode.getTextContent());
				}
				subNode = (Node) xPath.evaluate("nciodm:PreferredTerm", eiNode,
						XPathConstants.NODE);
				if (subNode != null) {
					ei.setPreferredTerm(subNode.getTextContent());
				}
				// System.out.println(ei);
				ei = enumeratedItemRepository.save(ei);
			}

		}
	}

}
