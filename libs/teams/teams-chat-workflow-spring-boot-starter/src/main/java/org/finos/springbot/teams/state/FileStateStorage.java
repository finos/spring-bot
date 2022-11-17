package org.finos.springbot.teams.state;

import static org.finos.springbot.teams.state.FileStateStorageUtility.checkAndCreateFile;
import static org.finos.springbot.teams.state.FileStateStorageUtility.checkAndCreateFolder;
import static org.finos.springbot.teams.state.FileStateStorageUtility.readFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.teams.TeamsException;
import org.finos.springbot.teams.content.TeamsChannel;
import org.finos.springbot.teams.content.TeamsUser;
import org.finos.springbot.teams.conversations.StateStorageBasedTeamsConversations;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.javatuples.Pair;

public class FileStateStorage extends AbstractStateStorage {

	final static String DATA_FOLDER = "data";
	final static String TAG_INDEX_FOLDER = "tag_index";
	final static String FILE_EXT = ".txt";

	protected Map<String, String> store = new HashMap<>();
	protected Map<String, List<Pair<String, String>>> tagIndex = new HashMap<>();

	private EntityJsonConverter ejc;
	private String filePath;

	public FileStateStorage(EntityJsonConverter ejc, String filePath) {
		super();
		this.ejc = ejc;
		this.filePath = filePath;
	}

	@Override
	public void store(String file, Map<String, String> tags, Map<String, Object> data) {
		if ((tags != null) && (tags.size() > 0)) {
			String addressable = getAddressable(file);
			String storageId = getStorage(file);
			try {
				Path path = checkAndCreateFolder(this.filePath + addressable);
				Path dataPath = checkAndCreateFolder(path.toString() + File.separator + DATA_FOLDER);
				Path tagPath = checkAndCreateFolder(path.toString() + File.separator + TAG_INDEX_FOLDER);

				createStorageFile(dataPath, storageId, data);

				createTagIndexFile(tags, storageId, tagPath);
			} catch (IOException e) {
				throw new TeamsException("Error while creating or getting folder " + e);
			}
		} else {
			throw new TeamsException("Cannot persist data to " + file + " - no tags");

		}
	}

	private void createStorageFile(Path dataPath, String storageId, Map<String, Object> data) throws IOException {
		Path storageFile = checkAndCreateFile(dataPath.toString() + File.separator + storageId + FILE_EXT);
		byte[] dataToByte = ejc.writeValue(data).getBytes();
		Files.write(storageFile, dataToByte);
	}

	/**
	 * create tag index file only if tags map contain tag key
	 * 
	 * @param tags
	 * @param storageId
	 * @param tagPath
	 * @throws IOException
	 */
	private void createTagIndexFile(Map<String, String> tags, String storageId, Path tagPath) throws IOException {
		for (Entry<String, String> e : tags.entrySet()) {
			String tagName = getAzureTag(e.getKey());
			Path tagIndexPath = checkAndCreateFolder(tagPath + File.separator + tagName);
			tagIndexPath = checkAndCreateFolder(tagIndexPath + File.separator + e.getValue());
			checkAndCreateFile(tagIndexPath + File.separator + storageId + FILE_EXT);
		}
	}

	@Override
	public Optional<Map<String, Object>> retrieve(String file) {
		String addressable = getAddressable(file);
		String storageId = getStorage(file);

		Optional<String> optData = readFile(
				this.filePath + addressable + File.separator + DATA_FOLDER + File.separator + storageId + FILE_EXT);

		if (optData.isPresent()) {
			return Optional.ofNullable(ejc.readValue(optData.get()));
		} else {
			return Optional.empty();
		}
	}

	@Override
	public Iterable<Map<String, Object>> retrieve(List<Filter> tags, boolean singleResultOnly) {

		List<File> tagFolders = getAllTagIndex(tags);// get all tag_index files

		List<File> files = extractDataFileName(tagFolders);

		Optional<Filter> ftOpt = tags.stream()
				.filter(t -> t.key.equals(StateStorageBasedTeamsConversations.ADDRESSABLE_INFO)
						|| t.key.equals(StateStorageBasedTeamsConversations.ADDRESSABLE_TYPE))
				.findAny();

		// if search for addressable-info like room information or user information
		if (ftOpt.isPresent()) {

			List<Map<String, Object>> out = files.stream()
					.map(f -> ejc.readValue(readFile(f.getAbsolutePath()).orElse("")))
					.filter(filterAddressableTypeFiles()).collect(Collectors.toList());

			if ((singleResultOnly) && (files.size() > 0)) {
				out = out.subList(0, 1);
			}
			return out;
		} else {
			if ((singleResultOnly) && (files.size() > 0)) {
				//files = files.stream().limit(1).collect(Collectors.toSet());
				files = files.subList(0, 1);
			}
			return files.stream().map(f -> ejc.readValue(readFile(f.getAbsolutePath()).orElse("")))
					.collect(Collectors.toList());
		}
	}

	private List<File> extractDataFileName(List<File> tagFolders) {
		return tagFolders.stream().map(t -> getDataFiles(t)).flatMap(f -> f.stream()).distinct().collect(Collectors.toList());
//		return list
//		        .stream()
//		        .distinct()
//		        .collect(Collectors.toList());
	}

	private List<File> getAllTagIndex(List<Filter> filter) {
		List<File> fileList = new ArrayList<>();
		getAllTagIndex(new File(filePath), fileList, filter);
		return fileList;
	}

	/**
	 * Get all filtered tag files base on given tags
	 * 
	 * @param node
	 * @param fileList
	 * @param tags
	 */
	private void getAllTagIndex(File node, List<File> fileList, List<Filter> tags) {
		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String fileName : subNote) {
				File dir = new File(node, fileName);
				if (dir.getParentFile().getName().equals(FileStateStorage.TAG_INDEX_FOLDER)) {
					List<File> file = filteredTagsFiles(tags, dir);
					if (file != null) {
						fileList.addAll(file);
					}

				} else if (dir.isDirectory()) {
					getAllTagIndex(dir, fileList, tags);
				}

			}
		}

	}

	/**
	 * return tag_index filed base on tags
	 * 
	 * @param tags
	 * @param f
	 * @return
	 */
	private Optional<Filter> matchEntity(List<Filter> tags, File f) {
		return tags.stream().filter(t -> getAzureTag(t.key).equals(f.getName())).findAny();

	}

	/**
	 * Get filtered tags file for given tags
	 * 
	 * @param tags
	 * @param tagFolder
	 * @return
	 */
	private List<File> filteredTagsFiles(List<Filter> tags, File tagFolder) {
		Optional<Filter> ftOpt = matchEntity(tags, tagFolder);
		List<String> subNote = Arrays.asList(tagFolder.list());

		if (ftOpt.isPresent()) {
			for (String n : subNote) {
				List<String> subSubNote = Arrays.asList(n);
				List<String> list = checkEntity(ftOpt.get(), subSubNote);
				return list.stream().map(l -> new File(tagFolder, l)).collect(Collectors.toList());
			}
		}
		return null;
	}

	private List<String> checkEntity(Filter f, List<String> subSubNote) {
		return subSubNote.stream().filter(s -> {
			int cmp = f.value.compareTo(s);

			if (f.operator.contains("=") && (cmp == 0)) {
				return true;
			}
			if (f.operator.contains(">") && (cmp < 0)) {
				return true;
			}
			if (f.operator.contains("<") && (cmp > 0)) {
				return true;
			}
			return false;
		}).collect(Collectors.toList());
	}

	private Predicate<? super EntityJson> filterAddressableTypeFiles() {
		return e -> e.entrySet().stream().map(s -> {
			if (s.getValue() instanceof TeamsChannel) {
				return true;
			} else if (s.getValue() instanceof TeamsUser) {
				return true;
			}
			return false;
		}).findAny().orElse(false);
	}

	/**
	 * Get data files from tag index Filter files if timestamp tag is provided
	 * 
	 * @param addressableId
	 * @param filterMap
	 * @param tagPath
	 * @return
	 */

	private List<File> getDataFiles(File tagPath) {

		String addressableId = tagPath.getParentFile().getParentFile().getParentFile().getName();
		try (Stream<Path> stream = Files.list(tagPath.toPath())) {

			Set<Path> paths = stream.filter(file -> !Files.isDirectory(file)).collect(Collectors.toSet());

			List<File> files = paths.stream().map(f -> new File(f.toString()))
					.sorted(Collections.reverseOrder(Comparator.comparingLong(File::lastModified)))
					.collect(Collectors.toList());

			return files.stream().map(f -> new File(this.filePath + File.separator + addressableId + File.separator
					+ DATA_FOLDER + File.separator + f.getName())).collect(Collectors.toList());
		} catch (IOException e) {
			throw new TeamsException("Error while retriving data files " + e);
		}
	}

	private String getAddressable(String file) {
		Optional<List<String>> split = splitString(file);
		if (split.isPresent()) {
			return getAzurePath(split.get().get(0));
		}
		return file;
	}

	private String getStorage(String file) {
		Optional<List<String>> split = splitString(file);
		if (split.isPresent()) {
			return split.get().get(1);
		}
		return file;
	}

	private Optional<List<String>> splitString(String s) {
		if (s.contains("/")) {
			String[] data = s.split("/");
			if (data.length > 1) {
				return Optional.of(Arrays.asList(data));
			}
		}
		return Optional.empty();
	}

	private String getAzureTag(String s) {
		return s.replaceAll("[^0-9a-zA-Z]", "_");
	}

	/**
	 * Get file path, replaced all special character with _ and removed conversation
	 * id
	 * 
	 * @param path
	 * @return
	 */
	private String getAzurePath(String s) {
		String path = s.replaceAll("[^0-9a-zA-Z/]", "_");
		if (path.contains("_messageid")) {// remove the room conversation id
			return path.substring(0, path.indexOf("_messageid"));
		}
		return path;
	}

}
