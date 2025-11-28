import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declarationOrStatement());
        }
        return statements;
    }

    private Stmt declarationOrStatement() {
        if (match(TokenType.INT, TokenType.REAL)) {
            Token type = previous();
            Token name = consume(TokenType.IDENTIFIER, "Esperado nome de variável.");
            consume(TokenType.SEMICOLON, "Esperado ';' após declaração.");
            return new Stmt.VarDecl(type, name);
        }
        return statement();
    }

    private Stmt statement() {
        if (match(TokenType.PRINT))  return printStatement();
        if (match(TokenType.INPUT))  return inputStatement();
        if (match(TokenType.IF))     return ifStatement();
        if (match(TokenType.WHILE))  return whileStatement();
        if (match(TokenType.LBRACE)) return new Stmt.Block(block());

        
        if (check(TokenType.IDENTIFIER) && checkNext(TokenType.ASSIGN)) {
            return assignmentStatement();
        }
        return expressionStatement();
    }

    private Stmt assignmentStatement() {
        Token name = consume(TokenType.IDENTIFIER, "Esperado nome de variável na atribuição.");
        consume(TokenType.ASSIGN, "Esperado '=' na atribuição.");
        Expr value = expression();
        consume(TokenType.SEMICOLON, "Esperado ';' após atribuição.");
        return new Stmt.Assign(name, value);
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(TokenType.SEMICOLON, "Esperado ';' após comando print.");
        return new Stmt.Print(value);
    }

    private Stmt inputStatement() {
        Token name = consume(TokenType.IDENTIFIER, "Esperado nome de variável após 'input'.");
        consume(TokenType.SEMICOLON, "Esperado ';' após comando input.");
        return new Stmt.Input(name);
    }

    private Stmt ifStatement() {
        consume(TokenType.LPAREN, "Esperado '(' após 'if'.");
        Expr condition = expression();
        consume(TokenType.RPAREN, "Esperado ')' após condição.");
        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(TokenType.ELSE)) {
            elseBranch = statement();
        }
        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt whileStatement() {
        consume(TokenType.LPAREN, "Esperado '(' após 'while'.");
        Expr condition = expression();
        consume(TokenType.RPAREN, "Esperado ')' após condição.");
        Stmt body = statement();
        return new Stmt.While(condition, body);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Esperado ';' após expressão.");
        return new Stmt.ExpressionStmt(expr);
    }

    private java.util.List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            statements.add(declarationOrStatement());
        }
        consume(TokenType.RBRACE, "Esperado '}' após bloco.");
        return statements;
    }



    private Expr expression() {
        return equality();
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)) {
            Token op = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, op, right);
        }
        return expr;
    }

    private Expr comparison() {
        Expr expr = term();

        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL,
                     TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token op = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, op, right);
        }
        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token op = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, op, right);
        }
        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token op = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, op, right);
        }
        return expr;
    }

    private Expr unary() {
        if (match(TokenType.MINUS)) {
            Token op = previous();
            Expr right = unary();
            return new Expr.Unary(op, right);
        }
        return primary();
    }

    private Expr primary() {
        if (match(TokenType.NUMBER)) {
            double value = Double.parseDouble(previous().lexeme);
            return new Expr.Literal(value);
        }

        if (match(TokenType.IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        if (match(TokenType.LPAREN)) {
            Expr expr = expression();
            consume(TokenType.RPAREN, "Esperado ')' após expressão.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expressão inválida.");
    }

    

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private boolean checkNext(TokenType type) {
        if (current + 1 >= tokens.size()) return false;
        return tokens.get(current + 1).type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private RuntimeException error(Token token, String message) {
        return new RuntimeException("Erro sintático em '" + token.lexeme + "': " + message);
    }
}
