package com.openodm.impl.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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

import com.openodm.impl.bo.ODMBo;
import com.openodm.impl.controller.response.OperationResponse;
import com.openodm.impl.controller.response.OperationResult;
import com.openodm.impl.entity.AbstractCodeList;
import com.openodm.impl.entity.AbstractEnumeratedItem;
import com.openodm.impl.entity.CodeList;
import com.openodm.impl.entity.ControlTerminology;
import com.openodm.impl.entity.CustomizedCodeList;
import com.openodm.impl.entity.EnumeratedItem;
import com.openodm.impl.entity.ExtendedEnumeratedItem;
import com.openodm.impl.entity.MetaDataVersion;
import com.openodm.impl.entity.ObjectStatus;
import com.openodm.impl.repository.CodeListRepository;
import com.openodm.impl.repository.ControlTerminologyRepository;
import com.openodm.impl.repository.EnumeratedItemRepository;
import com.openodm.impl.repository.ExtendedEnumeratedItemRepository;
import com.openodm.impl.repository.MetaDataVersionRepository;

@RestController
public class CodeListController {
	private static final Logger LOG = LoggerFactory
			.getLogger(CodeListController.class);
	@Autowired
	private MetaDataVersionRepository metaDataVersionRepository;
	@Autowired
	private CodeListRepository codeListRepository;
	@Autowired
	private EnumeratedItemRepository enumeratedItemRepository;
	@Autowired
	private ControlTerminologyRepository controlTerminologyRepository;
	@Autowired
	private ExtendedEnumeratedItemRepository extendedEnumeratedItemRepository;
	@Autowired
	private ODMBo odmBo;

	@RequestMapping(value = "/odm/v1/metaDataVersion", method = RequestMethod.GET)
	public List<MetaDataVersion> listMetaDataVersion() {
		return metaDataVersionRepository.findAll();
	}

	@RequestMapping(value = "/odm/v1/codeList", method = RequestMethod.GET)
	public List<CodeList> listCodeList(
			@RequestParam("metaDataVersionId") Long metaDataVersionId) {
		return codeListRepository.findByMetaDataVersionId(metaDataVersionId);
	}

	@RequestMapping(value = "/odm/v1/codeListQuery", method = RequestMethod.GET)
	public List<CodeList> queryCodeList(@RequestParam("q") String q,
			@RequestParam("ctId") Long ctId) {
		List<CodeList> codeLists = codeListRepository.query(q.toLowerCase());
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
	public List<AbstractCodeList> listCodeListForCT(
			@RequestParam("ctId") Long ctId) {
		ControlTerminology ct = controlTerminologyRepository.findOne(ctId);
		List<CodeList> codeLists = ct.getCodeLists();
		List<CustomizedCodeList> customizedCodeLists = ct
				.getCustomizedCodeLists();
		List<AbstractCodeList> list = new ArrayList<AbstractCodeList>();
		list.addAll(customizedCodeLists);
		list.addAll(codeLists);
		return list;
	}

	@RequestMapping(value = "/odm/v1/enumeratedItem", method = RequestMethod.GET)
	public List<AbstractEnumeratedItem> listEnumeratedItem(
			@RequestParam(value = "ctId", required = false) Long ctId,
			@RequestParam("codeListId") Long codeListId) {
		List<EnumeratedItem> list = enumeratedItemRepository
				.findByCodeListId(codeListId);
		List<AbstractEnumeratedItem> result = new ArrayList<>();
		if (ctId != null) {
			List<ExtendedEnumeratedItem> extendedList = this.extendedEnumeratedItemRepository
					.findByCodeListId(ctId, codeListId);
			result.addAll(extendedList);
		}
		result.addAll(list);
		return result;
	}

	@RequestMapping(value = "/odm/v1/controlTerminology", method = RequestMethod.GET)
	public List<ControlTerminology> listControlTerminology() {
		return controlTerminologyRepository.findAll();
	}

	@RequestMapping(value = "/odm/v1/controlTerminology", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> createControlTerminology(
			@RequestBody Map<String, String> request) {
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
			return new ResponseEntity<OperationResponse>(or,
					HttpStatus.BAD_REQUEST);
		} else {
			List<ControlTerminology> existingCts = controlTerminologyRepository
					.findByName(name);
			if (!CollectionUtils.isEmpty(existingCts)) {
				ControlTerminology existingCt = existingCts.get(0);
				if (existingCt.getStatus().equals(ObjectStatus.active)) {
					OperationResponse or = new OperationResponse();
					OperationResult result = new OperationResult();
					result.setSuccess(false);
					result.setError("CT with same name existed!");
					or.setResult(result);
					return new ResponseEntity<OperationResponse>(or,
							HttpStatus.BAD_REQUEST);
				} else {
					ct = existingCt;
					ct.setStatus(ObjectStatus.active);
					ct.setDateLastModified(Calendar.getInstance(TimeZone
							.getTimeZone("UTC")));
					ct.setUpdatedBy("admin");
				}
			}
		}

		ct.setName(name);
		ct.setDescription(desc);
		return saveCT(ct, true);
	}

	@RequestMapping(value = "/odm/v1/controlTerminology/{id}", method = RequestMethod.PUT)
	public ResponseEntity<OperationResponse> updateControlTerminology(
			@PathVariable("id") Long id,
			@RequestBody Map<String, String> request) {
		String name = StringUtils.trim(request.get("name"));
		String desc = StringUtils.trim(request.get("description"));
		ControlTerminology ct = null;
		if (StringUtils.isEmpty(name)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Name is required");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or,
					HttpStatus.BAD_REQUEST);
		}
		ct = controlTerminologyRepository.findOne(id);
		if (ct == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("id is invalid");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or,
					HttpStatus.NOT_FOUND);
		}

		ct.setName(name);
		ct.setDescription(desc);
		ct.setStatus(ObjectStatus.active);
		ct.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
		ct.setUpdatedBy("admin");
		return saveCT(ct, false);
	}

	@RequestMapping(value = "/odm/v1/controlTerminology/{id}/codeList/{codeListId}", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> addCodeList(
			@PathVariable("id") Long id,
			@PathVariable("codeListId") Long codeListId) {
		ControlTerminology ct = controlTerminologyRepository.findOne(id);
		if (ct == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("id is invalid");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or,
					HttpStatus.BAD_REQUEST);
		} else {
			CodeList codeList = codeListRepository.findOne(codeListId);
			if (codeList == null) {
				OperationResponse or = new OperationResponse();
				OperationResult result = new OperationResult();
				result.setSuccess(false);
				result.setError("codeListId is invalid");
				or.setResult(result);
				return new ResponseEntity<OperationResponse>(or,
						HttpStatus.BAD_REQUEST);
			} else {
				List<CodeList> codeLists = ct.getCodeLists();
				if (!codeLists.contains(codeList)) {
					codeLists.add(codeList);
				}
				return saveCT(ct, false);
			}
		}

	}

	@RequestMapping(value = "/odm/v1/controlTerminology/{id}/metaDataVersion/{metaDataVersionId}", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> addMetaDataVersion(
			@PathVariable("id") Long id,
			@PathVariable("metaDataVersionId") Long metaDataVersionId) {
		ControlTerminology ct = controlTerminologyRepository.findOne(id);
		if (ct == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("id is invalid");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or,
					HttpStatus.BAD_REQUEST);
		} else {
			List<CodeList> codeLists = codeListRepository
					.findByMetaDataVersionId(metaDataVersionId);
			if (CollectionUtils.isEmpty(codeLists)) {
				OperationResponse or = new OperationResponse();
				OperationResult result = new OperationResult();
				result.setSuccess(false);
				result.setError("metaDataVersionId is invalid");
				or.setResult(result);
				return new ResponseEntity<OperationResponse>(or,
						HttpStatus.BAD_REQUEST);
			} else {
				List<CodeList> existingCodeLists = ct.getCodeLists();
				for (CodeList codeList : codeLists) {
					if (!existingCodeLists.contains(codeList)) {
						existingCodeLists.add(codeList);
					}
				}
				return saveCT(ct, false);
			}
		}

	}

	protected ResponseEntity<OperationResponse> saveCT(ControlTerminology ct,
			boolean isNew) {
		try {
			controlTerminologyRepository.save(ct);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(true);
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or,
					isNew ? HttpStatus.CREATED : HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error during creating the CT", e);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Error during creating the CT");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/odm/v1/import", method = RequestMethod.POST)
	public void upload(@RequestParam("file") MultipartFile file)
			throws Exception {
		this.odmBo.importMedatDataVersion(file.getInputStream());

	}

}
