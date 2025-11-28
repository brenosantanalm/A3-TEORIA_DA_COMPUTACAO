import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SemanticAnalyzer {

    private final Map<String, String> symbolTable = new HashMap<>();

    public void analyze(List<Stmt> program) {
        for (Stmt stmt : program) {
            analyzeStmt(stmt);
        }
    }

    private void analyzeStmt(Stmt stmt) {
        if (stmt instanceof Stmt.VarDecl) {
            Stmt.VarDecl v = (Stmt.VarDecl) stmt;
            String name = v.name.lexeme;
            if (symbolTable.containsKey(name)) {
                throw new RuntimeException("Erro semântico: variável '" + name + "' já declarada.");
            }
            String typeName = v.type.type == TokenType.INT ? "int" : "real";
            symbolTable.put(name, typeName);
        } else if (stmt instanceof Stmt.Assign) {
            Stmt.Assign a = (Stmt.Assign) stmt;
            checkVarDeclared(a.name);
            analyzeExpr(a.value);
        } else if (stmt instanceof Stmt.Print) {
            analyzeExpr(((Stmt.Print) stmt).expression);
        } else if (stmt instanceof Stmt.Input) {
            Stmt.Input i = (Stmt.Input) stmt;
            checkVarDeclared(i.name);
        } else if (stmt instanceof Stmt.Block) {
            for (Stmt s : ((Stmt.Block) stmt).statements) {
                analyzeStmt(s);
            }
        } else if (stmt instanceof Stmt.If) {
            Stmt.If i = (Stmt.If) stmt;
            analyzeExpr(i.condition);
            analyzeStmt(i.thenBranch);
            if (i.elseBranch != null) analyzeStmt(i.elseBranch);
        } else if (stmt instanceof Stmt.While) {
            Stmt.While w = (Stmt.While) stmt;
            analyzeExpr(w.condition);
            analyzeStmt(w.body);
        } else if (stmt instanceof Stmt.ExpressionStmt) {
            analyzeExpr(((Stmt.ExpressionStmt) stmt).expression);
        }
    }

    private void analyzeExpr(Expr expr) {
        if (expr instanceof Expr.Binary) {
            Expr.Binary b = (Expr.Binary) expr;
            analyzeExpr(b.left);
            analyzeExpr(b.right);
        } else if (expr instanceof Expr.Unary) {
            analyzeExpr(((Expr.Unary) expr).right);
        } else if (expr instanceof Expr.Grouping) {
            analyzeExpr(((Expr.Grouping) expr).expression);
        } else if (expr instanceof Expr.Variable) {
            checkVarDeclared(((Expr.Variable) expr).name);
        } else if (expr instanceof Expr.Literal) {
            // ok
        }
    }

    private void checkVarDeclared(Token name) {
        if (!symbolTable.containsKey(name.lexeme)) {
            throw new RuntimeException("Erro semântico: variável '" + name.lexeme + "' não declarada.");
        }
    }
}
