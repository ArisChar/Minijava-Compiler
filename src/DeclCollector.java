package src;
// visitor 1

import syntaxtree.*;
import visitor.*;

public class DeclCollector extends GJDepthFirst<String, String>{
    private Symbol_table symTable;

    public DeclCollector(Symbol_table symTable) {
        this.symTable = symTable;
    }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */
    @Override
	public String visit(MainClass n, String argu) throws Exception {
        String _ret = null;

        String class_name = n.f1.accept(this, null);

        symTable.setClass_scope(class_name);
        symTable.setMethod_scope("main");

        symTable.addClass(class_name, class_name);
        Class_data class_main = symTable.getClass_data(class_name);
		class_main.addMethod("main", "void", false);

        String ident = n.f11.accept(this, null);

        Method_data method = class_main.getMethod("main");
        method.addParam(ident, "String[]");

        n.f14.accept(this, class_name);

        symTable.resetClass_scope();
        symTable.resetMethod_scope();

        return _ret;
    }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
    @Override
	public String visit(ClassDeclaration n, String argu) throws Exception {
        String _ret = null;
        
		String class_name = n.f1.accept(this, null);

        symTable.setClass_scope(class_name);
        symTable.addClass(class_name, class_name);

		n.f3.accept(this, class_name);
		n.f4.accept(this, class_name);

        symTable.resetClass_scope();

		return _ret;
	}

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */
    @Override
	public String visit(ClassExtendsDeclaration n, String argu) throws Exception {
        String _ret = null;

		String class_name = n.f1.accept(this, null);
        String parent_name = n.f3.accept(this, null);

        symTable.setClass_scope(class_name);
        symTable.addClass(class_name, parent_name);

		n.f5.accept(this, class_name);
		n.f6.accept(this, class_name);

        symTable.resetClass_scope();

		return _ret;        
    }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    @Override
    public String visit(VarDeclaration n, String argu) throws Exception {
        String _ret = null;

        String type = n.f0.accept(this, null);
        String name = n.f1.accept(this, null);

		String class_name = symTable.getClass_scope();
		String method_name = symTable.getMethod_scope();
        
        Class_data class_data = symTable.getClass_data(class_name);

        if (method_name == null){
            class_data.addField(name, type);
        }else{
            Method_data method_data = class_data.getMethod(method_name);
            method_data.addVar(name,type);
        }

        return _ret;    
    } 

   /**
    * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */  
    @Override
    public String visit(MethodDeclaration n, String argu) throws Exception {
        String _ret = null;

        String method_type = n.f1.accept(this,null);
        String method_name = n.f2.accept(this,null);

        symTable.setMethod_scope(method_name);
        Class_data class_data = symTable.getClass_data(symTable.getClass_scope());

        String argumentList = n.f4.present() ? n.f4.accept(this, null) : "";

        symTable.overrideCheck(argumentList, method_name, method_type, class_data);
        
        n.f7.accept(this, null);

        symTable.resetMethod_scope();

        return _ret;
    }

   /**
    * f0 -> FormalParameter()
    * f1 -> FormalParameterTail()
    */
    @Override
    public String visit(FormalParameterList n, String argu) throws Exception {
        String ret = n.f0.accept(this, null);

        if (n.f1 != null) {
            ret += n.f1.accept(this, null);
        }

        return ret;
    }

   /**
    * f0 -> FormalParameter()
    * f1 -> FormalParameterTail()
    */
    @Override
    public String visit(FormalParameterTerm n, String argu) throws Exception {
        return n.f1.accept(this, argu);
    }

   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
    @Override
    public String visit(FormalParameterTail n, String argu) throws Exception {
        String ret = "";
        for ( Node node: n.f0.nodes) {
            ret += ", " + node.accept(this, null);
        }

        return ret;
    }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
    @Override
    public String visit(FormalParameter n, String argu) throws Exception{
        String type = n.f0.accept(this, null);
        String name = n.f1.accept(this, null);
        return type + " " + name;
    }

    @Override
    public String visit(IntegerType n, String argu) {
        return "int";
    }

    @Override
    public String visit(ArrayType n, String argu) {
        return "int[]";
    }

    @Override
    public String visit(BooleanType n, String argu) {
        return "boolean";
    }

    @Override
    public String visit(BooleanArrayType n, String argu){
        return "boolean[]";
    }

    @Override
    public String visit(Identifier n, String argu) {
        return n.f0.toString();
    }

}
