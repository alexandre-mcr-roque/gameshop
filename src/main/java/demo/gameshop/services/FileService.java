package demo.gameshop.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Service for managing file storage and retrieval using MongoDB GridFS.
 * 
 * <p>This service provides methods to upload files, retrieve them by ID or filename,
 * and obtain an {@link InputStream} for reading stored file data. 
 * The files are stored in MongoDB using {@link GridFsTemplate} and file data is obtained through {@link GridFsOperations}.</p>
 * 
 * <p><b>Example usage:</b></p>
 * 
 * <pre>{@code
 * // Upload a file
 * String fileId = fileService.addFile(uploadedFile, "test-game.jpg");
 * 
 * // Retrieve a file by ID
 * GridFSFile gridFSFile = fileService.findById(fileId);
 * if (gridFSFile != null) {
 *     String fileName = gridFSFile.getFilename();
 *     String contentType = gridFSFile.getMetadata().get("_contentType").toString();
 *     long fileSize = gridFSFile.getMetadata().get("fileSize");
 * }
 * 
 * // Retrieve by file name
 * GridFSFile fileByName = fileService.findByFileName("test-game.jpg");
 * 
 * // Get InputStream for file contents
 * InputStream stream = fileService.getInputStream(gridFSFile);
 * }</pre>
 */
@Service
@RequiredArgsConstructor
public class FileService {
	
	private final GridFsTemplate template;
	private final GridFsOperations operations;
	
	/**
	 * Uploads a file to MongoDB GridFS.
	 *
	 * <p>This method stores the file’s input stream, along with its content type and metadata
	 * (such as file size), into the GridFS storage. The file name is also stored as a reference
	 * and can be used to look up the file later.</p>
	 *
	 * @param upload   The {@link MultipartFile} to be uploaded
	 * @param fileName The name under which the file should be stored in GridFS
	 * @return The generated file ID as a {@link String}
	 * @throws IOException if an I/O error occurs while reading the file input stream
	 */
	public String addFile(MultipartFile upload, String fileName) throws IOException {
        DBObject metadata = new BasicDBObject();
        metadata.put("fileSize", upload.getSize());
        ObjectId fileID = template.store(upload.getInputStream(), fileName, upload.getContentType(), metadata);

        return fileID.toHexString();
	}
	
	/**
	 * Deletes a file from GridFS using its unique identifier.
	 * 
	 * <p>If no file with the given ID exists, nothing happens.</p>
	 * @param id The file ID to delete
	 */
	public void deleteFile(String id) {
		template.delete(Query.query(Criteria.where("_id").is(id)));
	}
	
	/**
	 * Retrieves a file from GridFS using its unique identifier.
	 *
	 * <p>If a file with the given ID exists, this method returns an {@link Optional}
	 * containing its corresponding {@link GridFSFile} object. The metadata (such as
	 * content type and size) can be accessed from this object.</p>
	 *
	 * <p><b>Example usage:</b></p>
	 * <pre>{@code
	 * Optional<GridFSFile> gridFSFileOpt = fileService.findById("652d1f7e8b1a9f0001a2c345");
	 * gridFSFileOpt.ifPresent(gridFSFile -> {
	 *     String fileName = gridFSFile.getFilename();
	 *     String contentType = gridFSFile.getMetadata().get("_contentType").toString();
	 *     long fileSize = gridFSFile.getMetadata().get("fileSize");
	 * });
	 * }</pre>
	 *
	 * @param id The file ID stored in GridFS
	 * @return An {@link Optional} containing the {@link GridFSFile} if found, or an empty {@code Optional} if not
	 * @throws IOException if an error occurs during retrieval
	 */
	public Optional<GridFSFile> findById(String id) throws IOException {
        GridFSFile gridFSFile = template.findOne(Query.query(Criteria.where("_id").is(id)));
        return gridFSFile != null && gridFSFile.getMetadata() != null
        		? Optional.of(gridFSFile)
        		: Optional.empty();
    }
	
	/**
	 * Retrieves a file from GridFS using its stored file name.
	 *
	 * <p>If a file with the specified name exists, this method returns an {@link Optional}
	 * containing its {@link GridFSFile} object. The metadata (such as
	 * content type and size) can be accessed from this object.</p>
	 *
	 * <p><b>Example usage:</b></p>
	 * <pre>{@code
	 * Optional<GridFSFile> gridFSFileOpt = fileService.findByFileName("game-cover.jpg");
	 * gridFSFileOpt.ifPresent(gridFSFile -> {
	 *     String fileName = gridFSFile.getFilename();
	 *     String contentType = gridFSFile.getMetadata().get("_contentType").toString();
	 *     long fileSize = gridFSFile.getMetadata().get("fileSize");
	 * });
	 * }</pre>
	 *
	 * @param fileName The name of the file stored in GridFS
	 * @return An {@link Optional} containing the {@link GridFSFile} if found, or an empty {@code Optional} if not
	 * @throws IOException if an error occurs during retrieval
	 */
	public Optional<GridFSFile> findByFileName(String fileName) throws IOException {
        GridFSFile gridFSFile = template.findOne(new Query(Criteria.where("filename").is(fileName)));
        return gridFSFile != null && gridFSFile.getMetadata() != null
        		? Optional.of(gridFSFile)
        		: Optional.empty();
    }
	
	/**
	 * Retrieves an {@link InputStream} for reading the contents of a stored file.
	 *
	 * <p>This method provides direct access to the binary data of a {@link GridFSFile}
	 * retrieved from the database. It can be used for file download or content processing.</p>
	 *
	 * @param gridFSFile A non-null {@link GridFSFile} representing the stored file
	 * @return An {@link InputStream} for reading the file’s content
	 * @throws IOException if the content stream could not be opened
	 * @throws IllegalStateException if the underlying stream is atttempted to be read multiple times
	 */

	public InputStream getInputStream(@NonNull GridFSFile gridFSFile) throws IOException, IllegalStateException{
		return operations.getResource(gridFSFile).getInputStream();
	}
}
