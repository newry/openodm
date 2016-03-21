package com.openodm.impl.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.openodm.impl.controller.response.OperationResponse;
import com.openodm.impl.controller.response.OperationResult;
import com.openodm.impl.entity.AbstractEnumeratedItem;
import com.openodm.impl.entity.ControlTerminology;
import com.openodm.impl.entity.CustomizedCodeList;
import com.openodm.impl.entity.CustomizedEnumeratedItem;
import com.openodm.impl.entity.ObjectStatus;
import com.openodm.impl.repository.CodeListRepository;
import com.openodm.impl.repository.ControlTerminologyRepository;
import com.openodm.impl.repository.CustomizedCodeListRepository;
import com.openodm.impl.repository.CustomizedEnumeratedItemRepository;
import com.openodm.impl.repository.ExtendedEnumeratedItemRepository;

@RestController
public class CustomizedCodeListController {
	private static final Logger LOG = LoggerFactory
			.getLogger(CustomizedCodeListController.class);
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

	@RequestMapping(value = "/odm/v1/customizedCodeList", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> createCustomizedCodeList(
			@RequestBody Map<String, String> request) {
		String name = StringUtils.trim(request.get("name"));
		String desc = StringUtils.trim(request.get("description"));
		String submissionValue = StringUtils.trim(request
				.get("submissionValue"));
		String ctId = StringUtils.trim(request.get("ctId"));
		ControlTerminology ct = null;
		CustomizedCodeList ccl = new CustomizedCodeList();
		if (StringUtils.isEmpty(name)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Name is required");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or,
					HttpStatus.BAD_REQUEST);
		} else {
			List<CustomizedCodeList> list = customizedCodeListRepository
					.findByName(name);
			if (!CollectionUtils.isEmpty(list)) {
				OperationResponse or = new OperationResponse();
				OperationResult result = new OperationResult();
				result.setSuccess(false);
				result.setError("Code with same name existed");
				or.setResult(result);
				return new ResponseEntity<OperationResponse>(or,
						HttpStatus.BAD_REQUEST);
			}
		}

		if (StringUtils.isEmpty(ctId) || !StringUtils.isNumeric(ctId)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Invalid ctId");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or,
					HttpStatus.BAD_REQUEST);

		} else {
			ct = controlTerminologyRepository.findOne(Long.valueOf(ctId));
			if (ct == null) {
				OperationResponse or = new OperationResponse();
				OperationResult result = new OperationResult();
				result.setSuccess(false);
				result.setError("Invalid ctId");
				or.setResult(result);
				return new ResponseEntity<OperationResponse>(or,
						HttpStatus.BAD_REQUEST);
			}
		}
		try {
			ccl.setName(name);
			ccl.setDescription(desc);
			ccl.setCDISCSubmissionValue(submissionValue);
			ccl.setCreator("admin");
			ccl.setUpdatedBy("admin");
			customizedCodeListRepository.save(ccl);
			ccl.setExtCodeId("X"
					+ new DecimalFormat("000000").format(ccl.getId()));
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
			return new ResponseEntity<OperationResponse>(or,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/odm/v1/customizedCodeList/{id}", method = RequestMethod.PUT)
	public ResponseEntity<OperationResponse> updateCustomizedCodeList(
			@PathVariable("id") Long id,
			@RequestBody Map<String, String> request) {
		String name = StringUtils.trim(request.get("name"));
		String desc = StringUtils.trim(request.get("description"));
		String submissionValue = StringUtils.trim(request
				.get("submissionValue"));
		CustomizedCodeList ccl = null;
		if (StringUtils.isEmpty(name)) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("Name is required");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or,
					HttpStatus.BAD_REQUEST);
		}
		ccl = this.customizedCodeListRepository.findOne(id);
		if (ccl == null) {
			OperationResponse or = new OperationResponse();
			OperationResult result = new OperationResult();
			result.setSuccess(false);
			result.setError("id is invalid");
			or.setResult(result);
			return new ResponseEntity<OperationResponse>(or,
					HttpStatus.NOT_FOUND);
		}

		try {
			ccl.setName(name);
			ccl.setDescription(desc);
			ccl.setCDISCSubmissionValue(submissionValue);
			ccl.setUpdatedBy("admin");
			ccl.setDateLastModified(Calendar.getInstance(TimeZone
					.getTimeZone("UTC")));
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
			return new ResponseEntity<OperationResponse>(or,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/odm/v1/customizedEnumeratedItem", method = RequestMethod.GET)
	public List<? extends AbstractEnumeratedItem> listEnumeratedItem(
			@RequestParam("codeListId") Long codeListId) {
		List<CustomizedEnumeratedItem> list = customizedEnumeratedItemRepository
				.findByCodeListId(codeListId);
		return list;
	}

	@RequestMapping(value = "/odm/v1/customizedEnumeratedItem", method = RequestMethod.POST)
	public ResponseEntity<OperationResponse> createEnumeratedItem(
			@RequestBody List<Map<String, String>> requests) {
		List<CustomizedEnumeratedItem> list = new ArrayList<CustomizedEnumeratedItem>();
		if (!CollectionUtils.isEmpty(requests)) {
			CustomizedCodeList codeList = null;
			for (Map<String, String> request : requests) {
				if (request == null) {
					continue;
				}
				String deleted = StringUtils.trim(request.get("deleted"));
				String idStr = StringUtils.trim(request.get("id"));
				String extCodeId = StringUtils.trim(request.get("extCodeId"));
				String codedValue = StringUtils.trim(request.get("codedValue"));
				String codeListId = StringUtils.trim(request.get("codeListId"));
				if (StringUtils.isAnyEmpty(extCodeId, codedValue)) {
					continue;
				}
				if (!StringUtils.isNumeric(codeListId)) {
					OperationResponse or = new OperationResponse();
					OperationResult result = new OperationResult();
					result.setSuccess(false);
					result.setError("invalid codeListId");
					or.setResult(result);
					return new ResponseEntity<OperationResponse>(or,
							HttpStatus.BAD_REQUEST);

				} else {
					Long id = Long.valueOf(codeListId);
					if (codeList == null) {
						codeList = customizedCodeListRepository.findOne(id);
						if (codeList == null) {
							OperationResponse or = new OperationResponse();
							OperationResult result = new OperationResult();
							result.setSuccess(false);
							result.setError("invalid codeListId");
							or.setResult(result);
							return new ResponseEntity<OperationResponse>(or,
									HttpStatus.BAD_REQUEST);
						}
					}

				}
				CustomizedEnumeratedItem cei = new CustomizedEnumeratedItem();
				if (StringUtils.isNotBlank(idStr)) {
					cei = customizedEnumeratedItemRepository.findOne(Long
							.valueOf(idStr));
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
					List<CustomizedEnumeratedItem> ceis = customizedEnumeratedItemRepository
							.findByCodeListIdAndCodeValue(
									Long.valueOf(codeListId), codedValue);
					if (!CollectionUtils.isEmpty(ceis)) {
						cei = ceis.get(0);
						cei.setExtCodeId(extCodeId);
						cei.setCodedValue(codedValue);
						cei.setCustomizedCodeList(codeList);
						cei.setStatus(ObjectStatus.active);
						cei.setUpdatedBy("admin");
						cei.setDateLastModified(Calendar.getInstance(TimeZone
								.getTimeZone("UTC")));
					} else {
						cei.setExtCodeId(extCodeId);
						cei.setCodedValue(codedValue);
						cei.setCustomizedCodeList(codeList);
						cei.setCreator("admin");
						cei.setUpdatedBy("admin");
						cei.setDateLastModified(Calendar.getInstance(TimeZone
								.getTimeZone("UTC")));
					}
				}
				list.add(cei);
			}

		}
		try {
			customizedEnumeratedItemRepository.save(list);
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
			return new ResponseEntity<OperationResponse>(or,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
