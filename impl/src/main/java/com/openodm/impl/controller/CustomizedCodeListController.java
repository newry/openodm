package com.openodm.impl.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.openodm.impl.controller.response.OperationResponse;
import com.openodm.impl.controller.response.OperationResult;
import com.openodm.impl.entity.ObjectStatus;
import com.openodm.impl.entity.ct.AbstractEnumeratedItem;
import com.openodm.impl.entity.ct.CodeList;
import com.openodm.impl.entity.ct.ControlTerminology;
import com.openodm.impl.entity.ct.CustomizedCodeList;
import com.openodm.impl.entity.ct.CustomizedEnumeratedItem;
import com.openodm.impl.entity.ct.ExtendedEnumeratedItem;
import com.openodm.impl.repository.ct.CodeListRepository;
import com.openodm.impl.repository.ct.ControlTerminologyRepository;
import com.openodm.impl.repository.ct.CustomizedCodeListRepository;
import com.openodm.impl.repository.ct.CustomizedEnumeratedItemRepository;
import com.openodm.impl.repository.ct.ExtendedEnumeratedItemRepository;
import com.openodm.impl.util.Utils;

@RestController
public class CustomizedCodeListController {
	private static final Logger LOG = LoggerFactory.getLogger(CustomizedCodeListController.class);
	@Autowired
	private CodeListRepository codeListRepository;
	@Autowired
	private ControlTerminologyRepository controlTerminologyRepository;
	@Autowired
	private CustomizedCodeListRepository customizedCodeListRepository;
	@Autowired
	private CustomizedEnumeratedItemRepository customizedEnumeratedItemRepository;
	@Autowired
	private ExtendedEnumeratedItemRepository extendedEnumeratedItemRepository;

	@RequestMapping(value = "/odm/v1/controlTerminology/{ctId}/customizedCodeList", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> createCustomizedCodeList(@PathVariable("ctId") Long ctId, @RequestBody Map<String, String> request) {
		request.put("ctId", String.valueOf(ctId));
		return this.createCustomizedCodeList(request);
	}

	@RequestMapping(value = "/odm/v1/customizedCodeList", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> createCustomizedCodeList(@RequestBody Map<String, String> request) {
		String name = StringUtils.trim(request.get("name"));
		String desc = StringUtils.trim(request.get("description"));
		String submissionValue = getSubmissionValue(request);
		String ctId = StringUtils.trim(request.get("ctId"));
		ControlTerminology ct = null;
		CustomizedCodeList ccl = new CustomizedCodeList();
		if (StringUtils.isEmpty(name)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Name is required");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}

		if (StringUtils.isEmpty(ctId) || !StringUtils.isNumeric(ctId)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Invalid ctId");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);

		} else {
			ct = controlTerminologyRepository.findOne(Long.valueOf(ctId));
			if (ct == null) {
				OperationResponse or = new OperationResponse();
				OperationResult result = new OperationResult();
				result.setSuccess(false);
				result.setError("Invalid ctId");
				or.setResult(result);
				return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
			}
		}
		try {
			ccl.setName(name);
			ccl.setDescription(desc);
			ccl.setCDISCSubmissionValue(submissionValue);
			ccl.setCreator("admin");
			ccl.setUpdatedBy("admin");
			customizedCodeListRepository.save(ccl);
			ccl.setExtCodeId("X" + new DecimalFormat("000000").format(ccl.getId()));
			customizedCodeListRepository.save(ccl);
			ct.getCustomizedCodeLists().add(ccl);
			this.controlTerminologyRepository.save(ct);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(true);
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.CREATED);
		} catch (Exception e) {
			LOG.error("Error during creating the CustomizedCodeList", e);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Error during creating the CustomizedCodeList");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String getSubmissionValue(Map<String, String> request) {
		String submissionValue = StringUtils.trim(request.get("submissionValue"));
		if (StringUtils.isEmpty(submissionValue)) {
			submissionValue = StringUtils.trim(request.get("cdiscsubmissionValue"));
		}
		return submissionValue;
	}

	@RequestMapping(value = "/odm/v1/customizedCodeList/{id}", method = RequestMethod.PUT)
	public ResponseEntity<OperationResponse> updateCustomizedCodeList(@PathVariable("id") Long id, @RequestBody Map<String, String> request) {
		String name = StringUtils.trim(request.get("name"));
		String desc = StringUtils.trim(request.get("description"));
		String submissionValue = getSubmissionValue(request);
		CustomizedCodeList ccl = null;
		if (StringUtils.isEmpty(name)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Name is required");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		ccl = this.customizedCodeListRepository.findOne(id);
		if (ccl == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("id is invalid");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.NOT_FOUND);
		}

		try {
			ccl.setName(name);
			ccl.setDescription(desc);
			ccl.setCDISCSubmissionValue(submissionValue);
			ccl.setUpdatedBy("admin");
			Utils.updatePODataLastModified(ccl);
			customizedCodeListRepository.save(ccl);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(true);
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error during creating the CustomizedCodeList", e);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Error during creating the CustomizedCodeList");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/odm/v1/controlTerminology/{ctId}/customizedCodeList/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<OperationResponse> deleteCustomizedCodeList(@PathVariable("ctId") Long ctId, @PathVariable("id") Long id) {
		ControlTerminology ct = this.controlTerminologyRepository.findOne(ctId);
		if (ct == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("ctId is invalid");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.NOT_FOUND);
		}
		CustomizedCodeList ccl = this.customizedCodeListRepository.findOne(id);
		if (ccl == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("id is invalid");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.NOT_FOUND);
		}

		try {
			ct.getCustomizedCodeLists().remove(ccl);
			this.controlTerminologyRepository.save(ct);
			ccl.setStatus(ObjectStatus.deleted);
			ccl.setUpdatedBy("admin");
			Utils.updatePODataLastModified(ccl);
			customizedCodeListRepository.save(ccl);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(true);
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error during creating the CustomizedCodeList", e);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Error during creating the CustomizedCodeList");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/odm/v1/customizedEnumeratedItem", method = RequestMethod.GET)
	public List<? extends AbstractEnumeratedItem> listEnumeratedItem(@RequestParam("codeListId") Long codeListId) {
		List<CustomizedEnumeratedItem> list = customizedEnumeratedItemRepository.findByCodeListId(codeListId);
		return list;
	}

	@RequestMapping(value = "/odm/v1/controlTerminology/{ctId}/customizedCodeList/{ccId}/customizedEnumeratedItem", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> createCustomizedEnumeratedItem(@PathVariable("ctId") Long ctId, @PathVariable("ccId") Long ccId,
			@RequestBody Map<String, String> request) {
		CustomizedCodeList customizedCodeList = customizedCodeListRepository.findOne(ccId);
		if (customizedCodeList == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("invalid codeListId");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		String extCodeId = StringUtils.trim(request.get("extCodeId"));
		String codedValue = StringUtils.trim(request.get("codedValue"));
		if (StringUtils.isAnyEmpty(extCodeId, codedValue)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("invalid extCodeId or codedValue");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		CustomizedEnumeratedItem cei = customizedEnumeratedItemRepository.findByCodeListIdAndCodeValue(ccId, codedValue);
		if (cei == null) {
			cei = new CustomizedEnumeratedItem();
			cei.setExtCodeId(extCodeId);
			cei.setCodedValue(codedValue);
			cei.setCustomizedCodeList(customizedCodeList);
			cei.setCreator("admin");
			cei.setUpdatedBy("admin");
		} else if (!cei.getStatus().equals(ObjectStatus.active)) {
			Utils.updatePODateAdded(cei);
			Utils.updatePOStatus(cei, ObjectStatus.active);
		} else {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Code already existed");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		try {
			customizedEnumeratedItemRepository.save(cei);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(true);
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.CREATED);
		} catch (Exception e) {
			LOG.error("Error during creating the CustomizedEnumeratedItem", e);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Error during creating the CustomizedEnumeratedItem");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/odm/v1/controlTerminology/{ctId}/codeList/{ccId}/extendedEnumeratedItem", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> createExtendedEnumeratedItem(@PathVariable("ctId") Long ctId, @PathVariable("ccId") Long ccId,
			@RequestBody Map<String, String> request) {
		CodeList codeList = codeListRepository.findOne(ccId);
		if (codeList == null || StringUtils.equalsIgnoreCase(codeList.getCodeListExtensible(), "No")) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("invalid codeListId");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		ControlTerminology ct = this.controlTerminologyRepository.findOne(ctId);
		if (ct == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("invalid ctId");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}

		String extCodeId = StringUtils.trim(request.get("extCodeId"));
		String codedValue = StringUtils.trim(request.get("codedValue"));
		if (StringUtils.isAnyEmpty(extCodeId, codedValue)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("invalid extCodeId or codedValue");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}

		ExtendedEnumeratedItem cei = extendedEnumeratedItemRepository.findByCtIdAndCodeListIdAndCodeValue(ctId, ccId, codedValue);
		if (cei == null) {
			cei = new ExtendedEnumeratedItem();
		} else if (!cei.getStatus().equals(ObjectStatus.active)) {
			Utils.updatePODateAdded(cei);
			Utils.updatePOStatus(cei, ObjectStatus.active);
		} else {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Code already existed");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		cei.setExtCodeId(extCodeId);
		cei.setCodedValue(codedValue);
		cei.setCodeList(codeList);
		cei.setControlTerminology(ct);
		cei.setCreator("admin");
		cei.setUpdatedBy("admin");
		try {
			extendedEnumeratedItemRepository.save(cei);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(true);
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.CREATED);
		} catch (Exception e) {
			LOG.error("Error during creating the ExtendedEnumeratedItem", e);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Error during creating the ExtendedEnumeratedItem");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/odm/v1/controlTerminology/{ctId}/codeList/{ccId}/extendedEnumeratedItem/{id}", method = RequestMethod.PUT)
	public ResponseEntity<OperationResponse> updateExtendedEnumeratedItem(@PathVariable("ctId") Long ctId, @PathVariable("ccId") Long ccId,
			@PathVariable("id") Long id, @RequestBody Map<String, String> request) {
		String extCodeId = StringUtils.trim(request.get("extCodeId"));
		String codedValue = StringUtils.trim(request.get("codedValue"));
		if (StringUtils.isAnyEmpty(extCodeId, codedValue)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("invalid extCodeId or codedValue");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		ExtendedEnumeratedItem cei = this.extendedEnumeratedItemRepository.findOne(id);
		if (cei == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("id is invalid");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.NOT_FOUND);
		}
		ExtendedEnumeratedItem existingCei = extendedEnumeratedItemRepository.findByCtIdAndCodeListIdAndCodeValue(ctId, ccId, codedValue);
		if (existingCei != null && existingCei.getStatus().equals(ObjectStatus.active) && !existingCei.getId().equals(cei.getId())) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Code already existed");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		cei.setExtCodeId(extCodeId);
		cei.setCodedValue(codedValue);
		cei.setUpdatedBy("admin");
		Utils.updatePODataLastModified(cei);
		return updateExtendedEnumerateItem(cei);
	}

	@RequestMapping(value = "/odm/v1/controlTerminology/{ctId}/codeList/{ccId}/extendedEnumeratedItem/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<OperationResponse> deleteExtendedEnumeratedItem(@PathVariable("ctId") Long ctId, @PathVariable("ccId") Long ccId,
			@PathVariable("id") Long id) {
		ExtendedEnumeratedItem cei = this.extendedEnumeratedItemRepository.findOne(id);
		if (cei == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("id is invalid");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.NOT_FOUND);
		}
		Utils.updatePOStatus(cei, ObjectStatus.deleted);
		cei.setUpdatedBy("admin");
		return updateExtendedEnumerateItem(cei);

	}

	@RequestMapping(value = "/odm/v1/controlTerminology/{ctId}/customizedCodeList/{ccId}/customizedEnumeratedItem/{id}", method = RequestMethod.PUT)
	public ResponseEntity<OperationResponse> updateCustomizedEnumeratedItem(@PathVariable("ctId") Long ctId, @PathVariable("ccId") Long ccId,
			@PathVariable("id") Long id, @RequestBody Map<String, String> request) {
		String extCodeId = StringUtils.trim(request.get("extCodeId"));
		String codedValue = StringUtils.trim(request.get("codedValue"));
		if (StringUtils.isAnyEmpty(extCodeId, codedValue)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("invalid extCodeId or codedValue");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}
		CustomizedEnumeratedItem cei = this.customizedEnumeratedItemRepository.findOne(id);
		if (cei == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("id is invalid");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.NOT_FOUND);
		}
		CustomizedEnumeratedItem existingCei = customizedEnumeratedItemRepository.findByCodeListIdAndCodeValue(ccId, codedValue);
		if (existingCei != null && existingCei.getStatus().equals(ObjectStatus.active) && !existingCei.getId().equals(cei.getId())) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Code already existed");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
		}

		cei.setExtCodeId(extCodeId);
		cei.setCodedValue(codedValue);
		cei.setUpdatedBy("admin");
		Utils.updatePODataLastModified(cei);
		return updateCustomizedEnumerateItem(cei);
	}

	@RequestMapping(value = "/odm/v1/controlTerminology/{ctId}/customizedCodeList/{ccId}/customizedEnumeratedItem/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<OperationResponse> deleteEnumeratedItem(@PathVariable("ctId") Long ctId, @PathVariable("ccId") Long ccId,
			@PathVariable("id") Long id) {
		CustomizedEnumeratedItem cei = this.customizedEnumeratedItemRepository.findOne(id);
		if (cei == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("id is invalid");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.NOT_FOUND);
		}
		Utils.updatePOStatus(cei, ObjectStatus.deleted);
		cei.setUpdatedBy("admin");
		return updateCustomizedEnumerateItem(cei);
	}

	private ResponseEntity<OperationResponse> updateExtendedEnumerateItem(ExtendedEnumeratedItem cei) {
		try {
			extendedEnumeratedItemRepository.save(cei);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(true);
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error during creating the ExtendedEnumeratedItem", e);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Error during saving the ExtendedEnumeratedItem");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ResponseEntity<OperationResponse> updateCustomizedEnumerateItem(CustomizedEnumeratedItem cei) {
		try {
			customizedEnumeratedItemRepository.save(cei);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(true);
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error during creating the CustomizedEnumeratedItem", e);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Error during saving the CustomizedEnumeratedItem");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Deprecated
	@RequestMapping(value = "/odm/v1/customizedEnumeratedItem", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> createEnumeratedItems(@RequestBody List<Map<String, String>> requests) {
		List<CustomizedEnumeratedItem> list = new ArrayList<CustomizedEnumeratedItem>();
		List<ExtendedEnumeratedItem> list2 = new ArrayList<ExtendedEnumeratedItem>();
		if (!CollectionUtils.isEmpty(requests)) {
			CustomizedCodeList customizedCodeList = null;
			CodeList codeList = null;
			ControlTerminology ct = null;
			for (Map<String, String> request : requests) {
				if (request == null) {
					continue;
				}
				String idStr = StringUtils.trim(request.get("id"));
				String deleted = StringUtils.trim(request.get("deleted"));
				String extCodeId = StringUtils.trim(request.get("extCodeId"));
				String extended = StringUtils.trim(request.get("extended"));
				String codedValue = StringUtils.trim(request.get("codedValue"));
				String codeListIdStr = StringUtils.trim(request.get("codeListId"));
				String ctIdStr = StringUtils.trim(request.get("ctId"));
				if (StringUtils.isAnyEmpty(extCodeId, codedValue)) {
					continue;
				}
				if (!StringUtils.isNumeric(codeListIdStr)) {
					OperationResponse or = new OperationResponse();
					OperationResult result = new OperationResult();
					result.setSuccess(false);
					result.setError("invalid codeListId");
					or.setResult(result);
					return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);

				} else {
					if (!StringUtils.isNumeric(codeListIdStr)) {
						OperationResponse or = new OperationResponse();
						OperationResult result = new OperationResult();
						result.setSuccess(false);
						result.setError("invalid codeListId");
						or.setResult(result);
						return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
					}
					if (Boolean.valueOf(extended)) {
						Long id = Long.valueOf(codeListIdStr);
						if (codeList == null) {
							codeList = codeListRepository.findOne(id);
							if (codeList == null) {
								OperationResponse or = new OperationResponse();
								OperationResult result = new OperationResult();
								result.setSuccess(false);
								result.setError("invalid codeListId");
								or.setResult(result);
								return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
							} else if (StringUtils.equalsIgnoreCase(codeList.getCodeListExtensible(), "No")) {
								OperationResponse or = new OperationResponse();
								OperationResult result = new OperationResult();
								result.setSuccess(false);
								result.setError("code list is not extensible");
								or.setResult(result);
								return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
							}
						}
						if (!StringUtils.isNumeric(ctIdStr)) {
							OperationResponse or = new OperationResponse();
							OperationResult result = new OperationResult();
							result.setSuccess(false);
							result.setError("invalid ctId");
							or.setResult(result);
							return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
						} else {
							if (ct == null) {
								ct = this.controlTerminologyRepository.findOne(Long.valueOf(ctIdStr));
							}
							if (ct == null) {
								OperationResponse or = new OperationResponse();
								OperationResult result = new OperationResult();
								result.setSuccess(false);
								result.setError("invalid ctId");
								or.setResult(result);
								return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
							}
						}

					} else {
						Long id = Long.valueOf(codeListIdStr);
						if (customizedCodeList == null) {
							customizedCodeList = customizedCodeListRepository.findOne(id);
							if (customizedCodeList == null) {
								OperationResponse or = new OperationResponse();
								OperationResult result = new OperationResult();
								result.setSuccess(false);
								result.setError("invalid codeListId");
								or.setResult(result);
								return new ResponseEntity<OperationResponse>(or, HttpStatus.BAD_REQUEST);
							}
						}
					}
				}
				if (Boolean.valueOf(extended)) {
					handleExtenedEnumerateItem(list2, codeList, ct, idStr, deleted, extCodeId, codedValue, codeListIdStr, ctIdStr);
				} else {
					handCustomizedEnumerateItem(list, customizedCodeList, idStr, deleted, extCodeId, codedValue, codeListIdStr);
				}
			}

		}
		try {
			customizedEnumeratedItemRepository.save(list);
			extendedEnumeratedItemRepository.save(list2);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(true);
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error during creating the CustomizedEnumeratedItem", e);
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Error during creating the CustomizedEnumeratedItem");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private void handleExtenedEnumerateItem(List<ExtendedEnumeratedItem> list2, CodeList codeList, ControlTerminology ct, String idStr, String deleted,
			String extCodeId, String codedValue, String codeListIdStr, String ctIdStr) {
		ExtendedEnumeratedItem eei;
		if (StringUtils.isNotBlank(idStr)) {
			eei = extendedEnumeratedItemRepository.findOne(Long.valueOf(idStr));
			if (eei != null) {
				if (StringUtils.isNotEmpty(deleted)) {
					eei.setExtCodeId(extCodeId);
					eei.setCodedValue(codedValue);
					eei.setStatus(ObjectStatus.deleted);
					eei.setCodeList(codeList);
					eei.setControlTerminology(ct);
					eei.setUpdatedBy("admin");
					Utils.updatePODataLastModified(eei);
				} else {
					eei.setExtCodeId(extCodeId);
					eei.setCodedValue(codedValue);
					eei.setCodeList(codeList);
					eei.setControlTerminology(ct);
					eei.setUpdatedBy("admin");
					Utils.updatePODataLastModified(eei);
				}
			}
		} else {
			eei = this.extendedEnumeratedItemRepository.findByCtIdAndCodeListIdAndCodeValue(Long.valueOf(ctIdStr), Long.valueOf(codeListIdStr), codedValue);
			if (eei != null) {
				eei.setExtCodeId(extCodeId);
				eei.setCodedValue(codedValue);
				eei.setCodeList(codeList);
				eei.setControlTerminology(ct);
				eei.setStatus(ObjectStatus.active);
				eei.setUpdatedBy("admin");
				Utils.updatePODataLastModified(eei);
			} else {
				eei = new ExtendedEnumeratedItem();
				eei.setExtCodeId(extCodeId);
				eei.setCodedValue(codedValue);
				eei.setCodeList(codeList);
				eei.setControlTerminology(ct);
				eei.setCreator("admin");
				eei.setUpdatedBy("admin");
				Utils.updatePODataLastModified(eei);
			}
		}
		list2.add(eei);
	}

	private void handCustomizedEnumerateItem(List<CustomizedEnumeratedItem> list, CustomizedCodeList codeList, String idStr, String deleted, String extCodeId,
			String codedValue, String codeListId) {
		CustomizedEnumeratedItem cei = new CustomizedEnumeratedItem();
		if (StringUtils.isNotBlank(idStr)) {
			cei = customizedEnumeratedItemRepository.findOne(Long.valueOf(idStr));
			if (StringUtils.isNotEmpty(deleted)) {
				cei.setExtCodeId(extCodeId);
				cei.setCodedValue(codedValue);
				cei.setStatus(ObjectStatus.deleted);
				cei.setCustomizedCodeList(codeList);
				cei.setUpdatedBy("admin");
			} else {
				cei.setExtCodeId(extCodeId);
				cei.setCodedValue(codedValue);
				cei.setCustomizedCodeList(codeList);
				cei.setUpdatedBy("admin");
			}
		} else {
			cei = customizedEnumeratedItemRepository.findByCodeListIdAndCodeValue(Long.valueOf(codeListId), codedValue);
			if (cei != null) {
				cei.setExtCodeId(extCodeId);
				cei.setCodedValue(codedValue);
				cei.setCustomizedCodeList(codeList);
				cei.setUpdatedBy("admin");
				Utils.updatePOStatus(cei, ObjectStatus.active);
			} else {
				cei = new CustomizedEnumeratedItem();
				cei.setExtCodeId(extCodeId);
				cei.setCodedValue(codedValue);
				cei.setCustomizedCodeList(codeList);
				cei.setCreator("admin");
				cei.setUpdatedBy("admin");
			}
		}
		list.add(cei);
	}

}
