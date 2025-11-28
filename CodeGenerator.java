import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {

    private final List<Instruction> code = new ArrayList<>();

    public List<Instruction> generate(List<Stmt> program) {
        for (Stmt stmt : program) {
            genStmt(stmt);
        }
        return code;
    }

    private void genStmt(Stmt stmt) {
        if (stmt instanceof Stmt.VarDecl) {
        } else if (stmt instanceof Stmt.Assign) {
            Stmt.Assign a = (Stmt.Assign) stmt;
            genExpr(a.value);
            code.add(new Instruction(OpCode.STORE_VAR, a.name.lexeme));
        } else if (stmt instanceof Stmt.Print) {
            genExpr(((Stmt.Print) stmt).expression);
            code.add(new Instruction(OpCode.PRINT));
        } else if (stmt instanceof Stmt.Input) {
            Stmt.Input i = (Stmt.Input) stmt;
            code.add(new Instruction(OpCode.INPUT, i.name.lexeme));
        } else if (stmt instanceof Stmt.ExpressionStmt) {
            genExpr(((Stmt.ExpressionStmt) stmt).expression);
            code.add(new Instruction(OpCode.POP)); 
        } else if (stmt instanceof Stmt.Block) {
            for (Stmt s : ((Stmt.Block) stmt).statements) {
                genStmt(s);
            }
        } else if (stmt instanceof Stmt.If) {
            genIf((Stmt.If) stmt);
        } else if (stmt instanceof Stmt.While) {
            genWhile((Stmt.While) stmt);
        }
    }

    private void genIf(Stmt.If stmt) {
        genExpr(stmt.condition);
        
        int jmpIfFalseIndex = code.size();
        code.add(new Instruction(OpCode.JMP_IF_FALSE, -1));

        
        genStmt(stmt.thenBranch);

        if (stmt.elseBranch != null) {
            
            int jmpEndIndex = code.size();
            code.add(new Instruction(OpCode.JMP, -1));

            
            code.get(jmpIfFalseIndex).jumpTarget = code.size();

            
            genStmt(stmt.elseBranch);

           
            code.get(jmpEndIndex).jumpTarget = code.size();
        } else {
            
            code.get(jmpIfFalseIndex).jumpTarget = code.size();
        }
    }

    private void genWhile(Stmt.While stmt) {
        int loopStart = code.size();

        genExpr(stmt.condition);
        int jmpIfFalseIndex = code.size();
        code.add(new Instruction(OpCode.JMP_IF_FALSE, -1));

        genStmt(stmt.body);

        
        code.add(new Instruction(OpCode.JMP, loopStart));

        
        code.get(jmpIfFalseIndex).jumpTarget = code.size();
    }

    private void genExpr(Expr expr) {
        if (expr instanceof Expr.Literal) {
            Expr.Literal lit = (Expr.Literal) expr;
            code.add(new Instruction(OpCode.PUSH_CONST, lit.value));
        } else if (expr instanceof Expr.Variable) {
            Expr.Variable v = (Expr.Variable) expr;
            code.add(new Instruction(OpCode.LOAD_VAR, v.name.lexeme));
        } else if (expr instanceof Expr.Grouping) {
            genExpr(((Expr.Grouping) expr).expression);
        } else if (expr instanceof Expr.Unary) {
            Expr.Unary u = (Expr.Unary) expr;
           
            if (u.operator.type == TokenType.MINUS) {
                code.add(new Instruction(OpCode.PUSH_CONST, 0.0));
                genExpr(u.right);
                code.add(new Instruction(OpCode.SUB));
            } else {
                genExpr(u.right);
            }
        } else if (expr instanceof Expr.Binary) {
            Expr.Binary b = (Expr.Binary) expr;
            genExpr(b.left);
            genExpr(b.right);

            switch (b.operator.type) {
                case PLUS:          code.add(new Instruction(OpCode.ADD)); break;
                case MINUS:         code.add(new Instruction(OpCode.SUB)); break;
                case STAR:          code.add(new Instruction(OpCode.MUL)); break;
                case SLASH:         code.add(new Instruction(OpCode.DIV)); break;
                case EQUAL_EQUAL:   code.add(new Instruction(OpCode.CMP_EQ)); break;
                case BANG_EQUAL:    code.add(new Instruction(OpCode.CMP_NEQ)); break;
                case GREATER:       code.add(new Instruction(OpCode.CMP_GT)); break;
                case GREATER_EQUAL: code.add(new Instruction(OpCode.CMP_GTE)); break;
                case LESS:          code.add(new Instruction(OpCode.CMP_LT)); break;
                case LESS_EQUAL:    code.add(new Instruction(OpCode.CMP_LTE)); break;
                default:
                    throw new RuntimeException("Operador não suportado: " + b.operator.lexeme);
            }
        } else {
            throw new RuntimeException("Tipo de expressão desconhecido.");
        }
    }
}
