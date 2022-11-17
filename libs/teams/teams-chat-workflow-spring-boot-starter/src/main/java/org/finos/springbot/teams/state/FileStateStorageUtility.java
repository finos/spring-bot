package org.finos.springbot.teams.state;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.finos.springbot.teams.TeamsException;

public class FileStateStorageUtility {

	public static Set<File> getAllDataFiles(String filePath) {
		Set<File> fileList = new HashSet<>();
		getAllAddressableFiles(new File(filePath), fileList);
		return fileList;
	}

	private static void getAllAddressableFiles(File node, Set<File> fileList) {
		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String fileName : subNote) {
				File dir = new File(node, fileName);
				if (dir.isDirectory()) {
					getAllAddressableFiles(dir, fileList);
				} else {
					if (dir.getParentFile().getName().equals(FileStateStorage.DATA_FOLDER)
							&& dir.getName().equals(TeamsStateStorage.ADDRESSABLE_KEY + FileStateStorage.FILE_EXT)) {
						fileList.add(dir);
					}
				}
			}
		}

	}

	public static Optional<String> readFile(String filePath) {
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

	public static Path checkAndCreateFile(String file) throws IOException {
		Path path = Paths.get(file);
		if (Files.notExists(path)) {
			Files.createFile(path);
		}
		return path;
	}

	public static Path checkAndCreateFolder(String pathStr) throws IOException {
		Path path = Paths.get(pathStr);
		if (Files.notExists(path)) {
			path = Files.createDirectory(path);
		}

		return path;
	}

}
