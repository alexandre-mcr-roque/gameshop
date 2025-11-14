package demo.gameshop.controllers;

import com.mongodb.client.gridfs.model.GridFSFile;
import demo.gameshop.services.FileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

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
                assert file.getMetadata() != null;
                String contentType = file.getMetadata().getString("_contentType");
				// Send only image files within this controller
				if (!contentType.startsWith("image/")) {
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
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
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		}
		response.setStatus(HttpStatus.NOT_FOUND.value());
	}
}
