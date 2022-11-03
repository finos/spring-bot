package org.finos.springbot.teams.state;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
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
import org.finos.springbot.teams.history.TeamsHistory;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.javatuples.Pair;

public class FileStateStorage extends AbstractStateStorage {

	final private static String DATA_FOLDER = "data";
	final private static String TAG_INDEX_FOLDER = "tag_index";
	final private static String FILE_EXT = ".txt";

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
		String addressable = getAddressable(file);
		String storageId = getStoragePath(file);
		try {
			Path path = checkAndCreateFolder(this.filePath + addressable);
			Path dataPath = checkAndCreateFolder(path.toString() + File.separator + DATA_FOLDER);
			Path tagPath = checkAndCreateFolder(path.toString() + File.separator + TAG_INDEX_FOLDER);

			createStorageFile(dataPath, storageId, data);

			createTagIndexFile(tags, storageId, tagPath);
		} catch (IOException e) {
			throw new TeamsException("Error while creating or getting folder " + e);
		}
	}

	@Override
	public Optional<Map<String, Object>> retrieve(String file) {
		String addressable = getAddressable(file);
		String storageId = getStoragePath(file);
		Optional<String> optData = readFile(
				this.filePath + addressable + File.separator + DATA_FOLDER + File.separator + storageId + FILE_EXT);

		if (optData.isPresent()) {
			return Optional.ofNullable(ejc.readValue(optData.get()));
		} else {
			return Optional.empty();
		}
	}

	private void createTagIndexFile(Map<String, String> tags, String storageId, Path tagPath) throws IOException {
		for (Entry<String, String> e : tags.entrySet()) {
			if (Objects.nonNull(e.getValue()) && e.getValue().equals(TeamsStateStorage.PRESENT)) {
				String tagName = getAzureTag(e.getKey());
				Path tagIndexPath = checkAndCreateFolder(tagPath + File.separator + tagName);
				checkAndCreateFile(tagIndexPath + File.separator + storageId + FILE_EXT);
			}
		}
	}

	private Optional<String> readFile(String filePath) {
		try {
			Path path = Paths.get(filePath);
			if (Files.exists(path)) {
				List<String> lines = Files.readAllLines(path);
				return Optional.of(String.join("", lines));
			} else {
				return Optional.empty();
			}
		} catch (IOException e1) {
			throw new TeamsException("Error while retrieve data " + e1);
		}
	}

	private void createStorageFile(Path dataPath, String storageId, Map<String, Object> data) throws IOException {
		Path storageFile = checkAndCreateFile(dataPath.toString() + File.separator + storageId + FILE_EXT);
		byte[] dataToByte = ejc.writeValue(data).getBytes();
		Files.write(storageFile, dataToByte);
	}

	private Path checkAndCreateFile(String file) throws IOException {
		Path path = Paths.get(file);
		if (Files.notExists(path)) {
			Files.createFile(path);
		}
		return path;
	}

	private Path checkAndCreateFolder(String pathStr) throws IOException {
		Path path = Paths.get(pathStr);
		if (Files.notExists(path)) {
			path = Files.createDirectory(path);
		}

		return path;
	}

	private String getAddressable(String file) {
		Optional<List<String>> split = splitString(file);
		if (split.isPresent()) {
			return getAzurePath(split.get().get(0));
		}
		return file;
	}

	private String getStoragePath(String file) {
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

	@Override
	public Iterable<Map<String, Object>> retrieve(List<Filter> tags, boolean singleResultOnly) {
		try {

			Map<String, Filter> filterMap = tags.stream().collect(Collectors.toMap(x -> x.key, x -> x));

			Filter addressFilter = null;
			if (filterMap.containsKey(TeamsStateStorage.ADDRESSABLE_KEY)) {
				addressFilter = filterMap.remove(TeamsStateStorage.ADDRESSABLE_KEY);

				String tagIndexFolder = getTagIndexFolder(filterMap, addressFilter);

				Path tagPath = Paths.get(tagIndexFolder);
				if (Files.isDirectory(tagPath)) {
					List<File> files = getDataFiles(addressFilter, filterMap, tagPath);
					if ((singleResultOnly) && (files.size() > 0)) {
						files = files.subList(0, 1);
					}

					Iterable<Map<String, Object>> list = files.stream()
							.map(f -> ejc.readValue(readFile(f.getAbsolutePath()).orElse("")))
							.collect(Collectors.toList());

					return list;
				}
			} else if (filterMap.containsKey(StateStorageBasedTeamsConversations.ADDRESSABLE_INFO)
					|| filterMap.containsKey(StateStorageBasedTeamsConversations.ADDRESSABLE_TYPE)) {
				Set<File> files = getAllDataFiles();
				List<Map<String, Object>> out = files.stream()
						.map(f -> ejc.readValue(readFile(f.getAbsolutePath()).orElse("")))
						.filter(filterAllDataFiles(filterMap)).collect(Collectors.toList());
				return out;
			}
		} catch (IOException e) {
			throw new TeamsException("Error while retrieve data " + e);
		}
		return Collections.emptyList();
	}

	private Predicate<? super EntityJson> filterAllDataFiles(Map<String, Filter> filterMap) {
		return e -> e.entrySet().stream().map(s -> {
			if (s.getValue() instanceof TeamsChannel && filterAddressableType(filterMap, "chat")) {
				return true;
			} else if (s.getValue() instanceof TeamsUser && filterAddressableType(filterMap, "user")) {
				return true;
			}
			return false;
		}).findAny().orElse(false);
	}

	private boolean filterAddressableType(Map<String, Filter> filterMap, String type) {

		if (filterMap.containsKey(StateStorageBasedTeamsConversations.ADDRESSABLE_TYPE)) {
			Filter filter = filterMap.get(StateStorageBasedTeamsConversations.ADDRESSABLE_TYPE);
			if (filter.value.equals(type))
				return true;
		} else {
			return true;
		}
		return false;

	}

	private Set<File> getAllDataFiles() {
		Set<File> fileList = new HashSet<>();
		getAllAddressableFiles(new File(this.filePath), fileList);
		return fileList;
	}

	private void getAllAddressableFiles(File node, Set<File> fileList) {
		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String fileName : subNote) {
				File dir = new File(node, fileName);
				if (dir.isDirectory()) {
					getAllAddressableFiles(dir, fileList);
				} else {
					if (dir.getParentFile().getName().equals(DATA_FOLDER)
							&& dir.getName().equals(TeamsStateStorage.ADDRESSABLE_KEY + FILE_EXT)) {
						fileList.add(dir);
					}
				}
			}
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<File> getDataFiles(Filter addressFilter, Map<String, Filter> filterMap, Path tagPath)
			throws IOException {
		List<File> l;

		try (Stream<Path> stream = Files.list(tagPath)) {
			Set<Path> paths = stream.filter(file -> !Files.isDirectory(file)).collect(Collectors.toSet());
			String address = getAzurePath(addressFilter.value);
			Map<Object, Long> files = paths.stream()
					.map(p -> Paths.get(this.filePath + File.separator + address + File.separator + DATA_FOLDER
							+ File.separator + p.getFileName().toString()))
					.map(f -> new File(f.toString())).filter(fileFilter(filterMap))
					.sorted(Collections.reverseOrder(Comparator.comparingLong(File::lastModified)))
					.collect(Collectors.toMap(k -> k, File::lastModified));
			l = new ArrayList(files.keySet());
			Collections.sort(l, Collections.reverseOrder(Comparator.comparingLong(File::lastModified)));
		}
		return l;
	}

	private Predicate<? super File> fileFilter(Map<String, Filter> filterMap) {
		return p -> filterMap.entrySet().stream().filter(f -> f.getKey().equals(TeamsHistory.TIMESTAMP_KEY)).map(e -> {

			if (e.getValue().operator.contains("==") && e.getValue().value.equals(String.valueOf(p.lastModified()))) {
				return true;
			} else if (e.getValue().operator.contains(">") && Long.valueOf(e.getValue().value) < p.lastModified()) {
				return true;
			} else if (e.getValue().operator.contains("<") && Long.valueOf(e.getValue().value) > p.lastModified()) {
				return true;
			}
			{
				return false;
			}
		}).findFirst().orElse(true);

	}

	private String getTagIndexFolder(Map<String, Filter> map, Filter addressFilter) {
		StringBuffer tagIndexFolder = new StringBuffer(
				this.filePath + File.separator + getAzurePath(addressFilter.value) + File.separator + TAG_INDEX_FOLDER);

		map.entrySet().stream().filter(m -> m.getValue().value.equals(TeamsStateStorage.PRESENT)).forEach((e) -> {
			tagIndexFolder.append(File.separator);
			tagIndexFolder.append(getAzureTag(e.getKey()));
		});
		return tagIndexFolder.toString();
	}

	private String getAzureTag(String s) {
		return s.replaceAll("[^0-9a-zA-Z]", "_");
	}

	private String getAzurePath(String s) {
		String path = s.replaceAll("[^0-9a-zA-Z/]", "_");
		if (path.contains("messageid")) {// remove the room conversation id
			return path.substring(0, path.indexOf("messageid"));
		}
		return path;
	}

}
