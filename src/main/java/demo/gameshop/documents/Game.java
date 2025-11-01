package demo.gameshop.documents;

import static lombok.AccessLevel.NONE;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document
@Getter
@Setter
public class Game {

	@Id
	@Setter(NONE)
	private String id;

	/**
	 * This field is set on constructor
	 */
	@Indexed(unique = true)
	@Setter(NONE)
	private String titleNormalized;
	
	private String title;
	
	private String genre;
	
	private String imageUrl;
	
	/** @deprecated Repository requires constructor with no arguments to work. */
	@SuppressWarnings("unused")
	private Game()
	{}
	
	public Game(String title) {
		this.title = title;
		this.titleNormalized = normalizeTitle(title);
	}

	private static final Pattern AND_PATTERN = Pattern.compile("&+");
	private static final Pattern PLUS_PATTERN = Pattern.compile("\\++");
	private static final Pattern REMOVE_PATTERN = Pattern.compile("[^\\w\\s-_']+");
	private static final Pattern SPACE_PATTERN = Pattern.compile("[\\s-_']+");
	private static final Pattern TRIM_PATTERN = Pattern.compile("(?:^-+|-+$)+");
	public static String normalizeTitle(String name) {
		String result = StringUtils.stripAccents(name).toLowerCase();
		result = AND_PATTERN.matcher(result).replaceAll(" and ");
		result = PLUS_PATTERN.matcher(result).replaceAll(" plus ");
		result = REMOVE_PATTERN.matcher(result).replaceAll("");
		result = SPACE_PATTERN.matcher(result).replaceAll("-");
		result = TRIM_PATTERN.matcher(result).replaceAll("");
		return result;
	}
}