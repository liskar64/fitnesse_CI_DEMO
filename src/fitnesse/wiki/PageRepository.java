package fitnesse.wiki;

import fitnesse.wikitext.widgets.WikiWordWidget;
import util.DiskFileSystem;
import util.FileSystem;

import java.util.ArrayList;
import java.util.List;

public class PageRepository {
    private FileSystem fileSystem;

    public PageRepository() {
        fileSystem = new DiskFileSystem();
    }

    public PageRepository(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public WikiPage makeChildPage(String name, FileSystemPage parent) throws Exception {
      String path = parent.getFileSystemPath() + "/" + name;
      if (hasContentChild(path)) {
          return new FileSystemPage(name, parent, fileSystem);
      }
      else if (hasHtmlChild(path)) {
          return new ExternalSuitePage(path, name, parent, fileSystem);
      }
      else {
          return new FileSystemPage(name, parent, fileSystem);
      }
    }

    private Boolean hasContentChild(String path) {
        for (String child: fileSystem.list(path)) {
            if (child.equals("content.txt")) return true;
            if (child.length() == 11
                    && child.charAt(0) == 'c'
                    && child.charAt(1) == 'o'
                    && child.charAt(2) == 'n'
                    && child.charAt(3) == 't'
                    && child.charAt(4) == 'e'
                    && child.charAt(5) == 'n'
                    && child.charAt(6) == 't'
                    && child.charAt(7) == '.'
                    && child.charAt(8) == 't'
                    && child.charAt(9) == 'x'
                    && child.charAt(10) == 't'

                    ) return true;
        }
        return false;
    }

    private Boolean hasHtmlChild(String path) {
        if (path.endsWith(".html")) return true;
        for (String child: fileSystem.list(path)) {
            if (hasHtmlChild(path + "/" + child)) return true;
        }
        return false;
    }

    public List<WikiPage> findChildren(ExternalSuitePage parent) throws Exception {
        List<WikiPage> children = new ArrayList<WikiPage>();
        for (String child: fileSystem.list(parent.getFileSystemPath())) {
            String childPath = parent.getFileSystemPath() + "/" + child;
            if (child.endsWith(".html")) {
                children.add(new ExternalTestPage(childPath,
                        WikiWordWidget.makeWikiWord(child.replace(".html", "")), parent, fileSystem));
            }
            else if (hasHtmlChild(childPath)) {
                children.add(new ExternalSuitePage(childPath,
                        WikiWordWidget.makeWikiWord(child), parent, fileSystem));
            }
        }
        return children;
    }
}
