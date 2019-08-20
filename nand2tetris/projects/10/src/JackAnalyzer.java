import java.io.File;

public class JackAnalyzer {
    public static void main(String[] args) {
        File source = new File(args[0]);
        CompilationEngine ce;
        File[] sourceFiles = source.isDirectory() ? source.listFiles(file -> file.getName().endsWith(".jack")) : new File[]{source};

        for (File sf : sourceFiles) {
            File outFile = new File(sf.getPath().substring(0, sf.getPath().indexOf(".jack")) + ".xml");
            ce = new CompilationEngine(sf, outFile);
            ce.compileClass();
            ce.close();
        }
    }
}
