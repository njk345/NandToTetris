import java.io.File;

public class JackCompiler {
    public static void main(String[] args) {
        File source = new File(args[0]);
        CompilationEngine ce;
        File[] sourceFiles = source.isDirectory() ? source.listFiles(file -> file.getName().endsWith(".jack")) : new File[]{source};

        for (File inFile : sourceFiles) {
            File outFile = new File(inFile.getPath().substring(0, inFile.getPath().indexOf(".jack")) + ".vm");
            ce = new CompilationEngine(inFile, outFile);
            ce.compileClass();
            ce.close();
        }
    }
}
