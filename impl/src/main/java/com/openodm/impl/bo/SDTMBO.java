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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.openodm.impl.entity.ObjectStatus;
import com.openodm.impl.entity.ct.CodeList;
import com.openodm.impl.entity.ct.ControlTerminology;
import com.openodm.impl.entity.ct.EnumeratedItem;
import com.openodm.impl.entity.sdtm.SDTMDomain;
import com.openodm.impl.entity.sdtm.SDTMVariable;
import com.openodm.impl.entity.sdtm.SDTMVariableRef;
import com.openodm.impl.entity.sdtm.SDTMVersion;
import com.openodm.impl.repository.ct.ControlTerminologyRepository;
import com.openodm.impl.repository.ct.EnumeratedItemRepository;
import com.openodm.impl.repository.sdtm.SDTMDomainRepository;
import com.openodm.impl.repository.sdtm.SDTMVariableRefRepository;
import com.openodm.impl.repository.sdtm.SDTMVariableRepository;
import com.openodm.impl.repository.sdtm.SDTMVersionRepository;

@Component
@SuppressWarnings("rawtypes")
public class SDTMBO {
	private static final Logger LOG = LoggerFactory.getLogger(SDTMBO.class);

	@Autowired
	private SDTMVersionRepository sdtmVersionRepository;
	@Autowired
	private SDTMDomainRepository sdtmDomainRepository;
	@Autowired
	private SDTMVariableRefRepository sdtmVariableRefRepository;
	@Autowired
	private SDTMVariableRepository sdtmVariableRepository;

	@Autowired
	private ControlTerminologyRepository controlTerminologyRepository;
	@Autowired
	private EnumeratedItemRepository enumeratedItemRepository;

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
		Map<String, CodeList> codeListMap = new HashMap<String, CodeList>();
		Map<String, EnumeratedItem> enumItemMap = new HashMap<String, EnumeratedItem>();
		for (CodeList codeList : ct.getCodeLists()) {
			codeListMap.put(codeList.getExtCodeId(), codeList);
			List<EnumeratedItem> enumItems = enumeratedItemRepository.findByCodeListId(codeList.getId());
			if (!CollectionUtils.isEmpty(enumItems)) {
				for (EnumeratedItem enumItem : enumItems) {
					enumItemMap.put(enumItem.getExtCodeId(), enumItem);
				}
			}
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
			List<SDTMVersion> versions = sdtmVersionRepository.findByOid(oid);
			SDTMVersion version;
			if (CollectionUtils.isEmpty(versions)) {
				version = new SDTMVersion();
				version.setCreator("admin");
				version.setUpdatedBy("admin");

				version.setOid(oid);
				version.setControlTerminology(ct);
				version.setName(attributes.getNamedItem("Name").getNodeValue());
				version.setDescription(attributes.getNamedItem("Description").getNodeValue());
				version.setDefineVersion(attributes.getNamedItem("def:DefineVersion").getNodeValue());
				version.setStandardName(attributes.getNamedItem("def:StandardName").getNodeValue());
				version.setStandardVersion(attributes.getNamedItem("def:StandardVersion").getNodeValue());
				version = sdtmVersionRepository.save(version);
			} else {
				version = versions.get(0);
			}

			NodeList domains = (NodeList) xPath.evaluate("odm:ItemGroupDef", item, XPathConstants.NODESET);
			if (domains != null) {
				saveDomains(xPath, version, domains, item, codeListMap, enumItemMap);
			}
		}
	}

	private void saveDomains(XPath xPath, SDTMVersion version, NodeList domainNodes, Node versionNode, Map<String, CodeList> codeListMap,
			Map<String, EnumeratedItem> enumItemMap) throws XPathExpressionException {
		for (int i = 0; i < domainNodes.getLength(); i++) {
			Node domainNode = domainNodes.item(i);
			NamedNodeMap domainAttributes = domainNode.getAttributes();
			String oid = domainAttributes.getNamedItem("OID").getNodeValue();
			List<SDTMDomain> domains = sdtmDomainRepository.findByVersionIdAndOid(version.getId(), oid);
			SDTMDomain domain;
			boolean needToSave = false;
			if (CollectionUtils.isEmpty(domains)) {
				domain = new SDTMDomain();
				domain.setCreator("admin");
				needToSave = true;
			} else {
				domain = domains.get(0);
				if (domain.getStatus().equals(ObjectStatus.deleted)) {
					needToSave = true;
					domain.setStatus(ObjectStatus.active);
					domain.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
				}
			}
			if (needToSave) {
				domain.setUpdatedBy("admin");
				domain.setSdtmVersion(version);
				domain.setOid(oid);
				domain.setName(domainAttributes.getNamedItem("Name").getNodeValue());
				domain.setDomain(domainAttributes.getNamedItem("Domain").getNodeValue());
				domain.setPurpose(domainAttributes.getNamedItem("Purpose").getNodeValue());
				domain.setRepeating(domainAttributes.getNamedItem("Repeating").getNodeValue());

				domain.setDefClass(domainAttributes.getNamedItem("def:Class").getNodeValue());
				domain.setStructure(domainAttributes.getNamedItem("def:Structure").getNodeValue());

				Node descNode = (Node) xPath.evaluate("odm:Description/TranslatedText", domainNode, XPathConstants.NODE);
				if (descNode != null) {
					domain.setDescription(descNode.getTextContent());
				}
				domain = sdtmDomainRepository.save(domain);
			}
			NodeList varRefNodes = (NodeList) xPath.evaluate("odm:ItemRef", domainNode, XPathConstants.NODESET);
			if (varRefNodes != null) {
				saveVars(xPath, versionNode, codeListMap, domain, varRefNodes, enumItemMap);
			}
		}
	}

	private void saveVars(XPath xPath, Node versionNode, Map<String, CodeList> codeListMap, SDTMDomain domain, NodeList varRefNodes,
			Map<String, EnumeratedItem> enumItemMap) throws XPathExpressionException {
		for (int j = 0; j < varRefNodes.getLength(); j++) {
			Node varRefNode = varRefNodes.item(j);
			NamedNodeMap varRefAttributes = varRefNode.getAttributes();
			String varOid = varRefAttributes.getNamedItem("ItemOID").getNodeValue();
			Node varNode = (Node) xPath.evaluate("//odm:ItemDef[@OID='" + varOid + "']", versionNode, XPathConstants.NODE);
			if (varNode == null) {
				LOG.error("Cannot find var, oid={}", varOid);
				continue;
			} else {
				SDTMVariable var;
				List<SDTMVariable> vars = sdtmVariableRepository.findByDomainIdAndOid(domain.getId(), varOid);
				if (CollectionUtils.isEmpty(vars)) {
					var = new SDTMVariable();
					var.setCreator("admin");
					var.setUpdatedBy("admin");
					var.setSdtmDomain(domain);
					var.setOid(varOid);
					NamedNodeMap varNodeAttributes = varNode.getAttributes();
					var.setName(varNodeAttributes.getNamedItem("Name").getNodeValue());
					Node dataTypeAttr = varNodeAttributes.getNamedItem("DataType");
					if (dataTypeAttr == null) {
						var.setDataType("text");
					} else {
						var.setDataType(dataTypeAttr.getNodeValue());
					}
					Node lengthNode = varNodeAttributes.getNamedItem("Length");
					if (lengthNode != null) {
						var.setLength(Integer.valueOf(lengthNode.getNodeValue()));
					} else {
						var.setLength(0);
					}
					Node descNode = (Node) xPath.evaluate("odm:Description/TranslatedText", varNode, XPathConstants.NODE);
					if (descNode != null) {
						var.setDescription(descNode.getTextContent());
					}

					sdtmVariableRepository.save(var);
				} else {
					var = vars.get(0);
				}
				Node codeListRefNode = (Node) xPath.evaluate("odm:CodeListRef", varNode, XPathConstants.NODE);
				if (codeListRefNode != null) {
					String codeListOid = codeListRefNode.getAttributes().getNamedItem("CodeListOID").getNodeValue();
					Node codeListNode = (Node) xPath.evaluate("//odm:CodeList[@OID='" + codeListOid + "']", versionNode, XPathConstants.NODE);
					if (codeListNode != null) {
						// String name =
						// codeListNode.getAttributes().getNamedItem("Name").getNodeValue();
						NodeList codeListItemNodes = (NodeList) xPath.evaluate("odm:CodeListItem", codeListNode, XPathConstants.NODESET);
						if (codeListItemNodes != null && codeListItemNodes.getLength() > 0) {
							for (int i = 0; i < codeListItemNodes.getLength(); i++) {
								Node codeListItemNode = codeListItemNodes.item(i);
								// ref for enumerated items
								Node aliasNode = (Node) xPath.evaluate("odm:Alias", codeListItemNode, XPathConstants.NODE);
								if (aliasNode != null) {
									String extCodeId = aliasNode.getAttributes().getNamedItem("Name").getNodeValue();
									EnumeratedItem enumItem = enumItemMap.get(extCodeId);
									if (enumItem != null) {
										List<EnumeratedItem> enumItems = var.getEnumeratedItems();
										if (!enumItems.contains(enumItem)) {
											enumItems.add(enumItem);
											var.setUpdatedBy("admin");
											var.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
										}
									} else {
										LOG.error("Cannot find EnumeratedItem, extCodeId={}", extCodeId);
									}
								}
							}
						} else {
							NodeList enumItemNodes = (NodeList) xPath.evaluate("odm:EnumeratedItem", codeListNode, XPathConstants.NODESET);
							if (enumItemNodes != null && enumItemNodes.getLength() > 0) {
								// ref for enumerated items
								for (int i = 0; i < enumItemNodes.getLength(); i++) {
									Node enumItemNode = enumItemNodes.item(i);
									Node aliasNode = (Node) xPath.evaluate("odm:Alias", enumItemNode, XPathConstants.NODE);
									if (aliasNode != null) {
										String extCodeId = aliasNode.getAttributes().getNamedItem("Name").getNodeValue();
										// LOG.info("CodedValue={}",
										// enumItemNode.getAttributes().getNamedItem("CodedValue").getNodeValue());
										EnumeratedItem enumItem = enumItemMap.get(extCodeId);
										if (enumItem != null) {
											List<EnumeratedItem> enumItems = var.getEnumeratedItems();
											if (!enumItems.contains(enumItem)) {
												var.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
												enumItems.add(enumItem);
												var.setUpdatedBy("admin");
												var.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
											}
										} else {
											LOG.error("Cannot find EnumeratedItem, extCodeId={}", extCodeId);
										}
									}
								}
							} else {
								// ref for codeList
								Node aliasNode = (Node) xPath.evaluate("odm:Alias", codeListNode, XPathConstants.NODE);
								if (aliasNode != null) {
									String extCodeId = aliasNode.getAttributes().getNamedItem("Name").getNodeValue();
									CodeList codeList = codeListMap.get(extCodeId);
									if (codeList != null) {
										if (var.getCodeList() == null || !var.getCodeList().getId().equals(codeList.getId())) {
											var.setUpdatedBy("admin");
											var.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
											var.setCodeList(codeList);
										}
									} else {
										LOG.error("Cannot find CodeList, extCodeId={}", extCodeId);
									}
								}
							}
						}
					}
					sdtmVariableRepository.save(var);
				}
				List<SDTMVariableRef> varRefs = sdtmVariableRefRepository.findByDomainIdAndVariableId(domain.getId(), var.getId());
				SDTMVariableRef varRef;
				Node roleAttr = varRefAttributes.getNamedItem("Role");
				if (CollectionUtils.isEmpty(varRefs)) {
					varRef = new SDTMVariableRef();
					varRef.setCreator("admin");
					varRef.setUpdatedBy("admin");
					varRef.setSdtmDomain(domain);
					varRef.setSdtmVariable(var);
					varRef.setMandatory(varRefAttributes.getNamedItem("Mandatory").getNodeValue());
					varRef.setOrderNumber(Integer.valueOf(varRefAttributes.getNamedItem("OrderNumber").getNodeValue()));
				} else {
					varRef = varRefs.get(0);
					varRef.setMandatory(varRefAttributes.getNamedItem("Mandatory").getNodeValue());
					varRef.setOrderNumber(Integer.valueOf(varRefAttributes.getNamedItem("OrderNumber").getNodeValue()));
					varRef.setUpdatedBy("admin");
					varRef.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
				}
				if (roleAttr != null) {
					varRef.setRole(roleAttr.getNodeValue());
				}
				sdtmVariableRefRepository.save(varRef);
			}
		}
	}
}
