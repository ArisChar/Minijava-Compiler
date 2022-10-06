package src;
// visitor 2

// import java.util.*;
import syntaxtree.*;
import visitor.*;

public class TypeCheck extends GJDepthFirst<String, String>{
    private Symbol_table symTable;

    public TypeCheck(Symbol_table symTable) {
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
        String class_name = n.f1.f0.tokenImage;

        symTable.setClass_scope(class_name);
        symTable.setMethod_scope("main");

        n.f15.accept(this, argu);

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

		String class_name = n.f1.f0.tokenImage;
        symTable.setClass_scope(class_name);

        n.f4.accept(this, argu);

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

		String class_name = n.f1.f0.tokenImage;
        symTable.setClass_scope(class_name);
		n.f6.accept(this, class_name);

        symTable.resetClass_scope();
        
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
        // System.out.println("In Method:" );

        String method_type = n.f1.accept(this,null);
        // System.out.println("MethodType is -> " + method_type);

        String method_name = n.f2.f0.tokenImage;
        // System.out.println("Methodname is -> " + method_name);

        symTable.setMethod_scope(method_name);
        n.f8.accept(this,argu);
        
        String returnType = n.f10.accept(this,argu);

        symTable.methodCheck(method_type, returnType);

        symTable.resetMethod_scope();

        return _ret;
    }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
    @Override
	public String visit(AssignmentStatement n, String argu) throws Exception {
        String _ret = null;

		String value1 = n.f0.accept(this, argu);
		String value2 = n.f2.accept(this, argu);

        // System.out.println("Is "+ value1 +" = "+ value2);
        symTable.assignCheck(value1, value2);

		return _ret;
	}

   /**
    * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    */
	@Override
	public String visit(ArrayAssignmentStatement n, String argu) throws Exception {
        String _ret = null;
		String array_name = n.f0.accept(this, argu);
        String index = n.f2.accept(this, argu);
        String val = n.f5.accept(this, argu);

 		if (!index.equals("int")){
            throw new Exception("Error: wrong array index type!");
        }       

        if (!array_name.equals("int[]")){
            if(!array_name.equals("boolean[]")){
                throw new Exception("Error: wrong type in ArrayAssignmentStatement!");
            }else{
                if(!val.equals("boolean")){
                    throw new Exception("Error: wrong type in ArrayAssignmentStatement!");
                }
            }
        }else{
            if(!val.equals("int")){
                throw new Exception("Error: wrong type in ArrayAssignmentStatement!");
            }
        }

		return _ret;
	}

   /**
    * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    */
	@Override
	public String visit(IfStatement n, String argu) throws Exception {
        String _ret = null;
		String cond = n.f2.accept(this, argu);

		if (!cond.equals("boolean")){
            throw new Exception("Error: if condition must be boolean type!");
        }

		n.f4.accept(this, argu);
		n.f6.accept(this, argu);

		return _ret;
	}

   /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
	@Override
	public String visit(WhileStatement n, String argu) throws Exception {
        String _ret = null;
		String cond = n.f2.accept(this, argu);

		if (!cond.equals("boolean")){
            throw new Exception("Error: while condition must be boolean type!");
        }

		n.f4.accept(this, argu);

		return _ret;
	}

   /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
	@Override
	public String visit(PrintStatement n, String argu) throws Exception {
        String _ret = null;
		String expr = n.f2.accept(this, argu);

        // System.out.println("I want to print: "+ expr);
		if (!expr.equals("int")){
            throw new Exception("Error: can only print int type!");
        }
			
        return _ret;
	}

   /**
    * f0 -> Clause()
    * f1 -> "&&"
    * f2 -> Clause()
    */
	@Override
	public String visit(AndExpression n, String argu) throws Exception {
        String _ret = "boolean";
		String clause1 = n.f0.accept(this, argu);
		String clause2 = n.f2.accept(this, argu);
        // System.out.println("C1 "+ clause1 +" c2 "+ clause2);

		if (!clause1.equals("boolean") || !clause2.equals("boolean")){
            throw new Exception("Error: wrong type in AndExpression!");
        }
			
		return _ret;
	}

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
	@Override
	public String visit(CompareExpression n, String argu) throws Exception {
        // System.out.println("I'm in < compare");
        String _ret = "boolean";
		String pr_expr1 = n.f0.accept(this, argu);
		String pr_expr2 = n.f2.accept(this, argu);

		if (!pr_expr1.equals("int") || !pr_expr2.equals("int")){
            throw new Exception("Error: comparison operator < accepts only int!");
        }
		
		return _ret;
	}

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
	@Override
	public String visit(PlusExpression n, String argu) throws Exception {
        // System.out.println("I'm in + op");
        String _ret = "int";
		String pr_expr1 = n.f0.accept(this, argu);
		String pr_expr2 = n.f2.accept(this, argu);

		if (!pr_expr1.equals("int") || !pr_expr2.equals("int")){
            throw new Exception("Error: arithmetic operator + accepts only int!");
        }

		return _ret;
	}

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
	@Override
	public String visit(MinusExpression n, String argu) throws Exception {
        // System.out.println("I'm in - op");
        String _ret = "int";
		String pr_expr1 = n.f0.accept(this, argu);
		String pr_expr2 = n.f2.accept(this, argu);

		if (!pr_expr1.equals("int") || !pr_expr2.equals("int")){
            throw new Exception("Error: arithmetic operator - accepts only int!");
        }

		return _ret;
	}

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
	@Override
	public String visit(TimesExpression n, String argu) throws Exception {
        // System.out.println("I'm in * op");
        String _ret = "int";
		String pr_expr1 = n.f0.accept(this, argu);
		String pr_expr2 = n.f2.accept(this, argu);

		if (!pr_expr1.equals("int") || !pr_expr2.equals("int")){
            throw new Exception("Error: arithmetic operator * accepts only int!");
        }

		return _ret;
	}

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
	@Override
	public String visit(ArrayLookup n, String argu) throws Exception {
        String _ret;
		String pr_expr = n.f0.accept(this, argu);
		String index  = n.f2.accept(this, argu);

        if(!index.equals("int")){
            throw new Exception("Error: array index must be int!");
        }

		if (pr_expr.equals("int[]")){
            _ret = "int";
        }else if(pr_expr.equals("boolean[]")){
            _ret = "boolean";
        }else{
            throw new Exception("Error: wrong array type in ArrayLookup!");
        }

        return _ret;
	}

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
	@Override
	public String visit(ArrayLength n, String argu) throws Exception {
        String _ret = "int";
		String pr_expr = n.f0.accept(this, argu);

		if (!pr_expr.equals("int[]") && !pr_expr.equals("boolean[]")){
            throw new Exception("Error: wrong array type in ArrayLength!");
        }
			
		return _ret;
	}

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
	@Override
	public String visit(MessageSend n, String argu) throws Exception {
        // System.out.println("I'm sending a message!");
        String _ret = null;
        String class_name = n.f0.accept(this, null);

        if(symTable.getClass_data(class_name) == null){
            throw new Exception("Error: in MessageSend class in not defined!");
        }

        String method_name = n.f2.f0.tokenImage;
        String arg_list = n.f4.accept(this, null);

        _ret = symTable.messageCheck(symTable.getClass_data(class_name), method_name, arg_list);

        return _ret;
	}

   /**
    * f0 -> Expression() 
    * f1 -> ExpressionTail()
    */
	@Override
	public String visit(ExpressionList n, String argu) throws Exception {
		String _ret = n.f0.accept(this, null);

		if (n.f1 != null)
			_ret += n.f1.accept(this, null);

		return _ret;
	}

   /**
    * f0 -> ( ExpressionTerm() )*
    */
	@Override
	public String visit(ExpressionTail n, String argu) throws Exception {
		String _ret = "";

		for (Node node : n.f0.nodes)
            _ret += "," + node.accept(this, null);

		return _ret;
	}

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
	@Override
	public String visit(ExpressionTerm n, String argu) throws Exception {
		return n.f1.accept(this, argu);
	}

   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    @Override
    public String visit(IntegerArrayAllocationExpression n, String argu) throws Exception {
        String _ret = "int[]";
        String expr = n.f3.accept(this, argu);

        if(!expr.equals("int")){
            throw new Exception("Error: in IntegerArrayAllocation, array index must be int!");
        }

        return _ret;
    }
    
   /**
    * f0 -> "new"
    * f1 -> "boolean"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    @Override
    public String visit(BooleanArrayAllocationExpression n, String argu) throws Exception {
        String _ret = "boolean[]";
        String expr = n.f2.accept(this, argu);

        if(!expr.equals("int")){
            throw new Exception("Error: in BooleanArrayAllocation, array index must be int!");
        }

        return _ret;
    }

   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
	@Override
	public String visit(AllocationExpression n, String argu) throws Exception {
		String class_name = n.f1.f0.tokenImage;

		if (symTable.getClass_data(class_name) == null){
            throw new Exception("Error: in AllocationExpression, class does not exist!");
        }
			
		return class_name;
	} 

   /**
    * f0 -> "!"
    * f1 -> Clause()
    */
	@Override
	public String visit(NotExpression n, String argu) throws Exception {
        String _ret = "boolean";
		String clause = n.f1.accept(this, argu);

		if (!clause.equals("boolean")){
            throw new Exception("Error in NotExpression, type is not boolean!");

        }
			
		return _ret;
	}  

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
	@Override
	public String visit(BracketExpression n, String argu) throws Exception {
		return n.f1.accept(this, argu);
	} 
    
    @Override
    public String visit(Identifier n, String argu) throws Exception{
        String ident = n.f0.toString();
        String type = symTable.retrieveType(ident);
        if(type == null){
            throw new Exception("Error: in Identifier wrong type!");
        }
        // System.out.println(ident + " -> " + type);
        return type;
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
    public String visit(IntegerLiteral n, String argu) {
        return "int";
    }

    @Override
    public String visit(TrueLiteral n, String argu) throws Exception {
        return "boolean";
    }

    @Override
    public String visit(FalseLiteral n, String argu) throws Exception {
        return "boolean";
    }

    @Override
    public String visit(ThisExpression n, String argu) throws Exception {
        return symTable.getClass_scope();
    }
}
