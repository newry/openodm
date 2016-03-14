<%@ page contentType="application/json"%>
<%@ page import="org.codehaus.jackson.*"%>
<%@ page import="org.codehaus.jackson.map.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.nio.file.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="org.apache.commons.io.*"%>
<%
	String rootFolder = application.getRealPath("/WEB-INF/root");
	if("POST".equals(request.getMethod())){
		Map<String, Object> map = new HashMap<String, Object>();
		ObjectMapper om = new ObjectMapper();
		JsonNode node = om.reader().readTree(request.getInputStream());
		JsonNode params = node.get("params");
		String mode = params.get("mode").asText();
		if("list".equalsIgnoreCase(mode)){
			String path = params.get("path").asText();
			File folder = new File(rootFolder, path);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			map.put("result", list);
			File[] files = folder.listFiles();
			Map<String, String> versionMap = new HashMap<String, String>();
			if(files != null){
				for(File file: files){
					Map<String, Object> m = new HashMap<String, Object>();
					String fileName = file.getName();
					String version = "1.0";
					int i = fileName.lastIndexOf("$");
					if(fileName.endsWith("$working")){
						continue;
					}
					String newFileName = fileName;
					if(i>0){
						newFileName = fileName.substring(0, i);
						version = fileName.substring(i+1);
					}
					String existedVersion = versionMap.get(newFileName);
					if(existedVersion == null || version.compareTo(existedVersion) > 0){
						versionMap.put(newFileName, version);
					}
					m.put("name", newFileName);
					m.put("version", version);
					String status = "checkin";
					if(new File(folder, newFileName+"$working").exists()){
						status = "checkout";
					}
					m.put("status", status);
					if(file.isDirectory()){
						m.put("type", "dir");
					}else{
						m.put("type", "file");
					}
					String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(file.lastModified()));
					m.put("date", date);
					m.put("size", file.length());
					list.add(m);
				}
			}
			Iterator<Map<String, Object>> it = list.iterator();
			while(it.hasNext()){
				Map<String, Object> m = it.next();
				String name = (String)m.get("name");
				String version = (String)m.get("version");
				String existedVersion = versionMap.get(name);
				if(existedVersion!=null && !existedVersion.equals(version)){
					it.remove();
				}
			}
		}else if("rename".equalsIgnoreCase(mode)){
			String path = params.get("path").asText();
			String newPath = params.get("newPath").asText();
			File file = new File(rootFolder, path);
			File file2 = new File(rootFolder, newPath);
			Map<String, Object> m = new HashMap<String, Object>();
			map.put("result", m);
			if (!file2.exists()){
				boolean success = file.renameTo(file2);
				if (success) {
					m.put("success", Boolean.TRUE);
					m.put("error", null);
				}else{
					m.put("success", Boolean.FALSE);
					m.put("error", "unknown error");
				}
			}else{
				m.put("success", Boolean.FALSE);
				m.put("error", newPath + " already existed!");
			}
		}else if("copy".equalsIgnoreCase(mode)){
			String path = params.get("path").asText();
			String newPath = params.get("newPath").asText();
			Path file1 = Paths.get(rootFolder, path);
			Path file2 =  Paths.get(rootFolder, newPath);
			Map<String, Object> m = new HashMap<String, Object>();
			map.put("result", m);
			if (!file2.toFile().exists()){
				try{
					Files.copy(file1, file2, StandardCopyOption.COPY_ATTRIBUTES);
					m.put("success", Boolean.TRUE);
					m.put("error", null);
				}catch(IOException e){
					m.put("success", Boolean.FALSE);
					m.put("error", e.getClass().getSimpleName());
				}
			}else{
				m.put("success", Boolean.FALSE);
				m.put("error", newPath + " already existed!");
			}
		}else if("delete".equalsIgnoreCase(mode)){
			String path = params.get("path").asText();
			Path file = Paths.get(rootFolder, path);
			Map<String, Object> m = new HashMap<String, Object>();
			map.put("result", m);
			if (file.toFile().exists()){
				try{
					Files.delete(file);
					m.put("success", Boolean.TRUE);
					m.put("error", null);
				}catch(IOException e){
					m.put("success", Boolean.FALSE);
					m.put("error", e.getClass().getSimpleName());
				}
			}else{
				m.put("success", Boolean.FALSE);
				m.put("error", path + " didn't exist!");
			}
		}else if("savefile".equalsIgnoreCase(mode)){
			String path = params.get("path").asText();
			String content = params.get("content").asText();
			File file = new File(rootFolder, path);
			Map<String, Object> m = new HashMap<String, Object>();
			map.put("result", m);
			if (file.exists()){
				OutputStream output = null;
				try{
					output = new FileOutputStream(file);
					IOUtils.copy(new ByteArrayInputStream(content.getBytes("UTF-8")), output);
					m.put("success", Boolean.TRUE);
					m.put("error", null);
				}catch(IOException e){
					m.put("success", Boolean.FALSE);
					m.put("error", e.getClass().getSimpleName());
				}finally{
					IOUtils.closeQuietly(output);
				}
			}else{
				m.put("success", Boolean.FALSE);
				m.put("error", path + " didn't exist!");
			}
		}else if("editfile".equalsIgnoreCase(mode)){
			String path = params.get("path").asText();
			File file = new File(rootFolder, path);
			if (file.exists()){
				InputStream input = null;
				try{
					input = new FileInputStream(file);
					map.put("result", IOUtils.toString(input));
				}catch(IOException e){
				}finally{
					IOUtils.closeQuietly(input);
				}
			}
		}else if("addfolder".equalsIgnoreCase(mode)){
			String path = params.get("path").asText();
			String name = params.get("name").asText();
			File file = new File(rootFolder, path);
			File newFolder = new File(file, name);
			Map<String, Object> m = new HashMap<String, Object>();
			map.put("result", m);
			if (!newFolder.exists()){
				boolean success = newFolder.mkdir();
				if (success) {
					m.put("success", Boolean.TRUE);
					m.put("error", null);
				}else{
					m.put("success", Boolean.FALSE);
					m.put("error", "unknown error");
				}
			}else{
				m.put("success", Boolean.FALSE);
				m.put("error", path + " already existed!");
			}
		}else if("checkout".equalsIgnoreCase(mode)){
			String path = params.get("path").asText();
			File file = new File(rootFolder, path);
			Map<String, Object> m = new HashMap<String, Object>();
			map.put("result", m);
			if (file.exists()){
				File newFile = new File(rootFolder, path+"$working");
				if(newFile.exists()){
					m.put("success", Boolean.FALSE);
					m.put("error", path + " was already checked out!");
				}else{
					Path file1 = Paths.get(rootFolder, path);
					Path file2 =  Paths.get(rootFolder, path + "$working");
					Files.copy(file1, file2, StandardCopyOption.COPY_ATTRIBUTES);
					m.put("success", Boolean.TRUE);
					m.put("error", null);
				}
			}else{
				m.put("success", Boolean.FALSE);
				m.put("error", path + " didn't already exist!");
			}
		}else if("undoCheckout".equalsIgnoreCase(mode)){
			String path = params.get("path").asText();
			File file = new File(rootFolder, path);
			Map<String, Object> m = new HashMap<String, Object>();
			map.put("result", m);
			if (file.exists()){
				File newFile = new File(rootFolder, path+"$working");
				if(!newFile.exists()){
					m.put("success", Boolean.FALSE);
					m.put("error", path + " was not checked out!");
				}else{
					newFile.delete();
					m.put("success", Boolean.TRUE);
					m.put("error", null);
				}
			}else{
				m.put("success", Boolean.FALSE);
				m.put("error", path + " didn't already exist!");
			}
		}else if("getHistory".equalsIgnoreCase(mode)){
			String path = params.get("path").asText();
			File f = new File(rootFolder, path);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			map.put("result", list);
			File[] files = f.getParentFile().listFiles();
			String name = f.getName();
			if(files != null){
				for(File file: files){
					String fileName = file.getName();
					String version = "1.0";

					int i = fileName.lastIndexOf("$");
					String newFileName = fileName;
					if(i>0){
						newFileName = fileName.substring(0, i);
						version = fileName.substring(i+1);
					}
					if(newFileName.equals(name)){
						Map<String, Object> m = new HashMap<String, Object>();
						m.put("name", newFileName);
						m.put("version", version);
						list.add(m);
					}
				}
			}
		}
			
		om.writer().writeValue(out, map);
	}
	%>
