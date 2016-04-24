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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.openodm.impl.entity.ObjectStatus;
import com.openodm.impl.entity.ct.ControlTerminology;
import com.openodm.impl.entity.sdtm.SDTMDomain;
import com.openodm.impl.entity.sdtm.SDTMVersion;
import com.openodm.impl.repository.ct.ControlTerminologyRepository;
import com.openodm.impl.repository.sdtm.SDTMDomainRepository;
import com.openodm.impl.repository.sdtm.SDTMVersionRepository;

@Component
@SuppressWarnings("rawtypes")
public class SDTMBO {
	@Autowired
	private SDTMVersionRepository sdtmVersionRepository;
	@Autowired
	private SDTMDomainRepository sdtmDomainRepository;
	@Autowired
	private ControlTerminologyRepository controlTerminologyRepository;

	private static class ODMNamespaceContext implements NamespaceContext {
		final private Map<String, String> namespaceMap = new HashMap<String, String>();

		public ODMNamespaceContext() {
			namespaceMap.put("odm", "http://www.cdisc.org/ns/odm/v1.3");
			namespaceMap.put("nciodm", "http://ncicb.nci.nih.gov/xml/odm/EVS/CDISC");
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

	public void importSDTMVersion(InputStream in, Long ctId) throws Exception {
		ControlTerminology ct = controlTerminologyRepository.findOne(ctId);
		if (ct == null || ct.getStatus().equals(ObjectStatus.deleted)) {
			return;
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(in);
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(new ODMNamespaceContext());
		NodeList nodes = (NodeList) xPath.evaluate("//odm:MetaDataVersion", doc.getDocumentElement(), XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node item = nodes.item(i);
			NamedNodeMap attributes = item.getAttributes();
			String oid = attributes.getNamedItem("OID").getNodeValue();
			List<SDTMVersion> mdvs = sdtmVersionRepository.findByOid(oid);
			SDTMVersion mdv;
			if (CollectionUtils.isEmpty(mdvs)) {
				mdv = new SDTMVersion();
				mdv.setCreator("admin");
				mdv.setUpdatedBy("admin");

				mdv.setOid(oid);
				mdv.setControlTerminology(ct);
				mdv.setName(attributes.getNamedItem("Name").getNodeValue());
				mdv.setDescription(attributes.getNamedItem("Description").getNodeValue());
				mdv.setDefineVersion(attributes.getNamedItem("def:DefineVersion").getNodeValue());
				mdv.setStandardName(attributes.getNamedItem("def:StandardName").getNodeValue());
				mdv.setStandardVersion(attributes.getNamedItem("def:StandardVersion").getNodeValue());
				mdv = sdtmVersionRepository.save(mdv);
			} else {
				mdv = mdvs.get(0);
			}

			NodeList codeListNodes = (NodeList) xPath.evaluate("odm:ItemGroupDef", item, XPathConstants.NODESET);
			saveCodeList(xPath, mdv, codeListNodes);
		}
	}

	private void saveCodeList(XPath xPath, SDTMVersion mdv, NodeList codeListNodes) throws XPathExpressionException {
		for (int j = 0; j < codeListNodes.getLength(); j++) {
			Node codeListNode = codeListNodes.item(j);
			NamedNodeMap codeListAttributes = codeListNode.getAttributes();
			String oid = codeListAttributes.getNamedItem("OID").getNodeValue();
			List<SDTMDomain> codeLists = sdtmDomainRepository.findByVersionIdAndOid(mdv.getId(), oid);
			SDTMDomain codeList;
			boolean needToSave = false;
			if (CollectionUtils.isEmpty(codeLists)) {
				codeList = new SDTMDomain();
				codeList.setCreator("admin");
				needToSave = true;
			} else {
				codeList = codeLists.get(0);
				if (codeList.getStatus().equals(ObjectStatus.deleted)) {
					needToSave = true;
					codeList.setStatus(ObjectStatus.active);
					codeList.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
				}
			}
			if (needToSave) {
				codeList.setUpdatedBy("admin");
				codeList.setSdtmVersion(mdv);
				codeList.setOid(oid);
				codeList.setName(codeListAttributes.getNamedItem("Name").getNodeValue());
				codeList.setDomain(codeListAttributes.getNamedItem("Domain").getNodeValue());
				codeList.setPurpose(codeListAttributes.getNamedItem("Purpose").getNodeValue());
				codeList.setRepeating(codeListAttributes.getNamedItem("Repeating").getNodeValue());

				codeList.setDefClass(codeListAttributes.getNamedItem("def:Class").getNodeValue());
				codeList.setStructure(codeListAttributes.getNamedItem("def:Structure").getNodeValue());

				Node descNode = (Node) xPath.evaluate("odm:Description/TranslatedText", codeListNode, XPathConstants.NODE);
				if (descNode != null) {
					codeList.setDescription(descNode.getTextContent());
				}
				codeList = sdtmDomainRepository.save(codeList);
			}
		}
	}

}
