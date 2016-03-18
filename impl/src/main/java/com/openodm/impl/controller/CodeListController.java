package com.openodm.impl.controller;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.openodm.impl.controller.response.OperationResponse;
import com.openodm.impl.controller.response.OperationResult;
import com.openodm.impl.entity.CodeList;
import com.openodm.impl.entity.ControlTerminology;
import com.openodm.impl.entity.EnumeratedItem;
import com.openodm.impl.entity.MetaDataVersion;
import com.openodm.impl.entity.ObjectStatus;
import com.openodm.impl.repository.CodeListRepository;
import com.openodm.impl.repository.ControlTerminologyRepository;
import com.openodm.impl.repository.EnumeratedItemRepository;
import com.openodm.impl.repository.MetaDataVersionRepository;

@RestController
public class CodeListController {
	private static final Logger LOG = LoggerFactory.getLogger(CodeListController.class);
	@Autowired
	private MetaDataVersionRepository metaDataVersionRepository;
	@Autowired
	private CodeListRepository codeListRepository;
	@Autowired
	private EnumeratedItemRepository enumeratedItemRepository;
	@Autowired
	private ControlTerminologyRepository controlTerminologyRepository;

	@RequestMapping(value = "/odm/v1/metaDataVersion", method = RequestMethod.GET)
	public List<MetaDataVersion> listMetaDataVersion() {
		return metaDataVersionRepository.findAll();
	}

	@RequestMapping(value = "/odm/v1/codeList", method = RequestMethod.GET)
	public List<CodeList> listCodeList(@RequestParam("metaDataVersionId") Long metaDataVersionId) {
		return codeListRepository.findByMetaDataVersionId(metaDataVersionId);
	}

	@RequestMapping(value = "/odm/v1/codeListQuery", method = RequestMethod.GET)
	public List<CodeList> queryCodeList(@RequestParam("q") String q, @RequestParam("ctId") Long ctId) {
		List<CodeList> codeLists = codeListRepository.query(q);
		ControlTerminology ct = controlTerminologyRepository.findOne(ctId);
		Iterator<CodeList> it = codeLists.iterator();
		List<CodeList> codeLists2 = ct.getCodeLists();
		while (it.hasNext()) {
			CodeList cl = it.next();
			boolean has = contains(codeLists2, cl);
			if (has) {
				it.remove();
			}
		}
		return codeLists;
	}

	protected boolean contains(List<CodeList> codeLists2, CodeList cl) {
		for (CodeList cl2 : codeLists2) {
			if (cl.getId().equals(cl2.getId())) {
				return true;
			}
		}
		return false;
	}

	@RequestMapping(value = "/odm/v1/codeListForCT", method = RequestMethod.GET)
	public List<CodeList> listCodeListForCT(@RequestParam("ctId") Long ctId) {
		return controlTerminologyRepository.findOne(ctId).getCodeLists();
	}

	@RequestMapping(value = "/odm/v1/enumeratedItem", method = RequestMethod.GET)
	public List<EnumeratedItem> listEnumeratedItem(@RequestParam("codeListId") Long codeListId) {
		return enumeratedItemRepository.findByCodeListId(codeListId);
	}

	@RequestMapping(value = "/odm/v1/controlTerminology", method = RequestMethod.GET)
	public List<ControlTerminology> listControlTerminology() {
		return controlTerminologyRepository.findAll();
	}

	@RequestMapping(value = "/odm/v1/controlTerminology", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> createControlTerminology(@RequestBody Map<String, String> request) {
		String name = StringUtils.trim(request.get("name"));
		String desc = StringUtils.trim(request.get("description"));
		ControlTerminology ct = new ControlTerminology();
		ct.setCreator("admin");
		ct.setUpdatedBy("admin");
		if (StringUtils.isEmpty(name)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Name is required");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		} else {
			List<ControlTerminology> existingCts = controlTerminologyRepository.findByName(name);
			if (!CollectionUtils.isEmpty(existingCts)) {
				ControlTerminology existingCt = existingCts.get(0);
				if (existingCt.getStatus().equals(ObjectStatus.active)) {
					OperationResponse or = new OperationResponse();
					OperationResult result = new OperationResult();
					result.setSuccess(false);
					result.setError("CT with same name existed!");
					or.setResult(result);
					return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
				} else {
					ct = existingCt;
					ct.setStatus(ObjectStatus.active);
					ct.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
					ct.setUpdatedBy("admin");
				}
			}
		}

		ct.setName(name);
		ct.setDescription(desc);
		return saveCT(ct);
	}

	@RequestMapping(value = "/odm/v1/controlTerminology", method = RequestMethod.PUT)
	public ResponseEntity<OperationResponse> updateControlTerminology(@RequestBody Map<String, String> request) {
		String idStr = StringUtils.trim(request.get("id"));
		String name = StringUtils.trim(request.get("name"));
		String desc = StringUtils.trim(request.get("description"));
		ControlTerminology ct = null;
		if (StringUtils.isEmpty(name)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Name is required");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		if (StringUtils.isEmpty(idStr) || !StringUtils.isNumeric(idStr)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("id is invalid");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		} else {
			Long id = Long.valueOf(idStr);
			ct = controlTerminologyRepository.findOne(id);
			if (ct == null) {
				OperationResponse or = new OperationResponse();
				OperationResult result = new OperationResult();
				result.setSuccess(false);
				result.setError("id is invalid");
				or.setResult(result);
				return new ResponseEntity<OperationResponse>(or, HttpStatus.NOT_FOUND);
			}
		}

		ct.setName(name);
		ct.setDescription(desc);
		ct.setStatus(ObjectStatus.active);
		ct.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
		ct.setUpdatedBy("admin");
		return saveCT(ct);
	}

	@RequestMapping(value = "/odm/v1/controlTerminology/{id}/codeList/{codeListId}", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> addCodeList(@PathVariable("id") Long id,
			@PathVariable("codeListId") Long codeListId) {
		ControlTerminology ct = controlTerminologyRepository.findOne(id);
		if (ct == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("id is invalid");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		} else {
			CodeList codeList = codeListRepository.findOne(codeListId);
			if (codeList == null) {
				OperationResponse or = new OperationResponse();
				OperationResult result = new OperationResult();
				result.setSuccess(false);
				result.setError("codeListId is invalid");
				or.setResult(result);
				return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
			} else {
				ct.getCodeLists().add(codeList);
				return saveCT(ct);
			}
		}

	}

	protected ResponseEntity<OperationResponse> saveCT(ControlTerminology ct) {
		try {
			controlTerminologyRepository.save(ct);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(true);
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.CREATED);
		} catch (Exception e) {
			LOG.error("Error during creating the CT", e);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Error during creating the CT");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/odm/v1/import", method = RequestMethod.POST)
	public void upload(@RequestParam("file") MultipartFile file) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file.getInputStream());
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes = (NodeList) xPath.evaluate("//MetaDataVersion", doc.getDocumentElement(),
				XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node item = nodes.item(i);
			NamedNodeMap attributes = item.getAttributes();
			String oid = attributes.getNamedItem("OID").getNodeValue();
			List<MetaDataVersion> mdvs = metaDataVersionRepository.findByOid(oid);
			MetaDataVersion mdv;
			if (CollectionUtils.isEmpty(mdvs)) {
				mdv = new MetaDataVersion();
				mdv.setCreator("admin");
				mdv.setUpdatedBy("admin");

				mdv.setOid(oid);
				mdv.setName(attributes.getNamedItem("Name").getNodeValue());
				mdv.setDescription(attributes.getNamedItem("Description").getNodeValue());
				mdv = metaDataVersionRepository.save(mdv);
			} else {
				mdv = mdvs.get(0);
			}

			NodeList codeListNodes = (NodeList) xPath.evaluate("CodeList", item, XPathConstants.NODESET);
			saveCodeList(xPath, mdv, codeListNodes);
		}

	}

	private void saveCodeList(XPath xPath, MetaDataVersion mdv, NodeList codeListNodes) throws XPathExpressionException {
		for (int j = 0; j < codeListNodes.getLength(); j++) {
			Node codeListNode = codeListNodes.item(j);
			NamedNodeMap codeListAttributes = codeListNode.getAttributes();
			String oid = codeListAttributes.getNamedItem("OID").getNodeValue();
			List<CodeList> codeLists = codeListRepository.findByMetaDataVersionIdAndOid(mdv.getId(), oid);
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
					codeList.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
				}
			}
			if (needToSave) {
				codeList.setUpdatedBy("admin");
				codeList.setMetaDataVersion(mdv);
				codeList.setOid(oid);
				codeList.setName(codeListAttributes.getNamedItem("Name").getNodeValue());
				codeList.setDataType(codeListAttributes.getNamedItem("DataType").getNodeValue());
				codeList.setExtCodeId(codeListAttributes.getNamedItem("nciodm:ExtCodeID").getNodeValue());
				codeList.setCodeListExtensible(codeListAttributes.getNamedItem("nciodm:CodeListExtensible")
						.getNodeValue());

				Node descNode = (Node) xPath.evaluate("Description/TranslatedText", codeListNode, XPathConstants.NODE);
				if (descNode != null) {
					codeList.setDescription(descNode.getTextContent());
				}
				descNode = (Node) xPath.evaluate("CDISCSubmissionValue", codeListNode, XPathConstants.NODE);
				if (descNode != null) {
					codeList.setCDISCSubmissionValue(descNode.getTextContent());
				}
				descNode = (Node) xPath.evaluate("CDISCSynonym", codeListNode, XPathConstants.NODE);
				if (descNode != null) {
					codeList.setCDISCSynonym(descNode.getTextContent());
				}
				descNode = (Node) xPath.evaluate("PreferredTerm", codeListNode, XPathConstants.NODE);
				if (descNode != null) {
					codeList.setPreferredTerm(descNode.getTextContent());
				}
				codeList = codeListRepository.save(codeList);

			}
			saveEnumeratedItem(xPath, codeListNode, codeList);
		}
	}

	private void saveEnumeratedItem(XPath xPath, Node codeListNode, CodeList codeList) throws XPathExpressionException {
		NodeList eiNodes = (NodeList) xPath.evaluate("EnumeratedItem", codeListNode, XPathConstants.NODESET);
		for (int k = 0; k < eiNodes.getLength(); k++) {
			Node eiNode = eiNodes.item(k);

			NamedNodeMap eiAttributes = eiNode.getAttributes();
			String codedValue = eiAttributes.getNamedItem("CodedValue").getNodeValue();
			List<EnumeratedItem> eis = enumeratedItemRepository.findByCodeListIdAndCodeValue(codeList.getId(),
					codedValue);
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
					ei.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
				}
			}
			if (needToSave) {
				ei.setUpdatedBy("admin");
				ei.setCodeList(codeList);

				ei.setCodedValue(codedValue);
				ei.setExtCodeId(eiAttributes.getNamedItem("nciodm:ExtCodeID").getNodeValue());
				NodeList subNodes = (NodeList) xPath.evaluate("CDISCSynonym", eiNode, XPathConstants.NODESET);
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < subNodes.getLength(); i++) {
					Node subNode = subNodes.item(i);
					if (i > 0) {
						sb.append(";");
					}
					sb.append(subNode.getTextContent());
				}
				ei.setCDISCSynonym(sb.toString());

				Node subNode = (Node) xPath.evaluate("CDISCDefinition", eiNode, XPathConstants.NODE);
				if (subNode != null) {
					ei.setCDISCDefinition(subNode.getTextContent());
				}
				subNode = (Node) xPath.evaluate("PreferredTerm", eiNode, XPathConstants.NODE);
				if (subNode != null) {
					ei.setPreferredTerm(subNode.getTextContent());
				}
				// System.out.println(ei);
				ei = enumeratedItemRepository.save(ei);
			}

		}
	}

}
