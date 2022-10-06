import syntaxtree.*;
// import visitor.*;
import src.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        if(args.length == 0){
            System.err.println("Usage: java Main <inputFile>");
            System.exit(1);
        }

        FileInputStream fis = null;
        int i = 1;
        for(String file: args) {
            System.err.println("\n---------------------------------------------------------\n");
            System.err.println(i + "| " + file);
            i++;
            try{
                Symbol_table symTable = null;
                fis = new FileInputStream(file);
                MiniJavaParser parser = new MiniJavaParser(fis);
                Goal root = parser.Goal();

                System.err.println("Program parsed successfully!");

                symTable = new Symbol_table();
                DeclCollector eval = new DeclCollector(symTable); //visitor 1
                root.accept(eval, null);
                
                symTable.printOffset_symTable();

                TypeCheck check = new TypeCheck(symTable);
                root.accept(check, null);

                System.err.println("Program type check successfull!");
            }
            catch(ParseException ex){
                System.out.println(ex.getMessage());
            }
            catch(FileNotFoundException ex){
                System.err.println(ex.getMessage());
            }
            catch(Exception  ex){
                System.err.println(ex.getMessage());
            }
            finally{
                try{
                    if(fis != null) fis.close();
                }
                catch(IOException ex){
                    System.err.println(ex.getMessage());
                }
            }
        }
        System.err.println("\n---------------------------------------------------------\n");
    }
}