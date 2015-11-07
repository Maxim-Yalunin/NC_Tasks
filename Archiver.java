package ru.ncedu.yalunin.archiver;
/**
 * There are six modes:
 * 1. To create zip file firstly enter key-word "zip", after that enter  name of
 *      new Zip-file and names of all files you need 
 * 2. To unzip archive enter "unzip" and name of Zip-file. You can enter a path
 *      where it will be unzipped 
 * 3. To create archive with comment enter: "zip+comment", name of new Zip-file,
 *      names of files and finally comment
 * 4. To add new files to archive  enter: "add", name of Zip-file and names of
 *      files you need
 * 5. To write comment enter: "comment-w", name of Zip-file and comment
 * 6. To read comment enter: "comment-r" and name of Zip-file 
 * 
 * @author Maxim Yalunin
 */
import java.io.*;
import java.util.zip.*;
import java.util.*;

public class Archiver {
    public static void main(String[] args) {
        //String[] myArgs = {"unzip","different.zip"};
        //String[] myArgs = {"zip","differentNEW.zip", "!fake\\!faketext.txt","Modals past deduction.htm",
        //    "Links to active vocabulary on Physics.docx", "!fake\\!fakeword.docx", "LSP_exercises.docx"};
        //String[] myArgs = {"add","different.zip", "20thCenturyFox.pptx", "!textForZip.txt"};
        String[] myArgs01 = {"zip+comment","MySpecZipAndComment.zip", "!specFolder",  "20thCenturyFox.pptx", "It's an example of using 'zip+comment'"};        
        runArchiver(myArgs01);
        String[] myArgs02 = {"comment-r","MySpecZipAndComment.zip"};
        runArchiver(myArgs02);
        
        String[] myArgs1 = {"zip","MySpecZip.zip", "!specFolder",  "20thCenturyFox.pptx"};        
        runArchiver(myArgs1);
        String[] myArgs2 = {"add", "MySpecZip.zip", "!textForZip.txt", "!fake"}; 
        runArchiver(myArgs2);
        String[] myArgs3 = {"comment-w", "MySpecZip.zip", "This Zip archive is stydy-test. \nDon't use it for another aims"};
        runArchiver(myArgs3);
        String[] myArgs4 = {"comment-r", "MySpecZip.zip"};
        runArchiver(myArgs4);
    }
    
    private static void runArchiver(String[] args){                            
        if(args[0].equalsIgnoreCase("zip")){
            packToArchive(args, null);
        }else if(args[0].equalsIgnoreCase("zip+comment")){
            String[] newArgs = new String[args.length -1];
            System.arraycopy(args, 0, newArgs, 0, args.length - 1);
            packToArchive(newArgs, args[args.length - 1]);
        } else if(args[0].equalsIgnoreCase("unzip")){
            unpackFromArchive(args);
        } else if(args[0].equalsIgnoreCase("add")){
            addToArchive(args, null);
        } else if(args[0].equalsIgnoreCase("comment-r")){
            readZipComment(args);
        } else if(args[0].equalsIgnoreCase("comment-w")){
            addToArchive(args,args[2]);
        }    
    }
    
    private static void packToArchive(String[] args, String comment){
        String curDir = System.getProperty("user.dir"); 
        ZipOutputStream zos = createZipOutputStream(args[1]);
        try {
            if(comment != null){
                zos.setComment(comment);
            }
            
            for(int i = 2 ; i < args.length; i++){
                File f1 = new File(curDir + File.separator + args[i]);
                if(!f1.exists()){
                    System.out.println("\nNot found: " + args[i]);
                    System.exit(0);
                }
                if(f1.isFile()){
                    addFileToZip(zos, args[i], args[0]);
                } else {
                    addDirToZip(zos, args[i],args[0]);
                }
            }  
            zos.close();
        } catch(Exception ex){
            System.out.println(ex.toString());
        }
    }

    private static ZipOutputStream createZipOutputStream(String szPath){
    File tempfile;  
    ZipOutputStream zos = null;
    
    try {    
        tempfile = new File(szPath);
        zos = new ZipOutputStream(new FileOutputStream(tempfile));
        zos.setLevel(Deflater.DEFAULT_COMPRESSION);
    } catch(Exception ex) {
        System.out.println(ex.toString());
    }
    return zos;
  }
  
    private static void addFileToZip(ZipOutputStream zos, String fileName, String args_0) throws Exception {
        String curDir = System.getProperty("user.dir");
        
        ZipEntry ze;
        if(args_0 == null){
            int index = fileName.lastIndexOf(File.separator) + 1;
            ze = new ZipEntry(fileName.substring(index));
        } else {
            ze = new ZipEntry(fileName);
        }
        zos.putNextEntry(ze);

        FileInputStream fis = new FileInputStream(curDir + File.separator + fileName);

        byte[] buf = new byte[8000];
        int nLength;
        while(true){
            nLength = fis.read(buf);
            if(nLength < 0) break;
            zos.write(buf, 0, nLength);
        }

        fis.close();
        zos.closeEntry(); 
    }   
    
    private static void addDirToZip(ZipOutputStream zos, String dirName, String args_0) throws Exception{
        String curDir = System.getProperty("user.dir");
        File f1 = new File(curDir + File.separator + dirName);
        
        String[] DirList = f1.list();
        String[] ArgsOfDirectory = new String[2+DirList.length];
        int index = dirName.lastIndexOf(File.separator) + 1;
        if(index == -1){
            ArgsOfDirectory[1] = dirName + ".zip";
        } else {
            ArgsOfDirectory[1] = dirName.substring(index) + ".zip";
        }
        for(int j = 2; j < 2+DirList.length; j++){
            ArgsOfDirectory[j] = dirName + File.separator + DirList[j-2];
        }
        packToArchive(ArgsOfDirectory, null);
        addFileToZip(zos,ArgsOfDirectory[1], args_0);

        File f = new File(curDir + File.separator + ArgsOfDirectory[1]);
        f.delete();
    }
    
 // ============================================================================     
    private static void unpackFromArchive(String[] args){
        String curDir = System.getProperty("user.dir"); 
        String szZipFilePath = curDir + File.separator + args[1];
        checkFileIsZip(args[1]);
        String ExtractPath = getExtractPath(args);

        try {  
            ZipFile zf = new ZipFile(szZipFilePath);    
            Enumeration en = zf.entries();

            Set<ZipEntry> zipEntries = new HashSet();
            while(en.hasMoreElements()){
                zipEntries.add( (ZipEntry)en.nextElement() );
            }

            for (ZipEntry ze : zipEntries) {
                extractFromZip(ExtractPath, ze.getName(), zf, ze);
            }

            zf.close();
        } catch(Exception ex) {
            System.out.println(ex.toString());
        }
    }
    
    private static String getExtractPath(String[] args){
        String curDir = System.getProperty("user.dir"); 
        String ExtractPath;
        if(args.length > 2){
            ExtractPath = args[2];
            File f1 = new File(ExtractPath);
            if(!f1.exists()){
                System.out.println("\nNot found: " + ExtractPath);
                System.exit(0);
            }
            if(!f1.isDirectory()){
                System.out.println("\nNot directory: " + ExtractPath);
                System.exit(0);
            }
        } else {
            ExtractPath = curDir;
        }
        return ExtractPath;
    }

    private static void checkFileIsZip(String zipName){
        String curDir = System.getProperty("user.dir"); 
        String szZipFilePath = curDir + File.separator + zipName;
        File f = new File(szZipFilePath);
        if(!f.exists()){
            System.out.println("\nNot found: " + szZipFilePath);
            System.exit(0);
        }
        if(!szZipFilePath.endsWith(".zip")){
            System.out.println("\n!!! " + szZipFilePath + " is not *.zip");
            System.exit(0);            
        }
    }
    
    private static void extractFromZip(String ExtractPath,String szName, ZipFile zf, ZipEntry ze){
        String szDstName = slashToSeparator(szName);

        String szEntryDir;
        if(szDstName.lastIndexOf(File.separator) != -1) {
            szEntryDir = szDstName.substring(0,szDstName.lastIndexOf(File.separator));
        } else {	  
            szEntryDir = "";
        }

        try {
            File newDir = new File(ExtractPath + File.separator + szEntryDir);
            newDir.mkdirs();	 

            FileOutputStream fos = new FileOutputStream(ExtractPath + File.separator + szDstName);
            InputStream is = zf.getInputStream(ze);
            writeForomInputSToFileOutputS(is, fos);

            is.close();
            fos.close();
        } catch(Exception ex) {
          System.out.println(ex.toString());
          System.exit(0);
        }
    }  
  
    private static void writeForomInputSToFileOutputS(InputStream is, FileOutputStream fos) throws IOException{
            byte[] buf = new byte[1024];
            int nLength;
            while(true) {
                try {
                    nLength = is.read(buf);
                } catch (EOFException ex) {
                    break;
                }  
                if(nLength < 0) break;
                fos.write(buf, 0, nLength);
            }
    }
    
    private static String slashToSeparator(String src){
        int i;
        char[] chDst = new char[src.length()];
        String dst;

        for(i = 0; i < src.length(); i++){
            if(src.charAt(i) == '/'){
                chDst[i] = File.separatorChar;
            } else {
                chDst[i] = src.charAt(i);
            }    
        }
        dst = new String(chDst);
        return dst;
    }

 // ============================================================================    
    private static void addToArchive(String[] args, String comment){
        String curDir = System.getProperty("user.dir"); 
        String ZipFilePath = curDir + File.separator + args[1];
        checkFileIsZip(args[1]);
        try {
            ZipOutputStream zos = createZipOutputStream("TempZipFile.zip");
            Set<ZipEntry> zipEntries = addOldFilesToArchive(ZipFilePath, zos);
            
            if(comment != null){
                zos.setComment(comment);
            } else {
                for(int i = 2; i < args.length; i++){
                    File f1 = new File(curDir + File.separator + args[i]);
                    if(!fileExistsInZip(args[i], zipEntries)){
                        if(f1.isFile()){
                            addFileToZip(zos, args[i],args[0]);
                        } else {
                            addDirToZip(zos, args[i],args[0]);
                        }
                    }
                } 
            }

            zos.close();
        } catch(Exception ex) {
            System.out.println(ex.toString());
        }               
        renameZip(args[1]);
    }
    
    private static Set<ZipEntry> addOldFilesToArchive(String ZipFilePath, ZipOutputStream zos) throws IOException{
            ZipFile zf = new ZipFile(ZipFilePath); 
            
            Enumeration en = zf.entries();
            Set<ZipEntry> zipEntries = new HashSet();
            while(en.hasMoreElements()){
                zipEntries.add( (ZipEntry)en.nextElement() );
            }
            for (ZipEntry ze : zipEntries) {
                addFileFromZipToZip(ze.getName(), zf, ze, zos);
            }
            zf.close();
            return zipEntries;
    }
    
    private static void addFileFromZipToZip(String szName, ZipFile zf, ZipEntry zeIn, ZipOutputStream zos){   
        String szDstName = slashToSeparator(szName);

        try {
            InputStream is = zf.getInputStream(zeIn);
            ZipEntry zeOut = new ZipEntry(szDstName);
            zos.putNextEntry(zeOut);
            
            byte[] buf = new byte[1024];
            int nLength;
            while(true) {
                try {
                    nLength = is.read(buf);
                } catch (EOFException ex) {
                    break;
                }  
                if(nLength < 0) break;
                zos.write(buf, 0, nLength);
            }

            is.close();
            zos.closeEntry();
        } catch(Exception ex) {
          System.out.println(ex.toString());
          System.exit(0);
        }
    }      
    
    private static void renameZip (String zipName){
        String curDir = System.getProperty("user.dir"); 
        File f = new File(curDir + File.separator + zipName);
        f.delete();
        
        File f1 = new File(curDir + File.separator + "TempZipFile.zip");
        f1.renameTo(new File(curDir + File.separator + zipName));
    }
    
    private static boolean fileExistsInZip(String fileName, Set<ZipEntry> zipEntries){
        for (ZipEntry ze : zipEntries) {
            if(ze.getName().equals((String)fileName)){
                System.out.println("File " + fileName + " already exists !!!");
                return true;
            }
        }
        return false;
    }
//==============================================================================
    private static void readZipComment(String[] args){
        String curDir = System.getProperty("user.dir"); 
        String ZipFilePath = curDir + File.separator + args[1];
        checkFileIsZip(args[1]);
        
        try{
        ZipFile zf = new ZipFile(ZipFilePath);
        System.out.println("Archive comment: " + zf.getComment());
        } catch(Exception ex) {
          System.out.println(ex.toString());
          System.exit(0);
        } 
    }               
    
}