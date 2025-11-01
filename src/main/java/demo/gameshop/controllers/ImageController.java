package demo.gameshop.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.client.gridfs.model.GridFSFile;

import demo.gameshop.services.FileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {
	
	private final FileService fileService;
	
	@GetMapping("/{id}")
	public void getImage(
			@PathVariable String id,
			@RequestParam(required=false, defaultValue="false") boolean download,
			HttpServletResponse response) {
		try {
			Optional<GridFSFile> fileOptional = fileService.findById(id);
			if (fileOptional.isPresent()) {
				GridFSFile file = fileOptional.get();
				String contentType = file.getMetadata().getString("_contentType");
				// Send only image files within this controller
				if (!contentType.startsWith("image/")) {
					response.setStatus(HttpStatus.NOT_FOUND.value());
					return;
				}
				response.setStatus(HttpStatus.OK.value());
				response.setContentType(contentType);
				if (download) {
					response.setHeader("Content-Disposition", "attachment; filename="+file.getFilename().replace(" ", "_"));
				}
				try (InputStream inputStream = fileService.getInputStream(file)) {
					IOUtils.copyLarge(inputStream, response.getOutputStream());
				}
			}
		} catch (IOException | IllegalStateException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			e.printStackTrace();
		}
		response.setStatus(HttpStatus.NOT_FOUND.value());
	}
}
