package com.example.upload;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

@Controller
public class UploadController {

	private static Drive getDrive() throws Exception {
		
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream("example-files-app1-f17c69bdf918.json"))
				.createScoped(Collections.singleton(DriveScopes.DRIVE));
		return new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("example-files1").build();
	}

	@GetMapping("/{folderId:.+}")
	public String getHandler(@PathVariable String folderId, Model model) {
		model.addAttribute("folderId", folderId);
		return "uploadPage";
	}

	@PostMapping("/{folderId:.+}")
	public String postHandler(@PathVariable String folderId, @RequestParam("file") MultipartFile file, Model model) throws Exception {
		Drive drive = getDrive(); 
		String fileType = file.getContentType();
		String fileName = file.getOriginalFilename();
		byte[] fileContent = file.getBytes();
		AbstractInputStreamContent uploadFile = new ByteArrayContent(fileType, fileContent);
		File fileMeta = new File();
		fileMeta.setName(fileName);
		List<String> parents = Arrays.asList("1Rsu142I_6zgUtmRTKBWwyq_R0n43jhH4");
		fileMeta.setParents(parents);
		drive.files().create(fileMeta, uploadFile).execute();
		model.addAttribute("fileName", fileName);
	    return "resultPage";
	}

}

